package org.guanzon.cas.parameter.model;

import java.sql.SQLException;
import java.util.Date;
import org.guanzon.appdriver.agent.services.Model;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.constant.Logical;
import org.guanzon.appdriver.agent.services.ReferenceCache;
import org.guanzon.appdriver.constant.RecordStatus;
import org.json.simple.JSONObject;

public class Model_Model extends Model {
    private Model_Brand poBrand;
    
    @Override
    public void initialize() {
        try {
            poEntity = MiscUtil.xml2ResultSet(System.getProperty("sys.default.path.metadata") + XML, getTable());
            
            poEntity.last();
            poEntity.moveToInsertRow();

            MiscUtil.initRowSet(poEntity);
            
            //assign default values
            
            poEntity.updateObject("nMfgYearx", 0);
            poEntity.updateString("cEndOfLfe", Logical.NO);
            poEntity.updateString("cRecdStat", RecordStatus.ACTIVE);
            //end - assign default values

            poEntity.insertRow();
            poEntity.moveToCurrentRow();

            poEntity.absolute(1);

            ID = poEntity.getMetaData().getColumnLabel(1);
            
            //poBrand is intentionally NOT constructed here - see Brand() below, which builds it
            //lazily on first access so opening this record never touches the Brand table.
            
            pnEditMode = EditMode.UNKNOWN;
        } catch (SQLException e) {
            logwrapr.severe(e.getMessage());
            System.exit(1);
        }
    }
    
    public Model_Brand Brand() throws SQLException, GuanzonException{
        if (poBrand == null) {
            poBrand = new Model_Brand();
            poBrand.setApplicationDriver(poGRider);
            poBrand.setXML("Model_Brand");
            poBrand.setTableName("Brand");
            poBrand.initialize();
        }
        
        String brandId = (String) (getValue("sBrandIDx") == null ? "" : getValue("sBrandIDx"));

        if (!"".equals(brandId)){
            if (poBrand.getEditMode() == EditMode.READY &&
                poBrand.getBrandId().equals(brandId))
                return poBrand;
            else{
                if (ReferenceCache.tryLoad("Brand", brandId, poBrand)) {
                    return poBrand;
                }

                poJSON = poBrand.openRecord(brandId);

                if ("success".equals((String) poJSON.get("result"))){
                    ReferenceCache.store("Brand", brandId, poBrand);
                    return poBrand;
                }
                else {
                    poBrand.initialize();
                    return poBrand;
                }
            }
        } else {
            poBrand.initialize();
            return poBrand;
        }
    }
    
    public JSONObject setModelId(String modelId) {
        return setValue("sModelIDx", modelId);
    }

    public String getModelId() {
        return (String) getValue("sModelIDx");
    }

    public JSONObject setModelCode(String modelCode) {
        return setValue("sModelCde", modelCode);
    }

    public String getModelCode() {
        return (String) getValue("sModelCde");
    }

    public JSONObject setDescription(String description) {
        return setValue("sDescript", description);
    }

    public String getDescription() {
        return (String) getValue("sDescript");
    }

    public JSONObject setManufactureYear(int manufactureYear) {
        return setValue("nMfgYearx", manufactureYear);
    }

    public int getManufactureYear() {
        return (int) getValue("nMfgYearx");
    }
    
    public JSONObject setMainModelId(String modelId) {
        return setValue("sMainModl", modelId);
    }

    public String getMainModelId() {
        return (String) getValue("sMainModl");
    }
    
    public JSONObject setBrandId(String brandId) {
        return setValue("sBrandIDx", brandId);
    }

    public String getBrandId() {
        return (String) getValue("sBrandIDx");
    }
    
    public JSONObject setIndustryCode(String industryCode) {
        return setValue("sIndstCdx", industryCode);
    }

    public String getIndustryCode() {
        return (String) getValue("sIndstCdx");
    }

    public JSONObject setEndOfLife(String endOfLife) {
        return setValue("cEndOfLfe", endOfLife);
    }

    public String getEndOfLife() {
        return (String) getValue("cEndOfLfe");
    }    
    
    public JSONObject setRecordStatus(String recordStatus) {
        return setValue("cRecdStat", recordStatus);
    }

    public String getRecordStatus() {
        return (String) getValue("cRecdStat");
    } 
    
    public JSONObject setModifyingId(String modifyingId){
        return setValue("sModified", modifyingId);
    }
    
    public String getModifyingId(){
        return (String) getValue("sModified");
    }
    
    public JSONObject setModifiedDate(Date modifiedDate){
        return setValue("dModified", modifiedDate);
    }
    
    public Date getModifiedDate(){
        return (Date) getValue("dModified");
    }
    
    @Override
    public String getNextCode() {
        return MiscUtil.getNextCode(getTable(), ID, true, poGRider.getGConnection().getConnection(), poGRider.getBranchCode());
    }
}
