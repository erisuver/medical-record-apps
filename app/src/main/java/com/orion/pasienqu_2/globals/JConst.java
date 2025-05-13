package com.orion.pasienqu_2.globals;

import android.os.Environment;

import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;

public class JConst {
    public static String TRUE_STRING = "T";
    public static String FALSE_STRING = "F";
    public static String STATUS_DRAFT     = "draft";
    public static String STATUS_CONFIRMED = "confirmed";
    public static String STATUS_CANCEL    = "canceled";

    public static String STATUS_DRAFT_TEXT     = "Draft";
    public static String STATUS_CONFIRMED_TEXT = "Confirmed";
    public static String STATUS_CANCEL_TEXT    = "Canceled";

    public static String STATUS_ARCHIVE_TRUE        = "true";
    public static String STATUS_ARCHIVE_FALSE        = "false";


    public static String TITLE_HOME_PATIENT        = "P";
    public static String TITLE_HOME_MEDICAL_RECORD = "M";
    public static String TITLE_HOME_BILLING        = "B";

    public static String HOST_SERVER_WITHOUT_HTTP = "35.223.196.200";
    public static String HOST_PORT = ":8069";
    public static String HOST_SERVER = "http://"+HOST_SERVER_WITHOUT_HTTP+HOST_PORT;
    public static String DB_SERVER_NAME = "pasienqu";

    public static final int idx_filter_calendar_this_month  = 0;
    public static final int idx_filter_calendar_last_month  = 1;
    public static final int idx_filter_calendar_three_month = 2;
    public static final int idx_filter_calendar_all_dates   = 3;
    public static final int idx_filter_calendar_custom      = 4;

    public static String value_category_patient_remarks = "patient_remarks";
    public static String value_category_anamnesa = "anamnesa";
    public static String value_category_diagnosa = "diagnosa";
    public static String value_category_physical_exam = "physical_exam";
    public static String value_category_therapy = "therapy";
    public static String value_category_others = "others";

    public static String renewSub30DaysId = "pasienqu30daysadd";
    public static String renewSub365DaysId = "pasienqu365daysadd";

    public static final String billingDay30 = "day30";
    public static final String billingDay365 = "day365";

    public static String renewSub30DaysIdProduct = "pasienqu_pro01";
    public static String renewSub365DaysIdProduct = "pasienqu_pro02";

    public static String gender_model_text = "res.gender";
    public static String work_location_model_text = "pasienqu.work.location";
    public static String note_template_model_text = "pasienqu.note.template";
    public static String billing_template_model_text = "pasienqu.billing.template";
    public static String patient_model_text = "pasienqu.patient";
    public static String appointment_model_text = "pasienqu.appointment";
    public static String billing_model_text = "pasienqu.billing";
    public static String billing_items_model_text = "pasienqu.billing.items";
    public static String user_model_text = "res.users";
    public static String medical_record_text = "pasienqu.medical.record";
    public static String medical_record_diagnosa_model_text = "pasienqu.medical.record.diagnosa";
    public static String medical_record_model_file_text = "pasienqu.medical.record.file";



    public static String text_umum = "Umum";
    public static String text_bpjs = "BPJS";
    public static String text_inhealth = "Inhealth";
    public static String text_asuransi = "Asuransi";
    public static String value_umum  = "1";
    public static String value_bpjs  = "2";
    public static String value_inhealth = "3";
    public static String value_asuransi = "4";


    public static String text_asc = "A-Z";
    public static String text_desc = "Z-A";
    public static String value_asc  = "1";
    public static String value_desc  = "2";

    public static String mediaLocationPath = Environment.getExternalStorageDirectory() + "/Android/Data/"+JApplication.getInstance().getPackageName()+ "/Media/";
    public static String DMPLocationPath = Environment.getExternalStorageDirectory() + "/Android/Data/"+JApplication.getInstance().getPackageName()+ "/Media/dmp/";
//    public static String sharedFolderLocationPath = Environment.getExternalStorageDirectory() + "/Android/media/com.orionit.app.PasienQu/Backup DB/";
    public static String sharedFolderLocationPath = Environment.getExternalStorageDirectory() + "/Android/media/com.orionit.app.PasienQu/Backup DB/";

    public static String sharedFolderLocation = Environment.getExternalStorageDirectory() + "/Android/media/"+JApplication.getInstance().getPackageName()+"/Backup DB/";
    public static String sharedFolderDbv1 = Environment.getExternalStorageDirectory() +"/"+Environment.DIRECTORY_DOCUMENTS+ "/PasienQu/Backup DB/";

    public static String sort_key_patient  = "sort_patient";
    public static String sort_key_medical  = "sort_medical";
    public static String sort_key_billing_template  = "sort_billing";
    public static String sort_key_note_template  = "sort_note";

    public static String value_1day  = "1";
    public static String value_3day  = "2";
    public static String value_7day = "3";
    public static String value_30day = "4";
    public static String value_never = "5";

    public static String value_sorting_newest = "5";
    public static String value_sorting_oldest = "6";

    public static String base64DeveloperKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAuW1mpqvB3iK0Qn42k35utj3bUnCzIkw/JWsdnWjk0VZ+G+49iJ+k9sNRQ5LzXG5Rvtq/oo/Kl81OFRyz37C+8euTKEL9pQZ0P6VpDduaeSv8ZqwAPLBdEFqCoFsBiyIq4qb7183AiI/5EScP9z/8H++/BFkuFTbEjkCoq8V/xhVyn5kfopLsGqmrTd8U21yradV3x6fdnxFUkHE0tEhslPi4ZQw0yYmYQMPaDLwlUE8BmVJCcXlLkWZT6MJgaEgTRAZZiNj6zYHTVFgw7w4XxIV3tmNgdKKc7dXUhPjuzugkAn7LtL6ny9VCmlkUkWYZGxIgNJcWCKOPmjeylNI9zQIDAQAB";

    public static String tipe_filter_date = "date";
    public static String tipe_filter_work_location = "work_location";
    public static String tipe_filter_diagnosa = "diagnosa";
    public static String tipe_filter_patient_type = "patient_type";


    public static String remark_1_value = "1";
    public static String remark_2_value = "2";
    public static String remark_3_value = "3";
    public static String remark_4_value = "4";
    public static String remark_5_value = "5";
    public static String remark_1_text = "-";
    public static String remark_2_text = "Asma";
    public static String remark_3_text = "Alergi";
    public static String remark_4_text = "Sensitif";
    public static String remark_5_text = "Gastritis";


    public static String file_backup_db_name = "PasienQu 2";

    public static String STATUS_API_SUCCESS     = "success";
    public static String STATUS_API_FAILED      = "failed";
    public static final String tag_json_obj = "json_obj_req";
    public static String STATUS_PAYMENT_CANCELED = "cancel";
    public static String STATUS_PAYMENT_SUCCESS = "success";
    public static String STATUS_PAYMENT_PENDING = "pending";
    public static String STATUS_PAYMENT_REFUND = "refund/unknown";
    public static String STATUS_PAYMENT_PENDING_30 = "pending_30";
    public static String STATUS_PAYMENT_PENDING_365 = "pending_365";

    //SATU SEHAT
    public static String ORGANIZATION_ID_ORION = "5dad02f8-fb10-4513-aeac-38e7172a3c94";
    public static String DEFAULT_LOCATION_SATUSEHAT = "Ruang 1";
    public static String value_terdaftar  = "1";
    public static String value_belum_terdaftar  = "2";
    public static String value_data_belum_lengkap = "3";

}


