package com.orion.pasienqu_2.globals;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.activities.syncronize_data.SyncronizeDataActivity;
import com.orion.pasienqu_2.data_table.SyncInfoTable;
import com.orion.pasienqu_2.models.LoginCompanyModel;
import com.orion.pasienqu_2.models.SyncInfoModel;

import java.util.ArrayList;

public class SyncOdoo {
    public static void syncUpOdoo(Context appContext){
        if (!Global.CheckConnectionInternet(appContext)){
            return;
        }
        SyncInfoTable syncInfoTable = ((JApplication)appContext).syncInfoTable;
        ArrayList<SyncInfoModel> arrayList = syncInfoTable.getRecords();
        for (int i = 0; i < arrayList.size(); i++){
            //erik tutup odoo 040325
//            ((JApplication)appContext).odooConnection.upload(arrayList.get(i), appContext);
        }
    }

    public static void syncDownOdoo(Context appContext, Activity activity){
        appContext.startActivity(new Intent(appContext, SyncronizeDataActivity.class));

    }
}
