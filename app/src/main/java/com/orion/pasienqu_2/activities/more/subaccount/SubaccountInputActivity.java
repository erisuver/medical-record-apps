package com.orion.pasienqu_2.activities.more.subaccount;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.data_table.GlobalTable;
import com.orion.pasienqu_2.data_table.SubaccountTable;
import com.orion.pasienqu_2.globals.CustomAppCompatActivity;
import com.orion.pasienqu_2.globals.ShowDialog;
import com.orion.pasienqu_2.models.SubaccountModel;

import java.util.UUID;

public class SubaccountInputActivity extends CustomAppCompatActivity {
    private TextInputEditText txtName, txtEmail, txtPassword;
    private TextInputLayout errName, errEmail, errPassword;
    private Button btnSave, btnCancel;
    private String mode = "";
    private String uuid;
    private Menu menu;
    private SubaccountTable subaccountTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subaccount_input);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CreateView();
        InitClass();
        EventClass();
    }
    private void CreateView() {
        txtName = (TextInputEditText) findViewById(R.id.txtName);
        txtEmail = (TextInputEditText) findViewById(R.id.txtEmail);
        txtPassword = (TextInputEditText) findViewById(R.id.txtPassword);
        errName = (TextInputLayout) findViewById(R.id.layoutName);
        errEmail = (TextInputLayout) findViewById(R.id.layoutEmail);
        errPassword = (TextInputLayout) findViewById(R.id.layoutPassword);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        subaccountTable = ((JApplication) getApplicationContext()).subaccountTable;
    }

    private void InitClass(){
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
                if(saveForm()) {
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
    }

    private void loadData(){
        SubaccountModel data = subaccountTable.getDataByUuid(uuid);
        txtName.setText(data.getName());
        txtEmail.setText(data.getLogin());
        txtPassword.setText(data.getPassword());
    }

    private boolean isValid() {
        if (TextUtils.isEmpty(txtName.getText())) {
            errName.setError(getString(R.string.error_name));
        } else {
            errName.setErrorEnabled(false);
        }

        //email
        if (TextUtils.isEmpty(txtEmail.getText())) {
            errEmail.setError(getString(R.string.error_email));
        }else if(!TextUtils.isEmpty(txtEmail.getText()) && !Patterns.EMAIL_ADDRESS.matcher(txtEmail.getText()).matches()){
            errEmail.setError(getString(R.string.error_invalid_email));
        }else{
            errName.setErrorEnabled(false);
        }

        //passwrd
        if (TextUtils.isEmpty(txtPassword.getText())) {
            errPassword.setError(getString(R.string.error_password_req));
        }else if (txtPassword.getText().toString().length() < 6 ) {
            errPassword.setError(getString(R.string.error_password));
        }else{
            errPassword.setErrorEnabled(false);
        }

        return true;
    }

    private boolean saveForm() {
        if (this.isValid()) {
            if (uuid.equals("")) {
                uuid = UUID.randomUUID().toString();
            }
            String name = txtName.getText().toString().trim();
            String email = txtEmail.getText().toString().trim();
            String password = txtPassword.getText().toString().trim();

            switch(this.mode.toString()){
                case "add":{
                    SubaccountModel newData = new SubaccountModel(0, uuid, name, email, password);
                    if(!subaccountTable.insert(newData, true)){
                        return false;
                    }
                    break;
                }
                case "edit":{
                    SubaccountModel data = subaccountTable.getDataByUuid(uuid);

                    data.setName(name);
                    data.setLogin(email);
                    data.setPassword(password);
                    if(!subaccountTable.update(data, true)){
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
            this.setTitle(String.format(getString(R.string.add_title), getString(R.string.sub_accounts)));
        } else if (mode.equals("edit")) {
            this.setTitle(String.format(getString(R.string.edit_title), getString(R.string.sub_accounts)));
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
        GlobalTable globalTable = ((JApplication) getApplicationContext()).globalTable;

        switch (item.getItemId()) {
            case R.id.menu_archive:
                Runnable runArchive = new Runnable() {
                    @Override
                    public void run() {
                        globalTable.archive("pasienqu_subaccount", uuid, "res.users");
                        finish();
                    }
                };
                ShowDialog.confirmDialog(SubaccountInputActivity.this, getString(R.string.archive),
                        String.format(getString(R.string.confirm_archive), getString(R.string.sub_accounts)), runArchive);
                return true;

            case R.id.menu_unarchive:
                Runnable runUnarchive = new Runnable() {
                    @Override
                    public void run() {
                        globalTable.unarchive("pasienqu_subaccount", uuid, "res.users");
                        finish();
                    }
                };
                ShowDialog.confirmDialog(SubaccountInputActivity.this, getString(R.string.unarchive),
                        String.format(getString(R.string.confirm_unarchive), getString(R.string.sub_accounts)), runUnarchive);
                return true;

            case android.R.id.home:
                onBackPressed();

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        menu.getItem(0).setVisible(false);
        menu.getItem(1).setVisible(false);

        if(this.mode.equals("edit")) {
            GlobalTable globalTable = ((JApplication) getApplicationContext()).globalTable;
            boolean isArchive = globalTable.isArchived("pasienqu_subaccount", uuid);
            menu.getItem(0).setVisible(!isArchive);
            menu.getItem(1).setVisible(isArchive);
        }
        return true;
    }

}