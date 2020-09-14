package science.icebreaker.mail;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import science.icebreaker.config.MailConfigurationProperties;

import java.util.Properties;

@Configuration
public class MailConfig  {

    private final MailConfigurationProperties mailProperties;

    public MailConfig(MailConfigurationProperties mailProperties) {
        this.mailProperties = mailProperties;
    }

    @Bean
    public JavaMailSender mailSender() {
        final JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailProperties.getHost());
        mailSender.setPort(mailProperties.getPort());
        mailSender.setProtocol(mailProperties.getProtocol());
        mailSender.setUsername(mailProperties.getUsername());
        mailSender.setPassword(mailProperties.getPassword());

        final Properties properties = new Properties();
        properties.setProperty("mail.smtp.auth", mailProperties.getAuth());
        properties.setProperty("mail.smtp.starttls.enable", mailProperties.getStarttls());

        mailSender.setJavaMailProperties(properties);
        return mailSender;
    }

    @Bean
    public TemplateEngine mailTextTemplateEngine() {
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.addTemplateResolver(mailTextTemplateResolver());
        return templateEngine;
    }

    private ITemplateResolver mailTextTemplateResolver() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("/templates/mail/");
        templateResolver.setSuffix(".txt");
        templateResolver.setTemplateMode(TemplateMode.TEXT);
        templateResolver.setCharacterEncoding("UTF8");
        templateResolver.setCheckExistence(false);
        templateResolver.setCacheable(false);
        return templateResolver;
    }
}
