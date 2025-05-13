package com.orion.pasienqu_2.data_table.v1;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;
import com.orion.pasienqu_2.DBConn;
import com.orion.pasienqu_2.DBConnV1;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.data_table.PatientTable;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.JConst;
import com.orion.pasienqu_2.globals.SharedPrefsUtils;
import com.orion.pasienqu_2.models.PatientModel;
import com.orion.pasienqu_2.models.RecordModel;
import com.orion.pasienqu_2.models.SyncInfoModel;
import com.orion.pasienqu_2.models.v1.DataPasienModel;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by user1 on 19/12/2016.
 */
public class DataPasienTable {
    private SQLiteDatabase db;
    private ArrayList<DataPasienModel> records;

    private String filterNama;
    private int filterUmur, filterJenisKelamin;
    private String filterAlamat, filterNoTelp;
    private boolean isFilter;

    public int getFilterJenisKelamin() {
        return filterJenisKelamin;
    }

    public boolean isFilter() {
        return isFilter;
    }

    public void setIsFilter(boolean filter) {
        isFilter = filter;
    }

    public void setFilterJenisKelamin(int filterJenisKelamin) {
        this.filterJenisKelamin = filterJenisKelamin;
    }

    public String getFilterNama() {
        return filterNama;
    }

    public void setFilterNama(String filterNama) {
        this.filterNama = filterNama;
    }

    public int getFilterUmur() {
        return filterUmur;
    }

    public void setFilterUmur(int filterUmur) {
        this.filterUmur = filterUmur;
    }

    public String getFilterAlamat() {
        return filterAlamat;
    }

    public void setFilterAlamat(String filterAlamat) {
        this.filterAlamat = filterAlamat;
    }

    public String getFilterNoTelp() {
        return filterNoTelp;
    }

    public void setFilterNoTelp(String filterNoTelp) {
        this.filterNoTelp = filterNoTelp;
    }

    public DataPasienTable(Context context){
        this.db = new DBConnV1(context).getWritableDatabase();
        this.records = new ArrayList<DataPasienModel>();

        filterNama = "";
        filterUmur = 0;
        filterJenisKelamin = 0;
        filterAlamat = "";
        filterNoTelp = "";
        isFilter = false;
    }

    public void resetFilter(){
        filterNama = "";
        filterUmur = 0;
        filterJenisKelamin = 0;
        filterAlamat = "";
        filterNoTelp = "";
        isFilter = false;
    }

    public ContentValues setValues(DataPasienModel DataPasien){
        ContentValues cv = new ContentValues();
        cv.put("id_pasien", DataPasien.getId_pasien());
        cv.put("tgl_daftar", DataPasien.getTgl_daftar());//getDateTime());//cv.put("tgl_daftar", String.valueOf(DataPasien.getTgl_daftar()));
        cv.put("nama", String.format(DataPasien.getNama().trim()));
        cv.put("jenis_kelamin", DataPasien.getJenis_kelamin());
        cv.put("alamat", String.format(DataPasien.getAlamat().trim()));
        cv.put("no_telp", DataPasien.getNo_telp());
        cv.put("tgl_lahir" , DataPasien.getTgl_lahir());
        cv.put("keterangan_1", DataPasien.getKeterangan1());
        cv.put("keterangan_2", String.format(DataPasien.getKeterangan2().trim()));
        cv.put("pekerjaan", String.format(DataPasien.getPekerjaan().trim()));
        cv.put("non_aktif", "F");
        return cv;
    }

    public void update(DataPasienModel DataPasien){
        ContentValues cv = this.setValues(DataPasien);

        this.db.disableWriteAheadLogging();
        this.db.update("data_pasien", cv, "_seq = " + DataPasien.getSeq(), null);
        this.reloadList();
    }

    public void insert(DataPasienModel DataPasien) {
        ContentValues cv = this.setValues(DataPasien);
        Log.w("tgldaftar",cv.get("tgl_daftar").toString());
        this.db.insert("data_pasien", null, cv);
        this.db.rawQuery("pragma wal_checkpoint", null);
        this.reloadList();
    }

     public void delete(long DataPasienSeq){
        this.db.delete("data_pasien", "_seq = " + DataPasienSeq, null);
        this.reloadList();
    }

    private void reloadList(){
        this.records.clear();
        String filter = "";
        if (filterNama != null && !filterNama.equals("")) {
            filter = filter + " and ((nama like '%"+filterNama+"%') or (id_pasien like '%"+filterNama+"%')) ";
        }

        if (filterUmur != 0){
            filter = filter + " and ((100 * (strftime('%Y', 'now') - strftime('%Y', tgl_lahir / 1000, 'unixepoch'))) + \n" +
                                 "(((strftime('%m', 'now') - strftime('%m', tgl_lahir / 1000, 'unixepoch')))))/100= "+filterUmur+" ";
        //    filter = filter + " and (strftime('%Y', 'now') - strftime('%Y', tgl_lahir / 1000, 'unixepoch')) = "+filterUmur+" ";
        }

        if (filterAlamat != null && !filterAlamat.equals("")) {
            filter = filter + " and alamat like '%"+filterAlamat+"%' ";
        }
        if (filterNoTelp != null && !filterNoTelp.equals("")) {
            filter = filter + " and no_telp like '%"+filterNoTelp+"%' ";
        }
        if (filterJenisKelamin != 0) {
            filter = filter + " and jenis_kelamin = '"+filterJenisKelamin+"' ";
        }

        if (filter != null && !filter.equals("")){
            filter = "where non_aktif = 'F' and "+filter.substring(4);
        }else{
            filter = "where non_aktif = 'F' ";
        }
        Log.w("sqllll", "SELECT * FROM data_pasien "+filter+" ORDER BY UPPER(nama) ");
        Cursor cr = this.db.rawQuery("SELECT * FROM data_pasien "+filter+" ORDER BY UPPER(nama) ", null);
        DataPasienModel tempDataPasien;
        if (cr != null && cr.moveToFirst()){
            do {
                tempDataPasien = new DataPasienModel(
                        cr.getLong(cr.getColumnIndexOrThrow("_seq")),
                        cr.getString(cr.getColumnIndexOrThrow("id_pasien")),
                        cr.getLong(cr.getColumnIndexOrThrow("tgl_daftar")),
                        cr.getString(cr.getColumnIndexOrThrow("nama")),
                        cr.getInt(cr.getColumnIndexOrThrow("jenis_kelamin")),
                        cr.getString(cr.getColumnIndexOrThrow("alamat")),
                        cr.getString(cr.getColumnIndexOrThrow("no_telp")),
                        cr.getLong(cr.getColumnIndexOrThrow("tgl_lahir")),
                        cr.getInt(cr.getColumnIndexOrThrow("keterangan_1")),
                        cr.getString(cr.getColumnIndexOrThrow("keterangan_2")),
                        cr.getString(cr.getColumnIndexOrThrow("non_aktif")),
                        cr.getString(cr.getColumnIndexOrThrow("pekerjaan"))
                );
                this.records.add(tempDataPasien);
            } while(cr.moveToNext());
        }
    }

    public DataPasienModel getDataPasienByIndex(int index){
        return this.records.get(index);
    }

    public ArrayList<DataPasienModel> getRecords(String Filter){
        if ((this.records.isEmpty()) || (filterNama != null && Filter != null && Filter.equals(filterNama))) {
            filterNama = Filter;
            this.reloadList();
        }else{
            filterNama = Filter;
        }
        return this.records;
    }


    public ArrayList<DataPasienModel> getRecords(){
        return this.records;
    }


    public ArrayList<DataPasienModel> getRecordsReload(){
        this.reloadList();
        return this.records;
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd-MM-yyyy", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public boolean setNonAktif(long seq){
        ContentValues cv = new ContentValues();
        cv.put("non_aktif", "'T'");
        this.db.update("data_pasien", cv, "_seq = " + seq, null);
        this.reloadList();
        return true;
    }

    public DataPasienModel getPerSeq(long seq){
        Cursor cr = this.db.rawQuery("SELECT * FROM data_pasien where _seq = "+seq+" And non_aktif = 'F' ", null);
        DataPasienModel tempDataPasien;
        if (cr != null && cr.moveToFirst()){
            tempDataPasien = new DataPasienModel(
                    cr.getLong(cr.getColumnIndexOrThrow("_seq")),
                    cr.getString(cr.getColumnIndexOrThrow("id_pasien")),
                    cr.getLong(cr.getColumnIndexOrThrow("tgl_daftar")),
                    cr.getString(cr.getColumnIndexOrThrow("nama")),
                    cr.getInt(cr.getColumnIndexOrThrow("jenis_kelamin")),
                    cr.getString(cr.getColumnIndexOrThrow("alamat")),
                    cr.getString(cr.getColumnIndexOrThrow("no_telp")),
                    cr.getLong(cr.getColumnIndexOrThrow("tgl_lahir")),
                    cr.getInt(cr.getColumnIndexOrThrow("keterangan_1")),
                    cr.getString(cr.getColumnIndexOrThrow("keterangan_2")),
                    cr.getString(cr.getColumnIndexOrThrow("non_aktif")),
                    cr.getString(cr.getColumnIndexOrThrow("pekerjaan"))
            );
        }else{
            tempDataPasien = null;
        }
        return tempDataPasien;
    }

    public String getIdPasien(){
        Cursor cr = this.db.rawQuery("SELECT max(_seq)+1 FROM data_pasien ", null);
        //String hasil = StringUtils.leftPad("foobar", 10, '*');
        if (cr != null && cr.moveToFirst()) {
            long hsl = cr.getLong(0);
            if (hsl == 0){
                hsl = hsl+1;
            }
            String hasil = String.valueOf(hsl);
            int i = 0;
            int panjang = hasil.length();
            while (panjang < 7) {
                hasil = "0" + hasil;
                panjang = panjang + 1;
            }
            return hasil;
        }return "";
    }

    public boolean syncToV2(boolean isClear, ProgressDialog progressDialog, int offset){
        this.records.clear();
        Cursor cr = this.db.rawQuery("SELECT * FROM data_pasien limit 500 offset "+offset, null);
        DataPasienModel tempDataPasien;
        PatientTable patientTable = JApplication.getInstance().patientTable;
        if (isClear && offset == 0) {
            patientTable.deleteAll();
        }

        this.db.beginTransaction();
        int progress = 0;
        if (cr != null && cr.moveToFirst()){
            do {
                tempDataPasien = new DataPasienModel(
                        cr.getLong(cr.getColumnIndexOrThrow("_seq")),
                        cr.getString(cr.getColumnIndexOrThrow("id_pasien")),
                        cr.getLong(cr.getColumnIndexOrThrow("tgl_daftar")),
                        cr.getString(cr.getColumnIndexOrThrow("nama")),
                        cr.getInt(cr.getColumnIndexOrThrow("jenis_kelamin")),
                        cr.getString(cr.getColumnIndexOrThrow("alamat")),
                        cr.getString(cr.getColumnIndexOrThrow("no_telp")),
                        cr.getLong(cr.getColumnIndexOrThrow("tgl_lahir")),
                        cr.getInt(cr.getColumnIndexOrThrow("keterangan_1")),
                        cr.getString(cr.getColumnIndexOrThrow("keterangan_2")),
                        cr.getString(cr.getColumnIndexOrThrow("non_aktif")),
                        cr.getString(cr.getColumnIndexOrThrow("pekerjaan"))
                );
                progress += 1;
                progressDialog.setProgress(progress + offset);

                PatientModel patientModel = new PatientModel();
                patientModel.setPatient_id(tempDataPasien.getId_pasien());
                String name = tempDataPasien.getNama();
                patientModel.setName(name);
                String[] splitStr = name.trim().split("\\s+");
                if (splitStr.length > 0) {
                    patientModel.setFirst_name(splitStr[0]);
                    if (splitStr.length > 1) {
                        String lastName = splitStr[1];
                        for (int j = 2; j < splitStr.length; j++) {
                            lastName += " " + splitStr[j];
                        }
                        patientModel.setSurname(lastName);
                    } else {
                        patientModel.setSurname("");
                    }
                } else {
                    patientModel.setFirst_name(name);
                }

                patientModel.setRegister_date(tempDataPasien.getTgl_daftar());
                patientModel.setGender_id(tempDataPasien.getJenis_kelamin());
                patientModel.setDate_of_birth(tempDataPasien.getTgl_lahir());
                patientModel.setOccupation(tempDataPasien.getPekerjaan());
                patientModel.setContact_no(tempDataPasien.getNo_telp());
                patientModel.setAddress_street_1(tempDataPasien.getAlamat());
                patientModel.setPatient_remark_1(String.valueOf(tempDataPasien.getKeterangan1()));
                patientModel.setPatient_remark_2(tempDataPasien.getKeterangan2());
                patientModel.setUuid(UUID.randomUUID().toString());
                patientModel.setAge(Global.getAgeMonth(patientModel.getDate_of_birth(), true, false));
                patientModel.setId((int) tempDataPasien.getSeq());
                //fix default patient type
                patientModel.setPatient_type_id(Integer.parseInt(JConst.value_umum));
                patientTable.insert(patientModel, false);
            } while(cr.moveToNext());
        }

        db.setTransactionSuccessful();
        db.endTransaction();
        return true;
    }

    public boolean syncTo2(boolean isClear, ProgressDialog progressDialog, int offset){
        this.records.clear();
        SQLiteDatabase dbLocal = JApplication.getInstance().dbConn.getWritableDatabase();
        DatabaseUtils.InsertHelper iHelp = new DatabaseUtils.InsertHelper(dbLocal, "pasienqu_patient");

        int uuid_index = iHelp.getColumnIndex("uuid");
        int patient_id_index = iHelp.getColumnIndex("patient_id");
        int first_name_index = iHelp.getColumnIndex("first_name");
        int surname_index = iHelp.getColumnIndex("surname");

        int register_date_index = iHelp.getColumnIndex("register_date");
        int gender_id_index = iHelp.getColumnIndex("gender_id");
        int date_of_birth_index = iHelp.getColumnIndex("date_of_birth");
        int age_index = iHelp.getColumnIndex("age");
        int occupation_index = iHelp.getColumnIndex("occupation");
        int contact_no_index = iHelp.getColumnIndex("contact_no");
        int address_street_1_index = iHelp.getColumnIndex("address_street_1");
        int patient_remark_1_index = iHelp.getColumnIndex("patient_remark_1");
        int patient_remark_2_index = iHelp.getColumnIndex("patient_remark_2");
        int patient_type_id_index = iHelp.getColumnIndex("patient_type_id");
        int active_index = iHelp.getColumnIndex("active");

        Cursor cr = this.db.rawQuery("SELECT * FROM data_pasien limit 500 offset "+offset, null);
        DataPasienModel tempDataPasien;
        PatientTable patientTable = JApplication.getInstance().patientTable;
        if (isClear && offset == 0) {
            patientTable.deleteAll();
        }


        dbLocal.beginTransaction();

        int progress = 0;
        if (cr != null && cr.moveToFirst()){

            do {
                tempDataPasien = new DataPasienModel(
                        cr.getLong(cr.getColumnIndexOrThrow("_seq")),
                        cr.getString(cr.getColumnIndexOrThrow("id_pasien")),
                        cr.getLong(cr.getColumnIndexOrThrow("tgl_daftar")),
                        cr.getString(cr.getColumnIndexOrThrow("nama")),
                        cr.getInt(cr.getColumnIndexOrThrow("jenis_kelamin")),
                        cr.getString(cr.getColumnIndexOrThrow("alamat")),
                        cr.getString(cr.getColumnIndexOrThrow("no_telp")),
                        cr.getLong(cr.getColumnIndexOrThrow("tgl_lahir")),
                        cr.getInt(cr.getColumnIndexOrThrow("keterangan_1")),
                        cr.getString(cr.getColumnIndexOrThrow("keterangan_2")),
                        cr.getString(cr.getColumnIndexOrThrow("non_aktif")),
                        cr.getString(cr.getColumnIndexOrThrow("pekerjaan"))
                );
                progress += 1;
                progressDialog.setProgress(progress + offset);
                PatientModel patientModel = new PatientModel();
//                boolean alreadyImport = SharedPrefsUtils.getBooleanPreference(JApplication.getInstance(), "already_import", false)
//                if (!isClear){
//                    String lastPatientId = Global.getlastPatientId();
//                    int countPatientId = Global.getCount(JApplication.getInstance().db, "pasienqu_patient", " patient_id = '" +tempDataPasien.getId_pasien()+"'");
//
//                    patientModel.setPatient_id(lastPatientId);
//                }else {
//                    patientModel.setPatient_id(tempDataPasien.getId_pasien());
//                }
                patientModel.setPatient_id(tempDataPasien.getId_pasien());
                String name = tempDataPasien.getNama();
                patientModel.setName(name);
                String[] splitStr = name.trim().split("\\s+");
                if (splitStr.length > 0) {
                    patientModel.setFirst_name(splitStr[0]);
                    if (splitStr.length > 1) {
                        String lastName = splitStr[1];
                        for (int j = 2; j < splitStr.length; j++) {
                            lastName += " " + splitStr[j];
                        }
                        patientModel.setSurname(lastName);
                    } else {
                        patientModel.setSurname("");
                    }
                } else {
                    patientModel.setFirst_name(name);
                }

                patientModel.setRegister_date(tempDataPasien.getTgl_daftar());
                patientModel.setGender_id(tempDataPasien.getJenis_kelamin());
                patientModel.setDate_of_birth(tempDataPasien.getTgl_lahir());
                patientModel.setOccupation(tempDataPasien.getPekerjaan());
                patientModel.setContact_no(tempDataPasien.getNo_telp());
                patientModel.setAddress_street_1(tempDataPasien.getAlamat());
                patientModel.setPatient_remark_1(String.valueOf(tempDataPasien.getKeterangan1()));
                patientModel.setPatient_remark_2(tempDataPasien.getKeterangan2());
                patientModel.setUuid(UUID.randomUUID().toString());
                patientModel.setAge(Global.getAgeMonth(patientModel.getDate_of_birth(), true, false));
                patientModel.setId((int) tempDataPasien.getSeq());
                //fix default patient type
                patientModel.setPatient_type_id(Integer.parseInt(JConst.value_umum));
//                patientTable.insert(patientModel, false);

                iHelp.prepareForInsert();

                iHelp.bind(uuid_index, patientModel.getUuid());
                iHelp.bind(patient_id_index, patientModel.getPatient_id());
                iHelp.bind(first_name_index, patientModel.getFirst_name());
                iHelp.bind(surname_index, patientModel.getSurname());


                iHelp.bind(register_date_index, patientModel.getRegister_date());
                iHelp.bind(gender_id_index, patientModel.getGender_id());
                iHelp.bind(date_of_birth_index, patientModel.getDate_of_birth());
                iHelp.bind(occupation_index, patientModel.getOccupation());
                iHelp.bind(contact_no_index, patientModel.getContact_no());
                iHelp.bind(address_street_1_index, patientModel.getAddress_street_1());
                iHelp.bind(patient_remark_1_index, patientModel.getPatient_remark_1());
                iHelp.bind(patient_remark_2_index, patientModel.getPatient_remark_2());
                iHelp.bind(age_index, patientModel.getAge());
                iHelp.bind(patient_type_id_index, patientModel.getPatient_type_id());
                iHelp.bind(active_index, "true");


                iHelp.execute();
            } while(cr.moveToNext());
            cr.close();
        }

        dbLocal.setTransactionSuccessful();
        dbLocal.endTransaction();

        patientTable.setOffset(offset);
        ArrayList<PatientModel> ListPatient = patientTable.getRecordsLimit(500);
        JSONArray jsonArray = new JSONArray();
        for (int i=0; i < ListPatient.size(); i++) {
            jsonArray.put(ListPatient.get(i));
        }
        Gson gson = new Gson();
        String json = gson.toJson(jsonArray);
        SyncInfoModel syncInfoModel = new SyncInfoModel(0, "pasienqu.patient", json.getBytes(), "create_batch", "");
        JApplication.getInstance().syncInfoTable.insert(syncInfoModel);

        return true;
    }

    public Integer getTotalDatas(){
        int hsl = 0;
        Cursor cr = this.db.rawQuery("SELECT count(*) FROM data_pasien " , null);

        if (cr != null && cr.moveToFirst()) {
            hsl = cr.getInt(0);
        }
        return hsl;
    }
}
