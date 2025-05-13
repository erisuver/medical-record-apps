package com.orion.pasienqu_2.adapter;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.activities.login.LoginActivity;
import com.orion.pasienqu_2.activities.more.data_migration.DataMigrationActivity;
import com.orion.pasienqu_2.activities.more.delete_archive.DeleteArchive;
import com.orion.pasienqu_2.activities.more.export.ExportActivity;
import com.orion.pasienqu_2.activities.more.export.ExportActivity2;
import com.orion.pasienqu_2.activities.more.export.ExportSuccessActivity;
import com.orion.pasienqu_2.activities.more.generate_patient_id.GeneratePatientIDActivity;
import com.orion.pasienqu_2.activities.more.change_password.ChangePasswordActivity;
import com.orion.pasienqu_2.activities.more.edit_profile.EditProfileActivity;
import com.orion.pasienqu_2.activities.more.billing_template.BillingTemplateActivity;
import com.orion.pasienqu_2.activities.more.google_drive.BackupDriveActivity;
import com.orion.pasienqu_2.activities.more.google_drive.BackupRestoreDrive;
import com.orion.pasienqu_2.activities.more.google_drive.RestoreDriveActivity;
import com.orion.pasienqu_2.activities.more.import_data.ImportDataActivity;
import com.orion.pasienqu_2.activities.more.import_data_v1.ImportDataV1Activity;
import com.orion.pasienqu_2.activities.more.kirim_database.KirimDatabaseActivity;
import com.orion.pasienqu_2.activities.more.note_template.NoteTemplateActivity;
import com.orion.pasienqu_2.activities.more.subaccount.SubaccountActivity;
import com.orion.pasienqu_2.activities.more.switch_language.SwitchLanguageActivity;
import com.orion.pasienqu_2.activities.more.pin_protection.PinProtectionActivity;
import com.orion.pasienqu_2.activities.more.renew_subscriotion.RenewSubscriptionActivity;
import com.orion.pasienqu_2.activities.more.work_location.WorkLocationActivity;
import com.orion.pasienqu_2.activities.satu_sehat.MedicalSatuSehatActivity;
import com.orion.pasienqu_2.activities.satu_sehat.PractitionerActivity;
import com.orion.pasienqu_2.activities.satu_sehat.location.LocationSatuSehatActivity;
import com.orion.pasienqu_2.globals.ExportExcel;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.ShowDialog;
import com.orion.pasienqu_2.models.MoreModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.recyclerview.widget.RecyclerView;

public class MoreAdapter extends RecyclerView.Adapter{

    Context context;
    List<MoreModel> Datas;
    private List<MoreModel> dataFiltererd;
    private int view;
    private int REQUEST_CODE_LOAD = 444;
    private final int VIEW_EDIT_PROFILE = 1, VIEW_CHANGE_PASSWORD = 2, VIEW_SET_LANGUAGE = 3,
                      VIEW_PIN_PROTECTION = 4, VIEW_SATU_SEHAT = 5, VIEW_AUTO_GEN_ID = 6, VIEW_WORK_LOCATION = 7,
                      VIEW_RENEW_SUBSCRIPTION = 8, VIEW_BILLING_TEMPLATE = 9, VIEW_NOTE_TEMPLATE = 10,
                      VIEW_SUB_ACCOUNTS = 11, VIEW_IMPORT = 12, VIEW_EXPORT = 13, VIEW_GDRIVE = 14,
                      VIEW_DELETE_ARCHIVE = 15, VIEW_DATA_MIGRATION = 16,
                      VIEW_EULA = 17, VIEW_HELP = 18,  VIEW_LOG_OUT = 19,  VIEW_KIRIM_DATABASE = 20,
                      VIEW_BACKUP_DRIVE = 21, VIEW_RESTORE_DRIVE = 22, VIEW_DELETE_ACCOUNT = 23;

    public MoreAdapter(Context context, List<MoreModel> datas, int view) {
        this.context = context;
        this.Datas = datas;
        this.dataFiltererd = new ArrayList<MoreModel>();
        this.dataFiltererd.addAll(Datas);
        this.view = view;
    }


    // inflates the row layout from xml when needed
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View row = inflater.inflate(view, parent, false);
        return new MoreAdapter.ItemHolder(row);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final MoreModel data = dataFiltererd.get(position);
        final MoreAdapter.ItemHolder itemHolder = (MoreAdapter.ItemHolder) holder;

        itemHolder.txtMenuName.setText(data.getMenuName());
        itemHolder.icon.setImageResource(data.getIcon());

        itemHolder.itemView.setOnClickListener(view1 -> {
            switch(data.getMenuId()) {

                case VIEW_EDIT_PROFILE: {
                    if (!Global.CheckConnectionInternet(context)) {
                        ShowDialog.infoDialog(((Activity)context), "PasienQu", context.getString(R.string.must_be_online));
                    }else {
                        Intent s = new Intent(context, EditProfileActivity.class);
                        s.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                        ((Activity) context).startActivityForResult(s, REQUEST_CODE_LOAD);
                    }
                    break;
                }
                case VIEW_CHANGE_PASSWORD: {
                    if (!Global.CheckConnectionInternet(context)) {
                        ShowDialog.infoDialog(((Activity)context), "PasienQu", context.getString(R.string.must_be_online));
                    }else {
                        context.startActivity(new Intent(context, ChangePasswordActivity.class)
                                .setFlags(FLAG_ACTIVITY_CLEAR_TOP));
                    }
                    break;
                }
                case VIEW_SET_LANGUAGE: {
                    context.startActivity(new Intent(context, SwitchLanguageActivity.class)
                            .setFlags(FLAG_ACTIVITY_CLEAR_TOP));
                    break;
                }
                case VIEW_PIN_PROTECTION: {
                    context.startActivity(new Intent(context, PinProtectionActivity.class)
                            .setFlags(FLAG_ACTIVITY_CLEAR_TOP));
                    break;
                }
                case VIEW_AUTO_GEN_ID: {
                    context.startActivity(new Intent(context, GeneratePatientIDActivity.class)
                            .setFlags(FLAG_ACTIVITY_CLEAR_TOP));
                    break;
                }
                case VIEW_WORK_LOCATION: {
                    context.startActivity(new Intent(context, WorkLocationActivity.class)
                            .setFlags(FLAG_ACTIVITY_CLEAR_TOP));
                    break;
                }
                case VIEW_RENEW_SUBSCRIPTION: {
                    if (!Global.CheckConnectionInternet(context)) {
                        ShowDialog.infoDialog(((Activity)context), "PasienQu", context.getString(R.string.must_be_online));
                    }else {
                        Intent s = new Intent(context, RenewSubscriptionActivity.class);
                        s.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                        ((Activity) context).startActivityForResult(s, REQUEST_CODE_LOAD);
                    }
                    break;
                }
                case VIEW_BILLING_TEMPLATE: {
                    context.startActivity(new Intent(context, BillingTemplateActivity.class)
                            .setFlags(FLAG_ACTIVITY_CLEAR_TOP));
                    break;
                }
                case VIEW_NOTE_TEMPLATE: {
                    context.startActivity(new Intent(context, NoteTemplateActivity.class)
                            .setFlags(FLAG_ACTIVITY_CLEAR_TOP));
                    break;
                }
                case VIEW_SUB_ACCOUNTS: {
//                    if (!Global.CheckConnectionInternet(context)) {
//                        ShowDialog.infoDialog(((Activity)context), "PasienQu", context.getString(R.string.must_be_online));
//                    }else {
//                        context.startActivity(new Intent(context, SubaccountActivity.class));
//                    }
                    //multi user
                    ShowDialog.infoDialog(((Activity)context), context.getString(R.string.multi_user), context.getString(R.string.fitur_not_available));
                    break;
                }
                case VIEW_IMPORT: {
//                    context.startActivity(new Intent(context, ImportDataActivity.class));
                    context.startActivity(new Intent(context, ImportDataV1Activity.class)
                            .setFlags(FLAG_ACTIVITY_CLEAR_TOP));
                    break;
                }
                case VIEW_EXPORT: {
//                    if (!Global.CheckConnectionInternet(context)) {
//                        ShowDialog.infoDialog(((Activity)context), "PasienQu", context.getString(R.string.must_be_online));
//                    }else {
//                        context.startActivity(new Intent(context, ExportActivity.class));
//                    }

                    context.startActivity(new Intent(context, ExportActivity2.class)
                            .setFlags(FLAG_ACTIVITY_CLEAR_TOP));
                    break;
                }

                case VIEW_DATA_MIGRATION: {
                    context.startActivity(new Intent(context, DataMigrationActivity.class)
                            .setFlags(FLAG_ACTIVITY_CLEAR_TOP));
                    break;
                }

                case VIEW_EULA: {
                    ShowDialog.ShowEulaDialog((Activity)context);
                    break;
                }

                case VIEW_HELP: {
                    Uri uri = Uri.parse("https://pasienqu.com/features");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    context.startActivity(intent);
                    break;
                }

                case VIEW_LOG_OUT: {
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            //fungsi buat dapetin sharepreference sebelum di clear karena ada perbaikan seting bahasa dan lokasi harus tetep ada
                            SharedPreferences sharedpreferences = JApplication.getInstance().getSharedPreferences("login_information", Context.MODE_PRIVATE);
                            String language = sharedpreferences.getString("language", "");
//                            String uuidLocation = sharedpreferences.getString("uuidLocation", "");

                            //fungsi logout
                            JApplication.getInstance().isLogOn = false;
                            JApplication.getInstance().clearSharedPreperence();  //clear session
                            Global.clearDatabase();  //clear db
                            context.startActivity(new Intent(context, LoginActivity.class));

                            //fungsi set preference yg sebelumnya diambil
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            if(language.equals("in")){
                                editor.putString("language", "in");
                            }
//                            editor.putString("uuidLocation", uuidLocation);
                            editor.commit();
                            editor.apply();
                            JApplication.getInstance().setLoginInformationBySharedPreferences();
                        }
                    };


                    Activity activity = (Activity) context;
                    ShowDialog.confirmDialog(activity, context.getString(R.string.logout_title),
                            context.getString(R.string.logout_content), runnable);
                    break;
                }
                case VIEW_GDRIVE: {
                    context.startActivity(new Intent(context, BackupRestoreDrive.class)
                            .setFlags(FLAG_ACTIVITY_CLEAR_TOP));
                    break;
                }
                case VIEW_DELETE_ARCHIVE: {
                    context.startActivity(new Intent(context, DeleteArchive.class)
                            .setFlags(FLAG_ACTIVITY_CLEAR_TOP));
                    break;
                }
                case VIEW_SATU_SEHAT: {
                    context.startActivity(new Intent(context, MedicalSatuSehatActivity.class)
                            .setFlags(FLAG_ACTIVITY_CLEAR_TOP));
                    break;
                }
                case VIEW_KIRIM_DATABASE: {
                    context.startActivity(new Intent(context, KirimDatabaseActivity.class)
                            .setFlags(FLAG_ACTIVITY_CLEAR_TOP));
                    break;
                }

                case VIEW_BACKUP_DRIVE: {
                    context.startActivity(new Intent(context, BackupDriveActivity.class)
                            .setFlags(FLAG_ACTIVITY_CLEAR_TOP));
                    break;
                }
                case VIEW_RESTORE_DRIVE: {
                    context.startActivity(new Intent(context, RestoreDriveActivity.class)
                            .setFlags(FLAG_ACTIVITY_CLEAR_TOP));
                    break;
                }

                case VIEW_DELETE_ACCOUNT: {
                    Uri uri = Uri.parse("https://pasienqu.com/delete-account");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    context.startActivity(intent);
                    break;
                }

            }
        });
    }



    @Override
    public int getItemCount() {
        return Datas.size();
    }


    public class ItemHolder extends RecyclerView.ViewHolder  {
        public TextView txtMenuName;
        public ImageView icon;

        public ItemHolder(View itemView) {
            super(itemView);
            txtMenuName = (TextView) itemView.findViewById(R.id.txtMenuName);
            icon = (ImageView) itemView.findViewById(R.id.imgIcon);
        }

    }

    public void addModel(MoreModel Datas) {
        int pos = this.dataFiltererd.size();
        this.Datas.add(Datas);
        notifyItemRangeInserted(pos, 1);
        this.dataFiltererd.add(Datas);
        notifyItemRangeInserted(pos, dataFiltererd.size());
    }

    public void removeAllModel(){
        int LastPosition = Datas.size();
        this.Datas.removeAll(Datas);
        LastPosition = dataFiltererd.size();
        this.dataFiltererd.removeAll(dataFiltererd);
        notifyItemRangeRemoved(0, LastPosition);
    }

//    private void export() {
//        final String locationExport = Environment.getExternalStorageDirectory() + "/PasienQu/" ;
//        final String locationExport10 = Environment.getExternalStorageDirectory()+Environment.DIRECTORY_DOCUMENTS;
//
//        Runnable runExport = new Runnable() {
//            @Override
//            public void run() {
//                File folder = new File(locationExport);
//                if (!folder.exists()){
//                    folder.mkdirs();
//                }
//                if (folder.exists()) {
//                    String namaFile = "PasienQu" + Global.serverNowFormated4Ekspor() + ".xls";
//                    String file = locationExport + namaFile;
//                    LaporanPraktekActivity.this.namaFileEkspor = file;
//                    createExcelSheet(file);
//                }
//            }
//        };
//
//    }

}
