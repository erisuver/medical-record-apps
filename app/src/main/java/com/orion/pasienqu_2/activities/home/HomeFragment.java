package com.orion.pasienqu_2.activities.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.activities.syncronize_data.SyncronizeDataActivity;
import com.orion.pasienqu_2.adapter.AppointmentAdapter;
import com.orion.pasienqu_2.adapter.HomeAdapter;
import com.orion.pasienqu_2.data_table.AppointmentTable;
import com.orion.pasienqu_2.data_table.BillingTable;
import com.orion.pasienqu_2.data_table.PatientTable;
import com.orion.pasienqu_2.data_table.RecordTable;
import com.orion.pasienqu_2.data_table.WorkLocationTable;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.GlobalMySQL;
import com.orion.pasienqu_2.globals.JConst;
import com.orion.pasienqu_2.globals.SharedPrefsUtils;
import com.orion.pasienqu_2.globals.ShowDialog;
import com.orion.pasienqu_2.models.AppointmentModel;
import com.orion.pasienqu_2.models.HomeModel;
import com.orion.pasienqu_2.models.LoginCompanyModel;
import com.orion.pasienqu_2.models.LoginInformationModel;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView rcvLoad, rcvLoadAppointment;
    private View v;
    private SwipeRefreshLayout swipe;
    public HomeAdapter mAdapter;
    public List<HomeModel> ListItems = new ArrayList<>();
    private Activity thisActivity;
    private FloatingActionButton btnRefresh, btnRefreshAkun;
    private TextView txtLocation, txtAppointmentNoData, txtLoggedAs, txtActiveUntil, txtUser, txtAppointments;
    private Button btnSetLocation;
    private String uuidLocation;

    public AppointmentAdapter mAdapterAppointment;
    public List<AppointmentModel> listAppointment = new ArrayList<>();

    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        v = view;
        CreateView();
        InitClass();
        EventClass();
        return view;
    }

    private void CreateView() {
        rcvLoad = (RecyclerView) v.findViewById(R.id.rcvLoad);
        rcvLoadAppointment = (RecyclerView) v.findViewById(R.id.rcvLoadAppointment);
        swipe = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        btnRefresh = (FloatingActionButton) v.findViewById(R.id.btnRefresh);
        btnSetLocation = (Button) v.findViewById(R.id.btnSetLocation);
        txtLocation = (TextView) v.findViewById(R.id.txtLocation);
        txtAppointmentNoData = (TextView) v.findViewById(R.id.txtAppointmentNoData);
        txtLoggedAs = (TextView) v.findViewById(R.id.txtLoggedAs);
        txtActiveUntil = (TextView) v.findViewById(R.id.txtActiveUntil);
        txtUser = (TextView) v.findViewById(R.id.txtUser);
        txtAppointments = (TextView) v.findViewById(R.id.txtAppointments);
        btnRefreshAkun = (FloatingActionButton) v.findViewById(R.id.btnRefreshAkun);

        this.mAdapter = new HomeAdapter(getActivity(), ListItems, R.layout.home_list_item);
        this.mAdapterAppointment = new AppointmentAdapter(getActivity(), listAppointment, R.layout.appointment_list_item);
        thisActivity = getActivity();
    }

    private void InitClass() {
        uuidLocation = "";
        rcvLoad.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false));
        rcvLoad.setAdapter(mAdapter);

        rcvLoadAppointment.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false));
        rcvLoadAppointment.setAdapter(mAdapterAppointment);
        loadData();

        //tutup btn syncronize
        btnRefresh.setVisibility(View.GONE);
        JApplication.getInstance().billingTable.deleteBillingTemp();  //delete billing temp yg ngegantung
    }

    public void loadData() {
        mAdapter.removeAllModel();
        PatientTable patientTable = JApplication.getInstance().patientTable;
        RecordTable recordTable = JApplication.getInstance().recordTable;
        BillingTable billingTable = JApplication.getInstance().billingTable;

        double totalPatient = patientTable.getCount(false);
        double thisMonthPatient = patientTable.getCount(true);

        HomeModel homeModelPatient = new HomeModel(JConst.TITLE_HOME_PATIENT, totalPatient, thisMonthPatient);

        double totalRecord = recordTable.getCount(false);
        double thisMonthRecord = recordTable.getCount(true);
        HomeModel homeModelRecord = new HomeModel(JConst.TITLE_HOME_MEDICAL_RECORD, totalRecord, thisMonthRecord);

        double totalBilling = billingTable.getSum(false);
        double thisMonthBilling = billingTable.getSum(true);
        HomeModel homeModelBilling = new HomeModel(JConst.TITLE_HOME_BILLING, totalBilling, thisMonthBilling);

        mAdapter.addModel(homeModelPatient);
        mAdapter.addModel(homeModelRecord);
        mAdapter.addModel(homeModelBilling);

        mAdapter.notifyDataSetChanged();
        swipe.setRefreshing(false);
        loadDataAppointment();
        loadLoginInformation();
    }

    public void loadLoginInformation() {
        LoginInformationModel loginInformationModel = JApplication.getInstance().loginInformationModel;
        LoginCompanyModel loginCompanyModel = JApplication.getInstance().loginCompanyModel;
        if (!loginInformationModel.getUuidLocation().equals("")) {
            WorkLocationTable workLocationTable = JApplication.getInstance().workLocationTable;
            String location = workLocationTable.getLocationByUuId(loginInformationModel.getUuidLocation());
            if (!location.equals("")) {
                txtLocation.setText(location);
            }else{
                txtLocation.setText(R.string.default_work_location_not_set);
            }
        }else{
            txtLocation.setText(R.string.default_work_location_not_set);
        }
        txtUser.setText(String.format(getString(R.string.welcome_user), loginCompanyModel.getName()));
        long activeUntil = Global.getMillisDateFmt(loginCompanyModel.getGrace_period_date(), "yyyy-MM-dd");
        long expiredDate = Global.getMillisDateFmt(loginCompanyModel.getGrace_period_date(), "yyyy-MM-dd");

//        long activeUntil = Global.getMillisDateFmt(loginCompanyModel.getTrial_end_date(), "yyyy-MM-dd");
        txtActiveUntil.setText(String.format(getString(R.string.active_until_s),Global.getDateFormated(activeUntil, "dd/MM/yyyy")));
        txtLoggedAs.setText(String.format(getString(R.string.logged_as), loginInformationModel.getUsername()));

        if (Global.ReadOnlyMode()){  //read only mode
            txtUser.setTextColor(Color.RED);
        }else{
            txtUser.setTextColor(Color.BLACK);
        }

        /**cek status subscription -- tutup pindah ke home */
//        long lastCekSubs = SharedPrefsUtils.getLongPreference(getContext(),"last_cek_subscription", 0);
//        long dateNow = Global.serverNowWithoutTimeLong();
//        if (dateNow > lastCekSubs && JApplication.getInstance().loginCompanyModel.getId() != 0) {
//            if (SharedPrefsUtils.getBooleanPreference(thisActivity, "is_mysql", false)) {
//                GlobalMySQL.getStatusSubsription(thisActivity, getContext());
//            }else {
//                GlobalOdoo.getStatusSubsription(thisActivity, getContext());
//            }
//        }

    }


    public void loadDataAppointment() {
        mAdapterAppointment.removeAllModel();
        mAdapterAppointment.isHome = true;
        AppointmentTable appointmentTable = new AppointmentTable(thisActivity);
        List<AppointmentModel> listAppointmentModels = appointmentTable.getRecordsAppointment(Global.serverNowLong());
        if (listAppointmentModels.size() == 0){
            txtAppointmentNoData.setVisibility(View.VISIBLE);
            rcvLoadAppointment.setVisibility(View.GONE);
        }else {
            txtAppointmentNoData.setVisibility(View.GONE);
            rcvLoadAppointment.setVisibility(View.VISIBLE);
            mAdapterAppointment.addModels(listAppointmentModels);
            mAdapterAppointment.notifyDataSetChanged();
        }
        txtAppointments.setText(getString(R.string.title_appointment)+" ("+appointmentTable.getCount()+")");
        swipe.setRefreshing(false);
    }

    private void EventClass() {
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });

        btnRefresh.setOnClickListener(view -> {
            Intent s = new Intent(thisActivity, SyncronizeDataActivity.class);
            s.putExtra("uuid", "");
            startActivityForResult(s,1);
        });


        btnSetLocation.setOnClickListener(view -> {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    SharedPreferences sharedpreferences;
                    sharedpreferences = JApplication.getInstance().getSharedPreferences("login_information", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("uuidLocation", JApplication.getInstance().SelectedLov);
                    editor.commit();
                    editor.apply();
                    JApplication.getInstance().setLoginInformationBySharedPreferences();
                }
            };
            ShowDialog.showWorkLocation(thisActivity, txtLocation, uuidLocation, runnable);
        });

        btnRefreshAkun.setOnClickListener(view -> {
            if (SharedPrefsUtils.getBooleanPreference(thisActivity, "is_mysql", false)) {
                GlobalMySQL.getStatusSubsription(thisActivity, getContext(), true);
            }else {
//                GlobalOdoo.getStatusSubsription(thisActivity, getContext());    //erik tutup odoo 040325
            }

        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loadData();
    }

}