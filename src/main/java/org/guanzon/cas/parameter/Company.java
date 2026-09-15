package org.guanzon.cas.parameter;

import java.sql.SQLException;
import org.guanzon.appdriver.agent.ShowDialogFX;
import org.guanzon.appdriver.agent.services.Parameter;
import org.guanzon.appdriver.agent.services.ReferenceCache;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.Logical;
import org.guanzon.appdriver.constant.UserRight;
import org.guanzon.cas.parameter.model.Model_Company;
import org.guanzon.cas.parameter.services.ParamModels;
import org.json.simple.JSONObject;

public class Company extends Parameter{
    Model_Company poModel;
    
    @Override
    public void initialize() throws SQLException, GuanzonException{
        psRecdStat = Logical.YES;
        
        poModel = new ParamModels(poGRider).Company();
        
        super.initialize();
    }
    
    @Override
    public JSONObject isEntryOkay() throws SQLException{
        poJSON = new JSONObject();
        
        if (poGRider.getUserLevel() < UserRight.SYSADMIN){
            poJSON.put("result", "error");
            poJSON.put("message", "User is not allowed to save record.");
            return poJSON;
        } else {
            poJSON = new JSONObject();
            
            if (poModel.getCompanyId().isEmpty()){
                poJSON.put("result", "error");
                poJSON.put("message", "Company must not be empty.");
                return poJSON;
            }
            
            if (poModel.getCompanyName()== null ||  poModel.getCompanyName().isEmpty()){
                poJSON.put("result", "error");
                poJSON.put("message", "Company name must not be empty.");
                return poJSON;
            }
            
            if (poModel.getCompanyCode()== null ||  poModel.getCompanyCode().isEmpty()){
                poJSON.put("result", "error");
                poJSON.put("message", "Company code must not be empty.");
                return poJSON;
            }
            
            if (poModel.getEmployerNo()== null ||  poModel.getEmployerNo().isEmpty()){
                poJSON.put("result", "error");
                poJSON.put("message", "Employer No must not be empty.");
                return poJSON;
            }
        }
        
        poModel.setModifyingId(poGRider.Encrypt(poGRider.getUserID()));
        poModel.setModifiedDate(poGRider.getServerDate());
        
        poJSON.put("result", "success");
        return poJSON;
    }
    
    @Override
    public Model_Company getModel() {
        return poModel;
    }

    @Override
    protected void saveComplete() {
        //Every lazy Company() accessor across the model layer serves repeat lookups for this id
        //from ReferenceCache - drop the stale snapshot now that the record has changed.
        ReferenceCache.invalidate("Company", poModel.getCompanyId());
    }

    @Override
    public JSONObject searchRecord(String value, boolean byCode) throws SQLException, GuanzonException{
        String lsCondition = "";

        if (psRecdStat.length() > 1) {
            for (int lnCtr = 0; lnCtr <= psRecdStat.length() - 1; lnCtr++) {
                lsCondition += ", " + SQLUtil.toSQL(Character.toString(psRecdStat.charAt(lnCtr)));
            }

            lsCondition = "cRecdStat IN (" + lsCondition.substring(2) + ")";
        } else {
            lsCondition = "cRecdStat = " + SQLUtil.toSQL(psRecdStat);
        }

        String lsSQL = MiscUtil.addCondition(getSQ_Browse(), lsCondition);
        
        poJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                value,
                "ID»Description»Company Code»Employer No",
                "sCompnyID»sCompnyNm»sCompnyCd»sEmplyrNo",
                "sCompnyID»sCompnyNm»sCompnyCd»sEmplyrNo",
                byCode ? 0 : 1);

        if (poJSON != null) {
            return poModel.openRecord((String) poJSON.get("sCompnyID"));
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
            return poJSON;
        }
    }
        
    public JSONObject searchRecordWithStatus(String value, boolean byCode) throws SQLException, GuanzonException{
        String lsCondition = "";

        if (psRecdStat.length() > 1) {
            for (int lnCtr = 0; lnCtr <= psRecdStat.length() - 1; lnCtr++) {
                lsCondition += ", " + SQLUtil.toSQL(Character.toString(psRecdStat.charAt(lnCtr)));
            }

            lsCondition = "cRecdStat IN (" + lsCondition.substring(2) + ")";
        } else {
            lsCondition = "cRecdStat = " + SQLUtil.toSQL(psRecdStat);
        }

        String lsSQL = MiscUtil.addCondition(getSQ_Browse(), lsCondition);

        poJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                value,
                "ID»Description»Company Code»Employer No",
                "sCompnyID»sCompnyNm»sCompnyCd»sEmplyrNo",
                "sCompnyID»sCompnyNm»sCompnyCd»sEmplyrNo",
                byCode ? 0 : 1);

        if (poJSON != null) {
            return poModel.openRecord((String) poJSON.get("sCompnyID"));
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
            return poJSON;
        }
    }
    
    @Override
    public JSONObject openRecord(String Id) throws SQLException, GuanzonException {
        if (!pbInitRec){
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "Object is not initialized.");
            return poJSON;
        }
        
        poJSON =  getModel().openRecord(Id);
        
        if ("success".equals((String) poJSON.get("result"))){
            poModel.setCompanyTownId(poModel.getCompanyTownId());
        }
        
        return poJSON;
    }
}