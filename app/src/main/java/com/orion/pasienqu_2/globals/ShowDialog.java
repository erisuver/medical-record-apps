package com.orion.pasienqu_2.globals;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.Routes;
import com.orion.pasienqu_2.activities.home.home;
import com.orion.pasienqu_2.activities.login.LoginActivity;
import com.orion.pasienqu_2.adapter.BillingInputAdapter;
import com.orion.pasienqu_2.adapter.IcdAdapter;
import com.orion.pasienqu_2.adapter.LovBillingTemplateAdapter;
import com.orion.pasienqu_2.adapter.LovAdapter;
import com.orion.pasienqu_2.adapter.LovIcdAdapter;
import com.orion.pasienqu_2.adapter.LovTemplateAdapter;
import com.orion.pasienqu_2.adapter.LovWorkLocationAdapter;
import com.orion.pasienqu_2.data_table.BackupDriveTable;
import com.orion.pasienqu_2.data_table.ICDTable;
import com.orion.pasienqu_2.data_table.WorkLocationTable;
import com.orion.pasienqu_2.data_table.more.BillingTemplateTable;
import com.orion.pasienqu_2.data_table.more.NoteTemplateTable;
import com.orion.pasienqu_2.models.BillingTemplateModel;
import com.orion.pasienqu_2.models.ICDModel;
import com.orion.pasienqu_2.models.LovModel;
import com.orion.pasienqu_2.models.RecordDiagnosaModel;
import com.orion.pasienqu_2.models.WorkLocationModel;
import com.orion.pasienqu_2.models.more.NoteTemplateModel;
import com.orion.pasienqu_2.utility.FileDownloadHelper;
import com.orion.pasienqu_2.utility.ModernDownloadHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ShowDialog {

    public static void showTemplate(Activity activity, TextInputEditText txtTemp, String uud){
        Global.hideSoftKeyboard(activity);
        LayoutInflater inflaterDetail;
        View alertLayoutDetail;
        inflaterDetail = activity.getLayoutInflater();
        alertLayoutDetail = inflaterDetail.inflate(R.layout.activity_lov_template, null);

        //alert dialog
        final android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(activity);
        alert.setTitle("");
        alert.setView(alertLayoutDetail);
        alert.setCancelable(true);
        final AlertDialog dialogDet = alert.create();
        dialogDet.show();
        dialogDet.setCancelable(true);
        dialogDet.setCanceledOnTouchOutside(true);

        //create view
        final RecyclerView rcvLoad = (RecyclerView) alertLayoutDetail.findViewById(R.id.rcvLoad);
        LovTemplateAdapter mAdapter;
        List<NoteTemplateModel> listNoteTemplate  = new ArrayList<>();
        final Button btnCancel = (Button) alertLayoutDetail.findViewById(R.id.btnCancel);
        final TextView txtName = (TextView) alertLayoutDetail.findViewById(R.id.txtName);
        txtName.setText(R.string.select_template);
        //show list
        mAdapter = new LovTemplateAdapter(activity, listNoteTemplate, R.layout.note_list_item, dialogDet, txtTemp, uud);
        rcvLoad.setLayoutManager(new GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false));
        rcvLoad.setAdapter(mAdapter);

        ShowDialog.fillData(activity, mAdapter);


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDet.dismiss();
            }
        });

        dialogDet.show();
    }

    public static void fillData(Activity activity, LovTemplateAdapter mAdapter){
        mAdapter.removeAllModel();

        List<String> listValue = ListValue.list_value_template_category(activity);
        List<String> listLabel = ListValue.list_template_category(activity);
        for (int i = 0; i < listLabel.size(); i++) {
            NoteTemplateModel data = new NoteTemplateModel(0, "", listLabel.get(i), listValue.get(i), "", 0);
            mAdapter.addModel(data);

            NoteTemplateTable noteTemplateTable = new NoteTemplateTable(activity);
            mAdapter.addModels(noteTemplateTable.getRecordByCategory(listValue.get(i)));
        }

//
//        data = new NoteTemplateModel(0, "", "Anamnesa", 2, "");
//        mAdapter.addModel(data);
//
//        noteTemplateTable = new NoteTemplateTable(activity);
//        mAdapter.addModels(noteTemplateTable.getRecordByCategory(2));
//
//        data = new NoteTemplateModel(0, "", "Diagnosa", 3, "");
//        mAdapter.addModel(data);
//
//        noteTemplateTable = new NoteTemplateTable(activity);
//        mAdapter.addModels(noteTemplateTable.getRecordByCategory(3));
//
//        data = new NoteTemplateModel(0, "", "Physical Exam", 4, "");
//        mAdapter.addModel(data);
//d
//        noteTemplateTable = new NoteTemplateTable(activity);
//        mAdapter.addModels(noteTemplateTable.getRecordByCategory(4));
//
//        data = new NoteTemplateModel(0, "", "Therapy", 5, "");
//        mAdapter.addModel(data);
//
//        noteTemplateTable = new NoteTemplateTable(activity);
//        mAdapter.addModels(noteTemplateTable.getRecordByCategory(5));
//
//        data = new NoteTemplateModel(0, "", "Others", 6, "");
//        mAdapter.addModel(data);
//
//        noteTemplateTable = new NoteTemplateTable(activity);
//        mAdapter.addModels(noteTemplateTable.getRecordByCategory(6));
//



        mAdapter.notifyDataSetChanged();

    }


    public static void showWorkLocation(Activity activity, TextInputEditText txtTemp, String uud){
        Global.hideSoftKeyboard(activity);
        LayoutInflater inflaterDetail;
        View alertLayoutDetail;
        inflaterDetail = activity.getLayoutInflater();
        alertLayoutDetail = inflaterDetail.inflate(R.layout.activity_lov_template, null);

        //alert dialog
        final android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(activity);
        alert.setTitle("");
        alert.setView(alertLayoutDetail);
        alert.setCancelable(true);
        final AlertDialog dialogDet = alert.create();
        dialogDet.show();
        dialogDet.setCancelable(true);
        dialogDet.setCanceledOnTouchOutside(true);

        //create view
        final RecyclerView rcvLoad = (RecyclerView) alertLayoutDetail.findViewById(R.id.rcvLoad);
        LovWorkLocationAdapter mAdapter;
        List<WorkLocationModel> listNoteTemplate  = new ArrayList<>();
        final Button btnCancel = (Button) alertLayoutDetail.findViewById(R.id.btnCancel);

        //show list
        mAdapter = new LovWorkLocationAdapter(activity, listNoteTemplate, R.layout.lov_list_item, dialogDet, txtTemp, uud);
        rcvLoad.setLayoutManager(new GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false));
        rcvLoad.setAdapter(mAdapter);

        ShowDialog.fillDataWorkLocation(activity, mAdapter);


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDet.dismiss();
            }
        });

        dialogDet.show();
    }


    public static void showWorkLocation(Activity activity, TextView txtTemp, String uuid, Runnable runDialogDismiss){
//        Global.hideSoftKeyboard(activity);
        LayoutInflater inflaterDetail;
        View alertLayoutDetail;
        inflaterDetail = activity.getLayoutInflater();
        alertLayoutDetail = inflaterDetail.inflate(R.layout.activity_lov_template, null);

        //alert dialog
        final android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(activity);
        alert.setTitle("");
        alert.setView(alertLayoutDetail);
        alert.setCancelable(true);
        final AlertDialog dialogDet = alert.create();
        dialogDet.show();
        dialogDet.setCancelable(true);
        dialogDet.setCanceledOnTouchOutside(true);

        //create view
        final RecyclerView rcvLoad = (RecyclerView) alertLayoutDetail.findViewById(R.id.rcvLoad);
        LovWorkLocationAdapter mAdapter;
        List<WorkLocationModel> listNoteTemplate  = new ArrayList<>();
        final Button btnCancel = (Button) alertLayoutDetail.findViewById(R.id.btnCancel);
        final TextView txtTitle = (TextView) alertLayoutDetail.findViewById(R.id.txtName);

        //show list
        mAdapter = new LovWorkLocationAdapter(activity, listNoteTemplate, R.layout.lov_list_item, dialogDet, txtTemp, uuid);
        rcvLoad.setLayoutManager(new GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false));
        rcvLoad.setAdapter(mAdapter);

        ShowDialog.fillDataWorkLocation(activity, mAdapter);

        txtTitle.setText(R.string.select_work_location);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDet.dismiss();
            }
        });

        dialogDet.setOnDismissListener(dialogInterface -> {
            runDialogDismiss.run();
        });
        dialogDet.show();
    }

    public static void fillDataWorkLocation(Activity activity, LovWorkLocationAdapter mAdapter){
        mAdapter.removeAllModel();
        WorkLocationTable workLocationTable = new WorkLocationTable(activity);
        mAdapter.addModels(workLocationTable.getRecords());
        mAdapter.notifyDataSetChanged();
    }



    public static void showLov(Activity activity, List<LovModel> listItem, String value, Runnable runDialogDismiss, String sortKey){
        JApplication.getInstance().SelectedLov = value;
        LayoutInflater inflaterDetail;
        View alertLayoutDetail;
        inflaterDetail = activity.getLayoutInflater();
        alertLayoutDetail = inflaterDetail.inflate(R.layout.activity_lov_template, null);

        //alert dialog
        final android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(activity);
        alert.setTitle("");
        alert.setView(alertLayoutDetail);
        alert.setCancelable(true);
        final AlertDialog dialogDet = alert.create();
        dialogDet.show();
        dialogDet.setCancelable(true);
        dialogDet.setCanceledOnTouchOutside(true);

        final RecyclerView rcvLoad = (RecyclerView) alertLayoutDetail.findViewById(R.id.rcvLoad);
        LovAdapter mAdapter;
        final Button btnCancel = (Button) alertLayoutDetail.findViewById(R.id.btnCancel);
        final TextView txtTitle = (TextView) alertLayoutDetail.findViewById(R.id.txtName);
        txtTitle.setText(activity.getString(R.string.sort));

        //show list
        mAdapter = new LovAdapter(activity, listItem, R.layout.lov_list_item_checked, dialogDet, value, sortKey);
        rcvLoad.setLayoutManager(new GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false));
        rcvLoad.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
//
//        ShowDi
//        alog.fillDataLov(activity, mAdapter, listItem);
//

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDet.dismiss();
            }
        });
        dialogDet.setOnDismissListener(dialogInterface -> {
            runDialogDismiss.run();
        });

        dialogDet.show();
    }

    public static void showBillingTemplate(Activity activity, BillingInputAdapter billingInputAdapter, String uuid){
        LayoutInflater inflaterDetail;
        View alertLayoutDetail;
        inflaterDetail = activity.getLayoutInflater();
        alertLayoutDetail = inflaterDetail.inflate(R.layout.activity_lov_template, null);

        //alert dialog
        final android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(activity);
        alert.setTitle("");
        alert.setView(alertLayoutDetail);
        alert.setCancelable(true);
        final AlertDialog dialogDet = alert.create();
        dialogDet.show();
        dialogDet.setCancelable(true);
        dialogDet.setCanceledOnTouchOutside(true);

        //create view
        final RecyclerView rcvLoad = (RecyclerView) alertLayoutDetail.findViewById(R.id.rcvLoad);
        LovBillingTemplateAdapter mAdapter;
        List<BillingTemplateModel> listTemplate  = new ArrayList<>();
        final Button btnCancel = (Button) alertLayoutDetail.findViewById(R.id.btnCancel);

        //show list
        mAdapter = new LovBillingTemplateAdapter(activity, listTemplate, R.layout.lov_list_item, dialogDet, billingInputAdapter, uuid);
        rcvLoad.setLayoutManager(new GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false));
        rcvLoad.setAdapter(mAdapter);

        ShowDialog.fillDataBillingTemplate(activity, mAdapter);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDet.dismiss();
            }
        });
        dialogDet.show();
    }

    public static void fillDataBillingTemplate(Activity activity, LovBillingTemplateAdapter mAdapter){
        mAdapter.removeAllModel();
        BillingTemplateTable billingTemplateTable = new BillingTemplateTable(activity);
        mAdapter.addModels(billingTemplateTable.getRecords());
        mAdapter.notifyDataSetChanged();
    }

//
//    public static void fillDataLov(Activity activity, LovAdapter mAdapter, List<LovModel> listItem){
//        mAdapter.notifyDataSetChanged();
//    }

    public static void showICD10(Activity activity, TextInputEditText txtTemp, String uuid, IcdAdapter icdAdapter, Runnable runnable, List<RecordDiagnosaModel> ListDefaultIcd){
        LayoutInflater inflaterDetail;
        View alertLayoutDetail;
        inflaterDetail = activity.getLayoutInflater();
        alertLayoutDetail = inflaterDetail.inflate(R.layout.activity_lov_icd, null);

        //alert dialog
        final android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(activity);
        alert.setTitle("");
        alert.setView(alertLayoutDetail);
        alert.setCancelable(false);
        final AlertDialog dialogDet = alert.create();
        dialogDet.show();
        dialogDet.setCancelable(false);
        dialogDet.setCanceledOnTouchOutside(false);
        dialogDet.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        //create view
        final AutoCompleteTextView txtSearch = (AutoCompleteTextView) alertLayoutDetail.findViewById(R.id.txtSearchIcd);
        final RecyclerView rcvLoad = (RecyclerView) alertLayoutDetail.findViewById(R.id.rcvLoad);
        LovIcdAdapter mAdapter;
        List<RecordDiagnosaModel> listIcdModel  = new ArrayList<>();
        listIcdModel.addAll(ListDefaultIcd);
        final Button btnCancel = (Button) alertLayoutDetail.findViewById(R.id.btnCancel);
        final Button btnDone = (Button) alertLayoutDetail.findViewById(R.id.btnDone);

        //show list
        mAdapter = new LovIcdAdapter(activity, listIcdModel, R.layout.lov_icd_list_item, dialogDet, txtTemp, uuid,icdAdapter );
        rcvLoad.setLayoutManager(new GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false));
        rcvLoad.setAdapter(mAdapter);

        runnable.run();

        //event
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDet.dismiss();
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogDet.dismiss();
            }
        });

        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                if (i2 == 2 || i == 1) {
//                    ShowDialog.fillSearchICD10(activity, txtSearch);
//                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                if (i2 == 2 || i == 1) {
//                    ShowDialog.fillSearchICD10(activity, txtSearch);
//                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() >= 2 && !editable.toString().contains("\n")) {
                    // Isi adapter dan tampilkan dropdown
                    ShowDialog.fillSearchICD10(activity, txtSearch);
                }
            }
        });

        txtSearch.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                String selection = (String) parent.getItemAtPosition(position);
                String codeIcd  = ShowDialog.getCodeICD10(activity, txtSearch, selection);

//                fillDataICD10(activity, mAdapter, selection, codeIcd, uuid);

                for (int i = 0; i < listIcdModel.size(); i++){
                    if (listIcdModel.get(i).getIcd_code().equals(codeIcd)){
                        Toast.makeText(activity, activity.getString(R.string.eror_item_exist), Toast.LENGTH_SHORT).show();
                        txtSearch.setText("");
                        return;
                    }
                }
                RecordDiagnosaModel recordDiagnosaModel = new RecordDiagnosaModel();
                recordDiagnosaModel.setIcd_name(selection);
                recordDiagnosaModel.setIcd_code(codeIcd);
                mAdapter.addModel(recordDiagnosaModel);
                mAdapter.notifyDataSetChanged();
                txtSearch.setText("");
            }
        });

        dialogDet.show();
    }



    public static void fillSearchICD10(Activity activity, AutoCompleteTextView txtSearch){

        ICDTable icdTable = new ICDTable(activity);
        ArrayList<ICDModel> listIcd = icdTable.getRecords(txtSearch.getText().toString());
//        ArrayList<ICDModel> listIcd = icdTable.getRecords();

        List<String> listStringIcd = new ArrayList<>();
        for (ICDModel icd : listIcd) {
            listStringIcd.add(icd.getCode() + "\n" + icd.getName());
        }


        ArrayAdapter<String> icdAdapter = new ArrayAdapter<>(activity, R.layout.custom_list_view, R.id.text_view_list_item, listStringIcd);
        icdAdapter.setDropDownViewResource(android.R.layout.simple_list_item_2);
        txtSearch.setAdapter(icdAdapter);
    }

    public static String getCodeICD10(Activity activity, AutoCompleteTextView txtSearch , String selection){

        ICDTable icdTable = new ICDTable(activity);
        ArrayList<ICDModel> listIcd = icdTable.getRecords();

        String codeIcd = "";
        for (int i = 0; i < listIcd.toArray().length; i++) {
            String codename = listIcd.get(i).getCode() + "\n" + listIcd.get(i).getName();
            if (codename.equals(selection)) {
                codeIcd = listIcd.get(i).getCode();
                break;
            }
        }
        return codeIcd;
    }

    public static void fillDataICD10(Activity activity, LovIcdAdapter mAdapter, String name, String codeIcd, String uuid){

        RecordDiagnosaModel recordDiagnosaModel = new RecordDiagnosaModel();
        recordDiagnosaModel.setIcd_name(name);
        recordDiagnosaModel.setIcd_code(codeIcd);
        mAdapter.addModel(recordDiagnosaModel);
        mAdapter.notifyDataSetChanged();
    }


    public static void confirmDialog(Activity activity, String title, String content, Runnable runnable){
        LayoutInflater inflaterDetail;
        View alertLayoutDetail;
        inflaterDetail = activity.getLayoutInflater();
        alertLayoutDetail = inflaterDetail.inflate(R.layout.layout_confirm_dialog, null);

        //alert dialog
        final android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(activity);
        alert.setTitle("");
        alert.setView(alertLayoutDetail);
        alert.setCancelable(false);
        final AlertDialog dialogDet = alert.create();
        dialogDet.setCanceledOnTouchOutside(false);
        dialogDet.show();

        //create view
        final TextView txtTitle = alertLayoutDetail.findViewById(R.id.txtTitle);
        final TextView txtContent = alertLayoutDetail.findViewById(R.id.txtContent);
        final Button btnYes = (Button) alertLayoutDetail.findViewById(R.id.btnYes);
        final Button btnNo = (Button) alertLayoutDetail.findViewById(R.id.btnNo);

        //init
        txtTitle.setText(title);
        txtContent.setText(content);

        //event
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDet.dismiss();
                runnable.run();
            }
        });
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDet.dismiss();
            }
        });
        dialogDet.show();
    }

    public static void confirmDialogWithDissmis(Activity activity, String title, String content, Runnable runnable, Runnable runDismiss){
        LayoutInflater inflaterDetail;
        View alertLayoutDetail;
        inflaterDetail = activity.getLayoutInflater();
        alertLayoutDetail = inflaterDetail.inflate(R.layout.layout_confirm_dialog, null);

        //alert dialog
        final android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(activity);
        alert.setTitle("");
        alert.setView(alertLayoutDetail);
        alert.setCancelable(false);
        final AlertDialog dialogDet = alert.create();
        dialogDet.setCanceledOnTouchOutside(false);
        dialogDet.show();

        //create view
        final TextView txtTitle = alertLayoutDetail.findViewById(R.id.txtTitle);
        final TextView txtContent = alertLayoutDetail.findViewById(R.id.txtContent);
        final Button btnYes = (Button) alertLayoutDetail.findViewById(R.id.btnYes);
        final Button btnNo = (Button) alertLayoutDetail.findViewById(R.id.btnNo);

        //init
        txtTitle.setText(title);
        txtContent.setText(content);

        //event
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDet.dismiss();
                runnable.run();
            }
        });
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runDismiss.run();
                dialogDet.dismiss();
            }
        });
        dialogDet.show();
    }


    public static void infoDialog(Activity activity, String title, String content){
        LayoutInflater inflaterDetail;
        View alertLayoutDetail;
        inflaterDetail = activity.getLayoutInflater();
        alertLayoutDetail = inflaterDetail.inflate(R.layout.layout_info_dialog, null);

        //alert dialog
        final android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(activity);
        alert.setTitle("");
        alert.setView(alertLayoutDetail);
        alert.setCancelable(false);
        final AlertDialog dialogDet = alert.create();
        dialogDet.setCanceledOnTouchOutside(false);
        dialogDet.show();

        //create view
        final TextView txtTitle = alertLayoutDetail.findViewById(R.id.txtTitle);
        final TextView txtContent = alertLayoutDetail.findViewById(R.id.txtContent);
        final Button btnOk = (Button) alertLayoutDetail.findViewById(R.id.btnOk);

        //init
        txtTitle.setText(title);
        txtContent.setText(content);

        //event
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDet.dismiss();
            }
        });
        dialogDet.show();
    }

    public static void infoDialogWithRunnable(Activity activity, String title, String content, Runnable runnable){
        LayoutInflater inflaterDetail;
        View alertLayoutDetail;
        inflaterDetail = activity.getLayoutInflater();
        alertLayoutDetail = inflaterDetail.inflate(R.layout.layout_info_dialog, null);

        //alert dialog
        final android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(activity);
        alert.setTitle("");
        alert.setView(alertLayoutDetail);
        alert.setCancelable(false);
        final AlertDialog dialogDet = alert.create();
        dialogDet.setCanceledOnTouchOutside(false);
        dialogDet.show();

        //create view
        final TextView txtTitle = alertLayoutDetail.findViewById(R.id.txtTitle);
        final TextView txtContent = alertLayoutDetail.findViewById(R.id.txtContent);
        final Button btnOk = (Button) alertLayoutDetail.findViewById(R.id.btnOk);

        //init
        txtTitle.setText(title);
        txtContent.setText(content);

        //event
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDet.dismiss();
                runnable.run();
            }
        });
        dialogDet.show();
    }

    public static void warningDialog(Activity activity, String title, String content){
        LayoutInflater inflaterDetail;
        View alertLayoutDetail;
        inflaterDetail = activity.getLayoutInflater();
        alertLayoutDetail = inflaterDetail.inflate(R.layout.layout_info_dialog, null);

        //alert dialog
        final android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(activity);
        alert.setTitle("");
        alert.setView(alertLayoutDetail);
        alert.setCancelable(false);
        final AlertDialog dialogDet = alert.create();
        dialogDet.setCanceledOnTouchOutside(false);
        dialogDet.show();

        //create view
        final TextView txtTitle = alertLayoutDetail.findViewById(R.id.txtTitle);
        final TextView txtContent = alertLayoutDetail.findViewById(R.id.txtContent);
        final Button btnOk = (Button) alertLayoutDetail.findViewById(R.id.btnOk);

        //init
        txtTitle.setText(title);
        txtContent.setText(content);
        txtContent.setTextColor(Color.RED);

        //event
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDet.dismiss();
            }
        });
        dialogDet.show();
    }

    public static void backupRestoreDialog(Activity activity, Runnable runBackup, Runnable runDismiss, boolean isRestore){
        LayoutInflater inflaterDetail;
        View alertLayoutDetail;
        inflaterDetail = activity.getLayoutInflater();
        alertLayoutDetail = inflaterDetail.inflate(R.layout.layout_dialog_backup, null);

        //alert dialog
        final android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(activity);
        alert.setTitle("");
        alert.setView(alertLayoutDetail);
        alert.setCancelable(false);
        final AlertDialog dialogDet = alert.create();
        dialogDet.setCanceledOnTouchOutside(false);
        dialogDet.show();

        //create view
        final TextView txtLastBackup = (TextView) alertLayoutDetail.findViewById(R.id.txtLastBackup);
        final TextView txtContent = (TextView) alertLayoutDetail.findViewById(R.id.txtContent);
        final TextView txtTitle = (TextView) alertLayoutDetail.findViewById(R.id.txtTitle);
        final Button btnBackup = (Button) alertLayoutDetail.findViewById(R.id.btnBackup);
        final Button btnNo = (Button) alertLayoutDetail.findViewById(R.id.btnNo);
        final Switch swcFile = (Switch) alertLayoutDetail.findViewById(R.id.swcFile);
        BackupDriveTable backupDriveTable = new BackupDriveTable(activity);

        //init
        SharedPreferences sharedPreferences = activity.getSharedPreferences("masterDevice", Context.MODE_PRIVATE);
        boolean isIncludeFile = sharedPreferences.getBoolean("include_file", false);
        long lastBackup = backupDriveTable.getLastUpdateData();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(JApplication.getInstance());
        if (lastBackup == 0 || account == null) {
            txtLastBackup.setText(String.format(activity.getString(R.string.text_last_backup), activity.getString(R.string.text_never_backup)));
        }else{
            txtLastBackup.setText(String.format(activity.getString(R.string.text_last_backup), Global.getDateTimeFormated(lastBackup)));
        }

        if (isIncludeFile) {
            swcFile.setChecked(true);
        } else {
            swcFile.setChecked(false);
        }

        if(isRestore){
            txtLastBackup.setText(activity.getString(R.string.restore));
            txtTitle.setText(R.string.title_restore_drive);
            txtContent.setText(R.string.content_restore_drive);
            btnBackup.setText(R.string.restore_now);
        }

        //event
        swcFile.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences.Editor editor = activity.getSharedPreferences("masterDevice", Context.MODE_PRIVATE).edit();
                editor.putBoolean("include_file", swcFile.isChecked());
                editor.apply();
            }
        });

        btnBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runBackup.run();
                dialogDet.dismiss();
            }
        });
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDet.dismiss();
                runDismiss.run();
            }
        });
        dialogDet.show();
    }

    public static void birthDateDialog(Activity activity, TextInputEditText txtDateBirth, Runnable runnable){
        LayoutInflater inflaterDetail;
        View alertLayoutDetail;
        inflaterDetail = activity.getLayoutInflater();
        alertLayoutDetail = inflaterDetail.inflate(R.layout.layout_input_birthdate, null);

        //alert dialog
        final android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(activity);
        alert.setTitle("");
        alert.setView(alertLayoutDetail);
        alert.setCancelable(false);
        final AlertDialog dialogDet = alert.create();
        dialogDet.setCanceledOnTouchOutside(false);
        dialogDet.show();

        //create view
        final TextInputEditText txtDay = alertLayoutDetail.findViewById(R.id.txtDay);
        final TextInputEditText txtMonth = alertLayoutDetail.findViewById(R.id.txtMonth);
        final TextInputEditText txtYear = alertLayoutDetail.findViewById(R.id.txtYear);
        final TextInputLayout errDay = alertLayoutDetail.findViewById(R.id.layoutDay);
        final TextInputLayout errMonth = alertLayoutDetail.findViewById(R.id.layoutMonth);
        final TextInputLayout errYear = alertLayoutDetail.findViewById(R.id.layoutYear);
        final Button btnDone = (Button) alertLayoutDetail.findViewById(R.id.btnDone);

        //init
        String datebirth = txtDateBirth.getText().toString();
        if (datebirth.equals("")){
            datebirth = Global.serverNow();
        }

        long dateLong = Global.getMillisDate(datebirth);
        txtDay.setText(Global.getHari(dateLong));
        txtMonth.setText(Global.getBulan(dateLong));
        txtYear.setText(Global.getTahun(dateLong));

        //event
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ValidationBirthDate(activity, txtDay, txtMonth, txtYear, errDay, errMonth, errYear)) {
                    String resultDate = Integer.parseInt(txtDay.getText().toString()) + "/" + Integer.parseInt(txtMonth.getText().toString()) + "/" + Integer.parseInt(txtYear.getText().toString());
                    txtDateBirth.setText(resultDate);
                    runnable.run();
                    dialogDet.dismiss();
                }
            }
        });
        dialogDet.show();
    }
    
    private static boolean ValidationBirthDate(Activity activity, TextInputEditText txtDay, TextInputEditText txtMonth, TextInputEditText txtYear, TextInputLayout errDay, TextInputLayout errMonth, TextInputLayout errYear){
        String currentYear, currentMonth, currentDay;
        currentDay = Global.getTahun(Global.serverNowLong());
        currentMonth = Global.getBulan(Global.serverNowLong());
        currentYear = Global.getHari(Global.serverNowLong());

        if (txtYear.getText().toString().equals("0") || txtYear.getText().toString().equals("")) {
            txtYear.setText(currentYear);
        }
        if (txtMonth.getText().toString().equals("0") || txtMonth.getText().toString().equals("")) {
            txtMonth.setText(currentMonth);
        }
        if (txtDay.getText().toString().equals("0") || txtDay.getText().toString().equals("")) {
            txtDay.setText(currentDay);
        }

        int vHari = Integer.parseInt(txtDay.getText().toString());
        int vBulan = Integer.parseInt(txtMonth.getText().toString());
        int vTahun = Integer.parseInt(txtYear.getText().toString());
        long tglNow = Global.serverNowLong();
        long tglInput = Global.getMillisDate(vHari+"/"+vBulan+"/"+vTahun);

        if (Integer.parseInt(txtMonth.getText().toString()) > 12) {
            errMonth.setError(activity.getString(R.string.error_month_exceed));
            txtMonth.setSelection(0);
            txtMonth.setFocusable(true);
            txtMonth.selectAll();
            txtMonth.requestFocus();
            return false;
        }else{
            errMonth.setErrorEnabled(false);
        }
        if (Integer.parseInt(txtDay.getText().toString()) > Global.getMaxDayOfMonth(vBulan, vTahun)){
            txtDay.setSelection(0);
            txtDay.setFocusable(true);
            txtDay.selectAll();
            txtDay.requestFocus();
            errDay.setError(activity.getString(R.string.error_date_exceed));
            return false;
        }else{
            errDay.setErrorEnabled(false);
        }
        if(tglInput > tglNow) {
            Toast.makeText(activity, R.string.error_datebirth_exceed, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public static void ShowEulaDialog(Activity activity){
        LayoutInflater inflaterDetail;
        View alertLayoutDetail;
        inflaterDetail = activity.getLayoutInflater();
        alertLayoutDetail = inflaterDetail.inflate(R.layout.layout_eula_dialog, null);

        //alert dialog
        final android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(activity);
        alert.setTitle("");
        alert.setView(alertLayoutDetail);
        alert.setCancelable(false);
        final AlertDialog dialogDet = alert.create();
        dialogDet.setCanceledOnTouchOutside(false);
        dialogDet.show();

        //create view
        final TextView txtTitle = alertLayoutDetail.findViewById(R.id.txtTitle);
        final TextView txtContent = alertLayoutDetail.findViewById(R.id.txtContent);
        final ImageButton btnClose = (ImageButton) alertLayoutDetail.findViewById(R.id.btnClose);

        //init
//        Spanned sp = Html.fromHtml(activity.getString(R.string.eula_body));
//        txtContent.setText(sp);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
////            txtContent.setText(Html.fromHtml(activity.getString(R.string.eula_body), Html.FROM_HTML_MODE_COMPACT));
//            txtContent.setText(Html.fromHtml("<b>sasa</b><br>dsdass<br>"));
//
//        } else {
//            txtContent.setText(Html.fromHtml(activity.getString(R.string.eula_body)));
//        }
        BufferedReader reader = null;
        StringBuilder text = new StringBuilder();

        try {

            reader = new BufferedReader(
                    new InputStreamReader(activity.getResources().openRawResource(R.raw.eula)));

            // do reading, usually loop until end of file reading
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                text.append(mLine);
                text.append('\n');
            }
        } catch (IOException e) {
            Toast.makeText(activity,"Error reading file!",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }


            txtContent.setText((CharSequence) text);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            txtContent.setText(Html.fromHtml(txtContent.getText().toString()));

        } else {
            txtContent.setText(Html.fromHtml(txtContent.getText().toString()));
        }

        //event
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDet.dismiss();
            }
        });
        dialogDet.show();
    }

    public static void permissionDialog(Activity activity){
        LayoutInflater inflaterDetail;
        View alertLayoutDetail;
        inflaterDetail = activity.getLayoutInflater();
        alertLayoutDetail = inflaterDetail.inflate(R.layout.layout_confirm_dialog, null);

        //alert dialog
        final android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(activity);
        alert.setTitle("");
        alert.setView(alertLayoutDetail);
        alert.setCancelable(false);
        final AlertDialog dialogDet = alert.create();
        dialogDet.setCanceledOnTouchOutside(false);
        dialogDet.show();

        //create view
        final TextView txtTitle = alertLayoutDetail.findViewById(R.id.txtTitle);
        final TextView txtContent = alertLayoutDetail.findViewById(R.id.txtContent);
        final Button btnYes = (Button) alertLayoutDetail.findViewById(R.id.btnYes);
        final Button btnNo = (Button) alertLayoutDetail.findViewById(R.id.btnNo);

        //init
        txtTitle.setText(R.string.app_name);
        txtContent.setText(R.string.inform_permission);
        btnYes.setText(R.string.setting);
        btnNo.setText(R.string.cancel);

        //event
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + activity.getPackageName()));
                activity.startActivity(intent);
                dialogDet.dismiss();
            }
        });
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDet.dismiss();
            }
        });
        dialogDet.show();
    }

    public static void migrasiMySQLDialog(Activity activity){
        LayoutInflater inflaterDetail;
        View alertLayoutDetail;
        inflaterDetail = activity.getLayoutInflater();
        alertLayoutDetail = inflaterDetail.inflate(R.layout.layout_dialog_migrasi, null);

        //alert dialog
        final android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(activity);
        alert.setTitle("");
        alert.setView(alertLayoutDetail);
        alert.setCancelable(false);
        final AlertDialog dialogDet = alert.create();
        dialogDet.setCanceledOnTouchOutside(false);
        dialogDet.show();

        //create view
        final TextView txtTitle = alertLayoutDetail.findViewById(R.id.txtTitle);
        final TextView txtContent = alertLayoutDetail.findViewById(R.id.txtContent);
        final Button btnOK =  alertLayoutDetail.findViewById(R.id.btnOk);
        final TextInputEditText txtPassword = alertLayoutDetail.findViewById(R.id.txtPassword);
        final TextInputEditText txtConfirm = alertLayoutDetail.findViewById(R.id.txtConfirm);
        final TextInputLayout errPassword = alertLayoutDetail.findViewById(R.id.layoutPassword);
        final TextInputLayout errConfirmPass = alertLayoutDetail.findViewById(R.id.layoutConfirm);

        //init
        txtTitle.setText(R.string.app_name);
        txtContent.setText(R.string.enter_password_first);
        btnOK.setText("OK");

        //event

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //validasi
                boolean isError = false;

                if (!Global.CheckConnectionInternet(activity)){
                    Snackbar.make(activity.findViewById(android.R.id.content), R.string.must_be_online, Snackbar.LENGTH_LONG).show();
                    isError = true;
                }

                //pasword
                if (TextUtils.isEmpty(txtPassword.getText())) {
                    errPassword.setError(activity.getString(R.string.error_password_req));
                    isError = true;
                }else if (txtPassword.getText().toString().length() < 6 ) {
                    errPassword.setError(activity.getString(R.string.error_password));
                    isError = true;
                }else{
                    errPassword.setErrorEnabled(false);
                    isError = false;
                }
                //confirm pas
                if (TextUtils.isEmpty(txtConfirm.getText())) {
                    errConfirmPass.setError(activity.getString(R.string.error_confirm_password));
                    isError = true;
                }else if (txtConfirm.getText().toString().length() < 6 ) {
                    errConfirmPass.setError(activity.getString(R.string.error_confirm_password_length));
                    isError = true;
                }else if (!txtConfirm.getText().toString().equals(txtPassword.getText().toString())){
                    errConfirmPass.setErrorEnabled(false);
                    Snackbar.make(activity.getWindow().getDecorView().getRootView(), R.string.sign_up_pwd_error, Snackbar.LENGTH_LONG).show();
                    isError = true;
                }else{
                    errConfirmPass.setErrorEnabled(false);
                    isError = false;
                }

                if (!isError){
                    ProgressDialog loading = Global.createProgresSpinner(activity, activity.getString(R.string.loading));
                    GlobalMySQL.migrasi_odoo_to_mysql(activity, txtPassword.getText().toString() ,loading);
                    dialogDet.dismiss();
                }
            }
        });
        dialogDet.show();
    }

    public static void savedSuccess(Activity activity, Runnable runnable) {
        LayoutInflater inflaterDetail;
        View alertLayoutDetail;
        inflaterDetail = activity.getLayoutInflater();
        alertLayoutDetail = inflaterDetail.inflate(R.layout.custom_dialog_layout, null);

        //alert dialog
        final android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(activity);
        alert.setTitle("");
        alert.setView(alertLayoutDetail);
        alert.setCancelable(false);
        final AlertDialog dialogDet = alert.create();
        dialogDet.setCanceledOnTouchOutside(false);
        dialogDet.show();

        // Menutup popup setelah 1 detik
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dialogDet != null && dialogDet.isShowing()) {
                    runnable.run();
                    dialogDet.dismiss();
                }
            }
        }, 1000); // 1000 milliseconds = 1 detik
    }

    public static void showDialogHelp(Activity activity) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_dialog_help_satusehat, null);
        // Set the view to the dialog
        builder.setView(dialogView);
        // Create and show the dialog
        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();

        // Set up the views
        Button btnHelpOk = dialogView.findViewById(R.id.btnOk);
        Button btnDownloadHelp = dialogView.findViewById(R.id.btnDownloadHelp);

        // Set up the button click listener
        btnHelpOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnDownloadHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FileDownloadHelper fileDownloaderHelper = new FileDownloadHelper(activity);
//                fileDownloaderHelper.downloadFile(Routes.url_help(), "Petunjuk Penggunaan Satu Sehat Aplikasi PasienQu PRO.pdf");

                ModernDownloadHelper modernDownloadHelper = new ModernDownloadHelper(activity);
                modernDownloadHelper.downloadToPublicDownloads(Routes.url_help(), "Petunjuk Penggunaan Satu Sehat Aplikasi PasienQu PRO.pdf", "application/pdf");
            }
        });

    }

    public static void overwriteDialog(Activity activity, String title, String content, String content2, Runnable runnable){
        LayoutInflater inflaterDetail;
        View alertLayoutDetail;
        inflaterDetail = activity.getLayoutInflater();
        alertLayoutDetail = inflaterDetail.inflate(R.layout.layout_overwrite_dialog, null);

        //alert dialog
        final android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(activity);
        alert.setTitle("");
        alert.setView(alertLayoutDetail);
        alert.setCancelable(false);
        final AlertDialog dialogDet = alert.create();
        dialogDet.setCanceledOnTouchOutside(false);
        dialogDet.show();

        //create view
        final TextView txtTitle = alertLayoutDetail.findViewById(R.id.txtTitle);
        final TextView txtContent = alertLayoutDetail.findViewById(R.id.txtContent);
        final TextView txtContent2 = alertLayoutDetail.findViewById(R.id.txtContent2);
        final Button btnOverwrite = alertLayoutDetail.findViewById(R.id.btnOverwrite);
        final Button btnCancel = alertLayoutDetail.findViewById(R.id.btnCancel);

        //init
        txtTitle.setText(title);
        txtContent.setText(content);
        txtContent2.setText(content2);

        //event
        btnOverwrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDet.dismiss();
                runnable.run();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDet.dismiss();
            }
        });
        dialogDet.show();
    }
}
