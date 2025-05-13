package com.orion.pasienqu_2.data_table;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.models.GenderModel;
import com.orion.pasienqu_2.models.ICDModel;
import com.orion.pasienqu_2.models.SyncInfoModel;

import java.util.ArrayList;

public class ICDTable {
    private SQLiteDatabase db;
    private ArrayList<ICDModel> records;
    private Context context;

    public ICDTable(Context context) {
        this.context = context;
        this.db = JApplication.getInstance().db;
        this.records = new ArrayList<ICDModel>();
    }

    private ContentValues setValues(ICDModel iCDModel){
        ContentValues cv = new ContentValues();
        cv.put("id", iCDModel.getId());
        cv.put("uuid", iCDModel.getUuid());
        cv.put("code", iCDModel.getCode());
        cv.put("remarks", iCDModel.getRemarks());
        cv.put("name", iCDModel.getName());
        return cv;
    }

    public void insert(ICDModel icdModel, boolean isSinc ){
        ContentValues cv = this.setValues(icdModel);
        this.db.insert("pasienqu_icd10", null, cv);

//        if (isSinc) {
//            Gson gson = new Gson();
//            String json = gson.toJson(icdModel);
//            SyncInfoModel syncInfoModel = new SyncInfoModel(0, "pasienqu.icd10", json.getBytes(), "create", icdModel.getUuid());
//            JApplication.getInstance().syncInfoTable.insert(syncInfoModel);
//        }

//        this.reloadList();
    }

    public void update(ICDModel icdModel){
        ContentValues cv = this.setValues(icdModel);
        this.db.update("pasienqu_icd10", cv, "id = " + icdModel.getId(), null);

        Gson gson = new Gson();
        String json = gson.toJson(icdModel);
        SyncInfoModel syncInfoModel = new SyncInfoModel(0, "pasienqu.icd10", json.getBytes(), "write", icdModel.getUuid());
        JApplication.getInstance().syncInfoTable.insert(syncInfoModel);

        this.reloadList();
    }

    private void reloadList(){
        this.records.clear();
        Cursor cr = this.db.rawQuery("SELECT * FROM pasienqu_icd10", null);

        ICDModel tempData;
        if (cr != null && cr.moveToFirst()){
            do {
                tempData = new ICDModel(
                        cr.getInt(cr.getColumnIndexOrThrow("id")),
                        cr.getString(cr.getColumnIndexOrThrow("uuid")),
                        cr.getString(cr.getColumnIndexOrThrow("code")),
                        cr.getString(cr.getColumnIndexOrThrow("name")),
                        cr.getString(cr.getColumnIndexOrThrow("remarks"))
                );
                this.records.add(tempData);
            } while(cr.moveToNext());
        }
    }

    private void reloadList(String name){
        this.records.clear();
        Cursor cr = this.db.rawQuery("SELECT * FROM pasienqu_icd10 where (name like '%"+name.replace("'", "''")+"%' OR code like '%"+name.replace("'", "''")+"%')", null);

        ICDModel tempData;
        if (cr != null && cr.moveToFirst()){
            do {
                tempData = new ICDModel(
                        cr.getInt(cr.getColumnIndexOrThrow("id")),
                        cr.getString(cr.getColumnIndexOrThrow("uuid")),
                        cr.getString(cr.getColumnIndexOrThrow("code")),
                        cr.getString(cr.getColumnIndexOrThrow("name")),
                        cr.getString(cr.getColumnIndexOrThrow("remarks"))
                );
                this.records.add(tempData);
            } while(cr.moveToNext());
        }
    }

    public ICDModel getDataByIndex(int index){
        return this.records.get(index);
    }

    public ArrayList<ICDModel> getRecords(){
        this.reloadList();
        return this.records;
    }

    public ArrayList<ICDModel> getRecords(String name){
        this.reloadList(name);
        return this.records;
    }


    public void deleteAll() {
        this.db.delete("pasienqu_icd10", null, null);
        this.reloadList();
    }

    public void delete(String uuid){
        this.db.delete("pasienqu_icd10",  "uuid = '" + uuid + "'", null);
        this.reloadList();
    }

    public Integer getTotalDatas(){
        int hsl = 0;
        String filter = "";
        Cursor cr = this.db.rawQuery("SELECT count(*) FROM pasienqu_icd10 " , null);

        if (cr != null && cr.moveToFirst()) {
            hsl = cr.getInt(0);
        }
        return hsl;
    }

}