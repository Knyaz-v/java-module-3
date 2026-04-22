package dbService.dao;

import dbService.dataSets.UsersDataSet;
import dbService.executor.Executor;

import java.sql.Connection;
import java.sql.SQLException;

public class UsersDAO {

    private final Executor executor;

    public UsersDAO(Connection connection) {
        this.executor = new Executor(connection);
    }

    public UsersDataSet get(long id) throws SQLException {
        return executor.execQuery("select * from users where id=?", result -> {
            if (result.next()) {
                return new UsersDataSet(
                        result.getLong("id"),
                        result.getString("user_login"),
                        result.getString("user_password"),
                        result.getString("user_email"));
            }
            return null;
        }, id);
    }

    public long getUserId(String login) throws SQLException {
        return executor.execQuery("select id from users where user_login=?", result -> {
            if (result.next()) {
                return result.getLong("id");
            }
            return -1L;
        }, login);
    }

    public void insertUser(String login, String password, String email) throws SQLException {
        executor.execUpdate("insert into users (user_login, user_password, user_email) values (?, ?, ?)", login, password, email);
    }

    public void createTable() throws SQLException {
        executor.execUpdate("create table if not exists users (" +
                "id bigint auto_increment, " +
                "user_login varchar(256), " +
                "user_password varchar(256), " +
                "user_email varchar(256), " +
                "primary key (id))");
    }

    public void dropTable() throws SQLException {
        executor.execUpdate("drop table users");
    }
}
