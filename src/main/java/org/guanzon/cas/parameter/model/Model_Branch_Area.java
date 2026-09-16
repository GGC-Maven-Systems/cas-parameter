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

/**
 *
 * @author maynevval 07-26-2025
 */
public class Model_Branch_Area extends Model {

    private Model_Industry poIndustry;

    @Override
    public void initialize() {
        try {
            poEntity = MiscUtil.xml2ResultSet(System.getProperty("sys.default.path.metadata") + XML, getTable());

            poEntity.last();
            poEntity.moveToInsertRow();

            MiscUtil.initRowSet(poEntity);

            poEntity.updateString("cRecdStat", "1");

            poEntity.insertRow();
            poEntity.moveToCurrentRow();

            poEntity.absolute(1);

            ID = poEntity.getMetaData().getColumnLabel(1);

            //poIndustry is intentionally NOT constructed here - see Industry() below, which
            //builds it lazily on first access so opening this record never touches Industry.

            pnEditMode = EditMode.UNKNOWN;
        } catch (SQLException e) {
            logwrapr.severe(e.getMessage());
            System.exit(1);
        }
    }
    //Getter & Setter 
    //sAreaCode
    //sAreaDesc*
    //sAreaMngr*
    //sAreaMail*
    //cDivision
    //cRecdStat

    //sAreaCode
    public JSONObject setAreaCode(String areaCode) {
        return setValue("sAreaCode", areaCode);
    }

    public String getAreaCode() {
        return (String) getValue("sAreaCode");
    }

    //sAreaDesc
    public JSONObject setAreaDescription(String areaDescription) {
        return setValue("sAreaDesc", areaDescription);
    }

    public String getAreaDescription() {
        return (String) getValue("sAreaDesc");
    }

    //cDivision
    public JSONObject setDivision(String industryCode) {
        return setValue("cDivision", industryCode);
    }

    public String getDivision() {
        return (String) getValue("cDivision");
    }

    //sAreaMngr
    public JSONObject setAreaManager(String areaManager) {
        return setValue("sAreaMngr", areaManager);
    }

    public String getAreaManager() {
        return (String) getValue("sAreaMngr");
    }

    //sAreaMail
    public JSONObject setAreaMail(String areaMail) {
        return setValue("sAreaMail", areaMail);
    }

    public String getAreaMail() {
        return (String) getValue("sAreaMail");
    }

    //cRecdStat
    public JSONObject setRecordStatus(String recordStatus) {
        return setValue("cRecdStat", recordStatus);
    }

    public String getRecordStatus() {
        return (String) getValue("cRecdStat");
    }

    //cRecdStat
    public JSONObject isRecordActive(boolean isRecordActive) {
        return setValue("cRecdStat", (isRecordActive == true) ? "1" : "0");
    }

    public boolean isRecordActive() {
        return RecordStatus.ACTIVE.equals(getValue("cRecdStat"));
    }

    //sModified
    public JSONObject setModifyingId(String modifyingId) {
        return setValue("sModified", modifyingId);
    }

    public String getModifyingId() {
        return (String) getValue("sModified");
    }

    //dModified
    public JSONObject setModifiedDate(Date modifiedDate) {
        return setValue("dModified", modifiedDate);
    }

    public Date getModifiedDate() {
        return (Date) getValue("dModified");
    }

    @Override
    public String getNextCode() {
        return "";
    }

    public Model_Industry Industry() throws SQLException, GuanzonException {
        if (this.poIndustry == null) {
            this.poIndustry = new Model_Industry();
            this.poIndustry.setApplicationDriver(poGRider);
            this.poIndustry.setXML("Model_Industry");
            this.poIndustry.setTableName("Industry");
            this.poIndustry.initialize();
        }

        String industryId = (String) (getValue("cDivision") == null ? "" : getValue("cDivision"));

        if (!"".equals(industryId)) {
            if (this.poIndustry.getEditMode() == EditMode.READY && this.poIndustry
                    .getIndustryId().equals(industryId)) {
                return this.poIndustry;
            }
            if (ReferenceCache.tryLoad("Industry", industryId, this.poIndustry)) {
                return this.poIndustry;
            }
            this.poJSON = this.poIndustry.openRecord(industryId);
            if ("success".equals(this.poJSON.get("result"))) {
                ReferenceCache.store("Industry", industryId, this.poIndustry);
                return this.poIndustry;
            }
            this.poIndustry.initialize();
            return this.poIndustry;
        }
        this.poIndustry.initialize();
        return this.poIndustry;
    }
}
