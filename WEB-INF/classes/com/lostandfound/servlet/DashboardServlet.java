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

/**
 * Servlet to provide dashboard statistics and recent items.
 * GET: Returns counts and recent items for the dashboard page.
 * 
 * Lost And Found Management System
 * GRIET - Department of CSE
 */
public class DashboardServlet extends HttpServlet {

    private LostItemDAO lostItemDAO = new LostItemDAO();
    private FoundItemDAO foundItemDAO = new FoundItemDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            int totalLost = lostItemDAO.getCount();
            int totalFound = foundItemDAO.getCount();
            int totalClaimed = lostItemDAO.getClaimedCount();
            int totalReturned = foundItemDAO.getReturnedCount();
            
            List<LostItem> recentLost = lostItemDAO.getRecent(5);
            List<FoundItem> recentFound = foundItemDAO.getRecent(5);

            StringBuilder json = new StringBuilder("{");
            
            // Statistics
            json.append("\"stats\":{");
            json.append("\"totalLost\":").append(totalLost).append(",");
            json.append("\"totalFound\":").append(totalFound).append(",");
            json.append("\"totalClaimed\":").append(totalClaimed).append(",");
            json.append("\"totalReturned\":").append(totalReturned);
            json.append("},");

            // Recent Lost Items
            json.append("\"recentLost\":[");
            for (int i = 0; i < recentLost.size(); i++) {
                LostItem item = recentLost.get(i);
                if (i > 0) json.append(",");
                json.append("{");
                json.append("\"id\":").append(item.getId()).append(",");
                json.append("\"itemName\":\"").append(escapeJson(item.getItemName())).append("\",");
                json.append("\"category\":\"").append(escapeJson(item.getCategory())).append("\",");
                json.append("\"description\":\"").append(escapeJson(item.getDescription())).append("\",");
                json.append("\"dateLost\":\"").append(escapeJson(item.getDateLost())).append("\",");
                json.append("\"location\":\"").append(escapeJson(item.getLocation())).append("\",");
                json.append("\"reporterName\":\"").append(escapeJson(item.getReporterName())).append("\",");
                json.append("\"contact\":\"").append(escapeJson(item.getContact())).append("\",");
                json.append("\"status\":\"").append(escapeJson(item.getStatus())).append("\"");
                json.append("}");
            }
            json.append("],");

            // Recent Found Items
            json.append("\"recentFound\":[");
            for (int i = 0; i < recentFound.size(); i++) {
                FoundItem item = recentFound.get(i);
                if (i > 0) json.append(",");
                json.append("{");
                json.append("\"id\":").append(item.getId()).append(",");
                json.append("\"itemName\":\"").append(escapeJson(item.getItemName())).append("\",");
                json.append("\"category\":\"").append(escapeJson(item.getCategory())).append("\",");
                json.append("\"description\":\"").append(escapeJson(item.getDescription())).append("\",");
                json.append("\"dateFound\":\"").append(escapeJson(item.getDateFound())).append("\",");
                json.append("\"location\":\"").append(escapeJson(item.getLocation())).append("\",");
                json.append("\"finderName\":\"").append(escapeJson(item.getFinderName())).append("\",");
                json.append("\"contact\":\"").append(escapeJson(item.getContact())).append("\",");
                json.append("\"status\":\"").append(escapeJson(item.getStatus())).append("\"");
                json.append("}");
            }
            json.append("]");

            json.append("}");
            out.print(json.toString());
        } catch (Exception e) {
            response.setStatus(500);
            out.print("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                     .replace("\"", "\\\"")
                     .replace("\n", "\\n")
                     .replace("\r", "\\r")
                     .replace("\t", "\\t");
    }
}
