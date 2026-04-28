package com.lostandfound.servlet;

import com.lostandfound.dao.LostItemDAO;
import com.lostandfound.model.LostItem;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Servlet to handle lost item reporting and listing.
 * POST: Create a new lost item report
 * GET:  Retrieve all lost items
 * 
 * Lost And Found Management System
 * GRIET - Department of CSE
 */
public class ReportLostServlet extends HttpServlet {

    private LostItemDAO lostItemDAO = new LostItemDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        javax.servlet.http.HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.setStatus(401);
            out.print("{\"success\": false, \"message\": \"Unauthorized. Please login first.\"}");
            return;
        }
        int userId = (int) session.getAttribute("userId");

        try {
            String itemName = request.getParameter("itemName");
            String category = request.getParameter("category");
            String description = request.getParameter("description");
            String dateLost = request.getParameter("dateLost");
            String location = request.getParameter("location");
            String reporterName = request.getParameter("reporterName");
            String contact = request.getParameter("contact");

            // Validation
            if (itemName == null || itemName.trim().isEmpty() ||
                category == null || category.trim().isEmpty() ||
                dateLost == null || dateLost.trim().isEmpty() ||
                location == null || location.trim().isEmpty() ||
                reporterName == null || reporterName.trim().isEmpty() ||
                contact == null || contact.trim().isEmpty()) {
                
                response.setStatus(400);
                out.print("{\"success\": false, \"message\": \"All required fields must be filled.\"}");
                return;
            }

            LostItem item = new LostItem(userId, itemName.trim(), category.trim(),
                    description != null ? description.trim() : "",
                    dateLost.trim(), location.trim(), reporterName.trim(), contact.trim());

            boolean success = lostItemDAO.insert(item);

            if (success) {
                out.print("{\"success\": true, \"message\": \"Lost item reported successfully!\"}");
            } else {
                response.setStatus(500);
                out.print("{\"success\": false, \"message\": \"Failed to report lost item. Please try again.\"}");
            }
        } catch (Exception e) {
            response.setStatus(500);
            out.print("{\"success\": false, \"message\": \"Server error: " + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            List<LostItem> items = lostItemDAO.getAll();
            StringBuilder json = new StringBuilder("[");
            
            for (int i = 0; i < items.size(); i++) {
                LostItem item = items.get(i);
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
