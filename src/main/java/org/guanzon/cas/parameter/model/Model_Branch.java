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

public class Model_Branch extends Model {
    
    private Model_TownCity poTown;
    @Override
    public void initialize() {
        try {
            poEntity = MiscUtil.xml2ResultSet(System.getProperty("sys.default.path.metadata") + XML, getTable());
            
            poEntity.last();
            poEntity.moveToInsertRow();

            MiscUtil.initRowSet(poEntity);
            
            //assign default values
            poEntity.updateString("cRecdStat", RecordStatus.ACTIVE);
            //end - assign default values

            poEntity.insertRow();
            poEntity.moveToCurrentRow();

            poEntity.absolute(1);

            ID = poEntity.getMetaData().getColumnLabel(1);
            //poTown is intentionally NOT constructed here - see TownCity() below, which builds
            //it lazily on first access so opening this record never touches the TownCity table.

            pnEditMode = EditMode.UNKNOWN;
        } catch (SQLException e) {
            logwrapr.severe(e.getMessage());
            System.exit(1);
        }
    }
    
    public Model_TownCity TownCity() throws SQLException, GuanzonException{
        if (poTown == null) {
            poTown = new Model_TownCity();
            poTown.setApplicationDriver(poGRider);
            poTown.setXML("Model_TownCity");
            poTown.setTableName("TownCity");
            poTown.initialize();
        }

        String townId = (String) (getValue("sTownIDxx") == null ? "" : getValue("sTownIDxx"));

        if (!"".equals(townId)){
            if (poTown.getEditMode() == EditMode.READY &&
                poTown.getTownId().equals(townId))
                return poTown;
            else{
                if (ReferenceCache.tryLoad("TownCity", townId, poTown)) {
                    return poTown;
                }

                poJSON = poTown.openRecord(townId);

                if ("success".equals((String) poJSON.get("result"))){
                    ReferenceCache.store("TownCity", townId, poTown);
                    return poTown;
                }
                else {
                    poTown.initialize();
                    return poTown;
                }
            }
        } else {
            poTown.initialize();
            return poTown;
        }
    }
    
    
    public JSONObject setBranchCode(String branchCode) {
        return setValue("sBranchCd", branchCode);
    }

    public String getBranchCode() {
        return (String) getValue("sBranchCd");
    }

    public JSONObject setBranchName(String modelCode) {
        return setValue("sBranchNm", modelCode);
    }

    public String getBranchName() {
        return (String) getValue("sBranchNm");
    }

    public JSONObject setDescription(String description) {
        return setValue("sDescript", description);
    }

    public String getDescription() {
        return (String) getValue("sDescript");
    }

    
    public JSONObject setCompanyId(String companyId) {
        return setValue("sCompnyID", companyId);
    }

    public String getCompanyId() {
        return (String) getValue("sCompnyID");
    }
    
    public JSONObject setIndustryCode(String industryCode) {
        return setValue("sIndstCdx", industryCode);
    }

    public String getIndustryCode() {
        return (String) getValue("sIndstCdx");
    }
    
    public JSONObject setAddress(String address) {
        return setValue("sAddressx", address);
    }

    public String getAddress() {
        return (String) getValue("sAddressx");
    }
    
    public JSONObject setTownId(String townId) {
        return setValue("sTownIDxx", townId);
    }

    public String getTownId() {
        return (String) getValue("sTownIDxx");
    }
    
    public JSONObject setManagerId(String managerId) {
        return setValue("sManagerx", managerId);
    }

    public String getManagerId() {
        return (String) getValue("sManagerx");
    }
    
    public JSONObject setSellerCode(String sellerCode) {
        return setValue("sSellCode", sellerCode);
    }

    public String getSellerCode() {
        return (String) getValue("sSellCode");
    }
    
    public JSONObject isWarehouse(boolean isWarehouse){
        return setValue("cWareHous", isWarehouse == true ? "1" : "0");
    }
    
    public boolean isWarehouse(){
        return "1".equals((String) getValue("cWareHous"));
    }
    
    public JSONObject isServiceCentre(boolean isServiceCentre){
        return setValue("cSrvcCntr", isServiceCentre == true ? "1" : "0");
    }
    
    public boolean isServiceCentre(){
        return "1".equals((String) getValue("cSrvcCntr"));
    }
    
    public JSONObject isAutomate(boolean isAutomate){
        return setValue("cAutomate", isAutomate == true ? "1" : "0");
    }
    
    public boolean isAutomate(){
        return "1".equals((String) getValue("cAutomate"));
    }
    
    public JSONObject isMainOffice(boolean isAutomate){
        return setValue("cMainOffc", isAutomate == true ? "1" : "0");
    }
    
    public boolean isMainOffice(){
        return "1".equals((String) getValue("cMainOffc"));
    }
    
    public JSONObject setLandLine(String landlineNo) {
        return setValue("sTelNumbr", landlineNo);
    }

    public String getLandLine() {
        return (String) getValue("sTelNumbr");
    }
    
    public JSONObject setMobile(String mobileNo) {
        return setValue("sContactx", mobileNo);
    }

    public String getMobile() {
        return (String) getValue("sContactx");
    }

    public JSONObject setRecordStatus(String recordStatus) {
        return setValue("cRecdStat", recordStatus);
    }

    public String getRecordStatus() {
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
    
    @Override
    public String getNextCode() {
        return "";
    }
}
