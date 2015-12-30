/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.dao.login;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.exception.OpenClinicaException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import javax.sql.DataSource;

/**
 * <P>
 * Purpose of this class is to test against new version of user account dao,
 * which uses the digester and queries stored in XML. This version is
 * self-sufficient, but fairly inflexible.
 *
 * @author thickerson
 *
 */
public class UserAccountDAOOld {
    private DataSource ds;

    public UserAccountDAOOld(DataSource ds) {
        this.ds = ds;
    }

    public UserAccountBean findByPrimaryKey(int pl_id) throws OpenClinicaException {
        String sql = "SELECT * FROM USER_ACCOUNT WHERE USER_ID = ?";
        PreparedStatement ps = null;
        Connection con = null;
        UserAccountBean uab = new UserAccountBean();
        ResultSet rs = null;
        try {
            con = ds.getConnection();
            if (con.isClosed()) {
                throw new OpenClinicaException("findByPrimaryKey: connection not open", "1");
            }
            ps = con.prepareStatement(sql);
            ps.setInt(1, pl_id);
            rs = ps.executeQuery();
            if (rs.next()) {
                // in order, we should get: Query_id, name, description,
                // sql_statement, owner_id, access_role, date_created,
                // last_run_date, num_runs, run_time
                uab.setName(rs.getString("user_name"));
                uab.setEmail(rs.getString("email"));
                uab.setFirstName(rs.getString("first_name"));
                return uab;
            } else {
                throw new OpenClinicaException("no Query found, findByPrimaryKey", "1");
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new OpenClinicaException(sqle.getMessage(), "1");
        } catch (OpenClinicaException pe) {
            pe.printStackTrace();
            throw new OpenClinicaException(pe.getMessage(), "1");
        } finally {
            try {
                if (con != null)
                    con.close();
                if (ps != null)
                    ps.close();
                if (rs != null)
                    rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new OpenClinicaException(e.getMessage(), "1");
            }
        }

    }

    public Collection findAll() throws OpenClinicaException {
        String sql = "SELECT USER_ID FROM USER_ACCOUNT ORDER BY USER_ID";
        PreparedStatement ps = null;
        Connection con = null;
        ResultSet rs = null;
        ArrayList list = new ArrayList();
        try {
            con = ds.getConnection();
            if (con.isClosed()) {
                throw new OpenClinicaException("typeDAO: find all reports: connection not open", "1");
            }
            ps = con.prepareStatement(sql);
            // ps.setString(1,name);
            rs = ps.executeQuery();
            while (rs.next()) {
                UserAccountBean ph = new UserAccountBean();
                ph = this.findByPrimaryKey(rs.getInt(1));
                list.add(ph);
            }
            return list;
        } catch (SQLException se) {
            se.printStackTrace();
            throw new OpenClinicaException(se.getMessage(), "1");
        } catch (OpenClinicaException me) {
            me.printStackTrace();
            throw new OpenClinicaException(me.getMessage(), "1");
        } finally {
            try {
                if (con != null)
                    con.close();
                if (ps != null)
                    ps.close();
                if (rs != null)
                    rs.close();
            } catch (SQLException se) {
                se.printStackTrace();
                throw new OpenClinicaException(se.getMessage(), "1");
            }
        }

    }

}
