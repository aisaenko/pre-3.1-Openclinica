/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2009 Akaza Research
 */
package org.akaza.openclinica.control.managestudy;

import static org.jmesa.facade.TableFacadeFactory.createTableFacade;

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.submit.SubmitDataServlet;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.dao.managestudy.FindSubjectsFilter;
import org.akaza.openclinica.dao.managestudy.FindSubjectsSort;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;
import org.apache.commons.lang.StringUtils;
import org.jmesa.core.filter.FilterMatcher;
import org.jmesa.core.filter.MatcherKey;
import org.jmesa.facade.TableFacade;
import org.jmesa.limit.Filter;
import org.jmesa.limit.FilterSet;
import org.jmesa.limit.Limit;
import org.jmesa.limit.Sort;
import org.jmesa.limit.SortSet;
import org.jmesa.view.component.Column;
import org.jmesa.view.component.Row;
import org.jmesa.view.component.Table;
import org.jmesa.view.editor.BasicCellEditor;
import org.jmesa.view.editor.CellEditor;
import org.jmesa.view.editor.DateCellEditor;
import org.jmesa.view.html.component.HtmlColumn;
import org.jmesa.view.html.component.HtmlRow;
import org.jmesa.view.html.component.HtmlTable;
import org.jmesa.view.html.editor.DroplistFilterEditor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Servlet for creating a user account.
 * 
 * @author Krikor Krumlian
 */
public class FindSubjectsServlet extends SecureController {

    private static final long serialVersionUID = 1L;

    // < ResourceBundle restext;
    Locale locale;
    private StudySubjectDAO studySubjectDAO;
    private SubjectDAO subjectDAO;

    /*
     * (non-Javadoc)
     * @see org.akaza.openclinica.control.core.SecureController#mayProceed()
     */
    @Override
    protected void mayProceed() throws InsufficientPermissionException {

        locale = request.getLocale();
        if (ub.isSysAdmin()) {
            return;
        }

        if (SubmitDataServlet.mayViewData(ub, currentRole)) {
            return;
        }

        addPageMessage(respage.getString("no_have_correct_privilege_current_study") + respage.getString("change_study_contact_sysadmin"));
        throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("may_not_submit_data"), "1");
    }

    @Override
    protected void processRequest() throws Exception {
        FormProcessor fp = new FormProcessor(request);
        String findSubjectsHtml = renderAuditUserLoginTable();
        request.setAttribute("findSubjectsHtml", findSubjectsHtml);
        forwardPage(Page.FIND_SUBJECTS);

    }

    private String renderAuditUserLoginTable() {

        TableFacade tableFacade = createTableFacade("findSubjects", request);
        tableFacade.setColumnProperties("label", "status", "oid", "subject.charGender", "secondaryLabel", "actions");
        tableFacade.addFilterMatcher(new MatcherKey(Character.class), new CharFilterMatcher());
        tableFacade.addFilterMatcher(new MatcherKey(Status.class), new StatusFilterMatcher());

        tableFacade.setStateAttr("restore");
        setDataAndLimitVariables(tableFacade);
        tableFacade.setToolbar(new FindSubjectsToolbar());

        return renderAuditUserLoginTableHtml(tableFacade);

    }

    private String renderAuditUserLoginTableHtml(TableFacade tableFacade) {

        // Fix column titles
        HtmlTable table = (HtmlTable) tableFacade.getTable();

        table.setCaption("");
        HtmlRow row = table.getRow();

        HtmlColumn userName = row.getColumn("label");
        userName.setTitle(resword.getString("study_subject_ID"));

        HtmlColumn status = row.getColumn("status");
        status.setTitle(resword.getString("subject_status"));
        status.getFilterRenderer().setFilterEditor(new StatusDroplistFilterEditor());
        status.getCellRenderer().setCellEditor(new CellEditor() {
            public Object getValue(Object item, String property, int rowcount) {
                Status status = (Status) new BasicCellEditor().getValue(item, "status", rowcount);
                return status.getName();
            }
        });

        HtmlColumn oid = row.getColumn("oid");
        oid.setTitle(resword.getString("rule_oid"));

        HtmlColumn gender = row.getColumn("subject.charGender");
        gender.setTitle(resword.getString("gender"));

        HtmlColumn secondaryLabel = row.getColumn("secondaryLabel");
        secondaryLabel.setTitle(resword.getString("secondary_label"));

        HtmlColumn actions = row.getColumn("actions");
        actions.setSortable(false);
        actions.setFilterable(false);
        actions.setTitle(resword.getString("rule_actions"));
        actions.getCellRenderer().setCellEditor(new CellEditor() {
            public Object getValue(Object item, String property, int rowcount) {
                String value = "";
                Integer studySubjectId = (Integer) new BasicCellEditor().getValue(item, "id", rowcount);
                if (studySubjectId != null) {
                    StringBuilder url = new StringBuilder();
                    url
                            .append("<a onmouseup=\"javascript:setImage('bt_View1','images/bt_View.gif');\" onmousedown=\"javascript:setImage('bt_View1','images/bt_View_d.gif');\" href=\"ViewStudySubject?id=");
                    url.append(studySubjectId.toString());
                    url
                            .append("\"><img hspace=\"6\" border=\"0\" align=\"left\" title=\"View\" alt=\"View\" src=\"images/bt_View.gif\" name=\"bt_View1\"/></a>");
                    value = url.toString();
                }
                return value;
            }
        });

        return tableFacade.render();
    }

    private String renderAuditUserLoginTableExport(TableFacade tableFacade) {

        tableFacade.setColumnProperties("userName", "loginAttemptDate", "loginStatus");
        // Fix column titles
        Table table = tableFacade.getTable();

        table.setCaption("");
        Row row = table.getRow();

        Column userName = row.getColumn("userName");
        userName.setTitle("User Name");

        Column loginAttemptDate = row.getColumn("loginAttemptDate");
        loginAttemptDate.setTitle("Login Attempt Date");
        loginAttemptDate.getCellRenderer().setCellEditor(new DateCellEditor("yyyy-MM-dd hh:mm:ss"));

        Column loginStatus = row.getColumn("loginStatus");
        loginStatus.setTitle("Login Status");

        return tableFacade.render();
    }

    /**
     * <p>
     * We are manually filtering and sorting the rows here. In addition we are
     * only returning one page of data. To do this we must use the Limit to tell
     * us where the rows start and end. However, to do that we must set the
     * RowSelect object using the maxRows and totalRows to create a valid Limit
     * object.
     * </p>
     * 
     * <p>
     * The idea is to first find the total rows. The total rows can only be
     * figured out after filtering out the data. The sorting does not effect the
     * total row count but is needed to return the correct set of sorted rows.
     * </p>
     * 
     * @param tableFacade
     *            The TableFacade to use.
     */
    protected void setDataAndLimitVariables(TableFacade tableFacade) {
        Limit limit = tableFacade.getLimit();

        FindSubjectsFilter subjectFilter = getSubjectFilter(limit);

        /*
         * Because we are using the State feature (via stateAttr) we can do a
         * check to see if we have a complete limit already. See the State
         * feature for more details
         */
        if (!limit.isComplete()) {
            int totalRows = getStudySubjectDAO().getCountWithFilter(subjectFilter, currentStudy);
            tableFacade.setTotalRows(totalRows); /*
                                                  * Very important to set the
                                                  * totalRow before trying to
                                                  * get the row start and row
                                                  * end variables.
                                                  */
        }

        FindSubjectsSort subjectSort = getSubjectSort(limit);

        int rowStart = limit.getRowSelect().getRowStart();
        int rowEnd = limit.getRowSelect().getRowEnd();
        Collection<StudySubjectBean> items = getStudySubjectDAO().getWithFilterAndSort(currentStudy, subjectFilter, subjectSort, rowStart, rowEnd);
        HashMap<Integer, SubjectBean> subjectCache = new HashMap<Integer, SubjectBean>();
        for (StudySubjectBean studySubjectBean : items) {
            Integer subjectId = studySubjectBean.getSubjectId();
            SubjectBean subjectBean = subjectCache.get(subjectId);
            if (subjectBean == null) {
                subjectBean = (SubjectBean) getSubjectDAO().findByPK(subjectId);
                subjectCache.put(subjectId, subjectBean);

            }
            studySubjectBean.setSubject(subjectBean);
        }
        tableFacade.setItems(items); // Do not forget to set the items back on
        // the tableFacade.
    }

    /**
     * A very custom way to filter the items. The PresidentFilter acts as a
     * command for the Hibernate criteria object. There are probably many ways
     * to do this, but this is the most flexible way I have found. The point is
     * you need to somehow take the Limit information and filter the rows.
     * 
     * @param limit
     *            The Limit to use.
     */
    protected FindSubjectsFilter getSubjectFilter(Limit limit) {
        FindSubjectsFilter auditUserLoginFilter = new FindSubjectsFilter();
        FilterSet filterSet = limit.getFilterSet();
        Collection<Filter> filters = filterSet.getFilters();
        for (Filter filter : filters) {
            String property = filter.getProperty();
            String value = filter.getValue();
            auditUserLoginFilter.addFilter(property, value);
        }

        return auditUserLoginFilter;
    }

    /**
     * A very custom way to sort the items. The PresidentSort acts as a command
     * for the Hibernate criteria object. There are probably many ways to do
     * this, but this is the most flexible way I have found. The point is you
     * need to somehow take the Limit information and sort the rows.
     * 
     * @param limit
     *            The Limit to use.
     */
    protected FindSubjectsSort getSubjectSort(Limit limit) {
        FindSubjectsSort auditUserLoginSort = new FindSubjectsSort();
        SortSet sortSet = limit.getSortSet();
        Collection<Sort> sorts = sortSet.getSorts();
        for (Sort sort : sorts) {
            String property = sort.getProperty();
            String order = sort.getOrder().toParam();
            auditUserLoginSort.addSort(property, order);
        }

        return auditUserLoginSort;
    }

    private class CharFilterMatcher implements FilterMatcher {
        public boolean evaluate(Object itemValue, String filterValue) {
            String item = StringUtils.lowerCase(String.valueOf(itemValue));
            String filter = StringUtils.lowerCase(String.valueOf(filterValue));
            if (StringUtils.contains(item, filter)) {
                return true;
            }

            return false;
        }
    }

    private class StatusDroplistFilterEditor extends DroplistFilterEditor {
        @Override
        protected List<Option> getOptions() {
            List<Option> options = new ArrayList<Option>();
            options.add(new Option(String.valueOf(Status.AVAILABLE.getId()), Status.AVAILABLE.getName()));
            options.add(new Option(String.valueOf(Status.SIGNED.getId()), Status.SIGNED.getName()));
            options.add(new Option(String.valueOf(Status.DELETED.getId()), Status.DELETED.getName()));
            return options;
        }
    }

    public class StatusFilterMatcher implements FilterMatcher {
        public boolean evaluate(Object itemValue, String filterValue) {

            String item = StringUtils.lowerCase(String.valueOf(((Status) itemValue).getId()));
            String filter = StringUtils.lowerCase(String.valueOf(filterValue));

            if (filter.equals(item)) {
                return true;
            }
            return false;
        }
    }

    @Override
    protected String getAdminServlet() {
        return SecureController.ADMIN_SERVLET_CODE;
    }

    public StudySubjectDAO getStudySubjectDAO() {
        studySubjectDAO = this.studySubjectDAO == null ? new StudySubjectDAO(sm.getDataSource()) : studySubjectDAO;
        return studySubjectDAO;
    }

    public SubjectDAO getSubjectDAO() {
        subjectDAO = this.subjectDAO == null ? new SubjectDAO(sm.getDataSource()) : subjectDAO;
        return subjectDAO;
    }

}
