package com.lostandfound.servlet;

import com.lostandfound.dao.LostItemDAO;
import com.lostandfound.dao.FoundItemDAO;
import com.lostandfound.model.LostItem;
import com.lostandfound.model.FoundItem;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class MyItemsServlet extends HttpServlet {

    private LostItemDAO lostItemDAO = new LostItemDAO();
    private FoundItemDAO foundItemDAO = new FoundItemDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.setStatus(401);
            out.print("{\"error\": \"Unauthorized. Please login first.\"}");
            return;
        }

        int userId = (int) session.getAttribute("userId");

        try {
            List<LostItem> lostItems = lostItemDAO.getByUser(userId);
            List<FoundItem> foundItems = foundItemDAO.getByUser(userId);

            StringBuilder json = new StringBuilder("{");
            
            // Lost Items
            json.append("\"lost\": [");
            for (int i = 0; i < lostItems.size(); i++) {
                LostItem item = lostItems.get(i);
                json.append("{")
                        .append("\"id\":").append(item.getId()).append(",")
                        .append("\"itemName\":\"").append(escapeJson(item.getItemName())).append("\",")
                        .append("\"category\":\"").append(escapeJson(item.getCategory())).append("\",")
                        .append("\"dateLost\":\"").append(escapeJson(item.getDateLost())).append("\",")
                        .append("\"location\":\"").append(escapeJson(item.getLocation())).append("\",")
                        .append("\"status\":\"").append(escapeJson(item.getStatus())).append("\",")
                        .append("\"claimRequested\":").append(item.isClaimRequested())
                        .append("}");
                if (i < lostItems.size() - 1) json.append(",");
            }
            json.append("],");

            // Found Items
            json.append("\"found\": [");
            for (int i = 0; i < foundItems.size(); i++) {
                FoundItem item = foundItems.get(i);
                json.append("{")
                        .append("\"id\":").append(item.getId()).append(",")
                        .append("\"itemName\":\"").append(escapeJson(item.getItemName())).append("\",")
                        .append("\"category\":\"").append(escapeJson(item.getCategory())).append("\",")
                        .append("\"dateFound\":\"").append(escapeJson(item.getDateFound())).append("\",")
                        .append("\"location\":\"").append(escapeJson(item.getLocation())).append("\",")
                        .append("\"status\":\"").append(escapeJson(item.getStatus())).append("\",")
                        .append("\"returnRequested\":").append(item.isReturnRequested())
                        .append("}");
                if (i < foundItems.size() - 1) json.append(",");
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
