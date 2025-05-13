package com.orion.pasienqu_2.data_table;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.applandeo.materialcalendarview.EventDay;
import com.google.gson.Gson;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.activities.calendar.FilterCalendarActivity;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.JConst;
import com.orion.pasienqu_2.models.AppointmentModel;
import com.orion.pasienqu_2.models.PatientModel;
import com.orion.pasienqu_2.models.SyncInfoModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class AppointmentTable {
    private SQLiteDatabase db;
    private ArrayList<AppointmentModel> records;
    private Context context;


    private int idxSelectedType = 0, idWorkLocation = 0;
    private long dateFrom = 0, dateTo = 0;
    private String notes = "";
    private boolean archived = false;

    public AppointmentTable(Context context) {
        this.context = context;
        this.db = JApplication.getInstance().db;
        this.records = new ArrayList<AppointmentModel>();

    }

    private ContentValues setValues(AppointmentModel appointmentModel, boolean isSave){
        ContentValues cv = new ContentValues();
        cv.put("uuid", appointmentModel.getUuid());
        cv.put("appointment_date", appointmentModel.getAppointment_date());
        cv.put("work_location_id", appointmentModel.getWork_location_id());
        cv.put("patient_id", appointmentModel.getPatient_id());
        cv.put("reminder", appointmentModel.getReminder());
        cv.put("notes", appointmentModel.getNotes());
        if (isSave){
            cv.put("active", "true");
        }
        return cv;
    }

    public boolean insert(AppointmentModel appointmentModel, boolean isSinc) {
        if (isSinc) {
            if (!validate(appointmentModel)) {
                return false;
            }
            ContentValues cv = this.setValues(appointmentModel, true);
            this.db.insert("pasienqu_appointment", null, cv);
        }else{
            ContentValues cv = this.setValues(appointmentModel, true);
            cv.put("id", appointmentModel.getId());
            this.db.insert("pasienqu_appointment", null, cv);
        }

        if (isSinc) {
            Gson gson = new Gson();
            String json = gson.toJson(appointmentModel);
            SyncInfoModel syncInfoModel = new SyncInfoModel(0, "pasienqu.appointment", json.getBytes(), "create", appointmentModel.getUuid());
            JApplication.getInstance().syncInfoTable.insert(syncInfoModel);
        }

        this.reloadList();
        return true;
    }

    public boolean update(AppointmentModel appointmentModel){
        if (!validate(appointmentModel)) {
            return false;
        }
        ContentValues cv = this.setValues(appointmentModel, false);
        this.db.update("pasienqu_appointment", cv, "id = " + appointmentModel.getId(), null);

        Gson gson = new Gson();
        String json = gson.toJson(appointmentModel);
        SyncInfoModel syncInfoModel = new SyncInfoModel(0, "pasienqu.appointment", json.getBytes(), "write", appointmentModel.getUuid());
        JApplication.getInstance().syncInfoTable.insert(syncInfoModel);

        this.reloadList();
        return true;
    }


    private boolean validate(AppointmentModel appointmentModel) {
        if (appointmentModel.getPatient_id() == 0){
            Toast.makeText(context, context.getString(R.string.field_must_be_fill), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (appointmentModel.getWork_location_id() == 0){
            Toast.makeText(context, context.getString(R.string.field_must_be_fill), Toast.LENGTH_SHORT).show();
            return false;
        }
        int count;
        count = Global.getCount(db, "pasienqu_appointment"," appointment_date = " + appointmentModel.getAppointment_date() +
                                                                            " and work_location_id = "+appointmentModel.getWork_location_id() +
                                                                            " and patient_id = "+appointmentModel.getPatient_id()+
                                                                            " and uuid <> '"+appointmentModel.getUuid()+"'");
        if (count > 0 ) {
            Toast.makeText(context, context.getString(R.string.unique_data_error), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public String getSqlLoad(){
        return "SELECT a.id, a.uuid, a.appointment_date, a.work_location_id, a.patient_id, a.reminder, a.notes, p.first_name, p.id, p.surname, p.patient_id "+
                 "FROM pasienqu_appointment a, pasienqu_patient p where a.patient_id = p.id and a.id <> 0 ";
    }


    private void reloadList(){
        this.records.clear();
        Cursor cr = this.db.rawQuery(getSqlLoad(), null);

        AppointmentModel tempData;
        if (cr != null && cr.moveToFirst()){
            do {
                tempData = new AppointmentModel(
                        cr.getInt(cr.getColumnIndexOrThrow("id")),
                        cr.getString(cr.getColumnIndexOrThrow("uuid")),
                        cr.getLong(cr.getColumnIndexOrThrow("appointment_date")),
                        cr.getInt(cr.getColumnIndexOrThrow("work_location_id")),
                        cr.getInt(cr.getColumnIndexOrThrow("patient_id")),
                        cr.getString(cr.getColumnIndexOrThrow("reminder")),
                        cr.getString(cr.getColumnIndexOrThrow("notes"))
                );
                tempData.setPatient_text(cr.getString(cr.getColumnIndexOrThrow("first_name"))+" "+
                        cr.getString(cr.getColumnIndexOrThrow("surname"))+" ("+
                        cr.getString(cr.getColumnIndexOrThrow("patient_id"))+")");
                this.records.add(tempData);
            } while(cr.moveToNext());
        }
    }

    private void reloadList(long date){
        this.records.clear();
        Cursor cr = this.db.rawQuery(getSqlLoad()+" and appointment_date >= "+date+" and appointment_date < "+ Global.addDay(date, 1)+ addFilter(true), null);

        AppointmentModel tempData;
        if (cr != null && cr.moveToFirst()){
            do {
                tempData = new AppointmentModel(
                        cr.getInt(cr.getColumnIndexOrThrow("id")),
                        cr.getString(cr.getColumnIndexOrThrow("uuid")),
                        cr.getLong(cr.getColumnIndexOrThrow("appointment_date")),
                        cr.getInt(cr.getColumnIndexOrThrow("work_location_id")),
                        cr.getInt(cr.getColumnIndexOrThrow("patient_id")),
                        cr.getString(cr.getColumnIndexOrThrow("reminder")),
                        cr.getString(cr.getColumnIndexOrThrow("notes"))
                );
                tempData.setPatient_text(cr.getString(cr.getColumnIndexOrThrow("first_name"))+" "+
                        cr.getString(cr.getColumnIndexOrThrow("surname"))+" ("+
                        cr.getString(cr.getColumnIndexOrThrow("patient_id"))+")");
                this.records.add(tempData);
            } while(cr.moveToNext());
        }
    }

    private void reloadListAppointment(long date){
        this.records.clear();
        Cursor cr = this.db.rawQuery(getSqlLoad()+" and appointment_date >= "+date+" and a.active = 'true'", null);

        AppointmentModel tempData;
        if (cr != null && cr.moveToFirst()){
            do {
                tempData = new AppointmentModel(
                        cr.getInt(cr.getColumnIndexOrThrow("id")),
                        cr.getString(cr.getColumnIndexOrThrow("uuid")),
                        cr.getLong(cr.getColumnIndexOrThrow("appointment_date")),
                        cr.getInt(cr.getColumnIndexOrThrow("work_location_id")),
                        cr.getInt(cr.getColumnIndexOrThrow("patient_id")),
                        cr.getString(cr.getColumnIndexOrThrow("reminder")),
                        cr.getString(cr.getColumnIndexOrThrow("notes"))
                );
                tempData.setPatient_text(cr.getString(cr.getColumnIndexOrThrow("first_name"))+" "+
                        cr.getString(cr.getColumnIndexOrThrow("surname"))+" ("+
                        cr.getString(cr.getColumnIndexOrThrow("patient_id"))+")");
                this.records.add(tempData);
            } while(cr.moveToNext());
        }
    }

    public AppointmentModel getRecordByUuId(String uuid){
//        Cursor cr = this.db.rawQuery(getSqlLoad()+ " and a.uuid = '"+uuid+"'", null);
        Cursor cr = this.db.rawQuery("SELECT * FROM pasienqu_appointment where id <> 0 and uuid = '"+uuid+"'", null);

        AppointmentModel tempData;
        if (cr != null && cr.moveToFirst()){
            tempData = new AppointmentModel(
                cr.getInt(cr.getColumnIndexOrThrow("id")),
                cr.getString(cr.getColumnIndexOrThrow("uuid")),
                cr.getLong(cr.getColumnIndexOrThrow("appointment_date")),
                cr.getInt(cr.getColumnIndexOrThrow("work_location_id")),
                cr.getInt(cr.getColumnIndexOrThrow("patient_id")),
                cr.getString(cr.getColumnIndexOrThrow("reminder")),
                cr.getString(cr.getColumnIndexOrThrow("notes"))
            );
//            tempData.setPatient_text(cr.getString(cr.getColumnIndexOrThrow("first_name"))+" "+
//                    cr.getString(cr.getColumnIndexOrThrow("surname"))+" ("+
//                    cr.getString(cr.getColumnIndexOrThrow("patient_id"))+")");
            this.records.add(tempData);
            return tempData;
        }else{
            return null;
        }
    }

    public AppointmentModel getDataByIndex(int index){
        return this.records.get(index);
    }

    public ArrayList<AppointmentModel> getRecords(){
        this.reloadList();
        return this.records;
    }

    public ArrayList<AppointmentModel> getRecords(int dateType, String note, long workLocation, boolean archive){
        this.reloadList();
        return this.records;
    }

    public ArrayList<AppointmentModel> getRecords(long date){
        this.reloadList(date);
        return this.records;
    }

    public ArrayList<AppointmentModel> getRecordsAppointment(long date){
        this.reloadListAppointment(date);
        return this.records;
    }

    public void deleteAll() {
        this.db.delete("pasienqu_appointment", null, null);
        this.reloadList();
    }

    public void delete(String uuid){
        this.db.delete("pasienqu_appointment",  "uuid = '" + uuid + "'", null);
        this.reloadList();
    }

    public List<EventDay> getListEvent(Context thisActivity){
//        String sql = "select count(*) as value, (appointment_date / (1000*60*60*24))*1000*60*60*24 as app_date  from pasienqu_appointment a where id <> 0 "+
//                addFilter(true)+
//                "group by (appointment_date / (1000*60*60*24))*1000*60*60*24 ";
        String sql = "select count(*) as value, (appointment_date) as app_date  from pasienqu_appointment a where id <> 0 "+
                addFilter(true)+
                "group by (appointment_date) order by app_date";
        Cursor cr = this.db.rawQuery(sql, null);

        List<EventDay> events = new ArrayList<>();
        AppointmentModel tempData;

        int countValue = 0;
        String lastDate = "";
        long appDate = 0;
        if (cr != null && cr.moveToFirst()){
            do {
//                Calendar calendar = Calendar.getInstance();
//                TimeZone mTimeZone = calendar.getTimeZone();
//                int mGMTOffset = mTimeZone.getRawOffset();
//                long gmttozero = TimeUnit.MILLISECONDS.convert(mGMTOffset, TimeUnit.MILLISECONDS);
//                calendar.setTimeInMillis(cr.getLong(cr.getColumnIndexOrThrow("app_date"))- gmttozero);

//                Calendar calendar = Calendar.getInstance();
//                calendar.setTimeInMillis(cr.getLong(cr.getColumnIndexOrThrow("app_date")));
//                String value = cr.getString(cr.getColumnIndexOrThrow("value"));
//                events.add(new EventDay(calendar, Global.getCircleDrawableWithText(thisActivity, value)));

                String date = Global.getDateFormated(cr.getLong(cr.getColumnIndexOrThrow("app_date")));
                if (date.equals(lastDate)){
                    countValue += cr.getInt(cr.getColumnIndexOrThrow("value"));
                    appDate = cr.getLong(cr.getColumnIndexOrThrow("app_date"));
                }else {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(appDate);
                    String value = String.valueOf(countValue);
                    events.add(new EventDay(calendar, Global.getCircleDrawableWithText(thisActivity, value)));

                    countValue = cr.getInt(cr.getColumnIndexOrThrow("value"));
                    appDate = cr.getLong(cr.getColumnIndexOrThrow("app_date"));
                }
                lastDate = date;
            } while(cr.moveToNext());
        }else{
            return events;
        }

        if (countValue > 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(appDate);
            String value = String.valueOf(countValue);
            events.add(new EventDay(calendar, Global.getCircleDrawableWithText(thisActivity, value)));
        }
        return events;
    }

    public int getCount(){
        long date = Global.serverNowLong();
        String sql = "SELECT count(*) as jml FROM pasienqu_appointment where id <> 0 and active = 'true' and appointment_date >= "+date;

        Cursor cr = this.db.rawQuery( sql, null);

        if (cr != null && cr.moveToFirst()){
            return cr.getInt(cr.getColumnIndexOrThrow("jml"));
        }else{
            return 0;
        }
    }

    private String addFilter(boolean withoutDate){
        String filter = "";
        if (idWorkLocation != 0){
            filter += " and a.work_location_id = "+idWorkLocation;
        }
        if (notes != null && !notes.equals("")){
            filter += " and a.notes like '%"+notes+"%'";
        }

        if (archived){
            filter += " and a.active = 'false'";
        }else{
            filter += " and a.active = 'true'";
        }
//        int a = JConst.idx_filter_calendar_this_month;
        if (withoutDate) {
            Calendar c = Calendar.getInstance();
            int year;
            int month;
            long appointmentFrom;
            long appointmentTo;
            switch (idxSelectedType) {
                case JConst.idx_filter_calendar_this_month:
//                    year = c.get(Calendar.YEAR);
//                    month = c.get(Calendar.MONTH)+1;
//                    appointmentFrom = Global.getMillisDate("01/" + month + "/" + year);
//                    c.add(Calendar.MONTH, 1);
//                    year = c.get(Calendar.YEAR);
//                    month = c.get(Calendar.MONTH)+1;
//                    appointmentTo = Global.getMillisDate("01/" + month + "/" + year);
                    appointmentFrom = Global.StartOfTheMonthLong(0);
                    appointmentTo = Global.EndOfTheMonthLong(0);

                    filter += " and appointment_date >= " + appointmentFrom + " and appointment_date <= " + appointmentTo;
                    break;
                case JConst.idx_filter_calendar_last_month:
//                    c.add(Calendar.MONTH, -1);
//                    year = c.get(Calendar.YEAR);
//                    month = c.get(Calendar.MONTH)+1;
//                    appointmentFrom = Global.getMillisDate("01/" + month + "/" + year);
//                    c.add(Calendar.MONTH, 2);
//                    year = c.get(Calendar.YEAR);
//                    month = c.get(Calendar.MONTH)+1;
//                    appointmentTo = Global.getMillisDate("01/" + month + "/" + year);

                    appointmentFrom = Global.StartOfTheMonthLong(-1);
                    appointmentTo = Global.EndOfTheMonthLong(-1);
                    filter += " and appointment_date >= " + appointmentFrom + " and appointment_date <= " + appointmentTo;
                    break;
                case JConst.idx_filter_calendar_three_month:
//                    c.add(Calendar.MONTH, -3);
//                    year = c.get(Calendar.YEAR);
//                    month = c.get(Calendar.MONTH)+1;
//                    appointmentFrom = Global.getMillisDate("01/" + month + "/" + year);
//                    c.add(Calendar.MONTH, 4);
//                    year = c.get(Calendar.YEAR);
//                    month = c.get(Calendar.MONTH)+1;
//                    appointmentTo = Global.getMillisDate("01/" + month + "/" + year);
                    appointmentFrom = Global.StartOfTheMonthLong(-4);
                    appointmentTo = Global.EndOfTheMonthLong(-1);

                    filter += " and appointment_date >= " + appointmentFrom + " and appointment_date <= " + appointmentTo;
                    break;
                case JConst.idx_filter_calendar_custom:
                    appointmentFrom = dateFrom;
                    appointmentTo = dateTo;
                    filter += " and appointment_date >= " + appointmentFrom + " and appointment_date <= " + appointmentTo;
                    break;
                default:
                    break;

            }
        }
        filter += " ";
        return filter;
    }

    public void setFilter(int idxSelectedType, int idWorkLocation, long dateFrom, long dateTo, String notes, boolean archived){
        this.idxSelectedType = idxSelectedType;
        this.idWorkLocation = idWorkLocation;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.notes = notes;
        this.archived = archived;
    }

}