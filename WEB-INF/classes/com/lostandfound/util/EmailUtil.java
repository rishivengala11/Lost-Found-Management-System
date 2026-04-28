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

    private static final String SMTP_SERVER = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String SYSTEM_EMAIL = "vengalarishi143@gmail.com";
    private static final String SYSTEM_PASSWORD = "sfqwwdhnencwinre";

    public static boolean sendVerificationEmail(String recipientEmail, String token) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_SERVER);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SYSTEM_EMAIL, SYSTEM_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SYSTEM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Verify Your Lost & Found Account");

            String verificationLink = "http://localhost:8080/Lost_Found/api/verify?token=" + token;
            
            String htmlContent = "<div style='font-family: Arial, sans-serif; padding: 20px; text-align: center;'>"
                    + "<h2 style='color: #4e73df;'>Welcome to Lost & Found!</h2>"
                    + "<p>Please verify your email address to activate your account and gain access to the dashboard.</p>"
                    + "<a href='" + verificationLink + "' style='display: inline-block; padding: 10px 20px; background-color: #4e73df; color: white; text-decoration: none; border-radius: 5px; margin-top: 20px;'>Verify Email Address</a>"
                    + "<p style='margin-top: 30px; font-size: 12px; color: #888;'>If you did not create this account, you can safely ignore this email.</p>"
                    + "</div>";

            message.setContent(htmlContent, "text/html; charset=utf-8");

            Transport.send(message);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static void sendItemNotificationEmail(java.util.List<String> bccEmails, String type, String itemName, String category, String location, String date) {
        if (bccEmails == null || bccEmails.isEmpty()) return;
        
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_SERVER);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SYSTEM_EMAIL, SYSTEM_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SYSTEM_EMAIL));
            
            String bccAddresses = String.join(",", bccEmails);
            message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(bccAddresses));
            
            String typeCapitalized = type.substring(0, 1).toUpperCase() + type.substring(1);
            message.setSubject("New " + typeCapitalized + " Item: " + itemName);

            String htmlContent = "<div style='font-family: Arial, sans-serif; padding: 20px;'>"
                    + "<h2 style='color: #4e73df;'>New " + typeCapitalized + " Item Reported</h2>"
                    + "<p>A new item has been " + type + " and approved by the admin. Please see the details below:</p>"
                    + "<ul style='list-style-type: none; padding-left: 0;'>"
                    + "<li><strong>Item Name:</strong> " + itemName + "</li>"
                    + "<li><strong>Category:</strong> " + category + "</li>"
                    + "<li><strong>Location:</strong> " + location + "</li>"
                    + "<li><strong>Date:</strong> " + date + "</li>"
                    + "</ul>"
                    + "<p>Please log in to the Lost & Found portal for more details.</p>"
                    + "</div>";

            message.setContent(htmlContent, "text/html; charset=utf-8");

            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
