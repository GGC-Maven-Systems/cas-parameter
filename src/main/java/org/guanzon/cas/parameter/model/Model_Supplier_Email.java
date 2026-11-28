package org.guanzon.cas.parameter.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import org.guanzon.appdriver.agent.services.Model;
import org.guanzon.appdriver.agent.services.ReferenceCache;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.constant.RecordStatus;
import org.json.simple.JSONObject;

public class Model_Supplier_Email extends Model {
    private Model_Industry poIndustry;
    private Model_Category poCategory;

    @Override
    public void initialize() {
        try {
            poEntity = MiscUtil.xml2ResultSet(System.getProperty("sys.default.path.metadata") + XML, getTable());

            poEntity.last();
            poEntity.moveToInsertRow();

            MiscUtil.initRowSet(poEntity);

            //assign default values
            poEntity.updateString("cRecdStat", RecordStatus.ACTIVE);
            //end - assign default values

            poEntity.insertRow();
            poEntity.moveToCurrentRow();

            poEntity.absolute(1);

            ID = poEntity.getMetaData().getColumnLabel(1);

            //poIndustry/poCategory are built lazily on first access, see Industry()/Category()

            pnEditMode = EditMode.UNKNOWN;
        } catch (SQLException e) {
            logwrapr.severe(e.getMessage());
            System.exit(1);
        }
    }

    public JSONObject setClientId(String clientId) {
        return setValue("sClientID", clientId);
    }

    public String getClientId() {
        return (String) getValue("sClientID");
    }

    public JSONObject setIndustryCode(String industryCode) {
        return setValue("sIndstCdx", industryCode);
    }

    public String getIndustryCode() {
        return (String) getValue("sIndstCdx");
    }

    public JSONObject setCategoryCode(String categoryCode) {
        return setValue("sCategrCd", categoryCode);
    }

    public String getCategoryCode() {
        return (String) getValue("sCategrCd");
    }

    public JSONObject setEmailAddress(String emailAddress) {
        return setValue("sEMailAdd", emailAddress);
    }

    public String getEmailAddress() {
        return (String) getValue("sEMailAdd");
    }

    public JSONObject setEntryNo(int entryNo) {
        return setValue("nEntryNox", entryNo);
    }

    public int getEntryNo() {
        Object loValue = getValue("nEntryNox");
        return loValue == null ? 0 : ((Number) loValue).intValue();
    }

    public JSONObject setContactPerson(String contactPerson) {
        return setValue("sContactP", contactPerson);
    }

    public String getContactPerson() {
        return (String) getValue("sContactP");
    }

    public JSONObject setRecipientType(String recipientType) {
        return setValue("cRecpient", recipientType);
    }

    public String getRecipientType() {
        return (String) getValue("cRecpient");
    }

    public JSONObject setRecordStatus(String recordStatus) {
        return setValue("cRecdStat", recordStatus);
    }

    public String getRecordStatus() {
        return (String) getValue("cRecdStat");
    }

    public JSONObject setModifyingId(String modifyingId) {
        return setValue("sModified", modifyingId);
    }

    public String getModifyingId() {
        return (String) getValue("sModified");
    }

    public JSONObject setModifiedDate(Date modifiedDate) {
        return setValue("dModified", modifiedDate);
    }

    public Date getModifiedDate() {
        return (Date) getValue("dModified");
    }

    /**
     * Loads one email address of a supplier. openRecord(id) filters by the
     * first column only (sClientID), and a supplier can have many addresses.
     */
    public JSONObject openRecordByEmail(String clientId, String emailAddress) throws SQLException, GuanzonException {
        initialize();

        String lsSQL = MiscUtil.addCondition("SELECT * FROM " + getTable(),
                "sClientID = " + SQLUtil.toSQL(clientId)
                        + " AND sEMailAdd = " + SQLUtil.toSQL(emailAddress));

        JSONObject loJSON = new JSONObject();
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        if (loRS == null) {
            loJSON.put("result", "error");
            loJSON.put("message", "Query execution failed.");
            return loJSON;
        }

        try {
            if (!loRS.next()) {
                loJSON.put("result", "error");
                loJSON.put("message", "No record to load.");
                return loJSON;
            }
            for (int lnCtr = 1; lnCtr <= loRS.getMetaData().getColumnCount(); lnCtr++) {
                poEntity.updateObject(loRS.getMetaData().getColumnLabel(lnCtr), loRS.getObject(lnCtr));
            }
            poEntity.updateRow();
        } finally {
            MiscUtil.close(loRS);
        }

        pnEditMode = EditMode.READY;
        loJSON.put("result", "success");
        loJSON.put("message", "Record loaded successfully.");
        return loJSON;
    }

    public Model_Industry Industry() throws SQLException, GuanzonException {
        if (poIndustry == null) {
            poIndustry = new Model_Industry();
            poIndustry.setApplicationDriver(poGRider);
            poIndustry.setXML("Model_Industry");
            poIndustry.setTableName("Industry");
            poIndustry.initialize();
        }

        String industryId = (String) (getValue("sIndstCdx") == null ? "" : getValue("sIndstCdx"));

        if (!"".equals(industryId)) {
            if (poIndustry.getEditMode() == EditMode.READY
                    && poIndustry.getIndustryId().equals(industryId)) {
                return poIndustry;
            }
            if (ReferenceCache.tryLoad("Industry", industryId, poIndustry)) {
                return poIndustry;
            }

            poJSON = poIndustry.openRecord(industryId);

            if ("success".equals((String) poJSON.get("result"))) {
                ReferenceCache.store("Industry", industryId, poIndustry);
                return poIndustry;
            }
        }

        poIndustry.initialize();
        return poIndustry;
    }

    public Model_Category Category() throws SQLException, GuanzonException {
        if (poCategory == null) {
            poCategory = new Model_Category();
            poCategory.setApplicationDriver(poGRider);
            poCategory.setXML("Model_Category");
            poCategory.setTableName("Category");
            poCategory.initialize();
        }

        String categoryId = (String) (getValue("sCategrCd") == null ? "" : getValue("sCategrCd"));

        if (!"".equals(categoryId)) {
            if (poCategory.getEditMode() == EditMode.READY
                    && poCategory.getCategoryId().equals(categoryId)) {
                return poCategory;
            }
            if (ReferenceCache.tryLoad("Category", categoryId, poCategory)) {
                return poCategory;
            }

            poJSON = poCategory.openRecord(categoryId);

            if ("success".equals((String) poJSON.get("result"))) {
                ReferenceCache.store("Category", categoryId, poCategory);
                return poCategory;
            }
        }

        poCategory.initialize();
        return poCategory;
    }

    @Override
    public String getNextCode() {
        //sClientID comes from the supplier; it is never generated here
        return "";
    }
}