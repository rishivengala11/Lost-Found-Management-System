package com.lostandfound.servlet;

import com.lostandfound.dao.LostItemDAO;
import com.lostandfound.dao.FoundItemDAO;
import com.lostandfound.model.LostItem;
import com.lostandfound.model.FoundItem;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.ArrayList;

/**
 * Servlet to find potential matches between lost and found items.
 * Matches are determined by category and keyword similarity.
 * 
 * Lost And Found Management System
 * GRIET - Department of CSE
 */
public class MatchServlet extends HttpServlet {

    private LostItemDAO lostItemDAO = new LostItemDAO();
    private FoundItemDAO foundItemDAO = new FoundItemDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            List<LostItem> lostItems = lostItemDAO.getAll();
            List<FoundItem> foundItems = foundItemDAO.getAll();

            StringBuilder json = new StringBuilder("[");
            boolean first = true;

            for (LostItem lost : lostItems) {
                for (FoundItem found : foundItems) {
                    // Skip if either is already resolved
                    if ("Claimed".equals(lost.getStatus()) || "Returned".equals(found.getStatus())) {
                        continue;
                    }

                    int score = calculateMatchScore(lost, found);

                    if (score >= 4) {
                        if (!first)
                            json.append(",");
                        first = false;

                        String confidence;
                        if (score >= 4)
                            confidence = "High";
                        else if (score >= 2)
                            confidence = "Medium";
                        else
                            confidence = "Low";

                        json.append("{");
                        json.append("\"matchScore\":").append(score).append(",");
                        json.append("\"confidence\":\"").append(confidence).append("\",");

                        // Lost item details
                        json.append("\"lostItem\":{");
                        json.append("\"id\":").append(lost.getId()).append(",");
                        json.append("\"itemName\":\"").append(escapeJson(lost.getItemName())).append("\",");
                        json.append("\"category\":\"").append(escapeJson(lost.getCategory())).append("\",");
                        json.append("\"description\":\"").append(escapeJson(lost.getDescription())).append("\",");
                        json.append("\"dateLost\":\"").append(escapeJson(lost.getDateLost())).append("\",");
                        json.append("\"location\":\"").append(escapeJson(lost.getLocation())).append("\",");
                        json.append("\"reporterName\":\"").append(escapeJson(lost.getReporterName())).append("\",");
                        json.append("\"contact\":\"").append(escapeJson(lost.getContact())).append("\"");
                        json.append("},");

                        // Found item details
                        json.append("\"foundItem\":{");
                        json.append("\"id\":").append(found.getId()).append(",");
                        json.append("\"itemName\":\"").append(escapeJson(found.getItemName())).append("\",");
                        json.append("\"category\":\"").append(escapeJson(found.getCategory())).append("\",");
                        json.append("\"description\":\"").append(escapeJson(found.getDescription())).append("\",");
                        json.append("\"dateFound\":\"").append(escapeJson(found.getDateFound())).append("\",");
                        json.append("\"location\":\"").append(escapeJson(found.getLocation())).append("\",");
                        json.append("\"finderName\":\"").append(escapeJson(found.getFinderName())).append("\",");
                        json.append("\"contact\":\"").append(escapeJson(found.getContact())).append("\"");
                        json.append("}");

                        json.append("}");
                    }
                }
            }

            json.append("]");
            out.print(json.toString());
        } catch (Exception e) {
            response.setStatus(500);
            out.print("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    /**
     * Calculate a match score between a lost item and a found item.
     * Criteria:
     * - Same category: +2 points
     * - Similar item name (contains common words): +2 points
     * - Similar location (contains common words): +1 point
     * - Close dates (within 7 days): +1 point
     */
    private int calculateMatchScore(LostItem lost, FoundItem found) {
        int score = 0;

        // Category match
        if (lost.getCategory() != null && found.getCategory() != null &&
                lost.getCategory().equalsIgnoreCase(found.getCategory())) {
            score += 2;
        }

        // Item name similarity
        if (hasCommonWords(lost.getItemName(), found.getItemName())) {
            score += 2;
        }

        // Location similarity
        if (hasCommonWords(lost.getLocation(), found.getLocation())) {
            score += 1;
        }

        // Date proximity (within 7 days)
        try {
            java.sql.Date dateLost = java.sql.Date.valueOf(lost.getDateLost());
            java.sql.Date dateFound = java.sql.Date.valueOf(found.getDateFound());
            long diffDays = Math.abs(dateFound.getTime() - dateLost.getTime()) / (1000 * 60 * 60 * 24);
            if (diffDays <= 7) {
                score += 1;
            }
        } catch (Exception e) {
            // Ignore date parsing errors
        }

        return score;
    }

    /**
     * Check if two strings share common meaningful words (3+ characters)
     */
    private boolean hasCommonWords(String str1, String str2) {
        if (str1 == null || str2 == null)
            return false;

        String[] words1 = str1.toLowerCase().split("\\s+");
        String[] words2 = str2.toLowerCase().split("\\s+");

        for (String w1 : words1) {
            if (w1.length() < 3)
                continue; // Skip short words like "a", "in", "on"
            for (String w2 : words2) {
                if (w2.length() < 3)
                    continue;
                if (w1.equals(w2) || w1.contains(w2) || w2.contains(w1)) {
                    return true;
                }
            }
        }
        return false;
    }

    private String escapeJson(String value) {
        if (value == null)
            return "";
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
