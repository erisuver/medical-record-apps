package com.orion.pasienqu_2.data_table;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.models.RecordDiagnosaModel;
import com.orion.pasienqu_2.models.SyncInfoModel;

import java.util.ArrayList;

public class RecordDiagnosaTable {
    private SQLiteDatabase db;
    private ArrayList<RecordDiagnosaModel> records;
    private Context context;

    public RecordDiagnosaTable(Context context) {
        this.context = context;
        this.db = JApplication.getInstance().db;
        this.records = new ArrayList<RecordDiagnosaModel>();
    }

    private ContentValues setValues(RecordDiagnosaModel recordDiagnosaModel){
        ContentValues cv = new ContentValues();
        cv.put("uuid", recordDiagnosaModel.getUuid());
        cv.put("record_id", recordDiagnosaModel.getRecord_id());
        cv.put("record_uuid", recordDiagnosaModel.getRecord_uuid());
        cv.put("icd_code", recordDiagnosaModel.getIcd_code());
        cv.put("icd_name", recordDiagnosaModel.getIcd_name());
        cv.put("remarks", recordDiagnosaModel.getRemarks());
        return cv;
    }

    public void insert(RecordDiagnosaModel recordDiagnosaModel, boolean isSinc) {
        ContentValues cv = this.setValues(recordDiagnosaModel);
        this.db.insert("pasienqu_record_diagnosa", null, cv);

        if (isSinc) {
            Gson gson = new Gson();
            String json = gson.toJson(recordDiagnosaModel);
            SyncInfoModel syncInfoModel = new SyncInfoModel(0, "pasienqu.medical.record.diagnosa", json.getBytes(), "create", recordDiagnosaModel.getUuid());
            JApplication.getInstance().syncInfoTable.insert(syncInfoModel);
        }

        this.reloadList();
    }

    public void update(RecordDiagnosaModel recordDiagnosaModel){
        ContentValues cv = this.setValues(recordDiagnosaModel);
        this.db.update("pasienqu_record_diagnosa", cv, "id = " + recordDiagnosaModel.getId(), null);

        Gson gson = new Gson();
        String json = gson.toJson(recordDiagnosaModel);
        SyncInfoModel syncInfoModel = new SyncInfoModel(0, "pasienqu.medical.record.diagnosa", json.getBytes(), "write", recordDiagnosaModel.getUuid());
        JApplication.getInstance().syncInfoTable.insert(syncInfoModel);

        this.reloadList();
    }

    public void delete(String recordUuid){
        this.db.delete("pasienqu_record_diagnosa",  "record_uuid = '" + recordUuid + "'", null);
        this.reloadList();
    }
    public boolean deleteAll() {
        this.db.delete("pasienqu_record_diagnosa", "", null);
        return true;
    }

    private void reloadList(){
        this.records.clear();
        Cursor cr = this.db.rawQuery("SELECT * from pasienqu_record_diagnosa", null);

        RecordDiagnosaModel tempData;
        if (cr != null && cr.moveToFirst()){
            do {
                tempData = new RecordDiagnosaModel(
                        cr.getInt(cr.getColumnIndexOrThrow("id")),
                        cr.getString(cr.getColumnIndexOrThrow("uuid")),
                        cr.getInt(cr.getColumnIndexOrThrow("record_id")),
                        cr.getString(cr.getColumnIndexOrThrow("record_uuid")),
                        cr.getString(cr.getColumnIndexOrThrow("icd_code")),
                        cr.getString(cr.getColumnIndexOrThrow("icd_name")),
                        cr.getString(cr.getColumnIndexOrThrow("remarks")),
                        cr.getString(cr.getColumnIndexOrThrow("id_condition"))
                );
                this.records.add(tempData);
            } while(cr.moveToNext());
        }
    }

    public RecordDiagnosaModel getDataByIndex(int index){
        return this.records.get(index);
    }

    public ArrayList<RecordDiagnosaModel> getRecords(){
        this.reloadList();
        return this.records;
    }


    public ArrayList<RecordDiagnosaModel> getRecordsById(String record_uuid) {
        this.records.clear();
        String sql = "SELECT * from pasienqu_record_diagnosa where record_uuid = '" + record_uuid+"'";
        Cursor cr = this.db.rawQuery(sql, null);

        RecordDiagnosaModel tempData;
        if (cr != null && cr.moveToFirst()) {
            do {
                tempData = new RecordDiagnosaModel(
                        cr.getInt(cr.getColumnIndexOrThrow("id")),
                        cr.getString(cr.getColumnIndexOrThrow("uuid")),
                        cr.getInt(cr.getColumnIndexOrThrow("record_id")),
                        cr.getString(cr.getColumnIndexOrThrow("record_uuid")),
                        cr.getString(cr.getColumnIndexOrThrow("icd_code")),
                        cr.getString(cr.getColumnIndexOrThrow("icd_name")),
                        cr.getString(cr.getColumnIndexOrThrow("remarks")),
                        cr.getString(cr.getColumnIndexOrThrow("id_condition"))
                );
                this.records.add(tempData);
            } while (cr.moveToNext());
        }
        return this.records;
    }

}