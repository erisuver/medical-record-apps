package com.orion.pasienqu_2.data_table;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.globals.SyncOdoo;
import com.orion.pasienqu_2.globals.SyncUp;
import com.orion.pasienqu_2.models.SyncInfoModel;

import java.util.ArrayList;

public class SyncInfoTable {
    private SQLiteDatabase db;
    private ArrayList<SyncInfoModel> records;
    private Context context;

    public SyncInfoTable(Context context) {
        this.context = context;
        this.db = JApplication.getInstance().db;
        this.records = new ArrayList<SyncInfoModel>();
    }

    private ContentValues setValues(SyncInfoModel syncInfoModel){
        ContentValues cv = new ContentValues();
        cv.put("model", syncInfoModel.getModel());
        cv.put("content", syncInfoModel.getContent());
        cv.put("command", syncInfoModel.getCommand());
        cv.put("uuid_model", syncInfoModel.getUuidModel());
        return cv;
    }

    public void insert(SyncInfoModel SyncInfoModel) {
        //tutup fungsi sync & dirty
//        ContentValues cv = this.setValues(SyncInfoModel);
//        this.db.insert("sync_info", null, cv);
        return;
    }

    public void update(SyncInfoModel SyncInfoModel){
        ContentValues cv = this.setValues(SyncInfoModel);
        this.db.update("sync_info", cv, "", null);
        this.reloadList();
    }

    private void reloadList(){
        this.records.clear();
        Cursor cr = this.db.rawQuery("SELECT id, model, content, command, uuid_model FROM sync_info order by id ", null);

        SyncInfoModel tempData;
        if (cr != null && cr.moveToFirst()){
            do {
                tempData = new SyncInfoModel(
                        cr.getInt(cr.getColumnIndexOrThrow("id")),
                        cr.getString(cr.getColumnIndexOrThrow("model")),
                        cr.getBlob(cr.getColumnIndexOrThrow("content")),
                        cr.getString(cr.getColumnIndexOrThrow("command")),
                        cr.getString(cr.getColumnIndexOrThrow("uuid_model"))
                );
                this.records.add(tempData);
            } while(cr.moveToNext());
        }
    }

    public SyncInfoModel getDataByIndex(int index){
        return this.records.get(index);
    }

    public ArrayList<SyncInfoModel> getRecords(){
        this.reloadList();
        return this.records;
    }

    public void deleteAll() {
        this.db.delete("sync_info", null, null);
        this.reloadList();
    }

    public void delete(int id) {
        this.db.delete("sync_info", "id = "+id, null);
        this.reloadList();
    }

}
