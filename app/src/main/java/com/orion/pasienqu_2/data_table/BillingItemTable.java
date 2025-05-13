package com.orion.pasienqu_2.data_table;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.models.BillingItemModel;
import com.orion.pasienqu_2.models.BillingModel;
import com.orion.pasienqu_2.models.SyncInfoModel;

import java.util.ArrayList;
import java.util.UUID;

public class BillingItemTable{
    private SQLiteDatabase db;
    private ArrayList<BillingItemModel> records;
    private Context context;

    public BillingItemTable(Context context) {
        this.context = context;
//        this.db = new DBConn(context).getWritableDatabase();
        this.db = JApplication.getInstance().db;
        this.records = new ArrayList<BillingItemModel>();
    }

    private ContentValues setValues(BillingItemModel billingItemModel){
        ContentValues cv = new ContentValues();
        cv.put("uuid", billingItemModel.getUuid());
        cv.put("header_id", billingItemModel.getHeader_id());
        cv.put("header_uuid", billingItemModel.getHeader_uuid());
        cv.put("name", billingItemModel.getName());
        cv.put("amount", billingItemModel.getAmount());
        return cv;
    }

    public void insert(BillingItemModel billingItemModel, boolean isSinc) {
        ContentValues cv = this.setValues(billingItemModel);
        this.db.insert("pasienqu_billing_item", null, cv);

        if (isSinc) {
            Gson gson = new Gson();
            String json = gson.toJson(billingItemModel);
            SyncInfoModel syncInfoModel = new SyncInfoModel(0, "pasienqu.billing.item", json.getBytes(), "create", billingItemModel.getUuid());
            JApplication.getInstance().syncInfoTable.insert(syncInfoModel);
        }

        this.reloadList();
    }

    public void insert(BillingItemModel billingItemModel) {
        ContentValues cv = this.setValues(billingItemModel);
        this.db.insert("pasienqu_billing_item", null, cv);
        this.reloadList();
    }

    public void update(BillingItemModel billingItemModel){
        ContentValues cv = this.setValues(billingItemModel);
        this.db.update("pasienqu_billing_item", cv, "id = " + billingItemModel.getId(), null);

        Gson gson = new Gson();
        String json = gson.toJson(billingItemModel);
        SyncInfoModel syncInfoModel = new SyncInfoModel(0, "pasienqu.billing.item", json.getBytes(), "write", billingItemModel.getUuid());
        JApplication.getInstance().syncInfoTable.insert(syncInfoModel);

        this.reloadList();
    }

    public void deleteItem(ArrayList<BillingItemModel> billingItemModel){
        Gson gson = new Gson();
        String json = gson.toJson(billingItemModel);
        SyncInfoModel syncInfoModel = new SyncInfoModel(0, "pasienqu.billing.item", json.getBytes(), "delete", billingItemModel.get(0).getUuid());
        JApplication.getInstance().syncInfoTable.insert(syncInfoModel);

        this.db.delete("pasienqu_billing_item", "" , null);
        this.reloadList();
    }

    public void deletebyHeaderUuid(String headerUuid){
        this.db.delete("pasienqu_billing_item",  "header_uuid = '" + headerUuid + "'", null);
        this.reloadList();
    }
    public void deleteByHeaderId(int headerId){
        this.db.delete("pasienqu_billing_item",  "header_id = " + headerId , null);
        this.reloadList();
    }
    public boolean deleteAll() {
        this.db.delete("pasienqu_billing_item", "", null);
        return true;
    }
    public void delete(String uuid){
        this.db.delete("pasienqu_billing_item",  "uuid = '" + uuid + "'", null);
        this.reloadList();
    }

    private void reloadList(){
        this.records.clear();
        Cursor cr = this.db.rawQuery("SELECT id, uuid, header_id, header_uuid, name, amount FROM pasienqu_billing_item", null);

        BillingItemModel tempData;
        if (cr != null && cr.moveToFirst()){
            do {
                tempData = new BillingItemModel(
                        cr.getInt(cr.getColumnIndexOrThrow("id")),
                        cr.getString(cr.getColumnIndexOrThrow("uuid")),
                        cr.getInt(cr.getColumnIndexOrThrow("header_id")),
                        cr.getString(cr.getColumnIndexOrThrow("header_uuid")),
                        cr.getString(cr.getColumnIndexOrThrow("name")),
                        cr.getDouble(cr.getColumnIndexOrThrow("amount"))
                );
                this.records.add(tempData);
            } while(cr.moveToNext());
        }
    }

    public BillingItemModel getDataByIndex(int index){
        return this.records.get(index);
    }

    public ArrayList<BillingItemModel> getRecords(){
        this.reloadList();
        return this.records;
    }


    public ArrayList<BillingItemModel> getRecordsByUuid(String header_uuid) {
        this.records.clear();
        Cursor cr = this.db.rawQuery("SELECT id, uuid, header_id, header_uuid, name, amount FROM pasienqu_billing_item where header_uuid = '" + header_uuid+"'", null);

        BillingItemModel tempData;
        if (cr != null && cr.moveToFirst()) {
            do {
                tempData = new BillingItemModel(
                        cr.getInt(cr.getColumnIndexOrThrow("id")),
                        cr.getString(cr.getColumnIndexOrThrow("uuid")),
                        cr.getInt(cr.getColumnIndexOrThrow("header_id")),
                        cr.getString(cr.getColumnIndexOrThrow("header_uuid")),
                        cr.getString(cr.getColumnIndexOrThrow("name")),
                        cr.getDouble(cr.getColumnIndexOrThrow("amount"))
                );
                this.records.add(tempData);
            } while (cr.moveToNext());
        }
        return this.records;
    }

    public ArrayList<BillingItemModel> getRecordsById(int header_id) {
        this.records.clear();
        Cursor cr = this.db.rawQuery("SELECT id, uuid, header_id, header_uuid, name, amount FROM pasienqu_billing_item where header_id = " + header_id, null);

        BillingItemModel tempData;
        if (cr != null && cr.moveToFirst()) {
            do {
                tempData = new BillingItemModel(
                        cr.getInt(cr.getColumnIndexOrThrow("id")),
                        cr.getString(cr.getColumnIndexOrThrow("uuid")),
                        cr.getInt(cr.getColumnIndexOrThrow("header_id")),
                        cr.getString(cr.getColumnIndexOrThrow("header_uuid")),
                        cr.getString(cr.getColumnIndexOrThrow("name")),
                        cr.getDouble(cr.getColumnIndexOrThrow("amount"))
                );
                this.records.add(tempData);
            } while (cr.moveToNext());
        }
        return this.records;
    }

    public BillingItemModel getRecordByHeaderId(int headerId){
        Cursor cr = this.db.rawQuery("SELECT * FROM pasienqu_billing_item WHERE header_id = "+headerId, null);

        BillingItemModel tempData = new BillingItemModel();
        if (cr != null && cr.moveToFirst()){
            tempData = new BillingItemModel(
                    cr.getInt(cr.getColumnIndexOrThrow("id")),
                    cr.getString(cr.getColumnIndexOrThrow("uuid")),
                    cr.getInt(cr.getColumnIndexOrThrow("header_id")),
                    cr.getString(cr.getColumnIndexOrThrow("header_uuid")),
                    cr.getString(cr.getColumnIndexOrThrow("name")),
                    cr.getDouble(cr.getColumnIndexOrThrow("amount"))
            );
        }
        return tempData;
    }

}