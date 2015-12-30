/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package org.akaza.openclinica.dao.core;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;

import javax.sql.DataSource;

import oracle.sql.CLOB;

import org.akaza.openclinica.bean.core.EntityBean;

/**
 * <P>
 * EntityDAO.java, the generic data access object class for the database layer,
 * by Tom Hickerson, 09/24/2004
 * 
 * A signalling system was added on 7 Dec 04 to indicate the success or failure
 * of a query. A query is considered successful iff a SQLException was not
 * thrown in the process of executing the query.
 * 
 * The system can be used by outside classes / subclasses as follows: -
 * Immediately after calling select or execute, isQuerySuccessful() is
 * <code>true</code> if the query was successful, <code>false</code>
 * otherwise. - If isQuerySuccessful returns <code>false</code>
 * getFailureDetails() returns the SQLException which was thrown.
 * 
 * In order to maintain the system, the following invariants must be maintained
 * by developers: 1. Every method executing a query must call clearSignals() as
 * the first statement. 2. Every method executing a query must call either
 * signalSuccess or signalFailure before returning.
 * 
 * At the time of writing, the only methods which execute queries are select and
 * execute.
 * 
 * @author thickerson
 */
public abstract class EntityDAO implements DAOInterface {
	protected DataSource ds;

	protected String digesterName;

	protected DAODigester digester;

	protected static String dbName=null;
	
	private HashMap<Integer, Integer> setTypes = new HashMap<Integer, Integer>();

	//set the types we expect from the database

	//private ArrayList results = new ArrayList();
	protected transient final Logger logger = Logger.getLogger(getClass().getName());

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

    /**
     * @return the digester.
     */
    public DAODigester getDigester(){
        return digester;
    }
    
	public EntityDAO(DataSource ds) {
		logger.setLevel(Level.WARNING);
		this.ds = ds;
		setDigesterName();
		SQLFactory factory=SQLFactory.getInstance();
		digester = factory.getDigester(digesterName);
		dbName = factory.getDbName();
	}

	/**
	 * setTypeExpected, expects to enter the type of object to retrieve from the
	 * database
	 * 
	 * @param num
	 *            the order the column should be extracted from the database
	 * @param type
	 *            the number that is equal to TypeNames
	 */
	public void setTypeExpected(int num, int type) {
		setTypes.put(new Integer(num), new Integer(type));
	}

	public void unsetTypeExpected() {
		setTypes = new HashMap<Integer, Integer>();
	}

	/**
	 * Sets the types expected to be a single number for a select count(*) type query
	 *
	 */
	public void setCountTypeExpected() {
		unsetTypeExpected();
		setTypeExpected(1, TypeNames.INT);
	}

	/**
	 * select, a static query interface to the database, returning an array of
	 * hashmaps that contain key->object pairs.
	 * <P>
	 * This is the first operation created for the database, so therefore it is
	 * the simplest; cull information from the database but not specify any
	 * parameters.
	 * 
	 * @param query
	 *            a static query of the database.
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
	 * @param query  a static SQL statement which updates or inserts.
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
					if (type==null) {
						logger.warning("Unknown \"type\": null for column " + column);
						throw new RuntimeException("Null type");
					}
					String colTypeName = rsmd.getColumnTypeName(i);
					int colType = rsmd.getColumnType(i);

					switch (type.intValue()) {
					//just putting the top five in here for now, tbh
					//put in statements to catch nulls in the db, tbh
					// 10-15-2004
					case TypeNames.DATE:
						hm.put(column, rs.getDate(i));
						//do we want to put in a fake date if it's null?
						/*
						 * if (rs.wasNull()) { hm.put(column,new
						 * Date(System.currentTimeMillis())); }
						 */
						break;
					case TypeNames.TIMESTAMP:
						hm.put(column, rs.getTimestamp(i));						
						break;	
					case TypeNames.DOUBLE:
						hm.put(column, new Double(rs.getDouble(i)));
						if (rs.wasNull()) {
							hm.put(column, new Double(0));
						}
						break;
					case TypeNames.BOOL:
						hm.put(column, new Boolean(rs.getBoolean(i)));
						if (rs.wasNull()) {
							hm.put(column, new Boolean(true));
							//bad idea? what to put, then?
						}
						break;
					case TypeNames.FLOAT:
						hm.put(column, new Float(rs.getFloat(i)));
						//if (rs.wasNull()) {
						//	hm.put(column, new Float(0.0));
						//}
                        if (rs.wasNull()) {
                            hm.put(column, null);
                        }
						break;
					case TypeNames.INT:
						hm.put(column, new Integer(rs.getInt(i)));
						if (rs.wasNull()) {
							hm.put(column, new Integer(0));
						}
						break;
					case TypeNames.STRING:
						String rsVal=rs.getString(i);
						if (rsVal==null) {
							Object obj=rs.getObject(i);
							if (obj instanceof CLOB) {
								rsVal=((CLOB)obj).getSubString(1L, (int)((CLOB)obj).length());
	                        } else if (obj instanceof Clob) {
								rsVal=((Clob)obj).getSubString(1L, (int)((Clob)obj).length());
							}
						}
						hm.put(column, rsVal);
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
						logger.warning("Unknown \"expected type\": "
								+ type + " for column " + column);
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

	/*
	 * @return the current value of the primary key sequence, if <code>
	 * getCurrentPKName </code> is non-null, or null if <code> getCurrentPKName
	 * </code> is null.
	 */
	public int getNextPK() {
		int answer = 0;

		if (getNextPKName == null) {
			return answer;
		}

		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);

		ArrayList al = select(digester.getQuery(getNextPKName));

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
	 * 
	 * Note that queries which join two tables may be included in the definition
	 * of "findByPK-style" query, as long as the first criterion is met.
	 * 
	 * @param queryName
	 *            The name of the query which should be executed.
	 * @param variables
	 *            The set of variables used to populate the PreparedStatement;
	 *            should be empty if none are needed.
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
	 * @param queryName
	 *            The name of the query which should be executed.
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

	/**getDS, had to add it to allow queries of other daos within the daos
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
	 * @param sqle
	 *            The SQLException which was thrown by
	 *            PreparedStatement.execute/executeUpdate.
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
			} catch (Exception e) { return ""; }
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
			} catch (Exception e) { return 0; }
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
	
	public static String createStatement(String paramStatement, List<Object> params) {
		String statement = ""+paramStatement;
		final String TEMP_PARAM_STRING = "#-#";
		statement = statement.replace("?", TEMP_PARAM_STRING);
		for (Object param : params) {
			String convertedParam = convertParam(param);
			statement=statement.replaceFirst(TEMP_PARAM_STRING, Matcher.quoteReplacement(convertedParam));
		}
		statement = statement.replace(TEMP_PARAM_STRING, "?");
		//System.out.println("***SQL***"+statement);
		return statement;
	}

	protected static String convertParam(Object param) {
		String convertedParam = null;
		if (param == null) {
			convertedParam="NULL";
		} else if (param instanceof String) {
			convertedParam = ""+param;
			convertedParam = convertedParam.replace("'", "''");
			if ("oracle".equals(dbName)) { //Oracle does not do backslash substitution the way Postgres does
				convertedParam = convertedParam.replace("\\\\", "\\");
			}
			convertedParam="'"+convertedParam+"'";
		} else if (param instanceof Number) {
			convertedParam=""+param;
		} else if (param instanceof Date) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			if ("oracle".equals(dbName)) {
				convertedParam = "to_date('"+sdf.format((Date)param)+"', 'YYYY-MM-DD')";
			} else {
				convertedParam = "date('"+sdf.format((Date)param)+"')";
			}
		} else if (param instanceof Boolean) {
			if ("oracle".equals(dbName)) {
				convertedParam = (((Boolean)param).booleanValue() ? "1" : "0");
			} else {
				convertedParam = param.toString();
			}
		} else if (param instanceof List) {
			List paramList = (List) param;
			boolean first=true;
			convertedParam="";
			for (Object paramItem : paramList) {
				convertedParam += (first ? "" : ",")+convertParam(paramItem);
				first=false;
			}
		} else {
			convertedParam=param.toString();
		}
		return convertedParam;
	}
}