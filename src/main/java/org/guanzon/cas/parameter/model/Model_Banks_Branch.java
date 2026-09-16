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

public class Model_Banks_Branch extends Model {
private Model_Banks poBanks;
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
            
            //poBanks/poTown are intentionally NOT constructed here - see Banks()/TownCity() below,
            //which build them lazily on first access so opening this record never touches the
            //Banks/TownCity tables.

            pnEditMode = EditMode.UNKNOWN;
        } catch (SQLException e) {
            logwrapr.severe(e.getMessage());
            System.exit(1);
        }
    }
    
    public Model_Banks Banks() throws SQLException, GuanzonException{
        if (poBanks == null) {
            poBanks = new Model_Banks();
            poBanks.setApplicationDriver(poGRider);
            poBanks.setXML("Model_Banks");
            poBanks.setTableName("Banks");
            poBanks.initialize();
        }

        String bankId = (String) (getValue("sBankIDxx") == null ? "" : getValue("sBankIDxx"));

        if (!"".equals(bankId)){
            if (poBanks.getEditMode() == EditMode.READY &&
                poBanks.getBankID().equals(bankId))
                return poBanks;
            else{
                if (ReferenceCache.tryLoad("Banks", bankId, poBanks)) {
                    return poBanks;
                }

                poJSON = poBanks.openRecord(bankId);

                if ("success".equals((String) poJSON.get("result"))){
                    ReferenceCache.store("Banks", bankId, poBanks);
                    return poBanks;
                }
                else {
                    poBanks.initialize();
                    return poBanks;
                }
            }
        } else {
            poBanks.initialize();
            return poBanks;
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
    
    public JSONObject setBranchBankID(String branchbankId) {
        return setValue("sBrBankID", branchbankId);
    }

    public String getBranchBankID() {
        return (String) getValue("sBrBankID");
    }

    public JSONObject setBranchBankName(String branchBankName) {
        return setValue("sBrBankNm", branchBankName);
    }

    public String getBranchBankName() {
        return (String) getValue("sBrBankNm");
    }
    
    public JSONObject setBranchBankCode(String branchbankCode) {
        return setValue("sBrBankCD", branchbankCode);
    }

    public String getBranchBankCode() {
        return (String) getValue("sBrBankCD");
    }

    public JSONObject setBankID(String bankId) {
        return setValue("sBankIDxx", bankId);
    }

    public String getBankID() {
        return (String) getValue("sBankIDxx");
    }
    
    public JSONObject setContactPerson(String contactPerson) {
        return setValue("sContactP", contactPerson);
    }

    public String getContactPerson() {
        return (String) getValue("sContactP");
    }
    
    public JSONObject setAddress(String adress) {
        return setValue("sAddressx", adress);
    }

    public String getAddress() {
        return (String) getValue("sAddressx");
    }
    
    public JSONObject setTownID(String townID) {
        return setValue("sTownIDxx", townID);
    }

    public String getTownID() {
        return (String) getValue("sTownIDxx");
    }
    
    public JSONObject setTelephoneNo(String telephoneNo) {
        return setValue("sTelNoxxx", telephoneNo);
    }

    public String getTelephoneNo() {
        return (String) getValue("sTelNoxxx");
    }
    
    public JSONObject setFaxNo(String faxNo) {
        return setValue("sFaxNoxxx", faxNo);
    }

    public String getFaxNo() {
        return (String) getValue("sFaxNoxxx");
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
        return MiscUtil.getNextCode(getTable(), ID, true, poGRider.getGConnection().getConnection(), poGRider.getBranchCode());
    }
}
