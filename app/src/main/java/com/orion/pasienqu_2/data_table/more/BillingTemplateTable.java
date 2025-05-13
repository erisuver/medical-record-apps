package com.orion.pasienqu_2.data_table.more;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.google.gson.Gson;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.JConst;
import com.orion.pasienqu_2.models.BillingTemplateModel;
import com.orion.pasienqu_2.models.SyncInfoModel;
import com.orion.pasienqu_2.models.more.NoteTemplateModel;

import java.util.ArrayList;

public class BillingTemplateTable {
    private SQLiteDatabase db;
    private ArrayList<BillingTemplateModel> records;
    private Context context;
    private boolean archived = false;
    private String valueSort = "";

    public BillingTemplateTable(Context context) {
        this.context = context;
        this.db = db;
        this.db = JApplication.getInstance().db;
        this.records = new ArrayList<BillingTemplateModel>();
        Global.setLanguage(context);  // handle bug bahasa pada inform
    }

    public ContentValues setValues (BillingTemplateModel billingTemplateModel, boolean isSave) {
        ContentValues cv = new ContentValues();
        cv.put("uuid", billingTemplateModel.getUuid());
        cv.put("name", billingTemplateModel.getName());
        cv.put("items", billingTemplateModel.getItems());
        if (isSave){
            cv.put("active", "true");
        }

        return cv;
    }

    private boolean validate(BillingTemplateModel billingTemplateModel){
        Global.setLanguage(context);  // handle bug bahasa pada inform
        int count;
        if (billingTemplateModel.getName().equals("")){
            Toast.makeText(context, context.getString(R.string.field_must_be_fill), Toast.LENGTH_LONG).show();
            return false;
        }

        count = Global.getCount(db, "pasienqu_billing_template"," name = '" + billingTemplateModel.getName() + "'");
        if (count > 0 && billingTemplateModel.getMode().equals("add")) {
            Toast.makeText(context, context.getString(R.string.name_must_be_unique), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public boolean insert(BillingTemplateModel billingTemplateModel, boolean isSinc) {
        if (isSinc) {
            if (!validate(billingTemplateModel)) {
                return false;
            }
        }

        ContentValues cv = this.setValues(billingTemplateModel, true);
        this.db.insert("pasienqu_billing_template", null, cv);

        if (isSinc) {
            Gson gson = new Gson();
            String json = gson.toJson(billingTemplateModel);
            SyncInfoModel syncInfoModel = new SyncInfoModel(0, "pasienqu.billing.template", json.getBytes(), "create", billingTemplateModel.getUuid());
            JApplication.getInstance().syncInfoTable.insert(syncInfoModel);
        }

        this.reloadList();
        return true;
    }

    public boolean update(BillingTemplateModel billingTemplateModel) {
        if (!validate(billingTemplateModel)) {
            return false;
        }

        ContentValues cv = this.setValues(billingTemplateModel, false);
        this.db.update("pasienqu_billing_template", cv, "id = " + billingTemplateModel.getId(), null);

        Gson gson = new Gson();
        String json = gson.toJson(billingTemplateModel);
        SyncInfoModel syncInfoModel = new SyncInfoModel(0, "pasienqu.billing.template", json.getBytes(), "write", billingTemplateModel.getUuid());
        JApplication.getInstance().syncInfoTable.insert(syncInfoModel);

        this.reloadList();
        return true;
    }

    public void deleteAll() {
        this.db.delete("pasienqu_billing_template", null, null);
        this.reloadList();
    }

    public void delete(String uuid){
        this.db.delete("pasienqu_billing_template",  "uuid = '" + uuid + "'", null);
        this.reloadList();
    }

    private void reloadList(){
        this.records.clear();
        Cursor cr = this.db.rawQuery("SELECT * FROM pasienqu_billing_template where id <> 0 "+addFilter()+addSorting(), null);

        BillingTemplateModel tempData;
        if (cr != null && cr.moveToFirst()){
            do {
                tempData = new BillingTemplateModel(
                        cr.getInt(cr.getColumnIndexOrThrow("id")),
                        cr.getString(cr.getColumnIndexOrThrow("uuid")),
                        cr.getString(cr.getColumnIndexOrThrow("name")),
                        cr.getString(cr.getColumnIndexOrThrow("items"))

                );
                this.records.add(tempData);
            } while(cr.moveToNext());
        }
    }

    public ArrayList<BillingTemplateModel> getRecords() {
        this.reloadList();
        return this.records;
    }

    public BillingTemplateModel getDataByIndex(int index){
        return this.records.get(index);
    }

    public BillingTemplateModel getDataByUuid(String uuid){
        Cursor cr = this.db.rawQuery("SELECT * FROM pasienqu_billing_template WHERE uuid = '"+uuid+"'", null);


        BillingTemplateModel tempData;
        if (cr != null && cr.moveToFirst()){
            tempData = new BillingTemplateModel(
                    cr.getInt(cr.getColumnIndexOrThrow("id")),
                    cr.getString(cr.getColumnIndexOrThrow("uuid")),
                    cr.getString(cr.getColumnIndexOrThrow("name")),
                    cr.getString(cr.getColumnIndexOrThrow("items"))
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

    public void setSorting(String valueSort) {
        this.valueSort = valueSort;
    }

    private String addSorting(){
        String sortby = "";
        if (valueSort.equals(JConst.value_desc)){
            sortby = " order by name desc ";
        }
        else if (valueSort.equals(JConst.value_asc)){
            sortby = " order by name asc ";
        }
        return sortby;
    }
}
