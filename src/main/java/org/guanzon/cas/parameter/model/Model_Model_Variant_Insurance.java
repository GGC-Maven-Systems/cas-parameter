package org.guanzon.cas.parameter.model;

import org.guanzon.appdriver.agent.services.Model;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.constant.RecordStatus;
import org.json.simple.JSONObject;

import java.sql.SQLException;
import java.util.Date;

public class Model_Model_Variant_Insurance extends Model {

    @Override
    public void initialize() {
        try {
            poEntity = MiscUtil.xml2ResultSet(System.getProperty("sys.default.path.metadata") + XML, getTable());

            poEntity.last();
            poEntity.moveToInsertRow();

            MiscUtil.initRowSet(poEntity);

            //assign default values
            poEntity.updateObject("sVhclType", "Commercial");
            poEntity.updateObject("sBodyType", "Sedan");
            poEntity.updateObject("sTransmss", "Manual");
            poEntity.updateObject("nAuthCapx", 0);
            poEntity.updateObject("nAuthCapx", 0);
            //end - assign default values

            poEntity.insertRow();
            poEntity.moveToCurrentRow();

            poEntity.absolute(1);

            ID = poEntity.getMetaData().getColumnLabel(1);

            pnEditMode = EditMode.UNKNOWN;
        } catch (SQLException e) {
            logwrapr.severe(e.getMessage());
            System.exit(1);
        }
    }

    public JSONObject setVariantId(String variantId) {
        return setValue("sVrntIDxx", variantId);
    }

    public String getVariantId() {
        return (String) getValue("sVrntIDxx");
    }

    public JSONObject setVehicleType(String vehicleType) {
        return setValue("sVhclType", vehicleType);
    }

    public String getVehicleType() {
        return (String) getValue("sVhclType");
    }

    public JSONObject setBodyType(String bodyType) {
        return setValue("sBodyType", bodyType);
    }

    public String getBodyType() {
        return (String) getValue("sBodyType");
    }

    public JSONObject setAuthCapx(int sortOrder) {
        return setValue("nAuthCapx", sortOrder);
    }

    public int getAuthCapx() {
        if (getValue("nAuthCapx") == null || "".equals(getValue("nAuthCapx"))) {
            return 0;
        }
        return Integer.parseInt(getValue("nAuthCapx").toString());
    }

    public JSONObject setTransmission(String transmission) {
        return setValue("sTransmss", transmission);
    }

    public String getTransmission() {
        return (String) getValue("sTransmss");
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

    @Override
    public String getNextCode() {
        return "";
//        return MiscUtil.getNextCode(getTable(), ID, false, poGRider.getGConnection().getConnection(), "");
    }
}
