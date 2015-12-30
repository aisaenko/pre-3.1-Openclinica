package org.akaza.openclinica.servlettests;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.core.SessionManager;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.login.UserAccountDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

/**
 * A mock version of SessionManager that is used for JUnit testing.
 */
public class MockSessionManager extends SessionManager {
    private Connection con;

    private UserAccountBean ub;

    private String logFileName;

    private Level logLevel;

    private DataSource ds;

    private final Logger logger = Logger.getLogger(getClass().getName());

    private String dbName;
    private UserAccountDAO uDAO = null;

    /**
     * Constructor of SessionManager
     *
     * @param userFromSession
     * @param userName
     * @throws SQLException
     */
    public MockSessionManager(UserAccountBean userFromSession, String userName) throws SQLException {

        if (userFromSession == null || StringUtil.isBlank(userFromSession.getName())) {
            // create a new user account bean form database
            SQLFactory factory = SQLFactory.getInstance();

            uDAO = new UserAccountDAO(ds);
            if (userName == null) {
                userName = "";
            }
            ub = (UserAccountBean) uDAO.findByUserName(userName);
            if (logger.isLoggable(Level.INFO)) {
                // logger.info("SessionManager: get user from db" +
                // ub.getName());
                // logger.info("password:" + ub.getPasswd());
            }

        } else {
            ub = userFromSession;
            // logger.info("get user from session" + ub.getName() +
            // ub.getPasswd());
        }

    }

    public MockSessionManager(DataSource dataSource) {
        this.ds = dataSource;
    }

    public void setDs(DataSource ds) {
        this.ds = ds;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    @Override
    public UserAccountBean getUserBean() {
        return ub;
    }

    @Override
    public void setConnection(Connection con) {
        this.con = con;
    }

    @Override
    public void setUserBean(UserAccountBean user) {
        this.ub = user;
    }

    @Override
    public DataSource getDataSource() {
        return ds;
    }

}