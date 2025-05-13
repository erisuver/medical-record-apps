package com.orion.pasienqu_2.activities.more;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.telephony.gsm.GsmCellLocation;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.activities.home.home;
import com.orion.pasienqu_2.adapter.MoreAdapter;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.JConst;
import com.orion.pasienqu_2.globals.PurchaseBilling;
import com.orion.pasienqu_2.globals.ShowDialog;
import com.orion.pasienqu_2.models.LoginCompanyModel;
import com.orion.pasienqu_2.models.LoginInformationModel;
import com.orion.pasienqu_2.models.MoreModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MoreFragment extends Fragment {
    private RecyclerView rcvList;
    private View v;
    public MoreAdapter mAdapter;
    public List<MoreModel> ListItems = new ArrayList<>();
    private Activity thisActivity;
    private TextView tvMember, tvActivePeriod, tvExpiredPeriod, tvProduct, tvAppVersion;
    private boolean is_subuser;
    private CollapsingToolbarLayout collapsingToolbar;
    private AppBarLayout appBarLayout;
    private ConstraintLayout InformationLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.more_fragment, container, false);
        v = view;
        CreateView();
        InitClass();
        EventClass();
        return view;
    }

    private void CreateView() {
        rcvList = (RecyclerView) v.findViewById(R.id.rcvList);
        tvMember = (TextView) v.findViewById(R.id.tvMember);
        tvActivePeriod = (TextView) v.findViewById(R.id.tvActivePeriod);
        tvExpiredPeriod = (TextView) v.findViewById(R.id.tvExpiredPeriod);
        tvProduct = (TextView) v.findViewById(R.id.tvProduct);
        tvAppVersion = (TextView) v.findViewById(R.id.tvAppVersion);
        collapsingToolbar = (CollapsingToolbarLayout) v.findViewById(R.id.collapsingToolbar);
        appBarLayout = (AppBarLayout) v.findViewById(R.id.appBarLayout);
        InformationLayout = (ConstraintLayout) v.findViewById(R.id.InformationLayout);

        this.mAdapter = new MoreAdapter(getActivity(), ListItems, R.layout.more_list_item);
        thisActivity = getActivity();
    }

    private void InitClass() {
        rcvList.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false));
        rcvList.setAdapter(mAdapter);
        loadData();

        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.TitleCollapsed);
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.TitleExpanded);

        // This is the most important when you are putting custom TextView in CollapsingToolbar
        collapsingToolbar.setTitle(" ");

    }

    public void EventClass() {
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    //when collapsingToolbar at that time display actionbar title
                    collapsingToolbar.setTitle(tvMember.getText().toString());

                    isShow = true;
                } else if (isShow) {
                    //carefull there must a space between double quote otherwise it dose't work
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    public void loadData(){
        mAdapter.removeAllModel();
        MoreModel moreModel;

        LoginInformationModel loginInformationModel = JApplication.getInstance().loginInformationModel;
        is_subuser = loginInformationModel.getIs_subuser();
        is_subuser = true;
        if (!is_subuser) {
            moreModel = new MoreModel(2, getString(R.string.change_password), R.drawable.ic_change_password);
            mAdapter.addModel(moreModel);
            moreModel = new MoreModel(3, getString(R.string.set_language), R.drawable.ic_language);
            mAdapter.addModel(moreModel);
            moreModel = new MoreModel(4, getString(R.string.pin_protection), R.drawable.ic_pin_protection);
            mAdapter.addModel(moreModel);
            moreModel = new MoreModel(19, getString(R.string.log_out), R.drawable.ic_logout);
            mAdapter.addModel(moreModel);
        }else if(Global.ReadOnlyMode()){
            moreModel = new MoreModel(3, getString(R.string.set_language), R.drawable.ic_language);
            mAdapter.addModel(moreModel);
            moreModel = new MoreModel(8, getString(R.string.renew_subscription), R.drawable.ic_renew_subscriptions);
            mAdapter.addModel(moreModel);
            moreModel = new MoreModel(13, getString(R.string.export_excel), R.drawable.ic_export);
            mAdapter.addModel(moreModel);
            moreModel = new MoreModel(19, getString(R.string.log_out), R.drawable.ic_logout);
            mAdapter.addModel(moreModel);
        }else {
            moreModel = new MoreModel(1, getString(R.string.edit_profile), R.drawable.ic_account_edit);
            mAdapter.addModel(moreModel);
            moreModel = new MoreModel(2, getString(R.string.change_password), R.drawable.ic_change_password);
            mAdapter.addModel(moreModel);
            moreModel = new MoreModel(3, getString(R.string.set_language), R.drawable.ic_language);
            mAdapter.addModel(moreModel);
            moreModel = new MoreModel(4, getString(R.string.pin_protection), R.drawable.ic_pin_protection);
            mAdapter.addModel(moreModel);
            moreModel = new MoreModel(5, getString(R.string.satu_sehat), R.drawable.ic_satu_sehat);
            mAdapter.addModel(moreModel);
            moreModel = new MoreModel(6, getString(R.string.auto_generate_id), R.drawable.ic_cardbulletedsettingsoutline);
            mAdapter.addModel(moreModel);
            moreModel = new MoreModel(7, getString(R.string.work_location), R.drawable.ic_work_locations);
            mAdapter.addModel(moreModel);
            moreModel = new MoreModel(8, getString(R.string.renew_subscription), R.drawable.ic_renew_subscriptions);
            mAdapter.addModel(moreModel);
            moreModel = new MoreModel(9, getString(R.string.billing_templates), R.drawable.ic_notebook_outline_icon);
            mAdapter.addModel(moreModel);
            moreModel = new MoreModel(10, getString(R.string.note_templates), R.drawable.ic_note_multiple);
            mAdapter.addModel(moreModel);

            //ubah subacoun jadi multi user
//            moreModel = new MoreModel(10, getString(R.string.sub_accounts), R.drawable.ic_sub_account);
//            mAdapter.addModel(moreModel);
            moreModel = new MoreModel(11, getString(R.string.multi_user), R.drawable.ic_sub_account);
            mAdapter.addModel(moreModel);

            moreModel = new MoreModel(12, getString(R.string.import_database), R.drawable.ic_add_to_home_screen);
            mAdapter.addModel(moreModel);
            moreModel = new MoreModel(13, getString(R.string.export_excel), R.drawable.ic_export);
            mAdapter.addModel(moreModel);
//            moreModel = new MoreModel(14, getString(R.string.title_backup_restore_gdrive), R.drawable.ic_google_drive_icon);
//            mAdapter.addModel(moreModel);
            moreModel = new MoreModel(21, "Backup", R.drawable.ic_backup);
            mAdapter.addModel(moreModel);
            moreModel = new MoreModel(22, getString(R.string.restore_drive), R.drawable.ic_restore);
            mAdapter.addModel(moreModel);
            moreModel = new MoreModel(20, "Kirim Data", R.drawable.ic_baseline_upload_24);
            mAdapter.addModel(moreModel);
            moreModel = new MoreModel(15, getString(R.string.title_delete_archive), R.drawable.ic_baseline_delete_forever_24);
            mAdapter.addModel(moreModel);
//            moreModel = new MoreModel(16, getString(R.string.data_migration), R.drawable.ic_baseline_compare_arrows_24);
//            mAdapter.addModel(moreModel);
            moreModel = new MoreModel(23, getString(R.string.title_delete_account), R.drawable.ic_baseline_no_accounts_24);
            mAdapter.addModel(moreModel);
            moreModel = new MoreModel(17, getString(R.string.eula), R.drawable.ic_eula);
            mAdapter.addModel(moreModel);
            moreModel = new MoreModel(18, getString(R.string.help), R.drawable.ic_baseline_help_24);
            mAdapter.addModel(moreModel);
            moreModel = new MoreModel(19, getString(R.string.log_out), R.drawable.ic_logout);
            mAdapter.addModel(moreModel);
        }

        updateUI();
    }

    private void updateUI(){
        LoginCompanyModel loginCompanyModel = JApplication.getInstance().loginCompanyModel;
        tvMember.setText(loginCompanyModel.getName());

        long archivedUntil = Global.getMillisDateFmt(loginCompanyModel.getGrace_period_date(), "yyyy-MM-dd");
        tvActivePeriod.setText(String.format(getString(R.string.active_until_s), Global.getDateFormated(archivedUntil, "dd/MM/yyyy")));
        long expiredDate = Global.getMillisDateFmt(loginCompanyModel.getExpired_date(), "yyyy-MM-dd");
        tvExpiredPeriod.setText(getString(R.string.expired_date)+" : "+Global.getDateFormated(expiredDate, "dd/MM/yyyy"));

        switch (loginCompanyModel.getBilling_period()) {
            case JConst.billingDay30:
                tvProduct.setText(getString(R.string.product) + " : " + loginCompanyModel.getProduct().toUpperCase() + " (" + getString(R.string.every_30_days) + ")");
                break;
            case JConst.billingDay365:
                tvProduct.setText(getString(R.string.product)+" : "+loginCompanyModel.getProduct().toUpperCase()+" ("+getString(R.string.every_365_days)+")");
        }
        tvAppVersion.setText(getString(R.string.app_version)+" : "+ JApplication.getInstance().getVersionName());

        //auto size appbar (yg biru2) sesuai panjang data agar tidak nabrak jika kepanjangan.
        int height = Global.getViewHeight(InformationLayout);
        ViewGroup.LayoutParams params = appBarLayout.getLayoutParams();
        params.height = height + 200;
        appBarLayout.setLayoutParams(params);
    }

    @Override
    public void onResume() {
        super.onResume();
        //untuk mengecek jika ada purchase pending
        boolean isPendingPurchase30 = JApplication.getInstance().sharedPrefSubscription.getString("status_payment_pending_30", "").equals("");
        boolean isPendingPurchase365 = JApplication.getInstance().sharedPrefSubscription.getString("status_payment_pending_365", "").equals("");
        if ((!isPendingPurchase30 || !isPendingPurchase365) && Global.CheckConnectionInternet(thisActivity)) {
            new PurchaseBilling(thisActivity);
        }
    }
}
