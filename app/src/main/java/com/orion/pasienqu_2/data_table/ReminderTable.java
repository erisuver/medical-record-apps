package com.orion.pasienqu_2.data_table;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.models.PatientModel;
import com.orion.pasienqu_2.models.ReminderModel;

import java.util.ArrayList;

public class ReminderTable {
    private SQLiteDatabase db;
    private ArrayList<ReminderModel> records;
    private Context context;

    public ReminderTable(Context context) {
        this.context = context;
        this.db = JApplication.getInstance().db;
        this.records = new ArrayList<ReminderModel>();
    }

    private ContentValues setValues(ReminderModel reminderModel){
        ContentValues cv = new ContentValues();
        cv.put("value_reminder", reminderModel.getValue_reminder());
        cv.put("patient_name", reminderModel.getPatient_name());
        cv.put("location", reminderModel.getLocation());
        cv.put("custom_reminder", reminderModel.getCustom_reminder());
        cv.put("date_reminder", reminderModel.getDate_reminder());
        cv.put("appointment", reminderModel.getAppointment());
        return cv;
    }

    public void insert(ReminderModel reminderModel) {
        ContentValues cv = this.setValues(reminderModel);
        this.db.insert("reminder_alarm", null, cv);
    }

    public void update(ReminderModel reminderModel){
        ContentValues cv = this.setValues(reminderModel);
        this.db.update("reminder_alarm", cv, "id = " + reminderModel.getId(), null);
    }

    public void delete(String id){
        this.db.delete("reminder_alarm",  "id = " + id, null);
    }

    public void delete(long value){
        this.db.delete("reminder_alarm",  "value_reminder < " + value, null);
    }

    private void reloadList(){
        this.records.clear();
        Cursor cr = this.db.rawQuery("SELECT * from reminder_alarm", null);

        ReminderModel tempData;
        if (cr != null && cr.moveToFirst()){
            do {
                tempData = new ReminderModel(
                        cr.getInt(cr.getColumnIndexOrThrow("id")),
                        cr.getInt(cr.getColumnIndexOrThrow("value_reminder")),
                        cr.getString(cr.getColumnIndexOrThrow("patient_name")),
                        cr.getString(cr.getColumnIndexOrThrow("location")),
                        cr.getString(cr.getColumnIndexOrThrow("custom_reminder")),
                        cr.getLong(cr.getColumnIndexOrThrow("date_reminder")),
                        cr.getString(cr.getColumnIndexOrThrow("appointment"))
                        );
                this.records.add(tempData);
            } while(cr.moveToNext());
        }
    }

    private void reloadList(long date){
        this.records.clear();
        Cursor cr = this.db.rawQuery("SELECT * from reminder_alarm where date_reminder > "+date, null);

        ReminderModel tempData;
        if (cr != null && cr.moveToFirst()){
            do {
                tempData = new ReminderModel(
                        cr.getInt(cr.getColumnIndexOrThrow("id")),
                        cr.getInt(cr.getColumnIndexOrThrow("value_reminder")),
                        cr.getString(cr.getColumnIndexOrThrow("patient_name")),
                        cr.getString(cr.getColumnIndexOrThrow("location")),
                        cr.getString(cr.getColumnIndexOrThrow("custom_reminder")),
                        cr.getLong(cr.getColumnIndexOrThrow("date_reminder")),
                        cr.getString(cr.getColumnIndexOrThrow("appointment"))
                );
                this.records.add(tempData);
            } while(cr.moveToNext());
        }
    }

    public ArrayList<ReminderModel> getRecords(){
        this.reloadList();
        return this.records;
    }

    public ArrayList<ReminderModel> getRecords(long date){
        this.reloadList(date);
        return this.records;
    }

    public ReminderModel getRecord(){
        Cursor cr = this.db.rawQuery("SELECT * from reminder_alarm", null);
        ReminderModel tempData;
        if (cr != null && cr.moveToFirst()){
            tempData = new ReminderModel(
                    cr.getInt(cr.getColumnIndexOrThrow("id")),
                    cr.getInt(cr.getColumnIndexOrThrow("value_reminder")),
                    cr.getString(cr.getColumnIndexOrThrow("patient_name")),
                    cr.getString(cr.getColumnIndexOrThrow("location")),
                    cr.getString(cr.getColumnIndexOrThrow("custom_reminder")),
                    cr.getLong(cr.getColumnIndexOrThrow("date_reminder")),
                    cr.getString(cr.getColumnIndexOrThrow("appointment"))
            );
            return tempData;
        }else{
            return null;
        }
    }

    public int getMaxId(){
        Cursor cr = this.db.rawQuery("SELECT max(id) as id FROM reminder_alarm", null);
        if (cr != null && cr.moveToFirst()){
            return cr.getInt(cr.getColumnIndexOrThrow("id"));
        }else{
            return 0;
        }
    }

}
