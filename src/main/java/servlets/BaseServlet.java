package servlets;

import accounts.UserProfile;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class BaseServlet extends HttpServlet {
    protected UserProfile getCurrentUser(HttpServletRequest req, HttpServletResponse resp) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            return null;
        }
        return (UserProfile) session.getAttribute("user");
    }

    protected String getBasePath() {
        return getServletContext().getInitParameter("explorerBasePath");
    }
}
