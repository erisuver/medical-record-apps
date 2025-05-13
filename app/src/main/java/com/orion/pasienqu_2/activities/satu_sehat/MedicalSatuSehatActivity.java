package com.orion.pasienqu_2.activities.satu_sehat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.textfield.TextInputEditText;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.activities.more.work_location.FilterWorkLocationActivity;
import com.orion.pasienqu_2.activities.more.work_location.WorkLocationActivity;
import com.orion.pasienqu_2.activities.record.FilterRecordActivity;
import com.orion.pasienqu_2.activities.satu_sehat.location.LocationSatuSehatActivity;
import com.orion.pasienqu_2.adapter.CustomAutoCompleteAdapter;
import com.orion.pasienqu_2.data_table.GlobalTable;
import com.orion.pasienqu_2.data_table.MedicalSatuSehatTable;
import com.orion.pasienqu_2.data_table.RecordTable;
import com.orion.pasienqu_2.globals.CustomAppCompatActivity;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.GlobalSatuSehat;
import com.orion.pasienqu_2.globals.ILoadMore;
import com.orion.pasienqu_2.globals.JConst;
import com.orion.pasienqu_2.globals.ListValue;
import com.orion.pasienqu_2.globals.SharedPrefsUtils;
import com.orion.pasienqu_2.globals.ShowDialog;
import com.orion.pasienqu_2.models.MedicalSatuSehatModel;
import com.orion.pasienqu_2.models.PractitionerModel;
import com.orion.pasienqu_2.models.RecordDiagnosaModel;

import java.util.ArrayList;
import java.util.List;

public class MedicalSatuSehatActivity extends CustomAppCompatActivity {
    public MedicalSatuSehatAdapter mAdapter;
    public List<MedicalSatuSehatModel> ListItems = new ArrayList<>();
    private Activity thisActivity;
    private RecyclerView rcvLoad;
    private SwipeRefreshLayout swipe;
    private EditText txtRecordFrom, txtRecordto;
    private Spinner spinLocation, spinStatus;
    private AppCompatButton btnKirim;

    //global
    private String practitionerID;

    ILoadMore loadMore;
    private int totalItemCount, lastVisibleItem;
    private LinearLayoutManager linearLayoutManager;
    private boolean IsLoading;
    int visibleThreshold = 20;
    private int offset = 0;

    private String status = "";
    private int idWorkLocation = 0;
    private long dateFrom = 0, dateTo = 0;

    private List<String> listStringWorkLocation = new ArrayList<>();
    private List<String> listIdWorkLocation = new ArrayList<>();
    private List<String> listStringStatus = new ArrayList<>();
    private List<String> listidStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSpecialLanguageSelected();
        setContentView(R.layout.activity_medical_satu_sehat);
        JApplication.currentActivity = this; //set awal currenactivity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.satu_sehat));
        CreateView();
        InitClass();
        EventClass();

        practitionerID = JApplication.getInstance().globalTable.getPractitionerID();
        if (practitionerID.isEmpty()) {
            Intent intent = new Intent(this, LocationSatuSehatActivity.class);
            intent.putExtra("isForceOpen", true);
            startActivity(intent);
            finish();
        }
    }

    private void CreateView() {
        rcvLoad = findViewById(R.id.rcvLoad);
        swipe = findViewById(R.id.swipe_refresh_layout);
        rcvLoad = findViewById(R.id.rcvLoad);
        txtRecordFrom = findViewById(R.id.txtRecordFrom);
        txtRecordto = findViewById(R.id.txtRecordTo);
        spinLocation = findViewById(R.id.spinLocation);
        spinStatus = findViewById(R.id.spinStatus);
        btnKirim = findViewById(R.id.btnKirim);

        this.mAdapter = new MedicalSatuSehatAdapter(MedicalSatuSehatActivity.this, ListItems, R.layout.medical_satu_sehat_list_item);
        thisActivity = MedicalSatuSehatActivity.this;
        rcvLoad.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false));
        linearLayoutManager = (LinearLayoutManager) rcvLoad.getLayoutManager();
        rcvLoad.setAdapter(mAdapter);
    }

    private void InitClass() {
        isiCombo();
        txtRecordFrom.setText(Global.serverNow());
        txtRecordto.setText(Global.serverNow());
        dateFrom = Global.serverNowLong();
        dateTo = Global.serverNowLong();
        status = "";
        idWorkLocation = 0;
        loadRefresh();
    }

    private void EventClass() {
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!IsLoading) {
                    mAdapter.removeAllModel();
                    RefreshRecyclerView();
                }
                swipe.setRefreshing(false);
            }
        });

        setLoadMore(new ILoadMore() {
            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        LoadData();
                    }
                }, 100);
            }
        });

        rcvLoad.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                RefreshRecyclerView();
            }
        });

        txtRecordFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Global.datePickerEdittextClick(MedicalSatuSehatActivity.this, txtRecordFrom, view, () -> loadRefresh());
            }
        });

        txtRecordto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Global.datePickerEdittextClick(MedicalSatuSehatActivity.this, txtRecordto, view, () -> loadRefresh());
            }
        });

        spinLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                idWorkLocation = Integer.parseInt(listIdWorkLocation.get(position));
                // Lakukan tindakan yang diperlukan setelah pemilihan item
                GlobalSatuSehat.checkTokenSatuSehatPerLocation(thisActivity, idWorkLocation, () -> loadRefresh());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Tindakan yang diambil jika tidak ada item yang dipilih
            }
        });

        spinStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                status = listidStatus.get(position);
                loadRefresh();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Tindakan yang diambil jika tidak ada item yang dipilih
            }
        });

        btnKirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Global.CheckConnectionInternet(thisActivity)) {
                    ShowDialog.infoDialog(thisActivity, getString(R.string.information), getString(R.string.must_be_online));
                    return;
                }

                boolean isAdaData = false;
                for (MedicalSatuSehatModel data : mAdapter.Datas) {
                    if (data.isSelected()) {
                        isAdaData = true;
                        break;
                    }
                }
                if (isAdaData) {
                    ShowDialog.confirmDialog(thisActivity, getString(R.string.title_app),
                            getString(R.string.confirm_send_data_satu_sehat), () -> kirimSatuSehat());
                }
            }
        });
    }

    private void isiCombo() {
        //isi status
        listStringStatus = ListValue.list_string_status_satu_sehat(getApplicationContext(), true);
        listidStatus = ListValue.list_value_status_satu_sehat(getApplicationContext(), true);

        String[] mStringArrayStatus = new String[listStringStatus.size()];
        mStringArrayStatus = listStringStatus.toArray(mStringArrayStatus);
        CustomAutoCompleteAdapter statusAdapter = new CustomAutoCompleteAdapter(this, android.R.layout.simple_dropdown_item_1line, mStringArrayStatus);
        spinStatus.setAdapter(statusAdapter);
        status = "";

        //isi location
        List<String> listStringWorkLocationTemp = ListValue.list_work_location(getApplicationContext());
        List<String> listIdWorkLocationTemp = ListValue.list_id_work_location(getApplicationContext());
        listStringWorkLocation.addAll(listStringWorkLocationTemp);
        listIdWorkLocation.addAll(listIdWorkLocationTemp);

        String[] mStringArrayWorkLocation = new String[listStringWorkLocation.size()];
        mStringArrayWorkLocation = listStringWorkLocation.toArray(mStringArrayWorkLocation);
        CustomAutoCompleteAdapter workLocationAdapter = new CustomAutoCompleteAdapter(this, android.R.layout.simple_dropdown_item_1line, mStringArrayWorkLocation);
        spinLocation.setAdapter(workLocationAdapter);
        idWorkLocation = 0;
    }


    public void setLoadMore(ILoadMore loadMore) {
        this.loadMore = loadMore;
    }

    public void setLoaded(boolean loading) {
        IsLoading = loading;
    }

    public void RefreshRecyclerView() {
        totalItemCount = linearLayoutManager.getItemCount();
        lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
        if (!IsLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
            if (loadMore != null) {
                loadMore.onLoadMore();
                setLoaded(true);
            }
        }
    }

    public void loadRefresh() {
        mAdapter.removeAllModel();
        RefreshRecyclerView();
    }

    private void LoadData() {
        dateFrom = Global.getMillisDate(txtRecordFrom.getText().toString());
        dateTo = Global.getMillisDate(txtRecordto.getText().toString());

        MedicalSatuSehatTable medicalSatuSehatTable = new MedicalSatuSehatTable(thisActivity);
        medicalSatuSehatTable.setFilter(status, idWorkLocation, dateFrom, dateTo);
        medicalSatuSehatTable.setOffset(mAdapter.getItemCount());

        if (mAdapter.getItemCount() == 0) {
            ListItems = medicalSatuSehatTable.getRecords(visibleThreshold);
        } else {
            ListItems = medicalSatuSehatTable.getRecords(100);
        }

        mAdapter.addModels(ListItems);
        mAdapter.notifyDataSetChanged();
        IsLoading = false;
        setLoaded(false);
        swipe.setRefreshing(false);
    }

/*    private void kirimSatuSehat() {
        for (MedicalSatuSehatModel data : mAdapter.Datas) {
            if (data.isSelected()) {
                Runnable runSuccess = new Runnable() {
                    @Override
                    public void run() {
                        //lakukan save
                        ShowDialog.infoDialog(MedicalSatuSehatActivity.this, getString(R.string.information),"Berhasil Terkirim");
                        loadRefresh();
                    }
                };
                Runnable runFailed = new Runnable() {
                    @Override
                    public void run() {
                        ShowDialog.infoDialog(MedicalSatuSehatActivity.this, getString(R.string.information),"Gagal");
                    }
                };
                GlobalSatuSehat.createResumeMedis(MedicalSatuSehatActivity.this, runSuccess, runFailed, data, practitionerID);
            }
        }
    }*/

    /*   private void kirimSatuSehat() {
           int totalData = 0;
           for (MedicalSatuSehatModel data : mAdapter.Datas) {
               if (data.isSelected()) {
                   totalData += 1;
               }
           }
           final ProgressDialog progressDialog = new ProgressDialog(MedicalSatuSehatActivity.this);
           progressDialog.setTitle(getString(R.string.sending_data_satu_sehat));
           progressDialog.setMessage(getString(R.string.inform_please_wait));
           progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
           progressDialog.setMax(totalData);
           progressDialog.setCancelable(false);

           progressDialog.show();

           // Variabel untuk mengetahui apakah dialog error sudah ditampilkan atau belum
           final boolean[] errorDialogShown = {false};
           int currentProgress = 0;
           int finalTotalData = totalData;

           for (int i = 0; i < mAdapter.Datas.size(); i++) {
               final MedicalSatuSehatModel data = mAdapter.Datas.get(i);
               if (data.isSelected()) {
                   currentProgress += 1;
                   int finalCurrentProgress = currentProgress;
                   Runnable runSuccess = new Runnable() {
                       @Override
                       public void run() {
                           //lakukan save
                           progressDialog.setProgress(finalCurrentProgress);
                           if (finalCurrentProgress == finalTotalData) {
                               progressDialog.dismiss();
                               ShowDialog.infoDialog(MedicalSatuSehatActivity.this, getString(R.string.information), getString(R.string.send_success_satu_sehat));
                               loadRefresh();
                           }
                       }
                   };
                   Runnable runFailed = new Runnable() {
                       @Override
                       public void run() {
                           progressDialog.dismiss();
                           ShowDialog.infoDialog(MedicalSatuSehatActivity.this, getString(R.string.information), getString(R.string.failed_save));
                       }
                   };

                   // Modifikasi GlobalSatuSehat.createResumeMedis untuk menangani koneksi terputus
                   GlobalSatuSehat.createResumeMedis(MedicalSatuSehatActivity.this, runSuccess, runFailed, data, practitionerID, new GlobalSatuSehat.RequestCallback() {
                       @Override
                       public void onConnectionError() {
                           progressDialog.dismiss();

                           if (!errorDialogShown[0]) {
                               ShowDialog.infoDialog(MedicalSatuSehatActivity.this, getString(R.string.information), getString(R.string.send_error_satu_sehat));
                               loadRefresh();
                           }
                           errorDialogShown[0] = true; // Set dialog error telah ditampilkan
                       }
                   });
               }
           }
       }
   */
    private void kirimSatuSehat() {
        int totalData = hitungDataTerpilih();
        if (totalData == 0) return;

        ProgressDialog progressDialog = buatProgressDialog(totalData);
        boolean[] errorDialogShown = {false};

        kirimData(progressDialog, errorDialogShown);
    }

    private int hitungDataTerpilih() {
        int totalData = 0;
        for (MedicalSatuSehatModel data : mAdapter.Datas) {
            if (data.isSelected()) {
                totalData++;
            }
        }
        return totalData;
    }

    private ProgressDialog buatProgressDialog(int totalData) {
        ProgressDialog progressDialog = new ProgressDialog(MedicalSatuSehatActivity.this);
        progressDialog.setTitle(getString(R.string.sending_data_satu_sehat));
        progressDialog.setMessage(getString(R.string.inform_please_wait));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(totalData);
        progressDialog.setCancelable(false);
        progressDialog.show();
        return progressDialog;
    }

    private void kirimData(ProgressDialog progressDialog, boolean[] errorDialogShown) {
        int currentProgress = 0;
        int finalTotalData = hitungDataTerpilih();

        for (int i = 0; i < mAdapter.Datas.size(); i++) {
            MedicalSatuSehatModel data = mAdapter.Datas.get(i);
            if (data.isSelected()) {
                currentProgress++;
                final int finalCurrentProgress = currentProgress;
                if (TextUtils.isEmpty(data.getId_encounter())) {
                    kirimEncounter(data, finalTotalData, finalCurrentProgress, progressDialog, errorDialogShown);
                }else{
                    //looping icd untuk dikirim satu persatu
                    int jmlKirimDiagnosa = 0;
                    int idx = 0;
                    boolean isSelesai = false;
                    for (RecordDiagnosaModel diagnosis : data.getIcd_ids()) {
                        idx++;
                        isSelesai = idx == data.getIcd_ids().size();
                        int countCode = JApplication.getInstance().globalTable.getCount("pasienqu_icd10", "code = '"+diagnosis.getIcd_code()+"'");
                        if (countCode > 0 && TextUtils.isEmpty(diagnosis.getId_condition())) {
                            jmlKirimDiagnosa += 1;
                            kirimDiagnosa(data, diagnosis, data.getId_encounter(), finalTotalData, finalCurrentProgress, isSelesai, progressDialog, errorDialogShown);
                        }
                    }
                    if (jmlKirimDiagnosa == 0){
                        //update status isPost
                        JApplication.getInstance().globalTable.UpdateTable("pasienqu_record", "isPosted = 'T'", "WHERE uuid = '" + data.getRecord_uuid() + "'");
                        //progress
                        updateProgress(progressDialog, finalCurrentProgress, finalTotalData, true);
                    }
                }
            }
        }
    }

    private void kirimEncounter(MedicalSatuSehatModel data, int finalTotalData, int finalCurrentProgress, ProgressDialog progressDialog, boolean[] errorDialogShown) {
        GlobalSatuSehat.sendEncounter(data, practitionerID, new GlobalSatuSehat.RequestCallback() {
            @Override
            public void onSuccess() {
                int totalICD = data.getIcd_ids().size();
                if (totalICD > 0){ //jika ada icd
                    //dapatkan idEncounter yang sebelumnya dikirim
                    String idEncounter = JApplication.getInstance().globalTable.GetStringFromTable("pasienqu_record", "id_encounter", "WHERE uuid = '"+data.getRecord_uuid()+"'");
                    //looping icd untuk dikirim satu persatu
                    int jmlKirimDiagnosa = 0;
                    int idx = 0;
                    boolean isSelesai = false;
                    for (RecordDiagnosaModel diagnosis : data.getIcd_ids()) {
                        idx++;
                        isSelesai = idx == totalICD;
                        int countCode = JApplication.getInstance().globalTable.getCount("pasienqu_icd10", "code = '"+diagnosis.getIcd_code()+"'");
                        if (countCode > 0) {
                            jmlKirimDiagnosa += 1;
                            kirimDiagnosa(data, diagnosis, idEncounter, finalTotalData, finalCurrentProgress, isSelesai, progressDialog, errorDialogShown);
                        }
                    }
                    if (jmlKirimDiagnosa == 0){
                        //update status isPost
                        JApplication.getInstance().globalTable.UpdateTable("pasienqu_record", "isPosted = 'T'", "WHERE uuid = '" + data.getRecord_uuid() + "'");
                        //progress
                        updateProgress(progressDialog, finalCurrentProgress, finalTotalData, true);
                    }
                }else { //jika tidak ada icd
                    //update status isPost
                    JApplication.getInstance().globalTable.UpdateTable("pasienqu_record", "isPosted = 'T'", "WHERE uuid = '" + data.getRecord_uuid() + "'");
                    //progress
                    updateProgress(progressDialog, finalCurrentProgress, finalTotalData, true);
                }
            }

            @Override
            public void onConnectionError() {
                progressDialog.dismiss();
                tampilkanErrorDialog(errorDialogShown);
            }
        });
    }

    private void kirimDiagnosa(MedicalSatuSehatModel data, RecordDiagnosaModel diagnosa, String encounterId, int finalTotalData, int finalCurrentProgress, boolean isSelesai, ProgressDialog progressDialog, boolean[] errorDialogShown) {
        GlobalSatuSehat.sendCondition(data, diagnosa, encounterId, new GlobalSatuSehat.RequestCallback() {
            @Override
            public void onSuccess() {
                //update status isPost
                JApplication.getInstance().globalTable.UpdateTable("pasienqu_record", "isPosted = 'T'", "WHERE uuid = '" + data.getRecord_uuid() + "'");
                updateProgress(progressDialog, finalCurrentProgress, finalTotalData, isSelesai);
            }

            @Override
            public void onConnectionError() {
                progressDialog.dismiss();
                tampilkanErrorDialog(errorDialogShown);
            }
        });
    }

    private void tampilkanErrorDialog(boolean[] errorDialogShown) {
        if (!errorDialogShown[0]) {
            JApplication.getInstance().cancelPendingRequests(Global.tag_json_obj);  //cancel semua pending req queue
            ShowDialog.infoDialog(MedicalSatuSehatActivity.this, getString(R.string.information), getString(R.string.send_error_satu_sehat));
            loadRefresh();
        }
        errorDialogShown[0] = true; // Set dialog error telah ditampilkan
    }

    private void updateProgress(ProgressDialog progressDialog, int progress, int total, boolean isSelesai) {
        progressDialog.setProgress(progress);
        if (progress == total & isSelesai) {
            progressDialog.dismiss();
            ShowDialog.infoDialog(MedicalSatuSehatActivity.this, getString(R.string.information), getString(R.string.send_success_satu_sehat));
            loadRefresh();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_setting:
                intent = new Intent(this, LocationSatuSehatActivity.class);
                startActivityForResult(intent, 2);
                return true;
            case R.id.menu_help:
                ShowDialog.showDialogHelp(thisActivity);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_satu_sehat, menu);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loadRefresh();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        resetLanguage();
    }
}
