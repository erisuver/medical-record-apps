package com.orion.pasienqu_2.data_table;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.models.CountryModel;
import com.orion.pasienqu_2.models.SyncInfoModel;

import java.util.ArrayList;

public class CountryTable {
    private SQLiteDatabase db;
    private ArrayList<CountryModel> records;
    private Context context;

    public CountryTable(Context context) {
        this.context = context;
        this.db = JApplication.getInstance().db;
        this.records = new ArrayList<CountryModel>();
    }

    private ContentValues setValues(CountryModel countryModel){
        ContentValues cv = new ContentValues();
        cv.put("id", countryModel.getId());
        cv.put("uuid", countryModel.getUuid());
        cv.put("name", countryModel.getName());
        return cv;
    }

    public void insert(CountryModel countryModel, boolean isSinc) {
        ContentValues cv = this.setValues(countryModel);
        this.db.insert("pasienqu_country", null, cv);

        if (isSinc) {
            Gson gson = new Gson();
            String json = gson.toJson(countryModel);
            SyncInfoModel syncInfoModel = new SyncInfoModel(0, "pasienqu.country", json.getBytes(), "create", countryModel.getUuid());
            JApplication.getInstance().syncInfoTable.insert(syncInfoModel);
        }

        this.reloadList();
    }

    public void update(CountryModel countryModel){
        ContentValues cv = this.setValues(countryModel);
        this.db.update("pasienqu_country", cv, "id = " + countryModel.getId(), null);

        Gson gson = new Gson();
        String json = gson.toJson(countryModel);
        SyncInfoModel syncInfoModel = new SyncInfoModel(0, "pasienqu.country", json.getBytes(), "write", countryModel.getUuid());
        JApplication.getInstance().syncInfoTable.insert(syncInfoModel);

        this.reloadList();
    }

    private void reloadList(){
        this.records.clear();
        Cursor cr = this.db.rawQuery("SELECT * FROM pasienqu_country", null);

        CountryModel tempData;
        if (cr != null && cr.moveToFirst()){
            do {
                tempData = new CountryModel(
                        cr.getInt(cr.getColumnIndexOrThrow("id")),
                        cr.getString(cr.getColumnIndexOrThrow("uuid")),
                        cr.getString(cr.getColumnIndexOrThrow("name")),0,0
                );
                this.records.add(tempData);
            } while(cr.moveToNext());
        }
    }

    public CountryModel getDataByIndex(int index){
        return this.records.get(index);
    }

    public ArrayList<CountryModel> getRecords(){
        this.reloadList();
        return this.records;
    }


    public void deleteAll() {
        this.db.delete("pasienqu_country", null, null);
        this.reloadList();
    }

    public void delete(String uuid){
        this.db.delete("pasienqu_country",  "uuid = '" + uuid + "'", null);
        this.reloadList();
    }

}