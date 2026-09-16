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

public class Model_Category extends Model {
    private Model_Industry poIndustry;
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
            
            //poIndustry/poInvType are intentionally NOT constructed here - see Industry()/Inv_Type()
            //below, which build them lazily on first access so opening this record never touches
            //the Industry/Inv_Type tables.

            pnEditMode = EditMode.UNKNOWN;
        } catch (SQLException e) {
            logwrapr.severe(e.getMessage());
            System.exit(1);
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
    
    public JSONObject setIndustryCode(String industryCode) {
        return setValue("sIndstCdx", industryCode);
    }

    public String getIndustryCode() {
        return (String) getValue("sIndstCdx");
    }
    
    public JSONObject setInventoryTypeCode(String inventoryTypeCode) {
        return setValue("sInvTypCd", inventoryTypeCode);
    }

    public String getInventoryTypeCode() {
        return (String) getValue("sInvTypCd");
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
    
    public Model_Industry Industry() throws SQLException, GuanzonException{
        if (poIndustry == null) {
            poIndustry = new Model_Industry();
            poIndustry.setApplicationDriver(poGRider);
            poIndustry.setXML("Model_Industry");
            poIndustry.setTableName("Industry");
            poIndustry.initialize();
        }

        String industryId = (String) (getValue("sIndstCdx") == null ? "" : getValue("sIndstCdx"));

        if (!"".equals(industryId)){
            if (poIndustry.getEditMode() == EditMode.READY &&
                poIndustry.getIndustryId().equals(industryId))
                return poIndustry;
            else{
                if (ReferenceCache.tryLoad("Industry", industryId, poIndustry)) {
                    return poIndustry;
                }

                poJSON = poIndustry.openRecord(industryId);

                if ("success".equals((String) poJSON.get("result"))){
                    ReferenceCache.store("Industry", industryId, poIndustry);
                    return poIndustry;
                }
                else {
                    poIndustry.initialize();
                    return poIndustry;
                }
            }
        } else {
            poIndustry.initialize();
            return poIndustry;
        }
    }

    //NOTE: pre-existing bug kept as-is (not introduced by this change, flagged separately) -
    //this checks sIndstCdx (Industry's column) for emptiness instead of sInvTypCd, and its
    //failure branch re-initializes poIndustry instead of poInvType.
    public Model_Inv_Type Inv_Type() throws SQLException, GuanzonException{
        if (poInvType == null) {
            poInvType = new Model_Inv_Type();
            poInvType.setApplicationDriver(poGRider);
            poInvType.setXML("Model_Inv_Type");
            poInvType.setTableName("Inv_Type");
            poInvType.initialize();
        }

        if (!"".equals((String) getValue("sIndstCdx"))){
            if (poInvType.getEditMode() == EditMode.READY &&
                poInvType.getInventoryTypeId().equals((String) getValue("sInvTypCd")))
                return poInvType;
            else{
                String invTypeId = (String) (getValue("sInvTypCd") == null ? "" : getValue("sInvTypCd"));

                if (ReferenceCache.tryLoad("Inv_Type", invTypeId, poInvType)) {
                    return poInvType;
                }

                poJSON = poInvType.openRecord(invTypeId);

                if ("success".equals((String) poJSON.get("result"))){
                    ReferenceCache.store("Inv_Type", invTypeId, poInvType);
                    return poInvType;
                }
                else {
                    poIndustry.initialize();
                    return poInvType;
                }
            }
        } else {
            poInvType.initialize();
            return poInvType;
        }
    }
    
    @Override
    public String getNextCode(){
        return MiscUtil.getNextCode(getTable(), ID, false, poGRider.getGConnection().getConnection(), poGRider.getBranchCode()); 
    }
}
