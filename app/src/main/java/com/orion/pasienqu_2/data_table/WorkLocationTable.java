package com.orion.pasienqu_2.data_table;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.google.gson.Gson;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.models.SyncInfoModel;
import com.orion.pasienqu_2.models.WorkLocationModel;

import java.util.ArrayList;

public class WorkLocationTable {
    private SQLiteDatabase db;
    private ArrayList<WorkLocationModel> records;
    private Context context;
    private boolean archived = false;

    public WorkLocationTable(Context context) {
        this.context = context;
        this.db = JApplication.getInstance().db;
        this.records = new ArrayList<WorkLocationModel>();
        Global.setLanguage(context);  // handle bug bahasa pada inform
    }

    private ContentValues setValues(WorkLocationModel workLocationModel, boolean isSave){
        ContentValues cv = new ContentValues();
        cv.put("uuid", workLocationModel.getUuid());
        cv.put("name", workLocationModel.getName());
        cv.put("location", workLocationModel.getLocation());
        cv.put("remarks", workLocationModel.getRemarks());
        cv.put("organization_ihs", workLocationModel.getOrganization_ihs());
        cv.put("location_ihs", workLocationModel.getLocation_ihs());
        cv.put("client_id", workLocationModel.getClient_id());
        cv.put("client_secret", workLocationModel.getClient_secret());
        cv.put("last_generate_token", workLocationModel.getLast_generate_token());
        cv.put("token", workLocationModel.getToken());
        if (isSave){
            cv.put("active", "true");
        }
        return cv;
    }

    private boolean validate(WorkLocationModel workLocationModel) {
        int count;

        if (workLocationModel.getName().equals("") || workLocationModel.getLocation().equals("")){
            Toast.makeText(context, context.getString(R.string.field_must_be_fill), Toast.LENGTH_LONG).show();
            return false;
        }

        count = Global.getCount(db, "pasienqu_work_location"," name = '" + workLocationModel.getName() + "' and uuid <> '"+workLocationModel.getUuid()+"'");
        if (count > 0 ) {
            Toast.makeText(context, context.getString(R.string.name_must_be_unique), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public boolean insert(WorkLocationModel workLocationModel , boolean isSinc) {
        if (isSinc) {
            if (!validate(workLocationModel)) {
                return false;
            }
            ContentValues cv = this.setValues(workLocationModel, true);
            this.db.insert("pasienqu_work_location", null, cv);
        }else{
            ContentValues cv = this.setValues(workLocationModel, true);
            cv.put("id", workLocationModel.getId());
            this.db.insert("pasienqu_work_location", null, cv);
        }

//        ContentValues cv = this.setValues(workLocationModel);
//        this.db.insert("pasienqu_work_location", null, cv);

        if (isSinc) {
            Gson gson = new Gson();
            String json = gson.toJson(workLocationModel);
            SyncInfoModel syncInfoModel = new SyncInfoModel(0, "pasienqu.work.location", json.getBytes(), "create", workLocationModel.getUuid());
            JApplication.getInstance().syncInfoTable.insert(syncInfoModel);
        }

        this.reloadList();
        return true;
    }

    public boolean update(WorkLocationModel workLocationModel){
        if (!validate(workLocationModel)) {
            return false;
        }
        ContentValues cv = this.setValues(workLocationModel, false);
        this.db.update("pasienqu_work_location", cv, "id = " + workLocationModel.getId(), null);

        Gson gson = new Gson();
        String json = gson.toJson(workLocationModel);
        SyncInfoModel syncInfoModel = new SyncInfoModel(0, "pasienqu.work.location", json.getBytes(), "write", workLocationModel.getUuid());
        JApplication.getInstance().syncInfoTable.insert(syncInfoModel);

        this.reloadList();
        return true;
    }

    private void reloadList(){
        this.records.clear();
        Cursor cr = this.db.rawQuery("SELECT * FROM pasienqu_work_location where id <> 0 "+addFilter(), null);

        WorkLocationModel tempData;
        if (cr != null && cr.moveToFirst()){
            do {
                tempData = new WorkLocationModel(
                        cr.getInt(cr.getColumnIndexOrThrow("id")),
                        cr.getString(cr.getColumnIndexOrThrow("uuid")),
                        cr.getString(cr.getColumnIndexOrThrow("name")),
                        cr.getString(cr.getColumnIndexOrThrow("location")),
                        cr.getString(cr.getColumnIndexOrThrow("remarks"))
                );
                tempData.setOrganization_ihs(cr.getString(cr.getColumnIndexOrThrow("organization_ihs")));
                tempData.setLocation_ihs(cr.getString(cr.getColumnIndexOrThrow("location_ihs")));
                tempData.setClient_id(cr.getString(cr.getColumnIndexOrThrow("client_id")));
                tempData.setClient_secret(cr.getString(cr.getColumnIndexOrThrow("client_secret")));
                tempData.setLast_generate_token(cr.getLong(cr.getColumnIndexOrThrow("last_generate_token")));
                this.records.add(tempData);
            } while(cr.moveToNext());
        }
    }

    public WorkLocationModel getDataByIndex(int index){
        return this.records.get(index);
    }

    public WorkLocationModel getDataByUuid(String uuid){
        Cursor cr = this.db.rawQuery("SELECT * FROM pasienqu_work_location where uuid = '"+uuid+"'", null);
        WorkLocationModel tempData;
        if (cr != null && cr.moveToFirst()){
            tempData = new WorkLocationModel(
                    cr.getInt(cr.getColumnIndexOrThrow("id")),
                    cr.getString(cr.getColumnIndexOrThrow("uuid")),
                    cr.getString(cr.getColumnIndexOrThrow("name")),
                    cr.getString(cr.getColumnIndexOrThrow("location")),
                    cr.getString(cr.getColumnIndexOrThrow("remarks"))
            );
            tempData.setOrganization_ihs(cr.getString(cr.getColumnIndexOrThrow("organization_ihs")));
            tempData.setLocation_ihs(cr.getString(cr.getColumnIndexOrThrow("location_ihs")));
            tempData.setClient_id(cr.getString(cr.getColumnIndexOrThrow("client_id")));
            tempData.setClient_secret(cr.getString(cr.getColumnIndexOrThrow("client_secret")));
            tempData.setLast_generate_token(cr.getLong(cr.getColumnIndexOrThrow("last_generate_token")));
            return tempData;
        }else{
            return null;
        }
    }

    public WorkLocationModel getDataById(int id){
        Cursor cr = this.db.rawQuery("SELECT * FROM pasienqu_work_location where id = "+id, null);
        WorkLocationModel tempData;
        if (cr != null && cr.moveToFirst()){
            tempData = new WorkLocationModel(
                    cr.getInt(cr.getColumnIndexOrThrow("id")),
                    cr.getString(cr.getColumnIndexOrThrow("uuid")),
                    cr.getString(cr.getColumnIndexOrThrow("name")),
                    cr.getString(cr.getColumnIndexOrThrow("location")),
                    cr.getString(cr.getColumnIndexOrThrow("remarks"))
            );
            tempData.setOrganization_ihs(cr.getString(cr.getColumnIndexOrThrow("organization_ihs")));
            tempData.setLocation_ihs(cr.getString(cr.getColumnIndexOrThrow("location_ihs")));
            tempData.setClient_id(cr.getString(cr.getColumnIndexOrThrow("client_id")));
            tempData.setClient_secret(cr.getString(cr.getColumnIndexOrThrow("client_secret")));
            tempData.setLast_generate_token(cr.getLong(cr.getColumnIndexOrThrow("last_generate_token")));
            return tempData;
        }else{
            return null;
        }
    }

    public String getLocationById(int id){
        Cursor cr = this.db.rawQuery("SELECT name FROM pasienqu_work_location where id = "+id, null);

        String hsl = "";
        if (cr != null && cr.moveToFirst()) {
            hsl = cr.getString(cr.getColumnIndexOrThrow("name"));
            cr.close();
        }
        return hsl;
    }

    public String getLocationByUuId(String uuid){
        Cursor cr = this.db.rawQuery("SELECT * FROM pasienqu_work_location where uuid = '"+uuid+"'", null);

        if (cr != null && cr.moveToFirst()){
            return cr.getString(cr.getColumnIndexOrThrow("name"));
        }else{
            return "";
        }
    }

    public int getLocationIDByUuId(String uuid){
        Cursor cr = this.db.rawQuery("SELECT * FROM pasienqu_work_location where uuid = '"+uuid+"'", null);

        if (cr != null && cr.moveToFirst()){
            return cr.getInt(cr.getColumnIndexOrThrow("id"));
        }else{
            return 0;
        }
    }

    public ArrayList<WorkLocationModel> getRecords(){
        this.reloadList();
        return this.records;
    }


    public void deleteAll() {
        this.db.delete("pasienqu_work_location", null, null);
        this.reloadList();
    }

    public void delete(String uuid){
        this.db.delete("pasienqu_work_location",  "uuid = '" + uuid + "'", null);
        this.reloadList();
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

}