package org.guanzon.cas.parameter.model;

import java.sql.SQLException;
import java.util.Date;
import org.guanzon.appdriver.agent.services.Model;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.agent.services.ReferenceCache;
import org.guanzon.appdriver.constant.RecordStatus;
import org.json.simple.JSONObject;

public class Model_Category_Level2 extends Model {
    private Model_Category poCategory;
    private Model_Inv_Type poInvType;
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
            
            //poCategory/poInvType are intentionally NOT constructed here - see Category()/InvType()
            //below, which build them lazily on first access so opening this record never touches
            //the Category/Inv_Type tables.

            pnEditMode = EditMode.UNKNOWN;
        } catch (SQLException e) {
            logwrapr.severe(e.getMessage());
            System.exit(1);
        }
    }
    
    public Model_Category Category() throws SQLException, GuanzonException{
        if (poCategory == null) {
            poCategory = new Model_Category();
            poCategory.setApplicationDriver(poGRider);
            poCategory.setXML("Model_Category");
            poCategory.setTableName("Category");
            poCategory.initialize();
        }

        String categoryId = (String) (getValue("sMainCatx") == null ? "" : getValue("sMainCatx"));

        if (!"".equals(categoryId)){
            if (poCategory.getEditMode() == EditMode.READY &&
                poCategory.getCategoryId().equals(categoryId))
                return poCategory;
            else{
                if (ReferenceCache.tryLoad("Category", categoryId, poCategory)) {
                    return poCategory;
                }

                poJSON = poCategory.openRecord(categoryId);

                if ("success".equals((String) poJSON.get("result"))){
                    ReferenceCache.store("Category", categoryId, poCategory);
                    return poCategory;
                }
                else {
                    poCategory.initialize();
                    return poCategory;
                }
            }
        } else {
            poCategory.initialize();
            return poCategory;
        }
    }
    public Model_Inv_Type InvType() throws SQLException, GuanzonException{
        if (poInvType == null) {
            poInvType = new Model_Inv_Type();
            poInvType.setApplicationDriver(poGRider);
            poInvType.setXML("Model_Inv_Type");
            poInvType.setTableName("Inv_Type");
            poInvType.initialize();
        }

        String invTypeId = (String) (getValue("sInvTypCd") == null ? "" : getValue("sInvTypCd"));

        if (!"".equals(invTypeId)){
            if (poInvType.getEditMode() == EditMode.READY &&
                poInvType.getInventoryTypeId().equals(invTypeId))
                return poInvType;
            else{
                if (ReferenceCache.tryLoad("Inv_Type", invTypeId, poInvType)) {
                    return poInvType;
                }

                poJSON = poInvType.openRecord(invTypeId);

                if ("success".equals((String) poJSON.get("result"))){
                    ReferenceCache.store("Inv_Type", invTypeId, poInvType);
                    return poInvType;
                }
                else {
                    poInvType.initialize();
                    return poInvType;
                }
            }
        } else {
            poInvType.initialize();
            return poInvType;
        }
    }

    public JSONObject setCategoryId(String categoryCode) {
        return setValue("sCategrCd", categoryCode);
    }

    public String getCategoryId() {
        return (String) getValue("sCategrCd");
    }

    public JSONObject setDescription(String description) {
        return setValue("sDescript", description);
    }

    public String getDescription() {
        return (String) getValue("sDescript");
    }
    
    public JSONObject setInventoryTypeCode(String inventoryTypeCode) {
        return setValue("sInvTypCd", inventoryTypeCode);
    }

    public String getInventoryTypeCode() {
        return (String) getValue("sInvTypCd");
    }
    
    public JSONObject setMainCategory(String mainCategory) {
        return setValue("sMainCatx", mainCategory);
    }

    public String getMainCategory() {
        return (String) getValue("sMainCatx");
    }
    
    public JSONObject setClassify(String classify) {
        return setValue("cClassify", classify);
    }

    public String getClassify() {
        return (String) getValue("cClassify");
    }
    
    public JSONObject setRecordStatus(String recordStatus){
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
    
    @Override
    public String getNextCode(){
        return MiscUtil.getNextCode(getTable(), ID, false, poGRider.getGConnection().getConnection(), poGRider.getBranchCode()); 
    }
}
