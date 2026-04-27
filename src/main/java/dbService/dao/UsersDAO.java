package dbService.dao;

import dbService.dataSets.UsersDataSet;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public class UsersDAO {

    private final Session session;

    public UsersDAO(Session session) {
        this.session = session;
    }

    public UsersDataSet get(long id) {
        return session.get(UsersDataSet.class, id);
    }

    public UsersDataSet getUserByLogin(String login) {
        try {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<UsersDataSet> criteria = builder.createQuery(UsersDataSet.class);
            Root<UsersDataSet> root = criteria.from(UsersDataSet.class);
            criteria.select(root).where(builder.equal(root.get("login"), login));
            Query<UsersDataSet> query = session.createQuery(criteria);
            return query.uniqueResult();
        } catch (Exception e) {
            return null;
        }
    }

    public long insertUser(String login, String password, String email) {
        UsersDataSet user = new UsersDataSet(login, password, email);
        return (long) session.save(user);
    }
}