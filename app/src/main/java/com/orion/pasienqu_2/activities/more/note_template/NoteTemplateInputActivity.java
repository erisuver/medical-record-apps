package com.orion.pasienqu_2.activities.more.note_template;

import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.activities.more.billing_template.BillingTemplateInputActivity;
import com.orion.pasienqu_2.data_table.GlobalTable;
import com.orion.pasienqu_2.data_table.more.NoteTemplateTable;
import com.orion.pasienqu_2.globals.CustomAppCompatActivity;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.ListValue;
import com.orion.pasienqu_2.globals.ShowDialog;
import com.orion.pasienqu_2.globals.SyncUp;
import com.orion.pasienqu_2.models.more.NoteTemplateModel;

import java.util.List;
import java.util.UUID;

import androidx.appcompat.app.AppCompatActivity;

public class NoteTemplateInputActivity extends CustomAppCompatActivity {
    private TextInputEditText txtName, txtTemplate;
    private Button btnSave, btnCancel;
    private NoteTemplateTable noteTemplateTable;
    private TextInputLayout errName, errTemplate;
    private Spinner spinCategory;
    private NoteTemplateModel noteTemplateModel;
    private int idTemplate;
    private String mode = "";
    private String uuid = "";
    private List<String> listValue;
    private List<String> listLabel;
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_template_input);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CreateView();
        InitializeView();
        EventClass();
    }

    private void CreateView() {
        txtName = (TextInputEditText) findViewById(R.id.txtName);
        txtTemplate = (TextInputEditText) findViewById(R.id.txtTemplate);
        spinCategory = (Spinner) findViewById(R.id.spinCategory);
        errName = (TextInputLayout) findViewById(R.id.layoutName);
        errTemplate = (TextInputLayout) findViewById(R.id.layoutTemplate);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        noteTemplateTable = ((JApplication) getApplicationContext()).noteTemplateTable;
        noteTemplateModel = new NoteTemplateModel();
        idTemplate = 0;
    }

    private void InitializeView() {
        listValue = ListValue.list_value_template_category(NoteTemplateInputActivity.this);
        listLabel = ListValue.list_template_category(NoteTemplateInputActivity.this);

        ArrayAdapter adapterCategory = new ArrayAdapter<>(this, R.layout.spinner_item_style,
                listLabel);
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinCategory.setAdapter(adapterCategory);
        spinCategory.setSelection(5);

        Bundle extra = this.getIntent().getExtras();
        uuid = extra.getString("uuid");
        if (uuid.equals("")) {
            mode = "add";
        } else {
            mode = "edit";
            loadData();
        }


        this.setTitleInput();
        invalidateOptionsMenu();
    }

    private void setTitleInput() {
        if (mode.equals("add")) {
            this.setTitle(String.format(getString(R.string.add_title), getString(R.string.note_template_one)));
        } else if (mode.equals("edit")) {
            this.setTitle(String.format(getString(R.string.edit_title), getString(R.string.note_template_one)));
        }
    }

    private void loadData() {
        NoteTemplateModel dataNote = noteTemplateTable.getDataByUuid(uuid);
        idTemplate = dataNote.getId();

        txtName.setText(dataNote.getName());
        for (int i = 0; i < listValue.size(); i++) {
            if (listValue.get(i).equals(dataNote.getCategory())) {
                spinCategory.setSelection(i);
            }
        }
        txtTemplate.setText(dataNote.getTemplate());
    }

    private void EventClass() {

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //fungsi mencegah duplicate save karena btnSave.setEnabled(false) tidak berhasil
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                if (saveForm()) {
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

    }

    private boolean isValid() {
        if (TextUtils.isEmpty(txtName.getText())) {
            errName.setError(getString(R.string.error_name));
        } else {
            errName.setErrorEnabled(false);
        }

        if (TextUtils.isEmpty(txtTemplate.getText())) {
            errTemplate.setError(getString(R.string.error_template));
        } else {
            errTemplate.setErrorEnabled(false);
        }
        return true;
    }

    private boolean saveForm() {
        if (this.isValid()) {
            String name = txtName.getText().toString().trim();
            int idxCategory = spinCategory.getSelectedItemPosition();
            String category = listValue.get(idxCategory);
            String template = txtTemplate.getText().toString().trim();
            if (uuid.equals("")) {
                uuid = UUID.randomUUID().toString();
            }

            switch (this.mode.toString()) {
                case "add": {
                    NoteTemplateModel newData = new NoteTemplateModel(0, uuid, name, category, template, JApplication.getInstance().loginCompanyModel.getId());
                    if (!noteTemplateTable.insert(newData, true)) {
                        return false;
                    }
                    break;
                }

                case "edit": {
                    NoteTemplateModel Data = noteTemplateTable.getDataByUuid(uuid);

                    Data.setName(name);
                    Data.setCategory(category);
                    Data.setTemplate(template);

                    if (!noteTemplateTable.update(Data)) {
                        return false;
                    }
                    break;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        GlobalTable globalTable;
        globalTable = ((JApplication) getApplicationContext()).globalTable;
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_archive:
                Runnable runArchive = new Runnable() {
                    @Override
                    public void run() {
                        globalTable.archive("pasienqu_note_template", uuid, "pasienqu.note.template");
                        finish();
                    }
                };
                ShowDialog.confirmDialog(NoteTemplateInputActivity.this, getString(R.string.archive),
                        String.format(getString(R.string.confirm_archive), getString(R.string.note_template_one)), runArchive);
                return true;
            case R.id.menu_unarchive:
                Runnable runUnarchive = new Runnable() {
                    @Override
                    public void run() {
                        globalTable.unarchive("pasienqu_note_template", uuid, "pasienqu.note.template");
                        finish();
                    }
                };
                ShowDialog.confirmDialog(NoteTemplateInputActivity.this, getString(R.string.unarchive),
                        String.format(getString(R.string.confirm_unarchive), getString(R.string.note_template_one)), runUnarchive);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_archive_unarchive, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.getItem(0).setVisible(false); //item archive
        menu.getItem(1).setVisible(false); //item unarchive

        if (mode.equals("edit")) {
            GlobalTable globalTable = ((JApplication) getApplicationContext()).globalTable;
            boolean isArchive = globalTable.isArchived("pasienqu_note_template", uuid);
            menu.getItem(0).setVisible(!isArchive);
            menu.getItem(1).setVisible(isArchive);
        }
        return true;
    }

    private void syncUpData(){
        if(Global.CheckConnectionInternet(NoteTemplateInputActivity.this)){
            SyncUp.sync_all(NoteTemplateInputActivity.this, getApplicationContext(), ()->{});
        }
    }

}
