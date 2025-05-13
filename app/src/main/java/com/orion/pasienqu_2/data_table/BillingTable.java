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
import com.orion.pasienqu_2.globals.JConst;
import com.orion.pasienqu_2.models.BillingItemModel;
import com.orion.pasienqu_2.models.BillingModel;
import com.orion.pasienqu_2.models.PatientModel;
import com.orion.pasienqu_2.models.RecordDiagnosaModel;
import com.orion.pasienqu_2.models.SyncInfoModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class BillingTable {
    private SQLiteDatabase db;
    private ArrayList<BillingModel> records;
    private Context context;

    private int date_idx = 0;
    private long dateFrom = 0, dateTo = 0;
    private String notes= "" ;
    private boolean archived = false;
    private boolean isTemp = false;
    private int offset = 0;

    public BillingTable(Context context) {
        this.context = context;
//        this.db = new DBConn(context).getWritableDatabase();
        this.db = JApplication.getInstance().db;
        this.records = new ArrayList<BillingModel>();
        Global.setLanguage(context);  // handle bug bahasa pada inform
    }

    private ContentValues setValues(BillingModel billingModel, boolean isSave){
        ContentValues cv = new ContentValues();
        cv.put("billing_date", billingModel.getBilling_date());
        cv.put("patient_id", billingModel.getPatient_id());
        cv.put("notes", billingModel.getNotes());
        cv.put("total_amount", billingModel.getTotal_amount());
        cv.put("uuid", billingModel.getUuid());
        cv.put("name", billingModel.getName());
        if (isSave){
            cv.put("active", "true");
        }
        cv.put("is_temp", "false");
        cv.put("medical_record_id", billingModel.getMedical_record_id());

//        List<BillingItemModel> ;

        return cv;
    }

    private boolean validate(BillingModel billingModel) {
        int count;
        if (billingModel.getPatient_id() == 0){
            Toast.makeText(context, context.getString(R.string.field_must_be_fill), Toast.LENGTH_LONG).show();
            return false;
        }
        if (billingModel.getBilling_date() > Global.serverNowLong()){
            Toast.makeText(context, String.format(
                    context.getString(R.string.date_cannot_in_the_future), context.getString(R.string.billing_date).toLowerCase(Locale.ROOT)
            ), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public boolean insert(BillingModel billingModel, boolean isSinc) {
        if (isSinc) {
            if (!validate(billingModel)) {
                return false;
            }
            ContentValues cv = this.setValues(billingModel, true);
            this.db.insert("pasienqu_billing", null, cv);
        }
        else{
            ContentValues cv = this.setValues(billingModel, true);
            cv.put("id", billingModel.getId());
            this.db.insert("pasienqu_billing", null, cv);
        }
//        ContentValues cv = this.setValues(billingModel);
//        this.db.insert("pasienqu_billing", null, cv);
//
        if (isSinc) {
            Gson gson = new Gson();
            String json = gson.toJson(billingModel);
            SyncInfoModel syncInfoModel = new SyncInfoModel(0, "pasienqu.billing", json.getBytes(), "create", billingModel.getUuid());
            JApplication.getInstance().syncInfoTable.insert(syncInfoModel);
        }
        int headerId = this.getMaxId();
        BillingItemTable billingItemTable = new BillingItemTable(context);
        for (int i = 0; i < billingModel.getBilling_item_ids().size(); i++) {
            billingModel.getBilling_item_ids().get(i).setUuid(UUID.randomUUID().toString());
            billingModel.getBilling_item_ids().get(i).setHeader_id(headerId);
            billingModel.getBilling_item_ids().get(i).setHeader_uuid(billingModel.getUuid());
            billingItemTable.insert(billingModel.getBilling_item_ids().get(i), true);
        }
//        this.reloadList();
        return true;
    }

    public boolean insert(BillingModel billingModel) {
        if (!validate(billingModel)) {
            return false;
        }
        ContentValues cv = this.setValues(billingModel, true);
        this.db.insert("pasienqu_billing", null, cv);

        int headerId = this.getMaxId();
        BillingItemTable billingItemTable = new BillingItemTable(context);
        for (int i = 0; i < billingModel.getBilling_item_ids().size(); i++) {
            billingModel.getBilling_item_ids().get(i).setUuid(UUID.randomUUID().toString());
            billingModel.getBilling_item_ids().get(i).setHeader_id(headerId);
            billingModel.getBilling_item_ids().get(i).setHeader_uuid(billingModel.getUuid());
            billingItemTable.insert(billingModel.getBilling_item_ids().get(i));
        }
        return true;
    }

    public boolean delete(String uuid) {
        this.db.delete("pasienqu_billing", " uuid = '"+uuid+"'", null);
        return true;
    }
    public boolean deleteAll() {
        this.db.delete("pasienqu_billing", "", null);
        return true;
    }
    public boolean deleteWithItem(String uuid, int header_id) {
        this.db.delete("pasienqu_billing", " uuid = '"+uuid+"'", null);
        BillingItemTable billingItemTable = new BillingItemTable(context);
        billingItemTable.deleteByHeaderId(header_id);
        return true;
    }
    public boolean deleteBillingTemp() {
        this.db.delete("pasienqu_billing", " is_temp = 'true'", null);
        return true;
    }


    public boolean update(BillingModel billingModel, boolean deleteDetail){
        if (!validate(billingModel)){
            return false;
        }else {
            ContentValues cv = this.setValues(billingModel, false);
            this.db.update("pasienqu_billing", cv, "id = " + billingModel.getId(), null);
            if (deleteDetail) {
                BillingItemTable billingItemTable = new BillingItemTable(context);
                //hapus dulu semua item local dan item di odoo
//                ArrayList<BillingItemModel> billingItemModels = billingItemTable.getRecordsById(billingModel.getId());
//                if (billingItemModels.size() > 0) {
//                    billingItemTable.deleteItem(billingItemModels);
//                }
                billingItemTable.deleteByHeaderId(billingModel.getId());

                for (int i = 0; i < billingModel.getBilling_item_ids().size(); i++) {
                    billingModel.getBilling_item_ids().get(i).setUuid(UUID.randomUUID().toString());
                    billingModel.getBilling_item_ids().get(i).setHeader_id(billingModel.getId());
                    billingModel.getBilling_item_ids().get(i).setHeader_uuid(billingModel.getUuid());
                    billingItemTable.insert(billingModel.getBilling_item_ids().get(i), true);
                }
            }

//            dailyTripLogModel.setExpense_ids(new ArrayList<>());//sementara biar gak error
            Gson gson = new Gson();
            String json = gson.toJson(billingModel);
            SyncInfoModel syncInfoModel = new SyncInfoModel(0, "pasienqu.billing", json.getBytes(), "write", billingModel.getUuid());
            JApplication.getInstance().syncInfoTable.insert(syncInfoModel);
        }
        return true;
    }

    private void reloadList(String state){
        String filter = "";
        if (!state.equals("")){
            filter = " AND state = '"+state+"'";
        }
        this.records.clear();
        Cursor cr = this.db.rawQuery("SELECT id, uuid, billing_date, patient_id, notes, total_amount, name " +
                                        " FROM pasienqu_billing where id <> 0 "+addFilter(), null);

        BillingModel tempData;
        if (cr != null && cr.moveToFirst()){
            do {
                tempData = new BillingModel(
                        cr.getInt(cr.getColumnIndexOrThrow("id")),
                        cr.getString(cr.getColumnIndexOrThrow("uuid")),
                        cr.getLong(cr.getColumnIndexOrThrow("billing_date")),
                        cr.getInt(cr.getColumnIndexOrThrow("patient_id")),
                        cr.getString(cr.getColumnIndexOrThrow("notes")),
                        cr.getDouble(cr.getColumnIndexOrThrow("total_amount")),
                        cr.getString(cr.getColumnIndexOrThrow("name"))
                );
                this.records.add(tempData);
            } while(cr.moveToNext());
        }
    }

    private void reloadList(){
        this.records.clear();
        String sql = "SELECT id, uuid, billing_date, patient_id, notes, total_amount, name " +
                " FROM pasienqu_billing where id <> 0 "+addFilter();
        Cursor cr = this.db.rawQuery(sql, null);

        BillingModel tempData;
        if (cr != null && cr.moveToFirst()){
            do {
                tempData = new BillingModel(
                        cr.getInt(cr.getColumnIndexOrThrow("id")),
                        cr.getString(cr.getColumnIndexOrThrow("uuid")),
                        cr.getLong(cr.getColumnIndexOrThrow("billing_date")),
                        cr.getInt(cr.getColumnIndexOrThrow("patient_id")),
                        cr.getString(cr.getColumnIndexOrThrow("notes")),
                        cr.getDouble(cr.getColumnIndexOrThrow("total_amount")),
                        cr.getString(cr.getColumnIndexOrThrow("name"))
                );
                this.records.add(tempData);
            } while(cr.moveToNext());
        }
    }

    private void reloadListLimit(){
        this.records.clear();
        String sql = "SELECT id, uuid, billing_date, patient_id, notes, total_amount, name " +
                " FROM pasienqu_billing where id <> 0 "+addFilter() +" limit 20 offset "+offset;
        Cursor cr = this.db.rawQuery(sql, null);

        BillingModel tempData;
        if (cr != null && cr.moveToFirst()){
            do {
                tempData = new BillingModel(
                        cr.getInt(cr.getColumnIndexOrThrow("id")),
                        cr.getString(cr.getColumnIndexOrThrow("uuid")),
                        cr.getLong(cr.getColumnIndexOrThrow("billing_date")),
                        cr.getInt(cr.getColumnIndexOrThrow("patient_id")),
                        cr.getString(cr.getColumnIndexOrThrow("notes")),
                        cr.getDouble(cr.getColumnIndexOrThrow("total_amount")),
                        cr.getString(cr.getColumnIndexOrThrow("name"))
                );
                this.records.add(tempData);
            } while(cr.moveToNext());
        }
    }

    private void reloadListTemp(){
        this.records.clear();
        String sql = "SELECT id, uuid, billing_date, patient_id, notes, total_amount, name " +
                " FROM pasienqu_billing where id <> 0 and is_temp = 'true'";
        Cursor cr = this.db.rawQuery(sql, null);

        BillingModel tempData;
        if (cr != null && cr.moveToFirst()){
            do {
                tempData = new BillingModel(
                        cr.getInt(cr.getColumnIndexOrThrow("id")),
                        cr.getString(cr.getColumnIndexOrThrow("uuid")),
                        cr.getLong(cr.getColumnIndexOrThrow("billing_date")),
                        cr.getInt(cr.getColumnIndexOrThrow("patient_id")),
                        cr.getString(cr.getColumnIndexOrThrow("notes")),
                        cr.getDouble(cr.getColumnIndexOrThrow("total_amount")),
                        cr.getString(cr.getColumnIndexOrThrow("name"))
                );
                this.records.add(tempData);
            } while(cr.moveToNext());
        }
    }


    public int getMaxId(){
        this.records.clear();
        Cursor cr = this.db.rawQuery("SELECT max(id) as id FROM pasienqu_billing", null);
        if (cr != null && cr.moveToFirst()){
            do {
                return cr.getInt(cr.getColumnIndexOrThrow("id"));
            } while(cr.moveToNext());
        }
        return 0;
    }


    public int getSum(boolean thisMonth){
        String sql = "SELECT sum(total_amount) as jml FROM pasienqu_billing where id <> 0 and active = 'true'";
        if (thisMonth) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH)+1;
            long dateFrom = Global.getMillisDate("01/" + month + "/" + year);
            c.add(Calendar.MONTH, 1);
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH)+1;
            long dateTo = Global.getMillisDate("01/" + month + "/" + year);
            sql += " and billing_date >= " + dateFrom + " and billing_date < " + dateTo;
        }else{
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH)+1;
            int day = c.get(Calendar.DAY_OF_MONTH);
            long today = Global.getMillisDate(day+"/" + month + "/" + year);
            sql += " and billing_date = " + today;
        }
        Cursor cr = this.db.rawQuery(sql, null);

        PatientModel tempData;
        if (cr != null && cr.moveToFirst()){
            return cr.getInt(cr.getColumnIndexOrThrow("jml"));
        }else{
            return 0;
        }
    }

    public int getSumByFilter(){
        String sql = "SELECT sum(total_amount) as jml FROM pasienqu_billing where id <> 0"+addFilter();
        Cursor cr = this.db.rawQuery(sql, null);

        PatientModel tempData;
        if (cr != null && cr.moveToFirst()){
            return cr.getInt(cr.getColumnIndexOrThrow("jml"));
        }else{
            return 0;
        }
    }

    public int getSumByMedicalRecord(int record_id){
        String filter = "";
        if (archived){
            filter += " and active = 'false'";
        }else{
            filter += " and active = 'true'";
        }
        String sql = "SELECT sum(total_amount) as jml FROM pasienqu_billing where id <> 0 and medical_record_id = "+record_id+ filter;
        Cursor cr = this.db.rawQuery(sql, null);

        PatientModel tempData;
        if (cr != null && cr.moveToFirst()){
            return cr.getInt(cr.getColumnIndexOrThrow("jml"));
        }else{
            return 0;
        }
    }

    public int getBillingTotalByMedical(int record_id){
        String sql = "SELECT total_amount as jml FROM pasienqu_billing where id <> 0 and medical_record_id = "+record_id;
        Cursor cr = this.db.rawQuery(sql, null);

        if (cr != null && cr.moveToFirst()){
            return cr.getInt(cr.getColumnIndexOrThrow("jml"));
        }else{
            return 0;
        }
    }

    public BillingModel getDataByIndex(int index){
        return this.records.get(index);
    }

    public ArrayList<BillingModel> getRecords(String state){
        this.reloadList(state);
        return this.records;
    }

    public ArrayList<BillingModel> getRecords(){
        this.reloadList();
        return this.records;
    }

    public ArrayList<BillingModel> getRecordsLimit(){
        this.reloadListLimit();
        return this.records;
    }

    public ArrayList<BillingModel> getTempRecords(){
        this.reloadListTemp();
        return this.records;
    }

    public ArrayList<BillingModel> getRecordsByMedical(int medical_record_id) {
        this.records.clear();
        String filter = "";
        if (archived){
            filter += " and active = 'false'";
        }else{
            filter += " and active = 'true'";
        }
        Cursor cr = this.db.rawQuery("SELECT * FROM pasienqu_billing where medical_record_id = "+medical_record_id +filter, null);

        BillingModel tempData;
        if (cr != null && cr.moveToFirst()) {
            do {
                tempData = new BillingModel(
                        cr.getInt(cr.getColumnIndexOrThrow("id")),
                        cr.getString(cr.getColumnIndexOrThrow("uuid")),
                        cr.getLong(cr.getColumnIndexOrThrow("billing_date")),
                        cr.getInt(cr.getColumnIndexOrThrow("patient_id")),
                        cr.getString(cr.getColumnIndexOrThrow("notes")),
                        cr.getDouble(cr.getColumnIndexOrThrow("total_amount")),
                        cr.getString(cr.getColumnIndexOrThrow("name"))
                );
                tempData.setMedical_record_id(cr.getInt(cr.getColumnIndexOrThrow("medical_record_id")));
                this.records.add(tempData);
            } while (cr.moveToNext());
        }
        return this.records;
    }


    public BillingModel getRecordByUuid(String uuid){

        Cursor cr = this.db.rawQuery("SELECT * FROM pasienqu_billing where uuid = '"+uuid+"'", null);

        BillingModel tempData = new BillingModel();
        if (cr != null && cr.moveToFirst()){
            tempData = new BillingModel(
                    cr.getInt(cr.getColumnIndexOrThrow("id")),
                    cr.getString(cr.getColumnIndexOrThrow("uuid")),
                    cr.getLong(cr.getColumnIndexOrThrow("billing_date")),
                    cr.getInt(cr.getColumnIndexOrThrow("patient_id")),
                    cr.getString(cr.getColumnIndexOrThrow("notes")),
                    cr.getDouble(cr.getColumnIndexOrThrow("total_amount")),
                    cr.getString(cr.getColumnIndexOrThrow("name"))
            );
            tempData.setMedical_record_id(cr.getInt(cr.getColumnIndexOrThrow("medical_record_id")));
            BillingItemTable billingItemTable = new BillingItemTable(context);
//            tempData.setBilling_item_ids(billingItemTable.getRecordsByUuid(tempData.getUuid()));
            tempData.setBilling_item_ids(billingItemTable.getRecordsById(tempData.getId()));
        }
        return tempData;
    }

    public BillingModel getRecordById(int id){

        Cursor cr = this.db.rawQuery("SELECT * FROM pasienqu_billing WHERE id = "+id, null);

        BillingModel tempData = new BillingModel();
        if (cr != null && cr.moveToFirst()){
            tempData = new BillingModel(
                    cr.getInt(cr.getColumnIndexOrThrow("id")),
                    cr.getString(cr.getColumnIndexOrThrow("uuid")),
                    cr.getLong(cr.getColumnIndexOrThrow("billing_date")),
                    cr.getInt(cr.getColumnIndexOrThrow("patient_id")),
                    cr.getString(cr.getColumnIndexOrThrow("notes")),
                    cr.getDouble(cr.getColumnIndexOrThrow("total_amount")),
                    cr.getString(cr.getColumnIndexOrThrow("name"))
            );
            BillingItemTable billingItemTable = new BillingItemTable(context);
//            tempData.setBilling_item_ids(billingItemTable.getRecordsById(tempData.getUuid()));
            tempData.setBilling_item_ids(billingItemTable.getRecordsById(tempData.getId()));
        }
        return tempData;
    }

    public BillingModel getRecordByPatientId(int patientId, int recordId){
        String sql = "SELECT b.id, b.uuid, b.billing_date, b.patient_id, b.notes, b.total_amount, b.name, r.id, r.uuid " +
                " FROM pasienqu_billing b, pasienqu_record r where id <> 0 and is_temp = 'true' and patient_id = "+patientId+"and r.id = "+recordId;
        Cursor cr = this.db.rawQuery(sql, null);

        BillingModel tempData = new BillingModel();
        if (cr != null && cr.moveToFirst()){
            tempData = new BillingModel(
                    cr.getInt(cr.getColumnIndexOrThrow("id")),
                    cr.getString(cr.getColumnIndexOrThrow("uuid")),
                    cr.getLong(cr.getColumnIndexOrThrow("billing_date")),
                    cr.getInt(cr.getColumnIndexOrThrow("patient_id")),
                    cr.getString(cr.getColumnIndexOrThrow("notes")),
                    cr.getDouble(cr.getColumnIndexOrThrow("total_amount")),
                    cr.getString(cr.getColumnIndexOrThrow("name"))
            );
            BillingItemTable billingItemTable = new BillingItemTable(context);
//            tempData.setBilling_item_ids(billingItemTable.getRecordsById(tempData.getUuid()));
            tempData.setBilling_item_ids(billingItemTable.getRecordsById(tempData.getId()));
        }
        return tempData;
    }

    private String addFilter(){
        String filter = "";

        if (notes != null && !notes.equals("")){
            filter += " and notes like '%"+notes+"%'";
        }

        if (archived){
            filter += " and active = 'false'";
        }else{
            filter += " and active = 'true'";
        }

        if (isTemp){
            filter += " and is_temp = 'true'";
        }else {
            filter += " and is_temp = 'false'";
        }


        Calendar c = Calendar.getInstance();
        int year;
        int month;
        long billingFrom;
        long billingTo;
        switch (date_idx) {
            case JConst.idx_filter_calendar_this_month:
//                    year = c.get(Calendar.YEAR);
//                    month = c.get(Calendar.MONTH)+1;
//                    billingFrom = Global.getMillisDate("01/" + month + "/" + year);
//                    c.add(Calendar.MONTH, 1);
//                    year = c.get(Calendar.YEAR);
//                    month = c.get(Calendar.MONTH)+1;
//                    billingTo = Global.getMillisDate("01/" + month + "/" + year);
                billingFrom = Global.StartOfTheMonthLong(0);
                billingTo = Global.EndOfTheMonthLong(0);

                filter += " and billing_date >= " + billingFrom + " and billing_date <= " + billingTo;
                break;
            case JConst.idx_filter_calendar_last_month:
//                    c.add(Calendar.MONTH, -1);
//                    year = c.get(Calendar.YEAR);
//                    month = c.get(Calendar.MONTH)+1;
//                    billingFrom = Global.getMillisDate("01/" + month + "/" + year);
//                    c.add(Calendar.MONTH, 2);
//                    year = c.get(Calendar.YEAR);
//                    month = c.get(Calendar.MONTH)+1;
//                    billingTo = Global.getMillisDate("01/" + month + "/" + year);

                billingFrom = Global.StartOfTheMonthLong(-1);
                billingTo = Global.EndOfTheMonthLong(-1);
                filter += " and billing_date >= " + billingFrom + " and billing_date <= " + billingTo;
                break;
            case JConst.idx_filter_calendar_three_month:
//                    c.add(Calendar.MONTH, -3);
//                    year = c.get(Calendar.YEAR);
//                    month = c.get(Calendar.MONTH)+1;
//                    billingFrom = Global.getMillisDate("01/" + month + "/" + year);
//                    c.add(Calendar.MONTH, 4);
//                    year = c.get(Calendar.YEAR);
//                    month = c.get(Calendar.MONTH)+1;
//                    billingTo = Global.getMillisDate("01/" + month + "/" + year);
                billingFrom = Global.StartOfTheMonthLong(-4);
                billingTo = Global.EndOfTheMonthLong(-1);

                filter += " and billing_date >= " + billingFrom + " and billing_date <= " + billingTo;
                break;
            case JConst.idx_filter_calendar_custom:
                billingFrom = dateFrom;
                billingTo = dateTo;
                filter += " and billing_date >= " + billingFrom + " and billing_date <= " + billingTo;
                break;
            default:
                break;
        }

        filter += " ";
        return filter;
    }

    public void setFilter(int date_idx, long dateFrom, long dateTo, String notes, boolean archived){
        this.date_idx = date_idx;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.notes = notes;
        this.archived = archived;
    }

    public void setTemp(boolean isTemp){
        this.isTemp = isTemp;
    }

    public void setOffset(int itemCount) {
        this.offset = itemCount;
    }


    public int getTotalDatas() {
        int hsl = 0;
        Cursor cr = this.db.rawQuery("SELECT count(*) FROM pasienqu_billing " , null);

        if (cr != null && cr.moveToFirst()) {
            hsl = cr.getInt(0);
        }
        return hsl;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }
}
