package dbService;

import dbService.dao.UsersDAO;
import dbService.dataSets.UsersDataSet;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBService {
    private final Connection connection;

    public DBService() {
        this.connection = getMySQLConnection();
    }

    public static Connection getMySQLConnection() {

        try {
            Driver driver = (Driver) Class.forName("com.mysql.jdbc.Driver").newInstance();
            DriverManager.registerDriver(driver);

            StringBuilder url = new StringBuilder();

            url.
                    append("jdbc:mysql://").        //db type
                    append("localhost:").           //host name
                    append("3306/").                //port
                    append("db_example?").          //db name
                    append("user=tully&").          //login
                    append("password=tully");       //password

            Connection connection1 = DriverManager.getConnection(url.toString());
            return connection1;

        } catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public UsersDataSet getUser(long id) throws DBException {
        try {
            return (new UsersDAO(connection).get(id));
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public UsersDataSet getUserByLogin(String login) throws DBException {
        try {
            UsersDAO dao = new UsersDAO(connection);
            long userId = dao.getUserId(login);
            if (userId == -1) {
                return null;
            }
            return dao.get(userId);
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public long addUser(String login, String password, String email) throws DBException {
        try {
            connection.setAutoCommit(false);
            UsersDAO dao = new UsersDAO(connection);
            dao.createTable();
            dao.insertUser(login, password, email);
            connection.commit();
            return dao.getUserId(login);
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ignore){
            }
            throw new DBException(e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ignore) {
            }
        }
    }

    public void printConnectInfo() {
        try {
            System.out.println("DB name: " + connection.getMetaData().getDatabaseProductName());
            System.out.println("DB version: " + connection.getMetaData().getDatabaseProductVersion());
            System.out.println("Driver: " + connection.getMetaData().getDriverName());
            System.out.println("Autocommit: " + connection.getAutoCommit());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void cleanUp() throws DBException {
        UsersDAO dao = new UsersDAO(connection);
        try {
            dao.dropTable();
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
