package com.orion.pasienqu_2.globals;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
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
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.common.collect.ImmutableList;
import com.instacart.library.truetime.TrueTimeRx;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.Routes;
import com.orion.pasienqu_2.activities.home.home;
import com.orion.pasienqu_2.activities.more.renew_subscriotion.Security;
import com.orion.pasienqu_2.models.LoginCompanyModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import io.reactivex.schedulers.Schedulers;

public class PurchaseBilling implements PurchasesUpdatedListener {

    private long ServerNow;
    private BillingClient billingClient;
    private final SharedPreferences sharedPrefSubscription = JApplication.getInstance().sharedPrefSubscription;
    private final SharedPreferences.Editor sharedPrefSubsEdit = JApplication.getInstance().sharedPrefSubscriptionEdit;
    private Activity activity;
    private ProductDetails productDetails;
    private String uuid = UUID.randomUUID().toString();
    private Runnable runSuccess;
    private String TAG = "RenewSubs";
    private String sku;
    private String orderID;

    public PurchaseBilling(Activity activity) {
        // Inisialisasi BillingClient
        this.activity = activity;
        this.runSuccess = runSuccess;
        TrueTimeRx.build()
                .initializeRx("time.google.com")
                .subscribeOn(Schedulers.io())
                .subscribe(date -> {
                    ServerNow = TrueTimeRx.now().getTime();
                });

        billingClient = BillingClient.newBuilder(activity)
                .enablePendingPurchases()
                .setListener(this)
                .build();

        // Menghubungkan dengan Google Play
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // BillingClient terhubung ke Google Play
                    // Lakukan pengecekan pembelian tertunda
                    checkPendingPurchases();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Koneksi ke Google Play terputus
            }
        });

    }

    public void checkPendingPurchases() {
        billingClient.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP).build(),
                new PurchasesResponseListener() {
                    public void onQueryPurchasesResponse(@NonNull BillingResult billingResult, @NonNull List<Purchase> purchases) {
                        for (Purchase purchase : purchases) {
                            int purchaseState = purchase.getPurchaseState();
                            if (purchaseState == Purchase.PurchaseState.PURCHASED) {
                                // Pembelian berhasil
                                handlePurchase(purchases);
                                Log.i(TAG, "PURCHASED");
                            } else if (purchaseState == Purchase.PurchaseState.PENDING) {
                                // Pembelian tertunda
                                Log.i(TAG, "PENDING");
                            } else if (purchaseState == Purchase.PurchaseState.UNSPECIFIED_STATE) {
                                // Pembelian dibatalkan oleh pengguna
                                Log.i(TAG, "UNSPECIFIED_STATE");
                            } else {
                                // Status pembelian tidak diketahui
                                Log.i(TAG, "kaga jelas");
                            }
                        }
                    }
                });
    }



    @Override
    public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
        orderID = "";
        if (purchases != null) {
            orderID = purchases.get(0).getOrderId();
        }
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (Purchase purchase : purchases) {
                if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                    // Pembelian berhasil
                    handlePurchase(purchases);
                    Log.i(TAG, "PURCHASED");
                } else if (purchase.getPurchaseState() == Purchase.PurchaseState.PENDING) {
                    // Pembelian tertunda
                    Log.i(TAG, "PENDING");
                } else if (purchase.getPurchaseState() == Purchase.PurchaseState.UNSPECIFIED_STATE) {
                    // Pembelian dibatalkan oleh pengguna
                    Log.i(TAG, "UNSPECIFIED_STATE");
                } else {
                    // Status pembelian tidak diketahui
                    Log.i(TAG, "kaga jelas");
                }
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            billingClient.queryPurchasesAsync(
                    QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP).build(),
                    new PurchasesResponseListener() {
                        public void onQueryPurchasesResponse(
                                @NonNull BillingResult billingResult,
                                @NonNull List<Purchase> purchases) {
                            // Process the result
                            if (purchases.size() > 0) {
                                handlePurchase(purchases);
                            }
                        }
                    });
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Pembelian dibatalkan oleh pengguna
//            update_status_log_pembayaran(JConst.STATUS_PAYMENT_CANCELED,"", orderID);
        } else {
            // Terjadi kesalahan saat memproses pembelian
            Log.i(TAG, billingResult.getDebugMessage());
        }
    }

    public void initiatePurchase(String productID) {
        save_log_pembayaran(productID);
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
                                billingClient.launchBillingFlow(activity, billingFlowParams);
                            }
                            else{
                                //try to add item/product id "consumable" inside managed product in google play console
                                Log.i(TAG, "Purchase Item not Found");

                            }
                        } else {
                            Log.i(TAG, billingResult.getDebugMessage());
                        }

                    }
                }
        );
    }


    public boolean verifyValidSignature(String signedData, String signature) {
        try {
            //To get key go to Developer Console > Select your app > Monetize > Monetization setup
            String base64Key = JConst.base64DeveloperKey;
            return Security.verifyPurchase(base64Key, signedData, signature);
        } catch (IOException e) {
            return false;
        }
    }

    public void handlePurchase(List<Purchase>  purchases) {
        for(Purchase purchase:purchases) {
            sku = purchase.getSkus().get(0);
            orderID = purchase.getOrderId();
            //store to pref
            sharedPrefSubsEdit.putString("product_id", sku);
            sharedPrefSubsEdit.apply();
            //if item is purchased
            if ((sku.equals(JConst.renewSub30DaysId) || sku.equals(JConst.renewSub365DaysId)) && purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED)
            {
                if (!verifyValidSignature(purchase.getOriginalJson(), purchase.getSignature())) {
                    // Invalid purchase >> show error to user
                    Log.i(TAG, "Invalid Purchase");
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
                Log.i(TAG, "Purchase is Pending. Please complete Transaction");
                update_status_log_pembayaran(JConst.STATUS_PAYMENT_PENDING, "", orderID);
            }
            //if purchase is refunded or unknown
            else if((sku.equals(JConst.renewSub30DaysId) || sku.equals(JConst.renewSub365DaysId)) && purchase.getPurchaseState() == Purchase.PurchaseState.UNSPECIFIED_STATE)
            {
                Log.i(TAG, "Purchase Status Unknown");
                update_status_log_pembayaran(JConst.STATUS_PAYMENT_REFUND, "", orderID);
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
                if(sku.equals(JConst.renewSub30DaysId)){
                    sharedPrefSubsEdit.putString("status_payment_pending_30", "");
                }else{
                    sharedPrefSubsEdit.putString("status_payment_pending_365", "");
                }
                sharedPrefSubsEdit.apply();

                //update ke odoo
                renewSub(sharedPrefSubscription.getString("product_id", ""));

                //update ke mysql log_pembayaran
                update_status_log_pembayaran(JConst.STATUS_PAYMENT_SUCCESS, sku, orderID);
            }
        }
    };

    public void subscribe(View view, String productID) {
        //check if service is already connected
        if (billingClient.isReady()) {
            initiatePurchase(productID);
        }
        //else reconnect service
        else{
            billingClient.startConnection(new BillingClientStateListener() {
                @Override
                public void onBillingSetupFinished(BillingResult billingResult) {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        initiatePurchase(productID);
                    } else {
                        Log.i(TAG, billingResult.getDebugMessage());
                    }
                }
                @Override
                public void onBillingServiceDisconnected() {
                    Log.i(TAG, "Service Disconnected");
                }
            });
        }
    }

    public void save_log_pembayaran(String produk){
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
//                    Toast.makeText(activity, pesan, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.i(TAG, e.getMessage());
            }
        }, Throwable::printStackTrace);
        JApplication.getInstance().addToRequestQueue(strReq, JConst.tag_json_obj);
    }

    public void update_status_log_pembayaran(String status_pembayaran, String produk, String order_id){
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
//                    Toast.makeText(activity, pesan, Toast.LENGTH_SHORT).show();
                    JApplication.getInstance().db.execSQL("INSERT INTO kirim_ulang (url) values ('"+url+"')");
                }
            } catch (JSONException e) {
                e.printStackTrace();
//                Toast.makeText(activity, "eror json", Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            JApplication.getInstance().db.execSQL("INSERT INTO kirim_ulang (url) values ('"+url+"')");
        });
        JApplication.getInstance().addToRequestQueue(strReq, JConst.tag_json_obj);
    }

    public void delete_log_pembayaran(String produk){
        String param = "?email="+JApplication.getInstance().loginCompanyModel.getEmail()+
                       "&uuid="+sharedPrefSubscription.getString("uuid_log_pembayaran", "")+
                        "&produk="+ produk;
        String url = Routes.url_delete_log_pembayaran() + param;
        StringRequest strReq = new StringRequest(Request.Method.POST, url, response -> {
            try {
                String status = new JSONObject(response).getString("status");
                if (status.equals(JConst.STATUS_API_FAILED)) {
                    String pesan = new JSONObject(response).getString("pesan");
//                    Toast.makeText(activity, pesan, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.i(TAG, e.getMessage());
            }
        }, Throwable::printStackTrace);
        JApplication.getInstance().addToRequestQueue(strReq, JConst.tag_json_obj);
    }

    public void renewSub(String productIDCode){
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
                loginCompanyModel.setSubsription_date(Global.serverNowFormated(ServerNow));
            } else {
                loginCompanyModel.setSubsription_date(loginCompanyModel.getGrace_period_date());
            }

            if (productIDCode.equals(JConst.renewSub30DaysId)) {
                loginCompanyModel.setBilling_period("day30");
            } else if (productIDCode.equals(JConst.renewSub365DaysId)) {
                loginCompanyModel.setBilling_period("day365");
            }

            //update data subs ke odoo
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Runnable runnable2 = new Runnable() {
                        @Override
                        public void run() {
                            //jika sudah selesai update ke odoo maka hapus yg di sharepref
                            //hal ini fungsinya nanti jika product_id <> '' yg artinya gagal perpanjang masa aktif maka akan di proses lagi pembelianya
                            sharedPrefSubsEdit.putString("product_id", "");
                            sharedPrefSubsEdit.apply();

                            Runnable runHome = new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(activity, home.class);
//                            intent.putExtra("purchase_expired", isAlreadyExpired); //jika sudah expired lalu melakukan purchase, kirim extra utk pengecekan show dialog
                                    activity.startActivity(intent);
                                }
                            };
                            ShowDialog.infoDialogWithRunnable(activity, "PasienQu", "Payment Success", runHome);
                        }
                    };
//                    GlobalOdoo.get_company(activity, activity, runnable2);
                    GlobalMySQL.get_company(activity, activity, runnable2);
                }
            };
//            GlobalOdoo.newRenewSubscriptionNoLoading(activity, loginCompanyModel, runnable);
            GlobalMySQL.newRenewSubscriptionNoLoading(activity, loginCompanyModel, runnable);
        }
    }

}
