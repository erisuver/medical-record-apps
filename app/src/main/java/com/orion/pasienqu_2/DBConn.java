package com.orion.pasienqu_2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.orion.pasienqu_2.models.RecordDiagnosaModel;
import com.orion.pasienqu_2.models.RecordFileModel;

import java.util.List;
import java.util.UUID;

public class DBConn extends SQLiteOpenHelper {
    Context ctx;
    boolean isVacuum = false;
    public DBConn(Context context) {
        super(context, context.getString(R.string.database_name), null, 28);
        ctx = context;
    }


    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS login(user_id varchar, contract_id integer, driver_id integer, vehicle_id integer, cust_name varchar(50), contract_no varchar(50), start_date integer, end_date integer, " +
                                "uid integer, companyId  integer, partnerId  integer, " +
                                "isSuperuser varchar, name varchar, username  varchar, lang varchar, tz varchar, db varchar, fcmProjectId  varchar, sessionId  varchar" +
                                ")");

        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS sync_info(id integer PRIMARY KEY AUTOINCREMENT, model varchar(50), content blob, command varchar(50), uuid_model varchar(255))");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS sync_down(id integer PRIMARY KEY AUTOINCREMENT, model varchar(50), last_update integer, is_sync varchar(1))");

        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS pasienqu_note_template(id integer PRIMARY KEY AUTOINCREMENT, uuid varchar, name varchar(255), category varchar(255), template varchar(255), active varchar(255), company_id integer)");

        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS pasienqu_billing_template(id integer PRIMARY KEY AUTOINCREMENT, uuid varchar, name varchar(255), items varchar, active varchar(255))");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS pasienqu_country(id integer PRIMARY KEY AUTOINCREMENT, uuid varchar(255), name varchar(255))");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS pasienqu_gender(id integer PRIMARY KEY AUTOINCREMENT, uuid varchar(255), name varchar(255))");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS pasienqu_icd10(id integer PRIMARY KEY AUTOINCREMENT, uuid varchar(255), code varchar(255), name varchar(255), remarks varchar(255), isBaru VARCHAR(1) DEFAULT 'F')");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS pasienqu_work_location(id integer PRIMARY KEY AUTOINCREMENT, uuid varchar(255), name varchar(255), location varchar(255), remarks varchar(255), active varchar(255), " +
                "organization_ihs varchar(255), location_ihs varchar(255), client_id varchar(255), client_secret varchar(255), token varchar(255), last_generate_token integer)");

        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS pasienqu_patient(id integer PRIMARY KEY AUTOINCREMENT, uuid varchar(255), patient_id varchar(255), name varchar(255), first_name varchar(255), "+
                                                                            "surname varchar(255), register_date integer, gender_id integer, date_of_birth integer, age integer, month integer, "+
                                                                            "identification_no varchar(255), email varchar(255), occupation varchar(255), contact_no varchar(255), "+
                                                                            "address_street_1 varchar(255), address_street_2 varchar(255), "+
                                                                            "patient_remark_1 varchar(255), patient_remark_2 varchar(255), active varchar(255), patient_type_id integer, description varchar(255), "+
                                                                            "patient_ihs varchar(255)"+
                                                                            ")");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS pasienqu_appointment(id integer PRIMARY KEY AUTOINCREMENT, uuid varchar(255), appointment_date long, work_location_id integer, patient_id integer, reminder varchar(255), notes varchar(255), active varchar(255))");


        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS pasienqu_billing(id integer PRIMARY KEY AUTOINCREMENT, uuid varchar(255), "+
                                    "billing_date integer, patient_id integer, notes varchar(255), total_amount double, name varchar(255), active varchar(255), is_temp varchar(10), medical_record_id integer)");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS pasienqu_billing_item(id integer PRIMARY KEY AUTOINCREMENT, uuid varchar(255), "+
                                    "header_id int, header_uuid varchar(255), name varchar(255), amount double)");

        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS pasienqu_record(id integer PRIMARY KEY AUTOINCREMENT, uuid varchar(255), "+
                                    "record_date integer, work_location_id integer, patient_id integer, name varchar(255),"+
                                    "weight double, temperature double, blood_pressure_systolic, blood_pressure_diastolic integer, anamnesa varchar(255), "+
                                    "physical_exam varchar(255), diagnosa varchar(255), therapy varchar(255), prescription_file varchar(255), "+
                                    "total_billing double, billing_id integer, active varchar(255), patient_type_id integer, create_date integer, write_date integer, " +
                "id_encounter varchar(255), isPosted VARCHAR(1) DEFAULT 'F')");

        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS pasienqu_record_diagnosa(id integer PRIMARY KEY AUTOINCREMENT, uuid varchar(255), "+
                "record_id int, record_uuid varchar(255), icd_code varchar(255), icd_name varchar(255), remarks varchar(255)," +
                "id_condition varchar(255))");

        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS pasienqu_record_file(id integer PRIMARY KEY AUTOINCREMENT, uuid varchar(255), "+
                "record_id int, record_uuid varchar(255), file_name varchar(255), record_file blob, mime_type varchar(255), create_date integer, write_date integer)");

        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS pasienqu_subaccount(id integer PRIMARY KEY AUTOINCREMENT, uuid varchar(255), name varchar(255), login varchar(255), password varchar(255), active varchar(255))");

        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS backup_drive(id integer PRIMARY KEY AUTOINCREMENT, last_backup_data integer, last_backup_media integer)");

        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS reminder_alarm(id integer PRIMARY KEY AUTOINCREMENT, value_reminder integer, patient_name varchar, " +
                "location varchar, date_reminder integer, custom_reminder varchar, appointment varchar )");

        //fix data (keperluan pasienqu offline)
        sqLiteDatabase.execSQL("INSERT INTO pasienqu_gender  (id, name) values (1, 'Male')");
        sqLiteDatabase.execSQL("INSERT INTO pasienqu_gender  (id, name) values (2, 'Female')");
        sqLiteDatabase.execSQL("INSERT INTO pasienqu_work_location (id, name, active, uuid) values (1, 'Default Location', 'true', '"+ UUID.randomUUID().toString()+"')");

        sqLiteDatabase.execSQL("INSERT INTO backup_drive  (id, last_backup_data, last_backup_media) values (1, 0, 0)");
//        insert_sync_down_model(sqLiteDatabase);

//        sqLiteDatabase.execSQL("create index idx_id_pasien on pasienqu_record (patient_id)");
//        sqLiteDatabase.execSQL("create index idx_id_record on pasienqu_record (id)");
//        sqLiteDatabase.execSQL("create index idx_patient_id on pasienqu_patient (id)");

        /** Penambahan Table Keperluan API SATU SEHAT*/
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS practitioner (id integer PRIMARY KEY AUTOINCREMENT, company_id integer, nomor_ihs varchar(255))");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS satu_sehat_token (id integer PRIMARY KEY AUTOINCREMENT, token varchar(255), last_update integer)");

        /** Penambahan Table Keperluan post api nyangkut*/
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS kirim_ulang (id INTEGER PRIMARY KEY AUTOINCREMENT, url VARCHAR, body VARCHAR)");

    }


    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int version, int newVersion) {
        String sql ="";

        if (version <= newVersion) {
            try {
                sqLiteDatabase.execSQL("alter table pasienqu_record add create_date integer");
                sqLiteDatabase.execSQL("alter table pasienqu_record add write_date integer");
            } catch (SQLiteException ex) {
            }

            try {
                /** Penambahan Table Keperluan API SATU SEHAT*/
                sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS practitioner (id integer PRIMARY KEY AUTOINCREMENT, company_id integer, nomor_ihs varchar(255))");
                sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS satu_sehat_token (id integer PRIMARY KEY AUTOINCREMENT, token varchar(255), last_update integer)");
                sqLiteDatabase.execSQL("alter table pasienqu_record add id_encounter varchar(255)");
                sqLiteDatabase.execSQL("alter table pasienqu_record_diagnosa add id_condition varchar(255)");
                sqLiteDatabase.execSQL("alter table pasienqu_patient add patient_ihs varchar(255)");
                sqLiteDatabase.execSQL("alter table pasienqu_work_location add organization_ihs varchar(255)");
                sqLiteDatabase.execSQL("alter table pasienqu_work_location add location_ihs varchar(255)");
            }catch (Exception e) {
                //nothing
            }
            try {
                /** Penambahan Table Keperluan API SATU SEHAT*/
                sqLiteDatabase.execSQL("alter table pasienqu_work_location add client_id varchar(255)");
                sqLiteDatabase.execSQL("alter table pasienqu_work_location add client_secret varchar(255)");
                sqLiteDatabase.execSQL("alter table pasienqu_work_location add token varchar(255)");
                sqLiteDatabase.execSQL("alter table pasienqu_work_location add last_generate_token integer");
            }catch (Exception e) {
                //nothing
            }
            try {
                /** Penambahan Table Keperluan API SATU SEHAT*/
                sqLiteDatabase.execSQL("alter table pasienqu_record add isPosted VARCHAR(1) DEFAULT 'F'");
            }catch (Exception e) {
                //nothing
            }
            try {
                /** Penambahan Table Keperluan API SATU SEHAT*/
                sqLiteDatabase.execSQL("alter table pasienqu_record add isPosted VARCHAR(1) DEFAULT 'F'");
            }catch (Exception e) {
                //nothing
            }
            try {
                /** Penambahan Table Keperluan API SATU SEHAT*/
                sqLiteDatabase.execSQL("alter table pasienqu_icd10 add isBaru VARCHAR(1) DEFAULT 'F'");
            }catch (Exception e) {
                //nothing
            }
            try {
                /** Penambahan Table Keperluan post api nyangkut*/
                sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS kirim_ulang (id INTEGER PRIMARY KEY AUTOINCREMENT, url VARCHAR)");
            }catch (Exception e) {
                //nothing
            }

            try {
                /** Penambahan Table Keperluan post api nyangkut*/
                sqLiteDatabase.execSQL("alter table kirim_ulang add body VARCHAR");
            }catch (Exception e) {
                //nothing
            }



        }
//        this.onCreate(sqLiteDatabase);
    }

    private void insert_sync_down_model(SQLiteDatabase sqLiteDatabase){
        sqLiteDatabase.execSQL("INSERT INTO sync_down (model, last_update, is_sync) values ('res.gender', 0, 'F')");
        sqLiteDatabase.execSQL("INSERT INTO sync_down (model, last_update, is_sync) values ('pasienqu.work.location', 0, 'F')");
        sqLiteDatabase.execSQL("INSERT INTO sync_down (model, last_update, is_sync) values ('pasienqu.note.template', 0, 'F')");
        sqLiteDatabase.execSQL("INSERT INTO sync_down (model, last_update, is_sync) values ('pasienqu.billing.template', 0, 'F')");
        sqLiteDatabase.execSQL("INSERT INTO sync_down (model, last_update, is_sync) values ('pasienqu.patient', 0, 'F')");
//        sqLiteDatabase.execSQL("INSERT INTO sync_down (model, last_update, is_sync) values ('pasienqu.country', 0, 'F')");
        sqLiteDatabase.execSQL("INSERT INTO sync_down (model, last_update, is_sync) values ('pasienqu.appointment', 0, 'F')");
//        sqLiteDatabase.execSQL("INSERT INTO sync_down (model, last_update, is_sync) values ('pasienqu.icd10', 0, 'F')");
        sqLiteDatabase.execSQL("INSERT INTO sync_down (model, last_update, is_sync) values ('pasienqu.medical.record', 0, 'F')");
        sqLiteDatabase.execSQL("INSERT INTO sync_down (model, last_update, is_sync) values ('pasienqu.medical.record.diagnosa', 0, 'F')");
        sqLiteDatabase.execSQL("INSERT INTO sync_down (model, last_update, is_sync) values ('pasienqu.medical.record.file', 0, 'F')");
        sqLiteDatabase.execSQL("INSERT INTO sync_down (model, last_update, is_sync) values ('pasienqu.billing', 0, 'F')");
        sqLiteDatabase.execSQL("INSERT INTO sync_down (model, last_update, is_sync) values ('pasienqu.billing.items', 0, 'F')");
        sqLiteDatabase.execSQL("INSERT INTO sync_down (model, last_update, is_sync) values ('res.users', 0, 'F')");

    }


    public String getDatabaseLocation() {
        String location = ctx.getDatabasePath(ctx.getString(R.string.database_name)).toString();
        return location;
    }

}
