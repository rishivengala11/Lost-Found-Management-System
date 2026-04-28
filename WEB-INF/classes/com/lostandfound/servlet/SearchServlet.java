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
 * Servlet to handle searching across both lost and found items.
 * GET: Search by keyword, category, or type
 * 
 * Lost And Found Management System
 * GRIET - Department of CSE
 */
public class SearchServlet extends HttpServlet {

    private LostItemDAO lostItemDAO = new LostItemDAO();
    private FoundItemDAO foundItemDAO = new FoundItemDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            String keyword = request.getParameter("keyword");
            String category = request.getParameter("category");
            String type = request.getParameter("type"); // "lost", "found", or "all"

            if (type == null || type.trim().isEmpty()) {
                type = "all";
            }

            StringBuilder json = new StringBuilder("{");

            // Search Lost Items
            if ("all".equals(type) || "lost".equals(type)) {
                List<LostItem> lostItems;
                if (category != null && !category.trim().isEmpty() && !"all".equals(category)) {
                    lostItems = lostItemDAO.searchByCategory(category.trim());
                } else if (keyword != null && !keyword.trim().isEmpty()) {
                    lostItems = lostItemDAO.search(keyword.trim());
                } else {
                    lostItems = lostItemDAO.getAll();
                }

                // Filter by keyword if category was also specified
                if (keyword != null && !keyword.trim().isEmpty() && category != null && !category.trim().isEmpty()) {
                    lostItems = lostItemDAO.search(keyword.trim());
                }

                json.append("\"lostItems\":[");
                for (int i = 0; i < lostItems.size(); i++) {
                    LostItem item = lostItems.get(i);
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
                json.append("]");
            }

            // Search Found Items
            if ("all".equals(type) || "found".equals(type)) {
                if (json.length() > 1) json.append(",");
                
                List<FoundItem> foundItems;
                if (category != null && !category.trim().isEmpty() && !"all".equals(category)) {
                    foundItems = foundItemDAO.searchByCategory(category.trim());
                } else if (keyword != null && !keyword.trim().isEmpty()) {
                    foundItems = foundItemDAO.search(keyword.trim());
                } else {
                    foundItems = foundItemDAO.getAll();
                }

                if (keyword != null && !keyword.trim().isEmpty() && category != null && !category.trim().isEmpty()) {
                    foundItems = foundItemDAO.search(keyword.trim());
                }

                json.append("\"foundItems\":[");
                for (int i = 0; i < foundItems.size(); i++) {
                    FoundItem item = foundItems.get(i);
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
            }

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
