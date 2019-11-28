package com.guardedbox.service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Email Utils Service.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Service
public class EmailService {

    /** JavaMailSender. */
    private final JavaMailSender javaMailSender;

    /**
     * Constructor with Attributes.
     *
     * @param javaMailSender JavaMailSender.
     */
    public EmailService(
            @Autowired JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    /**
     * Sends an email.
     *
     * @param to Receiver.
     * @param subject Subject.
     * @param content HTML Content.
     */
    public void send(
            String to,
            String subject,
            String content) {

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

        try {
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(content, true);
        } catch (MessagingException e) {
            throw new MailSendException("Error setting the email attributes", e);
        }

        javaMailSender.send(mimeMessage);

    }

    /**
     * Sends an email asynchronously.
     *
     * @param to Receiver.
     * @param subject Subject.
     * @param content HTML Content.
     */
    public void sendAsync(
            String to,
            String subject,
            String content) {

        new Thread(new Runnable() {

            @Override
            public void run() {

                send(to, subject, content);

            }

        }).start();

    }

}
