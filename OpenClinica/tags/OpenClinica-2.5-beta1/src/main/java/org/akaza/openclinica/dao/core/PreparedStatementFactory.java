/*
 * Created on Aug 26, 2004
 *
 *
 */
package org.akaza.openclinica.dao.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author thickerson
 * 
 * 
 */
public class PreparedStatementFactory {
    private HashMap variables = new HashMap();
    private HashMap nullVars = new HashMap();// to handle null
    // inputs,jxu,2004-10-28
    protected final Logger logger = LoggerFactory.getLogger(getClass().getName());

    public PreparedStatementFactory() {
    }

    public PreparedStatementFactory(HashMap variables) {
        this.variables = variables;
    }

    /**
     * If need to handle null inputs
     * 
     * @param variables
     * @param nullVars
     */
    public PreparedStatementFactory(HashMap variables, HashMap nullVars) {
        this.variables = variables;
        this.nullVars = nullVars;
    }

    public void addVariable(int order, Object param) {
        variables.put(new Integer(order), param);
    }

    public PreparedStatement generate(PreparedStatement ps) throws SQLException, NullPointerException {

        Set varSet = variables.entrySet();
        for (Iterator varIt = varSet.iterator(); varIt.hasNext();) {
            Map.Entry varMe = (Map.Entry) varIt.next();
            Integer order = (Integer) varMe.getKey();
            Object objParam = varMe.getValue();
            if (objParam == null) {
                logger.warn("found null object! " + order);
                if (nullVars.get(order) != null) {
                    Integer nullType = (Integer) nullVars.get(order);
                    ps.setNull(order.intValue(), nullType.intValue());
                } else {
                    throw new NullPointerException("No type found for this null object at order:" + order + ", make sure you set the type in your DAO.");
                }
            } else {
                String objType = objParam.getClass().getName();

                // logger.info("\nfound object name:["+objType + "] [" +
                // order+ "] value["+ objParam + "]");

                if ("java.lang.String".equals(objType)) {
                    ps.setString(order.intValue(), objParam.toString());
                } else if ("java.lang.Float".equals(objType)) {
                    Float objFloatParam = (Float) objParam;
                    ps.setFloat(order.intValue(), objFloatParam.floatValue());
                } else if ("java.lang.Integer".equals(objType)) {
                    Integer objIntParam = (Integer) objParam;
                    ps.setInt(order.intValue(), objIntParam.intValue());
                } else if ("java.util.Date".equals(objType)) {
                    java.util.Date objTempDate = (java.util.Date) objParam;
                    java.sql.Date objDateParam = new java.sql.Date(objTempDate.getTime());
                    // (java.sql.Date)objParam;
                    ps.setDate(order.intValue(), objDateParam);
                } else if ("java.sql.Date".equals(objType)) {// added by
                    // jxu,2004-10-26
                    // a date from DB but not set on page, still sql date type
                    ps.setDate(order.intValue(), (java.sql.Date) objParam);
                } else if ("java.sql.Timestamp".equals(objType)) {
                    ps.setTimestamp(order.intValue(), (java.sql.Timestamp) objParam);
                } else if ("java.lang.Boolean".equals(objType)) {
                    // BADS FLAG
                    if (SQLInitServlet.getDBName().equals("oracle")) {
                        Boolean objBoolParam = (Boolean) objParam;
                        ps.setInt(order.intValue(), objBoolParam ? 1 : 0);
                    } else {
                        Boolean objBoolParam = (Boolean) objParam;
                        ps.setBoolean(order.intValue(), objBoolParam.booleanValue());
                    }

                } else if ("java.lang.Byte".equals(objType)) {
                    ps.setObject(order.intValue(), objParam, Types.BIT);
                } else if ("java.lang.Character".equals(objType)) {
                    ps.setObject(order.intValue(), objParam, Types.CHAR);
                } else if ("java.lang.Double".equals(objType)) {
                    ps.setObject(order.intValue(), objParam, Types.DOUBLE);
                } else if ("java.lang.Long".equals(objType)) {
                    ps.setObject(order.intValue(), objParam, Types.NUMERIC);
                } else if ("java.lang.Short".equals(objType)) {
                    ps.setObject(order.intValue(), objParam, Types.SMALLINT);
                } else if ("java.math.BigDecimal".equals(objType)) {
                    ps.setBigDecimal(order.intValue(), (java.math.BigDecimal) objParam);
                } else {
                    // throw missing variable type exception here???
                    throw new NullPointerException("did not find object, possible null at " + order);
                }
            }// end of else loop
        }// end of for loop
        // added by jxu for debugging,but not implemented by postgres,
        // 2004-10-27
        /*
         * ParameterMetaData pmd = ps.getParameterMetaData(); for(int i=1;i<=pmd.getParameterCount();i++){
         * logger.info("Meta Data for Parameter "+i);
         * logger.info("Parameter Type Name
         * "+pmd.getParameterTypeName(i)); logger.info("Parameter SQL
         * TYPE "+pmd.getParameterType(i)); }
         */
        return ps;
    }
}
