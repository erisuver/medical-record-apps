package com.orion.pasienqu_2.activities.more.renew_subscriotion;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.google.common.collect.ImmutableList;
import com.instacart.library.truetime.TrueTimeRx;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.Routes;
import com.orion.pasienqu_2.activities.home.home;
import com.orion.pasienqu_2.globals.CustomAppCompatActivity;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.GlobalMySQL;
import com.orion.pasienqu_2.globals.JConst;
import com.orion.pasienqu_2.globals.PurchaseBilling;
import com.orion.pasienqu_2.globals.ShowDialog;
import com.orion.pasienqu_2.models.LoginCompanyModel;
import com.orion.pasienqu_2.utility.GetIPAddress;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.reactivex.schedulers.Schedulers;

public class RenewSubscriptionActivity extends CustomAppCompatActivity {
    private TextView tvMonthRenewal, tvYearsRenewal;
    private Button btnPriceMonth, btnPriceYears;
    private String active30, expired30, active365, expired365;
    private BillingClient billingClient;
    private PurchasesUpdatedListener purchasesUpdatedListener;
    private Activity thisActivity = RenewSubscriptionActivity.this;
    private long activePeriod;
    private final SharedPreferences sharedPrefSubscription = JApplication.getInstance().sharedPrefSubscription;
    private final SharedPreferences.Editor sharedPrefSubsEdit = JApplication.getInstance().sharedPrefSubscriptionEdit;
    private boolean isAlreadyExpired = false;
    ProductDetails productDetails;
    private String uuid = UUID.randomUUID().toString();
    private String TAG = "RenewSubs";
    private String sku;
    private long ServerNow;
    private String orderID;
    private TextView tvActivePeriod, tvExpiredPeriod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renew_subscription);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.renew_subscription));
        CreateView();
        InitClass();
        EventClass();
    }

    private void CreateView(){
        tvMonthRenewal = (TextView) findViewById(R.id.tvMonthRenewal);
        tvYearsRenewal = (TextView) findViewById(R.id.tvYearsRenewal);
        btnPriceMonth = (Button) findViewById(R.id.btnPriceMonth);
        btnPriceYears = (Button) findViewById(R.id.btnPriceYears);
        tvActivePeriod = (TextView) findViewById(R.id.tvActivePeriod);
        tvExpiredPeriod = (TextView) findViewById(R.id.tvExpiredPeriod);
    }

    private void InitClass(){
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            isAlreadyExpired = extra.getBoolean("expired");
        }

        updateUI(Global.serverNowLong());
        TrueTimeRx.build()
                .initializeRx("time.google.com")
                .subscribeOn(Schedulers.io())
                .subscribe(date -> {
                    ServerNow = TrueTimeRx.now().getTime();
//                    updateUI(ServerNow);
                });

        purchasesUpdatedListener = new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
                orderID = "";
                if (purchases != null) {
                    orderID = purchases.get(0).getOrderId();
                }
                //if item newly purchased
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
                    handlePurchase(purchases);
                }
                //if item already purchased then check and reflect changes
                else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
                    billingClient.queryPurchasesAsync(
                            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP).build(),
                            new PurchasesResponseListener() {
                                public void onQueryPurchasesResponse(
                                        @NonNull BillingResult billingResult,
                                        @NonNull List<Purchase> purchases) {
                                    // Process the result
                                    if(purchases.size()>0){
                                        handlePurchase(purchases);
                                        update_status_log_pembayaran(JConst.STATUS_PAYMENT_SUCCESS, String.valueOf(purchases.get(1).getSkus()), orderID);
                                    }
                                }
                            });
                }
                //if purchase cancelled
                else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                    Toast.makeText(getApplicationContext(),"Purchase Canceled",Toast.LENGTH_SHORT).show();
//                    update_status_log_pembayaran(JConst.STATUS_PAYMENT_CANCELED, "", orderID);
                }
                else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.DEVELOPER_ERROR) {
                    update_status_log_pembayaran(JConst.STATUS_PAYMENT_PENDING, "", orderID);
                }
                // Handle any other error msgs
                else {
//                    Toast.makeText(getApplicationContext(),billingResult.getDebugMessage(),Toast.LENGTH_SHORT).show();
//                    update_status_log_pembayaran(JConst.STATUS_PAYMENT_CANCELED, "", orderID);
                }
            }
        };
        billingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener(purchasesUpdatedListener).build();

        refreshDataCompany();
    }

    private void refreshDataCompany() {
        ProgressDialog loading = Global.createProgresSpinner(thisActivity, getString(R.string.loading));
        loading.show();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                updateUI(ServerNow);
                loading.dismiss();
            }
        };
        GlobalMySQL.get_company(thisActivity, thisActivity, runnable, loading);
    }

    private void EventClass(){
        btnPriceMonth.setOnClickListener(view -> {
            //fff
            Runnable runSuccess = new Runnable() {
                @Override
                public void run() {
                    subscribe(view, JConst.renewSub30DaysId);
                }
            };
            Runnable runCheckEmail = new Runnable() {
                @Override
                public void run() {
                    check_email(runSuccess);
                }
            };
            if (Global.CheckConnectionInternet(RenewSubscriptionActivity.this) && JApplication.getInstance().real_url.isEmpty()) {
                GetIPAddress.get_ip_address(RenewSubscriptionActivity.this, runCheckEmail);
                return;
            }
            runCheckEmail.run();
        });

        btnPriceYears.setOnClickListener(view -> {
            //fff
            Runnable runSuccess = new Runnable() {
                @Override
                public void run() {
                    subscribe(view, JConst.renewSub365DaysId);
                }
            };
            Runnable runCheckEmail = new Runnable() {
                @Override
                public void run() {
                    check_email(runSuccess);
                }
            };
            if (Global.CheckConnectionInternet(RenewSubscriptionActivity.this) && JApplication.getInstance().real_url.isEmpty()) {
                GetIPAddress.get_ip_address(RenewSubscriptionActivity.this, runCheckEmail);
                return;
            }
//            subscribe(view, JConst.renewSub365DaysId);
            runCheckEmail.run();
        });
    }

    private void updateUI(long serverNow){
        String active30ui, expired30ui, active365ui, expired365ui;
        LoginCompanyModel loginCompanyModel = JApplication.getInstance().loginCompanyModel;
        activePeriod = Global.getMillisDateFmt(loginCompanyModel.getGrace_period_date(), "yyyy-MM-dd");
        //initialization
        if (serverNow < activePeriod){
            //format odoo (yyyy-MM-dd)
            active30 = Global.getDayFromNow(activePeriod, 30);
            expired30 = Global.getDayFromNow(Global.getMillisDateFmt(active30, "yyyy-MM-dd"), 365);  //rentang waktu read only
            active365 = Global.getDayFromNow(activePeriod, 365);
            expired365 = Global.getDayFromNow(Global.getMillisDateFmt(active365, "yyyy-MM-dd"), 365);    //rentang waktu read only

            //format ui (dd/MM/yyyy)
            active30ui = Global.getDayFromNowFormated(activePeriod, 30, "dd/MM/yyyy");
            expired30ui = Global.getDayFromNowFormated(Global.getMillisDateFmt(active30, "yyyy-MM-dd"), 365, "dd/MM/yyyy");  //rentang waktu read only
            active365ui = Global.getDayFromNowFormated(activePeriod, 365, "dd/MM/yyyy");
            expired365ui = Global.getDayFromNowFormated(Global.getMillisDateFmt(active365, "yyyy-MM-dd"), 365, "dd/MM/yyyy");    //rentang waktu read only

        }else{//jika sudah masuk mode baca / sudah expired  perhitungannya dimulai dari tanggal sekarang
            //format odoo (yyyy-MM-dd)
            active30 = Global.getDayFromNow(serverNow, 30);
            expired30 = Global.getDayFromNow(Global.getMillisDateFmt(active30, "yyyy-MM-dd"), 365);  //rentang waktu read only
            active365 = Global.getDayFromNow(serverNow, 365);
            expired365 = Global.getDayFromNow(Global.getMillisDateFmt(active365, "yyyy-MM-dd"), 365);    //rentang waktu read only

            //format ui (dd/MM/yyyy)
            active30ui = Global.getDayFromNowFormated(serverNow, 30, "dd/MM/yyyy");
            expired30ui = Global.getDayFromNowFormated(Global.getMillisDateFmt(active30, "yyyy-MM-dd"), 365, "dd/MM/yyyy");  //rentang waktu read only
            active365ui = Global.getDayFromNowFormated(serverNow, 365, "dd/MM/yyyy");
            expired365ui = Global.getDayFromNowFormated(Global.getMillisDateFmt(active365, "yyyy-MM-dd"), 365, "dd/MM/yyyy");    //rentang waktu read only
        }

        //set ui
        tvMonthRenewal.setText(getString(R.string.activiting_full_access) + active30ui + getString(R.string.activiting_read_only) +expired30ui);
        tvYearsRenewal.setText(getString(R.string.activiting_full_access) + active365ui + getString(R.string.activiting_read_only) + expired365ui);

        //current expiredate
        long archivedUntil = Global.getMillisDateFmt(loginCompanyModel.getGrace_period_date(), "yyyy-MM-dd");
        long expiredDate = Global.getMillisDateFmt(loginCompanyModel.getExpired_date(), "yyyy-MM-dd");
        tvActivePeriod.setText(String.format(getString(R.string.active_until_s), Global.getDateFormated(archivedUntil, "dd/MM/yyyy")));
        tvExpiredPeriod.setText(getString(R.string.expired_date)+" : "+Global.getDateFormated(expiredDate, "dd/MM/yyyy"));

    }


    private void renewSub(String productIDCode){
        if (!productIDCode.equals("")) {
            LoginCompanyModel loginCompanyModel = JApplication.getInstance().loginCompanyModel;
            int companyId = loginCompanyModel.getId();
            long maxperiod = Global.getMillisDateStrip(loginCompanyModel.getGrace_period_date());

            loginCompanyModel.setId(companyId);
            loginCompanyModel.setIs_trial(false);
            if (ServerNow == 0) {
                ServerNow = Global.serverNowLong();
            }
            if (ServerNow > maxperiod) {  //sudah dikondisi expired
//                loginCompanyModel.setLatest_activation_date(Global.serverNowFormated());
                loginCompanyModel.setSubsription_date(Global.serverNowFormated(ServerNow));
            } else {
//                loginCompanyModel.setLatest_activation_date(loginCompanyModel.getGrace_period_date());
                loginCompanyModel.setSubsription_date(loginCompanyModel.getGrace_period_date());
            }

            if (productIDCode.equals(JConst.renewSub30DaysId)) {
                loginCompanyModel.setBilling_period("day30");
//                loginCompanyModel.setGrace_period_date(active30);
//                loginCompanyModel.setExpired_date(expired30);
            } else if (productIDCode.equals(JConst.renewSub365DaysId)) {
                loginCompanyModel.setBilling_period("day365");
//                loginCompanyModel.setGrace_period_date(active365);
//                loginCompanyModel.setExpired_date(expired365);
            }

            //update data subs ke odoo
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Runnable runnable2 = new Runnable() {
                        @Override
                        public void run() {
                            ProgressDialog loading = new ProgressDialog(RenewSubscriptionActivity.this);
                            loading.dismiss();
//                            onBackPressed();

                            //jika sudah selesai update ke odoo maka hapus yg di sharepref
                            //hal ini fungsinya nanti jika product_id <> '' yg artinya gagal perpanjang masa aktif maka akan di proses lagi pembelianya
                            sharedPrefSubsEdit.putString("product_id", "");
                            sharedPrefSubsEdit.apply();

                            Runnable runHome = new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(RenewSubscriptionActivity.this, home.class);
                                    intent.putExtra("purchase_expired", isAlreadyExpired); //jika sudah expired lalu melakukan purchase, kirim extra utk pengecekan show dialog
                                    startActivity(intent);
                                }
                            };
                            ShowDialog.infoDialogWithRunnable(RenewSubscriptionActivity.this, "PasienQu", getString(R.string.payment_success), runHome);
                        }
                    };
//                    GlobalOdoo.get_company(RenewSubscriptionActivity.this, getApplicationContext(), runnable2);
                    GlobalMySQL.get_company(RenewSubscriptionActivity.this, getApplicationContext(), runnable2);
                }
            };
//            GlobalOdoo.renewSubscription(RenewSubscriptionActivity.this, getApplicationContext(), loginCompanyModel, runnable);
//            GlobalOdoo.newRenewSubscription(RenewSubscriptionActivity.this, loginCompanyModel, runnable);
            GlobalMySQL.newRenewSubscription(RenewSubscriptionActivity.this, loginCompanyModel, runnable);

        }
    }


    @Override
    public void onBackPressed() {
        if (!isAlreadyExpired) {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void subscribe(View view, String productID) {
        //check if service is already connected
        if (billingClient.isReady()) {
            initiatePurchase(productID);
        }
        //else reconnect service
        else{
            billingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener(purchasesUpdatedListener).build();
            billingClient.startConnection(new BillingClientStateListener() {
                @Override
                public void onBillingSetupFinished(BillingResult billingResult) {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        initiatePurchase(productID);
                    } else {
                        Toast.makeText(getApplicationContext(),"Error 0 "+billingResult.getDebugMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onBillingServiceDisconnected() {
                    Toast.makeText(getApplicationContext(),"Service Disconnected ",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void initiatePurchase(String productID) {
        save_log_pembayaran(productID);
        delete_log_pembayaran(productID);
        ImmutableList<QueryProductDetailsParams.Product> productList = ImmutableList.of(QueryProductDetailsParams.Product.newBuilder()
                .setProductId(productID)
                .setProductType(BillingClient.ProductType.INAPP)
                .build());

        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build();

        billingClient.queryProductDetailsAsync(
                params,
                new ProductDetailsResponseListener() {
                    public void onProductDetailsResponse(BillingResult billingResult, List<ProductDetails> productDetailsList) {
                        // Process the result
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            if (productDetailsList != null && productDetailsList.size() > 0) {
                                // Retrieve a value for "productDetails" by calling queryProductDetailsAsync()
                                productDetails = productDetailsList.get(0);
                                ImmutableList productDetailsParamsList =
                                        ImmutableList.of(
                                                BillingFlowParams.ProductDetailsParams.newBuilder()
                                                        // retrieve a value for "productDetails" by calling queryProductDetailsAsync()
                                                        .setProductDetails(productDetails)
                                                        // to get an offer token, call ProductDetails.getSubscriptionOfferDetails()
                                                        // for a list of offers that are available to the user
                                                        .build()
                                        );

                                BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                                        .setProductDetailsParamsList(productDetailsParamsList)
                                        .build();

                                // Launch the billing flow
                                billingClient.launchBillingFlow(thisActivity, billingFlowParams);
                            }
                            else{
                                //try to add item/product id "consumable" inside managed product in google play console
                                Toast.makeText(getApplicationContext(),"Purchase Item not Found",Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    " Error 1 "+billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                }
        );
    }


    private boolean verifyValidSignature(String signedData, String signature) {
        try {
            //To get key go to Developer Console > Select your app > Monetize > Monetization setup
            String base64Key = JConst.base64DeveloperKey;
            return Security.verifyPurchase(base64Key, signedData, signature);
        } catch (IOException e) {
            return false;
        }
    }

    private boolean isValidProduct(String sku){
       return sku.equals(JConst.renewSub30DaysId) || sku.equals(JConst.renewSub365DaysId);
    }

    void handlePurchase(List<Purchase>  purchases) {
        for(Purchase purchase:purchases) {
            sku = purchase.getSkus().get(0);
            orderID = purchase.getOrderId();
            //store to pref
            sharedPrefSubsEdit.putString("product_id", sku);
            sharedPrefSubsEdit.apply();
            //if item is purchased
            if (isValidProduct(sku) && purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED)
            {
                if (!verifyValidSignature(purchase.getOriginalJson(), purchase.getSignature())) {
                    // Invalid purchase >> show error to user
                    Toast.makeText(getApplicationContext(), "Error : Invalid Purchase", Toast.LENGTH_SHORT).show();
                    return;
                }
                // else purchase is valid
                //if item is purchased and not consumed
                if (!purchase.isAcknowledged()) {
                    ConsumeParams consumeParams = ConsumeParams.newBuilder()
                            .setPurchaseToken(purchase.getPurchaseToken())
                            .build();

                    billingClient.consumeAsync(consumeParams, consumeListener);
                }
            }
            //if purchase is pending
            else if((sku.equals(JConst.renewSub30DaysId) || sku.equals(JConst.renewSub365DaysId)) && purchase.getPurchaseState() == Purchase.PurchaseState.PENDING)
            {
                Toast.makeText(getApplicationContext(),
                        getApplicationContext().getString(R.string.purchase_peding), Toast.LENGTH_SHORT).show();
                //store to pref
                if(sku.equals(JConst.renewSub30DaysId)){
                    sharedPrefSubsEdit.putString("status_payment_pending_30", JConst.STATUS_PAYMENT_PENDING_30);
                }else{
                    sharedPrefSubsEdit.putString("status_payment_pending_365", JConst.STATUS_PAYMENT_PENDING_365);
                }
                sharedPrefSubsEdit.apply();
                //update log
                update_status_log_pembayaran(JConst.STATUS_PAYMENT_PENDING,"", orderID);
            }
            //if purchase is refunded or unknown
            else if((sku.equals(JConst.renewSub30DaysId) || sku.equals(JConst.renewSub365DaysId)) && purchase.getPurchaseState() == Purchase.PurchaseState.UNSPECIFIED_STATE)
            {
                Toast.makeText(getApplicationContext(), "Purchase Status Unknown", Toast.LENGTH_SHORT).show();
                update_status_log_pembayaran(JConst.STATUS_PAYMENT_REFUND,"", orderID);
            }
        }
    }


    //handle after success consume / purchase
    ConsumeResponseListener consumeListener = new ConsumeResponseListener() {
        @Override
        public void onConsumeResponse(BillingResult billingResult, @NonNull String purchaseToken) {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                //update new token ke sharepref
                sharedPrefSubsEdit.putString("token", purchaseToken);
                sharedPrefSubsEdit.apply();

                //update ke odoo
                Thread threadOdoo = new Thread(){
                    public void run(){
                        runOnUiThread(new Runnable() {
                            public void run() {
                                renewSub(sharedPrefSubscription.getString("product_id", ""));
                            }
                        });
                    }
                };
                threadOdoo.start();

                //update ke log pembayaran
                Thread threadLog = new Thread(){
                    public void run(){
                        runOnUiThread(new Runnable() {
                            public void run() {
                                update_status_log_pembayaran(JConst.STATUS_PAYMENT_SUCCESS, sku, orderID);
                                delete_log_pembayaran(sharedPrefSubscription.getString("product_id", ""));
                            }
                        });
                    }
                };
                threadLog.start();
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(billingClient!=null){
            billingClient.endConnection();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //untuk mengecek jika ada purchase pending
        boolean isPendingPurchase30 = JApplication.getInstance().sharedPrefSubscription.getString("status_payment_pending_30", "").equals("");
        boolean isPendingPurchase365 = JApplication.getInstance().sharedPrefSubscription.getString("status_payment_pending_365", "").equals("");
        if ((!isPendingPurchase30 || !isPendingPurchase365) && Global.CheckConnectionInternet(thisActivity)) {
            new PurchaseBilling(thisActivity);
        }
        updateUI(ServerNow);

        if (((JApplication) getApplicationContext()).isLogOn) {
        onBackPressed();
        }
    }

    private void save_log_pembayaran(String produk){
        uuid = UUID.randomUUID().toString();    //dibikin ulang setiap save;
        sharedPrefSubsEdit.putString("uuid_log_pembayaran", uuid);
        sharedPrefSubsEdit.apply();
        String param =  "?email="+JApplication.getInstance().loginCompanyModel.getEmail()+
                        "&uuid="+ uuid+
                        "&produk="+ produk+
                        "&company_id="+ JApplication.getInstance().loginCompanyModel.getId()+
                        "&grace_period="+ JApplication.getInstance().loginCompanyModel.getGrace_period_date();
        String url = Routes.url_save_log_pembayaran() + param;
        StringRequest strReq = new StringRequest(Request.Method.POST, url, response -> {
            try {
                String status = new JSONObject(response).getString("status");
                if (status.equals(JConst.STATUS_API_FAILED)) {
                    String pesan = new JSONObject(response).getString("pesan");
                    Toast.makeText(RenewSubscriptionActivity.this, pesan, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "eror json", Toast.LENGTH_SHORT).show();
            }
        }, Throwable::printStackTrace);
        JApplication.getInstance().addToRequestQueue(strReq, JConst.tag_json_obj);
    }

    private void update_status_log_pembayaran(String status_pembayaran, String produk, String order_id){
        String gracePeriod;
        LoginCompanyModel loginCompanyModel = JApplication.getInstance().loginCompanyModel;
        long maxperiod = Global.getMillisDateStrip(loginCompanyModel.getGrace_period_date());
        if (ServerNow == 0) {
            ServerNow = Global.serverNowLong();
        }
        if (ServerNow > maxperiod) {  //sudah dikondisi expired
            gracePeriod = Global.serverNowFormated(ServerNow);
        } else {
            gracePeriod = loginCompanyModel.getGrace_period_date();
        }

        String param =  "?email="+JApplication.getInstance().loginCompanyModel.getEmail()+
                        "&status_pembayaran="+ status_pembayaran+
                        "&uuid="+sharedPrefSubscription.getString("uuid_log_pembayaran", "")+
                        "&produk="+ produk+
                        "&grace_period="+ gracePeriod+
                        "&order_id="+ order_id;
        String url = Routes.url_update_status_log_pembayaran() + param;
        StringRequest strReq = new StringRequest(Request.Method.POST, url, response -> {
            try {
                String status = new JSONObject(response).getString("status");
                if (status.equals(JConst.STATUS_API_FAILED)) {
                    String pesan = new JSONObject(response).getString("pesan");
//                    Toast.makeText(RenewSubscriptionActivity.this, pesan, Toast.LENGTH_SHORT).show();
                    JApplication.getInstance().db.execSQL("INSERT INTO kirim_ulang (url) values ('"+url+"')");
                }
            } catch (JSONException e) {
                e.printStackTrace();
//                Toast.makeText(getApplicationContext(), "eror json", Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            JApplication.getInstance().db.execSQL("INSERT INTO kirim_ulang (url) values ('"+url+"')");
        });
        JApplication.getInstance().addToRequestQueue(strReq, JConst.tag_json_obj);
    }

    private void delete_log_pembayaran(String produk){
        String param = "?email="+JApplication.getInstance().loginCompanyModel.getEmail()+
                "&uuid="+sharedPrefSubscription.getString("uuid_log_pembayaran", "")+
                "&produk="+ produk;
        String url = Routes.url_delete_log_pembayaran() + param;
        StringRequest strReq = new StringRequest(Request.Method.POST, url, response -> {
        }, Throwable::printStackTrace);
        JApplication.getInstance().addToRequestQueue(strReq, JConst.tag_json_obj);
    }

    public void check_email(Runnable runnable) {
        ProgressDialog loading = Global.createProgresSpinner(thisActivity, getString(R.string.loading));
        loading.show();

        String url = Routes.url_cek_email();
        StringRequest strReq = new StringRequest(Request.Method.POST, url, response -> {
            try {
                if (new JSONObject(response).has("error")){
                    loading.dismiss();
                    ShowDialog.infoDialog(thisActivity, getString(R.string.information), "Transaksi tidak dapat dilakukan. Silahkan kirim email ke admin@pasienqu.com");
                }else {
                    loading.dismiss();
                    runnable.run();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                loading.dismiss();
            }
        }, error -> {
            loading.dismiss();
        })
        {
            @Override
            public byte[] getBody() throws AuthFailureError {
                final JSONObject body = new JSONObject();
                try {
                    body.put("email", JApplication.getInstance().loginCompanyModel.getEmail());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String strbody = "{\"params\": "+body.toString()+"}";
                return strbody.getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type","application/json");
                return headers;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                JApplication.getInstance().checkSessionCookie(response.headers);
                return super.parseNetworkResponse(response);
            }
        };
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }

}