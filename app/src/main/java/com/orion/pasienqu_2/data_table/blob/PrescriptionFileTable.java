package com.orion.pasienqu_2.data_table.blob;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.models.BillingItemModel;
import com.orion.pasienqu_2.models.blob.PrescriptionFileModel;

import java.util.ArrayList;

public class PrescriptionFileTable {
    private SQLiteDatabase dbBlob;
    private ArrayList<PrescriptionFileModel> records;
    private Context context;

    public PrescriptionFileTable(Context context) {
        this.context = context;
        this.dbBlob = JApplication.getInstance().dbBlob;
        this.records = new ArrayList<PrescriptionFileModel>();
    }

    private ContentValues setValues(PrescriptionFileModel prescriptionFileModel){
        ContentValues cv = new ContentValues();
        cv.put("uuid", prescriptionFileModel.getUuid());
        cv.put("record_id", prescriptionFileModel.getRecord_id());
        cv.put("record_uuid", prescriptionFileModel.getRecord_uuid());
        cv.put("prescription_file", prescriptionFileModel.getPrescription_file());
        return cv;
    }

    public void insert(PrescriptionFileModel prescriptionFileModel) {
        ContentValues cv = this.setValues(prescriptionFileModel);
        this.dbBlob.insert("pasienqu_prescription_file", null, cv);
    }

    public void update(PrescriptionFileModel prescriptionFileModel){
        ContentValues cv = this.setValues(prescriptionFileModel);
        this.dbBlob.update("pasienqu_prescription_file", cv, "id = " + prescriptionFileModel.getId(), null);
    }

    public void delete(String recordUuid){
        this.dbBlob.delete("pasienqu_prescription_file",  "record_uuid = '" + recordUuid + "'", null);
    }

    public ArrayList<PrescriptionFileModel> getRecordsByUuid(String record_uuid) {
        this.records.clear();
        Cursor cr = this.dbBlob.rawQuery("SELECT * FROM pasienqu_prescription_file WHERE record_uuid = '" + record_uuid+"'", null);

        PrescriptionFileModel tempData;
        if (cr != null && cr.moveToFirst()) {
            do {
                tempData = new PrescriptionFileModel(
                        cr.getInt(cr.getColumnIndexOrThrow("id")),
                        cr.getString(cr.getColumnIndexOrThrow("uuid")),
                        cr.getInt(cr.getColumnIndexOrThrow("record_id")),
                        cr.getString(cr.getColumnIndexOrThrow("record_uuid")),
                        cr.getString(cr.getColumnIndexOrThrow("prescription_file"))
                );
                this.records.add(tempData);
            } while (cr.moveToNext());
        }
        return this.records;
    }

    public String getPrescriptionFile(String record_uuid){
        Cursor cr = this.dbBlob.rawQuery("SELECT * FROM pasienqu_prescription_file WHERE record_uuid = '" + record_uuid+"'", null);
        if (cr != null && cr.moveToFirst()){
            return cr.getString(cr.getColumnIndexOrThrow("prescription_file"));
        }else{
            return "";
        }
    }


}
