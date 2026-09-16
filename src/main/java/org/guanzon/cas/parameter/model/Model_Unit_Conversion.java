package org.guanzon.cas.parameter.model;

import org.guanzon.appdriver.agent.services.Model;
import org.guanzon.appdriver.agent.services.ReferenceCache;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.constant.RecordStatus;
import org.json.simple.JSONObject;

import java.sql.SQLException;
import java.util.Date;

public class Model_Unit_Conversion extends Model {
    Model_Measure poMeasure;
    Model_Measure poConvert;
    @Override
    public void initialize() {
        try {
            poEntity = MiscUtil.xml2ResultSet(System.getProperty("sys.default.path.metadata") + XML, getTable());

            poEntity.last();
            poEntity.moveToInsertRow();

            MiscUtil.initRowSet(poEntity);

            //assign default values
            
            poEntity.updateObject("nQtyCnvrt", 0.00);
            poEntity.updateString("cRecdStat", "0");
            //end - assign default values

            poEntity.insertRow();
            poEntity.moveToCurrentRow();

            poEntity.absolute(1);

            ID = poEntity.getMetaData().getColumnLabel(1);
            //poMeasure/poConvert are intentionally NOT constructed here - see Measurement()/
            //ConvertTo() below, which build them lazily on first access so opening this record
            //never touches the Measure table.
            pnEditMode = EditMode.UNKNOWN;
        } catch (SQLException e) {
            logwrapr.severe(e.getMessage());
            System.exit(1);
        }
    }


    
    public JSONObject setConversionID(String conversionID) {
        return setValue("sCnvrsnID", conversionID);
    }

    public String getConversionID() {
        return (String) getValue("sCnvrsnID");
    }

    public JSONObject setMeasureID(String measureID) {
        return setValue("sMeasurID", measureID);
    }

    public String getMeasureID() {
        return (String) getValue("sMeasurID");
    }
    
    public JSONObject setConvertedID(String convertedID) {
        return setValue("sConvrtID", convertedID);
    }

    public String getConvertedID() {
        return (String) getValue("sConvrtID");
    }
    
    public JSONObject setQuantityConverted(double quantityConverted) {
        return setValue("nQtyCnvrt", quantityConverted);
    }

    public double getQuantityConverted() {
        return Double.parseDouble(String.valueOf(getValue("nQtyCnvrt")));

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

    public Model_Measure Measurement() throws SQLException, GuanzonException {
        if (poMeasure == null) {
            poMeasure = new Model_Measure();
            poMeasure.setApplicationDriver(poGRider);
            poMeasure.setXML("Model_Measure");
            poMeasure.setTableName("Measure");
            poMeasure.initialize();
        }
        
        String measureId = (String) (getValue("sMeasurID") == null ? "" : getValue("sMeasurID"));

        if (!"".equals(measureId)) {
            if (poMeasure.getEditMode() == EditMode.READY
                    && poMeasure.getMeasureId().equals(measureId)) {
                return poMeasure;
            } else {
                if (ReferenceCache.tryLoad("Measure", measureId, poMeasure)) {
                    return poMeasure;
                }

                poJSON = poMeasure.openRecord(measureId);

                if ("success".equals((String) poJSON.get("result"))) {
                    ReferenceCache.store("Measure", measureId, poMeasure);
                    return poMeasure;
                } else {
                    poMeasure.initialize();
                    return poMeasure;
                }
            }
        } else {
            poMeasure.initialize();
            return poMeasure;
        }
    }
    public Model_Measure ConvertTo() throws SQLException, GuanzonException {
        if (poConvert == null) {
            poConvert = new Model_Measure();
            poConvert.setApplicationDriver(poGRider);
            poConvert.setXML("Model_Measure");
            poConvert.setTableName("Measure");
            poConvert.initialize();
        }

        String convertId = (String) (getValue("sConvrtID") == null ? "" : getValue("sConvrtID"));

        if (!"".equals(convertId)) {
            if (poConvert.getEditMode() == EditMode.READY
                    && poConvert.getMeasureId().equals(convertId)) {
                return poConvert;
            } else {
                if (ReferenceCache.tryLoad("Measure", convertId, poConvert)) {
                    return poConvert;
                }

                poJSON = poConvert.openRecord(convertId);

                if ("success".equals((String) poJSON.get("result"))) {
                    ReferenceCache.store("Measure", convertId, poConvert);
                    return poConvert;
                } else {
                    poConvert.initialize();
                    return poConvert;
                }
            }
        } else {
            poConvert.initialize();
            return poConvert;
        }
    }

}
