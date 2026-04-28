package com.lostandfound.servlet;

import com.lostandfound.dao.FoundItemDAO;
import com.lostandfound.dao.LostItemDAO;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class UpdateStatusServlet extends HttpServlet {

    private LostItemDAO lostItemDAO = new LostItemDAO();
    private FoundItemDAO foundItemDAO = new FoundItemDAO();

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
            String idStr = request.getParameter("id");
            String type = request.getParameter("type"); // "lost" or "found"

            if (idStr == null || type == null) {
                out.print("{\"success\": false, \"message\": \"Missing parameters\"}");
                return;
            }

            int id = Integer.parseInt(idStr);
            boolean updated = false;

            if ("lost".equalsIgnoreCase(type)) {
                com.lostandfound.model.LostItem lItem = lostItemDAO.getById(id);
                if(lItem == null || lItem.getUserId() != userId) {
                    out.print("{\"success\": false, \"message\": \"Unauthorized to request claim for this item.\"}");
                    return;
                }
                updated = lostItemDAO.requestClaim(id);
            } else if ("found".equalsIgnoreCase(type)) {
                com.lostandfound.model.FoundItem fItem = foundItemDAO.getById(id);
                if(fItem == null || fItem.getUserId() != userId) {
                    out.print("{\"success\": false, \"message\": \"Unauthorized to request return for this item.\"}");
                    return;
                }
                updated = foundItemDAO.requestReturn(id);
            }

            if (updated) {
                out.print("{\"success\": true, \"message\": \"Status updated successfully\"}");
            } else {
                out.print("{\"success\": false, \"message\": \"Failed to update status\"}");
            }
        } catch (Exception e) {
            response.setStatus(500);
            out.print("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
        }
    }
}
