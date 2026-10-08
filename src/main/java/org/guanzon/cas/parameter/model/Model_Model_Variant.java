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

public class Model_Model_Variant extends Model {
    private Model_Model poModel;
    private Model_Color poColor;
    
    @Override
    public void initialize() {
        try {
            poEntity = MiscUtil.xml2ResultSet(System.getProperty("sys.default.path.metadata") + XML, getTable());
            
            poEntity.last();
            poEntity.moveToInsertRow();

            MiscUtil.initRowSet(poEntity);
            
            //assign default values
            poEntity.updateObject("nSelPrice", 0);
            poEntity.updateObject("nYearMdlx", 0);
            poEntity.updateObject("cEndOfLfe", "0");
            poEntity.updateObject("cRecdStat", RecordStatus.ACTIVE);
            //end - assign default values

            poEntity.insertRow();
            poEntity.moveToCurrentRow();

            poEntity.absolute(1);

            ID = poEntity.getMetaData().getColumnLabel(1);
            
            //poModel/poColor are intentionally NOT constructed here - see Model()/Color() below,
            //which build them lazily on first access so opening this record never touches the
            //Model/Color tables.
            
            pnEditMode = EditMode.UNKNOWN;
        } catch (SQLException e) {
            logwrapr.severe(e.getMessage());
            System.exit(1);
        }
    }

    public JSONObject setVariantId(String seriesId) {
        return setValue("sVrntIDxx", seriesId);
    }

    public String getVariantId() {
        return (String) getValue("sVrntIDxx");
    }


    public JSONObject setDescription(String description) {
        return setValue("sDescript", description);
    }

    public String getDescription() {
        return (String) getValue("sDescript");
    }
    
    public JSONObject setSellingPrice(double price) {
        return setValue("nSelPrice", price);
    }

    public double getSellingPrice() {
        return Double.valueOf(getValue("nSelPrice").toString());
    }

    public JSONObject setYearModel(int yearModel) {
        return setValue("nYearMdlx", yearModel);
    }

    public int getYearModel() {
        return (int) getValue("nYearMdlx");
    }
    
    public JSONObject setPayload(String payload) {
        return setValue("sPayloadx", payload);
    }

    public String getPayload() {
        return (String) getValue("sPayloadx");
    }
    
    public JSONObject setModelId(String modelId) {
        return setValue("sModelIDx", modelId);
    }

    public String getModelId() {
        return (String) getValue("sModelIDx");
    }
    
    public JSONObject setColorId(String colorId) {
        return setValue("sColorIDx", colorId);
    }

    public String getColorId() {
        return (String) getValue("sColorIDx");
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
    
    public Model_Model Model() throws SQLException, GuanzonException{
        if (poModel == null) {
            poModel = new Model_Model();
            poModel.setApplicationDriver(poGRider);
            poModel.setXML("Model_Model");
            poModel.setTableName("Model");
            poModel.initialize();
        }

        String modelId = (String) (getValue("sModelIDx") == null ? "" : getValue("sModelIDx"));

        if (!"".equals(modelId)){
            if (poModel.getEditMode() == EditMode.READY &&
                poModel.getModelId().equals(modelId))
                return poModel;
            else{
                if (ReferenceCache.tryLoad("Model", modelId, poModel)) {
                    return poModel;
                }

                poJSON = poModel.openRecord(modelId);

                if ("success".equals((String) poJSON.get("result"))){
                    ReferenceCache.store("Model", modelId, poModel);
                    return poModel;
                }
                else {
                    poModel.initialize();
                    return poModel;
                }
            }
        } else {
            poModel.initialize();
            return poModel;
        }
    }

    //NOTE: pre-existing bug kept as-is (not introduced by this change, flagged separately) -
    //the failure branch below re-initializes poModel instead of poColor.
    public Model_Color Color() throws SQLException, GuanzonException{
        if (poColor == null) {
            poColor = new Model_Color();
            poColor.setApplicationDriver(poGRider);
            poColor.setXML("Model_Color");
            poColor.setTableName("Color");
            poColor.initialize();
        }

        String colorId = (String) (getValue("sColorIDx") == null ? "" : getValue("sColorIDx"));

        if (!"".equals(colorId)){
            if (poColor.getEditMode() == EditMode.READY &&
                poColor.getColorId().equals(colorId))
                return poColor;
            else{
                if (ReferenceCache.tryLoad("Color", colorId, poColor)) {
                    return poColor;
                }

                poJSON = poColor.openRecord(colorId);

                if ("success".equals((String) poJSON.get("result"))){
                    ReferenceCache.store("Color", colorId, poColor);
                    return poColor;
                }
                else {
                    poModel.initialize();
                    return poColor;
                }
            }
        } else {
            poColor.initialize();
            return poColor;
        }
    }
    
    @Override
    public String getNextCode() {
        return MiscUtil.getNextCode(getTable(), ID, false, poGRider.getGConnection().getConnection(), "");
    }
}
