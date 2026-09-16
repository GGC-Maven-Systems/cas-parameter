package org.guanzon.cas.parameter.model;

import java.sql.SQLException;
import java.util.Date;
import org.guanzon.appdriver.agent.services.Model;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.constant.Logical;
import org.guanzon.appdriver.agent.services.ReferenceCache;
import org.guanzon.appdriver.constant.RecordStatus;
import org.json.simple.JSONObject;

public class Model_Barangay extends Model{
    //other model connections
    Model_TownCity poTownCity;
    
    @Override
    public void initialize() {
        try {
            poEntity = MiscUtil.xml2ResultSet(System.getProperty("sys.default.path.metadata") + XML, getTable());
            
            poEntity.last();
            poEntity.moveToInsertRow();

            MiscUtil.initRowSet(poEntity);
            
            //assign default values
            poEntity.updateString("cBlackLst", Logical.NO);
            poEntity.updateString("cHasRoute", Logical.YES);
            poEntity.updateString("cRecdStat", RecordStatus.ACTIVE);
            //end - assign default values

            poEntity.insertRow();
            poEntity.moveToCurrentRow();

            poEntity.absolute(1);

            ID = poEntity.getMetaData().getColumnLabel(1);
            
            //poTownCity is intentionally NOT constructed here - see Town()/setTownId() below,
            //which build it lazily on first access so opening this record never touches TownCity.
            
            pnEditMode = EditMode.UNKNOWN;
        } catch (SQLException e) {
            logwrapr.severe(e.getMessage());
            System.exit(1);
        }
    }
        
    public JSONObject setBarangayId(String barangayId){
        return setValue("sBrgyIDxx", barangayId);
    }
    
    public String getBarangayId(){
        return (String) getValue("sBrgyIDxx");
    }
    
    public JSONObject setBarangayName(String barangayName){
        return setValue("sBrgyName", barangayName);
    }
    
    public String getBarangayName(){
        return (String) getValue("sBrgyName");
    }
    
    public JSONObject setTownId(String townId){
        if (poTownCity == null) {
            poTownCity = new Model_TownCity();
            poTownCity.setApplicationDriver(poGRider);
            poTownCity.setXML("Model_TownCity");
            poTownCity.setTableName("TownCity");
            poTownCity.initialize();
        }

        poJSON =  setValue("sTownIDxx", townId);

        if ("success".equals(poJSON.get("result"))){
            if (!townId.isEmpty()) {
                if (!poTownCity.getTownId().equals(townId)) {
                    try {
                        //TownCity is a small, rarely-changing parameter table - serve repeat
                        //lookups for the same id from memory instead of hitting the database.
                        if (!ReferenceCache.tryLoad("TownCity", townId, poTownCity)) {
                            poJSON = poTownCity.openRecord(townId);

                            if (!"success".equals(poJSON.get("result"))){
                                return poJSON;
                            }

                            ReferenceCache.store("TownCity", townId, poTownCity);
                        }
                    } catch (SQLException | GuanzonException e) {
                        poJSON = new JSONObject();
                        poJSON.put("result", "error");
                        poJSON.put("message", e.getMessage());
                        return poJSON;
                    }
                }
            }
        }

        return poJSON;
    }
    
    public String getTownId(){
        return (String) getValue("sTownIDxx");
    }
    
    public JSONObject hasRoute(boolean hasRoute){
        return setValue("cHasRoute", hasRoute == true ? "1" : "0");
    }
    
    public boolean hasRoute(){
        return "1".equals((String) getValue("cHasRoute"));
    }
    
    public JSONObject isBlacklisted(boolean isBlacklisted){
        return setValue("cBlackLst", isBlacklisted == true ? "1" : "0");
    }
    
    public boolean isBlacklisted(){
        return "1".equals((String) getValue("cBlackLst"));
    }
    
    public JSONObject setRecordStatus(String recordStatus){
        return setValue("cRecdStat", recordStatus);
    }
    
    public String getRecordStatus(){
        return (String) getValue("cRecdStat");
    }
    
    public JSONObject setModifyingId(String modifyingId){
        return setValue("sModified", modifyingId);
    }
    
    public String getModifyingId(){
        return (String) getValue("sModified");
    }
    
    public JSONObject setModifiedDate(Date modifiedDate){
        return setValue("dModified", modifiedDate);
    }
    
    public Date getModifiedDate(){
        return (Date) getValue("dModified");
    }
    
    public Model_TownCity Town() throws SQLException, GuanzonException{
        if (poTownCity == null) {
            poTownCity = new Model_TownCity();
            poTownCity.setApplicationDriver(poGRider);
            poTownCity.setXML("Model_TownCity");
            poTownCity.setTableName("TownCity");
            poTownCity.initialize();
        }
        
        return poTownCity;
    }
    
    @Override
    public String getNextCode() {
        return MiscUtil.getNextCode(getTable(), ID, true, poGRider.getGConnection().getConnection(), "");
    }
}
