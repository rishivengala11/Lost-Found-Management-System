package com.lostandfound.servlet;

import com.lostandfound.dao.UserDAO;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;

public class VerifyServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String token = request.getParameter("token");

        if (token != null && !token.isEmpty()) {
            if (userDAO.verifyUser(token)) {
                response.sendRedirect(request.getContextPath() + "/login.html?success=verified");
            } else {
                response.sendRedirect(request.getContextPath() + "/login.html?error=invalid_token");
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/login.html");
        }
    }
}
