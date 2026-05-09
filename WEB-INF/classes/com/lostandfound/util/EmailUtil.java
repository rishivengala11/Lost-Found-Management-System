package com.lostandfound.util;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailUtil {

    private static final String SMTP_SERVER = "smtp-relay.brevo.com";
    private static final String SMTP_PORT = "587";

    private static final String SYSTEM_EMAIL = System.getenv("MAIL_USER");
    private static final String SYSTEM_PASSWORD = System.getenv("MAIL_PASSWORD");
    private static final String APP_BASE_URL = System.getenv("APP_BASE_URL");

    private static Properties getMailProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_SERVER);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.writetimeout", "10000");

        return props;
    }

    private static Session getMailSession() {
        return Session.getInstance(getMailProperties(), new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SYSTEM_EMAIL, SYSTEM_PASSWORD);
            }
        });
    }

    public static boolean sendVerificationEmail(String recipientEmail, String token) {
        System.out.println("BREVO METHOD CALLED");
        System.out.println("MAIL_USER = " + SYSTEM_EMAIL);
        try {
            if (SYSTEM_EMAIL == null || SYSTEM_PASSWORD == null || APP_BASE_URL == null) {
                System.out.println("BREVO FAILED: Missing MAIL_USER / MAIL_PASSWORD / APP_BASE_URL");
                return false;
            }

            Session session = getMailSession();

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SYSTEM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Verify Your Lost & Found Account");

            String verificationLink = APP_BASE_URL + "/api/verify?token=" + token;

            String htmlContent = "<div style='font-family: Arial, sans-serif; padding: 20px; text-align: center;'>"
                    + "<h2 style='color: #4e73df;'>Welcome to Lost & Found!</h2>"
                    + "<p>Please verify your email address to activate your account.</p>"
                    + "<a href='" + verificationLink + "' style='display:inline-block;padding:10px 20px;background:#4e73df;color:white;text-decoration:none;border-radius:5px;'>Verify Email</a>"
                    + "</div>";

            message.setContent(htmlContent, "text/html; charset=utf-8");

            System.out.println("SENDING VERIFICATION EMAIL TO: " + recipientEmail);
            Transport.send(message);
            System.out.println("VERIFICATION EMAIL SENT SUCCESSFULLY TO: " + recipientEmail);

            return true;

        } catch (MessagingException e) {
            System.out.println("BREVO FAILED: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static void sendItemNotificationEmail(java.util.List<String> emails, String type, String itemName, String category, String location, String date) {
        if (emails == null || emails.isEmpty()) {
            System.out.println("BREVO SKIPPED: No verified users to notify");
            return;
        }

        try {
            if (SYSTEM_EMAIL == null || SYSTEM_PASSWORD == null) {
                System.out.println("BREVO FAILED: Missing MAIL_USER / MAIL_PASSWORD");
                return;
            }

            Session session = getMailSession();

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SYSTEM_EMAIL));

            String recipients = String.join(",", emails);
            message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(recipients));

            String typeCapitalized = type.substring(0, 1).toUpperCase() + type.substring(1);
            message.setSubject("New " + typeCapitalized + " Item: " + itemName);

            String htmlContent = "<div style='font-family: Arial, sans-serif; padding:20px;'>"
                    + "<h2 style='color:#4e73df;'>New " + typeCapitalized + " Item Approved</h2>"
                    + "<p>A new item has been approved by the admin.</p>"
                    + "<ul>"
                    + "<li><strong>Item Name:</strong> " + itemName + "</li>"
                    + "<li><strong>Category:</strong> " + category + "</li>"
                    + "<li><strong>Location:</strong> " + location + "</li>"
                    + "<li><strong>Date:</strong> " + date + "</li>"
                    + "</ul>"
                    + "<p>Please log in to the Lost & Found portal for more details.</p>"
                    + "</div>";

            message.setContent(htmlContent, "text/html; charset=utf-8");

            System.out.println("SENDING ITEM NOTIFICATION EMAIL TO: " + recipients);
            Transport.send(message);
            System.out.println("ITEM NOTIFICATION EMAIL SENT SUCCESSFULLY");

        } catch (MessagingException e) {
            System.out.println("BREVO FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }
}