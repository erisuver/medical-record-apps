package com.orion.pasienqu_2.globals;
/*
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.models.LoginModel;
import com.orion.pasienqu_2.models.SyncInfoModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import oogbox.api.odoo.OdooClient;
import oogbox.api.odoo.OdooUser;
import oogbox.api.odoo.client.helper.OdooErrorException;
import oogbox.api.odoo.client.helper.data.OdooRecord;
import oogbox.api.odoo.client.helper.data.OdooResult;
import oogbox.api.odoo.client.helper.utils.ODomain;
import oogbox.api.odoo.client.helper.utils.OdooFields;
import oogbox.api.odoo.client.helper.utils.OdooValues;
import oogbox.api.odoo.client.listeners.AuthenticateListener;
import oogbox.api.odoo.client.listeners.IOdooResponse;

public class OdooConnectionOld {
    private OdooClient client;
    private OdooUser odooUser;
    private Context context;
    private OdooValues values;
    private String uuid = "";
    private String expense_ids = "";

    public OdooConnectionOld(Context context) {
        client = new OdooClient.Builder(context)
                .setHost(JConst.HOST_SERVER)
                .build();
    }

    public OdooConnectionOld(Context context, String session) {
        client = new OdooClient.Builder(context)
                .setHost(JConst.HOST_SERVER)
                .setSession(session)
                .build();
    }

    public OdooClient getClient() {
        return client;
    }

    public void setClient(OdooClient client) {
        this.client = client;
    }

    public OdooUser getOdooUser() {
        return odooUser;
    }

    public void setOdooUser(OdooUser odooUser) {
        this.odooUser = odooUser;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void login(String email, String password, AuthenticateListener loginCallback){
        if (client.isConnected()) {
            client.authenticate(email, password, JConst.DB_SERVER_NAME, loginCallback);
        }
    }



    public void download(Context appContext){
//        ODomain domain = new ODomain();
//        LoginModel loginModel = ((JApplication) appContext).getDtLoginGlobal();
//        domain.add("contract_id","=", loginModel.getContract_id());
//        OdooFields fields = new OdooFields();
//
//        fields.addAll("id", "uuid", "__last_update", "create_date");
//        int offset = 0;
//        int limit = 80;
//
//        String sorting = "id asc";
//        client.searchRead("foms.daily.expense.proof", domain, fields, offset, limit, sorting, new IOdooResponse() {
//            @SuppressLint("LongLogTag")
//            @Override
//            public void onResult(OdooResult result) {
//                Log.d("hasil foms.daily.expense.proof", result.toString());
//            }
//        });
    }

    public void loadExpenseProof(Context appContext, Activity activity, Runnable runnableSuccess){
        boolean connected = Global.CheckConnectionInternet(appContext);
        if (!connected){
            runnableSuccess.run();
            return;
        }
        long lastSyncDown = ((JApplication)appContext).syncDownTable.getLastUpdate("foms.daily.expense.proof");
        ProgressDialog loading = Global.showProgressDialog(appContext, activity);
        ((JApplication) appContext).isSyncExpenseProof = true;
        ODomain domain = new ODomain();
        LoginModel loginModel = ((JApplication) appContext).getDtLoginGlobal();
//        domain.add("contract_id", "=", loginModel.getContract_id());
        domain.add("write_date", ">=", Global.getDateTimeFormatedOdoo(lastSyncDown));
        OdooFields fields = new OdooFields();
        fields.addAll("id", "contract_id", "driver_id", "vehicle_id", "proof_date", "proof_file", "notes", "uuid", "write_date");
        int offset = 0;
        int limit = 80;
        String sorting = "";
        client.searchRead("foms.daily.expense.proof", domain, fields, offset, limit, sorting, new IOdooResponse() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResult(OdooResult result) {
                OdooRecord[] records = result.getRecords();
//                ExpenseProofModel expenseProofModel = new ExpenseProofModel();
//                ExpenseProofTable expenseProofTable = ((JApplication) appContext).expenseProofTable;
//                SyncDownTable syncDownTable = ((JApplication) appContext).syncDownTable;
//                SyncDownModel syncDownModel = ((JApplication) appContext).syncDownTable.getRecord("foms.daily.expense.proof");
//
//                long tempLastUpdate = 0;
//                for (OdooRecord record : records) {
//                    Log.d("hasil foms.daily.expense.proof", result.toString());
//                    expenseProofModel.setId(record.getInt("id"));
//                    expenseProofModel.setDriver_id(get_id_by_object_odoo(record.getArray("driver_id")));
//                    expenseProofModel.setContract_id(get_id_by_object_odoo(record.getArray("contract_id")));
//                    expenseProofModel.setVehicle_id(get_id_by_object_odoo(record.getArray("vehicle_id")));
//                    expenseProofModel.setProof_date(Global.getMillisDateFmtFromOdoo(record.getString("proof_date"), "yyyy-MM-dd"));
////                        expenseProofModel.setProof_file(record.getString("proof_file").getBytes());
//                    expenseProofModel.setProof_file(record.getString("proof_file"));
//                    expenseProofModel.setNotes(record_to_string(record.getString("notes")));
//                    expenseProofModel.setUuid(record.getString("uuid"));
//
//                    expenseProofTable.delete(expenseProofModel.getUuid());
//                    expenseProofTable.insert(expenseProofModel, false);
//                    long lastUpdate = Global.getMillisDateFmtFromOdoo(record.getString("write_date"), "yyyy-MM-dd HH:mm:ss");
//                    if (lastUpdate > tempLastUpdate) {
//                        syncDownModel.setLast_update(lastUpdate);
//                        syncDownModel.setModel("foms.daily.expense.proof");
//                        syncDownTable.delete(syncDownModel.getModel());
//                        syncDownTable.insert(syncDownModel);
//                        tempLastUpdate = lastUpdate;
//                    }
//                }
                ((JApplication) appContext).isSyncExpenseProof = false;
                runnableSuccess.run();
                loading.dismiss();
            }


            @Override
            public boolean onError(OdooErrorException error) {
                ((JApplication) appContext).isSyncExpenseProof = false;
                runnableSuccess.run();
                loading.dismiss();
                return super.onError(error);
            }
        });
    }


    public void loadDailyTripLog(Context appContext, Activity activity,  Runnable runnableSuccess){
            boolean connected = Global.CheckConnectionInternet(appContext);
            if (!connected){
                runnableSuccess.run();
                return;
            }
            long lastSyncDown = ((JApplication)appContext).syncDownTable.getLastUpdate("foms.daily.trip.log");
            ProgressDialog loading = Global.showProgressDialog(appContext, activity);
            ((JApplication) appContext).isSyncDailyTripLog = true;
            ODomain domain = new ODomain();
            LoginModel loginModel = ((JApplication) appContext).getDtLoginGlobal();
//            domain.add("contract_id", "=", loginModel.getContract_id());
//        1621221382000
            domain.add("write_date", ">", Global.getDateTimeFormatedOdoo(lastSyncDown));
            OdooFields fields = new OdooFields();
            fields.addAll("id", "contract_id", "driver_id", "vehicle_id", "start_date", "end_date", "input_source",
                          "location", "passenger_name", "start_odometer", "finish_odometer", "trip_type",
                          "trip_notes", "state", "total_expense", "confirm_date", "confirm_by", "cost_center_id",
                          "uuid", "write_date", "expense_ids");
            int offset = 0;
            int limit = 80;
            String sorting = "";
            client.searchRead("foms.daily.trip.log", domain, fields, offset, limit, sorting, new IOdooResponse() {
                @SuppressLint("LongLogTag")
                @Override
                public void onResult(OdooResult result) {
                    OdooRecord[] records = result.getRecords();
//                    DailyTripLogModel dailyTripLogModel = new DailyTripLogModel();
//                    DailyTripLogTable dailyTripLogTable = ((JApplication) appContext).dailyTripLogTable;
//                    SyncDownTable syncDownTable = ((JApplication) appContext).syncDownTable;
//                    SyncDownModel syncDownModel = ((JApplication) appContext).syncDownTable.getRecord("foms.daily.trip.log");
//
//                    long tempLastUpdate = 0;
//                    for (OdooRecord record : records) {
//                        Log.d("hasil foms_daily trip log", result.toString());
////                        fields.addAll("id", "contract_id", "driver_id.id", "vehicle_id.id", "start_date", "end_date", "input_source"
////                                "uuid");
//                        dailyTripLogModel.setId(record.getInt("id"));
//                        dailyTripLogModel.setDriver_id(get_id_by_object_odoo(record.getArray("driver_id")));
//                        dailyTripLogModel.setContract_id(get_id_by_object_odoo(record.getArray("contract_id")));
//                        dailyTripLogModel.setVehicle_id(get_id_by_object_odoo(record.getArray("vehicle_id")));
//                        dailyTripLogModel.setStart_date(Global.getMillisDateFmtFromOdoo(record.getString("start_date"), "yyyy-MM-dd HH:mm:ss"));
//                        dailyTripLogModel.setEnd_date(Global.getMillisDateFmtFromOdoo(record.getString("end_date"), "yyyy-MM-dd HH:mm:ss"));
//                        dailyTripLogModel.setInput_source(record.getString("input_source"));
//                        dailyTripLogModel.setLocation(record_to_string(record.getString("location")));
//                        dailyTripLogModel.setPassenger_name(record_to_string(record.getString("passenger_name")));
//                        dailyTripLogModel.setStart_odometer(record.getInt("start_odometer"));
//                        dailyTripLogModel.setFinish_odometer(record.getInt("finish_odometer"));
//                        dailyTripLogModel.setTrip_type(record_to_string(record.getString("trip_type")));
//                        dailyTripLogModel.setTrip_notes(record_to_string(record.getString("trip_notes")));
//                        dailyTripLogModel.setState(record.getString("state"));
//                        dailyTripLogModel.setTotal_expense(record.getInt("total_expense"));
//                        dailyTripLogModel.setConfirm_date(Global.getMillisDateFmtFromOdoo(record.getString("confirm_date"), "yyyy-MM-dd HH:mm:ss"));
//                        dailyTripLogModel.setConfirm_by(get_id_by_object_odoo(record.getArray("cost_center_id")));//record.getString("confirm_by"));
//                        dailyTripLogModel.setCost_center_id(get_id_by_object_odoo(record.getArray("cost_center_id")));
//                        dailyTripLogModel.setUuid(record.getString("uuid"));
//
//                        dailyTripLogTable.delete(dailyTripLogModel.getUuid());
//                        dailyTripLogTable.insert(dailyTripLogModel, false);
//                        long lastUpdate = Global.getMillisDateFmtFromOdoo(record.getString("write_date"), "yyyy-MM-dd HH:mm:ss");
//                        if (lastUpdate > tempLastUpdate) {
//                            syncDownModel.setLast_update(lastUpdate);
//                            syncDownModel.setModel("foms.daily.trip.log");
//                            syncDownTable.delete(syncDownModel.getModel());
//                            syncDownTable.insert(syncDownModel);
//                            tempLastUpdate = lastUpdate;
//                        }
//                        loadDailyTripExpense(appContext, record.getInt("id"), record.getString("uuid"));
//                    }
                    ((JApplication) appContext).isSyncDailyTripLog = false;
                    runnableSuccess.run();
                    loading.dismiss();
                }


                @Override
                public boolean onError(OdooErrorException error) {
                    ((JApplication) appContext).isSyncDailyTripLog = false;
                    runnableSuccess.run();
                    loading.dismiss();
                    return super.onError(error);
                }
            });
    }

    public void loadDailyTripExpense(Context appContext, int trip_log_id, final String trip_log_uuid){
        ODomain domain = new ODomain();
        domain.add("trip_log_id", "=", trip_log_id);
        OdooFields fields = new OdooFields();
        fields.addAll("id", "trip_log_id", "driver_id", "product_id", "amount", "notes", "contract_id", "cost_center_id", "uuid");
        int offset = 0;
        int limit = 80;
        String sorting = "";
        client.searchRead("foms.daily.trip.expense", domain, fields, offset, limit, sorting, new IOdooResponse() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResult(OdooResult result) {
                OdooRecord[] records = result.getRecords();
//                DailyTripExpenseModel dailyTripExpenseModel = new DailyTripExpenseModel();
//                DailyTripExpenseTable dailyTripExpenseTable = ((JApplication) appContext).dailyTripExpenseTable;
//                dailyTripExpenseTable.delete(trip_log_uuid);
//
//                for (OdooRecord record : records) {
//                    Log.d("hasil foms_daily trip log", result.toString());
//                    dailyTripExpenseModel.setCost_center_id(get_id_by_object_odoo(record.getArray("cost_center_id")));
//                    dailyTripExpenseModel.setContract_id(get_id_by_object_odoo(record.getArray("contract_id")));
//                    dailyTripExpenseModel.setTrip_log_id(get_id_by_object_odoo(record.getArray("trip_log_id")));
//                    dailyTripExpenseModel.setProduct_id(get_id_by_object_odoo(record.getArray("product_id")));
//                    dailyTripExpenseModel.setAmount(record.getInt("amount"));
//                    dailyTripExpenseModel.setNotes(record_to_string(record.getString("notes")));
//                    dailyTripExpenseModel.setUuid(record.getString("uuid"));
//                    dailyTripExpenseModel.setTrip_log_uuid(trip_log_uuid);
//                    dailyTripExpenseTable.insert(dailyTripExpenseModel);
//                }
                ((JApplication) appContext).isSyncDailyTripLog = false;
            }


            @Override
            public boolean onError(OdooErrorException error) {
                ((JApplication) appContext).isSyncDailyTripLog = false;
                return super.onError(error);
            }
        });
    }

    public void loadProduct(Context appContext, Activity activity, ProgressDialog progressDialog, Runnable runnable){
        ODomain domain = new ODomain();
        domain.add("hr_expense_ok","=", true);

        OdooFields fields = new OdooFields();
        fields.addAll("id", "name");

        int offset = 0;
        int limit = 80;

        String sorting = "name DESC";

        client.searchRead("product.product", domain, fields, offset, limit, sorting, new IOdooResponse() {
            @Override
            public void onResult(OdooResult result) {
                OdooRecord[] records = result.getRecords();
//                ProductModel productModel = new ProductModel();
//                ProductTable productTable = ((JApplication)appContext).productTable;
//                productTable.deleteAll();
//                for (OdooRecord record : records) {
//                    productModel.setId(record.getInt("id"));
//                    productModel.setName(record.getString("name"));
//                    productTable.insert(productModel);
//                }
                runnable.run();
                progressDialog.dismiss();
            }
        });
    }

//
//    public void loadDailyTripCostCenter(Context appContext){
////        loadLoginInformation(appContext);
//    }

    public void loadLoginInformation(Context appContext, Activity activity, String userid, Runnable runnableSuccess){
        List<Integer> ids = Arrays.asList(1, 2, 3);
        List<String> fields = Arrays.asList("id", "name");

        client.read("res.users", ids, fields, new IOdooResponse() {
            @Override
            public boolean onError(OdooErrorException error) {
                Log.d("Hasil", error.toString());
                return false;
            }
            @Override
            public void onResult(OdooResult result) {
                OdooRecord[] records = result.getRecords();

                for(OdooRecord record: records) {
                    Log.v("Name:", record.getString("name"));
                }
            }
        });
//        client.connect();
//
//        ODomain domain = new ODomain();
//        domain.add("id","=", 0);
//
//        OdooFields fields = new OdooFields();
//        fields.addAll("id", "name");
//
//        int offset = 0;
//        int limit = 80;
//
//        String sorting = "name DESC";
//
//        client.searchRead("product.product", domain, fields, offset, limit, sorting, new IOdooResponse() {
//
//            @Override
//            public boolean onError(OdooErrorException error) {
//                Log.d("Hasil", error.toString());
//                return false;
//            }
//            @Override
//            public void onResult(OdooResult result) {
//                OdooRecord[] records = result.getRecords();
////                ProductModel productModel = new ProductModel();
////                ProductTable productTable = ((JApplication)appContext).productTable;
////                productTable.deleteAll();
////                for (OdooRecord record : records) {
////                    productModel.setId(record.getInt("id"));
////                    productModel.setName(record.getString("name"));
////                    productTable.insert(productModel);
////                }
////                runnable.run();
////                progressDialog.dismiss();
//            }
//
//        });

//        boolean connected = Global.CheckConnectionInternet(appContext);
//        if (!connected){
//            runnableSuccess.run();
//            return;
//        }
//        OArguments arguments = new OArguments();
//
////        ProgressDialog loading = Global.showProgressDialog(appContext, activity);
//
//
//        OdooFields fields = new OdooFields();
////        fields.addAll("login", "name", "email", "groups_id", "use_pin", "pin_protection", "company_id", "is_subuser", "initial_change_pass");
//        fields.addAll("uid");
//        int offset = 0;
//        int limit = 80;
//        String sorting = "";
//
//        ODomain domain = new ODomain();
//        LoginModel loginModel = ((JApplication) appContext).getDtLoginGlobal();
//        domain.add("uid","=", odooUser.uid);
//
//
//        client.searchRead("res.users", domain, fields, offset, limit, sorting, new IOdooResponse() {
//            @SuppressLint("LongLogTag")
//
//
//            @Override
//            public boolean onError(OdooErrorException error) {
//                Log.d("Hasil", error.toString());
//                return false;
//            }
//
//            @Override
//            public void onResult(OdooResult result) {
//                OdooRecord[] records = result.getRecords();
//                Toast.makeText(activity, String.valueOf(records.length), Toast.LENGTH_SHORT).show();
////                ExpenseProofModel expenseProofModel = new ExpenseProofModel();
////                ExpenseProofTable expenseProofTable = ((JApplication) appContext).expenseProofTable;
////                SyncDownTable syncDownTable = ((JApplication) appContext).syncDownTable;
////                SyncDownModel syncDownModel = ((JApplication) appContext).syncDownTable.getRecord("foms.daily.expense.proof");
////
////                long tempLastUpdate = 0;
////                for (OdooRecord record : records) {
////                    Log.d("hasil foms.daily.expense.proof", result.toString());
////                    expenseProofModel.setId(record.getInt("id"));
////                    expenseProofModel.setDriver_id(get_id_by_object_odoo(record.getArray("driver_id")));
////                    expenseProofModel.setContract_id(get_id_by_object_odoo(record.getArray("contract_id")));
////                    expenseProofModel.setVehicle_id(get_id_by_object_odoo(record.getArray("vehicle_id")));
////                    expenseProofModel.setProof_date(Global.getMillisDateFmtFromOdoo(record.getString("proof_date"), "yyyy-MM-dd"));
//////                        expenseProofModel.setProof_file(record.getString("proof_file").getBytes());
////                    expenseProofModel.setProof_file(record.getString("proof_file"));
////                    expenseProofModel.setNotes(record_to_string(record.getString("notes")));
////                    expenseProofModel.setUuid(record.getString("uuid"));
////
////                    expenseProofTable.delete(expenseProofModel.getUuid());
////                    expenseProofTable.insert(expenseProofModel, false);
////                    long lastUpdate = Global.getMillisDateFmtFromOdoo(record.getString("write_date"), "yyyy-MM-dd HH:mm:ss");
////                    if (lastUpdate > tempLastUpdate) {
////                        syncDownModel.setLast_update(lastUpdate);
////                        syncDownModel.setModel("foms.daily.expense.proof");
////                        syncDownTable.delete(syncDownModel.getModael());
////                        syncDownTable.insert(syncDownModel);
////                        tempLastUpdate = lastUpdate;
////                    }
////                }
////                ((JApplication) appContext).isSyncExpenseProof = false;
////                runnableSuccess.run();
////                loading.dismiss();
//            }
//
//
////        client.call_kw("res.users", "get_contract_info", arguments, new IOdooResponse() {
////            @Override
////            public void onResult(OdooResult result) {
////                Log.d("Login information", result.toString());
////                LoginTable loginTable = ((JApplication) appContext).loginTable;
//////                DailyTripCostCenterTable dailyTripCostCenterTable = ((JApplication) appContext).dailyTripCostCenterTable;
//////
//////                List<WorkingTimeModel> workingtimesList = new ArrayList <WorkingTimeModel>();
//////                LoginModel loginModel = new LoginModel();
//////                DailyTripCostCenterModel dailyTripCostCenterModel = new DailyTripCostCenterModel();
//////                dailyTripCostCenterTable.deleteAll();
//////
//////                loginTable.deleteAll();
//////                loginModel.setUser_id(userid);
//////                loginModel.setContract_id(result.getInt("contract_id"));
//////                loginModel.setContract_no(result.getString("contract_no"));
//////                loginModel.setCust_name(result.getString("customer_name"));
//////                loginModel.setDriver_id(result.getInt("driver_id"));
//////                loginModel.setEnd_date(Global.getMillisDateFmtFromOdoo(result.getString("end_date"), "yyyy-MM-dd"));
//////                loginModel.setStart_date(Global.getMillisDateFmtFromOdoo(result.getString("start_date"), "yyyy-MM-dd"));
//////                loginModel.setVehicle_id(result.getInt("vehicle_id"));
//////
//////                loginModel.setOdooUser(((JApplication)appContext).odooConnection.odooUser);
//////
//////                try {
//////                    JSONObject jsonObjectCostCenter = new JSONObject("{\"cost_centers\":"+result.getString("cost_centers")+"}");
//////                    JSONArray jsonArrayCostCenter = jsonObjectCostCenter.getJSONArray("cost_centers");
//////                    for (int i = 0; i < jsonArrayCostCenter.length(); i++) {
//////                        JSONObject objCostCenter = jsonArrayCostCenter.getJSONObject(i);
//////                        dailyTripCostCenterModel.setId(objCostCenter.getInt("id"));
//////                        dailyTripCostCenterModel.setPin(objCostCenter.getString("pin"));
//////                        dailyTripCostCenterModel.setName(objCostCenter.getString("name"));
//////                        dailyTripCostCenterTable.insert(dailyTripCostCenterModel);
//////                    }
//////                } catch (JSONException e) {
//////                    e.printStackTrace();
//////                }
//////
//////
//////                try {
//////                    JSONObject jsonObjectWorkingTime = new JSONObject("{\"working_time\":"+result.getString("working_time")+"}");
//////                    JSONArray jsonArrayWorkingTime = jsonObjectWorkingTime.getJSONArray("working_time");
//////                    for (int i = 0; i < jsonArrayWorkingTime.length(); i++) {
//////                        WorkingTimeModel workingTimeModel = new WorkingTimeModel();
//////                        JSONObject objWorkingTime = jsonArrayWorkingTime.getJSONObject(i);
//////                        workingTimeModel.setDayofweek(objWorkingTime.getInt("dayofweek"));
//////                        workingTimeModel.setHour_from(objWorkingTime.getDouble("hour_from"));
//////                        workingTimeModel.setHour_to(objWorkingTime.getDouble("hour_to"));
//////                        workingTimeModel.setName(objWorkingTime.getString("name"));
//////                        workingtimesList.add(workingTimeModel);
//////                    }
//////                } catch (JSONException e) {
//////                    e.printStackTrace();
//////                }
//////                loginModel.setWorking_times(workingtimesList);
//////                loginTable.insert(loginModel);
//////                ((JApplication) appContext).setDtLoginGlobal(loginModel);
//////                loadProduct(appContext, activity, loading, runnableSuccess);
////                runnableSuccess.run();
////            }
//        });


    }


    public void upload(SyncInfoModel syncInfoModel, Context appContext){
        if (syncInfoModel.getCommand().equals("create")){
            this.create(syncInfoModel, appContext);
        }
        if (syncInfoModel.getCommand().equals("write")){
            this.write(syncInfoModel, appContext);
        }
    }

    private OdooValues getOdooValues(SyncInfoModel syncInfoModel){
        OdooValues values = new OdooValues();
        byte[] blob = syncInfoModel.getContent();
        String value = new String(blob);

        try {
            JSONObject jsonObject = new JSONObject(value);
            JSONArray keys = jsonObject.names();
            for (int i = 0; i < keys.length (); i++) {
                String key = keys.getString (i);
                if (key.contains("date")){
                    Object object = jsonObject.get(key);
                    String valueObject = object.toString();
                    if (key.equals("start_date") || key.equals("end_date") || key.equals("confirm_date")) {
                        if (Long.valueOf(valueObject) == 0) {
                            values.put(key, false);
                        }else{
                            String date = Global.getDateTimeFormatedOdoo(Long.valueOf(valueObject));
                            values.put(key, date);
                        }
                    }else{
                        String date = Global.getDateFormatedOdoo(Long.valueOf(valueObject));
                        values.put(key, date);
                    }
                }else if (key.equals("expense_ids")){
                    expense_ids = jsonObject.getString("expense_ids");
                }else{
                    Object object = jsonObject.get(key);
                    values.put(key, object);
                    if (key.equals("uuid")){
                        uuid = jsonObject.getString(key);
                    }
                }
            }
        } catch (JSONException e) {
            Log.d("gagal", e.getMessage().toString());
        } catch (Exception e) {
            Log.d("gagal", e.getMessage().toString());
        }
        return values;
    }

    public void create(SyncInfoModel syncInfoModel, Context appContext){
        this.expense_ids = "";
        this.uuid = "";
        OdooValues values = getOdooValues(syncInfoModel);
        final String expense_ids_local = this.expense_ids;
        final String uuidLocal = this.uuid;

        client.create(syncInfoModel.getModel(), values, new IOdooResponse() {
            @Override
            public void onResult(OdooResult result) {
                Log.d("Hasil", result.toString());
                if (!expense_ids.equals("") && !expense_ids.equals("[]")){
                    create_expense_ids(expense_ids_local, uuidLocal, appContext);
                }
                ((JApplication)appContext).syncInfoTable.delete(syncInfoModel.getId());
            }
        });
    }

    public void create_expense_ids(String expense_ids_local, String uuidLocal, Context appContext){
        ODomain domain = new ODomain();
        domain.add("uuid","=", uuidLocal);
        OdooFields fields = new OdooFields();
        fields.addAll("id");
        int offset = 0;
        int limit = 1;
        String sorting = "id";
        String model = "foms.daily.trip.log";
        client.searchRead(model, domain, fields, offset, limit, sorting, new IOdooResponse() {
            @Override
            public void onResult(OdooResult result) {
                OdooRecord[] records = result.getRecords();
                int id = 0;
                for (OdooRecord record : records) {
                    id = record.getInt("id");
                }
                try {
                    JSONObject jsonObjectCostCenter = new JSONObject("{\"expense_ids\":"+expense_ids_local+"}");
                    JSONArray jsonArrayCostCenter = jsonObjectCostCenter.getJSONArray("expense_ids");

                    OdooValues values = new OdooValues();
                    for (int i = 0; i < jsonArrayCostCenter.length(); i++) {
                        JSONObject objCostCenter = jsonArrayCostCenter.getJSONObject(i);
                        values.put("contract_id", objCostCenter.getInt("contract_id"));
                        values.put("trip_log_id", id);
                        values.put("product_id", objCostCenter.getInt("product_id"));
                        values.put("amount", objCostCenter.getDouble("amount"));
                        values.put("notes", objCostCenter.getString("notes"));
                        values.put("cost_center_id", objCostCenter.getInt("cost_center_id"));
                        values.put("uuid", objCostCenter.getString("uuid"));
                    }

                    client.create("foms.daily.trip.expense", values, new IOdooResponse() {
                        @Override
                        public void onResult(OdooResult result) {
                            Log.d("Hasil detail", result.toString());
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void write_expense_ids(String expense_ids_local, String uuidLocal, Context appContext){
        ODomain domain = new ODomain();
        domain.add("uuid","=", uuidLocal);
        OdooFields fields = new OdooFields();
        fields.addAll("id");
        int offset = 0;
        int limit = 1;
        String sorting = "id";
        String model = "foms.daily.trip.log";
        client.searchRead(model, domain, fields, offset, limit, sorting, new IOdooResponse() {
            @Override
            public void onResult(OdooResult result) {
                OdooRecord[] records = result.getRecords();
                int id = 0;
                for (OdooRecord record : records) {
                    id = record.getInt("id");
                }


                ODomain domain = new ODomain();
                domain.add("trip_log_id","=", id);
                OdooFields fields = new OdooFields();
                fields.addAll("id");
                int offset = 0;
                String sorting = "id";
                String model = "foms.daily.trip.expense";
                final int idTripLog = id;
                client.searchRead(model, domain, fields, offset, 0, sorting, new IOdooResponse() {
                    @Override
                    public void onResult(OdooResult result) {
                        OdooRecord[] records = result.getRecords();
                        Integer[] arId = new Integer[records.length];
                        int i = 0;
                        for (OdooRecord record : records) {
                            int idExpenseTrip = record.getInt("id");
                            arId[i] = idExpenseTrip;
                            i = i + 1;
                        }
                        if (arId.length == 0){
                            insert_expense_idx(expense_ids_local, idTripLog);
                        }else {
                            client.unlink("foms.daily.trip.expense", arId, new IOdooResponse() {
                                @Override
                                public void onResult(OdooResult result) {
                                    insert_expense_idx(expense_ids_local, idTripLog);
                                }
                            });
                        }
                    }
                });

            }
        });
    }

    public void insert_expense_idx (String expense_ids_local, int id){
        try {
            JSONObject jsonObjectCostCenter = new JSONObject("{\"expense_ids\":"+expense_ids_local+"}");
            JSONArray jsonArrayCostCenter = jsonObjectCostCenter.getJSONArray("expense_ids");

            OdooValues values = new OdooValues();
            for (int i = 0; i < jsonArrayCostCenter.length(); i++) {
                JSONObject objCostCenter = jsonArrayCostCenter.getJSONObject(i);
                values.put("contract_id", objCostCenter.getInt("contract_id"));
                values.put("trip_log_id", id);
                values.put("product_id", objCostCenter.getInt("product_id"));
                values.put("amount", objCostCenter.getDouble("amount"));
                values.put("notes", objCostCenter.getString("notes"));
                values.put("cost_center_id", objCostCenter.getInt("cost_center_id"));
                values.put("uuid", objCostCenter.getString("uuid"));
                client.create("foms.daily.trip.expense", values, new IOdooResponse() {
                    @Override
                    public void onResult(OdooResult result) {
                        Log.d("Hasil detail", result.toString());
                    }
                });
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void write(SyncInfoModel syncInfoModel, Context appContext){
        this.expense_ids = "";
        this.uuid = "";
        OdooValues values = getOdooValues(syncInfoModel);
        final String expense_ids_local = this.expense_ids;
        final String uuidLocal = this.uuid;

        ODomain domain = new ODomain();
        domain.add("uuid","=", uuid);
        OdooFields fields = new OdooFields();
        fields.addAll("id");
        int offset = 0;
        int limit = 1;
        String sorting = "id";
        String model = syncInfoModel.getModel();
        client.searchRead(model, domain, fields, offset, limit, sorting, new IOdooResponse() {
            @Override
            public void onResult(OdooResult result) {
                OdooRecord[] records = result.getRecords();
                int id = 0;
                for (OdooRecord record : records) {
                    id = record.getInt("id");
                }

                client.write(syncInfoModel.getModel(), new Integer[]{id}, values, new IOdooResponse() {
                    @Override
                    public void onResult(OdooResult result) {
                        Log.d("Hasil", result.toString());
                        ((JApplication)appContext).syncInfoTable.delete(syncInfoModel.getId());
                        if (!expense_ids.equals("") && !expense_ids.equals("[]")){
                            write_expense_ids(expense_ids_local, uuidLocal, appContext);
                        }
                    }

                    @Override
                    public boolean onError(OdooErrorException error) {
                        Log.d("Hasil", error.toString());
                        return false;
                    }
                });
            }
        });
    }
//
//    public void writeExpenseProof(ExpenseProofModel expenseProofModel){
//        OdooValues values = new OdooValues();
//
//        values.put("contract_id", expenseProofModel.getContract_id());
//        values.put("driver_id", expenseProofModel.getDriver_id());
//        values.put("vehicle_id", expenseProofModel.getVehicle_id());
//        values.put("proof_date", Global.getDateFormatedOdoo(expenseProofModel.getProof_date()));
//        values.put("proof_file", expenseProofModel.getProof_file());
//        values.put("notes", expenseProofModel.getNotes());
//
//        client.create("foms.daily.expense.proof", values, new IOdooResponse() {
//            @Override
//            public void onResult(OdooResult result) {
//                Log.d("Hasil", result.toString());
//            }
//        });
//    }
//
//
//    public void writeDailyTripLog(DailyTripLogModel dailyTripLogModel){
//        OdooValues values = new OdooValues();
//
//        values.put("contract_id", dailyTripLogModel.getContract_id());
//        values.put("customer_name", dailyTripLogModel.getCustomer_name());
//        values.put("start_date", Global.getDateTimeFormatedOdoo(dailyTripLogModel.getStart_date()));
//        values.put("end_date", Global.getDateTimeFormatedOdoo(dailyTripLogModel.getEnd_date()));
//        values.put("input_source", dailyTripLogModel.getInput_source());
//        values.put("driver_id", dailyTripLogModel.getDriver_id());
//        values.put("vehicle_id", dailyTripLogModel.getVehicle_id());
//        values.put("location", dailyTripLogModel.getLocation());
//        values.put("passenger_name", dailyTripLogModel.getPassenger_name());
//        values.put("start_odometer", dailyTripLogModel.getStart_odometer());
//        values.put("finish_odometer", dailyTripLogModel.getFinish_odometer());
//        values.put("trip_type", dailyTripLogModel.getTrip_type());
//        values.put("trip_notes", dailyTripLogModel.getTrip_notes());
//        values.put("state", dailyTripLogModel.getState());
//        values.put("total_expense", dailyTripLogModel.getTotal_expense());
//        values.put("recap_id", dailyTripLogModel.getRecap_id());
//        values.put("confirm_date", Global.getDateTimeFormatedOdoo(dailyTripLogModel.getConfirm_date()));
//        values.put("confirm_by", dailyTripLogModel.getConfirm_by());
//        values.put("cost_center_id", dailyTripLogModel.getCost_center_id());
//
//        client.create("foms.daily.trip.log", values, new IOdooResponse() {
//            @Override
//            public void onResult(OdooResult result) {
//                for (int i = 0; i < dailyTripLogModel.getExpense_ids().size(); i++){
//                    DailyTripExpenseModel dailyTripExpenseModel = dailyTripLogModel.getExpense_ids().get(i);
//                    OdooValues valuesExpenseProof = new OdooValues();
////                    valuesExpenseProof.put("contract_id", dailyTripExpenseModel.getContract_id());
////                    valuesExpenseProof.put("driver_id", dailyTripExpenseModel.());
//                    valuesExpenseProof.put("contract_id", dailyTripExpenseModel.getContract_id());
//                    valuesExpenseProof.put("trip_log_id", dailyTripExpenseModel.getTrip_log_id());
//                    valuesExpenseProof.put("product_id", dailyTripExpenseModel.getProduct_id());
//                    valuesExpenseProof.put("amount", dailyTripExpenseModel.getAmount());
//                    valuesExpenseProof.put("notes", dailyTripExpenseModel.getNotes());
//                    valuesExpenseProof.put("contract_id", dailyTripExpenseModel.getContract_id());
//                    valuesExpenseProof.put("cost_center_id", dailyTripExpenseModel.getCost_center_id());
//                    client.create("foms.daily.expense.proof", values, new IOdooResponse() {
//                        @Override
//                        public void onResult(OdooResult result) {
//
//                        }
//                    });
//                }
//            }
//        });
//    }

    private int get_id_by_object_odoo(ArrayList<Object> arrayList){
        String tempp = arrayList.get(0).toString();
        double temp = Double.valueOf(tempp);
        return ((int) temp);
    }

    private String record_to_string(String string){
        if (string.equals("false")){
            return "";
        }else{
            return string;
        }
    }
}
*/