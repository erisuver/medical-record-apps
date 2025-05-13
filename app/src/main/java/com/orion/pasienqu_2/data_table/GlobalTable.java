package com.orion.pasienqu_2.data_table;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.ListValue;
import com.orion.pasienqu_2.globals.SyncUp;
import com.orion.pasienqu_2.models.AppointmentModel;
import com.orion.pasienqu_2.models.BillingModel;
import com.orion.pasienqu_2.models.GenderModel;
import com.orion.pasienqu_2.models.GlobalModel;
import com.orion.pasienqu_2.models.SyncInfoModel;

import java.util.ArrayList;
import java.util.List;

public class GlobalTable {
    private SQLiteDatabase db;
    private Context context;

    public GlobalTable(Context context) {
        this.context = context;
        this.db = JApplication.getInstance().db;
    }

    public boolean isArchived(String tableName, String uuid){
        ContentValues cv = new ContentValues();
        Cursor cr = this.db.rawQuery("select active from "+tableName+" where uuid = '"+uuid+"'", null);

        if (cr != null && cr.moveToFirst()){
            if (cr.getString(cr.getColumnIndexOrThrow("active")).equals("false")){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }


    public boolean archive(String tableName, String uuid, String modelName){
        Cursor c = this.db.rawQuery("update "+tableName+" set active = 'false' where uuid = '"+uuid+"'", null);
        c.moveToFirst();
        c.close();
//        ContentValues cv = new ContentValues();
//        cv.put("archived", "true");
//        this.db.update(tableName, cv, "uuid = '" + uuid, null);
        GlobalModel globalModel = new GlobalModel(uuid, false);
        Gson gson = new Gson();
        String json = gson.toJson(globalModel);
        SyncInfoModel syncInfoModel = new SyncInfoModel(0, modelName, json.getBytes(), "write", globalModel.getUuid());
        JApplication.getInstance().syncInfoTable.insert(syncInfoModel);
        return true;
    }

    public boolean archiveUnarchive(String tableName, String whereClause, String setActive){
        Cursor c = this.db.rawQuery("update "+tableName+" set active = '"+setActive+"' "+whereClause, null);
        c.moveToFirst();
        c.close();
        return true;
    }


    public boolean unarchive(String tableName, String uuid, String modelName){
//        ContentValues cv = new ContentValues();
//        cv.put("archived", "false");
//
//        this.db.update(tableName, cv, "uuid = '" + uuid, null);
        Cursor c = this.db.rawQuery("update "+tableName+" set active = 'true' where uuid = '"+uuid+"'", null);
        c.moveToFirst();
        c.close();

        GlobalModel globalModel = new GlobalModel(uuid, true);
        Gson gson = new Gson();
        String json = gson.toJson(globalModel);
        SyncInfoModel syncInfoModel = new SyncInfoModel(0, modelName, json.getBytes(), "write", globalModel.getUuid());
        JApplication.getInstance().syncInfoTable.insert(syncInfoModel);
        return true;
    }

    private void deleteArchive(String tableName){
        Cursor c = this.db.rawQuery("delete from "+tableName+" where active = 'false'", null);
        c.moveToFirst();
        c.close();
    }

    public void deleteAllArchive(){
        List<String>listTable = ListValue.list_table_contain_archive(context);
        for (int i = 0; i < listTable.size(); i++){
            deleteArchive(listTable.get(i));
        }
    }

    public boolean saveTemp(String tableName, String uuid){
        Cursor c = this.db.rawQuery("update "+tableName+" set is_temp = 'true' where uuid = '"+uuid+"'", null);
        c.moveToFirst();
        c.close();
        return true;
    }


    public boolean unSaveTemp(String tableName, String uuid){
        Cursor c = this.db.rawQuery("update "+tableName+" set is_temp = 'false' where uuid = '"+uuid+"'", null);
        c.moveToFirst();
        c.close();
        return true;
    }

    public void updateTotalBillingMedicalRecord(int recordId, double totalBilling){
        Cursor c = this.db.rawQuery("update pasienqu_record set total_billing = "+totalBilling+" where id = "+recordId, null);
        c.moveToFirst();
        c.close();
    }

    public void updateMedicalRecordId(int recordId, String billingUuid){
        Cursor c = this.db.rawQuery("update pasienqu_billing set medical_record_id = "+recordId+" where uuid = '"+billingUuid+"'", null);
        c.moveToFirst();
        c.close();
    }

    public boolean isTemp(String tableName, String uuid){
        Cursor cr = this.db.rawQuery("select is_temp from "+tableName+" where uuid = '"+uuid+"'", null);

        if (cr != null && cr.moveToFirst()){
            if (cr.getString(cr.getColumnIndexOrThrow("is_temp")).equals("false")){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }


    public boolean archivePatient (String patientUuid, int patientId, String recordUuid, int recordId){
        archive("pasienqu_patient", patientUuid, "pasienqu.patient");
        archive("pasienqu_record", recordUuid, "pasienqu.medical.record");

        archiveUnarchive("pasienqu_billing", "where medical_record_id = "+recordId, "false");
        archiveUnarchive("pasienqu_appointment", "where patient_id = "+patientId, "false");
        archiveUnarchive("pasienqu_billing", "where patient_id = "+patientId, "false");
        return true;
    }

    public boolean unArchivePatient (String patientUuid, int patientId, String recordUuid, int recordId){
        unarchive("pasienqu_patient", patientUuid, "pasienqu.patient");
        unarchive("pasienqu_record", recordUuid, "pasienqu.medical.record");

        archiveUnarchive("pasienqu_billing", "where medical_record_id = "+recordId, "true");
        archiveUnarchive("pasienqu_appointment", "where patient_id = "+patientId, "true");
        archiveUnarchive("pasienqu_billing", "where patient_id = "+patientId, "true");
        return true;
    }

    public boolean UpdateTable(String tableName, String field, String whereClause){
        Cursor c = JApplication.getInstance().db.rawQuery("UPDATE "+tableName+" SET "+field+" "+whereClause, null);
        c.moveToFirst();
        c.close();
        return true;
    }

    public int GetIntegerFromTable(String tableName, String field, String where){
        int result = 0;
        String sql = "SELECT "+field+" as field FROM "+tableName;
        if (!where.equals("")){
            sql = sql + " where "+where;
        }
        Cursor cr = JApplication.getInstance().db.rawQuery(sql, null);
        if (cr != null && cr.moveToFirst()){
            result = cr.getInt(cr.getColumnIndexOrThrow("field"));
        }
        return result;
    }

    public String GetStringFromTable(String tableName, String field, String where){
        String result = "";
        String sql = "SELECT "+field+" as field FROM "+tableName;
        if (!where.equals("")){
            sql = sql + " "+where;
        }
        Cursor cr = JApplication.getInstance().db.rawQuery(sql, null);
        if (cr != null && cr.moveToFirst()){
            result = cr.getString(cr.getColumnIndexOrThrow("field"));
        }
        return result;
    }

    public boolean UpdateLocationIdSatuSehat(String uuid, String idSatuSehat){
        try{
            String sql = "UPDATE pasienqu_work_location SET id_satu_sehat = '"+idSatuSehat+"' WHERE uuid = '"+uuid+"'";
            Cursor c = db.rawQuery(sql, null);
            c.moveToFirst();
            c.close();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean UpdatePatientIdSatuSehat(String uuid, String idSatuSehat){
        try{
            String sql = "UPDATE pasienqu_patient SET id_satu_sehat = '"+idSatuSehat+"' WHERE uuid = '"+uuid+"'";
            Cursor c = db.rawQuery(sql, null);
            c.moveToFirst();
            c.close();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean UpdateRecordEncounterID(String uuid, String idEncounter, String idCondition){
        try{
            String sql = "UPDATE pasienqu_record SET " +
                    "id_encounter = '"+idEncounter+
                    "' WHERE uuid = '"+uuid+"'";
            Cursor c = db.rawQuery(sql, null);
            c.moveToFirst();
            c.close();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public String getTokenSatuSehat(){
        String result = "";
        String sql = "SELECT token as field FROM satu_sehat_token";
        Cursor cr = db.rawQuery(sql, null);
        if (cr != null && cr.moveToFirst()){
            result = cr.getString(cr.getColumnIndexOrThrow("field"));
        }
        return result;
    }

    public long getLastGenerateTokenSatuSehat(){
        long result = 0;
        String sql = "SELECT last_update as field FROM satu_sehat_token";
        Cursor cr = db.rawQuery(sql, null);
        if (cr != null && cr.moveToFirst()){
            result = cr.getLong(cr.getColumnIndexOrThrow("field"));
        }
        return result;
    }

    public String getPractitionerID(){
        String result = "";
        String sql = "SELECT nomor_ihs as field FROM practitioner";
        Cursor cr = db.rawQuery(sql, null);
        if (cr != null && cr.moveToFirst()){
            result = cr.getString(cr.getColumnIndexOrThrow("field"));
        }
        return result;
    }

    public String getMaxClientId(){
        String result = "";
        String sql = "SELECT client_id as field FROM pasienqu_work_location where client_id is not null order by id limit 1";
        Cursor cr = db.rawQuery(sql, null);
        if (cr != null && cr.moveToFirst()){
            result = cr.getString(cr.getColumnIndexOrThrow("field"));
        }
        return result;
    }

    public String getMaxClientSecret(){
        String result = "";
        String sql = "SELECT client_secret as field FROM pasienqu_work_location where client_secret is not null order by id limit 1";
        Cursor cr = db.rawQuery(sql, null);
        if (cr != null && cr.moveToFirst()){
            result = cr.getString(cr.getColumnIndexOrThrow("field"));
        }
        return result;
    }

    public int getCount(String tableName, String where) {
        String sql = "SELECT count(*) as jml FROM " + tableName;
        if (!where.equals("")) {
            sql = sql + " where " + where;
        }
        int result = 0;
        Cursor cr = db.rawQuery(sql, null);
        if (cr != null && cr.moveToFirst()) {
            result = cr.getInt(cr.getColumnIndexOrThrow("jml"));
        }
        return result;
    }

    public long getLastMedicalRecordDate(){
        long result = 0;
        String sql = "SELECT create_date as field FROM pasienqu_record order by create_date desc";
        Cursor cr = db.rawQuery(sql, null);
        if (cr != null && cr.moveToFirst()){
            result = cr.getLong(cr.getColumnIndexOrThrow("field"));
        }
        return result;
    }
}