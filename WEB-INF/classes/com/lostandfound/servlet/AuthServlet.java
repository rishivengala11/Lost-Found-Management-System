package com.lostandfound.servlet;

import com.lostandfound.dao.UserDAO;
import com.lostandfound.model.User;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.UUID;
import com.lostandfound.util.EmailUtil;

public class AuthServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        HttpSession session = request.getSession();

        if ("login".equals(action)) {
            String email = request.getParameter("email");
            String pass = request.getParameter("password");

            User authenticatedUser = userDAO.authenticate(email, pass);

            if (authenticatedUser != null) {
                if (!authenticatedUser.isVerified()) {
                    response.sendRedirect(request.getContextPath() + "/login.html?error=unverified&email=" + authenticatedUser.getEmail());
                    return;
                }

                session.setAttribute("email", authenticatedUser.getEmail());
                session.setAttribute("userId", authenticatedUser.getId());
                session.setAttribute("role", authenticatedUser.getRole());
                session.setAttribute("fullName", authenticatedUser.getFullName());

                if ("ADMIN".equals(authenticatedUser.getRole())) {
                    // Also set 'admin' for backward compatibility
                    session.setAttribute("admin", authenticatedUser.getEmail());
                    response.sendRedirect(request.getContextPath() + "/admin-dashboard.html");
                } else {
                    response.sendRedirect(request.getContextPath() + "/index.html");
                }
            } else {
                response.sendRedirect(request.getContextPath() + "/login.html?error=1");
            }
        } else if ("register".equals(action)) {
            String email = request.getParameter("email");
            String pass = request.getParameter("password");
            String name = request.getParameter("fullName");

            User newUser = new User();
            newUser.setEmail(email);
            newUser.setPassword(pass);
            newUser.setFullName(name);

            String token = UUID.randomUUID().toString();
            Timestamp expiry = new Timestamp(System.currentTimeMillis() + (15 * 60 * 1000)); // 15 mins

            if (userDAO.registerUser(newUser, token, expiry)) {
                // Send verification email
                new Thread(() -> {
                EmailUtil.sendVerificationEmail(email, token);
            }).start();
                response.sendRedirect(request.getContextPath() + "/login.html?success=registered");
            } else {
                response.sendRedirect(request.getContextPath() + "/login.html?reg_error=1");
            }
        } else if ("resend".equals(action)) {
            String email = request.getParameter("email");
            if (email != null && !email.trim().isEmpty()) {
                String token = UUID.randomUUID().toString();
                Timestamp expiry = new Timestamp(System.currentTimeMillis() + (15 * 60 * 1000));
                
                if (userDAO.updateVerificationToken(email, token, expiry)) {
                    EmailUtil.sendVerificationEmail(email, token);
                }
            }
            response.sendRedirect(request.getContextPath() + "/login.html?success=resent");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        HttpSession session = request.getSession(false);

        if ("logout".equals(action)) {
            if (session != null) {
                session.invalidate();
            }
            response.sendRedirect(request.getContextPath() + "/login.html");
        } else if ("session".equals(action)) {
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            
            if (session != null && session.getAttribute("email") != null) {
                String email = (String) session.getAttribute("email");
                String role = (String) session.getAttribute("role");
                String fullName = (String) session.getAttribute("fullName");
                out.print("{\"loggedIn\": true, \"email\": \"" + email + "\", \"role\": \"" + role + "\", \"fullName\": \"" + fullName + "\"}");
            } else {
                out.print("{\"loggedIn\": false}");
            }
        }
    }
}
