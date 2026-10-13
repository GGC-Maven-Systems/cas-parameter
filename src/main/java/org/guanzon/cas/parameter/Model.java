package org.guanzon.cas.parameter;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.guanzon.appdriver.agent.ShowDialogFX;
import org.guanzon.appdriver.agent.services.Parameter;
import org.guanzon.appdriver.agent.services.ReferenceCache;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.Logical;
import org.guanzon.appdriver.constant.UserRight;
import org.guanzon.cas.parameter.model.Model_Model;
import org.guanzon.cas.parameter.services.ParamModels;
import org.json.simple.JSONObject;

public class Model extends Parameter {

    Model_Model poModel;

    @Override
    public void initialize() throws SQLException, GuanzonException {
        psRecdStat = Logical.YES;

        poModel = new ParamModels(poGRider).Model();

        super.initialize();
    }

    @Override
    public JSONObject isEntryOkay() throws SQLException {
        poJSON = new JSONObject();

        poModel.setIndustryCode(poGRider.getIndustry());
        if (poGRider.getUserLevel() < UserRight.SYSADMIN) {
            poJSON.put("result", "error");
            poJSON.put("message", "User is not allowed to save record.");
            return poJSON;
        } else {
            poJSON = new JSONObject();

            if (poModel.getDescription().isEmpty()) {
                poJSON.put("result", "error");
                poJSON.put("message", "Model must not be empty.");
                return poJSON;
            }

            if (poModel.getManufactureYear() == 0) {
                poJSON.put("result", "error");
                poJSON.put("message", "Year manufactured is invalid.");
                return poJSON;
            }

            if (poModel.getBrandId().isEmpty()) {
                poJSON.put("result", "error");
                poJSON.put("message", "Brand must not be empty.");
                return poJSON;
            }

            if (poModel.getIndustryCode().isEmpty()) {
                poJSON.put("result", "error");
                poJSON.put("message", "Industry must not be empty.");
                return poJSON;
            }
        }

        poModel.setModifyingId(poGRider.Encrypt(poGRider.getUserID()));
        poModel.setModifiedDate(poGRider.getServerDate());

        poJSON.put("result", "success");
        return poJSON;
    }

    @Override
    public Model_Model getModel() {
        return poModel;
    }

    @Override
    protected void saveComplete() {
        //Every lazy Model() accessor across the model layer serves repeat lookups for this id
        //from ReferenceCache - drop the stale snapshot now that the record has changed.
        ReferenceCache.invalidate("Model", poModel.getModelId());
    }

    @Override
    public JSONObject searchRecord(String value, boolean byCode) throws SQLException, GuanzonException {
        String lsSQL = getSQ_Browse();

        poJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                value,
                "ID»Description»Model Code»Mfg. Year»Brand",
                "sModelIDx»sDescript»sModelCde»nMfgYearx»xBrandNme",
                "a.sModelIDx»a.sDescript»a.sModelCde»nMfgYearx»b.sDescript",
                byCode ? 0 : 2);

        if (poJSON != null) {
            return poModel.openRecord((String) poJSON.get("sModelIDx"));
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
            return poJSON;
        }
    }

    public JSONObject searchRecordbyMainModel(String value, boolean byCode) throws SQLException, GuanzonException {
        String lsSQL = getSQ_Browse();
        Model_Model loModel;
        loModel = new ParamModels(poGRider).Model();

        lsSQL = MiscUtil.addCondition(lsSQL, "a.sMainModl = ''");
        poJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                value,
                "ID»Description»Model Code»Mfg. Year»Brand",
                "sModelIDx»sDescript»sModelCde»nMfgYearx»xBrandNme",
                "a.sModelIDx»a.sDescript»a.sModelCde»nMfgYearx»b.sDescript",
                byCode ? 0 : 2);

        if (poJSON != null) {
            JSONObject loResult = loModel.openRecord((String) poJSON.get("sModelIDx"));

            loResult.put("result", "success");
            loResult.put("message", "Record loaded successfully.");
            loResult.put("Description", (String) poJSON.get("sDescript"));
            poModel.setMainModelId((String) poJSON.get("sModelIDx"));

            return loResult;
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
            return poJSON;
        }
    }

    public JSONObject searchRecord(String value, boolean byCode, String brandId) throws SQLException, GuanzonException {
        String lsSQL = getSQ_Browse();

        if (brandId != null) {
            lsSQL = MiscUtil.addCondition(lsSQL, "a.sBrandIDx = " + SQLUtil.toSQL(brandId));
        }

        poJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                value,
                "ID»Description»Model Code»Mfg. Year»Brand",
                "sModelIDx»sDescript»sModelCde»nMfgYearx»xBrandNme",
                "a.sModelIDx»a.sDescript»a.sModelCde»nMfgYearx»b.sDescript",
                byCode ? 0 : 2);

        if (poJSON != null) {
            return poModel.openRecord((String) poJSON.get("sModelIDx"));
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
            return poJSON;
        }
    }

    public JSONObject searchRecordbyIndustry(String value, boolean byCode, String industryId) throws SQLException, GuanzonException {
        String lsSQL = getSQ_Browse();

        if (industryId != null) {
            lsSQL = MiscUtil.addCondition(lsSQL, "a.sIndstCdx = " + SQLUtil.toSQL(industryId));
        }

        poJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                value,
                "ID»Description»Model Code»Mfg. Year»Brand",
                "sModelIDx»sDescript»sModelCde»nMfgYearx»xBrandNme",
                "a.sModelIDx»a.sDescript»a.sModelCde»nMfgYearx»b.sDescript",
                byCode ? 0 : 2);

        if (poJSON != null) {
            return poModel.openRecord((String) poJSON.get("sModelIDx"));
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
            return poJSON;
        }
    }

    public JSONObject searchRecordbyIndustryWCondition(String value, boolean byCode, String industryId, String condition) throws SQLException, GuanzonException {
        String lsSQL = getSQ_Browse();

        if (industryId != null) {
            lsSQL = MiscUtil.addCondition(lsSQL, "a.sIndstCdx = " + SQLUtil.toSQL(industryId));
        }
        if (condition != null) {

            lsSQL = MiscUtil.addCondition(lsSQL, condition);
        }

        poJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                value,
                "ID»Description»Model Code»Mfg. Year»Brand",
                "sModelIDx»sDescript»sModelCde»nMfgYearx»xBrandNme",
                "a.sModelIDx»a.sDescript»a.sModelCde»nMfgYearx»b.sDescript",
                byCode ? 0 : 2);

        if (poJSON != null) {
            return poModel.openRecord((String) poJSON.get("sModelIDx"));
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
            return poJSON;
        }
    }

    @Override
    public String getSQ_Browse() {
        String lsCondition = "";

        if (psRecdStat.length() > 1) {
            for (int lnCtr = 0; lnCtr <= psRecdStat.length() - 1; lnCtr++) {
                lsCondition += ", " + SQLUtil.toSQL(Character.toString(psRecdStat.charAt(lnCtr)));
            }

            lsCondition = "a.cRecdStat IN (" + lsCondition.substring(2) + ")";
        } else {
            lsCondition = "a.cRecdStat = " + SQLUtil.toSQL(psRecdStat);
        }

        String lsSQL = "SELECT"
                + "  a.sModelIDx"
                + ", a.sModelCde"
                + ", a.sDescript"
                + ", a.nMfgYearx"
                + ", a.sMainModl"
                + ", a.sBrandIDx"
                + ", a.sIndstCdx"
                + ", a.cEndOfLfe"
                + ", a.cRecdStat"
                + ", a.sModified"
                + ", a.dModified"
                + ", b.sDescript xBrandNme"
                + " FROM Model a"
                + " LEFT JOIN Brand b ON a.sBrandIDx = b.sBrandIDx";;

        return MiscUtil.addCondition(lsSQL, lsCondition);
    }

    public JSONObject getMainModelName(String mainMdlID) throws SQLException {

        JSONObject result = new JSONObject();

        // ✅ Guard clause: check null or empty
        if (mainMdlID == null || mainMdlID.trim().isEmpty()) {
            result.put("result", "success");
            result.put("Description", "");
            return result; // end method immediately
        }

        String lsSQL = "SELECT sDescript FROM Model";
        lsSQL = MiscUtil.addCondition(lsSQL,
                "sModelIDx = " + SQLUtil.toSQL(mainMdlID));
        lsSQL += " ORDER BY sModelIDx DESC LIMIT 1";

        System.out.println("EXECUTING SQL: " + lsSQL);

        try (ResultSet loRS = poGRider.executeQuery(lsSQL)) {

            if (loRS != null && loRS.next()) {
                result.put("result", "success");
                result.put("Description", loRS.getString("sDescript"));
            } else {
                result.put("result", "error");
                result.put("message", "No record found.");
                result.put("Description", "");
            }

            return result;
        }
    }

}
