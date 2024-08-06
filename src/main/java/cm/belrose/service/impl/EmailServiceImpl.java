package cm.belrose.service.impl;

import cm.belrose.service.EmailService;
import cm.belrose.service.dto.EmailContent;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {
    //JavaMailSender allows us to send the email
    private final JavaMailSender javaMailSender;

    @Override
    public void send(EmailContent content) {
        var baseUrl = "http://localhost:8080/storage";
        var greetings = """
                Hello Mr %s. Welcome to 'Spring Batch Become a Legend' Series.
                Below you can find the links to download the documents.
                """.formatted("Samuel");

        String emailBody = content.fileCodes()
                .stream()
                .map(str -> baseUrl.concat(str).concat("\n"))
                .collect(Collectors.joining("\n"));

        /*var simpleMailMessage = new SimpleMailMessage();
       // simpleMailMessage.setFrom("ngnawens@gmail.com");
        simpleMailMessage.setTo("ngnawens1@gmail.com");
        simpleMailMessage.setSubject("IMPORTANT: Your files Uploaded");
        simpleMailMessage.setText(greetings.concat(emailBody));
        log.info("-------->Sending email<-----------");
        javaMailSender.send(simpleMailMessage);*/

        try{
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper =new MimeMessageHelper(message,true);
            helper.setTo("devsvn39@gmail.com");
            helper.setSubject("IMPORTANT: Your files Uploaded");
            helper.setText(greetings.concat(emailBody));
            javaMailSender.send(message);
            log.info("-------->Sending email<-----------");
            log.info(String.format("JavaMailSender:sendEmail: %s {} is successfully sent",message));
        } catch (MessagingException e) {
            log.error(String.format("JavaMailSender:sendEmail:Error to send OTP to ngnawens1@gmail.com. Cause: %s",e));
            throw new RuntimeException(e);
        }
    }

}
