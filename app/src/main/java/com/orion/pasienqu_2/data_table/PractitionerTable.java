package com.orion.pasienqu_2.data_table;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Patterns;
import android.widget.Toast;

import com.google.gson.Gson;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.models.PractitionerModel;
import com.orion.pasienqu_2.models.SyncInfoModel;

import java.util.ArrayList;

public class PractitionerTable {
    private SQLiteDatabase db;
    private ArrayList<PractitionerModel> records;
    private Context context;
    private boolean archived = false;

    public PractitionerTable(Context context) {
        this.db = JApplication.getInstance().db;
        this.records = new ArrayList<PractitionerModel>();
        this.context = context;
    }

    public ContentValues setValues (PractitionerModel practitionerModel) {
        ContentValues cv = new ContentValues();
        cv.put("company_id", practitionerModel.getCompany_id());
        cv.put("nomor_ihs", practitionerModel.getNomor_ihs());

        return cv;
    }

    public boolean insert(PractitionerModel practitionerModel) {
        ContentValues cv = this.setValues(practitionerModel);
        this.db.insert("practitioner", null, cv);
        return true;
    }

    public boolean update(PractitionerModel practitionerModel) {
        ContentValues cv = this.setValues(practitionerModel);
        this.db.update("practitioner", cv, "id = " + practitionerModel.getId(), null);
        return true;
    }

    public void deleteAll() {
        this.db.delete("practitioner", null, null);
        this.reloadList();
    }

    public void delete(String uuid){
        this.db.delete("practitioner",  "uuid = '" + uuid + "'", null);
        this.reloadList();
    }

    private void reloadList(){
        this.records.clear();
        Cursor cr = this.db.rawQuery("SELECT * FROM practitioner", null);

        PractitionerModel tempData;
        if (cr != null && cr.moveToFirst()){
            do {
                tempData = new PractitionerModel(
                        cr.getInt(cr.getColumnIndexOrThrow("id")),
                        cr.getInt(cr.getColumnIndexOrThrow("company_id")),
                        cr.getString(cr.getColumnIndexOrThrow("nomor_ihs"))
                );
                this.records.add(tempData);
            } while(cr.moveToNext());
        }

    }


    public PractitionerModel getData(){
        Cursor cr = this.db.rawQuery("SELECT * FROM practitioner ", null);

        PractitionerModel tempData;
        if (cr != null && cr.moveToFirst()){
            tempData = new PractitionerModel(
                    cr.getInt(cr.getColumnIndexOrThrow("id")),
                    cr.getInt(cr.getColumnIndexOrThrow("company_id")),
                    cr.getString(cr.getColumnIndexOrThrow("nomor_ihs"))
            );
            return tempData;
        }else{
            return null;
        }
    }

    private boolean validate(PractitionerModel practitionerModel){
        if (practitionerModel.getCompany_id() == 0 || practitionerModel.getNomor_ihs().equals("")){
            Toast.makeText(context, context.getString(R.string.field_must_be_fill), Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    public ArrayList<PractitionerModel> getRecords() {
        this.reloadList();
        return this.records;
    }


    public PractitionerModel getDataByIndex(int index){
        return this.records.get(index);
    }

}
