package org.akaza.openclinica.controller.helper.table;

import java.util.List;
import org.jmesa.core.CoreContext;
import org.jmesa.view.ViewUtils;
import org.jmesa.view.component.Row;
import org.jmesa.view.html.HtmlUtils;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.toolbar.*;
import org.jmesa.web.WebContext;

/**
 * @author Bruce Perry, borrowed from Jmesa's Jeff Johnston and cuts and pastes from
 * http://code.google.com/p/jmesa/source/browse/trunk/jmesaWeb/src/org/jmesaweb/controller/CustomToolbar.java
 */
public class SDVToolbar extends AbstractToolbar {

    public SDVToolbar() {
    }


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
            addToolbarItem(ToolbarItemType.FILTER_ITEM);
            addToolbarItem(ToolbarItemType.CLEAR_ITEM);
        }

        boolean editable = ViewUtils.isEditable(getCoreContext().getWorksheet());

        if (editable) {
            addToolbarItem(ToolbarItemType.SEPARATOR);
            addToolbarItem(ToolbarItemType.SAVE_WORKSHEET_ITEM);
            addToolbarItem(ToolbarItemType.FILTER_WORKSHEET_ITEM);
        }

        addToolbarItem(ToolbarItemType.SEPARATOR);

        addToolbarItem(createCustomItem());

        return super.render();
    }

    private ShowColumnsItem createCustomItem() {
        ShowColumnsItem item = new ShowColumnsItem();

        ToolbarItemRenderer renderer = new CustomItemRenderer(item, getCoreContext());
        renderer.setOnInvokeAction("onInvokeAction");
        item.setToolbarItemRenderer(renderer);

        return item;
    }

    private class ShowColumnsItem extends AbstractItem {

        @Override
        public String disabled() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String enabled() {
            String javaScript =
                "if(this.innerHTML.indexOf('Show') == -1){hideCols('sdv',[1,2,5,6,9,10]);} else {hideCols('sdv',[1,2,5,6,9,10],true);} toggleName(this);";
            HtmlBuilder html = new HtmlBuilder();
            html.a().href().quote().append("javascript:void(0)").quote();
            html.onclick(javaScript).close();
            html.append("Show");
            html.aEnd();
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
