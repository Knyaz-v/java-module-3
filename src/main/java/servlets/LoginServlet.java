package servlets;

import accounts.AccountService;
import accounts.UserProfile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LoginServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        UserProfile user = getCurrentUser(req, resp);
        if (user != null) {
            resp.sendRedirect(req.getContextPath() + "/explorer");
            return;
        }
        req.getRequestDispatcher("/WEB-INF/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        String login = req.getParameter("login");
        String password = req.getParameter("password");

        if (login == null || password == null ||
                login.isEmpty() || password.isEmpty()) {
            showError("Логин и пароль обязательны", req, resp);
            return;
        }

        login = login.toLowerCase();
        AccountService accountService = (AccountService) req.getServletContext()
                .getAttribute("accountService");
        UserProfile user = accountService.getUserByLogin(login);

        if (user == null || !user.getPassword().equals(password)) {
            showError("Неверный логин или пароль", req, resp);
            return;
        }

        HttpSession session = req.getSession();
        session.setAttribute("user", user);

        resp.sendRedirect(req.getContextPath() + "/explorer");
    }

    private void showError(String message,
                           HttpServletRequest req,
                           HttpServletResponse resp)
            throws ServletException, IOException{

        req.setAttribute("error", message);
        req.getRequestDispatcher("/WEB-INF/login.jsp").forward(req, resp);
    }
}
