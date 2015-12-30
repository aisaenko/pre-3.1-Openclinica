/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.dao.core;

import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p/>
 * EntityDAO.java, the generic data access object class for the database layer,
 * by Tom Hickerson, 09/24/2004
 * <p/>
 * A signalling system was added on 7 Dec 04 to indicate the success or failure
 * of a query. A query is considered successful iff a SQLException was not
 * thrown in the process of executing the query.
 * <p/>
 * The system can be used by outside classes / subclasses as follows: -
 * Immediately after calling select or execute, isQuerySuccessful() is
 * <code>true</code> if the query was successful, <code>false</code>
 * otherwise. - If isQuerySuccessful returns <code>false</code>
 * getFailureDetails() returns the SQLException which was thrown.
 * <p/>
 * In order to maintain the system, the following invariants must be maintained
 * by developers: 1. Every method executing a query must call clearSignals() as
 * the first statement. 2. Every method executing a query must call either
 * signalSuccess or signalFailure before returning.
 * <p/>
 * At the time of writing, the only methods which execute queries are select and
 * execute.
 *
 * @author thickerson
 */
public abstract class EntityDAO implements DAOInterface {
    protected DataSource ds;

    protected String digesterName;

    protected DAODigester digester;

    private HashMap setTypes = new HashMap();

    //set the types we expect from the database

    //private ArrayList results = new ArrayList();
    protected final Logger logger = Logger.getLogger(getClass().getName());

    private boolean querySuccessful;

    private SQLException failureDetails;

    /**
     * Should the name of a query which refers to a SQL command of the following
     * form: <code>SELECT currval('sequence') AS key</code> The column name
     * "key" is required, as getCurrentPK() relies on it.
     */
    protected String getCurrentPKName;

    /**
     * Should the name of a query which refers to a SQL command of the following
     * form: <code>SELECT nextval('sequence') AS key</code> The column name
     * "key" is required, as getNextPK() relies on it.
     */
    protected String getNextPKName;

    protected abstract void setDigesterName();

    //YW 11-26-2007, at this time, it is set only by the method "executeWithPK".
    private int latestPK;

    protected Locale locale = ResourceBundleProvider.getLocale();
    protected String oc_df_string = ResourceBundleProvider.getFormatBundle(locale).getString("oc_date_format_string");
    protected String local_df_string = ResourceBundleProvider.getFormatBundle(locale).getString("date_format_string");

    public EntityDAO(DataSource ds) {
        logger.setLevel(Level.WARNING);
        this.ds = ds;
        setDigesterName();
        digester = SQLFactory.getInstance().getDigester(digesterName);
    }

    /**
     * setTypeExpected, expects to enter the type of object to retrieve from the
     * database
     *
     * @param num  the order the column should be extracted from the database
     * @param type the number that is equal to TypeNames
     */
    public void setTypeExpected(int num, int type) {
        setTypes.put(new Integer(num), new Integer(type));
    }

    public void unsetTypeExpected() {
        setTypes = new HashMap();
    }

    /**
     * select, a static query interface to the database, returning an array of
     * hashmaps that contain key->object pairs.
     * <p/>
     * This is the first operation created for the database, so therefore it is
     * the simplest; cull information from the database but not specify any
     * parameters.
     *
     * @param query a static query of the database.
     * @return ArrayList of HashMaps carrying the database values.
     */
    public ArrayList select(String query) {
        clearSignals();

        ArrayList results = new ArrayList();
        ResultSet rs = null;
        Connection con = null;
        Statement ps = null;
        try {
            con = ds.getConnection();
            if (con.isClosed()) {
                if (logger.isLoggable(Level.WARNING))
                    logger.warning("Connection is closed: GenericDAO.select!");
                throw new SQLException();
            }
            ps = con.createStatement();
            rs = ps.executeQuery(query);
            if (logger.isLoggable(Level.INFO)) {
                logger.info("Executing static query, GenericDAO.select: "
                        + query);
                //logger.info("fond information about result set: was null: "+
                //	rs.wasNull());
            }
            //ps.close();
            signalSuccess();
            results = this.processResultRows(rs);
            //rs.close();

        } catch (SQLException sqle) {
            signalFailure(sqle);
            if (logger.isLoggable(Level.WARNING)) {
                logger
                        .warning("Exeception while executing static query, GenericDAO.select: "
                                + query + ": " + sqle.getMessage());
                sqle.printStackTrace();
            }
        } finally {
            this.closeIfNecessary(con, rs, ps);
        }
        //return rs;
        return results;

    }

    public ArrayList select(String query, HashMap variables) {
        clearSignals();

        ArrayList results = new ArrayList();
        ResultSet rs = null;
        Connection con = null;
        PreparedStatementFactory psf = new PreparedStatementFactory(variables);
        PreparedStatement ps = null;
        try {
            con = ds.getConnection();
            if (con.isClosed()) {
                if (logger.isLoggable(Level.WARNING))
                    logger.warning("Connection is closed: GenericDAO.select!");
                throw new SQLException();
            }

            ps = con.prepareStatement(query);
            ps = psf.generate(ps);//enter variables here!
            rs = ps.executeQuery();
            if (logger.isLoggable(Level.INFO)) {
                logger.info("Executing dynamic query, EntityDAO.select:query "
                        + query);
            }
            signalSuccess();
            results = this.processResultRows(rs);

        } catch (SQLException sqle) {
            signalFailure(sqle);
            if (logger.isLoggable(Level.WARNING)) {
                logger
                        .warning("Exeception while executing dynamic query, GenericDAO.select: "
                                + query + ":message: " + sqle.getMessage());
                sqle.printStackTrace();
            }
        } finally {
            this.closeIfNecessary(con, rs, ps);
        }
        return results;

    }

    //Added by YW, 11-26-2007
    public ArrayList select(String query, Connection con) {
        clearSignals();

        ArrayList results = new ArrayList();
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            if (con.isClosed()) {
                if (logger.isLoggable(Level.WARNING))
                    logger.warning("Connection is closed: GenericDAO.select!");
                throw new SQLException();
            }

            ps = con.prepareStatement(query);
            rs = ps.executeQuery();
            if (logger.isLoggable(Level.INFO)) {
                logger.info("Executing dynamic query, EntityDAO.select:query "
                        + query);
            }
            signalSuccess();
            results = this.processResultRows(rs);

        } catch (SQLException sqle) {
            signalFailure(sqle);
            if (logger.isLoggable(Level.WARNING)) {
                logger
                        .warning("Exeception while executing dynamic query, GenericDAO.select: "
                                + query + ":message: " + sqle.getMessage());
                sqle.printStackTrace();
            }
        } finally {
            this.closeIfNecessary(rs, ps);
        }
        return results;

    }

    //	private int[] insertKeys;
    //
    //	/**
    //	 * This method is only meaningful immediately after an insert call.
    //	 * @return The number of available insert keys.
    //	 */
    //	public int getNumInsertKeys() {
    //		if (insertKeys == null) {
    //			return 0;
    //		}
    //		return insertKeys.length;
    //	}
    //
    //	/**
    //	 * This method is only meaningful immediately after an insert call.
    //	 * @param ind The index of the insert key to get.
    //	 * @return The insert key for the <code>ind</code>-th row inserted, or
    // <code>0</code> if no such row was inserted.
    //	 */
    //	public int getInsertKey(int ind) {
    //		if (insertKeys == null) {
    //			return 0;
    //		}
    //
    //		if (insertKeys.length >= ind) {
    //			return 0;
    //		}
    //
    //		return insertKeys[ind];
    //	}
    //
    //	/**
    //	 * Retrieve the set of generated keys created by an INSERT query; in
    // particular,
    //	 * populate the insertKeys[] member. This method is called by insert.
    //	 * @param rs The ResultSet of generated keys.
    //	 */
    //	private void processGeneratedKeys(ResultSet rs) {
    //		if (rs != null) {
    //			try {
    //				ArrayList keys = new ArrayList();
    //				while (rs.next()) {
    //					int key = rs.getInt(1);
    //					keys.add(new Integer(key));
    //				}
    //
    //				insertKeys = new int[keys.size()];
    //				for (int i = 0; i < keys.size(); i++) {
    //					Integer key = (Integer) keys.get(i);
    //					if (key == null) {
    //						insertKeys[i] = 0;
    //					}
    //					else {
    //						insertKeys[i] = key.intValue();
    //					}
    //				}
    //			}
    //			catch (Exception e) { }
    //		}
    //	}
    //
    //	/**
    //	 * Executes an INSERT query, automatically tracking the inserted keys.
    //	 * The <code>getNumInsertKeys</code> and <code>getInsertKey</code> method
    // may be called immediately after this method in order to retrieve insert
    // keys automatically generated by the database as a result of this call.
    //	 * @param query The SQL query.
    //	 * @param variables A HashMap of values to be inserted into the SQL
    // statement.
    //	 * The keys are Integer objects whose intValue() indicates the index of
    // the value within the SQL statement (0 is the first index.)
    //	 * The values are Strings, Integers, Characters, Booleans, or Dates which
    // indicate the value to be inserted, or null if the value is to be left
    // null.
    //	 * @param nullVars A HashMap indicating the values in the SQL statement
    // which are to be left null.
    //	 * The keys are Integer objects whose intValue() indicates the index of
    // the value within the SQL statement (0 is the first index.)
    //	 * The values are members of TypeNames indicating the type of value which
    // was expected at the corresponding index in the SQL statement.
    //	 */
    //	public void insert(String query, HashMap variables, HashMap nullVars) {
    //
    //		Connection con = null;
    //		PreparedStatement ps = null;
    //		PreparedStatementFactory psf = new
    // PreparedStatementFactory(variables,nullVars);
    //		try {
    //			con = ds.getConnection();
    //			if (con.isClosed()) {
    //				if (logger.isLoggable(Level.WARNING))
    //					logger.warning("Connection is closed: EntityDAO.execute!");
    //				throw new SQLException();
    //			}
    //			ps = con.prepareStatement(query);
    //			ps = psf.generate(ps);//enter variables here!
    //			if (ps.executeUpdate() !=1) {
    //				logger.warning("Problem with executing dynamic query, EntityDAO: "+
    //						query);
    //				throw new SQLException();
    //
    //			} else {
    //				ResultSet rs = ps.getGeneratedKeys();
    //				processGeneratedKeys(rs);
    //
    //				logger.info("Executing dynamic query, EntityDAO: "+query);
    //			}
    //		} catch (SQLException sqle) {
    //			if (logger.isLoggable(Level.WARNING)) {
    //				logger.warning("Exeception while executing dynamic statement,
    // EntityDAO.execute: "+
    //					query+ ": " + sqle.getMessage());
    //				sqle.printStackTrace();
    //			}
    //		} finally {
    //			this.closeIfNecessary(con, ps);
    //		}
    //	}

    /**
     * execute, the static version of executing an update or insert on a table
     * in the database.
     *
     * @param query a static SQL statement which updates or inserts.
     */
    public void execute(String query) {
        clearSignals();

        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = ds.getConnection();
            if (con.isClosed()) {
                if (logger.isLoggable(Level.WARNING))
                    logger.warning("Connection is closed: EntityDAO.execute!");
                throw new SQLException();
            }
            ps = con.prepareStatement(query);

            if (ps.executeUpdate() != 1) {
                logger
                        .warning("Problem with executing static query, EntityDAO: "
                                + query);
                throw new SQLException();
            } else {
                signalSuccess();
                logger.info("Executing static query, EntityDAO: " + query);
            }
        } catch (SQLException sqle) {
            signalFailure(sqle);
            if (logger.isLoggable(Level.WARNING)) {
                logger
                        .warning("Exeception while executing static statement, GenericDAO.execute: "
                                + query + ":message: " + sqle.getMessage());
                sqle.printStackTrace();
            }
        } finally {
            this.closeIfNecessary(con, ps);
        }
    }

    public void execute(String query, HashMap variables) {
        clearSignals();

        Connection con = null;
        PreparedStatement ps = null;
        PreparedStatementFactory psf = new PreparedStatementFactory(variables);
        try {
            con = ds.getConnection();
            if (con.isClosed()) {
                if (logger.isLoggable(Level.WARNING))
                    logger.warning("Connection is closed: EntityDAO.execute!");
                throw new SQLException();
            }
            ps = con.prepareStatement(query);
            ps = psf.generate(ps);//enter variables here!
            if (ps.executeUpdate() < 0) {// change by jxu, delete can affect
                // more than one row
                logger.warning("Problem with executing dynamic query, EntityDAO: "
                        + query);
                throw new SQLException();

            } else {
                signalSuccess();
                logger.info("Executing dynamic query, EntityDAO: " + query);
            }
        } catch (SQLException sqle) {
            signalFailure(sqle);
            if (logger.isLoggable(Level.WARNING)) {
                logger.warning("Exeception while executing dynamic statement, EntityDAO.execute: "
                        + query + ": " + sqle.getMessage());
                sqle.printStackTrace();
            }
        } finally {
            this.closeIfNecessary(con, ps);
        }
    }

    public void execute(String query, HashMap variables, HashMap nullVars) {
        clearSignals();

        Connection con = null;
        PreparedStatement ps = null;
        PreparedStatementFactory psf = new PreparedStatementFactory(variables,
                nullVars);
        try {
            con = ds.getConnection();
            if (con.isClosed()) {
                if (logger.isLoggable(Level.WARNING))
                    logger.warning("Connection is closed: EntityDAO.execute!");
                throw new SQLException();
            }
            ps = con.prepareStatement(query);
            ps = psf.generate(ps);//enter variables here!
            if (ps.executeUpdate() != 1) {
                logger.warning("Problem with executing dynamic query, EntityDAO: "
                        + query);
                throw new SQLException();

            } else {
                signalSuccess();
                logger.info("Executing dynamic query, EntityDAO: " + query);
            }
        } catch (SQLException sqle) {
            signalFailure(sqle);
            if (logger.isLoggable(Level.WARNING)) {
                logger
                        .warning("Exeception while executing dynamic statement, EntityDAO.execute: "
                                + query + ": " + sqle.getMessage());
                sqle.printStackTrace();
            }
        } finally {
            this.closeIfNecessary(con, ps);
        }
    }

    /**
     * This method inserts one row for an entity table and gets latestPK of this row.
     *
     * @param query
     * @param variables
     * @param nullVars
     * @author ywang 11-26-2007
     */
    public void executeWithPK(String query, HashMap variables, HashMap nullVars) {
        clearSignals();

        Connection con = null;
        PreparedStatement ps = null;
        PreparedStatementFactory psf = new PreparedStatementFactory(variables,
                nullVars);
        try {
            con = ds.getConnection();
/*            Map map = con.getTypeMap();
            for (Iterator it = map.keySet().iterator(); it.hasNext();){
                String key = (String)it.next();
                System.out.println("Key [" + key + "] Value [" + map.get(key) + "]");
            }*/
            if (con.isClosed()) {
                if (logger.isLoggable(Level.WARNING))
                    logger.warning("Connection is closed: EntityDAO.execute!");
                throw new SQLException();
            }
            ps = con.prepareStatement(query);
            ps = psf.generate(ps);//enter variables here!
            if (ps.executeUpdate() != 1) {
                logger.warning("Problem with executing dynamic query, EntityDAO: "
                        + query);
                throw new SQLException();

            } else {
                logger.info("Executing dynamic query, EntityDAO: " + query);

                if (getCurrentPKName == null) {
                    this.latestPK = 0;
                }

                this.unsetTypeExpected();
                this.setTypeExpected(1, TypeNames.INT);

                ArrayList al = select(digester.getQuery(getCurrentPKName), con);

                if (al.size() > 0) {
                    HashMap h = (HashMap) al.get(0);
                    this.latestPK = ((Integer) h.get("key")).intValue();
                }

            }

        } catch (SQLException sqle) {
            signalFailure(sqle);
            if (logger.isLoggable(Level.WARNING)) {
                logger
                        .warning("Exeception while executing dynamic statement, EntityDAO.execute: "
                                + query + ": " + sqle.getMessage());
                sqle.printStackTrace();
            }
        } finally {
            this.closeIfNecessary(con, ps);
        }
    }

    /*
      * Currently, latestPK is set only in executeWithPK() after inserting has been executed successfully.
      * So, this method should be called only immediately after executeWithPK()
      *
      * @return
      *
      * ywang 11-26-2007
      */
    protected int getLatestPK() {
        return latestPK;
    }

    public ArrayList processResultRows(ResultSet rs) {//throws SQLException
        ArrayList al = new ArrayList();
        HashMap hm;

        try {
            //rs.beforeFirst();
            while (rs.next()) {
                hm = new HashMap();
                ResultSetMetaData rsmd = rs.getMetaData();

                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    String column = rsmd.getColumnName(i).toLowerCase();
                    Integer type = (Integer) setTypes.get(new Integer(i));
                    //logger.warning("column name: "+column+" type # "+type.intValue()+" row # "+i);
                    switch (type.intValue()) {
                        //just putting the top five in here for now, tbh
                        //put in statements to catch nulls in the db, tbh
                        // 10-15-2004
                        case TypeNames.DATE:
                            //logger.warning("date: "+column);
                            hm.put(column, rs.getDate(i));
                            //do we want to put in a fake date if it's null?
                            /*
                                * if (rs.wasNull()) { hm.put(column,new
                                * Date(System.currentTimeMillis())); }
                                */
                            break;
                        case TypeNames.TIMESTAMP:
                            //logger.warning("timestamp: "+column);
                            hm.put(column, rs.getTimestamp(i));
                            break;
                        case TypeNames.DOUBLE:
                            //logger.warning("double: "+column);
                            hm.put(column, new Double(rs.getDouble(i)));
                            if (rs.wasNull()) {
                                hm.put(column, new Double(0));
                            }
                            break;
                        case TypeNames.BOOL:
                        //BADS FLAG
                            if (SQLInitServlet.getDBName().equals("oracle")) {
                                hm.put(column, new Boolean(rs.getInt(i)==1 ? true : false));
                                if (rs.wasNull()) {
                                    if (column.equalsIgnoreCase("start_time_flag") || column.equalsIgnoreCase("end_time_flag")) {
                                        hm.put(column, new Boolean(false));
                                    } else {
                                        hm.put(column, new Boolean(true));
                                    }
                                }
                            } else {
                                hm.put(column, new Boolean(rs.getBoolean(i)));
                                if (rs.wasNull()) {
                                    //YW 08-17-2007 << Since I didn't investigate what's the impact if changing true to false,
                                    // I only do change for the columns of "start_time_flag" and "end_time_flag" in the table study_event
                                    if (column.equalsIgnoreCase("start_time_flag") || column.equalsIgnoreCase("end_time_flag")) {
                                        hm.put(column, new Boolean(false));
                                    } else {
                                        hm.put(column, new Boolean(true));
                                    }
                                    //bad idea? what to put, then?
                                }
                            }
                            break;
                        case TypeNames.FLOAT:
                            hm.put(column, new Float(rs.getFloat(i)));
                            if (rs.wasNull()) {
                                hm.put(column, new Float(0.0));
                            }
                            break;
                        case TypeNames.INT:
                            hm.put(column, new Integer(rs.getInt(i)));
                            if (rs.wasNull()) {
                                hm.put(column, new Integer(0));
                            }
                            break;
                        case TypeNames.STRING:
                            hm.put(column, rs.getString(i));
                            if (rs.wasNull()) {
                                hm.put(column, "");
                            }
                            break;
                        case TypeNames.CHAR:
                            hm.put(column, rs.getString(i));
                            if (rs.wasNull()) {
                                char x = 'x';
                                hm.put(column, new Character(x));
                            }
                            break;
                        default:
                            //do nothing?
                    }//end switch

                }//end for loop
                al.add(hm);
                //adding a row gotten from the database
            }
        } catch (SQLException sqle) {
            if (logger.isLoggable(Level.WARNING)) {
                logger
                        .warning("Exception while processing result rows, EntityDAO.select: "
                                + ": "
                                + sqle.getMessage()
                                + ": array length: "
                                + al.size());
                sqle.printStackTrace();
            }
        }
        return al;
    }

    /*
    * @return the current value of the primary key sequence, if <code>
    * getNextPKName </code> is non-null, or null if <code> getNextPKName
    * </code> is null.
    */
    public int getNextPK() {
        int answer = 0;

        if (getNextPKName == null) {
            return answer;
        }

        this.unsetTypeExpected();
        this.setTypeExpected(1, TypeNames.INT);

        ArrayList<HashMap<String, ?>> al = select(digester.getQuery(getNextPKName));

        if (al.size() > 0) {
            HashMap<String, ?> h = al.get(0);
            answer = ((Integer) h.get("key")).intValue();
        }

        return answer;
    }


    /*
      * @return the current value of the primary key sequence, if <code>
      * getCurrentPKName </code> is non-null, or null if <code> getCurrentPKName
      * </code> is null.
      */
    public int getCurrentPK() {
        int answer = 0;

        if (getCurrentPKName == null) {
            return answer;
        }

        this.unsetTypeExpected();
        this.setTypeExpected(1, TypeNames.INT);

        ArrayList al = select(digester.getQuery(getCurrentPKName));

        if (al.size() > 0) {
            HashMap h = (HashMap) al.get(0);
            answer = ((Integer) h.get("key")).intValue();
        }

        return answer;
    }

    /**
     * This method executes a "findByPK-style" query. Such a query has two
     * characteristics:
     * <ol>
     * <li>The columns SELECTed by the SQL are all of the columns in the table
     * relevant to the DAO, and only those columns. (e.g., in StudyDAO, the
     * columns SELECTed are all of the columns in the study table, and only
     * those columns.)
     * <li>It returns at most one EntityBean.
     * <ul>
     * <li>Typically this means that the WHERE clause includes the columns in a
     * candidate key with "=" criteria.
     * <li>e.g., "WHERE item_id = ?" when selecting from item
     * <li>e.g., "WHERE item_id = ? AND event_crf_id=?" when selecting from
     * item_data
     * </ol>
     * <p/>
     * Note that queries which join two tables may be included in the definition
     * of "findByPK-style" query, as long as the first criterion is met.
     *
     * @param queryName The name of the query which should be executed.
     * @param variables The set of variables used to populate the PreparedStatement;
     *                  should be empty if none are needed.
     * @return The EntityBean selected by the query.
     */
    public EntityBean executeFindByPKQuery(String queryName, HashMap variables) {
        EntityBean answer = new EntityBean();

        String sql = digester.getQuery(queryName);

        ArrayList rows;
        if ((variables == null) || variables.isEmpty()) {
            rows = this.select(sql);
        } else {
            rows = this.select(sql, variables);
        }

        Iterator it = rows.iterator();

        if (it.hasNext()) {
            answer = (EntityBean) this
                    .getEntityFromHashMap((HashMap) it.next());
        }

        return answer;
    }

    /**
     * Exactly equivalent to calling
     * <code>executeFindByPKQuery(queryName, new HashMap())</code>.
     *
     * @param queryName The name of the query which should be executed.
     * @return The EntityBean selected by the query.
     */
    public EntityBean executeFindByPKQuery(String queryName) {
        return executeFindByPKQuery(queryName, new HashMap());
    }

    public void closeIfNecessary(Connection con) {
        try {
            //close the connection for right now
            if (con != null)
                con.close();
        } catch (SQLException sqle) {//eventually throw a custom exception,tbh
            if (logger.isLoggable(Level.WARNING)) {
                logger
                        .warning("Exception thrown in GenericDAO.closeIfNecessary");
                sqle.printStackTrace();
            }
        }//end of catch
    }

    public void closeIfNecessary(Connection con, ResultSet rs) {
        try {
            //close the connection for right now
            if (rs != null)
                rs.close();
            if (con != null)
                con.close();
        } catch (SQLException sqle) {//eventually throw a custom exception,tbh
            if (logger.isLoggable(Level.WARNING)) {
                logger
                        .warning("Exception thrown in GenericDAO.closeIfNecessary");
                sqle.printStackTrace();
            }
        }//end of catch
    }

    public void closeIfNecessary(Connection con, ResultSet rs, Statement ps) {
        try {
            if (ps != null)
                ps.close();
            if (rs != null)
                rs.close();
            if (con != null)
                con.close();
        } catch (SQLException sqle) {//eventually throw a custom exception,tbh
            if (logger.isLoggable(Level.WARNING)) {
                logger
                        .warning("Exception thrown in GenericDAO.closeIfNecessary");
                sqle.printStackTrace();
            }
        }//end of catch
    }

    public void closeIfNecessary(ResultSet rs, PreparedStatement ps) {
        try {
            if (ps != null)
                ps.close();
            if (rs != null)
                rs.close();
        } catch (SQLException sqle) {//eventually throw a custom exception,tbh
            if (logger.isLoggable(Level.WARNING)) {
                logger
                        .warning("Exception thrown in GenericDAO.closeIfNecessary(rs,ps)");
                sqle.printStackTrace();
            }
        }//end of catch
    }

    public void closeIfNecessary(Connection con, PreparedStatement ps) {
        try {
            if (ps != null)
                ps.close();
            if (con != null)
                con.close();
        } catch (SQLException sqle) {//eventually throw a custom exception,tbh
            if (logger.isLoggable(Level.WARNING)) {
                logger
                        .warning("Exception thrown in GenericDAO.closeIfNecessary");
                sqle.printStackTrace();
            }
        }//end of catch
    }

    /**
     * getDS, had to add it to allow queries of other daos within the daos
     *
     * @return Returns the ds.
     */
    public DataSource getDs() {
        return ds;
    }
    /**
     * @param ds The ds to set.
     */
    //public void setDs(DataSource ds) {
    //this.ds = ds;
    //}

    /**
     * Clear the signals which indicate the success or failure of the query.
     * This method should be called at the beginning of every select or execute
     * method.
     */
    protected void clearSignals() {
        querySuccessful = false;
    }

    /**
     * Signal that the query was successful. Either this method or signalFailure
     * should be called by the time a select or execute method returns.
     */
    protected void signalSuccess() {
        querySuccessful = true;
    }

    /**
     * Signal that the query was unsuccessful. Either this method or
     * signalSuccess should be called by the time a select or execute method
     * returns.
     *
     * @param sqle The SQLException which was thrown by
     *             PreparedStatement.execute/executeUpdate.
     */
    protected void signalFailure(SQLException sqle) {
        querySuccessful = false;
        failureDetails = sqle;
    }

    /**
     * @return Returns the failureDetails.
     */
    public SQLException getFailureDetails() {
        return failureDetails;
    }

    /**
     * @return Returns the querySuccessful.
     */
    public boolean isQuerySuccessful() {
        return querySuccessful;
    }

    protected String selectString(HashMap hm, String column) {
        if (hm.containsKey(column)) {
            try {
                String s = (String) hm.get(column);
                if (s != null) {
                    return s;
                }
            } catch (Exception e) {
                return "";
            }
        }
        return "";
    }

    protected int selectInt(HashMap hm, String column) {
        if (hm.containsKey(column)) {
            try {
                Integer i = (Integer) hm.get(column);
                if (i != null) {
                    return i.intValue();
                }
            } catch (Exception e) {
                return 0;
            }
        }
        return 0;
    }

    protected boolean selectBoolean(HashMap hm, String column) {
        if (hm.containsKey(column)) {
			try {
				Boolean b = (Boolean) hm.get(column);
				if (b != null) {
					return b.booleanValue();
				}
			} catch (Exception e) { return false; }
		}
		return false;
	}
}