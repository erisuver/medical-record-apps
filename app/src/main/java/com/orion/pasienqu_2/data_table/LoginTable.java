package com.orion.pasienqu_2.data_table;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.models.LoginModel;

import java.util.ArrayList;


public class LoginTable {
    private SQLiteDatabase db;
    private ArrayList<LoginModel> records;
    private Context context;

    public LoginTable(Context context) {
        this.context = context;
//        this.db = new DBConn(context).getWritableDatabase();
        this.db = JApplication.getInstance().db;
        this.records = new ArrayList<LoginModel>();
    }

    private ContentValues setValues(LoginModel loginModel){
        ContentValues cv = new ContentValues();
//        cv.put("user_id", loginModel.getUser_id());
//        cv.put("contract_id", loginModel.getContract_id());
//        cv.put("driver_id", loginModel.getDriver_id());
//        cv.put("vehicle_id", loginModel.getVehicle_id());
//        cv.put("cust_name", loginModel.getCust_name());
//        cv.put("contract_no", loginModel.getContract_no());
//        cv.put("start_date", loginModel.getStart_date());
//        cv.put("end_date", loginModel.getEnd_date());

       /*//erik tutup odoo 040325
       OdooUser odooUser = loginModel.getOdooUser();
        cv.put("uid", odooUser.uid);
        cv.put("companyId", odooUser.companyId);
        cv.put("partnerId", odooUser.partnerId);
        cv.put("name", odooUser.name);
        cv.put("username", odooUser.username);
        cv.put("lang", odooUser.lang);
        cv.put("tz", odooUser.tz);
        cv.put("tz", odooUser.tz);
        cv.put("db", odooUser.database);
        cv.put("fcmProjectId", odooUser.fcmProjectId);
        cv.put("sessionId", odooUser.sessionId);
        cv.put("isSuperuser", odooUser.isSuperuser);*/
        return cv;
    }

    public void insert(LoginModel loginModel) {
        ContentValues cv = this.setValues(loginModel);
        this.db.insert("login", null, cv);


    }

    public void update(LoginModel loginModel){
        ContentValues cv = this.setValues(loginModel);
        this.db.update("login", cv, "", null);
        this.reloadList();
    }

    private void reloadList(){
        this.records.clear();
        Cursor cr = this.db.rawQuery("SELECT * FROM login", null);

        LoginModel tempData;
        if (cr != null && cr.moveToFirst()){
            do {
//                tempData = new LoginModel(
//                        cr.getString(cr.getColumnIndexOrThrow("user_id")),
//                        cr.getInt(cr.getColumnIndexOrThrow("contract_id")),
//                        cr.getInt(cr.getColumnIndexOrThrow("driver_id")),
//                        cr.getInt(cr.getColumnIndexOrThrow("vehicle_id")),
//                        cr.getString(cr.getColumnIndexOrThrow("cust_name")),
//                        cr.getString(cr.getColumnIndexOrThrow("contract_no")),
//                        cr.getLong(cr.getColumnIndexOrThrow("start_date")),
//                        cr.getLong(cr.getColumnIndexOrThrow("end_date"))
//                );
                /*//erik tutup odoo 040325
                OdooUser odooUser = new OdooUser();
                odooUser.uid = cr.getInt(cr.getColumnIndexOrThrow("uid"));
                odooUser.companyId = cr.getInt(cr.getColumnIndexOrThrow("companyId"));
                odooUser.partnerId = cr.getInt(cr.getColumnIndexOrThrow("partnerId"));
                odooUser.name = cr.getString(cr.getColumnIndexOrThrow("name"));
                odooUser.username = cr.getString(cr.getColumnIndexOrThrow("username"));
                odooUser.lang = cr.getString(cr.getColumnIndexOrThrow("lang"));
                odooUser.tz = cr.getString(cr.getColumnIndexOrThrow("tz"));
                odooUser.database = cr.getString(cr.getColumnIndexOrThrow("db"));
                odooUser.fcmProjectId = cr.getString(cr.getColumnIndexOrThrow("fcmProjectId"));
                odooUser.sessionId = cr.getString(cr.getColumnIndexOrThrow("sessionId"));
                odooUser.isSuperuser = cr.getString(cr.getColumnIndexOrThrow("isSuperuser")).equals("true") ;*/
//                this.records.add(tempData);
            } while(cr.moveToNext());
        }
    }

    public LoginModel getDataByIndex(int index){
        return this.records.get(index);
    }

    public ArrayList<LoginModel> getRecords(){
        this.reloadList();
        return this.records;
    }

    public LoginModel getRecord(){

        this.records.clear();
        Cursor cr = this.db.rawQuery("SELECT * FROM login", null);

        LoginModel tempData = new LoginModel();
        if (cr != null && cr.moveToFirst()){
//            tempData = new LoginModel(
//                    cr.getString(cr.getColumnIndexOrThrow("user_id")),
//                    cr.getInt(cr.getColumnIndexOrThrow("contract_id")),
//                    cr.getInt(cr.getColumnIndexOrThrow("driver_id")),
//                    cr.getInt(cr.getColumnIndexOrThrow("vehicle_id")),
//                    cr.getString(cr.getColumnIndexOrThrow("cust_name")),
//                    cr.getString(cr.getColumnIndexOrThrow("contract_no")),
//                    cr.getLong(cr.getColumnIndexOrThrow("start_date")),
//                    cr.getLong(cr.getColumnIndexOrThrow("end_date"))
//            );
            /*//erik tutup odoo 040325
            OdooUser odooUser = new OdooUser();
            odooUser.uid = cr.getInt(cr.getColumnIndexOrThrow("uid"));
            odooUser.companyId = cr.getInt(cr.getColumnIndexOrThrow("companyId"));
            odooUser.partnerId = cr.getInt(cr.getColumnIndexOrThrow("partnerId"));
            odooUser.name = cr.getString(cr.getColumnIndexOrThrow("name"));
            odooUser.username = cr.getString(cr.getColumnIndexOrThrow("username"));
            odooUser.lang = cr.getString(cr.getColumnIndexOrThrow("lang"));
            odooUser.tz = cr.getString(cr.getColumnIndexOrThrow("tz"));
            odooUser.database = cr.getString(cr.getColumnIndexOrThrow("db"));
            odooUser.fcmProjectId = cr.getString(cr.getColumnIndexOrThrow("fcmProjectId"));
            odooUser.sessionId = cr.getString(cr.getColumnIndexOrThrow("sessionId"));
            odooUser.isSuperuser = cr.getString(cr.getColumnIndexOrThrow("isSuperuser")).equals("true") ;
            tempData.setOdooUser(odooUser);*/
        }
        return tempData;
    }

    public void deleteAll() {
        this.db.delete("login", null, null);
        this.reloadList();
    }
}