package org.akaza.openclinica.control.managestudy;

import org.jmesa.core.CoreContext;
import org.jmesa.view.ViewUtils;
import org.jmesa.view.component.Row;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.toolbar.AbstractItem;
import org.jmesa.view.html.toolbar.AbstractItemRenderer;
import org.jmesa.view.html.toolbar.AbstractToolbar;
import org.jmesa.view.html.toolbar.MaxRowsItem;
import org.jmesa.view.html.toolbar.ToolbarItem;
import org.jmesa.view.html.toolbar.ToolbarItemRenderer;
import org.jmesa.view.html.toolbar.ToolbarItemType;

import java.util.List;

public class FindSubjectsToolbar extends AbstractToolbar {

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

    private ToolbarItem createCustomItem() {
        /*
         * ImageItemImpl item = new ImageItemImpl();
         * item.setCode("custom-item"); item.setTooltip("Add New Subject");
         * item.setImage(getImage("custom.png", getWebContext(),
         * getCoreContext())); item.item.setAlt("custom");
         */

        LinkItem item = new LinkItem();

        ToolbarItemRenderer renderer = new CustomItemRenderer(item, getCoreContext());
        renderer.setOnInvokeAction("onInvokeAction");
        item.setToolbarItemRenderer(renderer);

        return item;
    }

    /*
     * private static String getImage(String image, WebContext webContext,
     * CoreContext coreContext) { String imagesPath =
     * HtmlUtils.imagesPath(webContext, coreContext); return imagesPath + image;
     * }
     */

    private class LinkItem extends AbstractItem {
        @Override
        public String getAction() {
            return "AddNewSubject";
        }

        @Override
        public String getStyle() {
            // TODO Auto-generated method stub
            return super.getStyle();
        }

        @Override
        public String enabled() {
            HtmlBuilder html = new HtmlBuilder();
            html.a().href();
            html.quote();
            html.append(getAction());
            html.quote().close();
            html.append("Add New Subject").bold();
            html.aEnd();
            return html.toString();
        }

        @Override
        public String disabled() {
            // TODO Auto-generated method stub
            return null;
        }

    }

    private static class CustomItemRenderer extends AbstractItemRenderer {
        public CustomItemRenderer(ToolbarItem item, CoreContext coreContext) {
            setToolbarItem(item);
            setCoreContext(coreContext);
        }

        public String render() {
            ToolbarItem item = getToolbarItem();
            // StringBuilder action = new StringBuilder("javascript:");
            // action.append("alert('Hello World')");
            // item.setAction(action.toString());
            return item.enabled();
        }
    }

}
