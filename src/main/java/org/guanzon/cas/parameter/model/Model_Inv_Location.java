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

public class Model_Inv_Location extends Model {
    private Model_Warehouse poWarehouse;
    private Model_Section poSection;
    
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
            
            //poWarehouse/poSection are intentionally NOT constructed here - see Warehouse()/
            //Section() below, which build them lazily on first access so opening this record
            //never touches the Warehouse/Section tables.
            
            pnEditMode = EditMode.UNKNOWN;
        } catch (SQLException e) {
            logwrapr.severe(e.getMessage());
            System.exit(1);
        }
    }
    
    public JSONObject setLocationId(String locationId) {
        return setValue("sLocatnID", locationId);
    }

    public String getLocationId() {
        return (String) getValue("sLocatnID");
    }

    public JSONObject setDescription(String description) {
        return setValue("sDescript", description);
    }

    public String getDescription() {
        return (String) getValue("sDescript");
    }
    
    public JSONObject setWarehouseId(String warehouseId) {
        return setValue("sWHouseID", warehouseId);
    }

    public String getWarehouseId() {
        return (String) getValue("sWHouseID");
    }
    
    public JSONObject setSectionId(String sectionId) {
        return setValue("sSectnIDx", sectionId);
    }

    public String getSectionId() {
        return (String) getValue("sSectnIDx");
    }
    
    public JSONObject setRecordStatus(String recordStatus){
        return setValue("cRecdStat", recordStatus);
    }

    public String getRecordStatus() {
        return (String) getValue("cRecdStat");
    }

    public JSONObject setModifyingId(String modifyingId) {
        return setValue("sModified", modifyingId);
    }

    public String getModifyingId() {
        return (String) getValue("sModified");
    }

    public JSONObject setModifiedDate(Date modifiedDate) {
        return setValue("dModified", modifiedDate);
    }

    public Date getModifiedDate() {
        return (Date) getValue("dModified");
    }
    
    @Override
    public String getNextCode() {
        return MiscUtil.getNextCode(getTable(), ID, false, poGRider.getGConnection().getConnection(), "");
    }
    
    public Model_Warehouse Warehouse() throws SQLException, GuanzonException{
        if (poWarehouse == null) {
            poWarehouse = new Model_Warehouse();
            poWarehouse.setApplicationDriver(poGRider);
            poWarehouse.setXML("Model_Warehouse");
            poWarehouse.setTableName("Warehouse");
            poWarehouse.initialize();
        }        
        
        String warehouseId = (String) (getValue("sWHouseID") == null ? "" : getValue("sWHouseID"));

        if (!"".equals(warehouseId)){
            if (poWarehouse.getEditMode() == EditMode.READY &&
                poWarehouse.getWarehouseId().equals(warehouseId))
                return poWarehouse;
            else{
                if (ReferenceCache.tryLoad("Warehouse", warehouseId, poWarehouse)) {
                    return poWarehouse;
                }

                poJSON = poWarehouse.openRecord(warehouseId);

                if ("success".equals((String) poJSON.get("result"))){
                    ReferenceCache.store("Warehouse", warehouseId, poWarehouse);
                    return poWarehouse;
                }
                else {
                    poWarehouse.initialize();
                    return poWarehouse;
                }
            }
        } else {
            poWarehouse.initialize();
            return poWarehouse;
        }
    }

    public Model_Section Section() throws SQLException, GuanzonException{
        if (poSection == null) {
            poSection = new Model_Section();
            poSection.setApplicationDriver(poGRider);
            poSection.setXML("Model_Section");
            poSection.setTableName("Section");
            poSection.initialize();
        }

        String sectionId = (String) (getValue("sSectnIDx") == null ? "" : getValue("sSectnIDx"));

        if (!"".equals(sectionId)){
            if (poSection.getEditMode() == EditMode.READY &&
                poSection.getSectionId().equals(sectionId))
                return poSection;
            else{
                if (ReferenceCache.tryLoad("Section", sectionId, poSection)) {
                    return poSection;
                }

                poJSON = poSection.openRecord(sectionId);

                if ("success".equals((String) poJSON.get("result"))){
                    ReferenceCache.store("Section", sectionId, poSection);
                    return poSection;
                }
                else {
                    poSection.initialize();
                    return poSection;
                }
            }
        } else {
            poSection.initialize();
            return poSection;
        }
    }
}
