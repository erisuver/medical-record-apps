package com.orion.pasienqu_2.data_table.v1;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.orion.pasienqu_2.DBConnV1;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.data_table.BillingItemTable;
import com.orion.pasienqu_2.data_table.BillingTable;
import com.orion.pasienqu_2.data_table.PatientTable;
import com.orion.pasienqu_2.data_table.RecordTable;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.JConst;
import com.orion.pasienqu_2.models.PatientModel;
import com.orion.pasienqu_2.models.RecordModel;
import com.orion.pasienqu_2.models.SyncInfoModel;
import com.orion.pasienqu_2.models.v1.CatatanMedisModel;
import com.orion.pasienqu_2.models.v1.DataPasienModel;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by User on 19/12/2016.
 */
public class CatatanMedisTable {
    private DBConnV1 dbConn;
    private SQLiteDatabase db;
    private ArrayList<CatatanMedisModel> records;
    private Context context;
    private long seqPasien;

    public CatatanMedisTable(Context context) {
        this.context = context;
        this.dbConn = new DBConnV1(context);
        this.db = this.dbConn.getWritableDatabase();
        this.records = new ArrayList<CatatanMedisModel>();
        //reloadList();
        seqPasien = 0;
    }

    private ContentValues setValues(CatatanMedisModel catatanMedisModel){
        ContentValues cv = new ContentValues();
        cv.put("seq_pasien", catatanMedisModel.getSeq_pasien());
        cv.put("tanggal", catatanMedisModel.getTanggal());
        cv.put("anamnesa", (catatanMedisModel.getAnamnesa()).trim());
        cv.put("pemeriksaan_fisik", (catatanMedisModel.getPemeriksaan_fisik()).trim());
        cv.put("diagnosa", (catatanMedisModel.getDiagnosa()).trim());
        String terapy = catatanMedisModel.getTherapy().trim();
        cv.put("therapi", terapy);
        cv.put("berat", catatanMedisModel.getBerat());
        cv.put("temperatur", catatanMedisModel.getTemperatur());
        cv.put("tensi_1", catatanMedisModel.getTensi_1());
        cv.put("tensi_2", catatanMedisModel.getTensi_2());
        return cv;
    }

    public void insert(CatatanMedisModel catatanMedisModel) {
        ContentValues cv = this.setValues(catatanMedisModel);
        this.db.insert("catatan_medis", null, cv);
        this.reloadList();
    }

    public void update(CatatanMedisModel catatanMedisModel){
        ContentValues cv = this.setValues(catatanMedisModel);
        this.db.update("catatan_medis", cv, "_seq = " + catatanMedisModel.getSeq(), null);
        this.reloadList();
    }

    public void delete(long seq){
        this.db.delete("catatan_medis", "_seq = " + seq, null);
        this.reloadList();
    }

    public void deleteAll(){
        this.db.delete("catatan_medis", null, null);
        this.reloadList();
    }

    private void reloadList(){
        this.records.clear();
        String filter = "";
        if (seqPasien !=0) {
            filter = filter + " and seq_pasien ="+seqPasien+"";
        }
        if (filter != null && !filter.equals("")){
            filter = "where "+filter.substring(4);
        }
        Cursor cr = this.db.rawQuery("SELECT * FROM catatan_medis "+filter+" order by tanggal desc", null);

        CatatanMedisModel tempData;
        if (cr != null && cr.moveToFirst()){
            do {
                tempData = new CatatanMedisModel(
                        cr.getLong(cr.getColumnIndexOrThrow("_seq")),
                        cr.getLong(cr.getColumnIndexOrThrow("seq_pasien")),
                        cr.getLong(cr.getColumnIndexOrThrow("tanggal")),
                        cr.getString(cr.getColumnIndexOrThrow("anamnesa")),
                        cr.getString(cr.getColumnIndexOrThrow("pemeriksaan_fisik")),
                        cr.getString(cr.getColumnIndexOrThrow("diagnosa")),
                        cr.getString(cr.getColumnIndexOrThrow("therapi")),
                        cr.getFloat(cr.getColumnIndexOrThrow("berat")),
                        cr.getFloat(cr.getColumnIndexOrThrow("temperatur")),
                        cr.getFloat(cr.getColumnIndexOrThrow("tensi_1")),
                        cr.getFloat(cr.getColumnIndexOrThrow("tensi_2"))
                );
                this.records.add(tempData);
            } while(cr.moveToNext());
        }
    }

    public CatatanMedisModel getCatatanMedisByIndex(int index){
        return this.records.get(index);
    }

    public CatatanMedisModel getCatatanMedisBySeq(long seq){
        Cursor cr = this.db.rawQuery("SELECT * FROM catatan_medis where _seq = "+seq+" order by tanggal desc", null);

        CatatanMedisModel tempData;
        if (cr != null && cr.moveToFirst()){
            tempData = new CatatanMedisModel(
                    cr.getLong(cr.getColumnIndexOrThrow("_seq")),
                    cr.getLong(cr.getColumnIndexOrThrow("seq_pasien")),
                    cr.getLong(cr.getColumnIndexOrThrow("tanggal")),
                    cr.getString(cr.getColumnIndexOrThrow("anamnesa")),
                    cr.getString(cr.getColumnIndexOrThrow("pemeriksaan_fisik")),
                    cr.getString(cr.getColumnIndexOrThrow("diagnosa")),
                    cr.getString(cr.getColumnIndexOrThrow("therapi")),
                    cr.getFloat(cr.getColumnIndexOrThrow("berat")),
                    cr.getFloat(cr.getColumnIndexOrThrow("temperatur")),
                    cr.getFloat(cr.getColumnIndexOrThrow("tensi_1")),
                    cr.getFloat(cr.getColumnIndexOrThrow("tensi_2"))
            );
            return tempData;
        }else{
            return null;
        }
    }

    public ArrayList<CatatanMedisModel> getRecords(long pasienSeq, boolean isChange){

        if ((this.records.isEmpty()) || (pasienSeq != 0 && pasienSeq != seqPasien) || (isChange = true)) {
            seqPasien = pasienSeq;
            this.reloadList();
        }else{
            seqPasien = pasienSeq;
        }

        return this.records;
    }

    public ArrayList<CatatanMedisModel> getRecords(long pasienSeq){

        if ((this.records.isEmpty()) || (pasienSeq != 0 && pasienSeq != seqPasien)) {
            seqPasien = pasienSeq;
            this.reloadList();
        }else{
            seqPasien = pasienSeq;
        }

        return this.records;
    }

    public ArrayList<CatatanMedisModel> getRecords(){
        return this.records;
    }

    public long getMaxTglBerobatPerPasien(long seqPasien){
        Cursor cr = db.rawQuery("SELECT max(tanggal) FROM catatan_medis where seq_pasien = "+seqPasien+" order by tanggal desc", null);
        long hasil = 0;
        if (cr != null && cr.moveToFirst()){
            hasil = cr.getLong(0);
        }
        return hasil;
    }


    public boolean syncToV2(boolean isClear, ProgressDialog progressDialog,  int offset, int id_location){
        this.records.clear();
        RecordTable recordTable = JApplication.getInstance().recordTable;
        BillingTable billingTable = JApplication.getInstance().billingTable;
        BillingItemTable billingItemTable = JApplication.getInstance().billingItemTable;

        SQLiteDatabase dbLocal = JApplication.getInstance().dbConn.getWritableDatabase();
        DatabaseUtils.InsertHelper iHelp = new DatabaseUtils.InsertHelper(dbLocal, "pasienqu_record");
        int uuid_index = iHelp.getColumnIndex("uuid");
        int record_date_index = iHelp.getColumnIndex("record_date");
        int weight_index = iHelp.getColumnIndex("weight");
        int temperature_index = iHelp.getColumnIndex("temperature");
        int systolic_index = iHelp.getColumnIndex("blood_pressure_systolic");
        int diastolic_index = iHelp.getColumnIndex("blood_pressure_diastolic");
        int anamnesa_index = iHelp.getColumnIndex("anamnesa");
        int physical_exam_index = iHelp.getColumnIndex("physical_exam");
        int diagnosa_index = iHelp.getColumnIndex("diagnosa");
        int therapy_index = iHelp.getColumnIndex("therapy");
        int patient_id_index = iHelp.getColumnIndex("patient_id");
        int prescription_file_index = iHelp.getColumnIndex("prescription_file");
        int name_index = iHelp.getColumnIndex("name");
        int patient_type_id_index = iHelp.getColumnIndex("patient_type_id");
        int work_location_index = iHelp.getColumnIndex("work_location_id");
        int active_index = iHelp.getColumnIndex("active");

        Cursor cr = this.db.rawQuery("SELECT c._seq, c.seq_pasien, c.tanggal, c.anamnesa, c.pemeriksaan_fisik, c.diagnosa, c.therapi, " +
                "c.berat, c.temperatur, c.tensi_1, c.tensi_2, p.id_pasien, p.nama FROM catatan_medis c, data_pasien p " +
                "where p._seq = c.seq_pasien order by p.id_pasien limit 500 offset "+offset, null);

        if (isClear && offset == 0) {
            recordTable.deleteAll();
            billingTable.deleteAll();
            billingItemTable.deleteAll();
        }
        CatatanMedisModel tempData;
        int progress = 0;

        dbLocal.beginTransaction();
        if (cr != null && cr.moveToFirst()){
            do {
                tempData = new CatatanMedisModel(
                        cr.getLong(cr.getColumnIndexOrThrow("_seq")),
                        cr.getLong(cr.getColumnIndexOrThrow("seq_pasien")),
                        cr.getLong(cr.getColumnIndexOrThrow("tanggal")),
                        cr.getString(cr.getColumnIndexOrThrow("anamnesa")),
                        cr.getString(cr.getColumnIndexOrThrow("pemeriksaan_fisik")),
                        cr.getString(cr.getColumnIndexOrThrow("diagnosa")),
                        cr.getString(cr.getColumnIndexOrThrow("therapi")),
                        cr.getFloat(cr.getColumnIndexOrThrow("berat")),
                        cr.getFloat(cr.getColumnIndexOrThrow("temperatur")),
                        cr.getFloat(cr.getColumnIndexOrThrow("tensi_1")),
                        cr.getFloat(cr.getColumnIndexOrThrow("tensi_2"))
                );
                progress += 1;
                progressDialog.setProgress(progress+offset);
                RecordModel recordModel = new RecordModel();
                recordModel.setWeight(tempData.getBerat());
                recordModel.setRecord_date(tempData.getTanggal());
                recordModel.setTemperature(tempData.getTemperatur());
                recordModel.setBlood_pressure_diastolic((int) tempData.getTensi_1());
                recordModel.setAnamnesa(tempData.getAnamnesa());
                recordModel.setPhysical_exam(tempData.getPemeriksaan_fisik());
                recordModel.setDiagnosa(tempData.getDiagnosa());
                recordModel.setTherapy(tempData.getTherapy());
//                int patient_id = patientTable.getIdByIdPatient(cr.getString(cr.getColumnIndexOrThrow("id_pasien")));
                String idPs = cr.getString(cr.getColumnIndexOrThrow("id_pasien"));
//                int patient_id = patientTable.getIdByIdPatient(idPs);
                recordModel.setPatient_id(-1);
                recordModel.setPrescription_file(idPs);
                recordModel.setId((int) tempData.getSeq());
                recordModel.setName(cr.getString(cr.getColumnIndexOrThrow("nama"))+" ("+idPs+")");
                String uuid = UUID.randomUUID().toString();
                recordModel.setUuid(uuid);

                //fix default
                recordModel.setpatient_type_id(Integer.parseInt(JConst.value_umum));
                if(id_location == 0){
                    recordModel.setWork_location_id(1);
                }else {
                    recordModel.setWork_location_id(id_location);
                }

                iHelp.prepareForInsert();
                //apply to index
                iHelp.bind(uuid_index, recordModel.getUuid());
                iHelp.bind(record_date_index, recordModel.getRecord_date());
                iHelp.bind(weight_index, recordModel.getWeight());
                iHelp.bind(temperature_index, recordModel.getTemperature());
                iHelp.bind(systolic_index, recordModel.getBlood_pressure_systolic());
                iHelp.bind(diastolic_index, recordModel.getBlood_pressure_diastolic());
                iHelp.bind(anamnesa_index, recordModel.getAnamnesa());
                iHelp.bind(physical_exam_index, recordModel.getPhysical_exam());
                iHelp.bind(diagnosa_index, recordModel.getDiagnosa());
                iHelp.bind(therapy_index, recordModel.getTherapy());
                iHelp.bind(patient_id_index, recordModel.getPatient_id());
                iHelp.bind(prescription_file_index, recordModel.getPrescription_file());
                iHelp.bind(name_index, recordModel.getName());
                iHelp.bind(patient_type_id_index, recordModel.getpatient_type_id());
                iHelp.bind(work_location_index, recordModel.getWork_location_id());
                iHelp.bind(active_index, "true");

                //insert ke medical record
//                recordTable.insert(recordModel, false);
                iHelp.execute();

            } while(cr.moveToNext());
            cr.close();
        }

        dbLocal.setTransactionSuccessful();
        dbLocal.endTransaction();

        recordTable.setOffset(offset);
        ArrayList<RecordModel> ListRecord = recordTable.getRecordsLimit(500);
        JSONArray jsonArray = new JSONArray();
        for (int i=0; i < ListRecord.size(); i++) {
            jsonArray.put(ListRecord.get(i));
        }
        Gson gson = new Gson();
        String json = gson.toJson(jsonArray);
        SyncInfoModel syncInfoModel = new SyncInfoModel(0, "pasienqu.medical.record", json.getBytes(), "create_batch", "");
        JApplication.getInstance().syncInfoTable.insert(syncInfoModel);

        return true;
    }

    public Integer getTotalDatas(){
        int hsl = 0;
        Cursor cr = this.db.rawQuery("SELECT count(*) FROM catatan_medis " , null);

        if (cr != null && cr.moveToFirst()) {
            hsl = cr.getInt(0);
        }
        return hsl;
    }
}
