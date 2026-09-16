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

public class Model_Labor_Category extends Model {
        Model_Category poModelCategory;
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
            ID2 = "sCategrCd";

            //poModelCategory/poLabor are intentionally NOT constructed here - see Category()/
            //Labor() below, which build them lazily on first access so opening this record never
            //touches the Category/Labor tables.
            pnEditMode = EditMode.UNKNOWN;
        } catch (SQLException e) {
            logwrapr.severe(e.getMessage());
            System.exit(1);
        }
    }
    
    public Model_Category Category() throws SQLException, GuanzonException{
        if (poModelCategory == null) {
            poModelCategory = new Model_Category();
            poModelCategory.setApplicationDriver(poGRider);
            poModelCategory.setXML("Model_Category");
            poModelCategory.setTableName("Category");
            poModelCategory.initialize();
        }

        String categoryId = (String) (getValue("sCategrCd") == null ? "" : getValue("sCategrCd"));

        if (!"".equals(categoryId)) {
            if (poModelCategory.getEditMode() == EditMode.READY
                    && poModelCategory.getCategoryId().equals(categoryId)) {
                return poModelCategory;
            } else {
                if (ReferenceCache.tryLoad("Category", categoryId, poModelCategory)) {
                    return poModelCategory;
                }

                poJSON = poModelCategory.openRecord(categoryId);

                if ("success".equals((String) poJSON.get("result"))) {
                    ReferenceCache.store("Category", categoryId, poModelCategory);
                    return poModelCategory;
                } else {
                    poModelCategory.initialize();
                    return poModelCategory;
                }
            }
        } else {
            poModelCategory.initialize();
            return poModelCategory;
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

    public JSONObject setCategoryID(String categoryID) {
        return setValue("sCategrCd", categoryID);
    }

    public String getCategoryID() {
        return (String) getValue("sCategrCd");
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
