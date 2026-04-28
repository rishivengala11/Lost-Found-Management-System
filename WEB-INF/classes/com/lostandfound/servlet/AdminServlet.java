package com.lostandfound.servlet;

import com.lostandfound.dao.UserDAO;
import com.lostandfound.dao.LostItemDAO;
import com.lostandfound.dao.FoundItemDAO;
import com.lostandfound.model.LostItem;
import com.lostandfound.model.FoundItem;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class AdminServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAO();
    private LostItemDAO lostItemDAO = new LostItemDAO();
    private FoundItemDAO foundItemDAO = new FoundItemDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        HttpSession session = request.getSession(false);

        if (session == null || !"ADMIN".equals(session.getAttribute("role"))) {
            response.setStatus(403);
            return;
        }

        if ("verify".equals(action)) {
            String type = request.getParameter("type");
            int id = Integer.parseInt(request.getParameter("id"));
            String status = request.getParameter("status"); // APPROVED or REJECTED

            boolean success = false;
            if ("lost".equals(type)) {
                success = lostItemDAO.verifyItem(id, status);
                if (success && "APPROVED".equals(status)) {
                    LostItem item = lostItemDAO.getById(id);
                    if (item != null) {
                        List<String> emails = userDAO.getAllVerifiedUserEmails();
                        new Thread(() -> {
                            com.lostandfound.util.EmailUtil.sendItemNotificationEmail(emails, "lost", item.getItemName(), item.getCategory(), item.getLocation(), item.getDateLost());
                        }).start();
                    }
                }
            } else if ("found".equals(type)) {
                success = foundItemDAO.verifyItem(id, status);
                if (success && "APPROVED".equals(status)) {
                    FoundItem item = foundItemDAO.getById(id);
                    if (item != null) {
                        List<String> emails = userDAO.getAllVerifiedUserEmails();
                        new Thread(() -> {
                            com.lostandfound.util.EmailUtil.sendItemNotificationEmail(emails, "found", item.getItemName(), item.getCategory(), item.getLocation(), item.getDateFound());
                        }).start();
                    }
                }
            }

            response.setContentType("application/json");
            response.getWriter().print("{\"success\": " + success + "}");
        } else if ("approveStatus".equals(action)) {
            String type = request.getParameter("type");
            int id = Integer.parseInt(request.getParameter("id"));
            String status = request.getParameter("status"); // Expected: Claimed or Returned

            boolean success = false;
            if ("lost".equals(type)) {
                success = lostItemDAO.updateStatus(id, status);
            } else if ("found".equals(type)) {
                success = foundItemDAO.updateStatus(id, status);
            }

            response.setContentType("application/json");
            response.getWriter().print("{\"success\": " + success + "}");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"ADMIN".equals(session.getAttribute("role"))) {
            response.setStatus(403);
            return;
        }

        String action = request.getParameter("action");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        if ("getPending".equals(action)) {
            List<LostItem> lost = lostItemDAO.getAllPending();
            List<FoundItem> found = foundItemDAO.getAllPending();

            StringBuilder json = new StringBuilder("{");
            json.append("\"lost\": [");
            for (int i = 0; i < lost.size(); i++) {
                LostItem item = lost.get(i);
                json.append("{")
                        .append("\"id\": ").append(item.getId()).append(",")
                        .append("\"itemName\": \"").append(escapeJson(item.getItemName())).append("\",")
                        .append("\"category\": \"").append(escapeJson(item.getCategory())).append("\",")
                        .append("\"date\": \"").append(escapeJson(item.getDateLost())).append("\",")
                        .append("\"location\": \"").append(escapeJson(item.getLocation())).append("\"")
                        .append("}");
                if (i < lost.size() - 1)
                    json.append(",");
            }
            json.append("],");

            json.append("\"found\": [");
            for (int i = 0; i < found.size(); i++) {
                FoundItem item = found.get(i);
                json.append("{")
                        .append("\"id\": ").append(item.getId()).append(",")
                        .append("\"itemName\": \"").append(escapeJson(item.getItemName())).append("\",")
                        .append("\"category\": \"").append(escapeJson(item.getCategory())).append("\",")
                        .append("\"date\": \"").append(escapeJson(item.getDateFound())).append("\",")
                        .append("\"location\": \"").append(escapeJson(item.getLocation())).append("\"")
                        .append("}");
                if (i < found.size() - 1)
                    json.append(",");
            }
            json.append("]");
            json.append("}");
            out.print(json.toString());
        } else if ("getStatusRequests".equals(action)) {
            List<LostItem> lostReqs = lostItemDAO.getPendingClaimRequests();
            List<FoundItem> foundReqs = foundItemDAO.getPendingReturnRequests();

            StringBuilder json = new StringBuilder("{");
            json.append("\"lost\": [");
            for (int i = 0; i < lostReqs.size(); i++) {
                LostItem item = lostReqs.get(i);
                json.append("{")
                        .append("\"id\": ").append(item.getId()).append(",")
                        .append("\"itemName\": \"").append(escapeJson(item.getItemName())).append("\",")
                        .append("\"reporterName\": \"").append(escapeJson(item.getReporterName())).append("\"")
                        .append("}");
                if (i < lostReqs.size() - 1) json.append(",");
            }
            json.append("],");

            json.append("\"found\": [");
            for (int i = 0; i < foundReqs.size(); i++) {
                FoundItem item = foundReqs.get(i);
                json.append("{")
                        .append("\"id\": ").append(item.getId()).append(",")
                        .append("\"itemName\": \"").append(escapeJson(item.getItemName())).append("\",")
                        .append("\"finderName\": \"").append(escapeJson(item.getFinderName())).append("\"")
                        .append("}");
                if (i < foundReqs.size() - 1) json.append(",");
            }
            json.append("]");
            json.append("}");
            out.print(json.toString());
        } else if ("getStats".equals(action)) {
            int pending = userDAO.getPendingCount();
            out.print("{\"pendingCount\": " + pending + "}");
        }
    }

    private String escapeJson(String value) {
        if (value == null)
            return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }
}
