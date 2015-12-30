package org.akaza.openclinica.logic.core.function;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.core.SessionManager;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.EntityDAO;
import org.akaza.openclinica.dao.logic.TableDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.logic.core.Parser;

public class LookupTB extends AbstractFunction {
    public LookupTB(){
        super();
    }
    
    /**
     * @see Function#execute(HashMap)
     */
    public void execute(HashMap<Integer, String> map){
        logger.info("Execute the function LookupTB... ");
        //private static final String FUNCTION_PACKAGE = "org.akaza.openclinica.logic.core.function";
        this.map = map;
        //parameters in format : tablename, output, input1, input2, SessionManager, EventCRFBean, and the original input args..
        if (argumentCount() >= 6) {
    		NumberFormat nf = NumberFormat.getIntegerInstance ();
    		String func="AGE";
    		value="";
			try {
				String inparams = (String)(getArgument(6));
				inparams=inparams.substring(inparams.indexOf('(')+1, inparams.length()-1);
				String inparamsList[]=inparams.split(",");
				String input1_arg = processArgument(getArgument(2));
		        String input2_arg = processArgument(getArgument(3));
		        String input1_name = inparamsList[2].trim().substring(0,inparamsList[2].indexOf(':')-1).toUpperCase();//(processArgument(getArgument(2))).trim().toLowerCase();
				String input2_name = inparamsList[3].trim().substring(0,inparamsList[3].indexOf(':')-1).toUpperCase();//(processArgument(getArgument(3))).trim().toLowerCase();
				
				if(input2_arg==null || input1_arg==null || (input2_arg.trim().length()==0 && !input2_name.equalsIgnoreCase(func)) || (input1_arg.trim().length()==0 && !input1_name.equalsIgnoreCase(func)) ){
					return;
				}
				
				SessionManager sm = (SessionManager)(getArgument(4));
				EventCRFBean ecb = (EventCRFBean)(getArgument(5));
				String tableName = (processArgument(getArgument(0))).trim().toUpperCase();
				String output = (processArgument(getArgument(1))).trim().toUpperCase();
				int input1_data=0;
				int input2_data=0;
				
				if(input1_name.contains(func) || input2_name.contains(func)){
					StudySubjectDAO ssdao = new StudySubjectDAO(sm.getDataSource());
					SubjectDAO subjectDao = new SubjectDAO(sm.getDataSource());
					StudySubjectBean ssb  = (StudySubjectBean) ssdao.findByPK(ecb.getStudySubjectId());
					SubjectBean subject   = (SubjectBean) subjectDao.findByPK(ssb.getSubjectId());	
					
					 if(input1_name.contains(func)){
						 
						Function f = (Function)(getArgument(2));
						f.addArgument(subject.getDateOfBirth());
						f.addArgument(ecb.getDateInterviewed());	
			            f.execute(map);
			            input1_data = (nf.parse(f.getValue())).intValue();
			            input2_data = (nf.parse(input2_arg)).intValue();
					}else if(input2_name.contains(func)){
						Function f = (Function)(getArgument(3));
						f.addArgument(subject.getDateOfBirth());
						f.addArgument(ecb.getDateInterviewed());	
			            f.execute(map);
			            input2_data = (nf.parse(f.getValue())).intValue();
			            input1_data = (nf.parse(input1_arg)).intValue();
					}
					
				}else{
					input1_data = (nf.parse(input1_arg)).intValue();
					input2_data = (nf.parse(input2_arg)).intValue();
				}
					
				List<Object> params = new ArrayList<Object>();
                TableDAO tdao = new TableDAO(sm.getDataSource());
                DAODigester digester = tdao.getDigester();
                String sql;
                int tableid=-1;
                Statement s = null;
                Connection con = null;
                ResultSet rs = null;
                con = (sm.getDataSource()).getConnection(); 
                s = con.createStatement();
                
                //to get unique table id: must provide Form Version ID or combination of (form name and version name)
                params.add(tableName);
                params.add(ecb.getCRFVersionId());
                sql = EntityDAO.createStatement(digester.getQuery("selectTableID"), params);
                rs = s.executeQuery(sql);
                if (rs.next()) {
                	tableid=rs.getInt("score_ref_table_id");
                }
                rs.close();
                
                params.clear();
                params.add(output);
                params.add(tableid);
                params.add(input1_name);
                params.add(tableid);
                params.add(input1_data);//input1_data  the age
                params.add(input2_name);
                params.add(tableid);
                params.add(input2_data);
                sql = EntityDAO.createStatement(digester.getQuery("selectAttributeValue"), params);
                rs = s.executeQuery(sql);
                if (rs.next()) {
                	value=rs.getString("score_attribute_charvalue");
                	if(value==null) return;
                	value=value.substring(1, value.length()-1);
                }
                rs.close();

                s.close();
                con.close();
                
			} catch (Exception e) {
				errors.put(new Integer(errorCount++), e.getMessage());
			}
        	
        } else {
			logger.info("Wrong number of args for LookupTB... ");
			value = "";
			return;
        }
       //value = Integer.toString(result);       
    }
}