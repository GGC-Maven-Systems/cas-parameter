package org.guanzon.cas.parameter;

import java.sql.SQLException;
import java.util.regex.Pattern;
import org.guanzon.appdriver.agent.ShowDialogFX;
import org.guanzon.appdriver.agent.services.Parameter;
import org.guanzon.appdriver.agent.services.ReferenceCache;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.Logical;
import org.guanzon.appdriver.constant.UserRight;
import org.guanzon.cas.parameter.model.Model_Supplier_Email;
import org.guanzon.cas.parameter.services.ParamModels;
import org.json.simple.JSONObject;

public class SupplierEmail extends Parameter{
    private static final Pattern EMAIL_FORMAT =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)+$");

    Model_Supplier_Email poModel;

    @Override
    public void initialize() throws SQLException, GuanzonException{
        psRecdStat = Logical.YES;

        poModel = new ParamModels(poGRider).SupplierEmail();

        super.initialize();
    }

    @Override
    public JSONObject isEntryOkay() throws SQLException {
        poJSON = new JSONObject();

//        if (poGRider.getUserLevel() < UserRight.SYSADMIN){
//            poJSON.put("result", "error");
//            poJSON.put("message", "User is not allowed to save record.");
//            return poJSON;
//        }

        if (poModel.getClientId() == null || poModel.getClientId().trim().isEmpty()){
            poJSON.put("result", "error");
            poJSON.put("message", "Supplier must not be empty.");
            return poJSON;
        }

        if (poModel.getEmailAddress() == null || poModel.getEmailAddress().trim().isEmpty()){
            poJSON.put("result", "error");
            poJSON.put("message", "Email address must not be empty.");
            return poJSON;
        }

        String lsEmail = poModel.getEmailAddress().trim();
        if (!EMAIL_FORMAT.matcher(lsEmail).matches()){
            poJSON.put("result", "error");
            poJSON.put("message", "Invalid email address format.");
            return poJSON;
        }
        poModel.setEmailAddress(lsEmail);

        //sModified holds 12 characters, so the user ID is saved as is
        poModel.setModifyingId(poGRider.getUserID());
        poModel.setModifiedDate(poGRider.getServerDate());

        poJSON.put("result", "success");
        return poJSON;
    }

    @Override
    public Model_Supplier_Email getModel() {
        return poModel;
    }

    @Override
    protected void saveComplete() {
        //drop any cached snapshot now that the record has changed
        ReferenceCache.invalidate("Supplier_Email", poModel.getClientId());
    }

    @Override
    public JSONObject searchRecord(String value, boolean byCode) throws SQLException, GuanzonException{
        return browse(getSQ_Browse(), value, byCode);
    }

    /** Searches the email addresses of one supplier. */
    public JSONObject searchRecord(String value, boolean byCode, String clientId) throws SQLException, GuanzonException{
        String lsSQL = MiscUtil.addCondition(getSQ_Browse(), "a.sClientID = " + SQLUtil.toSQL(clientId));

        return browse(lsSQL, value, byCode);
    }

    private JSONObject browse(String sql, String value, boolean byCode) throws SQLException, GuanzonException{
        poJSON = ShowDialogFX.Search(poGRider,
                sql,
                value,
                "Supplier ID»Email Address»Contact Person",
                "a.sClientID»a.sEMailAdd»a.sContactP",
                "a.sClientID»a.sEMailAdd»a.sContactP",
                byCode ? 0 : 1);

        if (poJSON != null) {
            return poModel.openRecordByEmail((String) poJSON.get("sClientID"),
                    (String) poJSON.get("sEMailAdd"));
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
                "  a.sClientID" +
                ", a.sIndstCdx" +
                ", a.sCategrCd" +
                ", a.sEMailAdd" +
                ", a.nEntryNox" +
                ", a.sContactP" +
                ", a.cRecpient" +
                ", a.cRecdStat" +
                " FROM Supplier_Email a";

        return MiscUtil.addCondition(lsSQL, lsCondition);
    }
}