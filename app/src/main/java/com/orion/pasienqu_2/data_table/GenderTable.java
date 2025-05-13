package com.orion.pasienqu_2.data_table;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.models.GenderModel;
import com.orion.pasienqu_2.models.SyncInfoModel;

import java.util.ArrayList;

public class GenderTable {
    private SQLiteDatabase db;
    private ArrayList<GenderModel> records;
    private Context context;

    public GenderTable(Context context) {
        this.context = context;
        this.db = JApplication.getInstance().db;
        this.records = new ArrayList<GenderModel>();
    }

    private ContentValues setValues(GenderModel genderModel){
        ContentValues cv = new ContentValues();
        cv.put("id", genderModel.getId());
        cv.put("uuid", genderModel.getUuid());
        cv.put("name", genderModel.getName());
        return cv;
    }

    public void insert(GenderModel genderModel, boolean isSinc ){
        ContentValues cv = this.setValues(genderModel);
        this.db.insert("pasienqu_gender", null, cv);

        if (isSinc) {
            Gson gson = new Gson();
            String json = gson.toJson(genderModel);
            SyncInfoModel syncInfoModel = new SyncInfoModel(0, "pasienqu.gender", json.getBytes(), "create", genderModel.getUuid());
            JApplication.getInstance().syncInfoTable.insert(syncInfoModel);
        }

        this.reloadList();
    }

    public void update(GenderModel genderModel){
        ContentValues cv = this.setValues(genderModel);
        this.db.update("pasienqu_gender", cv, "id = " + genderModel.getId(), null);

        Gson gson = new Gson();
        String json = gson.toJson(genderModel);
        SyncInfoModel syncInfoModel = new SyncInfoModel(0, "pasienqu.gender", json.getBytes(), "write", genderModel.getUuid());
        JApplication.getInstance().syncInfoTable.insert(syncInfoModel);

        this.reloadList();
    }

    private void reloadList(){
        this.records.clear();
        Cursor cr = this.db.rawQuery("SELECT * FROM pasienqu_gender", null);

        GenderModel tempData;
        if (cr != null && cr.moveToFirst()){
            do {
                tempData = new GenderModel(
                        cr.getInt(cr.getColumnIndexOrThrow("id")),
                        cr.getString(cr.getColumnIndexOrThrow("uuid")),
                        cr.getString(cr.getColumnIndexOrThrow("name"))
                );
                this.records.add(tempData);
            } while(cr.moveToNext());
        }
    }

    public GenderModel getDataByIndex(int index){
        return this.records.get(index);
    }

    public ArrayList<GenderModel> getRecords(){
        this.reloadList();
        return this.records;
    }


    public void deleteAll() {
        this.db.delete("pasienqu_gender", null, null);
        this.reloadList();
    }

    public void delete(String uuid){
        this.db.delete("pasienqu_gender",  "uuid = '" + uuid + "'", null);
        this.reloadList();
    }


}