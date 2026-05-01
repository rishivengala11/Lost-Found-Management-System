package com.lostandfound.util;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class EmailUtil {

    private static final String RESEND_API_KEY = System.getenv("RESEND_API_KEY");
    private static final String APP_BASE_URL = System.getenv("APP_BASE_URL");
    private static final String FROM_EMAIL = System.getenv("FROM_EMAIL");

    private static String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r");
    }

    private static boolean sendEmail(String toJsonArray, String subject, String html) {
        try {
            if (RESEND_API_KEY == null || FROM_EMAIL == null) {
                System.out.println("RESEND FAILED: Missing RESEND_API_KEY or FROM_EMAIL");
                return false;
            }

            URL url = new URL("https://api.resend.com/emails");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setRequestProperty("Authorization", "Bearer " + RESEND_API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String json = "{"
                    + "\"from\":\"" + escapeJson(FROM_EMAIL) + "\","
                    + "\"to\":" + toJsonArray + ","
                    + "\"subject\":\"" + escapeJson(subject) + "\","
                    + "\"html\":\"" + escapeJson(html) + "\""
                    + "}";

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }

            int status = conn.getResponseCode();
            System.out.println("RESEND STATUS: " + status);

            return status >= 200 && status < 300;

        } catch (Exception e) {
            System.out.println("RESEND FAILED: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static boolean sendVerificationEmail(String recipientEmail, String token) {
        String verificationLink = APP_BASE_URL + "/api/verify?token=" + token;

        String htmlContent = "<div style='font-family: Arial, sans-serif; padding: 20px; text-align: center;'>"
                + "<h2 style='color: #4e73df;'>Welcome to Lost & Found!</h2>"
                + "<p>Please verify your email address to activate your account.</p>"
                + "<a href='" + verificationLink + "' style='display:inline-block;padding:10px 20px;background:#4e73df;color:white;text-decoration:none;border-radius:5px;'>Verify Email</a>"
                + "</div>";

        String to = "[\"" + escapeJson(recipientEmail) + "\"]";
        return sendEmail(to, "Verify Your Lost & Found Account", htmlContent);
    }

    public static void sendItemNotificationEmail(java.util.List<String> bccEmails, String type, String itemName, String category, String location, String date) {
        if (bccEmails == null || bccEmails.isEmpty()) return;
        bccEmails.clear();
        bccEmails.add("eslavathasrithabai@gmail.com");
        StringBuilder toBuilder = new StringBuilder("[");
        for (int i = 0; i < bccEmails.size(); i++) {
            if (i > 0) toBuilder.append(",");
            toBuilder.append("\"").append(escapeJson(bccEmails.get(i))).append("\"");
        }
        toBuilder.append("]");

        String typeCapitalized = type.substring(0, 1).toUpperCase() + type.substring(1);

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

        sendEmail(toBuilder.toString(), "New " + typeCapitalized + " Item: " + itemName, htmlContent);
    }
}
