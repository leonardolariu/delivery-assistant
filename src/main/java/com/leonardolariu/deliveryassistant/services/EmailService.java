package com.leonardolariu.deliveryassistant.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
class EmailService {
    @Autowired
    private JavaMailSender emailSender;

    void sendMessageWithAttachment(String to, String subject, String text, String fileName,
                                   Resource gcsResource) throws MessagingException {

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text);
        helper.addAttachment(fileName, gcsResource);

        emailSender.send(message);
    }
}
