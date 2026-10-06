package org.guanzon.cas.parameter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import org.guanzon.appdriver.agent.ShowDialogFX;
import org.guanzon.appdriver.agent.services.Parameter;
import org.guanzon.appdriver.agent.services.ReferenceCache;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.Logical;
import org.guanzon.appdriver.constant.RecordStatus;
import org.guanzon.appdriver.constant.UserRight;
import org.guanzon.cas.parameter.model.Model_Model;
import org.guanzon.cas.parameter.services.ParamModels;
import org.json.simple.JSONObject;

public class Model extends Parameter {

    Model_Model poModel;
    private static String psCarIndustry = "03";
    private String psIndustryId = "";
    public void setIndustryId(String industryId) { psIndustryId = industryId; }
    
    @Override
    public void initialize() throws SQLException, GuanzonException {
        psRecdStat = Logical.YES;

        poModel = new ParamModels(poGRider).Model();

        super.initialize();
    }

    @Override
    public JSONObject isEntryOkay() throws SQLException {
        poJSON = new JSONObject();
        if(pbWithUI){
            poModel.setIndustryCode(poGRider.getIndustry());
        } else {
            poModel.setIndustryCode(psIndustryId);
        }
        if (poGRider.getUserLevel() < UserRight.SYSADMIN && !psCarIndustry.equals(poModel.getIndustryCode())) {
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
            
            if(!psCarIndustry.equals(poModel.getIndustryCode())){
                if (poModel.getManufactureYear() == 0) {
                    poJSON.put("result", "error");
                    poJSON.put("message", "Year manufactured is invalid.");
                    return poJSON;
                }
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

        if (brandId != null && !"".equals(brandId)) {
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

    @Override
    public String getSQ_Browse() {
        String lsCondition = "";
        String lsIndustryId = "";
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
                + " LEFT JOIN Brand b ON a.sBrandIDx = b.sBrandIDx";
        lsSQL = MiscUtil.addCondition(lsSQL, lsCondition);
        
        if(psIndustryId != null && !"".equals(psIndustryId)){
            lsSQL = lsSQL + " AND a.sIndstCdx = " +  SQLUtil.toSQL(psIndustryId);
        }
        
        return lsSQL;
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


    /**
     * Loads the status history of the current record into a cached row set.
     *
     * @return cached row set containing status history data
     * @throws SQLException if a database error occurs
     */
    protected CachedRowSet getStatusHistoryTest() throws SQLException {
        String lsSQL = "SELECT  a.sTableNme, a.sSourceNo, a.sRemarksx, a.cRefrStat cTranStat, IFNULL(c.sCompnyNm, '-') xModified, IFNULL(e.sCompnyNm, '-') xApproved, a.dModified, a.dApproved, a.sModified, a.sApproved " +
                    " FROM Parameter_Status_History a " +
                    "LEFT JOIN xxxSysUser b ON b.sUserIDxx = a.sModified " +
                    "LEFT JOIN Client_Master c ON b.sEmployNo = c.sClientID " +
                    "LEFT JOIN xxxSysUser d ON d.sUserIDxx = a.sApproved " +
                    "LEFT JOIN Client_Master e ON d.sEmployNo = e.sClientID " +
                    " WHERE a.sSourceNo = " + SQLUtil.toSQL(getModel().getModelId()) +
                    " AND a.sTableNme = " + SQLUtil.toSQL(getModel().getTable()) + " ORDER BY a.dModified";
        System.out.println("STATUS HISTORY : " + lsSQL);
        ResultSet loRS = this.poGRider.executeQuery(lsSQL);
        RowSetFactory factory = RowSetProvider.newFactory();
        CachedRowSet rowset = factory.createCachedRowSet();
        rowset.populate(loRS);
        MiscUtil.close(loRS);
        return rowset;
    }
    
    /**
     * Displays the status history of a record.
     *
     * Retrieves status records, maps status codes to readable text, and
     * shows them in the UI along with entry details.
     *
     * @throws SQLException if a database error occurs
     * @throws GuanzonException if application-specific error occurs
     * @throws Exception for other unexpected errors
     */
    public void ShowStatusHistory() throws SQLException, GuanzonException, Exception {
        CachedRowSet crs;
        if(pbWithUI){
            crs = getStatusHistory();
        } else {
            crs = getStatusHistoryTest();
        }

        crs.beforeFirst();

        while(crs.next()){
            switch (crs.getString("cRefrStat")){
                case "":
                    crs.updateString("cRefrStat", "-");
                    break;
                case RecordStatus.ACTIVE:
                    crs.updateString("cRefrStat", "ACTIVE");
                    break;
                case RecordStatus.INACTIVE:
                    crs.updateString("cRefrStat", "INACTIVE");
                    break;
                default:
                    char ch = crs.getString("cRefrStat").charAt(0);
                    String stat = String.valueOf((int) ch - 64);

                    switch (stat){
                        case RecordStatus.ACTIVE:
                            crs.updateString("cRefrStat", "ACTIVE");
                            break;
                        case RecordStatus.INACTIVE:
                            crs.updateString("cRefrStat", "INACTIVE");
                            break;
                    }
            }
            crs.updateRow();
        }

        JSONObject loJSON = getEntryBy();
        String entryBy = "";
        String entryDate = "";

        if (isJSONSuccess(loJSON)) {
            entryBy = (String) loJSON.get("sCompnyNm");
            entryDate = (String) loJSON.get("sEntryDte");
        }
        if(pbWithUI){
            showStatusHistoryUI("Model", (String) getModel().getValue("sModelIDx"), entryBy, entryDate, crs);
        }
    }
    /**
     * Retrieves the user and timestamp of who created the current transaction.
     *
     * @return JSONObject containing "sCompnyNm" (user) and "sEntryDte" (timestamp)
     * @throws SQLException if a database error occurs
     * @throws GuanzonException if application-specific error occurs
     */
    public JSONObject getEntryBy() throws SQLException, GuanzonException {
        poJSON = new JSONObject();
        String lsEntry = "";
        String lsEntryDate = "";
        String lsSQL = " SELECT b.sModified, b.dModified "
                + " FROM "+getModel().getTable()+" a "
                + " LEFT JOIN xxxAuditLogMaster b ON b.sSourceNo = a.sModelIDx AND b.sEventNme LIKE 'ADD%NEW' AND b.sRemarksx = " + SQLUtil.toSQL(getModel().getTable());
        lsSQL = MiscUtil.addCondition(lsSQL, " a.sModelIDx =  " + SQLUtil.toSQL(getModel().getModelId()));
        lsSQL = lsSQL + " ORDER BY b.dModified DESC ";
        System.out.println("Execute SQL : " + lsSQL);
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        try {
            if (MiscUtil.RecordCount(loRS) > 0L) {
                if (loRS.next()) {
                    if (loRS.getString("sModified") != null && !"".equals(loRS.getString("sModified"))) {
                        if (loRS.getString("sModified").length() > 10) {
                            lsEntry = getSysUser(poGRider.Decrypt(loRS.getString("sModified")));
                        } else {
                            lsEntry = getSysUser(loRS.getString("sModified"));
                        }
                        // Get the LocalDateTime from your result set
                        LocalDateTime dModified = loRS.getObject("dModified", LocalDateTime.class);
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss");
                        lsEntryDate = dModified.format(formatter);
                    }
                }
            }
            MiscUtil.close(loRS);
        } catch (SQLException e) {
            poJSON = setJSON("error", e.getMessage());
            return poJSON;
        }

        poJSON.put("result", "success");
        poJSON.put("sCompnyNm", lsEntry);
        poJSON.put("sEntryDte", lsEntryDate);
        return poJSON;
    }
    /**
     * Retrieves the company name of a system user based on user ID.
     *
     * @param fsId User ID to lookup
     * @return Company name of the user
     * @throws SQLException if a database error occurs
     * @throws GuanzonException if application-specific error occurs
     */
    public String getSysUser(String fsId) throws SQLException, GuanzonException {
        String lsEntry = "";
        String lsSQL = " SELECT b.sCompnyNm from xxxSysUser a "
                + " LEFT JOIN Client_Master b ON b.sClientID = a.sEmployNo ";
        lsSQL = MiscUtil.addCondition(lsSQL, " a.sUserIDxx =  " + SQLUtil.toSQL(fsId));
        System.out.println("Execute SQL : " + lsSQL);
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        try {
            if (MiscUtil.RecordCount(loRS) > 0L) {
                if (loRS.next()) {
                    lsEntry = loRS.getString("sCompnyNm");
                }
            }
            MiscUtil.close(loRS);
        } catch (SQLException e) {
            poJSON = setJSON("error", e.getMessage());
        }
        return lsEntry;
    }
    
    
    /**
    * Creates a JSONObject with "result" and "message" fields.
    *
    * @param fsResult  The result value (e.g., "success", "error")
    * @param fsMessage The message describing the result
    * @return JSONObject containing the result and message
    */
    private JSONObject setJSON(String fsResult, String fsMessage) {
        JSONObject loJSON = new JSONObject();
        loJSON.put("result", fsResult);
        loJSON.put("message", fsMessage);
        return loJSON;
    }

    /**
     * Checks whether a JSONObject indicates a successful result.
     *
     * Returns true if the "result" field equals "success" or is not "error".
     *
     * @param foJSON The JSONObject to check
     * @return true if successful, false otherwise
     */
    public boolean isJSONSuccess(JSONObject foJSON) {
        return ("success".equals((String) foJSON.get("result")) || !"error".equals((String) foJSON.get("result")));
    }
}