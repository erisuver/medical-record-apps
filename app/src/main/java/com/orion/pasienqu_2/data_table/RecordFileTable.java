package com.orion.pasienqu_2.data_table;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.models.BillingItemModel;
import com.orion.pasienqu_2.models.RecordFileModel;
import com.orion.pasienqu_2.models.SyncInfoModel;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class RecordFileTable {
    private SQLiteDatabase db;
    private ArrayList<RecordFileModel> records;
    private Context context;

    public RecordFileTable(Context context) {
        this.context = context;
        this.db = JApplication.getInstance().db;
        this.records = new ArrayList<RecordFileModel>();
    }

    private ContentValues setValues(RecordFileModel recordFileModel){
        ContentValues cv = new ContentValues();
        cv.put("uuid", recordFileModel.getUuid());
        cv.put("record_id", recordFileModel.getRecord_id());
        cv.put("record_uuid", recordFileModel.getRecord_uuid());
        cv.put("file_name", recordFileModel.getFile_name());
        cv.put("record_file", recordFileModel.getRecord_file());
        cv.put("mime_type", recordFileModel.getMime_type());
        cv.put("create_date", recordFileModel.getCreate_date());
        cv.put("write_date", recordFileModel.getWrite_date());
        return cv;
    }

    public void insert(RecordFileModel recordFileModel, boolean isSinc) {
        ContentValues cv = this.setValues(recordFileModel);
        this.db.insert("pasienqu_record_file", null, cv);

//        if (isSinc) {
//            Gson gson = new Gson();
//            String json = gson.toJson(recordFileModel);
//            SyncInfoModel syncInfoModel = new SyncInfoModel(0, "pasienqu.medical.record.file", json.getBytes(), "create", recordFileModel.getUuid());
//            JApplication.getInstance().syncInfoTable.insert(syncInfoModel);
//        }
//
//        this.reloadList();
    }

    public void update(RecordFileModel recordFileModel){
        ContentValues cv = this.setValues(recordFileModel);
        this.db.update("pasienqu_record_file", cv, "id = " + recordFileModel.getId(), null);

        Gson gson = new Gson();
        String json = gson.toJson(recordFileModel);
        SyncInfoModel syncInfoModel = new SyncInfoModel(0, "pasienqu.medical.record.file", json.getBytes(), "write", recordFileModel.getUuid());
        JApplication.getInstance().syncInfoTable.insert(syncInfoModel);

        this.reloadList();
    }

    public void deleteItem(ArrayList<RecordFileModel> recordFileModel){
        Gson gson = new Gson();
        String json = gson.toJson(recordFileModel);
        SyncInfoModel syncInfoModel = new SyncInfoModel(0, "pasienqu.medical.record.file", json.getBytes(), "delete", recordFileModel.get(0).getUuid());
        JApplication.getInstance().syncInfoTable.insert(syncInfoModel);

        this.db.delete("pasienqu_record_file", "" , null);
        this.reloadList();
    }

    public void delete(String recordUuid){
        this.db.delete("pasienqu_record_file",  "record_uuid = '" + recordUuid + "'", null);
        this.reloadList();
    }
    public boolean deleteAll() {
        this.db.delete("pasienqu_record_file", "", null);
        return true;
    }

    private void reloadList(){
        this.records.clear();
        Cursor cr = this.db.rawQuery("SELECT * from pasienqu_record_file", null);

        RecordFileModel tempData;
        if (cr != null && cr.moveToFirst()){
            do {
                tempData = new RecordFileModel(
                        cr.getInt(cr.getColumnIndexOrThrow("id")),
                        cr.getString(cr.getColumnIndexOrThrow("uuid")),
                        cr.getInt(cr.getColumnIndexOrThrow("record_id")),
                        cr.getString(cr.getColumnIndexOrThrow("record_uuid")),
                        cr.getString(cr.getColumnIndexOrThrow("file_name")),
                        cr.getString(cr.getColumnIndexOrThrow("record_file")),
                        cr.getString(cr.getColumnIndexOrThrow("mime_type")),
                        cr.getLong(cr.getColumnIndexOrThrow("create_date")),
                        cr.getLong(cr.getColumnIndexOrThrow("write_date"))
                );
                this.records.add(tempData);
            } while(cr.moveToNext());
        }
    }

    private void reloadList(long last_update){
        this.records.clear();
        Cursor cr = this.db.rawQuery("SELECT * from pasienqu_record_file WHERE write_date > "+last_update +" ORDER BY write_date", null);

        RecordFileModel tempData;
        if (cr != null && cr.moveToFirst()){
            do {
                tempData = new RecordFileModel(
                        cr.getInt(cr.getColumnIndexOrThrow("id")),
                        cr.getString(cr.getColumnIndexOrThrow("uuid")),
                        cr.getInt(cr.getColumnIndexOrThrow("record_id")),
                        cr.getString(cr.getColumnIndexOrThrow("record_uuid")),
                        cr.getString(cr.getColumnIndexOrThrow("file_name")),
                        cr.getString(cr.getColumnIndexOrThrow("record_file")),
                        cr.getString(cr.getColumnIndexOrThrow("mime_type")),
                        cr.getLong(cr.getColumnIndexOrThrow("create_date")),
                        cr.getLong(cr.getColumnIndexOrThrow("write_date"))
                );
                this.records.add(tempData);
            } while(cr.moveToNext());
        }
    }

    public RecordFileModel getDataByIndex(int index){
        return this.records.get(index);
    }

    public ArrayList<RecordFileModel> getRecords(){
        this.reloadList();
        return this.records;
    }

    public ArrayList<RecordFileModel> getRecords(long last_update){
        this.reloadList(last_update);
        return this.records;
    }


    public ArrayList<RecordFileModel> getRecordsById(int record_id) {
        this.records.clear();
        Cursor cr = this.db.rawQuery("SELECT * from pasienqu_record_file where record_id = "+record_id, null);

        RecordFileModel tempData;
        if (cr != null && cr.moveToFirst()) {
            do {
                tempData = new RecordFileModel(
                        cr.getInt(cr.getColumnIndexOrThrow("id")),
                        cr.getString(cr.getColumnIndexOrThrow("uuid")),
                        cr.getInt(cr.getColumnIndexOrThrow("record_id")),
                        cr.getString(cr.getColumnIndexOrThrow("record_uuid")),
                        cr.getString(cr.getColumnIndexOrThrow("file_name")),
                        "",
                        cr.getString(cr.getColumnIndexOrThrow("mime_type")),
                        cr.getLong(cr.getColumnIndexOrThrow("create_date")),
                        cr.getLong(cr.getColumnIndexOrThrow("write_date"))
                );
                String recordFile = setRecorfile(tempData.getId(), "", 1);
                tempData.setRecord_file(recordFile);
                this.records.add(tempData);
            } while (cr.moveToNext());
        }
        return this.records;
    }

    public ArrayList<RecordFileModel> getRecordsByUuid(String record_uuid) {
        this.records.clear();
        Cursor cr = this.db.rawQuery("SELECT id, uuid, record_id, record_uuid, file_name, mime_type from pasienqu_record_file where record_uuid = '" + record_uuid+"'", null);

        RecordFileModel tempData;
        if (cr != null && cr.moveToFirst()) {
            do {
                tempData = new RecordFileModel(
                        cr.getInt(cr.getColumnIndexOrThrow("id")),
                        cr.getString(cr.getColumnIndexOrThrow("uuid")),
                        cr.getInt(cr.getColumnIndexOrThrow("record_id")),
                        cr.getString(cr.getColumnIndexOrThrow("record_uuid")),
                        cr.getString(cr.getColumnIndexOrThrow("file_name")),
                        "",
                        cr.getString(cr.getColumnIndexOrThrow("mime_type")),
                        cr.getLong(cr.getColumnIndexOrThrow("create_date")),
                        cr.getLong(cr.getColumnIndexOrThrow("write_date"))
                );
                String recordFile = setRecorfile(tempData.getId(), "", 1);
                tempData.setRecord_file(recordFile);
                this.records.add(tempData);
            } while (cr.moveToNext());
        }
        return this.records;
    }

    private String setRecorfile(int id, String lastRecordFile, int idx){
        Cursor r = db.rawQuery("SELECT id, length(record_file) as leng FROM pasienqu_record_file WHERE id = "+id, null);
        int lengthCount = 2000000;
        r.moveToFirst();
        int startCount = 1;

        while (!r.isAfterLast()) {
            byte[] item = null;
            String hasil = "";
            int lengthImage = r.getInt(r.getColumnIndex("leng"));
            while (lengthImage > startCount) {
                Cursor r1;
                if (lengthImage > (startCount + lengthCount)) {
                    r1 = db.rawQuery("SELECT substr(record_file, " + startCount + ", " + lengthCount + ") as x FROM pasienqu_record_file WHERE id ='" + id + "'", null);
                } else {
                    r1 = db.rawQuery("SELECT substr(record_file, " + startCount + ", " + lengthCount + ") as x FROM pasienqu_record_file WHERE id ='" + id + "'", null);
                }
                r1.moveToFirst();
                if (!r1.isAfterLast()) {
                    if (item != null && item.length > 0) {
                        item = ByteBuffer.allocate(item.length + r1.getBlob(r1.getColumnIndex("x")).length).put(item).put(r1.getBlob(r1.getColumnIndex("x"))).array();
                        hasil += hasil +  r1.getString(r1.getColumnIndex("x"));
                    } else {
                        item = r1.getBlob(r1.getColumnIndex("x"));
                        hasil = r1.getString(r1.getColumnIndex("x"));
                    }
                }
                r1.close();
                startCount += lengthCount;
            }
            return hasil;
        }
        return null;
    }

    public int getCountFile (long lastBackup){
        String sql = "SELECT count(record_file) as jml FROM pasienqu_record_file "+
                " WHERE id <> 0 and write_date > "+lastBackup+
                " ORDER by write_date";
        Cursor cr = this.db.rawQuery(sql, null);
        if (cr != null && cr.moveToFirst()){
            return cr.getInt(cr.getColumnIndexOrThrow("jml"));
        }else{
            return 0;
        }
    }



}
