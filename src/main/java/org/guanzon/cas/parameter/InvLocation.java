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
import org.guanzon.cas.parameter.model.Model_Inv_Location;
import org.guanzon.cas.parameter.services.ParamModels;
import org.json.simple.JSONObject;

public class InvLocation extends Parameter{
    Model_Inv_Location poModel;
    
    @Override
    public void initialize() throws SQLException, GuanzonException{
        psRecdStat = Logical.YES;
        
        poModel = new ParamModels(poGRider).InventoryLocation();
        
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
            
            if (poModel.getDescription().isEmpty()){
                poJSON.put("result", "error");
                poJSON.put("message", "Country must not be empty.");
                return poJSON;
            }
            
            if (poModel.getWarehouseId().isEmpty()){
                poJSON.put("result", "error");
                poJSON.put("message", "Warehouse must not be empty.");
                return poJSON;
            }
            
            if (poModel.getSectionId().isEmpty()){
                poJSON.put("result", "error");
                poJSON.put("message", "Section must not be empty.");
                return poJSON;
            }
        }
        
        poModel.setModifyingId(poGRider.Encrypt(poGRider.getUserID()));
        poModel.setModifiedDate(poGRider.getServerDate());
        
        poJSON.put("result", "success");
        return poJSON;
    }
    
    @Override
    public Model_Inv_Location getModel() {
        return poModel;
    }

    @Override
    protected void saveComplete() {
        //Model_POR_Serial.Location() serves repeat lookups for this id from ReferenceCache -
        //drop the stale snapshot now that the record has changed.
        ReferenceCache.invalidate("Inv_Location", poModel.getLocationId());
    }

    @Override
    public JSONObject searchRecord(String value, boolean byCode) throws SQLException, GuanzonException{
        String lsSQL = getSQ_Browse();
        
        poJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                value,
                "ID»Description»Warehouse»Section",
                "sLocatnID»sDescript»xWHouseNm»xSectnIDx",
                "sLocatnID»sDescript»IFNULL(b.sWHouseNm, '')»IFNULL(c.sSectnNme, '')",
                byCode ? 0 : 1);

        if (poJSON != null) {
            return poModel.openRecord((String) poJSON.get("sLocatnID"));
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
            return poJSON;
        }
    }    
    
    @Override
    public String getSQ_Browse(){
        String lsCondition = "";

        if (psRecdStat.length() > 1) {
            for (int lnCtr = 0; lnCtr <= psRecdStat.length() - 1; lnCtr++) {
                lsCondition += ", " + SQLUtil.toSQL(Character.toString(psRecdStat.charAt(lnCtr)));
            }

            lsCondition = "a.cRecdStat IN (" + lsCondition.substring(2) + ")";
        } else {
            lsCondition = "a.cRecdStat = " + SQLUtil.toSQL(psRecdStat);
        }
        
        String lsSQL = "SELECT" +
                            "  a.sLocatnID" +
                            ", a.sDescript" +
                            ", a.sWHouseID" +
                            ", a.sSectnIDx" +
                            ", a.cRecdStat" +
                            ", a.sModified" +
                            ", a.dModified" +
                            ", IFNULL(b.sWHouseNm, '') xWHouseNm" +
                            ", IFNULL(c.sSectnNme, '') xSectnIDx" + 
                        " FROM Inv_Location a" +
                            " LEFT JOIN Warehouse b ON a.sWHouseID = b.sWHouseID" +
                            " LEFT JOIN Section c ON a.sSectnIDx = c.sSectnIDx";
        
        return MiscUtil.addCondition(lsSQL, lsCondition);
    }
}