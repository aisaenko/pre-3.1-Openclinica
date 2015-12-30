package org.akaza.openclinica.control.managestudy;

import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupClassBean;
import org.jmesa.core.CoreContext;
import org.jmesa.view.ViewUtils;
import org.jmesa.view.component.Row;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.toolbar.AbstractItem;
import org.jmesa.view.html.toolbar.AbstractItemRenderer;
import org.jmesa.view.html.toolbar.AbstractToolbar;
import org.jmesa.view.html.toolbar.ClearItemRenderer;
import org.jmesa.view.html.toolbar.FilterItemRenderer;
import org.jmesa.view.html.toolbar.MaxRowsItem;
import org.jmesa.view.html.toolbar.ToolbarItem;
import org.jmesa.view.html.toolbar.ToolbarItemRenderer;
import org.jmesa.view.html.toolbar.ToolbarItemType;

import java.util.ArrayList;
import java.util.List;

public class ListEventsForSubjectTableToolbar extends AbstractToolbar {

    private final ArrayList<StudyEventDefinitionBean> studyEventDefinitions;
    private final ArrayList<StudyGroupClassBean> studyGroupClasses;
    private final StudyEventDefinitionBean selectedStudyEventDefinition;

    public ListEventsForSubjectTableToolbar(ArrayList<StudyEventDefinitionBean> studyEventDefinitions, ArrayList<StudyGroupClassBean> studyGroupClasses,
            StudyEventDefinitionBean selectedStudyEventDefinition) {
        super();
        this.studyEventDefinitions = studyEventDefinitions;
        this.studyGroupClasses = studyGroupClasses;
        this.selectedStudyEventDefinition = selectedStudyEventDefinition;

    }

    @SuppressWarnings("unchecked")
    @Override
    public String render() {
        addToolbarItem(ToolbarItemType.FIRST_PAGE_ITEM);
        addToolbarItem(ToolbarItemType.PREV_PAGE_ITEM);
        addToolbarItem(ToolbarItemType.NEXT_PAGE_ITEM);
        addToolbarItem(ToolbarItemType.LAST_PAGE_ITEM);

        addToolbarItem(ToolbarItemType.SEPARATOR);

        MaxRowsItem maxRowsItem = (MaxRowsItem) addToolbarItem(ToolbarItemType.MAX_ROWS_ITEM);
        if (getMaxRowsIncrements() != null) {
            maxRowsItem.setIncrements(getMaxRowsIncrements());
        }

        boolean exportable = ViewUtils.isExportable(getExportTypes());

        if (exportable) {
            addToolbarItem(ToolbarItemType.SEPARATOR);
            addExportToolbarItems(getExportTypes());
        }

        Row row = getTable().getRow();
        List columns = row.getColumns();

        boolean filterable = ViewUtils.isFilterable(columns);

        if (filterable) {
            addToolbarItem(ToolbarItemType.SEPARATOR);
            //addToolbarItem(ToolbarItemType.FILTER_ITEM);
            //addToolbarItem(ToolbarItemType.CLEAR_ITEM);
            addToolbarItem(createFilterItem());
            addToolbarItem(createResetFilterItem());
            addToolbarItem(createCustomItem(new ShowMoreItem()));
        }

        boolean editable = ViewUtils.isEditable(getCoreContext().getWorksheet());

        if (editable) {
            addToolbarItem(ToolbarItemType.SEPARATOR);
            addToolbarItem(ToolbarItemType.SAVE_WORKSHEET_ITEM);
            addToolbarItem(ToolbarItemType.FILTER_WORKSHEET_ITEM);
        }

        addToolbarItem(ToolbarItemType.SEPARATOR);

        addToolbarItem(createCustomItem(new StudyEventDefinitionDropDownItem()));

        return super.render();
    }

    private ToolbarItem createCustomItem(AbstractItem item) {

        ToolbarItemRenderer renderer = new CustomItemRenderer(item, getCoreContext());
        renderer.setOnInvokeAction("onInvokeAction");
        item.setToolbarItemRenderer(renderer);

        return item;
    }

    public ToolbarItem createFilterItem() {

        FilterItem item = new FilterItem();
        item.setCode(ToolbarItemType.FILTER_ITEM.toCode());
        ToolbarItemRenderer renderer = new FilterItemRenderer(item, getCoreContext());
        renderer.setOnInvokeAction("onInvokeAction");
        item.setToolbarItemRenderer(renderer);

        return item;
    }

    public ToolbarItem createResetFilterItem() {

        ResetFilterItem item = new ResetFilterItem();
        item.setCode(ToolbarItemType.CLEAR_ITEM.toCode());
        ToolbarItemRenderer renderer = new ClearItemRenderer(item, getCoreContext());
        renderer.setOnInvokeAction("onInvokeAction");
        item.setToolbarItemRenderer(renderer);

        return item;
    }

    private class FilterItem extends AbstractItem {

        @Override
        public String disabled() {
            return null;
        }

        @Override
        public String enabled() {
            HtmlBuilder html = new HtmlBuilder();
            html.a().href();
            html.quote();
            html.append(getAction());
            html.quote().close();
            html.nbsp().append("Filter").nbsp().aEnd();

            return html.toString();

        }

    }

    private class ResetFilterItem extends AbstractItem {

        @Override
        public String disabled() {
            return null;
        }

        @Override
        public String enabled() {
            HtmlBuilder html = new HtmlBuilder();
            html.a().href();
            html.quote();
            html.append(getAction());
            html.quote().close();
            html.nbsp().append("Reset Filter").nbsp().aEnd();

            return html.toString();

        }

    }

    private class ShowMoreItem extends AbstractItem {

        @Override
        public String disabled() {
            return null;
        }

        @Override
        public String enabled() {
            HtmlBuilder html = new HtmlBuilder();
            html.a().id("showMore").href("javascript:hideCols('listEventsForSubject',[" + getIndexes() + "],true);").close();
            html.div().close().nbsp().append("Show More").nbsp().divEnd().aEnd();
            html.a().id("hide").style("display: none;").href("javascript:hideCols('listEventsForSubject',[" + getIndexes() + "],false);").close();
            html.div().close().nbsp().append("Hide").nbsp().divEnd().aEnd();

            html.script().type("text/javascript").close().append(
                    "$j = jQuery.noConflict(); $j(document).ready(function(){ " + "hideCols('listEventsForSubject',[" + getIndexes() + "],false);});")
                    .scriptEnd();

            return html.toString();
        }

        /**
         * @return Dynamically generate the indexes of studyGroupClasses. It
         *         starts from 4 because there are 4 columns before study group
         *         columns that will require to be hidden.
         * @see ListEventsForSubjectTableFactory#configureColumns(org.jmesa.facade.TableFacade,
         *      java.util.Locale)
         */
        String getIndexes() {
            String result = "1,2";
            for (int i = 0; i < studyGroupClasses.size(); i++) {
                result += "," + (2 + i + 1);
            }
            return result;
        }

    }

    private class StudyEventDefinitionDropDownItem extends AbstractItem {

        @Override
        public String disabled() {
            return null;
        }

        @Override
        public String enabled() {
            String js =
                "var selectedValue = document.getElementById('sedDropDown').options[document.getElementById('sedDropDown').selectedIndex].value;  "
                    + " if (selectedValue != null && selectedValue != 0 ) { " + "window.location='ListEventsForSubjects?module=submit&defId='+selectedValue;}"
                    + " if (selectedValue != null && selectedValue == 0 ) { " + "window.location='ListStudySubjects' } ";
            HtmlBuilder html = new HtmlBuilder();
            html.append("Events : ");
            html.select().id("sedDropDown").onchange(js).close();
            html.option().value("0");
            html.close().append("ALL").optionEnd();
            for (StudyEventDefinitionBean studyEventDefinition : studyEventDefinitions) {
                html.option().value(String.valueOf(studyEventDefinition.getId()));
                if (studyEventDefinition.getId() == selectedStudyEventDefinition.getId()) {
                    html.selected();
                }
                html.close().append(studyEventDefinition.getName()).optionEnd();
            }
            html.selectEnd();
            return html.toString();
        }

    }

    private static class CustomItemRenderer extends AbstractItemRenderer {
        public CustomItemRenderer(ToolbarItem item, CoreContext coreContext) {
            setToolbarItem(item);
            setCoreContext(coreContext);
        }

        public String render() {
            ToolbarItem item = getToolbarItem();
            return item.enabled();
        }
    }

}
