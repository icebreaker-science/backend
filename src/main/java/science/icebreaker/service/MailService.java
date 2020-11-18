package science.icebreaker.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailException;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import science.icebreaker.dao.entity.Account;
import science.icebreaker.dao.entity.AccountProfile;
import science.icebreaker.config.properties.MailConfigurationProperties;
import science.icebreaker.data.request.ContactRequest;
import science.icebreaker.exception.AccountNotFoundException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
@Configuration
public class MailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailService.class);

    private AccountService accountService;
    private final TemplateEngine mailTextTemplateEngine;
    private final JavaMailSender mailSender;
    private final MailConfigurationProperties mailProperties;
    private final String hostUrl;

    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    public MailService(
        TemplateEngine mailTextTemplateEngine,
        JavaMailSender mailSender,
        MailConfigurationProperties mailProperties,
        @Value("${icebreaker.host}") String hostUrl
    ) {
        this.mailTextTemplateEngine = mailTextTemplateEngine;
        this.mailSender = mailSender;
        this.mailProperties = mailProperties;
        this.hostUrl = hostUrl;
    }

    /**
     * Sends a reset password token to the given account
     * @param token the token to send
     * @param account the account to send to
     */
    public void sendResetPasswordRequest(String token, Account account) {
        // todo: finding the profile should not be the responsibility of the mail service
        // tie the profile to the account instead.
        AccountProfile accountProfile = accountService.getAccountProfile(account.getId());
        Context context = new Context();
        context.setVariable("token", token);
        context.setVariable("host", hostUrl);
        context.setVariable("fullName", accountProfile.getFullName());

        String message = mailTextTemplateEngine.process("resetPassword", context);

        final String subject = "Your password reset request";
        sendMail(account.getEmail(), message, subject);
    }

    @SuppressWarnings("ConstantConditions")
    public void sendContactRequestMail(ContactRequest contactRequest, Account account)
        throws AccountNotFoundException, science.icebreaker.exception.MailException {
        AccountProfile accountProfile = accountService.getAccountProfile(account.getId());
        Context context = new Context();
        context.setVariable("contactRequest", contactRequest);
        context.setVariable("accountProfile", accountProfile);

        String receiverMessage = mailTextTemplateEngine.process("contactReceiver", context);
        String senderMessage = mailTextTemplateEngine.process("contactSender", context);

        try {
            sendMail(account.getEmail(), receiverMessage, "Device request via icebreaker.science");
            try {
                sendMail(
                    contactRequest.getEmail(),
                    senderMessage,
                    "Your device request via icebreaker.science");
            } catch (MailException e) {
                LOGGER.error(
                    "Exception while sending contact confirmation mail: {}", e.getMessage());
                throw new science.icebreaker.exception.MailException(
                    "Error while sending confirmation mail.");
            }
        } catch (MailException e) {
            LOGGER.error("Exception while sending contact request: {}", e.getMessage());
            throw new science.icebreaker.exception.MailException(
                "Error while sending contact request.");
        }
    }

    public void sendTemplateMail(String receiver, String template, String subject, Map<String, String> values)
            throws MailException {
        Context context = new Context();
        values.forEach(context::setVariable);
        String message = mailTextTemplateEngine.process(template, context);
        sendMail(receiver, message, subject);
    }

    public Future<Void> sendMail(String receiver, String message, String subject) throws MailException {
        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, "UTF-8");
        try {
            messageHelper.setFrom(this.mailProperties.getFromAddress());
            messageHelper.setReplyTo(this.mailProperties.getReplyAddress());
            messageHelper.setBcc(this.mailProperties.getBccAddress());
            messageHelper.setTo(receiver);
            messageHelper.setSubject(subject);
            messageHelper.setText(message);
        } catch (MessagingException e) {
            throw new MailPreparationException("Error sending mail", e);
        }

        return executorService.submit(() -> {
            try {
                this.mailSender.send(mimeMessage);
            } catch (Exception e) {
                LOGGER.error("Failed to send a mail: receiver=" + receiver + "; subject=" + subject, e);
            }
            return null;
        });
    }

    @Autowired
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }
}
