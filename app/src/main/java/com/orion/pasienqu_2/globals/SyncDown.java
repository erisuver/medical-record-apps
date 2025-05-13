package com.orion.pasienqu_2.globals;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.data_table.AppointmentTable;
import com.orion.pasienqu_2.data_table.BillingItemTable;
import com.orion.pasienqu_2.data_table.BillingTable;
import com.orion.pasienqu_2.data_table.CountryTable;
import com.orion.pasienqu_2.data_table.GenderTable;
import com.orion.pasienqu_2.data_table.GlobalTable;
import com.orion.pasienqu_2.data_table.ICDTable;
import com.orion.pasienqu_2.data_table.PatientTable;
import com.orion.pasienqu_2.data_table.RecordDiagnosaTable;
import com.orion.pasienqu_2.data_table.RecordFileTable;
import com.orion.pasienqu_2.data_table.RecordTable;
import com.orion.pasienqu_2.data_table.SubaccountTable;
import com.orion.pasienqu_2.data_table.SyncDownTable;
import com.orion.pasienqu_2.data_table.SyncInfoTable;
import com.orion.pasienqu_2.data_table.WorkLocationTable;
import com.orion.pasienqu_2.data_table.more.BillingTemplateTable;
import com.orion.pasienqu_2.data_table.more.NoteTemplateTable;
import com.orion.pasienqu_2.models.AppointmentModel;
import com.orion.pasienqu_2.models.BillingItemModel;
import com.orion.pasienqu_2.models.BillingModel;
import com.orion.pasienqu_2.models.BillingTemplateModel;
import com.orion.pasienqu_2.models.CountryModel;
import com.orion.pasienqu_2.models.GenderModel;
import com.orion.pasienqu_2.models.ICDModel;
import com.orion.pasienqu_2.models.LoginCompanyModel;
import com.orion.pasienqu_2.models.LoginInformationModel;
import com.orion.pasienqu_2.models.PatientModel;
import com.orion.pasienqu_2.models.RecordDiagnosaModel;
import com.orion.pasienqu_2.models.RecordFileModel;
import com.orion.pasienqu_2.models.RecordModel;
import com.orion.pasienqu_2.models.SubaccountModel;
import com.orion.pasienqu_2.models.SyncDownModel;
import com.orion.pasienqu_2.models.SyncInfoModel;
import com.orion.pasienqu_2.models.WorkLocationModel;
import com.orion.pasienqu_2.models.more.NoteTemplateModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SyncDown {

    public static void sync_all(Activity activity, Context appContext, Runnable runnable, ProgressBar progressBar) {
        SyncDownTable syncDownTable = JApplication.getInstance().syncDownTable;
        ArrayList<SyncDownModel> arrayList = syncDownTable.getRecords();
        if (arrayList.size() > 0) {
            String filter = "";

            syncDown(activity, appContext, arrayList.get(0).getModel(), progressBar, runnable, filter);
        } else {
            runnable.run();
        }
    }

    public static void syncDown(Activity activity, Context appContext, String modelName, ProgressBar progressBar, Runnable runnable, String filter) {
//        runnable.run();
        LoginInformationModel loginInformationModel = JApplication.getInstance().loginInformationModel;
        SyncDownTable syncDownTable = JApplication.getInstance().syncDownTable;
        String last_update = Global.getDateTimeFormatedOdoo(syncDownTable.getLastUpdate(modelName));
        filter = "?parameter=[[\"__last_update\", \">=\",\"" + last_update + "\"]]";

        if (modelName.equals("res.users")) {
            filter += "&filter=[[\"is_subuser\",\"=\",\"true\"]]";
        }
        String url = JConst.HOST_SERVER + "/api/" + modelName + filter;

        StringRequest jArr = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    if (new JSONObject(response).has("result")) {
                        try {
                            JSONArray jsonArray = new JSONObject(response).getJSONArray("result");
                            process_json_result(activity, appContext, modelName, progressBar, runnable, jsonArray);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                            runnable.run();
                        }

                        JApplication.getInstance().setLoginCompanyBySharedPreferences();
                    } else {
                        SyncDownTable syncDownTable = JApplication.getInstance().syncDownTable;
                        syncDownTable.update_isSync("F", modelName);
                        runnable.run();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                    //jika error ke yang lain dulu
                    SyncDownTable syncDownTable = JApplication.getInstance().syncDownTable;
                    syncDownTable.update_isSync("F", modelName);
                    runnable.run();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activity, error.toString(), Toast.LENGTH_SHORT).show();
                //jika error ke yang lain dulu
                SyncDownTable syncDownTable = JApplication.getInstance().syncDownTable;
                syncDownTable.update_isSync("F", modelName);
                runnable.run();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Cookie", JApplication.getInstance().loginInformationModel.getCookie());
                return headers;
            }
        };
        JApplication.getInstance().addToRequestQueue(jArr, Global.tag_json_obj);
    }


    public static void process_json_result(Activity activity, Context appContext, String modelName, ProgressBar progressBar, Runnable runnable, JSONArray jsonArray) {
        try {
//            int progres = 0;
//            progressBar.setProgress(progres);
//            Toast.makeText(activity, modelName, Toast.LENGTH_SHORT).show();

            SyncDownTable syncDownTable = JApplication.getInstance().syncDownTable;
            SyncDownModel syncDownModel = syncDownTable.getRecord(modelName);

            ArrayList<SyncDownModel> arrayList = new ArrayList<>();
            arrayList.addAll(syncDownTable.getRecords());
            double sizeArr = arrayList.size();
//            ArrayList<SyncDownModel> arrayListTot = syncDownTable.getRecordsAll();
            ArrayList<SyncDownModel> arrayListTot = new ArrayList<>();
            arrayListTot.addAll(syncDownTable.getRecordsAll());
            double sizeArrTot = arrayListTot.size();


            double sizePerGrup = 1 / sizeArrTot * 100;

            double progres = ((sizeArrTot - sizeArr) * sizePerGrup);
            progressBar.setProgress((int) progres);

            if (modelName.equals("res.gender")) {
                GenderTable genderTable = JApplication.getInstance().genderTable;
//                genderTable.deleteAll();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    GenderModel genderModel = new GenderModel();
                    genderModel.setId(jsonObject.getInt("id"));
                    genderModel.setName(jsonObject.getString("name"));
                    genderModel.setUuid(jsonObject.getString("uuid"));

                    syncDownModel.setLast_update(Global.getMillisDateTimeFromOdoo(jsonObject.getString("__last_update")));
                    syncDownTable.update(syncDownModel, modelName);
                    genderTable.delete(genderModel.getUuid());
                    genderTable.insert(genderModel, false);
                }
            } else if (modelName.equals("pasienqu.work.location")) {
                WorkLocationTable workLocationTable = JApplication.getInstance().workLocationTable;
                workLocationTable.deleteAll();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    WorkLocationModel workLocationModel = new WorkLocationModel();
                    workLocationModel.setId(jsonObject.getInt("id"));
                    workLocationModel.setName(jsonObject.getString("name"));
                    if (jsonObject.get("location").equals(false)) {
                        workLocationModel.setLocation("");
                    } else {
                        workLocationModel.setLocation(jsonObject.getString("location"));
                    }
                    if (jsonObject.get("remarks").equals(false)) {
                        workLocationModel.setRemarks("");
                    } else {
                        workLocationModel.setRemarks(jsonObject.getString("remarks"));
                    }
                    workLocationModel.setUuid(jsonObject.getString("uuid"));

                    syncDownModel.setLast_update(Global.getMillisDateTimeFromOdoo(jsonObject.getString("__last_update")));
                    syncDownTable.update(syncDownModel, modelName);
                    workLocationTable.delete(workLocationModel.getUuid());
                    workLocationTable.insert(workLocationModel, false);
                }
            } else if (modelName.equals("pasienqu.note.template")) {
                NoteTemplateTable noteTemplateTable = JApplication.getInstance().noteTemplateTable;
//                noteTemplateTable.deleteAll();
                for (int i = 0; i < jsonArray.length(); i++) {
//                    int total = jsonArray.length();
//                    int x = i+1;
//
//                    progres = (x/total)*100;
//                    progressBar.setProgress((int) progres);

                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    NoteTemplateModel noteTemplateModel = new NoteTemplateModel();
                    noteTemplateModel.setId(jsonObject.getInt("id"));
                    noteTemplateModel.setName(jsonObject.getString("name"));
                    noteTemplateModel.setCategory(jsonObject.getString("category"));
                    noteTemplateModel.setTemplate(jsonObject.getString("template"));
                    noteTemplateModel.setUuid(jsonObject.getString("uuid"));

                    syncDownModel.setLast_update(Global.getMillisDateTimeFromOdoo(jsonObject.getString("__last_update")));
                    syncDownTable.update(syncDownModel, modelName);
                    noteTemplateTable.delete(noteTemplateModel.getUuid());
                    noteTemplateTable.insert(noteTemplateModel, false);
                }

            } else if (modelName.equals("pasienqu.appointment")) {
                AppointmentTable appointmentTable = JApplication.getInstance().appointmentTable;
//                appointmentTable.deleteAll();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    AppointmentModel appointmentModel = new AppointmentModel();
                    appointmentModel.setId(jsonObject.getInt("id"));
                    appointmentModel.setUuid(jsonObject.getString("uuid"));
                    appointmentModel.setAppointment_date(Global.getMillisDateTimeFromOdoo(jsonObject.getString("appointment_date")));
                    appointmentModel.setWork_location_id(jsonObject.getInt("work_location_id"));
                    appointmentModel.setPatient_id(jsonObject.getInt("patient_id"));
//                    appointmentModel.setReminder(jsonObject.getString("reminder"));
                    appointmentModel.setNotes(jsonObject.getString("notes"));

                    syncDownModel.setLast_update(Global.getMillisDateTimeFromOdoo(jsonObject.getString("__last_update")));
                    syncDownTable.update(syncDownModel, modelName);
                    appointmentTable.delete(appointmentModel.getUuid());
                    appointmentTable.insert(appointmentModel, false);
                }
            } else if (modelName.equals("pasienqu.country")) {
                CountryTable countryTable = JApplication.getInstance().countryTable;
                countryTable.deleteAll();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    CountryModel countryModel = new CountryModel();
                    countryModel.setId(jsonObject.getInt("id"));
                    countryModel.setUuid(jsonObject.getString("uuid"));
                    countryModel.setName(jsonObject.getString("name"));
                    countryTable.insert(countryModel, false);
                }
            } else if (modelName.equals("pasienqu.icd10")) {
                ICDTable icdTable = JApplication.getInstance().icdTable;
                icdTable.deleteAll();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    ICDModel icdModel = new ICDModel();
                    icdModel.setId(jsonObject.getInt("id"));
                    icdModel.setUuid(jsonObject.getString("uuid"));
                    icdModel.setName(jsonObject.getString("description"));
                    icdModel.setCode(jsonObject.getString("name"));
                    icdModel.setRemarks(jsonObject.getString("display_name"));
                    icdTable.insert(icdModel, false);
                }
            } else if (modelName.equals("pasienqu.patient")) {
                PatientTable patientTable = JApplication.getInstance().patientTable;
                patientTable.deleteAll();
                for (int i = 0; i < jsonArray.length(); i++) {

//                    ArrayList<SyncDownModel> arrayList = syncDownTable.getRecords();
//                    ArrayList<SyncDownModel> arrayListTot = syncDownTable.getRecordsAll();
//                    double sizeArr = arrayList.size();
//                    double sizeArrTot = arrayListTot.size();
//
//                    double sizePerGrup = 1/sizeArrTot * 100;
//

//                    double total = jsonArray.length();
//                    double x = i+1;
//
//                    progres = ((x*sizePerGrup)/total);
//                    progres = progres + ((sizeArrTot-sizeArr) * sizePerGrup);
//                    progressBar.setProgress((int) progres);

                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    PatientModel patientModel = new PatientModel();
                    patientModel.setId(jsonObject.getInt("id"));
                    patientModel.setUuid(jsonObject.getString("uuid"));
                    patientModel.setPatient_id(jsonObject.getString("patient_id"));
                    patientModel.setName(jsonObject.getString("name"));
                    patientModel.setFirst_name(jsonObject.getString("first_name"));
                    patientModel.setSurname(jsonObject.getString("surname"));
                    patientModel.setRegister_date(Global.getMillisDateStrip(jsonObject.getString("register_date")));
                    patientModel.setGender_id(jsonObject.getInt("gender_id"));
                    patientModel.setDate_of_birth(Global.getMillisDateStrip(jsonObject.getString("date_of_birth")));
                    patientModel.setAge(jsonObject.getInt("age"));
                    patientModel.setIdentification_no(jsonObject.getString("identification_no"));
                    patientModel.setEmail(jsonObject.getString("email"));
                    patientModel.setOccupation(jsonObject.getString("occupation"));
                    patientModel.setContact_no(jsonObject.getString("contact_no"));
                    patientModel.setAddress_street_1(jsonObject.getString("address_street_1"));
                    patientModel.setAddress_street_2(jsonObject.getString("address_street_2"));
                    patientModel.setPatient_remark_1(jsonObject.getString("patient_remark_1"));
                    patientModel.setPatient_remark_2(jsonObject.getString("patient_remark_2"));
                    patientTable.setArchived(jsonObject.getBoolean("active"));

                    syncDownModel.setLast_update(Global.getMillisDateTimeFromOdoo(jsonObject.getString("__last_update")));
                    syncDownTable.update(syncDownModel, modelName);
                    patientTable.delete(patientModel.getUuid());
                    patientTable.insert(patientModel, false);
                }
            } else if (modelName.equals("pasienqu.billing")) {
                BillingTable billingTable = JApplication.getInstance().billingTable;
                billingTable.deleteAll();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    BillingModel billingModel = new BillingModel();
                    billingModel.setId(jsonObject.getInt("id"));
                    billingModel.setUuid(jsonObject.getString("uuid"));
                    billingModel.setBilling_date(Global.getMillisDateStrip(jsonObject.getString("billing_date")));
                    billingModel.setPatient_id(jsonObject.getInt("patient_id"));
                    billingModel.setNotes(jsonObject.getString("notes"));
                    billingModel.setTotal_amount(jsonObject.getDouble("total_amount"));
                    billingModel.setName(jsonObject.getString("name"));

                    if (!jsonObject.get("medical_record_id").equals(false)) {
                        billingModel.setMedical_record_id(jsonObject.getInt("medical_record_id"));
                    }

                    syncDownModel.setLast_update(Global.getMillisDateTimeFromOdoo(jsonObject.getString("__last_update")));
                    syncDownTable.update(syncDownModel, modelName);
                    billingTable.delete(billingModel.getUuid());
                    billingTable.insert(billingModel, false);
                }
            } else if (modelName.equals("pasienqu.billing.template")) {
                BillingTemplateTable billingTemplateTable = JApplication.getInstance().billingTemplateTable;
                billingTemplateTable.deleteAll();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    BillingTemplateModel billingTemplateModel = new BillingTemplateModel();
                    billingTemplateModel.setId(jsonObject.getInt("id"));
                    billingTemplateModel.setUuid(jsonObject.getString("uuid"));
                    billingTemplateModel.setName(jsonObject.getString("name"));
                    billingTemplateModel.setItems(jsonObject.getString("items"));

                    syncDownModel.setLast_update(Global.getMillisDateTimeFromOdoo(jsonObject.getString("__last_update")));
                    syncDownTable.update(syncDownModel, modelName);
                    billingTemplateTable.delete(billingTemplateModel.getUuid());
                    billingTemplateTable.insert(billingTemplateModel, false);
                }
            } else if (modelName.equals("pasienqu.billing.item")) {
                BillingItemTable billingItemTable = JApplication.getInstance().billingItemTable;
                billingItemTable.deleteAll();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    BillingItemModel billingItemModel = new BillingItemModel();
                    billingItemModel.setId(jsonObject.getInt("id"));
                    billingItemModel.setUuid(jsonObject.getString("uuid"));
                    billingItemModel.setHeader_id(jsonObject.getInt("header_id"));
                    billingItemModel.setName(jsonObject.getString("name"));
                    billingItemModel.setAmount(jsonObject.getDouble("amount"));

                    syncDownModel.setLast_update(Global.getMillisDateTimeFromOdoo(jsonObject.getString("__last_update")));
                    syncDownTable.update(syncDownModel, modelName);
                    billingItemTable.delete(billingItemModel.getUuid());
                    billingItemTable.insert(billingItemModel, false);
                }
            } else if (modelName.equals("pasienqu.medical.record")) {
                RecordTable recordTable = JApplication.getInstance().recordTable;
                recordTable.deleteAll();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    RecordModel recordModel = new RecordModel();
                    recordModel.setId(jsonObject.getInt("id"));
                    recordModel.setUuid(jsonObject.getString("uuid"));
                    recordModel.setRecord_date(Global.getMillisDateStrip(jsonObject.getString("record_date")));
                    recordModel.setWork_location_id(jsonObject.getInt("work_location_id"));
                    recordModel.setPatient_id(jsonObject.getInt("patient_id"));
//                    recordModel.setName(jsonObject.getString("name"));
                    recordModel.setName(jsonObject.getString("patient_first_name") + jsonObject.getString("patient_surname") + " (" + jsonObject.getString("patient_patient_id") + ")");
                    recordModel.setWeight(jsonObject.getDouble("weight"));
                    recordModel.setTemperature(jsonObject.getDouble("temperature"));
                    recordModel.setBlood_pressure_systolic(jsonObject.getInt("blood_pressure_systolic"));
                    recordModel.setBlood_pressure_diastolic(jsonObject.getInt("blood_pressure_diastolic"));
                    recordModel.setAnamnesa(jsonObject.getString("anamnesa"));
                    recordModel.setPhysical_exam(jsonObject.getString("physical_exam"));
                    recordModel.setDiagnosa(jsonObject.getString("diagnosa"));
                    recordModel.setTherapy(jsonObject.getString("therapy"));
                    recordModel.setPrescription_file(jsonObject.getString("prescription_file"));
                    recordModel.setTotal_billing(jsonObject.getDouble("total_billing"));
//                    recordModel.setBilling_id(jsonObject.getInt("billing_ids"));
                    recordTable.setArchived(jsonObject.getBoolean("active"));

                    syncDownModel.setLast_update(Global.getMillisDateTimeFromOdoo(jsonObject.getString("__last_update")));
                    syncDownTable.update(syncDownModel, modelName);
                    recordTable.delete(recordModel.getUuid());
                    recordTable.insert(recordModel, false);
                }
            } else if (modelName.equals("pasienqu.medical.record.diagnosa")) {
                RecordDiagnosaTable recordDiagnosaTable = JApplication.getInstance().recordDiagnosaTable;
                recordDiagnosaTable.deleteAll();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    RecordDiagnosaModel recordDiagnosaModel = new RecordDiagnosaModel();
                    recordDiagnosaModel.setId(jsonObject.getInt("id"));
                    recordDiagnosaModel.setUuid(jsonObject.getString("uuid"));
                    recordDiagnosaModel.setRecord_id(jsonObject.getInt("record_id"));
//                    recordDiagnosaModel.setRecord_uuid(jsonObject.getString("record_uuid"));
                    recordDiagnosaModel.setIcd_code(jsonObject.getString("icd_code"));
                    recordDiagnosaModel.setIcd_name(jsonObject.getString("icd_name"));
                    recordDiagnosaModel.setRemarks(jsonObject.getString("remarks"));

                    syncDownModel.setLast_update(Global.getMillisDateTimeFromOdoo(jsonObject.getString("__last_update")));
                    syncDownTable.update(syncDownModel, modelName);
                    recordDiagnosaTable.delete(recordDiagnosaModel.getUuid());
                    recordDiagnosaTable.insert(recordDiagnosaModel, false);
                }
            } else if (modelName.equals("pasienqu.medical.record.file")) {
                RecordFileTable recordFileTable = JApplication.getInstance().recordFileTable;
                recordFileTable.deleteAll();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    RecordFileModel recordFileModel = new RecordFileModel();
                    recordFileModel.setId(jsonObject.getInt("id"));
                    recordFileModel.setUuid(jsonObject.getString("uuid"));
                    recordFileModel.setRecord_id(jsonObject.getInt("record_id"));
//                    recordFileModel.setRecord_uuid(jsonObject.getString("record_uuid"));
                    recordFileModel.setFile_name(jsonObject.getString("file_name"));
                    recordFileModel.setRecord_file(jsonObject.getString("record_file"));
                    recordFileModel.setMime_type(jsonObject.getString("mime_type"));

                    syncDownModel.setLast_update(Global.getMillisDateTimeFromOdoo(jsonObject.getString("__last_update")));
                    syncDownTable.update(syncDownModel, modelName);
                    recordFileTable.delete(recordFileModel.getUuid());
                    recordFileTable.insert(recordFileModel, false);
                }
            } else if (modelName.equals("res.users")) {
                SubaccountTable subaccountTable = JApplication.getInstance().subaccountTable;
                subaccountTable.deleteAll();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    SubaccountModel subaccountModel = new SubaccountModel();
                    subaccountModel.setId(jsonObject.getInt("id"));
                    subaccountModel.setUuid(jsonObject.getString("uuid"));
                    subaccountModel.setName(jsonObject.getString("name"));
                    subaccountModel.setLogin(jsonObject.getString("login"));
                    subaccountModel.setPassword(jsonObject.getString("password"));

                    syncDownModel.setLast_update(Global.getMillisDateTimeFromOdoo(jsonObject.getString("__last_update")));
                    syncDownTable.update(syncDownModel, modelName);
                    subaccountTable.delete(subaccountModel.getUuid());
                    subaccountTable.insert(subaccountModel, false);
                }
            }

            int totprogres = 0;
            progressBar.setMax(100);

            syncDownTable.update_isSync("F", modelName);
            ArrayList<SyncDownModel> syncInfoModelArrayList = syncDownTable.getRecords();
            if (syncInfoModelArrayList.size() == 0) {
                runnable.run();
            } else {
                sync_all(activity, appContext, runnable, progressBar);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
            runnable.run();
            //jika error ke yang lain dulu
            SyncDownTable syncDownTable = JApplication.getInstance().syncDownTable;
            syncDownTable.update_isSync("F", modelName);
        }
//        runnable.run();
    }


//    ----------------------------------------------------------------------------------------------------

    //    ----------------------------------------------------------------------------------------------------
    public static void sync_all(Activity activity, Context appContext, Runnable runnable, ProgressBar progressBar, TextView labelProgres) {
        SyncDownTable syncDownTable = JApplication.getInstance().syncDownTable;
        ArrayList<SyncDownModel> arrayList = syncDownTable.getRecords();
        if (arrayList.size() > 0) {
            String filter = "";

            syncDown(activity, appContext, arrayList.get(0).getModel(), progressBar, runnable, filter, labelProgres);
        } else {
            runnable.run();
        }
    }

    public static void syncDown(Activity activity, Context appContext, String modelName, ProgressBar progressBar, Runnable runnable, String filter, TextView labelProgres) {
//        runnable.run();
        LoginInformationModel loginInformationModel = JApplication.getInstance().loginInformationModel;
        SyncDownTable syncDownTable = JApplication.getInstance().syncDownTable;
        String last_update = Global.getDateTimeFormatedOdoo(syncDownTable.getLastUpdate(modelName));
        filter = "?parameter=[[\"__last_update\", \">=\",\"" + last_update + "\"]]";

        if (modelName.equals("res.users")) {
            filter += "&filter=[[\"is_subuser\",\"=\",\"true\"]]";
        }
        String url = JConst.HOST_SERVER + "/api/" + modelName + filter;

        StringRequest jArr = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    if (new JSONObject(response).has("result")) {
                        try {
                            JSONArray jsonArray = new JSONObject(response).getJSONArray("result");
                            process_json_result(activity, appContext, modelName, progressBar, runnable, jsonArray, labelProgres);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                            runnable.run();
                        }

                        JApplication.getInstance().setLoginCompanyBySharedPreferences();
                    } else {
                        SyncDownTable syncDownTable = JApplication.getInstance().syncDownTable;
                        syncDownTable.update_isSync("F", modelName);
                        runnable.run();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                    //jika error ke yang lain dulu
                    SyncDownTable syncDownTable = JApplication.getInstance().syncDownTable;
                    syncDownTable.update_isSync("F", modelName);
                    runnable.run();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activity, error.toString(), Toast.LENGTH_SHORT).show();
                //jika error ke yang lain dulu
                SyncDownTable syncDownTable = JApplication.getInstance().syncDownTable;
                syncDownTable.update_isSync("F", modelName);
                runnable.run();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Cookie", JApplication.getInstance().loginInformationModel.getCookie());
                return headers;
            }
        };
        JApplication.getInstance().addToRequestQueue(jArr, Global.tag_json_obj);
    }


    public static void process_json_result(Activity activity, Context appContext, String modelName, ProgressBar progressBar, Runnable runnable, JSONArray jsonArray, TextView labelProgres) {
        try {

            SyncDownTable syncDownTable = JApplication.getInstance().syncDownTable;
            SyncDownModel syncDownModel = syncDownTable.getRecord(modelName);

            ArrayList<SyncDownModel> arrayList = new ArrayList<>();
            arrayList.addAll(syncDownTable.getRecords());
            double sizeArr = arrayList.size();
//            ArrayList<SyncDownModel> arrayListTot = syncDownTable.getRecordsAll();
            ArrayList<SyncDownModel> arrayListTot = new ArrayList<>();
            arrayListTot.addAll(syncDownTable.getRecordsAll());
            double sizeArrTot = arrayListTot.size();


            double sizePerGrup = 1 / sizeArrTot * 100;

            double progres = ((sizeArrTot - sizeArr) * sizePerGrup);
            progressBar.setProgress((int) progres);
            labelProgres.setText(Global.getNameLoading(appContext, modelName));

            if (modelName.equals("res.gender")) {
                GenderTable genderTable = JApplication.getInstance().genderTable;
//                genderTable.deleteAll();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    GenderModel genderModel = new GenderModel();
                    genderModel.setId(jsonObject.getInt("id"));
                    genderModel.setName(jsonObject.getString("name"));
                    genderModel.setUuid(jsonObject.getString("uuid"));

                    syncDownModel.setLast_update(Global.getMillisDateTimeFromOdoo(jsonObject.getString("__last_update")));
                    syncDownTable.update(syncDownModel, modelName);
                    genderTable.delete(genderModel.getUuid());
                    genderTable.insert(genderModel, false);
                }
            } else if (modelName.equals("pasienqu.work.location")) {
                WorkLocationTable workLocationTable = JApplication.getInstance().workLocationTable;
//                workLocationTable.deleteAll();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    WorkLocationModel workLocationModel = new WorkLocationModel();
                    workLocationModel.setId(jsonObject.getInt("id"));
                    workLocationModel.setName(jsonObject.getString("name"));
                    if (jsonObject.get("location").equals(false)) {
                        workLocationModel.setLocation("");
                    } else {
                        workLocationModel.setLocation(jsonObject.getString("location"));
                    }
                    if (jsonObject.get("remarks").equals(false)) {
                        workLocationModel.setRemarks("");
                    } else {
                        workLocationModel.setRemarks(jsonObject.getString("remarks"));
                    }
                    workLocationModel.setUuid(jsonObject.getString("uuid"));

                    syncDownModel.setLast_update(Global.getMillisDateTimeFromOdoo(jsonObject.getString("__last_update")));
                    syncDownTable.update(syncDownModel, modelName);
                    workLocationTable.delete(workLocationModel.getUuid());
                    workLocationTable.insert(workLocationModel, false);
                }
            } else if (modelName.equals("pasienqu.note.template")) {
                NoteTemplateTable noteTemplateTable = JApplication.getInstance().noteTemplateTable;
//                noteTemplateTable.deleteAll();
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    NoteTemplateModel noteTemplateModel = new NoteTemplateModel();
                    noteTemplateModel.setId(jsonObject.getInt("id"));
                    noteTemplateModel.setName(jsonObject.getString("name"));
                    noteTemplateModel.setCategory(jsonObject.getString("category"));
                    noteTemplateModel.setTemplate(jsonObject.getString("template"));
                    noteTemplateModel.setUuid(jsonObject.getString("uuid"));

                    syncDownModel.setLast_update(Global.getMillisDateTimeFromOdoo(jsonObject.getString("__last_update")));
                    syncDownTable.update(syncDownModel, modelName);
                    noteTemplateTable.delete(noteTemplateModel.getUuid());
                    noteTemplateTable.insert(noteTemplateModel, false);
                }

            } else if (modelName.equals("pasienqu.appointment")) {
                AppointmentTable appointmentTable = JApplication.getInstance().appointmentTable;
//                appointmentTable.deleteAll();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    AppointmentModel appointmentModel = new AppointmentModel();
                    appointmentModel.setId(jsonObject.getInt("id"));
                    appointmentModel.setUuid(jsonObject.getString("uuid"));
                    appointmentModel.setAppointment_date(Global.getMillisDateTimeFromOdoo(jsonObject.getString("appointment_date")));
                    appointmentModel.setWork_location_id(jsonObject.getInt("work_location_id"));
                    appointmentModel.setPatient_id(jsonObject.getInt("patient_id"));
//                    appointmentModel.setReminder(jsonObject.getString("reminder"));
                    appointmentModel.setNotes(jsonObject.getString("notes"));

                    syncDownModel.setLast_update(Global.getMillisDateTimeFromOdoo(jsonObject.getString("__last_update")));
                    syncDownTable.update(syncDownModel, modelName);
                    appointmentTable.delete(appointmentModel.getUuid());
                    appointmentTable.insert(appointmentModel, false);
                }
            } else if (modelName.equals("pasienqu.country")) {
                CountryTable countryTable = JApplication.getInstance().countryTable;
                countryTable.deleteAll();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    CountryModel countryModel = new CountryModel();
                    countryModel.setId(jsonObject.getInt("id"));
                    countryModel.setUuid(jsonObject.getString("uuid"));
                    countryModel.setName(jsonObject.getString("name"));
                    countryTable.insert(countryModel, false);
                }
            } else if (modelName.equals("pasienqu.icd10")) {
                ICDTable icdTable = JApplication.getInstance().icdTable;
                icdTable.deleteAll();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    ICDModel icdModel = new ICDModel();
                    icdModel.setId(jsonObject.getInt("id"));
                    icdModel.setUuid(jsonObject.getString("uuid"));
                    icdModel.setName(jsonObject.getString("description"));
                    icdModel.setCode(jsonObject.getString("name"));
                    icdModel.setRemarks(jsonObject.getString("display_name"));
                    icdTable.insert(icdModel, false);
                }
            } else if (modelName.equals("pasienqu.patient")) {
                PatientTable patientTable = JApplication.getInstance().patientTable;
                patientTable.deleteAll();
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    PatientModel patientModel = new PatientModel();
                    patientModel.setId(jsonObject.getInt("id"));
                    patientModel.setUuid(jsonObject.getString("uuid"));
                    patientModel.setPatient_id(jsonObject.getString("patient_id"));
                    patientModel.setName(jsonObject.getString("name"));
                    patientModel.setFirst_name(jsonObject.getString("first_name"));
                    patientModel.setSurname(jsonObject.getString("surname"));
                    patientModel.setRegister_date(Global.getMillisDateStrip(jsonObject.getString("register_date")));
                    patientModel.setGender_id(jsonObject.getInt("gender_id"));
                    patientModel.setDate_of_birth(Global.getMillisDateStrip(jsonObject.getString("date_of_birth")));
                    patientModel.setAge(jsonObject.getInt("age"));
                    patientModel.setIdentification_no(jsonObject.getString("identification_no"));
                    patientModel.setEmail(jsonObject.getString("email"));
                    patientModel.setOccupation(jsonObject.getString("occupation"));
                    patientModel.setContact_no(jsonObject.getString("contact_no"));
                    patientModel.setAddress_street_1(jsonObject.getString("address_street_1"));
                    patientModel.setAddress_street_2(jsonObject.getString("address_street_2"));
                    patientModel.setPatient_remark_1(jsonObject.getString("patient_remark_1"));
                    patientModel.setPatient_remark_2(jsonObject.getString("patient_remark_2"));
                    patientTable.setArchived(jsonObject.getBoolean("active"));

                    syncDownModel.setLast_update(Global.getMillisDateTimeFromOdoo(jsonObject.getString("__last_update")));
                    syncDownTable.update(syncDownModel, modelName);
                    patientTable.delete(patientModel.getUuid());
                    patientTable.insert(patientModel, false);
                }
            } else if (modelName.equals("pasienqu.billing")) {
                BillingTable billingTable = JApplication.getInstance().billingTable;
                billingTable.deleteAll();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    BillingModel billingModel = new BillingModel();
                    billingModel.setId(jsonObject.getInt("id"));
                    billingModel.setUuid(jsonObject.getString("uuid"));
                    billingModel.setBilling_date(Global.getMillisDateStrip(jsonObject.getString("billing_date")));
                    billingModel.setPatient_id(jsonObject.getInt("patient_id"));
                    billingModel.setNotes(jsonObject.getString("notes"));
                    billingModel.setTotal_amount(jsonObject.getDouble("total_amount"));
                    billingModel.setName(jsonObject.getString("name"));

                    if (!jsonObject.get("medical_record_id").equals(false)) {
                        billingModel.setMedical_record_id(jsonObject.getInt("medical_record_id"));
                    }

                    syncDownModel.setLast_update(Global.getMillisDateTimeFromOdoo(jsonObject.getString("__last_update")));
                    syncDownTable.update(syncDownModel, modelName);
                    billingTable.delete(billingModel.getUuid());
                    billingTable.insert(billingModel, false);
                }
            } else if (modelName.equals("pasienqu.billing.template")) {
                BillingTemplateTable billingTemplateTable = JApplication.getInstance().billingTemplateTable;
                billingTemplateTable.deleteAll();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    BillingTemplateModel billingTemplateModel = new BillingTemplateModel();
                    billingTemplateModel.setId(jsonObject.getInt("id"));
                    billingTemplateModel.setUuid(jsonObject.getString("uuid"));
                    billingTemplateModel.setName(jsonObject.getString("name"));
                    billingTemplateModel.setItems(jsonObject.getString("items"));

                    syncDownModel.setLast_update(Global.getMillisDateTimeFromOdoo(jsonObject.getString("__last_update")));
                    syncDownTable.update(syncDownModel, modelName);
                    billingTemplateTable.delete(billingTemplateModel.getUuid());
                    billingTemplateTable.insert(billingTemplateModel, false);
                }
            } else if (modelName.equals("pasienqu.billing.item")) {
                BillingItemTable billingItemTable = JApplication.getInstance().billingItemTable;
                billingItemTable.deleteAll();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    BillingItemModel billingItemModel = new BillingItemModel();
                    billingItemModel.setId(jsonObject.getInt("id"));
                    billingItemModel.setUuid(jsonObject.getString("uuid"));
                    billingItemModel.setHeader_id(jsonObject.getInt("header_id"));
                    billingItemModel.setName(jsonObject.getString("name"));
                    billingItemModel.setAmount(jsonObject.getDouble("amount"));

                    syncDownModel.setLast_update(Global.getMillisDateTimeFromOdoo(jsonObject.getString("__last_update")));
                    syncDownTable.update(syncDownModel, modelName);
                    billingItemTable.delete(billingItemModel.getUuid());
                    billingItemTable.insert(billingItemModel, false);
                }
            } else if (modelName.equals("pasienqu.medical.record")) {
                RecordTable recordTable = JApplication.getInstance().recordTable;
                recordTable.deleteAll();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    RecordModel recordModel = new RecordModel();
                    recordModel.setId(jsonObject.getInt("id"));
                    recordModel.setUuid(jsonObject.getString("uuid"));
                    recordModel.setRecord_date(Global.getMillisDateStrip(jsonObject.getString("record_date")));
                    recordModel.setWork_location_id(jsonObject.getInt("work_location_id"));
                    recordModel.setPatient_id(jsonObject.getInt("patient_id"));
//                    recordModel.setName(jsonObject.getString("name"));
                    recordModel.setName(jsonObject.getString("patient_first_name") + jsonObject.getString("patient_surname") + " (" + jsonObject.getString("patient_patient_id") + ")");
                    recordModel.setWeight(jsonObject.getDouble("weight"));
                    recordModel.setTemperature(jsonObject.getDouble("temperature"));
                    recordModel.setBlood_pressure_systolic(jsonObject.getInt("blood_pressure_systolic"));
                    recordModel.setBlood_pressure_diastolic(jsonObject.getInt("blood_pressure_diastolic"));
                    recordModel.setAnamnesa(jsonObject.getString("anamnesa"));
                    recordModel.setPhysical_exam(jsonObject.getString("physical_exam"));
                    recordModel.setDiagnosa(jsonObject.getString("diagnosa"));
                    recordModel.setTherapy(jsonObject.getString("therapy"));
                    recordModel.setPrescription_file(jsonObject.getString("prescription_file"));
                    recordModel.setTotal_billing(jsonObject.getDouble("total_billing"));
//                    recordModel.setBilling_id(jsonObject.getInt("billing_ids"));
                    recordTable.setArchived(jsonObject.getBoolean("active"));

                    syncDownModel.setLast_update(Global.getMillisDateTimeFromOdoo(jsonObject.getString("__last_update")));
                    syncDownTable.update(syncDownModel, modelName);
                    recordTable.delete(recordModel.getUuid());
                    recordTable.insert(recordModel, false);
                }
            } else if (modelName.equals("pasienqu.medical.record.diagnosa")) {
                RecordDiagnosaTable recordDiagnosaTable = JApplication.getInstance().recordDiagnosaTable;
                recordDiagnosaTable.deleteAll();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    RecordDiagnosaModel recordDiagnosaModel = new RecordDiagnosaModel();
                    recordDiagnosaModel.setId(jsonObject.getInt("id"));
                    recordDiagnosaModel.setUuid(jsonObject.getString("uuid"));
                    recordDiagnosaModel.setRecord_id(jsonObject.getInt("record_id"));
//                    recordDiagnosaModel.setRecord_uuid(jsonObject.getString("record_uuid"));
                    recordDiagnosaModel.setIcd_code(jsonObject.getString("icd_code"));
                    recordDiagnosaModel.setIcd_name(jsonObject.getString("icd_name"));
                    recordDiagnosaModel.setRemarks(jsonObject.getString("remarks"));

                    syncDownModel.setLast_update(Global.getMillisDateTimeFromOdoo(jsonObject.getString("__last_update")));
                    syncDownTable.update(syncDownModel, modelName);
                    recordDiagnosaTable.delete(recordDiagnosaModel.getUuid());
                    recordDiagnosaTable.insert(recordDiagnosaModel, false);
                }
            } else if (modelName.equals("pasienqu.medical.record.file")) {
                RecordFileTable recordFileTable = JApplication.getInstance().recordFileTable;
                recordFileTable.deleteAll();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    RecordFileModel recordFileModel = new RecordFileModel();
                    recordFileModel.setId(jsonObject.getInt("id"));
                    recordFileModel.setUuid(jsonObject.getString("uuid"));
                    recordFileModel.setRecord_id(jsonObject.getInt("record_id"));
//                    recordFileModel.setRecord_uuid(jsonObject.getString("record_uuid"));
                    recordFileModel.setFile_name(jsonObject.getString("file_name"));
                    recordFileModel.setRecord_file(jsonObject.getString("record_file"));
                    recordFileModel.setMime_type(jsonObject.getString("mime_type"));

                    syncDownModel.setLast_update(Global.getMillisDateTimeFromOdoo(jsonObject.getString("__last_update")));
                    syncDownTable.update(syncDownModel, modelName);
                    recordFileTable.delete(recordFileModel.getUuid());
                    recordFileTable.insert(recordFileModel, false);
                }
            } else if (modelName.equals("res.users")) {
                SubaccountTable subaccountTable = JApplication.getInstance().subaccountTable;
                subaccountTable.deleteAll();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    SubaccountModel subaccountModel = new SubaccountModel();
                    subaccountModel.setId(jsonObject.getInt("id"));
                    subaccountModel.setUuid(jsonObject.getString("uuid"));
                    subaccountModel.setName(jsonObject.getString("name"));
                    subaccountModel.setLogin(jsonObject.getString("login"));
                    subaccountModel.setPassword(jsonObject.getString("password"));

                    syncDownModel.setLast_update(Global.getMillisDateTimeFromOdoo(jsonObject.getString("__last_update")));
                    syncDownTable.update(syncDownModel, modelName);
                    subaccountTable.delete(subaccountModel.getUuid());
                    subaccountTable.insert(subaccountModel, false);
                }
            }


            syncDownTable.update_isSync("F", modelName);
            ArrayList<SyncDownModel> syncInfoModelArrayList = syncDownTable.getRecords();
            if (syncInfoModelArrayList.size() == 0) {
                runnable.run();
            } else {
                sync_all(activity, appContext, runnable, progressBar, labelProgres);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
            runnable.run();
            //jika error ke yang lain dulu
            SyncDownTable syncDownTable = JApplication.getInstance().syncDownTable;
            syncDownTable.update_isSync("F", modelName);
        }
//        runnable.run();
    }


}
