package org.guanzon.cas.parameter.model;

import java.sql.SQLException;
import java.util.Date;
import org.guanzon.appdriver.agent.services.Model;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.json.simple.JSONObject;
public class Model_Insurance_Type extends Model {

    @Override
    public void initialize() {
        try {
            poEntity = MiscUtil.xml2ResultSet(System.getProperty("sys.default.path.metadata")
                    + "/Model_Insurance_Type.xml", getTable());

            poEntity.last();
            poEntity.moveToInsertRow();

            MiscUtil.initRowSet(poEntity);
            poEntity.updateString("cRecdStat", "1");   // ACTIVE

            poEntity.insertRow();
            poEntity.moveToCurrentRow();
            poEntity.absolute(1);

            ID = "sInsTypID";

            pnEditMode = EditMode.UNKNOWN;
        } catch (SQLException e) {
            logwrapr.severe(e.getMessage());
            System.exit(1);
        }
    }

    public JSONObject setInsuranceTypeID(String value) {
        return setValue("sInsTypID", value);
    }

    public String getInsuranceTypeID() {
        return (String) getValue("sInsTypID");
    }

    public JSONObject setInsuranceTypeName(String value) {
        return setValue("sInsTypNm", value);
    }

    public String getInsuranceTypeName() {
        return (String) getValue("sInsTypNm");
    }

    public JSONObject setRegistrationType(String value) {
        return setValue("cRegisTyp", value);
    }

    public String getRegistrationType() {
        return (String) getValue("cRegisTyp");
    }

    public JSONObject setMonitor(String value) {
        return setValue("cMonitorx", value);
    }

    public String getMonitor() {
        return (String) getValue("cMonitorx");
    }

    public JSONObject setRecordStatus(String value) {
        return setValue("cRecdStat", value);
    }

    public String getRecordStatus() {
        return (String) getValue("cRecdStat");
    }

    public JSONObject setModifiedBy(String value) {
        return setValue("sModified", value);
    }

    public String getModifiedBy() {
        return (String) getValue("sModified");
    }

    public JSONObject setModifiedDate(Date value) {
        return setValue("dModified", value);
    }

    public Date getModifiedDate() {
        return (Date) getValue("dModified");
    }

    @Override
    public String getNextCode() {
        return MiscUtil.getNextCode(getTable(), ID, true,
                poGRider.getGConnection().getConnection(), poGRider.getBranchCode());
    }
}
