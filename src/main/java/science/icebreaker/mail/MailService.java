package science.icebreaker.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailException;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import science.icebreaker.account.Account;
import science.icebreaker.account.AccountNotFoundException;
import science.icebreaker.account.AccountProfile;
import science.icebreaker.account.AccountService;
import science.icebreaker.config.MailConfigurationProperties;
import science.icebreaker.device_availability.ContactRequest;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@Configuration
public class MailService {

    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    private final AccountService accountService;
    private final TemplateEngine mailTextTemplateEngine;
    private final JavaMailSender mailSender;
    private final MailConfigurationProperties mailProperties;

    public MailService(AccountService accountService, TemplateEngine mailTextTemplateEngine, JavaMailSender mailSender, MailConfigurationProperties mailProperties) {
        this.accountService = accountService;
        this.mailTextTemplateEngine = mailTextTemplateEngine;
        this.mailSender = mailSender;
        this.mailProperties = mailProperties;
    }

    @SuppressWarnings("ConstantConditions")
    public void sendContactRequestMail(ContactRequest contactRequest, Account account) throws AccountNotFoundException, science.icebreaker.mail.MailException {
        AccountProfile accountProfile = accountService.getAccountProfile(account.getId());
        Context context = new Context();
        context.setVariable("contactRequest", contactRequest);
        context.setVariable("accountProfile", accountProfile);

        String receiverMessage = mailTextTemplateEngine.process("contactReceiver", context);
        String senderMessage = mailTextTemplateEngine.process("contactSender", context);

        try {
            sendMail(account.getEmail(), receiverMessage, "Device request via icebreaker.science");
            try {
                sendMail(contactRequest.getEmail(), senderMessage, "Your device request via icebreaker.science");
            } catch (MailException e) {
                logger.error("Exception while sending contact confirmation mail: {}", e.getMessage());
                throw new science.icebreaker.mail.MailException("Error while sending confirmation mail.");
            }
        } catch (MailException e) {
            logger.error("Exception while sending contact request: {}", e.getMessage());
            throw new science.icebreaker.mail.MailException("Error while sending contact request.");
        }
    }

    public void sendMail(String receiver, String message, String subject) throws MailException {
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
        this.mailSender.send(mimeMessage);
    }
}
