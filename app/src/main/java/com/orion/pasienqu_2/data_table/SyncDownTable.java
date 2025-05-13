package com.orion.pasienqu_2.data_table;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.models.SyncDownModel;

import java.util.ArrayList;

public class SyncDownTable {
    private SQLiteDatabase db;
    private ArrayList<SyncDownModel> records;
    private Context context;

    public SyncDownTable(Context context) {
        this.context = context;
        this.db = JApplication.getInstance().db;
        this.records = new ArrayList<SyncDownModel>();
    }

    private ContentValues setValues(SyncDownModel syncDownModel){
        ContentValues cv = new ContentValues();
        cv.put("model", syncDownModel.getModel());
        cv.put("last_update", syncDownModel.getLast_update());
        return cv;
    }

    public void insert(SyncDownModel SyncDownModel) {
        ContentValues cv = this.setValues(SyncDownModel);
        this.db.insert("sync_down", null, cv);
        this.reloadList();
    }

    public void update(SyncDownModel SyncDownModel, String model){
        ContentValues cv = this.setValues(SyncDownModel);
        this.db.update("sync_down", cv, "model = '"+model+"'", null);
        this.reloadList();
    }

    public void update_isSync_all(String isSync){
        ContentValues cv = new ContentValues();
        cv.put("is_sync", isSync);
        this.db.update("sync_down", cv, "", null);
        this.reloadList();
    }

    public void update_isSync(String isSync, String model){
        ContentValues cv = new ContentValues();
        cv.put("is_sync", isSync);
        this.db.update("sync_down", cv, "model = '"+model+"'", null);
        this.reloadList();
    }

    private void reloadList(){
        this.records.clear();
        Cursor cr = this.db.rawQuery("SELECT id, model, last_update FROM sync_down where is_sync = 'T'", null);

        SyncDownModel tempData;
        if (cr != null && cr.moveToFirst()){
            do {
                tempData = new SyncDownModel(
                        cr.getInt(cr.getColumnIndexOrThrow("id")),
                        cr.getString(cr.getColumnIndexOrThrow("model")),
                        cr.getLong(cr.getColumnIndexOrThrow("last_update"))
                );
                this.records.add(tempData);
            } while(cr.moveToNext());
        }
    }


    private void reloadListAll(){
        this.records.clear();
        Cursor cr = this.db.rawQuery("SELECT id, model, last_update FROM sync_down ", null);

        SyncDownModel tempData;
        if (cr != null && cr.moveToFirst()){
            do {
                tempData = new SyncDownModel(
                        cr.getInt(cr.getColumnIndexOrThrow("id")),
                        cr.getString(cr.getColumnIndexOrThrow("model")),
                        cr.getLong(cr.getColumnIndexOrThrow("last_update"))
                );
                this.records.add(tempData);
            } while(cr.moveToNext());
        }
    }

    public SyncDownModel getDataByIndex(int index){
        return this.records.get(index);
    }

    public ArrayList<SyncDownModel> getRecords(){
        this.reloadList();
        return this.records;
    }
    public ArrayList<SyncDownModel> getRecordsAll(){
        this.reloadListAll();
        return this.records;
    }

    public void deleteAll() {
        this.db.delete("sync_down", null, null);
        this.reloadList();
    }

    public void delete(int id) {
        this.db.delete("sync_down", "id = "+id, null);
        this.reloadList();
    }

    public SyncDownModel getRecord(String model){
        Cursor cr = this.db.rawQuery("SELECT id, model, last_update FROM sync_down where model = '"+model+"'", null);

        SyncDownModel tempData = new SyncDownModel();
        if (cr != null && cr.moveToFirst()){
            do {
                tempData = new SyncDownModel(
                        cr.getInt(cr.getColumnIndexOrThrow("id")),
                        cr.getString(cr.getColumnIndexOrThrow("model")),
                        cr.getLong(cr.getColumnIndexOrThrow("last_update"))
                );
                this.records.add(tempData);
            } while(cr.moveToNext());
        }
        return tempData;
    }
    public void delete(String model) {
        this.db.delete("sync_down", "model = '"+model+"'", null);
        this.reloadList();
    }

    public long getLastUpdate(String model) {
        Cursor cr = this.db.rawQuery("SELECT last_update FROM sync_down where model = '"+model+"'", null);
        long lastUpdate = 0;
        if (cr != null && cr.moveToFirst()){
            do {
                lastUpdate = cr.getLong(cr.getColumnIndexOrThrow("last_update"));
            } while(cr.moveToNext());
        }
        return lastUpdate;
    }

    public int getTotalSync(){
        String sql = "SELECT count(*) as jml FROM sync_down where id <> 0 and is_sync = 'T'";
        Cursor cr = this.db.rawQuery(sql, null);
        if (cr != null && cr.moveToFirst()){
            return cr.getInt(cr.getColumnIndexOrThrow("jml"));
        }else{
            return 0;
        }
    }
}