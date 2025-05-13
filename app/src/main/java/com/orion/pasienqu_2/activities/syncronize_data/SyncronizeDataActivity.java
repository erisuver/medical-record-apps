package com.orion.pasienqu_2.activities.syncronize_data;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.data_table.SyncDownTable;
import com.orion.pasienqu_2.globals.CustomAppCompatActivity;
import com.orion.pasienqu_2.globals.SyncDown;

public class SyncronizeDataActivity extends CustomAppCompatActivity {
    private TextView txtLabel;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_progress);
        CreateView();
        InitClass();
        EventClass();
    }


    private void CreateView(){
        txtLabel = (TextView) findViewById(R.id.txtLabel);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    private void InitClass(){
        //isi gender
        txtLabel.setText("");
        progressBar.setProgress(0);
    }

    private void EventClass(){
    }

    private void syncData(){
//        onBackPressed();
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
////                onBackPressed();
//            }
//        };
//        SyncUp.sync_all(SyncronizeDataActivity.this, getApplicationContext(), runnable);
//
//        LoginCompanyModel loginCompanyModel = JApplication.getInstance().loginCompanyModel;
//        final String filterCompanyId = "?filter=[[\"company_id\",\"=\",\""+loginCompanyModel.getId()+"\"]]";
//
//        Runnable runnable0 = new Runnable() {
//            @Override
//            public void run() {
//                Runnable runnable1 = new Runnable() {
//                    @Override
//                    public void run() {
//                        Runnable runnable2 = new Runnable() {
//                            @Override
//                            public void run() {
//                                Runnable runnable3 = new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Runnable runnable4 = new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                Runnable runnable5 = new Runnable() {
//                                                    @Override
//                                                    public void run() {
//                                                        Runnable runnable6 = new Runnable() {
//                                                            @Override
//                                                            public void run() {
//                                                                Runnable runnable7 = new Runnable() {
//                                                                    @Override
//                                                                    public void run() {
//                                                                        Runnable runnable8 = new Runnable() {
//                                                                            @Override
//                                                                            public void run() {
//                                                                                Runnable runnable9 = new Runnable() {
//                                                                                    @Override
//                                                                                    public void run() {
//                                                                                        Runnable runnable10 = new Runnable() {
//                                                                                            @Override
//                                                                                            public void run() {
//                                                                                                Runnable runnable11 = new Runnable() {
//                                                                                                    @Override
//                                                                                                    public void run() {
//                                                                                                        Runnable runnable12 = new Runnable() {
//                                                                                                            @Override
//                                                                                                            public void run() {
//                                                                                                                onBackPressed();
//                                                                                                            }
//                                                                                                        };
//
//                                                                                                        SyncDown.syncDown(SyncronizeDataActivity.this, getApplicationContext(), "pasienqu.res.users", progressBar, runnable12, filterCompanyId);
//                                                                                                    }
//                                                                                                };
//
//                                                                                                SyncDown.syncDown(SyncronizeDataActivity.this, getApplicationContext(), "pasienqu.medical.record.file", progressBar, runnable11, filterCompanyId);
//                                                                                            }
//                                                                                        };
//
//                                                                                        SyncDown.syncDown(SyncronizeDataActivity.this, getApplicationContext(), "pasienqu.medical.record.diagnosa", progressBar, runnable10, filterCompanyId);
//                                                                                    }
//                                                                                };
//
//                                                                                SyncDown.syncDown(SyncronizeDataActivity.this, getApplicationContext(), "pasienqu.medical.record", progressBar, runnable9, filterCompanyId);
//                                                                            }
//                                                                        };
//
//                                                                        SyncDown.syncDown(SyncronizeDataActivity.this, getApplicationContext(), "pasienqu.billing.items", progressBar, runnable8, filterCompanyId);
//                                                                    }
//                                                                };
//
//                                                                SyncDown.syncDown(SyncronizeDataActivity.this, getApplicationContext(), "pasienqu.appointment", progressBar, runnable7, filterCompanyId);
//                                                            }
//                                                        };
//
//                                                        SyncDown.syncDown(SyncronizeDataActivity.this, getApplicationContext(), "pasienqu.billing", progressBar, runnable6, filterCompanyId);
//                                                    }
//                                                };
//
//                                                SyncDown.syncDown(SyncronizeDataActivity.this, getApplicationContext(), "pasienqu.icd10", progressBar, runnable5, filterCompanyId);
//                                            }
//                                        };
//
//                                        SyncDown.syncDown(SyncronizeDataActivity.this, getApplicationContext(), "pasienqu.country", progressBar, runnable4, filterCompanyId);
//                                    }
//                                };
//
//                                SyncDown.syncDown(SyncronizeDataActivity.this, getApplicationContext(), "pasienqu.patient", progressBar, runnable3, filterCompanyId);
//                            onBackPressed();
//                            }
//                        };
//                        SyncDown.syncDown(SyncronizeDataActivity.this, getApplicationContext(), "pasienqu.note.template", progressBar, runnable2, filterCompanyId);
//                    }
//                };
//
//                SyncDown.syncDown(SyncronizeDataActivity.this, getApplicationContext(), "pasienqu.work.location", progressBar, runnable1, filterCompanyId);
//            }
//        };
//
//        String filter = "";
//        SyncDown.syncDown(SyncronizeDataActivity.this, getApplicationContext(), "res.gender", progressBar, runnable0, filter);

//        SyncUp.sync_all(SyncronizeDataActivity.this, getApplicationContext(), () -> {});

        SyncDownTable syncDownTable = JApplication.getInstance().syncDownTable;
        syncDownTable.update_isSync_all("T");
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                onBackPressed();
            }
        };
        SyncDown.sync_all(SyncronizeDataActivity.this, getApplicationContext(), runnable, progressBar, txtLabel);
    }



    @Override
    public void onBackPressed() {
        Intent intent = SyncronizeDataActivity.this.getIntent();
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        syncData();
    }
}

