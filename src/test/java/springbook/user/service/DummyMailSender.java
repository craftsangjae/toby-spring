package springbook.user.service;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

public class DummyMailSender implements MailSender {
    @Override
    public void send(SimpleMailMessage simpleMailMessage) throws MailException {
        System.out.println(simpleMailMessage.getText());
    }

    @Override
    public void send(SimpleMailMessage[] simpleMailMessages) throws MailException {
        for (SimpleMailMessage message : simpleMailMessages) {
            System.out.println(message.getText());
        }
    }
}
