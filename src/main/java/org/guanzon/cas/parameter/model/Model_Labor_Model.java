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

public class Model_Labor_Model extends Model {
        Model_Model poModel;
        Model_Labor poLabor;
    @Override
    public void initialize() {
        try {
            poEntity = MiscUtil.xml2ResultSet(System.getProperty("sys.default.path.metadata") + XML, getTable());

            poEntity.last();
            poEntity.moveToInsertRow();

            MiscUtil.initRowSet(poEntity);

            //assign default values
            
            poEntity.updateObject("nAmountxx", 0.00);
            poEntity.updateString("cRecdStat", RecordStatus.ACTIVE);
            //end - assign default values

            poEntity.insertRow();
            poEntity.moveToCurrentRow();

            poEntity.absolute(1);

            ID = "sLaborIDx";
            ID2 = "sModelIDx";

            //poModel/poLabor are intentionally NOT constructed here - see Model()/Labor() below,
            //which build them lazily on first access so opening this record never touches the
            //Model/Labor tables.
            pnEditMode = EditMode.UNKNOWN;
        } catch (SQLException e) {
            logwrapr.severe(e.getMessage());
            System.exit(1);
        }
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

        if (!"".equals(modelId)) {
            if (poModel.getEditMode() == EditMode.READY
                    && poModel.getModelId().equals(modelId)) {
                return poModel;
            } else {
                if (ReferenceCache.tryLoad("Model", modelId, poModel)) {
                    return poModel;
                }

                poJSON = poModel.openRecord(modelId);

                if ("success".equals((String) poJSON.get("result"))) {
                    ReferenceCache.store("Model", modelId, poModel);
                    return poModel;
                } else {
                    poModel.initialize();
                    return poModel;
                }
            }
        } else {
            poModel.initialize();
            return poModel;
        }
    }

    public Model_Labor Labor() throws SQLException, GuanzonException{
        if (poLabor == null) {
            poLabor = new Model_Labor();
            poLabor.setApplicationDriver(poGRider);
            poLabor.setXML("Model_Labor");
            poLabor.setTableName("Labor");
            poLabor.initialize();
        }
        
        String laborId = (String) (getValue("sLaborIDx") == null ? "" : getValue("sLaborIDx"));

        if (!"".equals(laborId)) {
            if (poLabor.getEditMode() == EditMode.READY
                    && poLabor.getLaborId().equals(laborId)) {
                return poLabor;
            } else {
                if (ReferenceCache.tryLoad("Labor", laborId, poLabor)) {
                    return poLabor;
                }

                poJSON = poLabor.openRecord(laborId);

                if ("success".equals((String) poJSON.get("result"))) {
                    ReferenceCache.store("Labor", laborId, poLabor);
                    return poLabor;
                } else {
                    poLabor.initialize();
                    return poLabor;
                }
            }
        } else {
            poLabor.initialize();
            return poLabor;
        }
    }

    public JSONObject setLaborId(String laborId) {
        return setValue("sLaborIDx", laborId);
    }

    public String getLaborId() {
        return (String) getValue("sLaborIDx");
    }

    public JSONObject setModelId(String modelId) {
        return setValue("sModelIDx", modelId);
    }

    public String getModelId() {
        return (String) getValue("sModelIDx");
    }
    
    public JSONObject setAmount(Number amount){
        return setValue("nAmountxx", amount);
    }
    
    public Number getAmount(){
        return (Number) getValue("nAmountxx");
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
    
    
}
