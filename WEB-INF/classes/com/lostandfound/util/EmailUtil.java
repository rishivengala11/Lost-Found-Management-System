package com.lostandfound.util;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class EmailUtil {

    private static final String BREVO_API_KEY = System.getenv("BREVO_API_KEY");
    private static final String APP_BASE_URL = System.getenv("APP_BASE_URL");

    // Must be verified in Brevo Senders
    private static final String SENDER_EMAIL = "vengalarishi143@gmail.com";
    private static final String SENDER_NAME = "Lost & Found";

    private static String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r");
    }

    private static boolean sendEmail(String recipientEmail, String subject, String htmlContent) {
        try {

            System.out.println("BREVO API METHOD CALLED");
            System.out.println("SENDING TO: " + recipientEmail);

            URL url = new URL("https://api.brevo.com/v3/smtp/email");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("accept", "application/json");
            conn.setRequestProperty("api-key", BREVO_API_KEY);
            conn.setRequestProperty("content-type", "application/json");

            conn.setDoOutput(true);

            String jsonInputString =
                    "{"
                    + "\"sender\":{"
                    + "\"name\":\"" + escapeJson(SENDER_NAME) + "\","
                    + "\"email\":\"" + escapeJson(SENDER_EMAIL) + "\""
                    + "},"
                    + "\"to\":[{"
                    + "\"email\":\"" + escapeJson(recipientEmail) + "\""
                    + "}],"
                    + "\"subject\":\"" + escapeJson(subject) + "\","
                    + "\"htmlContent\":\"" + escapeJson(htmlContent) + "\""
                    + "}";

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();

            System.out.println("BREVO RESPONSE CODE: " + responseCode);

            return responseCode >= 200 && responseCode < 300;

        } catch (Exception e) {
            System.out.println("BREVO API FAILED: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static boolean sendVerificationEmail(String recipientEmail, String token) {

        String verificationLink =
                APP_BASE_URL + "/api/verify?token=" + token;

        String htmlContent =
                "<div style='font-family: Arial, sans-serif; padding: 20px;'>"
                + "<h2>Welcome to Lost & Found</h2>"
                + "<p>Please verify your email.</p>"
                + "<a href='" + verificationLink + "' "
                + "style='padding:10px 20px;background:#4e73df;color:white;text-decoration:none;border-radius:5px;'>"
                + "Verify Email"
                + "</a>"
                + "</div>";

        return sendEmail(
                recipientEmail,
                "Verify Your Lost & Found Account",
                htmlContent
        );
    }

    public static void sendItemNotificationEmail(
            java.util.List<String> emails,
            String type,
            String itemName,
            String category,
            String location,
            String date) {

        if (emails == null || emails.isEmpty()) return;

        String typeCapitalized =
                type.substring(0, 1).toUpperCase() + type.substring(1);

        String htmlContent =
                "<div style='font-family: Arial, sans-serif; padding:20px;'>"
                + "<h2>New " + typeCapitalized + " Item Approved</h2>"
                + "<ul>"
                + "<li><strong>Item:</strong> " + itemName + "</li>"
                + "<li><strong>Category:</strong> " + category + "</li>"
                + "<li><strong>Location:</strong> " + location + "</li>"
                + "<li><strong>Date:</strong> " + date + "</li>"
                + "</ul>"
                + "</div>";

        for (String email : emails) {
            sendEmail(
                    email,
                    "New " + typeCapitalized + " Item Approved",
                    htmlContent
            );
        }
    }
}