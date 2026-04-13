import accounts.AccountService;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class AppContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        AccountService accountService = new AccountService();
        sce.getServletContext().setAttribute("accountService", accountService);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }
}
