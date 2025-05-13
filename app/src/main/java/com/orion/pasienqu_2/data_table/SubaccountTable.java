package com.orion.pasienqu_2.data_table;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Patterns;
import android.widget.Toast;

import com.google.gson.Gson;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.models.SubaccountModel;
import com.orion.pasienqu_2.models.SyncInfoModel;

import java.util.ArrayList;
import java.util.UUID;

public class SubaccountTable {
    private SQLiteDatabase db;
    private ArrayList<SubaccountModel> records;
    private Context context;
    private boolean archived = false;

    public SubaccountTable(Context context) {
        this.db = db;
        this.db = JApplication.getInstance().db;
        this.records = new ArrayList<SubaccountModel>();
        this.context = context;
    }

    public ContentValues setValues (SubaccountModel subaccountModel) {
        ContentValues cv = new ContentValues();
        cv.put("uuid", subaccountModel.getUuid());
        cv.put("name", subaccountModel.getName());
        cv.put("login", subaccountModel.getLogin());
        cv.put("password", subaccountModel.getPassword());
        cv.put("active", "true");

        return cv;
    }

    public boolean insert(SubaccountModel subaccountModel, boolean isSinc) {
        if (isSinc) {
            if (!validate(subaccountModel)) {
                return false;
            }
        }
        ContentValues cv = this.setValues(subaccountModel);
        this.db.insert("pasienqu_subaccount", null, cv);
        this.reloadList();

        if (isSinc) {
            Gson gson = new Gson();
            String json = gson.toJson(subaccountModel);
            SyncInfoModel syncInfoModel = new SyncInfoModel(0, "res.users", json.getBytes(), "create", subaccountModel.getUuid());
            JApplication.getInstance().syncInfoTable.insert(syncInfoModel);
        }

        return true;
    }

    public boolean update(SubaccountModel subaccountModel, boolean isSinc) {
        if (isSinc) {
            if (!validate(subaccountModel)) {
                return false;
            }
        }
        ContentValues cv = this.setValues(subaccountModel);
        this.db.update("pasienqu_subaccount", cv, "id = " + subaccountModel.getId(), null);
        this.reloadList();

        if (isSinc) {
            Gson gson = new Gson();
            String json = gson.toJson(subaccountModel);
            SyncInfoModel syncInfoModel = new SyncInfoModel(0, "res.users", json.getBytes(), "write", subaccountModel.getUuid());
            JApplication.getInstance().syncInfoTable.insert(syncInfoModel);
        }

        return true;
    }

    public void deleteAll() {
        this.db.delete("pasienqu_subaccount", null, null);
        this.reloadList();
    }

    public void delete(String uuid){
        this.db.delete("pasienqu_subaccount",  "uuid = '" + uuid + "'", null);
        this.reloadList();
    }

    private void reloadList(){
        this.records.clear();
        Cursor cr = this.db.rawQuery("SELECT * FROM pasienqu_subaccount", null);

        SubaccountModel tempData;
        if (cr != null && cr.moveToFirst()){
            do {
                tempData = new SubaccountModel(
                        cr.getInt(cr.getColumnIndexOrThrow("id")),
                        cr.getString(cr.getColumnIndexOrThrow("uuid")),
                        cr.getString(cr.getColumnIndexOrThrow("name")),
                        cr.getString(cr.getColumnIndexOrThrow("login")),
                        cr.getString(cr.getColumnIndexOrThrow("password"))
                );
                this.records.add(tempData);
            } while(cr.moveToNext());
        }

    }

    private boolean validate(SubaccountModel subaccountModel){
        int count;
        if (subaccountModel.getName().equals("") || subaccountModel.getLogin().equals("") || subaccountModel.getPassword().equals("") ||
                !Patterns.EMAIL_ADDRESS.matcher(subaccountModel.getLogin()).matches() || subaccountModel.getPassword().length() < 6){
            Toast.makeText(context, context.getString(R.string.field_must_be_fill), Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    public ArrayList<SubaccountModel> getRecords() {
        this.reloadList();
        return this.records;
    }


    public SubaccountModel getDataByIndex(int index){
        return this.records.get(index);
    }


    public SubaccountModel getDataByUuid(String uuid){
        Cursor cr = this.db.rawQuery("SELECT * FROM pasienqu_subaccount where uuid = '"+uuid+"'", null);

        SubaccountModel tempData;
        if (cr != null && cr.moveToFirst()){
            tempData = new SubaccountModel(
                    cr.getInt(cr.getColumnIndexOrThrow("id")),
                    cr.getString(cr.getColumnIndexOrThrow("uuid")),
                    cr.getString(cr.getColumnIndexOrThrow("name")),
                    cr.getString(cr.getColumnIndexOrThrow("login")),
                    cr.getString(cr.getColumnIndexOrThrow("password"))
            );
            return tempData;
        }else{
            return null;
        }
    }


    private String addFilter(){
        String filter = "";

        if (archived){
            filter += " and active = 'false'";
        }else{
            filter += " and active = 'true'";
        }
        return filter;
    }

    public void setFilter( boolean archived){
        this.archived = archived;
    }

    public boolean isArchived() {
        return archived;
    }

}
