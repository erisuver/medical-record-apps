package com.orion.pasienqu_2.data_table;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.models.SatuSehatTokenModel;

import java.util.ArrayList;

public class SatuSehatTokenTable {
    private SQLiteDatabase db;
    private ArrayList<SatuSehatTokenModel> records;
    private Context context;
    private boolean archived = false;

    public SatuSehatTokenTable(Context context) {
        this.db = JApplication.getInstance().db;
        this.records = new ArrayList<SatuSehatTokenModel>();
        this.context = context;
    }

    public ContentValues setValues (SatuSehatTokenModel satuSehatTokenModel) {
        ContentValues cv = new ContentValues();
        cv.put("token", satuSehatTokenModel.getToken());
        cv.put("last_update", satuSehatTokenModel.getLast_update());

        return cv;
    }

    public boolean insert(SatuSehatTokenModel satuSehatTokenModel) {
        if (!validate(satuSehatTokenModel)) {
            return false;
        }
        ContentValues cv = this.setValues(satuSehatTokenModel);
        this.db.insert("satu_sehat_token", null, cv);
        return true;
    }

    public boolean update(SatuSehatTokenModel satuSehatTokenModel) {

        if (!validate(satuSehatTokenModel)) {
            return false;
        }

        ContentValues cv = this.setValues(satuSehatTokenModel);
        this.db.update("satu_sehat_token", cv, "id = " + satuSehatTokenModel.getId(), null);
        return true;
    }

    public void deleteAll() {
        this.db.delete("satu_sehat_token", null, null);
        this.reloadList();
    }

    public void delete(String uuid){
        this.db.delete("satu_sehat_token",  "uuid = '" + uuid + "'", null);
        this.reloadList();
    }

    private void reloadList(){
        this.records.clear();
        Cursor cr = this.db.rawQuery("SELECT * FROM satu_sehat_token", null);

        SatuSehatTokenModel tempData;
        if (cr != null && cr.moveToFirst()){
            do {
                tempData = new SatuSehatTokenModel(
                        cr.getInt(cr.getColumnIndexOrThrow("id")),
                        cr.getString(cr.getColumnIndexOrThrow("token")),
                        cr.getLong(cr.getColumnIndexOrThrow("last_update"))
                );
                this.records.add(tempData);
            } while(cr.moveToNext());
        }

    }


    public SatuSehatTokenModel getDataByUuid(String uuid){
        Cursor cr = this.db.rawQuery("SELECT * FROM satu_sehat_token where uuid = '"+uuid+"'", null);

        SatuSehatTokenModel tempData;
        if (cr != null && cr.moveToFirst()){
            tempData = new SatuSehatTokenModel(
                    cr.getInt(cr.getColumnIndexOrThrow("id")),
                    cr.getString(cr.getColumnIndexOrThrow("token")),
                    cr.getLong(cr.getColumnIndexOrThrow("last_update"))
            );
            return tempData;
        }else{
            return null;
        }
    }

    private boolean validate(SatuSehatTokenModel satuSehatTokenModel){
        if (satuSehatTokenModel.getLast_update() == 0 || satuSehatTokenModel.getToken().equals("")){
            Toast.makeText(context, context.getString(R.string.field_must_be_fill), Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    public ArrayList<SatuSehatTokenModel> getRecords() {
        this.reloadList();
        return this.records;
    }


    public SatuSehatTokenModel getDataByIndex(int index){
        return this.records.get(index);
    }

}
