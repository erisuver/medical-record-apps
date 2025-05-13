package com.orion.pasienqu_2.activities.more.billing_template;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.adapter.more.BillingTemplateItemAdapter;
import com.orion.pasienqu_2.data_table.GlobalTable;
import com.orion.pasienqu_2.data_table.more.BillingTemplateTable;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.CustomAppCompatActivity;
import com.orion.pasienqu_2.globals.ShowDialog;
import com.orion.pasienqu_2.globals.SyncUp;
import com.orion.pasienqu_2.models.BillingItemModel;
import com.orion.pasienqu_2.models.BillingTemplateModel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BillingTemplateInputActivity extends CustomAppCompatActivity {
    private TextInputEditText txtName, txtLabel;
    private TextInputLayout errName;
    private Button btnSave, btnCancel;
    private ImageButton btnAddItem;
    private String mode = "";
    private String uuid;
    private int idTemplate;
    private Menu menu;
    private BillingTemplateTable billingTemplateTable;
    private String label = "";

    public List<BillingItemModel> ListItems = new ArrayList<>();
    private List<String> listLabel;

    private RecyclerView rcvItem;
    private BillingTemplateItemAdapter mAdapter;
    private long mLastClickTime = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing_template_input);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CreateView();
        InitClass();
        EventClass();
    }
    private void CreateView() {
        txtName = (TextInputEditText) findViewById(R.id.txtName);
        errName = (TextInputLayout) findViewById(R.id.layoutName);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnAddItem = (ImageButton) findViewById(R.id.btnAddItem);
        rcvItem = (RecyclerView) findViewById(R.id.rcvItem);
        billingTemplateTable = ((JApplication) getApplicationContext()).billingTemplateTable;
        mAdapter = new BillingTemplateItemAdapter(BillingTemplateInputActivity.this, ListItems, R.layout.billing_template_input_list_item);
        View child = getLayoutInflater().inflate(R.layout.billing_template_input_list_item, null);
        txtLabel = (TextInputEditText) child.findViewById(R.id.txtLabel);
    }

    private void InitClass(){
        rcvItem.setLayoutManager(new GridLayoutManager(BillingTemplateInputActivity.this, 1, GridLayoutManager.VERTICAL, false));
        rcvItem.setAdapter(mAdapter);
        idTemplate = 0;

        Bundle extra = this.getIntent().getExtras();
        uuid = extra.getString("uuid");
        if (uuid.equals("")){
            mode   = "add";
        }else{
            mode   = "edit";
            loadData();
        }

        this.setTitleInput();
        invalidateOptionsMenu();
    }

    private void EventClass(){
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //fungsi mencegah duplicate save karena btnSave.setEnabled(false) tidak berhasil
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                if(saveForm()) {
//                    syncUpData();
                    onBackPressed();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddItems();
            }
        });
    }

    public void AddItems() {
        BillingItemModel data = new BillingItemModel();
        mAdapter.addModel(data);
//
        mAdapter.notifyDataSetChanged();
    }


    private void loadData(){
        Bundle extra = this.getIntent().getExtras();
        BillingTemplateModel data = billingTemplateTable.getDataByUuid(uuid);

        idTemplate = data.getId();
        txtName.setText(data.getName());

        String items = data.getItems();
        if (!items.equals("['']")) {
            String item1 = items.replaceAll("'", "").replace("[", "").replace("]", "");
            String[] itemStringlist = item1.split(",");
            for (int i = 0; i < itemStringlist.length; i++) {
                String item2 = itemStringlist[i];
                BillingItemModel model = new BillingItemModel();
                model.setName(item2);
                mAdapter.addModel(model);
                mAdapter.notifyDataSetChanged();
            }
        }

//        JSONArray temp = null;
//        try {
//            temp = new JSONArray(items);
//            String[] itemStringlist = temp.join(",").split(",");
//            for (int i = 0; i < itemStringlist.length; i++){
//                String item = itemStringlist[i];
//                item.replace("\"", "");
//                BillingItemModel model = new BillingItemModel();
//                model.setName(item);
//                mAdapter.addModel(model);
//                mAdapter.notifyDataSetChanged();
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

    }



    private boolean isValid() {
        if (TextUtils.isEmpty(txtName.getText())) {
            errName.setError(getString(R.string.error_name));
        } else {
            errName.setErrorEnabled(false);
        }

        return true;
    }


    private boolean saveForm() {
        if (this.isValid()) {
            if (idTemplate == 0) {
                uuid = UUID.randomUUID().toString();
            }
            String name = txtName.getText().toString().trim();

            String items = "['";
            for (int i = 0; i < ListItems.size(); i++){
                if (i>0){
                    items = items + "','";
                }
                items = items + ListItems.get(i).getName().trim();
            }
            items = items +"']";

//            for (int i = 0; i < ListItems.size(); i++){
//                if (i>0){
//                    items = items + "#";
//                }
//                items = items + ListItems.get(i).getName();
//            }

            switch (this.mode.toString()) {
                case "add": {
                    BillingTemplateModel newData;
                    newData = new BillingTemplateModel(0, uuid, name, items);
                    newData.setMode(this.mode);

                    if(!billingTemplateTable.insert(newData, true)){
                        return false;
                    }
                    break;
                }

                case "edit": {
                    BillingTemplateModel Data = billingTemplateTable.getDataByUuid(uuid);
                    Data.setName(name);
                    Data.setItems(items);
                    Data.setMode(this.mode);

                    if(!billingTemplateTable.update(Data)){
                        return false;
                    }
                    break;
                }
            }
            return true;
        }
        return false;
    }



    private void setTitleInput() {
        if (mode.equals("add")) {
            this.setTitle(String.format(getString(R.string.add_title), getString(R.string.billing_templates)));
        } else if (mode.equals("edit")) {
            this.setTitle(String.format(getString(R.string.edit_title), getString(R.string.billing_templates)));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_archive_unarchive, menu);
        this.menu = menu;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final GlobalTable globalTable = ((JApplication) getApplicationContext()).globalTable;

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_archive:
                Runnable runArchive = new Runnable() {
                    @Override
                    public void run() {
                        globalTable.archive("pasienqu_billing_template", uuid, "pasienqu.billing.template");
                        finish();
                    }
                };
                ShowDialog.confirmDialog(BillingTemplateInputActivity.this, getString(R.string.archive),
                        String.format(getString(R.string.confirm_archive), getString(R.string.billing_templates)), runArchive);
                return true;
            case R.id.menu_unarchive:
                Runnable runUnarchive = new Runnable() {
                    @Override
                    public void run() {
                        globalTable.unarchive("pasienqu_billing_template", uuid, "pasienqu.billing.template");
                        finish();
                    }
                };
                ShowDialog.confirmDialog(BillingTemplateInputActivity.this, getString(R.string.unarchive),
                        String.format(getString(R.string.confirm_unarchive), getString(R.string.billing_templates)), runUnarchive);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        GlobalTable globalTable = ((JApplication) getApplicationContext()).globalTable;
        boolean isArchive = globalTable.isArchived("pasienqu_billing_template", uuid);
        menu.getItem(0).setVisible(false);
        menu.getItem(1).setVisible(false);

        if (mode.equals("edit")) {
            menu.getItem(0).setVisible(!isArchive);
            menu.getItem(1).setVisible(isArchive);
        }
        return true;
    }

    private void syncUpData(){
        if(Global.CheckConnectionInternet(BillingTemplateInputActivity.this)){
            SyncUp.sync_all(BillingTemplateInputActivity.this, getApplicationContext(), ()->{});
        }
    }

}