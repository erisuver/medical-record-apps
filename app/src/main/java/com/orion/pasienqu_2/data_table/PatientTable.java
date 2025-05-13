package com.orion.pasienqu_2.data_table;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.JConst;
import com.orion.pasienqu_2.models.PatientModel;
import com.orion.pasienqu_2.models.SyncInfoModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

public class PatientTable {
    private SQLiteDatabase db;
    private ArrayList<PatientModel> records;
    private Context context;
    //filter
    private String ageFrom = "", ageTo = "";
    private long genderId = 0;
    private boolean archived = false;
    private String valueSort = "";
    private int patientTypeId = 0;
    private int offset = 0;
    private String searchQuery ="";

    public PatientTable(Context context) {
        this.context = context;
        this.db = JApplication.getInstance().db;
        this.records = new ArrayList<PatientModel>();
        Global.setLanguage(context);  // handle bug bahasa pada inform

        //reset filter
        this.ageFrom = "";
        this.ageTo = "";
        this.genderId = 0;
        this.archived = false;
        this.valueSort = "";
        this.patientTypeId = 0;
        this.offset = 0;
        this.searchQuery ="";
    }

    private ContentValues setValues(PatientModel patientModel, boolean isSave){
        ContentValues cv = new ContentValues();
//        cv.put("id", patientModel.getId());
        cv.put("uuid", patientModel.getUuid());
        cv.put("patient_id", patientModel.getPatient_id());
        cv.put("name", patientModel.getName());
        cv.put("first_name", patientModel.getFirst_name());
        cv.put("surname", patientModel.getSurname());
        cv.put("register_date", patientModel.getRegister_date());
        cv.put("gender_id", patientModel.getGender_id());
        cv.put("date_of_birth", patientModel.getDate_of_birth());
        cv.put("age", patientModel.getAge());
        cv.put("month", patientModel.getMonth());
        cv.put("identification_no", patientModel.getIdentification_no());
        cv.put("email", patientModel.getEmail());
        cv.put("occupation", patientModel.getOccupation());
        cv.put("contact_no", patientModel.getContact_no());
        cv.put("address_street_1", patientModel.getAddress_street_1());
        cv.put("address_street_2", patientModel.getAddress_street_2());
        cv.put("patient_remark_1", patientModel.getPatient_remark_1());
        cv.put("patient_remark_2", patientModel.getPatient_remark_2());
        cv.put("patient_type_id", patientModel.getPatient_type_id());
        cv.put("description", patientModel.getDescription());
        cv.put("patient_ihs", patientModel.getPatient_ihs());
        if (isSave){
            cv.put("active", "true");
        }
        return cv;
    }

    public boolean insert(PatientModel patientModel, boolean isSinc) {
        if (isSinc) {
            if (!validate(patientModel)) {
                return false;
            }
            ContentValues cv = this.setValues(patientModel, true);
            this.db.insert("pasienqu_patient", null, cv);
        }else{
            ContentValues cv = this.setValues(patientModel, true);
            cv.put("id", patientModel.getId());
            this.db.insert("pasienqu_patient", null, cv);
        }
//        ContentValues cv = this.setValues(patientModel);
//        cv.put("active", "true");
//        this.db.insert("pasienqu_patient", null, cv);

        RecordTable recordTable = new RecordTable(context);
        for (int i = 0; i < patientModel.getMedical_record_ids().size(); i++) {
            recordTable.insert(patientModel.getMedical_record_ids().get(i), true);
        }

        if (isSinc) {
            Gson gson = new Gson();
            String json = gson.toJson(patientModel);
            SyncInfoModel syncInfoModel = new SyncInfoModel(0, "pasienqu.patient", json.getBytes(), "create", patientModel.getUuid());
            JApplication.getInstance().syncInfoTable.insert(syncInfoModel);
        }

        this.reloadList();
        return true;
    }

    public boolean update(PatientModel patientModel){
        if (!validate(patientModel)) {
            return false;
        }
        ContentValues cv = this.setValues(patientModel, false);
        this.db.update("pasienqu_patient", cv, "id = " + patientModel.getId(), null);

        Gson gson = new Gson();
        String json = gson.toJson(patientModel);
        SyncInfoModel syncInfoModel = new SyncInfoModel(0, "pasienqu.patient", json.getBytes(), "write", patientModel.getUuid());
        JApplication.getInstance().syncInfoTable.insert(syncInfoModel);

        this.reloadList();
        return true;
    }

    private void reloadList(){
        this.records.clear();
        Cursor cr = this.db.rawQuery("SELECT * FROM pasienqu_patient where id <> 0 "+
                addFilter()+
                addSorting(), null);

        PatientModel tempData;
        if (cr != null && cr.moveToFirst()){
            do {
                tempData = new PatientModel(
                    cr.getInt(cr.getColumnIndexOrThrow("id")),
                    cr.getString(cr.getColumnIndexOrThrow("uuid")),
                    cr.getString(cr.getColumnIndexOrThrow("patient_id")),
                    cr.getString(cr.getColumnIndexOrThrow("name")),
                    cr.getString(cr.getColumnIndexOrThrow("first_name")),
                    cr.getString(cr.getColumnIndexOrThrow("surname")),
                    cr.getLong(cr.getColumnIndexOrThrow("register_date")),
                    cr.getInt(cr.getColumnIndexOrThrow("gender_id")),
                    cr.getLong(cr.getColumnIndexOrThrow("date_of_birth")),
                    cr.getInt(cr.getColumnIndexOrThrow("age")),
                    cr.getInt(cr.getColumnIndexOrThrow("month")),
                    cr.getString(cr.getColumnIndexOrThrow("identification_no")),
                    cr.getString(cr.getColumnIndexOrThrow("email")),
                    cr.getString(cr.getColumnIndexOrThrow("occupation")),
                    cr.getString(cr.getColumnIndexOrThrow("contact_no")),
                    cr.getString(cr.getColumnIndexOrThrow("address_street_1")),
                    cr.getString(cr.getColumnIndexOrThrow("address_street_2")),
                    cr.getString(cr.getColumnIndexOrThrow("patient_remark_1")),
                    cr.getString(cr.getColumnIndexOrThrow("patient_remark_2")),
                    cr.getInt(cr.getColumnIndexOrThrow("patient_type_id")),
                    cr.getString(cr.getColumnIndexOrThrow("description"))
                );
                tempData.setPatient_ihs(cr.getString(cr.getColumnIndexOrThrow("patient_ihs")));
                this.records.add(tempData);
            } while(cr.moveToNext());
        }
    }

    private void reloadListLimit(int limit){
        this.records.clear();
        Cursor cr = this.db.rawQuery("SELECT * FROM pasienqu_patient where id <> 0 "+
                addFilter()+
                addSorting()+" limit "+limit+" offset "+offset, null);

        PatientModel tempData;
        if (cr != null && cr.moveToFirst()){
            do {
                tempData = new PatientModel(
                        cr.getInt(cr.getColumnIndexOrThrow("id")),
                        cr.getString(cr.getColumnIndexOrThrow("uuid")),
                        cr.getString(cr.getColumnIndexOrThrow("patient_id")),
                        cr.getString(cr.getColumnIndexOrThrow("name")),
                        cr.getString(cr.getColumnIndexOrThrow("first_name")),
                        cr.getString(cr.getColumnIndexOrThrow("surname")),
                        cr.getLong(cr.getColumnIndexOrThrow("register_date")),
                        cr.getInt(cr.getColumnIndexOrThrow("gender_id")),
                        cr.getLong(cr.getColumnIndexOrThrow("date_of_birth")),
                        cr.getInt(cr.getColumnIndexOrThrow("age")),
                        cr.getInt(cr.getColumnIndexOrThrow("month")),
                        cr.getString(cr.getColumnIndexOrThrow("identification_no")),
                        cr.getString(cr.getColumnIndexOrThrow("email")),
                        cr.getString(cr.getColumnIndexOrThrow("occupation")),
                        cr.getString(cr.getColumnIndexOrThrow("contact_no")),
                        cr.getString(cr.getColumnIndexOrThrow("address_street_1")),
                        cr.getString(cr.getColumnIndexOrThrow("address_street_2")),
                        cr.getString(cr.getColumnIndexOrThrow("patient_remark_1")),
                        cr.getString(cr.getColumnIndexOrThrow("patient_remark_2")),
                        cr.getInt(cr.getColumnIndexOrThrow("patient_type_id")),
                        cr.getString(cr.getColumnIndexOrThrow("description"))
                );
                tempData.setPatient_ihs(cr.getString(cr.getColumnIndexOrThrow("patient_ihs")));
                this.records.add(tempData);
            } while(cr.moveToNext());
        }
    }

    private boolean validate(PatientModel patientModel) {
        Global.setLanguage(context);  // handle bug bahasa pada inform
        if (patientModel.getPatient_id().equals("") || patientModel.getFirst_name().equals("") ||
                patientModel.getGender_id() == 0 || patientModel.getDate_of_birth() == 0 )
                //|| (!patientModel.getEmail().equals("") && !Patterns.EMAIL_ADDRESS.matcher(patientModel.getEmail()).matches()))
        {
            Toast.makeText(context, context.getString(R.string.field_must_be_fill), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (patientModel.getDate_of_birth() > Global.serverNowLong()){
            Toast.makeText(context, String.format(
                    context.getString(R.string.date_cannot_in_the_future), context.getString(R.string.date_of_birth).toLowerCase(Locale.ROOT)
            ), Toast.LENGTH_SHORT).show();
            return false;
        }
        int count = Global.getCount(db, "pasienqu_patient"," patient_id = '" + patientModel.getPatient_id() + "' and uuid <> '"+patientModel.getUuid()+"'");
        if (count > 0 ) {
            Toast.makeText(context, String.format(context.getString(R.string.error_unique), context.getString(R.string.patient_id)), Toast.LENGTH_SHORT).show();
            return false;
        }

        /*if (!patientModel.getIdentification_no().equals("") && Global.getCount(db, "pasienqu_patient"," identification_no = '" + patientModel.getIdentification_no() + "' and uuid <> '"+patientModel.getUuid()+"'") > 0 ) {
            Toast.makeText(context, String.format(context.getString(R.string.error_unique), context.getString(R.string.identification_number)), Toast.LENGTH_SHORT).show();
            return false;
        }*/


        return true;
    }

    public PatientModel getRecordByUuId(String uuid){
        Cursor cr = this.db.rawQuery("SELECT * FROM pasienqu_patient where uuid = '"+uuid+"'", null);

        PatientModel tempData = new PatientModel();
        if (cr != null && cr.moveToFirst()){
            tempData = new PatientModel(
                cr.getInt(cr.getColumnIndexOrThrow("id")),
                cr.getString(cr.getColumnIndexOrThrow("uuid")),
                cr.getString(cr.getColumnIndexOrThrow("patient_id")),
                "",
                cr.getString(cr.getColumnIndexOrThrow("first_name")),
                cr.getString(cr.getColumnIndexOrThrow("surname")),
                cr.getLong(cr.getColumnIndexOrThrow("register_date")),
                cr.getInt(cr.getColumnIndexOrThrow("gender_id")),
                cr.getLong(cr.getColumnIndexOrThrow("date_of_birth")),
                cr.getInt(cr.getColumnIndexOrThrow("age")),
                cr.getInt(cr.getColumnIndexOrThrow("month")),
                cr.getString(cr.getColumnIndexOrThrow("identification_no")),
                cr.getString(cr.getColumnIndexOrThrow("email")),
                cr.getString(cr.getColumnIndexOrThrow("occupation")),
                cr.getString(cr.getColumnIndexOrThrow("contact_no")),
                cr.getString(cr.getColumnIndexOrThrow("address_street_1")),
                cr.getString(cr.getColumnIndexOrThrow("address_street_2")),
                cr.getString(cr.getColumnIndexOrThrow("patient_remark_1")),
                cr.getString(cr.getColumnIndexOrThrow("patient_remark_2")),
                    cr.getInt(cr.getColumnIndexOrThrow("patient_type_id")),
                    cr.getString(cr.getColumnIndexOrThrow("description"))
            );
            RecordTable recordTable = new RecordTable(context);
            tempData.setMedical_record_ids(recordTable.getRecordByIdPatient(tempData.getId()));
            tempData.setPatient_ihs(cr.getString(cr.getColumnIndexOrThrow("patient_ihs")));
        }
        return tempData;
    }

    public int getMaxId(){
        Cursor cr = this.db.rawQuery("SELECT max(id) as id FROM pasienqu_patient", null);

        PatientModel tempData;
        if (cr != null && cr.moveToFirst()){
            return cr.getInt(cr.getColumnIndexOrThrow("id"));
        }else{
            return 0;
        }
    }

    public int getCountId(){
        String sql = "SELECT count(*) as id FROM pasienqu_patient where id <> 0";
        Cursor cr = this.db.rawQuery(sql, null);

        PatientModel tempData;
        if (cr != null && cr.moveToFirst()){
            return cr.getInt(cr.getColumnIndexOrThrow("id"));
        }else{
            return 0;
        }
    }

    public String getMaxPatientId(){
        String sql = "SELECT max(patient_id) as id FROM pasienqu_patient where patient_id GLOB '*[^a-Z]*' and patient_id  and length(patient_id) = 7";
        Cursor cr = this.db.rawQuery(sql, null);
        String result = "0";
        if (cr != null && cr.moveToFirst()){
            result = cr.getString(cr.getColumnIndexOrThrow("id"));
            cr.close();
        }

        if (result != null){ //cek
            return result;
        }else {
            return "0";
        }
    }


    public int getIdByIdPatient(String idPatient){
        Cursor cr = this.db.rawQuery("SELECT max(id) as id FROM pasienqu_patient where patient_id = '"+idPatient+"'", null);

        PatientModel tempData;
        if (cr != null && cr.moveToFirst()){
            int hasil = cr.getInt(cr.getColumnIndexOrThrow("id"));
            cr.close();
            return hasil;
        }else{
            cr.close();
            return 0;
        }
    }


    public int getCount(boolean thisMonthOnly){
        String sql = "SELECT count(*) as jml FROM pasienqu_patient where id <> 0 and active = 'true'";
        if (thisMonthOnly) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH)+1;
            long dateFrom = Global.getMillisDate("01/" + month + "/" + year);
            c.add(Calendar.MONTH, 1);
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH)+1;
            long dateTo = Global.getMillisDate("01/" + month + "/" + year);
            sql += " and register_date >= " + dateFrom + " and register_date < " + dateTo;
        }
        Cursor cr = this.db.rawQuery(sql, null);

        PatientModel tempData;
        if (cr != null && cr.moveToFirst()){
            return cr.getInt(cr.getColumnIndexOrThrow("jml"));
        }else{
            return 0;
        }
    }

    public int getpatientTypeById(int patientId){
        Cursor cr = this.db.rawQuery("SELECT patient_type_id FROM pasienqu_patient where id = "+patientId, null);

        if (cr != null && cr.moveToFirst()){
            return cr.getInt(cr.getColumnIndexOrThrow("patient_type_id"));
        }else{
            return 0;
        }
    }

    public String getNameIdByRecord(int id){
        Cursor cr = this.db.rawQuery("SELECT * FROM pasienqu_patient WHERE id = "+id, null);
        if (cr != null && cr.moveToFirst()){
            String nameId = cr.getString(cr.getColumnIndexOrThrow("first_name")) + " "+
                            cr.getString(cr.getColumnIndexOrThrow("surname")) + " ("+
                            cr.getString(cr.getColumnIndexOrThrow("patient_id")) + ")";
            cr.close();
            return nameId;
        }else{
            return "";
        }
    }

    public PatientModel getDataByIndex(int index){
        return this.records.get(index);
    }

    public ArrayList<PatientModel> getRecords(){
        this.reloadList();
        return this.records;
    }

    public ArrayList<PatientModel> getRecordsLimit(int limit){
        this.reloadListLimit(limit);
        return this.records;
    }

    public ArrayList<PatientModel> getRecords(String name){
        this.reloadList(name);
        return this.records;
    }

    private void reloadList(String name){
        this.records.clear();
        Cursor cr = this.db.rawQuery("SELECT * FROM pasienqu_patient where id <> 0 and first_name like '%"+name+"%'"+
                addFilter()+
                addSorting(), null);

        PatientModel tempData;
        if (cr != null && cr.moveToFirst()){
            do {
                tempData = new PatientModel(
                        cr.getInt(cr.getColumnIndexOrThrow("id")),
                        cr.getString(cr.getColumnIndexOrThrow("uuid")),
                        cr.getString(cr.getColumnIndexOrThrow("patient_id")),
                        "",
                        cr.getString(cr.getColumnIndexOrThrow("first_name")),
                        cr.getString(cr.getColumnIndexOrThrow("surname")),
                        cr.getLong(cr.getColumnIndexOrThrow("register_date")),
                        cr.getInt(cr.getColumnIndexOrThrow("gender_id")),
                        cr.getLong(cr.getColumnIndexOrThrow("date_of_birth")),
                        cr.getInt(cr.getColumnIndexOrThrow("age")),
                        cr.getInt(cr.getColumnIndexOrThrow("month")),
                        cr.getString(cr.getColumnIndexOrThrow("identification_no")),
                        cr.getString(cr.getColumnIndexOrThrow("email")),
                        cr.getString(cr.getColumnIndexOrThrow("occupation")),
                        cr.getString(cr.getColumnIndexOrThrow("contact_no")),
                        cr.getString(cr.getColumnIndexOrThrow("address_street_1")),
                        cr.getString(cr.getColumnIndexOrThrow("address_street_2")),
                        cr.getString(cr.getColumnIndexOrThrow("patient_remark_1")),
                        cr.getString(cr.getColumnIndexOrThrow("patient_remark_2")),
                        cr.getInt(cr.getColumnIndexOrThrow("patient_type_id")),
                        cr.getString(cr.getColumnIndexOrThrow("description"))
                );
                tempData.setPatient_ihs(cr.getString(cr.getColumnIndexOrThrow("patient_ihs")));
                this.records.add(tempData);
            } while(cr.moveToNext());
        }
    }


    public void deleteAll() {
        this.db.delete("pasienqu_patient", null, null);
        this.reloadList();
    }

    public void delete(String uuid){
        this.db.delete("pasienqu_patient",  "uuid = '" + uuid + "'", null);
        this.reloadList();
    }

    private String addFilter(){
        String filter = "";
        if (!ageFrom.equals("")){
            filter += " and (strftime('%Y', (1625467080000-date_of_birth)/1000, 'unixepoch')-1969) >= "+ageFrom+
                      " and (strftime('%Y', (1625467080000-date_of_birth)/1000, 'unixepoch')-1969) <= "+ageTo;
        }
        if (genderId != 0){
            filter += " and gender_id = "+genderId;
        }
        if (archived){
            filter += " and active = 'false'";
        }else{
            filter += " and active = 'true'";
        }

        if (patientTypeId != 0){
            filter += " and patient_type_id = "+patientTypeId;
        }

        if (!searchQuery.equals("")){
            filter += " and (patient_id||' - '||first_name||' ' || surname || address_street_1 like '%"+searchQuery.replace("'", "''")+"%') ";
        }

        return filter;
    }

    public void setFilter(String ageFrom, String ageTo, int genderId, boolean archived, int patientTypeId){
        this.ageFrom = ageFrom;
        this.ageTo = ageTo;
        this.genderId = genderId;
        this.archived = archived;
        this.patientTypeId = patientTypeId;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public void setSorting(String valueSort){
        this.valueSort = valueSort;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }


    private String addSorting(){
        String sortby = "";
        if (valueSort.equals("")){  //default sort is newest data
            sortby = " order by id desc ";
        }

        if (valueSort.equals(context.getString(R.string.value_sorting_youngest_fist))){
            sortby = " order by date_of_birth desc ";
        }
        else if (valueSort.equals(context.getString(R.string.value_sorting_oldest_fist))){
            sortby = " order by date_of_birth asc ";
        }

        else if (valueSort.equals(context.getString(R.string.value_sorting_first_name_alphabetically))){
            sortby = " order by first_name COLLATE NOCASE asc, surname COLLATE NOCASE asc ";
        }
        else if (valueSort.equals(context.getString(R.string.value_sorting_first_name_alphabetically_descending))){
            sortby = " order by first_name COLLATE NOCASE desc, surname COLLATE NOCASE desc ";
        }
        else if (valueSort.equals(JConst.value_sorting_newest)){
            sortby = " order by id desc ";
        }
        else if (valueSort.equals(JConst.value_sorting_oldest)){
            sortby = " order by id asc ";
        }
        return sortby;
    }

    public String getPatientFieldById(int id, String field){
        Cursor cr = this.db.rawQuery("SELECT "+field+" FROM pasienqu_patient where id = "+id, null);

        if (cr != null && cr.moveToFirst()){
            return cr.getString(cr.getColumnIndexOrThrow(field));
        }else{
            return "";
        }
    }

    public String getPatientNameById(int id){
        Cursor cr = this.db.rawQuery("SELECT first_name, surname FROM pasienqu_patient where id = "+id, null);
        String hsl = "";
//        try {
            if (cr != null && cr.moveToFirst()) {
                hsl = cr.getString(cr.getColumnIndexOrThrow("first_name")) + " " + cr.getString(cr.getColumnIndexOrThrow("surname"));
                cr.close();
            }
//        }finally {
//        }
        return hsl;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public int getTotalDatas() {
        int hsl = 0;
        Cursor cr = this.db.rawQuery("SELECT count(*) FROM pasienqu_patient " , null);

        if (cr != null && cr.moveToFirst()) {
            hsl = cr.getInt(0);
        }
        return hsl;

    }

    public PatientModel getRecordById(int id){
        Cursor cr = this.db.rawQuery("SELECT * FROM pasienqu_patient where id = "+id, null);

        PatientModel tempData = new PatientModel();
        if (cr != null && cr.moveToFirst()){
            tempData = new PatientModel(
                    cr.getInt(cr.getColumnIndexOrThrow("id")),
                    cr.getString(cr.getColumnIndexOrThrow("uuid")),
                    cr.getString(cr.getColumnIndexOrThrow("patient_id")),
                    "",
                    cr.getString(cr.getColumnIndexOrThrow("first_name")),
                    cr.getString(cr.getColumnIndexOrThrow("surname")),
                    cr.getLong(cr.getColumnIndexOrThrow("register_date")),
                    cr.getInt(cr.getColumnIndexOrThrow("gender_id")),
                    cr.getLong(cr.getColumnIndexOrThrow("date_of_birth")),
                    cr.getInt(cr.getColumnIndexOrThrow("age")),
                    cr.getInt(cr.getColumnIndexOrThrow("month")),
                    cr.getString(cr.getColumnIndexOrThrow("identification_no")),
                    cr.getString(cr.getColumnIndexOrThrow("email")),
                    cr.getString(cr.getColumnIndexOrThrow("occupation")),
                    cr.getString(cr.getColumnIndexOrThrow("contact_no")),
                    cr.getString(cr.getColumnIndexOrThrow("address_street_1")),
                    cr.getString(cr.getColumnIndexOrThrow("address_street_2")),
                    cr.getString(cr.getColumnIndexOrThrow("patient_remark_1")),
                    cr.getString(cr.getColumnIndexOrThrow("patient_remark_2")),
                    cr.getInt(cr.getColumnIndexOrThrow("patient_type_id")),
                    cr.getString(cr.getColumnIndexOrThrow("description"))
            );
            tempData.setPatient_ihs(cr.getString(cr.getColumnIndexOrThrow("patient_ihs")));
        }
        return tempData;
    }
}
