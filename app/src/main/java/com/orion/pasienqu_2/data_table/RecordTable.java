package com.orion.pasienqu_2.data_table;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.google.gson.Gson;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.data_table.blob.PrescriptionFileTable;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.JConst;
import com.orion.pasienqu_2.globals.SharedPrefsUtils;
import com.orion.pasienqu_2.models.BillingItemModel;
import com.orion.pasienqu_2.models.BillingModel;
import com.orion.pasienqu_2.models.ICDModel;
import com.orion.pasienqu_2.models.PatientModel;
import com.orion.pasienqu_2.models.RecordDiagnosaModel;
import com.orion.pasienqu_2.models.RecordFileModel;
import com.orion.pasienqu_2.models.RecordModel;
import com.orion.pasienqu_2.models.SyncInfoModel;
import com.orion.pasienqu_2.models.blob.PrescriptionFileModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class RecordTable {
    private SQLiteDatabase db;
    private ArrayList<RecordModel> records;
    private Context context;

    private int idxSelectedType = 0, idWorkLocation = 0;
    private long dateFrom = 0, dateTo = 0;
    private String diagnosa = "";
    private boolean archived = false;
    private String valueSort = "";
    private int patientTypeId = 0;
    private int offset = 0;
    private String searchQuery = "";

    public RecordTable(Context context) {
        this.context = context;
//        this.db = new DBConn(context).getWritableDatabase();
        this.db = JApplication.getInstance().db;
        this.records = new ArrayList<RecordModel>();
        Global.setLanguage(context);  // handle bug bahasa pada inform
    }

    private ContentValues setValues(RecordModel recordModel, boolean isSave){
        ContentValues cv = new ContentValues();
        cv.put("uuid", recordModel.getUuid());
        cv.put("record_date", recordModel.getRecord_date());
        cv.put("work_location_id", recordModel.getWork_location_id());
        cv.put("patient_id", recordModel.getPatient_id());
        cv.put("name", recordModel.getName());
        cv.put("weight", recordModel.getWeight());
        cv.put("temperature", recordModel.getTemperature());
        cv.put("blood_pressure_systolic", recordModel.getBlood_pressure_systolic());
        cv.put("blood_pressure_diastolic", recordModel.getBlood_pressure_diastolic());
        cv.put("anamnesa", recordModel.getAnamnesa());
        cv.put("physical_exam", recordModel.getPhysical_exam());
        cv.put("diagnosa", recordModel.getDiagnosa());
        cv.put("therapy", recordModel.getTherapy());
        cv.put("prescription_file", recordModel.getPrescription_file()); //tutup pisah penyimpanan database
        cv.put("total_billing", recordModel.getTotal_billing());
        cv.put("billing_id", recordModel.getBilling_id());
        if (isSave){
            cv.put("active", "true");
        }
        cv.put("patient_type_id", recordModel.getpatient_type_id());
        cv.put("create_date", recordModel.getCreate_date());
        cv.put("write_date", recordModel.getWrite_date());

        
        return cv;
    }

    private boolean validate(RecordModel recordModel) {
        int count;
        if (recordModel.getPatient_id() == 0 || recordModel.getWork_location_id() == 0){
            Toast.makeText(context, context.getString(R.string.field_must_be_fill), Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }


    public boolean insert(RecordModel recordModel, boolean isSinc) {
        if (isSinc) {
            if (!validate(recordModel)) {
                return false;
            }
            //local
            ContentValues cv = this.setValues(recordModel, true);
            this.db.insert("pasienqu_record", null, cv);
        }else{
            ContentValues cv = this.setValues(recordModel, true);
            cv.put("id", recordModel.getId());
            this.db.insert("pasienqu_record", null, cv);
        }
//        ContentValues cv = this.setValues(recordModel);
//        this.db.insert("pasienqu_record", null, cv);


        if (isSinc) {
            Gson gson = new Gson();
            String json = gson.toJson(recordModel);
            SyncInfoModel syncInfoModel = new SyncInfoModel(0, "pasienqu.medical.record", json.getBytes(), "create", recordModel.getUuid());
            JApplication.getInstance().syncInfoTable.insert(syncInfoModel);



        }

        int headerId = 0;
        if (recordModel.getFile_ids().size() > 0) {
            headerId = this.getMaxId();
            RecordFileTable recordFileTable = new RecordFileTable(context);
            for (int i = 0; i < recordModel.getFile_ids().size(); i++) {
                recordModel.getFile_ids().get(i).setUuid(UUID.randomUUID().toString());
                recordModel.getFile_ids().get(i).setRecord_id(headerId);
                recordModel.getFile_ids().get(i).setRecord_uuid(recordModel.getUuid());
                recordFileTable.insert(recordModel.getFile_ids().get(i), true);
            }
        }

        //tes image
//        int headerId = 0;
//        RecordFileTable recordFileTable = new RecordFileTable(context);
//        for (int i = 0; i < 1000; i++) {
//            recordModel.getFile_ids().get(0).setUuid(UUID.randomUUID().toString());
//            recordModel.getFile_ids().get(0).setRecord_id(0);
//            recordModel.getFile_ids().get(0).setRecord_uuid(recordModel.getUuid());
//            recordFileTable.insert(recordModel.getFile_ids().get(0), true);
//        }

        if (recordModel.getIcd_ids().size() > 0) {
            if (headerId == 0) {
                headerId = this.getMaxId();
            }
            RecordDiagnosaTable recordDiagnosaTable = new RecordDiagnosaTable(context);
            for (int i = 0; i < recordModel.getIcd_ids().size(); i++) {
                recordModel.getIcd_ids().get(i).setUuid(UUID.randomUUID().toString());
                recordModel.getIcd_ids().get(i).setRecord_id(headerId);
                recordModel.getIcd_ids().get(i).setRecord_uuid(recordModel.getUuid());
                recordDiagnosaTable.insert(recordModel.getIcd_ids().get(i), true);
            }
        }
//        BillingTable billingTable = new BillingTable(context);
//        for (int i = 0; i < recordModel.getBilling_ids().size(); i++) {
//            recordModel.getBilling_ids().get(i).setMedical_record_id(headerId);
//            billingTable.insert(recordModel.getBilling_ids().get(i), true);
//        }
//        this.reloadList();

        return true;
    }

    public boolean delete(String uuid) {
        this.db.delete("pasienqu_record", " uuid = '"+uuid+"'", null);
        return true;
    }
    public boolean deleteAll() {
        this.db.delete("pasienqu_record", "", null);
        return true;
    }

    public boolean update(RecordModel recordModel, boolean deleteDetail){
        if (!validate(recordModel)){
            return false;
        }else {
            ContentValues cv = this.setValues(recordModel, false);
            this.db.update("pasienqu_record", cv, "id = " + recordModel.getId(), null);
            if (deleteDetail) {
                RecordFileTable recordFileTable = new RecordFileTable(context);
                //hapus dulu semua item local dan item di odoo
//                ArrayList<RecordFileModel> recordFileModels = recordFileTable.getRecordsById(recordModel.getId());
//                if (recordFileModels.size() > 0) {
//                    recordFileTable.deleteItem(recordFileModels);
//                }
                recordFileTable.delete(recordModel.getUuid());
                for (int i = 0; i < recordModel.getFile_ids().size(); i++) {
                    recordModel.getFile_ids().get(i).setUuid(UUID.randomUUID().toString());
                    recordModel.getFile_ids().get(i).setRecord_id(recordModel.getId());
                    recordModel.getFile_ids().get(i).setRecord_uuid(recordModel.getUuid());
                    recordFileTable.insert(recordModel.getFile_ids().get(i), true);
                }

                RecordDiagnosaTable recordDiagnosaTable = new RecordDiagnosaTable(context);
                recordDiagnosaTable.delete(recordModel.getUuid());
                for (int i = 0; i < recordModel.getIcd_ids().size(); i++) {
                    recordModel.getIcd_ids().get(i).setUuid(UUID.randomUUID().toString());
                    recordModel.getIcd_ids().get(i).setRecord_id(recordModel.getId());
                    recordModel.getIcd_ids().get(i).setRecord_uuid(recordModel.getUuid());
                    recordDiagnosaTable.insert(recordModel.getIcd_ids().get(i), true);
                }
//                BillingTable billingTable = new BillingTable(context);
//                billingTable.delete(recordModel.getUuid());
//                for (int i = 0; i < recordModel.getBilling_ids().size(); i++) {
//                    recordModel.getBilling_ids().get(i).setMedical_record_id(recordModel.getId());
//                    billingTable.insert(recordModel.getBilling_ids().get(i), true);
//                }

            }

//            dailyTripLogModel.setExpense_ids(new ArrayList<>());//sementara biar gak error
            Gson gson = new Gson();
            String json = gson.toJson(recordModel);
            SyncInfoModel syncInfoModel = new SyncInfoModel(0, "pasienqu.medical.record", json.getBytes(), "write", recordModel.getUuid());
            JApplication.getInstance().syncInfoTable.insert(syncInfoModel);
        }
        return true;
    }

    private void reloadList(String state){
        this.records.clear();
        Cursor cr = this.db.rawQuery("SELECT id, uuid, record_date, work_location_id, patient_id, name, weight, temperature, blood_pressure_systolic, blood_pressure_diastolic, " +
                                        "anamnesa, physical_exam, diagnosa, therapy, prescription_file, total_billing, billing_id, patient_type_id, create_date, write_date " +
                                        " FROM pasienqu_record where id <> 0 "+addFilter()+addSorting(), null);

        RecordModel tempData;
        if (cr != null && cr.moveToFirst()){
            do {
                tempData = new RecordModel(
                        cr.getInt(cr.getColumnIndexOrThrow("id")),
                        cr.getString(cr.getColumnIndexOrThrow("uuid")),
                        cr.getLong(cr.getColumnIndexOrThrow("record_date")),
                        cr.getInt(cr.getColumnIndexOrThrow("work_location_id")),
                        cr.getInt(cr.getColumnIndexOrThrow("patient_id")),
                        cr.getString(cr.getColumnIndexOrThrow("name")),
                        cr.getDouble(cr.getColumnIndexOrThrow("weight")),
                        cr.getDouble(cr.getColumnIndexOrThrow("temperature")),
                        cr.getInt(cr.getColumnIndexOrThrow("blood_pressure_systolic")),
                        cr.getInt(cr.getColumnIndexOrThrow("blood_pressure_diastolic")),
                        cr.getString(cr.getColumnIndexOrThrow("anamnesa")),
                        cr.getString(cr.getColumnIndexOrThrow("physical_exam")),
                        cr.getString(cr.getColumnIndexOrThrow("diagnosa")),
                        cr.getString(cr.getColumnIndexOrThrow("therapy")),
                        cr.getString(cr.getColumnIndexOrThrow("prescription_file")),
                        cr.getDouble(cr.getColumnIndexOrThrow("total_billing")),
                        cr.getInt(cr.getColumnIndexOrThrow("billing_id")),
                        cr.getInt(cr.getColumnIndexOrThrow("patient_type_id")),
                        cr.getLong(cr.getColumnIndexOrThrow("create_date")),
                        cr.getLong(cr.getColumnIndexOrThrow("write_date"))
                );
                this.records.add(tempData);
            } while(cr.moveToNext());
            cr.close();
        }
    }

    private void reloadList(){
        this.records.clear();
        String sql = "SELECT id, uuid, record_date, work_location_id, patient_id, name, weight, temperature, blood_pressure_systolic, blood_pressure_diastolic, " +
                "anamnesa, physical_exam, diagnosa, therapy, prescription_file, total_billing, billing_id, patient_type_id, create_date, write_date " +
                " FROM pasienqu_record where id <> 0 "+addFilter()+addSorting();
        Cursor cr = this.db.rawQuery(sql, null);

        RecordModel tempData;
        if (cr != null && cr.moveToFirst()){
            do {
                tempData = new RecordModel(
                        cr.getInt(cr.getColumnIndexOrThrow("id")),
                        cr.getString(cr.getColumnIndexOrThrow("uuid")),
                        cr.getLong(cr.getColumnIndexOrThrow("record_date")),
                        cr.getInt(cr.getColumnIndexOrThrow("work_location_id")),
                        cr.getInt(cr.getColumnIndexOrThrow("patient_id")),
                        cr.getString(cr.getColumnIndexOrThrow("name")),
                        cr.getDouble(cr.getColumnIndexOrThrow("weight")),
                        cr.getDouble(cr.getColumnIndexOrThrow("temperature")),
                        cr.getInt(cr.getColumnIndexOrThrow("blood_pressure_systolic")),
                        cr.getInt(cr.getColumnIndexOrThrow("blood_pressure_diastolic")),
                        cr.getString(cr.getColumnIndexOrThrow("anamnesa")),
                        cr.getString(cr.getColumnIndexOrThrow("physical_exam")),
                        cr.getString(cr.getColumnIndexOrThrow("diagnosa")),
                        cr.getString(cr.getColumnIndexOrThrow("therapy")),
                        cr.getString(cr.getColumnIndexOrThrow("prescription_file")),
                        cr.getDouble(cr.getColumnIndexOrThrow("total_billing")),
                        cr.getInt(cr.getColumnIndexOrThrow("billing_id")),
                        cr.getInt(cr.getColumnIndexOrThrow("patient_type_id")),
                        cr.getLong(cr.getColumnIndexOrThrow("create_date")),
                        cr.getLong(cr.getColumnIndexOrThrow("write_date"))
                );
                this.records.add(tempData);
            } while(cr.moveToNext());
//            cr.close();
        }
    }

    private void reloadListLimit(int limit){
        this.records.clear();
        String sql = "SELECT id, uuid, record_date, work_location_id, patient_id, name, weight, temperature, blood_pressure_systolic, blood_pressure_diastolic, " +
                "anamnesa, physical_exam, diagnosa, therapy, prescription_file, total_billing, billing_id, patient_type_id, create_date, write_date " +
                " FROM pasienqu_record where id <> 0 "+addFilter()+addSorting()+" limit "+limit+" offset "+offset;
        Cursor cr = this.db.rawQuery(sql, null);

        RecordModel tempData;
        if (cr != null && cr.moveToFirst()){
            do {
                tempData = new RecordModel(
                        cr.getInt(cr.getColumnIndexOrThrow("id")),
                        cr.getString(cr.getColumnIndexOrThrow("uuid")),
                        cr.getLong(cr.getColumnIndexOrThrow("record_date")),
                        cr.getInt(cr.getColumnIndexOrThrow("work_location_id")),
                        cr.getInt(cr.getColumnIndexOrThrow("patient_id")),
                        cr.getString(cr.getColumnIndexOrThrow("name")),
                        cr.getDouble(cr.getColumnIndexOrThrow("weight")),
                        cr.getDouble(cr.getColumnIndexOrThrow("temperature")),
                        cr.getInt(cr.getColumnIndexOrThrow("blood_pressure_systolic")),
                        cr.getInt(cr.getColumnIndexOrThrow("blood_pressure_diastolic")),
                        cr.getString(cr.getColumnIndexOrThrow("anamnesa")),
                        cr.getString(cr.getColumnIndexOrThrow("physical_exam")),
                        cr.getString(cr.getColumnIndexOrThrow("diagnosa")),
                        cr.getString(cr.getColumnIndexOrThrow("therapy")),
                        cr.getString(cr.getColumnIndexOrThrow("prescription_file")),
                        cr.getDouble(cr.getColumnIndexOrThrow("total_billing")),
                        cr.getInt(cr.getColumnIndexOrThrow("billing_id")),
                        cr.getInt(cr.getColumnIndexOrThrow("patient_type_id")),
                        cr.getLong(cr.getColumnIndexOrThrow("create_date")),
                        cr.getLong(cr.getColumnIndexOrThrow("write_date"))
                );
                this.records.add(tempData);
            } while(cr.moveToNext());
            cr.close();
        }
    }

    private void reloadHistory(int patientId){
        this.records.clear();
        String sql = "SELECT id, uuid, record_date, work_location_id, patient_id, name, weight, temperature, blood_pressure_systolic, blood_pressure_diastolic, " +
                " anamnesa, physical_exam, diagnosa, therapy, prescription_file, total_billing, billing_id, patient_type_id, create_date, write_date " +
                " FROM pasienqu_record where id <> 0 and patient_id = "+patientId+ " ORDER BY record_date desc ";
        Cursor cr = this.db.rawQuery( sql, null);

        RecordModel tempData;
        if (cr != null && cr.moveToFirst()){
            do {
                tempData = new RecordModel(
                        cr.getInt(cr.getColumnIndexOrThrow("id")),
                        cr.getString(cr.getColumnIndexOrThrow("uuid")),
                        cr.getLong(cr.getColumnIndexOrThrow("record_date")),
                        cr.getInt(cr.getColumnIndexOrThrow("work_location_id")),
                        cr.getInt(cr.getColumnIndexOrThrow("patient_id")),
                        cr.getString(cr.getColumnIndexOrThrow("name")),
                        cr.getDouble(cr.getColumnIndexOrThrow("weight")),
                        cr.getDouble(cr.getColumnIndexOrThrow("temperature")),
                        cr.getInt(cr.getColumnIndexOrThrow("blood_pressure_systolic")),
                        cr.getInt(cr.getColumnIndexOrThrow("blood_pressure_diastolic")),
                        cr.getString(cr.getColumnIndexOrThrow("anamnesa")),
                        cr.getString(cr.getColumnIndexOrThrow("physical_exam")),
                        cr.getString(cr.getColumnIndexOrThrow("diagnosa")),
                        cr.getString(cr.getColumnIndexOrThrow("therapy")),
                        cr.getString(cr.getColumnIndexOrThrow("prescription_file")),
                        cr.getDouble(cr.getColumnIndexOrThrow("total_billing")),
                        cr.getInt(cr.getColumnIndexOrThrow("billing_id")),
                        cr.getInt(cr.getColumnIndexOrThrow("patient_type_id")),
                        cr.getLong(cr.getColumnIndexOrThrow("create_date")),
                        cr.getLong(cr.getColumnIndexOrThrow("write_date"))
                );
                this.records.add(tempData);
            } while(cr.moveToNext());
            cr.close();
        }
    }

    private void reloadListExport(){
        this.records.clear();
        String sql = "SELECT r.id, r.uuid, r.record_date,r.work_location_id, r.patient_id, r.name, r.weight, r.temperature, r.blood_pressure_systolic, r.blood_pressure_diastolic, " +
                "r.anamnesa, r.physical_exam, r.diagnosa, r.therapy, r.prescription_file, r.total_billing, r.billing_id, r.patient_type_id, r.create_date, r.write_date, " +
                "w.name as location, p.first_name as first_name, p.surname as surname, p.patient_id as patient_id_kode"+
                " FROM pasienqu_record r, pasienqu_work_location w, pasienqu_patient p " +
                " where r.id <> 0 and w.id = r.work_location_id and p.id = r.patient_id and r.active = 'true' ";
        Cursor cr = this.db.rawQuery(sql, null);

        RecordModel tempData;
        if (cr != null && cr.moveToFirst()){
            do {
                tempData = new RecordModel(
                        cr.getInt(cr.getColumnIndexOrThrow("id")),
                        cr.getString(cr.getColumnIndexOrThrow("uuid")),
                        cr.getLong(cr.getColumnIndexOrThrow("record_date")),
                        cr.getInt(cr.getColumnIndexOrThrow("work_location_id")),
                        cr.getInt(cr.getColumnIndexOrThrow("patient_id")),
                        cr.getString(cr.getColumnIndexOrThrow("name")),
                        cr.getDouble(cr.getColumnIndexOrThrow("weight")),
                        cr.getDouble(cr.getColumnIndexOrThrow("temperature")),
                        cr.getInt(cr.getColumnIndexOrThrow("blood_pressure_systolic")),
                        cr.getInt(cr.getColumnIndexOrThrow("blood_pressure_diastolic")),
                        cr.getString(cr.getColumnIndexOrThrow("anamnesa")),
                        cr.getString(cr.getColumnIndexOrThrow("physical_exam")),
                        cr.getString(cr.getColumnIndexOrThrow("diagnosa")),
                        cr.getString(cr.getColumnIndexOrThrow("therapy")),
                        cr.getString(cr.getColumnIndexOrThrow("prescription_file")),
                        cr.getDouble(cr.getColumnIndexOrThrow("total_billing")),
                        cr.getInt(cr.getColumnIndexOrThrow("billing_id")),
                        cr.getInt(cr.getColumnIndexOrThrow("patient_type_id")),
                        cr.getLong(cr.getColumnIndexOrThrow("create_date")),
                        cr.getLong(cr.getColumnIndexOrThrow("write_date"))
                );
                tempData.setLocation(cr.getString(cr.getColumnIndexOrThrow("location")));
                tempData.setPatient_name(cr.getString(cr.getColumnIndexOrThrow("first_name")) +" "+ cr.getString(cr.getColumnIndexOrThrow("surname")));
                tempData.setPatient_id_kode(cr.getString(cr.getColumnIndexOrThrow("patient_id_kode")));

                this.records.add(tempData);
            } while(cr.moveToNext());
//            cr.close();
        }
    }

    private void reloadListLimited(int limit){
        this.records.clear();
        String sql = "SELECT r.id, r.uuid, r.record_date,r.work_location_id, r.patient_id, r.name, r.weight, r.temperature, r.blood_pressure_systolic, r.blood_pressure_diastolic, " +
                "r.anamnesa, r.physical_exam, r.diagnosa, r.therapy, r.prescription_file, r.total_billing, r.billing_id, r.patient_type_id, r.create_date, r.write_date, " +
                "p.first_name as first_name, p.surname as surname, p.patient_id as patient_id_kode"+
                " FROM pasienqu_record r, pasienqu_patient p " +
                " where p.id = r.patient_id "+addFilterMultipleTable()+addSortingMultipleTable()+" limit "+limit+" offset "+offset;
        Cursor cr = this.db.rawQuery(sql, null);

        RecordModel tempData;
        if (cr != null && cr.moveToFirst()){
            do {
                tempData = new RecordModel(
                        cr.getInt(cr.getColumnIndexOrThrow("id")),
                        cr.getString(cr.getColumnIndexOrThrow("uuid")),
                        cr.getLong(cr.getColumnIndexOrThrow("record_date")),
                        cr.getInt(cr.getColumnIndexOrThrow("work_location_id")),
                        cr.getInt(cr.getColumnIndexOrThrow("patient_id")),
                        cr.getString(cr.getColumnIndexOrThrow("name")),
                        cr.getDouble(cr.getColumnIndexOrThrow("weight")),
                        cr.getDouble(cr.getColumnIndexOrThrow("temperature")),
                        cr.getInt(cr.getColumnIndexOrThrow("blood_pressure_systolic")),
                        cr.getInt(cr.getColumnIndexOrThrow("blood_pressure_diastolic")),
                        cr.getString(cr.getColumnIndexOrThrow("anamnesa")),
                        cr.getString(cr.getColumnIndexOrThrow("physical_exam")),
                        cr.getString(cr.getColumnIndexOrThrow("diagnosa")),
                        cr.getString(cr.getColumnIndexOrThrow("therapy")),
                        cr.getString(cr.getColumnIndexOrThrow("prescription_file")),
                        cr.getDouble(cr.getColumnIndexOrThrow("total_billing")),
                        cr.getInt(cr.getColumnIndexOrThrow("billing_id")),
                        cr.getInt(cr.getColumnIndexOrThrow("patient_type_id")),
                        cr.getLong(cr.getColumnIndexOrThrow("create_date")),
                        cr.getLong(cr.getColumnIndexOrThrow("write_date"))
                );
                tempData.setPatient_name(cr.getString(cr.getColumnIndexOrThrow("first_name")) +" "+
                        cr.getString(cr.getColumnIndexOrThrow("surname")) +" ("+
                        cr.getString(cr.getColumnIndexOrThrow("patient_id_kode")) +")"
                );
                this.records.add(tempData);
            } while(cr.moveToNext());
            cr.close();
        }
    }


    private void reloadPrescriptionFile(long lastBackup) {
        this.records.clear();
        String sql = "SELECT id, uuid, record_date, work_location_id, patient_id, name, weight, temperature, blood_pressure_systolic, blood_pressure_diastolic, " +
                " anamnesa, physical_exam, diagnosa, therapy, prescription_file, total_billing, billing_id, patient_type_id, create_date, write_date " +
                " FROM pasienqu_record "+
                " WHERE id <> 0 and write_date > "+lastBackup+
                " ORDER by write_date";
        Cursor cr = this.db.rawQuery( sql, null);

        RecordModel tempData;
        if (cr != null && cr.moveToFirst()){
            do {
                tempData = new RecordModel(
                        cr.getInt(cr.getColumnIndexOrThrow("id")),
                        cr.getString(cr.getColumnIndexOrThrow("uuid")),
                        cr.getLong(cr.getColumnIndexOrThrow("record_date")),
                        cr.getInt(cr.getColumnIndexOrThrow("work_location_id")),
                        cr.getInt(cr.getColumnIndexOrThrow("patient_id")),
                        cr.getString(cr.getColumnIndexOrThrow("name")),
                        cr.getDouble(cr.getColumnIndexOrThrow("weight")),
                        cr.getDouble(cr.getColumnIndexOrThrow("temperature")),
                        cr.getInt(cr.getColumnIndexOrThrow("blood_pressure_systolic")),
                        cr.getInt(cr.getColumnIndexOrThrow("blood_pressure_diastolic")),
                        cr.getString(cr.getColumnIndexOrThrow("anamnesa")),
                        cr.getString(cr.getColumnIndexOrThrow("physical_exam")),
                        cr.getString(cr.getColumnIndexOrThrow("diagnosa")),
                        cr.getString(cr.getColumnIndexOrThrow("therapy")),
                        cr.getString(cr.getColumnIndexOrThrow("prescription_file")),
                        cr.getDouble(cr.getColumnIndexOrThrow("total_billing")),
                        cr.getInt(cr.getColumnIndexOrThrow("billing_id")),
                        cr.getInt(cr.getColumnIndexOrThrow("patient_type_id")),
                        cr.getLong(cr.getColumnIndexOrThrow("create_date")),
                        cr.getLong(cr.getColumnIndexOrThrow("write_date"))
                );
                this.records.add(tempData);
            } while(cr.moveToNext());
            cr.close();
        }
    }



    public int getMaxId(){
        this.records.clear();
        Cursor cr = this.db.rawQuery("SELECT max(id) as id FROM pasienqu_record", null);
        if (cr != null && cr.moveToFirst()){
            do {
                return cr.getInt(cr.getColumnIndexOrThrow("id"));
            } while(cr.moveToNext());
        }
        return 0;
    }

    public int getCountRecordPatient (int patientId){
        String sql = "SELECT count(*) as jml FROM pasienqu_record where id <> 0 and patient_id = "+patientId;
        Cursor cr = this.db.rawQuery(sql, null);
        if (cr != null && cr.moveToFirst()){
            return cr.getInt(cr.getColumnIndexOrThrow("jml"));
        }else{
            return 0;
        }
    }

    public int getCountRecordFiltered (){
        String sql = "SELECT count(*) as jml FROM pasienqu_record where id <> 0  "+ addFilter() + addSorting();
        Cursor cr = this.db.rawQuery(sql, null);
        if (cr != null && cr.moveToFirst()){
            return cr.getInt(cr.getColumnIndexOrThrow("jml"));
        }else{
            return 0;
        }
    }

    public int getCountPrescriptionFile (long lastBackup){
        String sql = "SELECT count(prescription_file) as jml FROM pasienqu_record "+
                " WHERE id <> 0 and write_date > "+lastBackup+
                " ORDER by write_date";
        Cursor cr = this.db.rawQuery(sql, null);
        if (cr != null && cr.moveToFirst()){
            return cr.getInt(cr.getColumnIndexOrThrow("jml"));
        }else{
            return 0;
        }
    }

    public int getCountBillingFiltered (){
        String sql = "SELECT SUM(total_billing) as jml FROM pasienqu_record where id <> 0  "+ addFilter() + addSorting();
        Cursor cr = this.db.rawQuery(sql, null);
        if (cr != null && cr.moveToFirst()){
            return cr.getInt(cr.getColumnIndexOrThrow("jml"));
        }else{
            return 0;
        }
    }


    public int getCount(boolean thisMonthOnly){
        String sql = "SELECT count(*) as jml FROM pasienqu_record where id <> 0 and active = 'true'";
        if (thisMonthOnly) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH)+1;
            long dateFrom = Global.getMillisDate("01/" + month + "/" + year);
            c.add(Calendar.MONTH, 1);
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH)+1;
            long dateTo = Global.getMillisDate("01/" + month + "/" + year);
            sql += " and record_date >= " + dateFrom + " and record_date < " + dateTo;
        }
        Cursor cr = this.db.rawQuery(sql, null);

        PatientModel tempData;
        if (cr != null && cr.moveToFirst()){
            return cr.getInt(cr.getColumnIndexOrThrow("jml"));
        }else{
            return 0;
        }
    }

    
    public RecordModel getDataByIndex(int index){
        return this.records.get(index);
    }

    public ArrayList<RecordModel> getRecords(String state){
        this.reloadList(state);
        return this.records;
    }

    public ArrayList<RecordModel> getRecords(){
        this.reloadList();
        return this.records;
    }

    public ArrayList<RecordModel> getRecordsExport(){
        this.reloadListExport();
        return this.records;
    }

    public ArrayList<RecordModel> getRecordsLimit(int limit){
        this.reloadListLimited(limit);

        return this.records;
    }

    public ArrayList<RecordModel> getRecordsHistory(int patientId){
        this.reloadHistory(patientId);
        return this.records;
    }

    public ArrayList<RecordModel> getRecordPrescriptionFile(long lastBackup){
        this.reloadPrescriptionFile(lastBackup);
        return this.records;
    }



    public RecordModel getRecordById(String uuid){

        Cursor cr = this.db.rawQuery("SELECT * FROM pasienqu_record where uuid = '"+uuid+"'", null);

        RecordModel tempData = new RecordModel();
        if (cr != null && cr.moveToFirst()){
            tempData = new RecordModel(
                    cr.getInt(cr.getColumnIndexOrThrow("id")),
                    cr.getString(cr.getColumnIndexOrThrow("uuid")),
                    cr.getLong(cr.getColumnIndexOrThrow("record_date")),
                    cr.getInt(cr.getColumnIndexOrThrow("work_location_id")),
                    cr.getInt(cr.getColumnIndexOrThrow("patient_id")),
                    cr.getString(cr.getColumnIndexOrThrow("name")),
                    cr.getDouble(cr.getColumnIndexOrThrow("weight")),
                    cr.getDouble(cr.getColumnIndexOrThrow("temperature")),
                    cr.getInt(cr.getColumnIndexOrThrow("blood_pressure_systolic")),
                    cr.getInt(cr.getColumnIndexOrThrow("blood_pressure_diastolic")),
                    cr.getString(cr.getColumnIndexOrThrow("anamnesa")),
                    cr.getString(cr.getColumnIndexOrThrow("physical_exam")),
                    cr.getString(cr.getColumnIndexOrThrow("diagnosa")),
                    cr.getString(cr.getColumnIndexOrThrow("therapy")),
                    cr.getString(cr.getColumnIndexOrThrow("prescription_file")),
                    cr.getDouble(cr.getColumnIndexOrThrow("total_billing")),
                    cr.getInt(cr.getColumnIndexOrThrow("billing_id")),
                    cr.getInt(cr.getColumnIndexOrThrow("patient_type_id")),
                    cr.getLong(cr.getColumnIndexOrThrow("create_date")),
                    cr.getLong(cr.getColumnIndexOrThrow("write_date"))
            );
            RecordFileTable recordFileTable = new RecordFileTable(context);
            tempData.setFile_ids(recordFileTable.getRecordsById(tempData.getId()));

            RecordDiagnosaTable recordDiagnosaTable = new RecordDiagnosaTable(context);
            tempData.setIcd_ids(recordDiagnosaTable.getRecordsById(tempData.getUuid()));

            BillingTable billingTable = new BillingTable(context);
            tempData.setBilling_ids(billingTable.getRecordsByMedical(tempData.getId()));

        }
        return tempData;
    }

    public ArrayList<RecordModel> getRecordByIdPatient(int patientId){
        Cursor cr = this.db.rawQuery("SELECT * FROM pasienqu_record where patient_id = "+patientId+" ORDER BY record_date desc", null);
        RecordModel tempData = new RecordModel();
        if (cr != null && cr.moveToFirst()) {
            do {
                tempData = new RecordModel(
                        cr.getInt(cr.getColumnIndexOrThrow("id")),
                        cr.getString(cr.getColumnIndexOrThrow("uuid")),
                        cr.getLong(cr.getColumnIndexOrThrow("record_date")),
                        cr.getInt(cr.getColumnIndexOrThrow("work_location_id")),
                        cr.getInt(cr.getColumnIndexOrThrow("patient_id")),
                        cr.getString(cr.getColumnIndexOrThrow("name")),
                        cr.getDouble(cr.getColumnIndexOrThrow("weight")),
                        cr.getDouble(cr.getColumnIndexOrThrow("temperature")),
                        cr.getInt(cr.getColumnIndexOrThrow("blood_pressure_systolic")),
                        cr.getInt(cr.getColumnIndexOrThrow("blood_pressure_diastolic")),
                        cr.getString(cr.getColumnIndexOrThrow("anamnesa")),
                        cr.getString(cr.getColumnIndexOrThrow("physical_exam")),
                        cr.getString(cr.getColumnIndexOrThrow("diagnosa")),
                        cr.getString(cr.getColumnIndexOrThrow("therapy")),
                        cr.getString(cr.getColumnIndexOrThrow("prescription_file")),
                        cr.getDouble(cr.getColumnIndexOrThrow("total_billing")),
                        cr.getInt(cr.getColumnIndexOrThrow("billing_id")),
                        cr.getInt(cr.getColumnIndexOrThrow("patient_type_id")),
                        cr.getLong(cr.getColumnIndexOrThrow("create_date")),
                        cr.getLong(cr.getColumnIndexOrThrow("write_date"))
                );
                RecordFileTable recordFileTable = new RecordFileTable(context);
                tempData.setFile_ids(recordFileTable.getRecordsById(tempData.getId()));

                RecordDiagnosaTable recordDiagnosaTable = new RecordDiagnosaTable(context);
                tempData.setIcd_ids(recordDiagnosaTable.getRecordsById(tempData.getUuid()));

                BillingTable billingTable = new BillingTable(context);
                tempData.setBilling_ids(billingTable.getRecordsByMedical(tempData.getId()));

                this.records.add(tempData);
            } while (cr.moveToNext());
            cr.close();
        }
        return this.records;
    }

    private String addFilter(){
        String filter = "";

        if (diagnosa != null && !diagnosa.equals("")){
            filter += " and diagnosa like '%"+diagnosa+"%'";
        }

        if (idWorkLocation != 0){
            filter += " and work_location_id = "+idWorkLocation;
        }

        if (archived){
            filter += " and active = 'false'";
        }else{
            filter += " and active = 'true'";
        }

        if (patientTypeId != 0){
            filter += " and patient_type_id = "+patientTypeId;
        }

        Calendar c = Calendar.getInstance();
        int year;
        int month;
        long recordFrom;
        long recordTo;
        switch (idxSelectedType) {
            case JConst.idx_filter_calendar_this_month:
//                    year = c.get(Calendar.YEAR);
//                    month = c.get(Calendar.MONTH)+1;
//                    recordFrom = Global.getMillisDate("01/" + month + "/" + year);
//                    c.add(Calendar.MONTH, 1);
//                    year = c.get(Calendar.YEAR);
//                    month = c.get(Calendar.MONTH)+1;
//                    recordTo = Global.getMillisDate("01/" + month + "/" + year);
                recordFrom = Global.StartOfTheMonthLong(0);
                recordTo = Global.EndOfTheMonthLong(0);

                filter += " and record_date >= " + recordFrom + " and record_date <= " + recordTo;
                break;
            case JConst.idx_filter_calendar_last_month:
//                    c.add(Calendar.MONTH, -1);
//                    year = c.get(Calendar.YEAR);
//                    month = c.get(Calendar.MONTH)+1;
//                    recordFrom = Global.getMillisDate("01/" + month + "/" + year);
//                    c.add(Calendar.MONTH, 2);
//                    year = c.get(Calendar.YEAR);
//                    month = c.get(Calendar.MONTH)+1;
//                    recordTo = Global.getMillisDate("01/" + month + "/" + year);

                recordFrom = Global.StartOfTheMonthLong(-1);
                recordTo = Global.EndOfTheMonthLong(-1);
                filter += " and record_date >= " + recordFrom + " and record_date <= " + recordTo;
                break;
            case JConst.idx_filter_calendar_three_month:
//                    c.add(Calendar.MONTH, -3);
//                    year = c.get(Calendar.YEAR);
//                    month = c.get(Calendar.MONTH)+1;
//                    recordFrom = Global.getMillisDate("01/" + month + "/" + year);
//                    c.add(Calendar.MONTH, 4);
//                    year = c.get(Calendar.YEAR);
//                    month = c.get(Calendar.MONTH)+1;
//                    recordTo = Global.getMillisDate("01/" + month + "/" + year);
                recordFrom = Global.StartOfTheMonthLong(-4);
                recordTo = Global.EndOfTheMonthLong(-1);

                filter += " and record_date >= " + recordFrom + " and record_date <= " + recordTo;
                break;
            case JConst.idx_filter_calendar_custom:
                recordFrom = dateFrom;
                recordTo = dateTo;
                filter += " and record_date >= " + recordFrom + " and record_date <= " + recordTo;
                break;
            default:
                break;

        }

        if (!searchQuery.equals("")){
//            filter += " and (name like '%"+searchQuery.replace("'", "''")+"%') ";
            filter += " and (name like '%"+searchQuery.replace("'", "''")+"%') ";
        }

        filter += " ";
        return filter;
    }

    private String addFilterMultipleTable(){
        String filter = "";

        if (diagnosa != null && !diagnosa.equals("")){
            filter += " and r.diagnosa like '%"+diagnosa+"%'";
        }

        if (idWorkLocation != 0){
            filter += " and r.work_location_id = "+idWorkLocation;
        }

        if (archived){
            filter += " and r.active = 'false'";
        }else{
            filter += " and r.active = 'true'";
        }

        if (patientTypeId != 0){
            filter += " and r.patient_type_id = "+patientTypeId;
        }

        long recordFrom;
        long recordTo;
        switch (idxSelectedType) {
            case JConst.idx_filter_calendar_this_month:
                recordFrom = Global.StartOfTheMonthLong(0);
                recordTo = Global.EndOfTheMonthLong(0);

                filter += " and record_date >= " + recordFrom + " and record_date <= " + recordTo;
                break;
            case JConst.idx_filter_calendar_last_month:
                recordFrom = Global.StartOfTheMonthLong(-1);
                recordTo = Global.EndOfTheMonthLong(-1);
                filter += " and record_date >= " + recordFrom + " and record_date <= " + recordTo;
                break;
            case JConst.idx_filter_calendar_three_month:
                recordFrom = Global.StartOfTheMonthLong(-4);
                recordTo = Global.EndOfTheMonthLong(-1);

                filter += " and record_date >= " + recordFrom + " and record_date <= " + recordTo;
                break;
            case JConst.idx_filter_calendar_custom:
                recordFrom = dateFrom;
                recordTo = dateTo;
                filter += " and record_date >= " + recordFrom + " and record_date <= " + recordTo;
                break;
            default:
                break;

        }

        if (!searchQuery.equals("")){
            filter += " and (r.name like '%"+searchQuery.replace("'", "''")+"%') ";
        }

        filter += " ";
        return filter;
    }

    public void setFilter(int idxSelectedType, long dateFrom, long dateTo, String diagnosa, int idWorkLocation, boolean archived, int patientTypeId){
        this.idxSelectedType = idxSelectedType;
        this.idWorkLocation = idWorkLocation;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.diagnosa = diagnosa;
        this.archived = archived;
        this.patientTypeId = patientTypeId;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public void setSorting(String valueSort){
        this.valueSort = valueSort;
    }


    private String addSorting(){
        String sortby = "";

        if (valueSort.equals(context.getString(R.string.value_sorting_record_date_desc)) || valueSort.equals("")){ //default ke record date desc
            sortby = " order by record_date desc, id desc";
        }
        else if (valueSort.equals(context.getString(R.string.value_sorting_record_date_asc))){
            sortby = " order by record_date asc, id asc";
        }

        return sortby;
    }

    private String addSortingMultipleTable(){
        String sortby = "";

        if (valueSort.equals(context.getString(R.string.value_sorting_record_date_desc)) || valueSort.equals("")){ //default ke record date desc
            sortby = " order by r.record_date desc, r.id desc";
        }
        else if (valueSort.equals(context.getString(R.string.value_sorting_record_date_asc))){
            sortby = " order by r.record_date asc, r.id asc";
        }

        return sortby;
    }

    public void isAllDate(boolean isFilterDate) {
        if (isFilterDate) {
            this.idxSelectedType = JConst.idx_filter_calendar_all_dates;
        }
    }


    public void updateIdPasienMedicalAfterSyncV1(){
        Cursor c = this.db.rawQuery("update pasienqu_record set patient_id = (SELECT max(id) as id FROM pasienqu_patient where patient_id = pasienqu_record.prescription_file), prescription_file = '' " +
                "where patient_id = -1 ", null);
        c.moveToFirst();
        c.close();
    }


    public void setOffset(int itemCount) {
        this.offset = itemCount;
    }


    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public int getTotalDatas() {
        int hsl = 0;
        Cursor cr = this.db.rawQuery("SELECT count(*) FROM pasienqu_record " , null);

        if (cr != null && cr.moveToFirst()) {
            hsl = cr.getInt(0);
        }
        return hsl;
    }
}
