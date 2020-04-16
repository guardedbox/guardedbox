package com.guardedbox.service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;

/**
 * Email Utils Service.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Service
@RequiredArgsConstructor
public class EmailService {

    /** Property: spring.mail.username. */
    @Value("${spring.mail.username}")
    private final String mailUsername;

    /** Property: spring.mail.from. */
    @Value("${spring.mail.from:}")
    private final String mailFrom;

    /** Property: spring.mail.bcc. */
    @Value("${spring.mail.bcc:}")
    private final String mailBcc;

    /** JavaMailSender. */
    private final JavaMailSender javaMailSender;

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

            if (!StringUtils.isEmpty(mailFrom))
                mimeMessageHelper.setFrom(mailFrom);

            mimeMessageHelper.setTo(to);

            if (!StringUtils.isEmpty(mailBcc))
                mimeMessageHelper.setBcc(mailBcc);

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
