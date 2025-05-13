package com.orion.pasienqu_2.globals;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.Routes;
import com.orion.pasienqu_2.RoutesSatuSehat;
import com.orion.pasienqu_2.activities.patient.PatientInputActivity;
import com.orion.pasienqu_2.data_table.SatuSehatTokenTable;
import com.orion.pasienqu_2.models.MedicalSatuSehatModel;
import com.orion.pasienqu_2.models.RecordDiagnosaModel;
import com.orion.pasienqu_2.models.SatuSehatTokenModel;
import com.orion.pasienqu_2.models.WorkLocationModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.UUID;

public class GlobalSatuSehat {

    public static final String INTERNAL_SERVER_ERROR = JApplication.currentActivity.getString(R.string.http_internal_server_error);

    public static void handleErrorResponse(int statusCode) {
        Activity activity = JApplication.currentActivity;
        switch (statusCode) {
/*            case HttpURLConnection.HTTP_OK:
                showMessage("200 OK: Request succeeded.");
                break;
            case HttpURLConnection.HTTP_CREATED:
                showMessage("201 Created: Resource created.");
                break;
            case HttpURLConnection.HTTP_ACCEPTED:
                showMessage("202 Accepted: Request accepted for processing.");
                break;
            case HttpURLConnection.HTTP_NO_CONTENT:
                showMessage("204 No Content: Request succeeded but no content returned.");
                break;
            case HttpURLConnection.HTTP_PARTIAL:
                showMessage("206 Partial Content: Partial content returned.");
                break;
            <string name="http_bad_request">400 Bad Request: Client error.</string>
<string name="http_unauthorized">401 Unauthorized: Authentication required.</string>
<string name="http_forbidden">403 Forbidden: Authorization required.</string>
<string name="http_not_found">404 Not Found: Resource not found.</string>
<string name="http_bad_method">405 Method Not Allowed: Method not allowed.</string>
<string name="http_conflict">409 Conflict: Resource conflict.</string>
<string name="http_entity_too_large">413 Payload Too Large: Request entity too large.</string>
<string name="http_unsupported_type">415 Unsupported Media Type: Unsupported media type.</string>
<string name="http_unprocessable_entity">422 Unprocessable Entity: Unprocessable entity.</string>
<string name="http_unavailable">503 Service Unavailable: Server unavailable.</string>
<string name="http_gateway_timeout">504 Gateway Timeout: Gateway timeout.</string>
<string name="http_default_message">Unhandled status code: %1$d</string>
            */
            case HttpURLConnection.HTTP_BAD_REQUEST:
                showMessage(activity.getString(R.string.http_bad_request));
                break;
            case HttpURLConnection.HTTP_UNAUTHORIZED:
                showMessage(activity.getString(R.string.http_unauthorized));
                break;
            case HttpURLConnection.HTTP_FORBIDDEN:
                showMessage(activity.getString(R.string.http_forbidden));
                break;
            case HttpURLConnection.HTTP_NOT_FOUND:
                showMessage(activity.getString(R.string.http_not_found));
                break;
            case HttpURLConnection.HTTP_BAD_METHOD:
                showMessage(activity.getString(R.string.http_bad_method));
                break;
            case HttpURLConnection.HTTP_CONFLICT:
                showMessage(activity.getString(R.string.http_conflict));
                break;
            case HttpURLConnection.HTTP_ENTITY_TOO_LARGE:
                showMessage(activity.getString(R.string.http_entity_too_large));
                break;
            case HttpURLConnection.HTTP_UNSUPPORTED_TYPE:
                showMessage(activity.getString(R.string.http_unsupported_type));
                break;
            case 422:
                showMessage(activity.getString(R.string.http_unprocessable_entity));
                break;
            case HttpURLConnection.HTTP_UNAVAILABLE:
                showMessage(activity.getString(R.string.http_unavailable));
                break;
            case HttpURLConnection.HTTP_GATEWAY_TIMEOUT:
                showMessage(activity.getString(R.string.http_gateway_timeout));
                break;
            default:
                showMessage(activity.getString(R.string.http_default_message, statusCode));
                break;
        }
    }

    public static void showMessage(String message) {
        Activity activity = JApplication.currentActivity;
        if (message.contains("Unable to resolve host")) {
            ShowDialog.infoDialog(activity, "Error", activity.getString(R.string.cannot_connect_to_server_satu_sehat));
        } else {
            ShowDialog.infoDialog(activity, "Error", message);
        }
    }

    /**
     * Generate token satu sehat
     * setelah dapat token langsung save ke tabel satu_sehat_token
     **/
/*    public static void generateToken(Activity activity, Runnable runnable, String clientID, String clientSecret) {
        ProgressDialog loading = Global.createProgresSpinner(activity, activity.getString(R.string.loading));
        loading.show();

        String url = Routes.url_generate_token_satu_sehat()
                + "?client_id=" + clientID
                + "&client_secret=" + clientSecret
                + "&url=" + RoutesSatuSehat.GENERATE_TOKEN_URL;

        StringRequest strReq = new StringRequest(Request.Method.GET, url, response -> {
            try {
                if (response.contains(JConst.STATUS_API_SUCCESS)) {
                    JSONObject jsonResponse = new JSONObject(response).getJSONObject("success");
                    String token = jsonResponse.getString("access_token");
                    long last_update = jsonResponse.getLong("issued_at");

                    //save ke tabel
                    SatuSehatTokenTable satuSehatTokenTable = new SatuSehatTokenTable(activity);
                    satuSehatTokenTable.deleteAll();
                    satuSehatTokenTable.insert(new SatuSehatTokenModel(0, token, last_update));

                    runnable.run();
                    loading.dismiss();
                } else if (response.contains(JConst.STATUS_API_FAILED)) {
                    String pesan = new JSONObject(response).getString("error");
                    loading.dismiss();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                loading.dismiss();
            }
        }, error -> {
            // Tangani kesalahan jaringan atau kegagalan permintaan
            loading.dismiss();
            NetworkResponse networkResponse = error.networkResponse;
            if (networkResponse != null) {
                handleErrorResponse(networkResponse.statusCode);
            } else {
                showMessage(INTERNAL_SERVER_ERROR);
            }
        });
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }*/

/*
    public static void generateTokenPerLocation(Activity activity, Runnable runnable, String clientID, String clientSecret, int workLocationId) {
        ProgressDialog loading = Global.createProgresSpinner(activity, activity.getString(R.string.loading));
        loading.show();

        String url = Routes.url_generate_token_satu_sehat()
                + "?client_id=" + clientID
                + "&client_secret=" + clientSecret
                + "&url=" + RoutesSatuSehat.GENERATE_TOKEN_URL;

        StringRequest strReq = new StringRequest(Request.Method.GET, url, response -> {
            try {
                if (response.contains(JConst.STATUS_API_SUCCESS)) {
                    JSONObject jsonResponse = new JSONObject(response).getJSONObject("success");
                    String token = jsonResponse.getString("access_token");
                    long last_update = jsonResponse.getLong("issued_at");

                    //save ke tabel
                    SatuSehatTokenTable satuSehatTokenTable = new SatuSehatTokenTable(activity);
                    satuSehatTokenTable.deleteAll();
                    satuSehatTokenTable.insert(new SatuSehatTokenModel(0, token, last_update));
                    JApplication.getInstance().globalTable.UpdateTable("pasienqu_work_location",
                            "token = '" + token + "', last_generate_token = " + last_update, "WHERE id = " + workLocationId);

                    runnable.run();
                    loading.dismiss();
                } else if (response.contains(JConst.STATUS_API_FAILED)) {
                    String pesan = new JSONObject(response).getString("error");
                    loading.dismiss();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                loading.dismiss();
            }
        }, error -> {
            // Tangani kesalahan jaringan atau kegagalan permintaan
            loading.dismiss();
            NetworkResponse networkResponse = error.networkResponse;
            if (networkResponse != null) {
                handleErrorResponse(networkResponse.statusCode);
            } else {
                showMessage(INTERNAL_SERVER_ERROR);
            }
        });
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }
*/

    public static void generateToken(Activity activity, Runnable runnable, String clientID, String clientSecret) {
        ProgressDialog loading = Global.createProgresSpinner(activity, activity.getString(R.string.loading));
        loading.show();

        String url = RoutesSatuSehat.GENERATE_TOKEN_URL;

        StringRequest strReq = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONObject jsonResponse = new JSONObject(response);
                if (jsonResponse.has("access_token") && jsonResponse.has("issued_at")) {
                    String token = jsonResponse.getString("access_token");
                    long last_update = jsonResponse.getLong("issued_at");

                    //save ke tabel
                    SatuSehatTokenTable satuSehatTokenTable = new SatuSehatTokenTable(activity);
                    satuSehatTokenTable.deleteAll();
                    satuSehatTokenTable.insert(new SatuSehatTokenModel(0, token, last_update));

                    runnable.run();
                    loading.dismiss();
                } else {
                    // Tangani respons tidak valid di sini
                    loading.dismiss();
                    showMessage("Invalid response format");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                loading.dismiss();
                showMessage("Error parsing response");
            }
        }, error -> {
            // Tangani kesalahan jaringan atau kegagalan permintaan
            loading.dismiss();
            if (error.getMessage() != null && error.getMessage().contains("Unable to resolve host")){
                showMessage("Tidak bisa terhubung ke Satu Sehat, silahkan coba beberapa saat lagi.");
                return;
            }
            NetworkResponse networkResponse = error.networkResponse;
            if (networkResponse != null) {
                handleErrorResponse(networkResponse.statusCode);
            } else {
                showMessage("Internal server error");
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("client_id", clientID);
                params.put("client_secret", clientSecret);
                return params;
            }
        };
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }

    public static void generateTokenPerLocation(Activity activity, Runnable runnable, String clientID, String clientSecret, int workLocationId) {
        ProgressDialog loading = Global.createProgresSpinner(activity, activity.getString(R.string.loading));
        loading.show();

        String url = RoutesSatuSehat.GENERATE_TOKEN_URL;

        StringRequest strReq = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONObject jsonResponse = new JSONObject(response);
                if (jsonResponse.has("access_token") && jsonResponse.has("issued_at")) {
                    String token = jsonResponse.getString("access_token");
                    long last_update = jsonResponse.getLong("issued_at");

                    //save ke tabel
                    SatuSehatTokenTable satuSehatTokenTable = new SatuSehatTokenTable(activity);
                    satuSehatTokenTable.deleteAll();
                    satuSehatTokenTable.insert(new SatuSehatTokenModel(0, token, last_update));
                    JApplication.getInstance().globalTable.UpdateTable("pasienqu_work_location",
                            "token = '" + token + "', last_generate_token = " + last_update, "WHERE id = " + workLocationId);

                    runnable.run();
                    loading.dismiss();
                } else {
                    // Tangani respons tidak valid di sini
                    loading.dismiss();
                    showMessage("Invalid response format");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                loading.dismiss();
                showMessage("Error parsing response");
            }
        }, error -> {
            // Tangani kesalahan jaringan atau kegagalan permintaan
            loading.dismiss();
            if (error.getMessage() != null && error.getMessage().contains("Unable to resolve host")){
                showMessage("Tidak bisa terhubung ke Satu Sehat, silahkan coba beberapa saat lagi.");
                return;
            }
            NetworkResponse networkResponse = error.networkResponse;
            if (networkResponse != null) {
                handleErrorResponse(networkResponse.statusCode);
            } else {
                showMessage("Internal server error");
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("client_id", clientID);
                params.put("client_secret", clientSecret);
                return params;
            }
        };
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }


    public static void checkTokenSatuSehat(Activity activity, Runnable runnable) {
        long lastGenerate = JApplication.getInstance().globalTable.getLastGenerateTokenSatuSehat();
        long currentTime = Global.serverNowLong();
        long thirtyMinutesInMillis = 30 * 60 * 1000; // 30 menit dalam milidetik

        String clientID = "";
        String clientSecret = "";
        String tmpClientID = JApplication.getInstance().globalTable.getMaxClientId();
        String tmpClientSecret = JApplication.getInstance().globalTable.getMaxClientSecret();

        if (!TextUtils.isEmpty(tmpClientID) && !TextUtils.isEmpty(tmpClientSecret)) {
            clientID = tmpClientID;
            clientSecret = tmpClientSecret;
        } else {
//            clientID = RoutesSatuSehat.CLIENT_ID;
//            clientSecret = RoutesSatuSehat.CLIENT_SECRET;
//            runnable.run();
            ShowDialog.infoDialog(activity, activity.getString(R.string.information), "Tidak bisa terhubung ke Satu Sehat. Silahkan lengkapi data lokasi di menu Satu Sehat.");
            return;
        }


        if (currentTime - lastGenerate > thirtyMinutesInMillis) {
            generateToken(activity, runnable, clientID, clientSecret);
        } else {
            runnable.run();
        }
    }

    public static void checkTokenSatuSehatPerLocation(Activity activity, int locationId, Runnable runnable) {
        WorkLocationModel dataLocation = JApplication.getInstance().workLocationTable.getDataById(locationId);
        long lastGenerate = dataLocation.getLast_generate_token();
        long currentTime = Global.serverNowLong();
        long thirtyMinutesInMillis = 30 * 60 * 1000; // 30 menit dalam milidetik

        String clientID = "";
        String clientSecret = "";

        String tmpClientID = dataLocation.getClient_id();
        String tmpClientSecret = dataLocation.getClient_secret();

        if (!TextUtils.isEmpty(tmpClientID) && !TextUtils.isEmpty(tmpClientSecret)) {
            clientID = tmpClientID;
            clientSecret = tmpClientSecret;
        } else {
//            clientID = RoutesSatuSehat.CLIENT_ID;
//            clientSecret = RoutesSatuSehat.CLIENT_SECRET;
            runnable.run();
            return;
        }


        if (currentTime - lastGenerate > thirtyMinutesInMillis) {
            generateTokenPerLocation(activity, runnable, clientID, clientSecret, locationId);
        } else {
            runnable.run();
        }
    }


    /**
     * Get Pasien IHS by NIK.
     * setelah dapat ihs langsung save ke tabel pasien.
     **/
    public static void checkPatientByNIK(Activity activity, Runnable runTerdaftar, Runnable runTidakTerdaftar, String NIK) {
//        JApplication.patientIHS = ""; //initial
        ProgressDialog loading = Global.createProgresSpinner(activity, activity.getString(R.string.loading));
        loading.show();
        String url = RoutesSatuSehat.url_patient + "?identifier=https://fhir.kemkes.go.id/id/nik|" + NIK;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                JSONArray entryArray = response.getJSONArray("entry");
                if (entryArray.length() > 0) {
                    JSONObject resourceObject = entryArray.getJSONObject(0).getJSONObject("resource");
                    JApplication.patientIHS = resourceObject.getString("id");
                    JApplication.getInstance().globalTable.UpdateTable("pasienqu_patient", "patient_ihs = '" + resourceObject.getString("id") + "'", "WHERE identification_no = '" + NIK + "'");
                    runTerdaftar.run();
                } else {
                    runTidakTerdaftar.run();
                }
                //dissmis
                loading.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
                showMessage(e.getMessage());
                loading.dismiss();
            }
        }, error -> {
            // Tangani kesalahan jaringan atau kegagalan permintaan
            loading.dismiss();
            NetworkResponse networkResponse = error.networkResponse;
            if (networkResponse != null) {
                handleErrorResponse(networkResponse.statusCode);
            } else {
                showMessage(INTERNAL_SERVER_ERROR);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + JApplication.getInstance().globalTable.getTokenSatuSehat());
                return headers;
            }
        };
        JApplication.getInstance().addToRequestQueue(jsonObjectRequest, Global.tag_json_obj);
    }

    /**
     * Get Practitioner by ID IHS nakes.
     * setelah dapat token langsung save ke tabel practitioner
     **/
    public static void checkPractitionerByID(Activity activity, Runnable runSuccess, Runnable runFailed, String ihsNumber) {
        ProgressDialog loading = Global.createProgresSpinner(activity, activity.getString(R.string.loading));
        loading.show();
        String url = RoutesSatuSehat.url_practitioner + "/" + ihsNumber;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                if (response != null) {
                    loading.dismiss();
                    runSuccess.run();
                }
            } catch (Exception e) {
                e.printStackTrace();
                showMessage(e.getMessage());
                loading.dismiss();
            }
        }, error -> {
            // Tangani kesalahan jaringan atau kegagalan permintaan
            loading.dismiss();
            NetworkResponse networkResponse = error.networkResponse;
            if (networkResponse != null) {
                if (HttpURLConnection.HTTP_NOT_FOUND == networkResponse.statusCode) {
                    loading.dismiss();
                    runFailed.run();
                } else {
                    handleErrorResponse(networkResponse.statusCode);
                }
            } else {
                showMessage(INTERNAL_SERVER_ERROR);
            }

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + JApplication.getInstance().globalTable.getTokenSatuSehat());
                return headers;
            }
        };
        JApplication.getInstance().addToRequestQueue(jsonObjectRequest, Global.tag_json_obj);
    }


    /**
     * Get Organization by ID IHS dari klinik yang sudah didaftarkan.
     * jika ID valid maka lanjut save location
     * jika save location sukses maka simpan ID Organisasi dan ID lokasi ke database.
     **/
    public static void checkOrganizationByID(Activity activity, Runnable runSuccess, Runnable runFailed, String organizationID) {
        ProgressDialog loading = Global.createProgresSpinner(activity, activity.getString(R.string.loading));
        loading.show();
        String url = RoutesSatuSehat.url_organization + "/" + organizationID;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                if (response.getBoolean("active")) {
                    loading.dismiss();
                    runSuccess.run();
                } else {
                    showMessage(activity.getString(R.string.number_not_registered_or_inactive));
                    loading.dismiss();
                }
            } catch (Exception e) {
                e.printStackTrace();
                showMessage(e.getMessage());
                loading.dismiss();
            }
        }, error -> {
            // Tangani kesalahan jaringan atau kegagalan permintaan
            loading.dismiss();
            NetworkResponse networkResponse = error.networkResponse;
            if (networkResponse != null) {
                if (HttpURLConnection.HTTP_NOT_FOUND == networkResponse.statusCode) {
                    loading.dismiss();
                    runFailed.run();
                } else {
                    handleErrorResponse(networkResponse.statusCode);
                }
            } else {
                showMessage(INTERNAL_SERVER_ERROR);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + JApplication.getInstance().globalTable.getTokenSatuSehat());
                return headers;
            }
        };
        JApplication.getInstance().addToRequestQueue(jsonObjectRequest, Global.tag_json_obj);
    }

    public static void checkLocationOrganizationID(Activity activity, Runnable runSuccess, Runnable runCreateLocation, String organizationID, String nameLocation) {
        ProgressDialog loading = Global.createProgresSpinner(activity, activity.getString(R.string.loading));
        loading.show();
        String url = RoutesSatuSehat.url_location + "?organization=" + organizationID;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                JSONArray entryArray = response.getJSONArray("entry"); // Mendapatkan array "entry" dari respons JSON
                // Iterasi setiap objek dalam array "entry"
                boolean isAlreadyCreated = false;
                for (int i = 0; i < entryArray.length(); i++) {
                    JSONObject entryObject = entryArray.getJSONObject(i);
                    JSONObject resourceObject = entryObject.getJSONObject("resource");
                    String name = resourceObject.getString("name");

                    if (name.equals(nameLocation)) {
                        isAlreadyCreated = true;
                        JApplication.locationIHS = resourceObject.getString("id");
                    }
                }
                // Lakukan sesuatu di sini karena lokasi ditemukan
                if (isAlreadyCreated) {
                    loading.dismiss();
                    runSuccess.run();
                } else {
                    runCreateLocation.run();
                }
            } catch (Exception e) {
                e.printStackTrace();
                showMessage(e.getMessage());
                loading.dismiss();
            }
        }, error -> {
            // Tangani kesalahan jaringan atau kegagalan permintaan
            loading.dismiss();
            NetworkResponse networkResponse = error.networkResponse;
            if (networkResponse != null) {
                handleErrorResponse(networkResponse.statusCode);
            } else {
                showMessage(INTERNAL_SERVER_ERROR);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + JApplication.getInstance().globalTable.getTokenSatuSehat());
                return headers;
            }
        };
        JApplication.getInstance().addToRequestQueue(jsonObjectRequest, Global.tag_json_obj);
    }

    /**
     * Get Organization by ID IHS dari klinik yang sudah didaftarkan.
     * jika ID valid maka lanjut save location
     * jika save location sukses maka simpan ID Organisasi dan ID lokasi ke database.
     **/
    public static void createLocation(Activity activity, Runnable runSuccess, Runnable runFailed, String organizationID, WorkLocationModel data) {
        ProgressDialog loading = Global.createProgresSpinner(activity, activity.getString(R.string.loading));
        loading.show();
        JApplication.locationIHS = ""; //initial

        String url = RoutesSatuSehat.url_location;
        JSONObject requestBody = createLocationRequestBody(organizationID, data);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody, response -> {
            try {
                if (response != null) {
                    JApplication.locationIHS = response.getString("id");
                    runSuccess.run();
                    loading.dismiss();
                }
            } catch (Exception e) {
                e.printStackTrace();
                showMessage(e.getMessage());
                loading.dismiss();
            }
        }, error -> {
            // Tangani kesalahan jaringan atau kegagalan permintaan
            loading.dismiss();
            NetworkResponse networkResponse = error.networkResponse;
            if (networkResponse != null) {
                handleErrorResponse(networkResponse.statusCode);
            } else {
                showMessage(INTERNAL_SERVER_ERROR);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + JApplication.getInstance().globalTable.getTokenSatuSehat());
                return headers;
            }
        };
        JApplication.getInstance().addToRequestQueue(jsonObjectRequest, Global.tag_json_obj);
    }

    private static JSONObject createLocationRequestBody(String organizationID, WorkLocationModel data) {
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("resourceType", "Location");

            JSONArray identifierArray = new JSONArray();
            JSONObject identifierObject = new JSONObject();
            identifierObject.put("system", "http://sys-ids.kemkes.go.id/location/" + organizationID);
            identifierObject.put("value", JConst.DEFAULT_LOCATION_SATUSEHAT + ", " + data.getName());
            identifierArray.put(identifierObject);

            requestBody.put("identifier", identifierArray);
            requestBody.put("status", "active");
            requestBody.put("name", JConst.DEFAULT_LOCATION_SATUSEHAT + ", " + data.getName());
            requestBody.put("description", JConst.DEFAULT_LOCATION_SATUSEHAT + ", " + data.getName());
            requestBody.put("mode", "instance");

            JSONObject managingOrganizationObject = new JSONObject();
            managingOrganizationObject.put("reference", "Organization/" + organizationID);
            requestBody.put("managingOrganization", managingOrganizationObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return requestBody;
    }

    /**
     * Get Organization by ID IHS dari klinik yang sudah didaftarkan.
     * jika ID valid maka lanjut save location
     * jika save location sukses maka simpan ID Organisasi dan ID lokasi ke database.
     **/
    /*public static void createResumeMedis(Activity activity, Runnable runSuccess, Runnable runFailed, MedicalSatuSehatModel data, String practitionerID) {
        ProgressDialog loading = Global.createProgresSpinner(activity, activity.getString(R.string.loading));
        loading.show();

        String url = RoutesSatuSehat.BASE_URL;
        JSONObject requestBody = createResumeMedisRequestBody(data, practitionerID);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody, response -> {
            try {
                if (response != null) {
                    JSONArray entryArray = response.getJSONArray("entry");
                    for (int i = 0; i < entryArray.length(); i++) {
                        JSONObject entryObject = entryArray.getJSONObject(i);
                        JSONObject responseObject = entryObject.getJSONObject("response");
                        String resourceType = responseObject.getString("resourceType");
                        String resourceID = responseObject.getString("resourceID");
                        if (resourceType.equals("Encounter")) {
                            // Simpan ke tabel rekam_medis
                            JApplication.getInstance().globalTable.UpdateTable("pasienqu_record", "id_encounter = '"+resourceID+"'", "WHERE uuid = '"+data.getRecord_uuid()+"'");
                        } else if (resourceType.equals("Condition")) {
                            // Simpan ke tabel diagnosa
                            JApplication.getInstance().globalTable.UpdateTable("pasienqu_record_diagnosa", "id_condition = '"+resourceID+"'", "WHERE record_uuid = '"+data.getRecord_uuid()+"'");
                        }
                    }
                    runSuccess.run();
                    loading.dismiss();
                }
            } catch (Exception e) {
                e.printStackTrace();
                showMessage(e.getMessage());
                loading.dismiss();
            }
        }, error -> {
            // Tangani kesalahan jaringan atau kegagalan permintaan
            loading.dismiss();
            NetworkResponse networkResponse = error.networkResponse;
            if (networkResponse != null) {
                handleErrorResponse(networkResponse.statusCode);
            }else{
                showMessage(INTERNAL_SERVER_ERROR);
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type","application/json");
                headers.put("Authorization","Bearer "+JApplication.getInstance().globalTable.getTokenSatuSehat());
                return headers;
            }
        };
        JApplication.getInstance().addToRequestQueue(jsonObjectRequest, Global.tag_json_obj);
    }*/
    // Tambahkan interface RequestCallback
    public interface RequestCallback {
        void onConnectionError();

        void onSuccess();
    }

/*
    // Modifikasi createResumeMedis dengan menambahkan parameter callback
    public static void createResumeMedis(Activity activity, Runnable runSuccess, Runnable runFailed, MedicalSatuSehatModel data, String practitionerID, RequestCallback callback) {
        String url = RoutesSatuSehat.BASE_URL;
        JSONObject requestBody = createResumeMedisRequestBody(data, practitionerID);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody, response -> {
            try {
                if (response != null) {
                    JSONArray entryArray = response.getJSONArray("entry");
                    for (int i = 0; i < entryArray.length(); i++) {
                        JSONObject entryObject = entryArray.getJSONObject(i);
                        JSONObject responseObject = entryObject.getJSONObject("response");
                        String resourceType = responseObject.getString("resourceType");
                        String resourceID = responseObject.getString("resourceID");
                        if (resourceType.equals("Encounter")) {
                            // Simpan ke tabel rekam_medis
                            JApplication.getInstance().globalTable.UpdateTable("pasienqu_record", "id_encounter = '" + resourceID + "'", "WHERE uuid = '" + data.getRecord_uuid() + "'");
                        } else if (resourceType.equals("Condition")) {
                            // Simpan ke tabel diagnosa
                            JApplication.getInstance().globalTable.UpdateTable("pasienqu_record_diagnosa", "id_condition = '" + resourceID + "'", "WHERE record_uuid = '" + data.getRecord_uuid() + "'");
                        }
                    }
                    runSuccess.run();
                }
            } catch (Exception e) {
                e.printStackTrace();
                showMessage(e.getMessage());
            }
        }, error -> {
            // Tangani kesalahan jaringan atau kegagalan permintaan
//            NetworkResponse networkResponse = error.networkResponse;
//            if (networkResponse != null) {
//                handleErrorResponse(networkResponse.statusCode);
//            } else {
//                showMessage(INTERNAL_SERVER_ERROR);
//            }
            callback.onConnectionError(); // Panggil kembali metode callback saat koneksi terputus
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + data.getToken());
                return headers;
            }
        };
        JApplication.getInstance().addToRequestQueue(jsonObjectRequest, Global.tag_json_obj);
    }

    private static JSONObject createResumeMedisRequestBody(MedicalSatuSehatModel data, String practitionerID) {

        JSONObject requestBody = new JSONObject();
        JSONArray entryArray = new JSONArray();
        try {
            JSONObject encounterObject = new JSONObject();
            encounterObject.put("resourceType", "Encounter");
            encounterObject.put("status", "finished");

            JSONObject encounterClassObject = new JSONObject();
            encounterClassObject.put("system", "http://terminology.hl7.org/CodeSystem/v3-ActCode");
            encounterClassObject.put("code", "AMB");
            encounterClassObject.put("display", "ambulatory");
            encounterObject.put("class", encounterClassObject);

            JSONObject subjectObject = new JSONObject();
            subjectObject.put("reference", "Patient/" + data.getPatient_ihs());
            subjectObject.put("display", data.getPatient_codename());
            encounterObject.put("subject", subjectObject);

            JSONArray participantArray = new JSONArray();
            JSONObject participantObject = new JSONObject();
            JSONObject individualObject = new JSONObject();
            individualObject.put("reference", "Practitioner/" + practitionerID);
            individualObject.put("display", JApplication.getInstance().loginCompanyModel.getName());
            participantObject.put("individual", individualObject);
            participantArray.put(participantObject);
            encounterObject.put("participant", participantArray);

            JSONObject periodObject = new JSONObject();
            periodObject.put("start", Global.getDateFormatSatuSehat(data.getRecord_date()));
            periodObject.put("end", Global.getDateFormatSatuSehat(data.getRecord_date()));
            encounterObject.put("period", periodObject);

            JSONArray locationArray = new JSONArray();
            JSONObject locationObject = new JSONObject();
            JSONObject locationRefObject = new JSONObject();
            locationRefObject.put("reference", "Location/" + data.getLocation_ihs());
            locationRefObject.put("display", data.getLocation());
            locationObject.put("location", locationRefObject);
            locationArray.put(locationObject);
            encounterObject.put("location", locationArray);

            JSONArray diagnosisArray = new JSONArray();
            for (RecordDiagnosaModel diagnosis : data.getIcd_ids()) {
                JSONObject diagnosisObject = new JSONObject();
                JSONObject conditionObject = new JSONObject();
                conditionObject.put("reference", "urn:uuid:"+diagnosis.getUuid());
                conditionObject.put("display", diagnosis.getIcd_name());
                diagnosisObject.put("condition", conditionObject);
                diagnosisArray.put(diagnosisObject);
            }
            encounterObject.put("diagnosis", diagnosisArray);

            JSONArray statusHistoryArray = new JSONArray();
            JSONObject statusHistoryObject = new JSONObject();
            statusHistoryObject.put("status", "finished");
            JSONObject statusPeriodObject = new JSONObject();
            statusPeriodObject.put("start", Global.getDateFormatSatuSehat(data.getRecord_date()));
            statusPeriodObject.put("end", Global.getDateFormatSatuSehat(data.getRecord_date()));
            statusHistoryObject.put("period", statusPeriodObject);
            statusHistoryArray.put(statusHistoryObject);
            encounterObject.put("statusHistory", statusHistoryArray);

            JSONObject serviceProviderObject = new JSONObject();
            serviceProviderObject.put("reference", "Organization/" + data.getOrganization_ihs());
            encounterObject.put("serviceProvider", serviceProviderObject);

            JSONArray identifierArray = new JSONArray();
            JSONObject identifierObject = new JSONObject();
            identifierObject.put("system", "http://sys-ids.kemkes.go.id/encounter/" + data.getOrganization_ihs());
            identifierObject.put("value", data.getRecord_uuid());
            identifierArray.put(identifierObject);
            encounterObject.put("identifier", identifierArray);

            JSONObject encounterEntryObject = new JSONObject();
            encounterEntryObject.put("fullUrl", "urn:uuid:" + UUID.randomUUID().toString());
            encounterEntryObject.put("resource", encounterObject);
            JSONObject encounterRequestObject = new JSONObject();
            encounterRequestObject.put("method", "POST");
            encounterRequestObject.put("url", "Encounter");
            encounterEntryObject.put("request", encounterRequestObject);
            entryArray.put(encounterEntryObject);

            for (RecordDiagnosaModel diagnosis : data.getIcd_ids()) {
                String icdKode = diagnosis.getIcd_code();
                if (icdKode.length() > 3) {
                    String firstThreeChars = icdKode.substring(0, 3);
                    String remainingChars = icdKode.substring(3);
                    icdKode = firstThreeChars + "." + remainingChars;
                }

                JSONObject diagnosisObject = new JSONObject();
                diagnosisObject.put("resourceType", "Condition");
                JSONObject clinicalStatusObject = new JSONObject();
                JSONArray codingArray = new JSONArray();
                JSONObject codingObject = new JSONObject();
                codingObject.put("system", "http://terminology.hl7.org/CodeSystem/condition-clinical");
                codingObject.put("code", "active");
                codingObject.put("display", "Active");
                codingArray.put(codingObject);
                clinicalStatusObject.put("coding", codingArray);
                diagnosisObject.put("clinicalStatus", clinicalStatusObject);
                JSONArray categoryArray = new JSONArray();
                JSONObject categoryObject = new JSONObject();
                JSONArray categoryCodingArray = new JSONArray();
                JSONObject categoryCodingObject = new JSONObject();
                categoryCodingObject.put("system", "http://terminology.hl7.org/CodeSystem/condition-category");
                categoryCodingObject.put("code", "encounter-diagnosis");
                categoryCodingObject.put("display", "Encounter Diagnosis");
                categoryCodingArray.put(categoryCodingObject);
                categoryObject.put("coding", categoryCodingArray);
                categoryArray.put(categoryObject);
                diagnosisObject.put("category", categoryArray);
                JSONObject codeObject = new JSONObject();
                JSONArray codeCodingArray = new JSONArray();
                JSONObject codeCodingObject = new JSONObject();
                codeCodingObject.put("system", "http://hl7.org/fhir/sid/icd-10");
                codeCodingObject.put("code", icdKode);
                codeCodingObject.put("display", diagnosis.getIcd_name());
                codeCodingArray.put(codeCodingObject);
                codeObject.put("coding", codeCodingArray);
                diagnosisObject.put("code", codeObject);
                JSONObject diagnosisSubjectObject = new JSONObject();
                diagnosisSubjectObject.put("reference", "Patient/" + data.getPatient_ihs());
                diagnosisSubjectObject.put("display", data.getPatient_codename());
                diagnosisObject.put("subject", diagnosisSubjectObject);
                JSONObject diagnosisEncounterObject = new JSONObject();
                diagnosisEncounterObject.put("reference", encounterEntryObject.getString("fullUrl"));
                diagnosisEncounterObject.put("display", "Kunjungan Pasien");
                diagnosisObject.put("encounter", diagnosisEncounterObject);
                JSONObject diagnosisEntryObject = new JSONObject();
                diagnosisEntryObject.put("fullUrl", "urn:uuid:"+diagnosis.getUuid());
                diagnosisEntryObject.put("resource", diagnosisObject);
                JSONObject diagnosisRequestObject = new JSONObject();
                diagnosisRequestObject.put("method", "POST");
                diagnosisRequestObject.put("url", "Condition");
                diagnosisEntryObject.put("request", diagnosisRequestObject);
                entryArray.put(diagnosisEntryObject);
            }

            requestBody.put("resourceType", "Bundle");
            requestBody.put("type", "transaction");
            requestBody.put("entry", entryArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return requestBody;
    }
*/
    public static void sendEncounter(MedicalSatuSehatModel data, String practitionerID, RequestCallback callback) {
        String url = RoutesSatuSehat.url_encounter;
        JSONObject requestBody = createEncounterObjectBody(data, practitionerID);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody, response -> {
            try {
                if (response != null) {
                    String encounterID = response.getString("id");
                    JApplication.getInstance().globalTable.UpdateTable("pasienqu_record", "id_encounter = '" + encounterID + "'", "WHERE uuid = '" + data.getRecord_uuid() + "'");
                    callback.onSuccess(); // Panggil metode onSuccess dari callback saat pengiriman berhasil
                }
            } catch (Exception e) {
                e.printStackTrace();
                showMessage(e.getMessage());
            }
        }, error -> {
            callback.onConnectionError(); // Panggil kembali metode callback saat koneksi terputus
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + data.getToken());
                return headers;
            }
        };
        JApplication.getInstance().addToRequestQueue(jsonObjectRequest, Global.tag_json_obj);
    }


    public static void sendCondition(MedicalSatuSehatModel data, RecordDiagnosaModel diagnosaModel, String encounterId, RequestCallback callback) {
        String url = RoutesSatuSehat.url_condition;
        JSONObject requestBody = createConditionObjectBody(data, diagnosaModel, encounterId);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody, response -> {
            try {
                if (response != null) {
                    String conditionID = response.getString("id");
                    JApplication.getInstance().globalTable.UpdateTable("pasienqu_record_diagnosa", "id_condition = '" + conditionID + "'", "WHERE uuid = '" + diagnosaModel.getUuid() + "'");
                    callback.onSuccess();
                }
            } catch (Exception e) {
                e.printStackTrace();
                showMessage(e.getMessage());
            }
        }, error -> {
            callback.onConnectionError(); // Panggil kembali metode callback saat koneksi terputus
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + data.getToken());
                return headers;
            }
        };
        JApplication.getInstance().addToRequestQueue(jsonObjectRequest, Global.tag_json_obj);
    }

    private static JSONObject createEncounterObjectBody(MedicalSatuSehatModel data, String practitionerID)  {
        JSONObject encounterObject = new JSONObject();
        try {
            encounterObject.put("resourceType", "Encounter");
            encounterObject.put("status", "arrived");

            JSONObject encounterClassObject = new JSONObject();
            encounterClassObject.put("system", "http://terminology.hl7.org/CodeSystem/v3-ActCode");
            encounterClassObject.put("code", "AMB");
            encounterClassObject.put("display", "ambulatory");
            encounterObject.put("class", encounterClassObject);

            JSONObject subjectObject = new JSONObject();
            subjectObject.put("reference", "Patient/" + data.getPatient_ihs());
            subjectObject.put("display", data.getPatient_codename());
            encounterObject.put("subject", subjectObject);

            JSONArray participantArray = new JSONArray();
            JSONObject participantObject = new JSONObject();
            JSONObject individualObject = new JSONObject();
            individualObject.put("reference", "Practitioner/" + practitionerID);
            individualObject.put("display", JApplication.getInstance().loginCompanyModel.getName());
            participantObject.put("type", new JSONArray().put(new JSONObject().put("coding", new JSONArray().put(new JSONObject().put("system", "http://terminology.hl7.org/CodeSystem/v3-ParticipationType").put("code", "ATND").put("display", "attender")))));
            participantObject.put("individual", individualObject);
            participantArray.put(participantObject);
            encounterObject.put("participant", participantArray);

            JSONObject periodObject = new JSONObject();
            periodObject.put("start", Global.getDateFormatSatuSehat(data.getRecord_date()));
            encounterObject.put("period", periodObject);

            JSONArray locationArray = new JSONArray();
            JSONObject locationObject = new JSONObject();
            JSONObject locationRefObject = new JSONObject();
            locationRefObject.put("reference", "Location/" + data.getLocation_ihs());
            locationRefObject.put("display", data.getLocation());
            locationObject.put("location", locationRefObject);
            locationArray.put(locationObject);
            encounterObject.put("location", locationArray);

            JSONArray statusHistoryArray = new JSONArray();
            JSONObject statusHistoryObject = new JSONObject();
            statusHistoryObject.put("status", "arrived");
            JSONObject statusPeriodObject = new JSONObject();
            statusPeriodObject.put("start", Global.getDateFormatSatuSehat(data.getRecord_date()));
            statusHistoryObject.put("period", statusPeriodObject);
            statusHistoryArray.put(statusHistoryObject);
            encounterObject.put("statusHistory", statusHistoryArray);

            JSONObject serviceProviderObject = new JSONObject();
            serviceProviderObject.put("reference", "Organization/" + data.getOrganization_ihs());
            encounterObject.put("serviceProvider", serviceProviderObject);

            JSONArray identifierArray = new JSONArray();
            JSONObject identifierObject = new JSONObject();
            identifierObject.put("system", "http://sys-ids.kemkes.go.id/encounter/" + data.getOrganization_ihs());
            identifierObject.put("value", data.getRecord_uuid());
            identifierArray.put(identifierObject);
            encounterObject.put("identifier", identifierArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return encounterObject;
    }

    private static JSONObject createConditionObjectBody(MedicalSatuSehatModel data, RecordDiagnosaModel diagnosis, String encounterReference){
        JSONObject conditionObject = new JSONObject();
        try {
            conditionObject.put("resourceType", "Condition");

            JSONObject clinicalStatusObject = new JSONObject();
            JSONArray codingArray = new JSONArray();
            JSONObject codingObject = new JSONObject();
            codingObject.put("system", "http://terminology.hl7.org/CodeSystem/condition-clinical");
            codingObject.put("code", "active");
            codingObject.put("display", "Active");
            codingArray.put(codingObject);
            clinicalStatusObject.put("coding", codingArray);
            conditionObject.put("clinicalStatus", clinicalStatusObject);

            JSONArray categoryArray = new JSONArray();
            JSONObject categoryObject = new JSONObject();
            JSONArray categoryCodingArray = new JSONArray();
            JSONObject categoryCodingObject = new JSONObject();
            categoryCodingObject.put("system", "http://terminology.hl7.org/CodeSystem/condition-category");
            categoryCodingObject.put("code", "encounter-diagnosis");
            categoryCodingObject.put("display", "Encounter Diagnosis");
            categoryCodingArray.put(categoryCodingObject);
            categoryObject.put("coding", categoryCodingArray);
            categoryArray.put(categoryObject);
            conditionObject.put("category", categoryArray);

            JSONObject codeObject = new JSONObject();
            JSONArray codeCodingArray = new JSONArray();
            JSONObject codeCodingObject = new JSONObject();
            codeCodingObject.put("system", "http://hl7.org/fhir/sid/icd-10");
            codeCodingObject.put("code", diagnosis.getIcd_code());
            codeCodingObject.put("display", diagnosis.getIcd_name());
            codeCodingArray.put(codeCodingObject);
            codeObject.put("coding", codeCodingArray);
            conditionObject.put("code", codeObject);

            JSONObject subjectObject = new JSONObject();
            subjectObject.put("reference", "Patient/" + data.getPatient_ihs());
            subjectObject.put("display", data.getPatient_codename());
            conditionObject.put("subject", subjectObject);

            JSONObject encounterObject = new JSONObject();
            encounterObject.put("reference", "Encounter/"+encounterReference);
            conditionObject.put("encounter", encounterObject);

            conditionObject.put("onsetDateTime", Global.getDateFormatSatuSehat(data.getRecord_date()));

            conditionObject.put("recordedDate", Global.getDateFormatSatuSehat(data.getRecord_date()));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return conditionObject;
    }


    /**
     * Get practitioner IHS by NIK.
     **/
    public static void checkPractitionerByNIK(Activity activity, Runnable runTerdaftar, Runnable runTidakTerdaftar, String NIK) {
        JApplication.practitionerIHS = ""; //initial
        ProgressDialog loading = Global.createProgresSpinner(activity, activity.getString(R.string.loading));
        loading.show();
        String url = RoutesSatuSehat.url_practitioner + "?identifier=https://fhir.kemkes.go.id/id/nik|" + NIK;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                JSONArray entryArray = response.getJSONArray("entry");
                if (entryArray.length() > 0) {
                    JSONObject resourceObject = entryArray.getJSONObject(0).getJSONObject("resource");
                    JApplication.practitionerIHS = resourceObject.getString("id");
                    runTerdaftar.run();
                } else {
                    runTidakTerdaftar.run();
                }
                //dissmis
                loading.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
                showMessage(e.getMessage());
                loading.dismiss();
            }
        }, error -> {
            // Tangani kesalahan jaringan atau kegagalan permintaan
            loading.dismiss();
            NetworkResponse networkResponse = error.networkResponse;
            if (networkResponse != null) {
                handleErrorResponse(networkResponse.statusCode);
            } else {
                showMessage(INTERNAL_SERVER_ERROR);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + JApplication.getInstance().globalTable.getTokenSatuSehat());
                return headers;
            }
        };
        JApplication.getInstance().addToRequestQueue(jsonObjectRequest, Global.tag_json_obj);
    }

    /**
     * cek client id dan secret id dengan cara generate token satu sehat
     **/
    public static void checkClient(Activity activity, Runnable runSuccess, Runnable runFailed, String clientId, String clientSecret) {
        ProgressDialog loading = Global.createProgresSpinner(activity, activity.getString(R.string.loading));
        loading.show();

        String url = Routes.url_generate_token_satu_sehat()
                + "?client_id=" + clientId
                + "&client_secret=" + clientSecret
                + "&url=" + RoutesSatuSehat.GENERATE_TOKEN_URL;

        StringRequest strReq = new StringRequest(Request.Method.GET, url, response -> {
            try {
                if (response.contains(JConst.STATUS_API_SUCCESS)) {
                    JSONObject jsonResponse = new JSONObject(response).getJSONObject("success");
                    String token = jsonResponse.getString("access_token");
                    long last_update = jsonResponse.getLong("issued_at");

                    //save ke tabel
                    SatuSehatTokenTable satuSehatTokenTable = new SatuSehatTokenTable(activity);
                    satuSehatTokenTable.deleteAll();
                    satuSehatTokenTable.insert(new SatuSehatTokenModel(0, token, last_update));

                    runSuccess.run();
                    loading.dismiss();
                } else if (response.contains(JConst.STATUS_API_FAILED)) {
                    runFailed.run();
                    loading.dismiss();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                loading.dismiss();
            }
        }, error -> {
            // Tangani kesalahan jaringan atau kegagalan permintaan
            loading.dismiss();
            NetworkResponse networkResponse = error.networkResponse;
            if (networkResponse != null) {
                handleErrorResponse(networkResponse.statusCode);
            } else {
                showMessage(INTERNAL_SERVER_ERROR);
            }
        });
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }

}
