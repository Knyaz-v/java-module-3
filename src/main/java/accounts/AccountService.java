package accounts;

import dbService.DBException;
import dbService.DBService;
import dbService.dataSets.UsersDataSet;

public class AccountService {
    private final DBService dbService;

    public AccountService(DBService dbService) {
        this.dbService = dbService;
    }

    public void addNewUser(UserProfile userProfile) {
        try {
            dbService.addUser(
                    userProfile.getLogin(),
                    userProfile.getPassword(),
                    userProfile.getEmail());
        } catch (DBException e) {
            e.printStackTrace();
        }
    }

    public UserProfile getUserByLogin(String login) {
        try {
            UsersDataSet dataSet = dbService.getUserByLogin(login);
            if (dataSet == null) return null;
            return new UserProfile(
                    dataSet.getLogin(),
                    dataSet.getPassword(),
                    dataSet.getEmail()
            );
        } catch (DBException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void close() {
        dbService.close();
    }
}