package org.guanzon.cas.parameter;

import java.sql.SQLException;
import org.guanzon.appdriver.agent.ShowDialogFX;
import org.guanzon.appdriver.agent.services.Parameter;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.json.simple.JSONObject;
import org.guanzon.cas.parameter.model.Model_Branch_Area;
import org.guanzon.cas.parameter.services.ParamModels;

/**
 *
 * @author 12mnv
 */
public class BranchArea extends Parameter {

    Model_Branch_Area poModel;
    String psIndustry = "";

    public String getPsIndustry() {
        return psIndustry;
    }

    public void setPsIndustry(String psIndustry) {
        this.psIndustry = psIndustry;
    }

    public void initialize() throws SQLException, GuanzonException {
        this.poModel = (new ParamModels(this.poGRider)).BranchArea();
        super.initialize();
    }

    public JSONObject isEntryOkay() throws SQLException {
        this.poJSON = new JSONObject();
        if (this.poGRider.getUserLevel() < 16) {
            this.poJSON.put("result", "error");
            this.poJSON.put("message", "User is not allowed to save record.");
            return this.poJSON;
        }
        this.poJSON = new JSONObject();
        if (this.poModel.getAreaCode().isEmpty()) {
            this.poJSON.put("result", "error");
            this.poJSON.put("message", "Area Code must not be empty.");
            return this.poJSON;
        }
        if (this.poModel.getAreaDescription().isEmpty()) {
            this.poJSON.put("result", "error");
            this.poJSON.put("message", "Area Description must not be empty.");
            return this.poJSON;
        }
        this.poModel.setModifyingId(this.poGRider.Encrypt(this.poGRider.getUserID()));
        this.poModel.setModifiedDate(this.poGRider.getServerDate());
        this.poJSON.put("result", "success");
        return this.poJSON;
    }

    public Model_Branch_Area getModel() {
        return this.poModel;
    }

    public JSONObject searchRecord(String value, boolean byCode) throws SQLException, GuanzonException {

        String lsCondition = "";
        String lsSQL = "";
        if (this.psRecdStat.length() > 1) {
            for (int lnCtr = 0; lnCtr <= this.psRecdStat.length() - 1; lnCtr++) {
                lsCondition = lsCondition + ", " + SQLUtil.toSQL(Character.toString(this.psRecdStat.charAt(lnCtr)));
            }
            lsCondition = "cRecdStat IN (" + lsCondition.substring(2) + ")";
        } else {
            lsCondition = "cRecdStat = " + SQLUtil.toSQL(this.psRecdStat);
        }
        lsSQL = MiscUtil.addCondition(getSQ_Browse(), lsCondition);

        if (!psIndustry.isEmpty()) {
            lsSQL = MiscUtil.addCondition(lsSQL, lsCondition);
        }
        this.poJSON = ShowDialogFX.Search(this.poGRider,
                lsSQL,
                value,
                "Code»Area Name",
                "sAreaCode»sAreaDesc",
                "sAreaCode»sAreaDesc",
                byCode ? 0 : 1);
        if (this.poJSON != null) {
            return this.poModel.openRecord((String) this.poJSON.get("sAreaCode"));
        }
        this.poJSON = new JSONObject();
        this.poJSON.put("result", "error");
        this.poJSON.put("message", "No record loaded.");
        return this.poJSON;
    }

    public JSONObject searchRecordbyIndustry(String value, boolean byCode, String industryId) throws SQLException, GuanzonException {

        String lsCondition = "";
        String lsSQL = "";
        if (this.psRecdStat.length() > 1) {
            for (int lnCtr = 0; lnCtr <= this.psRecdStat.length() - 1; lnCtr++) {
                lsCondition = lsCondition + ", " + SQLUtil.toSQL(Character.toString(this.psRecdStat.charAt(lnCtr)));
            }
            lsCondition = "cRecdStat IN (" + lsCondition.substring(2) + ")";
        } else {
            lsCondition = "cRecdStat = " + SQLUtil.toSQL(this.psRecdStat);
        }
        lsSQL = MiscUtil.addCondition(getSQ_Browse(), lsCondition);
        if (industryId != null) {
            lsSQL = MiscUtil.addCondition(lsSQL, "b.sIndstCdx = " + SQLUtil.toSQL(industryId));
        }

        if (!psIndustry.isEmpty()) {
            lsSQL = MiscUtil.addCondition(lsSQL, lsCondition);
        }
        this.poJSON = ShowDialogFX.Search(this.poGRider,
                lsSQL,
                value,
                "Code»Area Name",
                "sAreaCode»sAreaDesc",
                "sAreaCode»sAreaDesc",
                byCode ? 0 : 1);
        if (this.poJSON != null) {
            return this.poModel.openRecord((String) this.poJSON.get("sAreaCode"));
        }
        this.poJSON = new JSONObject();
        this.poJSON.put("result", "error");
        this.poJSON.put("message", "No record loaded.");
        return this.poJSON;
    }
}
