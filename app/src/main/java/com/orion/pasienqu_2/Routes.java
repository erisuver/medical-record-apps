package com.orion.pasienqu_2;

public class Routes {
//    public static String URL_API = "http://192.168.0.6/pasienqu_api/";
//    public static String IP_ADDRESS = "http://192.168.18.5";
    public static String DEFAULT_IP = "https://pasienqu.com";//"http://192.168.18.5";//
    public static String IP_ADDRESS = "https://orionbdg.xyz";
    public static String NAMA_API = "pasienqu_api";
    public static String NAMA_API_PASIENQU_ONLINE = "pasienqu_online_api";
    public static String URL_API_AWAL = IP_ADDRESS + "/"+NAMA_API+"/public/";
//    public static String URL_GET_REAL_API = IP_ADDRESS + "/internal_orion/public/setting_ip/get_real_ip_address?nama_api="+NAMA_API;
    public static String URL_GET_REAL_API = "http://orionbdg.xyz/internal_orion/public/setting_ip/get_rjkeal_ip_address?nama_api="+NAMA_API;
    public static String URL_DRIVE_PROFILE = "https://drive.usercontent.google.com/uc?id=1xMbmvRtn8OuQYG-jkjYbwCL-IM5QbeMllKH&export=download";

//    public static String URL_API = "http://35.213.156.70/pasienqu_api/";
//    public static String URL_API = "pasienqu_api/";
//    Application.getInstance().real_url +

//    public static String URL_API = "http://orionbdg.xyz/pasienqu_api/";

//    public static String URL = URL_API + "public/";
    public static String url_folder_file(){
        return JApplication.getInstance().real_url +  "uploads/backup_db/";
   }  
    public static String url_file (){
        return JApplication.getInstance().real_url + "pasienqu.db";
   }

    //log pembayaran
    public static String url_save_log_pembayaran (){
        return JApplication.getInstance().real_url + "engine/save_log_pembayaran";
    }
    public static String url_update_status_log_pembayaran (){
        return JApplication.getInstance().real_url + "engine/update_status_log_pembayaran";
    }
    public static String url_delete_log_pembayaran (){
        return JApplication.getInstance().real_url + "engine/delete_log_pembayaran";
    }


    //auth - data users
    public static String url_signup_new_member (){
        return JApplication.getInstance().real_url + "auth/signup_new_member";
    }
    public static String url_login (){
        return JApplication.getInstance().real_url + "auth/login";
    }
    public static String url_login2 (){
        return JApplication.getInstance().real_url + "auth/login2";
    }
    public static String url_change_password (){
        return JApplication.getInstance().real_url + "auth/change_password";
    }
    public static String url_forget_password (){
        return JApplication.getInstance().real_url + "auth/forget_password";
    }
    public static String url_forget_pin (){
        return JApplication.getInstance().real_url + "auth/forget_pin";
    }
    public static String url_migrasi_data_odoo (){
        return JApplication.getInstance().real_url + "auth/migrasi_data_odoo";
    }
    public static String url_cek_email (){
        return JApplication.getInstance().real_url + "auth/cek_email";
    }

    //company - profile - data akun
    public static String url_get_company (){
        return JApplication.getInstance().real_url + "company/get_company";
    }
    public static String url_update_company (){
        return JApplication.getInstance().real_url + "company/update_company";
    }
    public static String url_renew_membership (){
        return JApplication.getInstance().real_url + "company/renew_membership";
    }

    //general setting
    public static String url_cek_versi (){
        return JApplication.getInstance().real_url + "general_setting/cek_versi";
    }


    public static String url_migrasi_data_upload (){
        String urlRestore = JApplication.getInstance().real_url.replace(NAMA_API, NAMA_API_PASIENQU_ONLINE);
        return urlRestore + "db/restore";
    }


    public static String url_migrasi_media (){
        String urlRestore = JApplication.getInstance().real_url.replace(NAMA_API, NAMA_API_PASIENQU_ONLINE);
        return urlRestore + "media/migrasi";
    }

    public static String url_check_data_user (){
        String urlRestore = JApplication.getInstance().real_url.replace(NAMA_API, NAMA_API_PASIENQU_ONLINE);
        return urlRestore + "db/get_count";
    }

    public static String url_generate_token_satu_sehat (){
        return JApplication.getInstance().real_url + "auth_satu_sehat/generate_token_satu_sehat";
    }

    public static String url_help() {
        return JApplication.getInstance().real_url + "Petunjuk_Penggunaan_Satu_Sehat_Aplikasi_PasienQu_Pro.pdf";
    }

    public static String url_kirim_database (){
        return JApplication.getInstance().real_url + "engine/kirim_db";
    }
}
