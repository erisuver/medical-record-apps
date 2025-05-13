package com.orion.pasienqu_2.utility;

import android.app.Activity;
import android.app.ProgressDialog;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.Routes;
import com.orion.pasienqu_2.activities.home.home;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.ShowDialog;

import java.util.HashMap;

public class GetIPAddress {

    public static void get_ip_address(Activity activity, Runnable runnable){
        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false); // Biarkan false jika Anda ingin mencegah pengguna menutupnya
        progressDialog.show();
        //pertama cobain konek ke ip sesuai sharedfreef
        Runnable runSukses = new Runnable() {
            @Override
            public void run() {
                Global.dismissProgressDialog(activity, progressDialog);
                JApplication.getInstance().isGetUrl = true;
                runnable.run();
            }
        };

        Runnable runGagal = new Runnable() {
            @Override
            public void run() {
                Global.dismissProgressDialog(activity, progressDialog);
                /*Runnable runRetry = new Runnable() {
                    @Override
                    public void run() {
                        get_ip_address();
                    }
                };
                ShowDialog.infoDialogWithRunnable(JApplication.currentActivity, activity.getString(R.string.app_name), activity.getString(R.string.error_connection_timeout), runRetry);
*/
            }
        };
        Runnable runAmbilIPDariDrive = new Runnable() {
            @Override
            public void run() {
                JApplication.getInstance().real_url = Routes.URL_API_AWAL;
                String url = Routes.URL_DRIVE_PROFILE;
                StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String ip = cariNilai(response, Routes.NAMA_API);
                        ip = decryptNew(ip);
                        ip = "http://"+ip;
                        cek_koneksi(ip, runSukses, runGagal);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        runGagal.run();
                    }
                });
                JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
            }
        };

        Runnable runAmbilIPOrionbdg = new Runnable() {
            @Override
            public void run() {
                JApplication.getInstance().real_url = Routes.URL_API_AWAL;
                String url = Routes.URL_GET_REAL_API;
                StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.contains("Hello")){
                            runAmbilIPDariDrive.run();
                        }else{
                            cek_koneksi(response, runSukses, runAmbilIPDariDrive);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        runAmbilIPDariDrive.run();
                    }
                });
                JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
            }
        };

//        String ip = JApplication.getInstance().sharedPreferencesSetting.getString("ip", "");
//        cek_koneksi(ip, runSukses, runAmbilIPOrionbdg);
        String ip = Routes.DEFAULT_IP;//"https://pasienqu.com";//JApplication.getInstance().sharedPreferencesSetting.getString("ip", "");
        cek_koneksi(ip, runSukses, runAmbilIPOrionbdg);
    }


    public static String cariNilai(String input, String cari) {
        // Split string berdasarkan newline
        String[] lines = input.split("\n");

        // Loop melalui setiap baris
        for (String line : lines) {
            // Split baris berdasarkan tanda sama (=)
            String[] parts = line.split("=");
            if (parts.length == 2 && parts[0].equals(cari)) {
                // Jika kunci ditemukan, kembalikan nilainya
                return parts[1];
            }
        }

        // Kembalikan string kosong jika kunci tidak ditemukan
        return "";
    }

    private static void cek_koneksi(String url, Runnable runSukses, Runnable runGagal){
        String link = url+"/"+Routes.NAMA_API+"/public/cek_koneksi.php";
        StringRequest strReq = new StringRequest(Request.Method.GET, link, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JApplication.getInstance().real_url = url;
                JApplication.getInstance().updateIPSharePref(url);
                JApplication.getInstance().real_url = JApplication.getInstance().real_url+"/"+Routes.NAMA_API+"/public/";
                runSukses.run();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                runGagal.run();
            }
        });
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }

    public static String decryptNew(String input) {
        HashMap<String, String> list = new HashMap<>();
        list.put("e9r", "a");
        list.put("asF", "b");
        list.put("ytH", "c");
        list.put("43y", "d");
        list.put("nSu", "e");
        list.put("fdQ", "f");
        list.put("uyo", "g");
        list.put("jhr", "h");
        list.put("vgb", "i");
        list.put("nm7", "j");
        list.put("cvF", "k");
        list.put("yKV", "l");
        list.put("k93", "m");
        list.put("fdk", "n");
        list.put("vfd", "o");
        list.put("34g", "p");
        list.put("320", "q");
        list.put("eds", "r");
        list.put("ehj", "s");
        list.put("ebv", "t");
        list.put("etr", "u");
        list.put("w94", "v");
        list.put("vcf", "w");
        list.put("pty", "x");
        list.put("j9f", "y");
        list.put("xje", "z");
        list.put("e92", "A");
        list.put("asD", "B");
        list.put("yFH", "C");
        list.put("4Xy", "D");
        list.put("n1u", "E");
        list.put("GdQ", "F");
        list.put("Yyo", "G");
        list.put("jJr", "H");
        list.put("v7b", "I");
        list.put("NM7", "J");
        list.put("c0F", "K");
        list.put("QeV", "L");
        list.put("kg3", "M");
        list.put("jhk", "N");
        list.put("cir", "O");
        list.put("dZi", "P");
        list.put("kR1", "Q");
        list.put("fdd", "R");
        list.put("jLi", "S");
        list.put("f9w", "T");
        list.put("DYx", "U");
        list.put("vde", "V");
        list.put("akb", "W");
        list.put("dsf", "X");
        list.put("gfv", "Y");
        list.put("jul", "Z");
        list.put("cHs", "0");
        list.put("dIe", "1");
        list.put("lgu", "2");
        list.put("mIe", "3");
        list.put("SuM", "4");
        list.put("heN", "5");
        list.put("32x", "6");
        list.put("efr", "7");
        list.put("fd8", "8");
        list.put("fUK", "9");

        StringBuilder hasil = new StringBuilder();
        StringBuilder cari = new StringBuilder();
        int charke = 0;

        for (int i = 0; i < input.length(); i++) {
            char currentChar = input.charAt(i);
            if (Character.isLetterOrDigit(currentChar)) {
                charke++;
                cari.append(currentChar);
                if (charke == 3) {
                    String temp = list.get(cari.toString());
                    if (temp == null || temp.isEmpty()) {
                        temp = cari.toString();
                    }
                    hasil.append(temp);
                    cari = new StringBuilder();
                    charke = 0;
                }
            } else {
                hasil.append(currentChar);
            }
        }

        return hasil.toString();
    }
}
