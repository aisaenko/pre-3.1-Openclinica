package org.akaza.openclinica.dao.submit;

import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.submit.ItemGroupMetadataBean;
import org.akaza.openclinica.dao.core.EntityDAO;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.core.TypeNames;
import org.akaza.openclinica.exception.OpenClinicaException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

/**
 * Created by IntelliJ IDEA. User: bruceperry Date: May 10, 2007
 */
public class ItemGroupMetadataDAO extends EntityDAO {
    public ItemGroupMetadataDAO(DataSource ds) {
        super(ds);
        // this.getCurrentPKName="findCurrentPKValue";
        this.getNextPKName = "getNextPK";
    }

    @Override
    protected void setDigesterName() {
        digesterName = SQLFactory.getInstance().DAO_ITEM_GROUP_METADATA;
    }

    public void setTypesExpected() {
        // item_group_metadata_id serial NOT NULL,
        // item_group_id numeric NOT NULL,
        // header varchar(255),
        // subheader varchar(255),
        // layout varchar(100),
        // repeat_number numeric,
        // repeat_max numeric,
        // repeat_array varchar(255),
        // row_start_number numeric,
        // crf_version_id numeric NOT NULL,
        // item_id numeric NOT NULL,
        // ordinal numeric NOT NULL,
        // borders numeric,
        this.unsetTypeExpected();
        this.setTypeExpected(1, TypeNames.INT);
        this.setTypeExpected(2, TypeNames.INT);
        this.setTypeExpected(3, TypeNames.STRING);
        this.setTypeExpected(4, TypeNames.STRING);
        this.setTypeExpected(5, TypeNames.STRING);
        this.setTypeExpected(6, TypeNames.INT);
        this.setTypeExpected(7, TypeNames.INT);
        this.setTypeExpected(8, TypeNames.STRING);
        this.setTypeExpected(9, TypeNames.INT);
        this.setTypeExpected(10, TypeNames.INT);
        this.setTypeExpected(11, TypeNames.INT);
        this.setTypeExpected(12, TypeNames.INT);
        this.setTypeExpected(13, TypeNames.INT);

    }

    public Object getEntityFromHashMap(HashMap hm) {
        ItemGroupMetadataBean meta = new ItemGroupMetadataBean();
        meta.setId((Integer) hm.get("item_group_metadata_id"));
        meta.setItemGroupId((Integer) hm.get("item_group_id"));
        meta.setHeader((String) hm.get("header"));
        meta.setSubheader((String) hm.get("subheader"));
        meta.setLayout((String) hm.get("layout"));
        meta.setRepeatNum((Integer) hm.get("repeat_number"));
        meta.setRepeatMax((Integer) hm.get("repeat_max"));
        meta.setRepeatArray((String) hm.get("repeat_array"));
        meta.setRowStartNumber((Integer) hm.get("row_start_number"));
        meta.setCrfVersionId((Integer) hm.get("crf_version_id"));
        meta.setItemId((Integer) hm.get("item_id"));
        meta.setOrdinal((Integer) hm.get("ordinal"));
        meta.setBorders((Integer) hm.get("borders"));
        return meta;
    }

    public Collection findAll(String strOrderByColumn, boolean blnAscendingSort, String strSearchPhrase) throws OpenClinicaException {
        return new ArrayList();
    }

    public Collection findAll() throws OpenClinicaException {
        return new ArrayList();
    }

    public EntityBean findByPK(int id) throws OpenClinicaException {
        return new ItemGroupMetadataBean(); // To change body of implemented
        // methods use File | Settings |
        // File Templates.;
    }

    public EntityBean create(EntityBean eb) throws OpenClinicaException {
        // INSERT INTO item_group_metadata (item_group_id,
        // header, subheader,layout,repeat_number,repeat_max,
        // repeat_array, row_start_number,crf_version_id,
        // item_id, ordinal,borders)
        // VALUES (?,?,?,?,?,?,?,?,?,?,?,?)
        ItemGroupMetadataBean igMetaBean = (ItemGroupMetadataBean) eb;
        HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
        int id = getNextPK();
        variables.put(1, id);
        variables.put(2, igMetaBean.getItemGroupId());
        variables.put(3, igMetaBean.getHeader());
        variables.put(4, igMetaBean.getSubheader());
        variables.put(5, igMetaBean.getLayout());
        variables.put(6, igMetaBean.getRepeatNum());
        variables.put(7, igMetaBean.getRepeatMax());
        variables.put(8, igMetaBean.getRepeatArray());
        variables.put(9, igMetaBean.getRowStartNumber());
        variables.put(10, igMetaBean.getCrfVersionId());
        variables.put(11, igMetaBean.getItemId());
        variables.put(12, igMetaBean.getOrdinal());
        variables.put(13, igMetaBean.getBorders());

        this.execute(digester.getQuery("create"), variables);
        if (isQuerySuccessful()) {
            eb.setId(id);
        }
        return eb;

    }

    public List<ItemGroupMetadataBean> findMetaByGroupAndSection(int itemGroupId, int sectionId) {
        this.setTypesExpected();
        HashMap<Integer, Integer> variables = new HashMap<Integer, Integer>();
        variables.put(1, itemGroupId);
        variables.put(2, sectionId);
        List listofMaps = this.select(digester.getQuery("findMetaByGroupAndSection"), variables);

        List<ItemGroupMetadataBean> beanList = new ArrayList<ItemGroupMetadataBean>();
        ItemGroupMetadataBean bean;
        for (Object map : listofMaps) {
            bean = (ItemGroupMetadataBean) this.getEntityFromHashMap((HashMap) map);
            beanList.add(bean);
        }
        return beanList;
    }

    public EntityBean update(EntityBean eb) throws OpenClinicaException {
        return new ItemGroupMetadataBean(); // To change body of implemented
        // methods use File | Settings |
        // File Templates.
    }

    public Collection findAllByPermission(Object objCurrentUser, int intActionType, String strOrderByColumn, boolean blnAscendingSort, String strSearchPhrase)
            throws OpenClinicaException {
        return new ArrayList(); // To change body of implemented methods use
        // File | Settings | File Templates.
    }

    public Collection findAllByPermission(Object objCurrentUser, int intActionType) throws OpenClinicaException {
        return new ArrayList(); // To change body of implemented methods use
        // File | Settings | File Templates.
    }

    // YW 08-22-2007
    /**
     *
     * @param crfVersionId
     * @return
     */
    public boolean versionIncluded(int crfVersionId) {
        this.unsetTypeExpected();
        this.setTypeExpected(1, TypeNames.INT);

        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(crfVersionId));

        ArrayList al = this.select(digester.getQuery("findThisCrfVersionId"), variables);

        if (al.size() > 0) {
            HashMap h = (HashMap) al.get(0);
            if (((Integer) h.get("crf_version_id")).intValue() == crfVersionId) {
                return true;
            }
        }

        return false;
    }

}
