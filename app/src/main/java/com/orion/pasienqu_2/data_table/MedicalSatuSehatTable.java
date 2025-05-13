package com.orion.pasienqu_2.data_table;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.JConst;
import com.orion.pasienqu_2.models.MedicalSatuSehatModel;
import com.orion.pasienqu_2.models.RecordDiagnosaModel;

import java.util.ArrayList;

public class MedicalSatuSehatTable {
    private SQLiteDatabase db;
    private ArrayList<MedicalSatuSehatModel> records;
    private Context context;
    private String filter;
    private int offset = 0;

    public MedicalSatuSehatTable(Context context) {
        this.db = JApplication.getInstance().db;
        this.records = new ArrayList<MedicalSatuSehatModel>();
        this.context = context;
    }

    public ArrayList<MedicalSatuSehatModel> getRecords(int limit) {
        this.reloadList(limit);
        return this.records;
    }

    private void reloadList(int limit){
        this.records.clear();
        String addFilter = this.filter;
        String orderBy = " ORDER BY r.record_date DESC ";
        String sql = "SELECT r.uuid as record_uuid, r.record_date, r.weight, r.temperature, r.blood_pressure_systolic, r.blood_pressure_diastolic, \n" +
                "r.anamnesa, r.physical_exam, r.diagnosa, r.therapy, r.id_encounter, r.isPosted, " +
                "w.name as location, w.organization_ihs, w.location_ihs, w.client_id, w.client_secret, w.token, " +
                "p.patient_id || ' - ' || p.first_name || ' ' || p.surname AS patient_codename, p.identification_no, p.patient_ihs, p.uuid as patient_uuid " +
                "FROM pasienqu_record r, pasienqu_work_location w, pasienqu_patient p " +
                "where r.id <> 0 and w.id = r.work_location_id and p.id = r.patient_id and r.active = 'true' and (w.organization_ihs is not null and w.client_id is not null and w.client_secret is not null and w.location_ihs is not null)"+
                addFilter+orderBy+" limit "+limit+" offset "+offset;
        Cursor cr = this.db.rawQuery(sql, null);

        MedicalSatuSehatModel tempData;
        if (cr != null && cr.moveToFirst()){
            do {
                tempData = new MedicalSatuSehatModel(
                        cr.getString(cr.getColumnIndexOrThrow("record_uuid")),
                        cr.getLong(cr.getColumnIndexOrThrow("record_date")),
                        cr.getDouble(cr.getColumnIndexOrThrow("weight")),
                        cr.getDouble(cr.getColumnIndexOrThrow("temperature")),
                        cr.getInt(cr.getColumnIndexOrThrow("blood_pressure_systolic")),
                        cr.getInt(cr.getColumnIndexOrThrow("blood_pressure_diastolic")),
                        cr.getString(cr.getColumnIndexOrThrow("anamnesa")),
                        cr.getString(cr.getColumnIndexOrThrow("physical_exam")),
                        cr.getString(cr.getColumnIndexOrThrow("diagnosa")),
                        cr.getString(cr.getColumnIndexOrThrow("therapy")),
                        cr.getString(cr.getColumnIndexOrThrow("id_encounter")),
                        cr.getString(cr.getColumnIndexOrThrow("location")),
                        cr.getString(cr.getColumnIndexOrThrow("organization_ihs")),
                        cr.getString(cr.getColumnIndexOrThrow("location_ihs")),
                        cr.getString(cr.getColumnIndexOrThrow("patient_codename")),
                        cr.getString(cr.getColumnIndexOrThrow("identification_no")),
                        cr.getString(cr.getColumnIndexOrThrow("patient_ihs")),
                        cr.getString(cr.getColumnIndexOrThrow("patient_uuid")),
                        cr.getString(cr.getColumnIndexOrThrow("client_id")),
                        cr.getString(cr.getColumnIndexOrThrow("client_secret")),
                        cr.getString(cr.getColumnIndexOrThrow("token")),
                        cr.getString(cr.getColumnIndexOrThrow("isPosted"))
                );
                ArrayList<RecordDiagnosaModel> Diagnosa = JApplication.getInstance().recordDiagnosaTable.getRecordsById(tempData.getRecord_uuid());
                ArrayList<RecordDiagnosaModel> tmpDiagnosa = new ArrayList<>(Diagnosa);
                tempData.setIcd_ids(tmpDiagnosa);
                this.records.add(tempData);
            } while(cr.moveToNext());
        }

    }

    public MedicalSatuSehatModel getDataByIndex(int index){
        return this.records.get(index);
    }

    public void setFilter(String status, int idWorkLocation, long recordFrom, long recordTo){
        this.filter = "";

        if (status != null && !status.equals("")){
            if (status.equals(JConst.value_terdaftar)) {
                this.filter += " and id_encounter is not null ";
            }else if (status.equals(JConst.value_belum_terdaftar)) {
                this.filter += " and id_encounter is null";
            }
        }

        if (idWorkLocation != 0){
            this.filter += " and r.work_location_id = "+idWorkLocation;
        }

        this.filter += " and r.record_date >= " + recordFrom + " and r.record_date <= " + recordTo;
        this.filter += " ";
    }

    public void setOffset(int itemCount) {
        this.offset = itemCount;
    }


}
