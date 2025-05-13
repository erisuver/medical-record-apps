package com.orion.pasienqu_2.globals;

import android.content.Context;

import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.data_table.GenderTable;
import com.orion.pasienqu_2.data_table.WorkLocationTable;
import com.orion.pasienqu_2.models.LovCheckModel;
import com.orion.pasienqu_2.models.LovModel;
import com.orion.pasienqu_2.models.RadioCheckModel;
import com.orion.pasienqu_2.models.WorkLocationModel;

import java.util.ArrayList;
import java.util.List;

public class ListValue {
    public static List<String> list_template_category(Context context) {
        Global.setLanguage(context);  //pencegah bug bahasa saat rotate
        List<String> list = new ArrayList<>();
        list.add(context.getString(R.string.patient_remarks));
        list.add(context.getString(R.string.anamnesa));
        list.add(context.getString(R.string.diagnosa));
        list.add(context.getString(R.string.physical_exam));
        list.add(context.getString(R.string.therapy));
        list.add(context.getString(R.string.others));
        return list;
    }

    public static List<String> list_value_template_category(Context context) {
        List<String> list = new ArrayList<>();
        list.add(JConst.value_category_patient_remarks);
        list.add(JConst.value_category_anamnesa);
        list.add(JConst.value_category_diagnosa);
        list.add(JConst.value_category_physical_exam);
        list.add(JConst.value_category_therapy);
        list.add(JConst.value_category_others);
        return list;
    }

    public static List<String> list_record_date(Context context) {
        List<String> list = new ArrayList<>();
        list.add(context.getString(R.string.this_month));
        list.add(context.getString(R.string.last_month));
        list.add(context.getString(R.string.last_three_month));
        list.add(context.getString(R.string.all_dates));
        list.add(context.getString(R.string.custom));
        return list;
    }

    public static List<String> list_filter_calendar_date(Context context) {
        List<String> list = new ArrayList<>();
        list.add(context.getString(R.string.filter_calendar_this_month));
        list.add(context.getString(R.string.filter_calendar_last_month));
        list.add(context.getString(R.string.filter_calendar_three_month));
        list.add(context.getString(R.string.filter_calendar_all_dates));
        list.add(context.getString(R.string.filter_calendar_custom));
        return list;
    }

    public static List<String> list_work_location(Context context) {
        List<WorkLocationModel> list = new ArrayList<>();
        WorkLocationTable workLocationTable;
        workLocationTable = new WorkLocationTable(context);
        list = workLocationTable.getRecords();
        List<String> listStringGender = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            listStringGender.add(list.get(i).getName());
        }
        return listStringGender;
    }

    public static List<String> list_id_work_location(Context context) {
        List<WorkLocationModel> list = new ArrayList<>();
        WorkLocationTable workLocationTable;
        workLocationTable = new WorkLocationTable(context);
        list = workLocationTable.getRecords();
        List<String> listStringGender = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            listStringGender.add(String.valueOf(list.get(i).getId()));
        }
        return listStringGender;
    }

    public static ArrayList<LovCheckModel> list_reminder(Context context) {
        ArrayList<LovCheckModel> listTemp = new ArrayList<>();
        listTemp.add(new LovCheckModel("0", context.getString(R.string.Reminder0), false));
        listTemp.add(new LovCheckModel("15", context.getString(R.string.Reminder15), false));
        listTemp.add(new LovCheckModel("30", context.getString(R.string.Reminder30), false));
        listTemp.add(new LovCheckModel("60", context.getString(R.string.Reminder60), false));
        listTemp.add(new LovCheckModel("1440", context.getString(R.string.Reminder1440), false));
        listTemp.add(new LovCheckModel("2880", context.getString(R.string.Reminder2880), false));
//        listTemp.add(new LovCheckModel("Custom", context.getString(R.string.ReminderCustom), false));   //sementara custom  ditutup
        listTemp.add(new LovCheckModel("0", context.getString(R.string.ReminderCustom), false));
        return listTemp;
    }


    public static List<LovModel> list_sorting_patient(Context context) {
        List<LovModel> list = new ArrayList<>();
        list.add(new LovModel(context.getString(R.string.value_sorting_youngest_fist), context.getString(R.string.sorting_youngest_fist)));
        list.add(new LovModel(context.getString(R.string.value_sorting_oldest_fist), context.getString(R.string.sorting_oldest_fist)));
        list.add(new LovModel(context.getString(R.string.value_sorting_first_name_alphabetically), context.getString(R.string.sorting_first_name_alphabetically)));
        list.add(new LovModel(context.getString(R.string.value_sorting_first_name_alphabetically_descending), context.getString(R.string.sorting_first_name_alphabetically_descending)));
        list.add(new LovModel(JConst.value_sorting_newest, context.getString(R.string.sort_latest_data)));
        list.add(new LovModel(JConst.value_sorting_oldest, context.getString(R.string.sort_oldest_data)));
        return list;
    }

    public static List<LovModel> list_sorting_record(Context context) {
        List<LovModel> list = new ArrayList<>();
        list.add(new LovModel(context.getString(R.string.value_sorting_record_date_asc), context.getString(R.string.sorting_record_date_asc)));
        list.add(new LovModel(context.getString(R.string.value_sorting_record_date_desc), context.getString(R.string.sorting_record_date_desc)));
        return list;
    }

    public static List<String> list_patient_type(Context context) {
        List<String> list = new ArrayList<>();
        list.add(context.getString(R.string.patient_type_general));
        list.add(context.getString(R.string.patient_type_bpjs));
        list.add(context.getString(R.string.patient_type_inhealth));
        list.add(context.getString(R.string.patient_type_insurance));
        return list;
    }

    //public static String text_umum = "Umum";
    //    public static String text_bpjs = "BPJS";
    //    public static String text_inhealth = "Inhealth";
    //    public static String text_asuransi = "Asuransi";

    public static List<String> list_id_patient_type(Context context) {
        List<String> list = new ArrayList<>();
        list.add(JConst.value_umum);
        list.add(JConst.value_bpjs);
        list.add(JConst.value_inhealth);
        list.add(JConst.value_asuransi);
        return list;
    }

    public static List<LovModel> list_sorting_name_az(Context context) {
        List<LovModel> list = new ArrayList<>();
        list.add(new LovModel(JConst.value_asc, JConst.text_asc));
        list.add(new LovModel(JConst.value_desc, JConst.text_desc));
        return list;
    }

    public static List<String> list_table_contain_archive(Context context) {
        List<String> list = new ArrayList<>();
        list.add("pasienqu_work_location");
        list.add("pasienqu_note_template");
        list.add("pasienqu_billing_template");
        list.add("pasienqu_patient");
        list.add("pasienqu_appointment");
        list.add("pasienqu_record");
        list.add("pasienqu_billing");
        return list;
    }

    public static List<String> list_table_pasienqu() {
        List<String> list = new ArrayList<>();
        list.add("backup_drive");
        list.add("login");
        list.add("pasienqu_work_location");
        list.add("pasienqu_note_template");
        list.add("pasienqu_patient");
        list.add("pasienqu_appointment");
        list.add("pasienqu_record");
        list.add("pasienqu_billing_template");
        list.add("pasienqu_billing_item");
        list.add("pasienqu_billing");
        list.add("pasienqu_record_diagnosa");
        list.add("pasienqu_record_file");
        list.add("practitioner");
        list.add("satu_sehat_token");
        return list;
    }

    public static List<LovModel> list_backup_reminder(Context context) {
        List<LovModel> list = new ArrayList<>();
        list.add(new LovModel(JConst.value_1day, context.getString(R.string.reminder_1day)));
        list.add(new LovModel(JConst.value_3day, context.getString(R.string.reminder_3day)));
        list.add(new LovModel(JConst.value_7day, context.getString(R.string.reminder_7day)));
        list.add(new LovModel(JConst.value_30day, context.getString(R.string.reminder_30day)));
        list.add(new LovModel(JConst.value_never, context.getString(R.string.reminder_never)));
        return list;
    }

    public static List<RadioCheckModel> list_backup_reminder2(Context context) {
        List<RadioCheckModel> list = new ArrayList<>();
        list.add(new RadioCheckModel(JConst.value_1day,"1", context.getString(R.string.reminder_1day)));
        list.add(new RadioCheckModel(JConst.value_3day,"3", context.getString(R.string.reminder_3day)));
        list.add(new RadioCheckModel(JConst.value_7day,"7", context.getString(R.string.reminder_7day)));
        list.add(new RadioCheckModel(JConst.value_30day,"30", context.getString(R.string.reminder_30day)));
        list.add(new RadioCheckModel(JConst.value_never,"0", context.getString(R.string.reminder_never)));
        return list;
    }

    public static List<String> list_string_status_satu_sehat(Context context, boolean isAddAll) {
        List<String> list = new ArrayList<>();
        if (isAddAll){
            list.add(context.getString(R.string.all));
        }
        list.add(context.getString(R.string.terdaftar));
        list.add(context.getString(R.string.belum_terdaftar));
        return list;
    }

    public static List<String> list_value_status_satu_sehat(Context context, boolean isAddAll) {
        List<String> list = new ArrayList<>();
        if (isAddAll){
            list.add("0");
        }
        list.add(JConst.value_terdaftar);
        list.add(JConst.value_belum_terdaftar);
        return list;
    }

}



