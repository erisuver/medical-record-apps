package com.orion.pasienqu_2.activities.more.export;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.activities.more.import_data_v1.ImportDataV1Activity;
import com.orion.pasienqu_2.data_table.BillingItemTable;
import com.orion.pasienqu_2.data_table.BillingTable;
import com.orion.pasienqu_2.data_table.PatientTable;
import com.orion.pasienqu_2.data_table.RecordTable;
import com.orion.pasienqu_2.data_table.WorkLocationTable;
import com.orion.pasienqu_2.globals.CustomAppCompatActivity;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.ShowDialog;
import com.orion.pasienqu_2.models.BillingItemModel;
import com.orion.pasienqu_2.models.BillingModel;
import com.orion.pasienqu_2.models.PatientModel;
import com.orion.pasienqu_2.models.RecordModel;
import static com.orion.pasienqu_2.JApplication.df1;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class ExportActivity2 extends CustomAppCompatActivity {
    private Button btnExport;
    private final String locationExport = Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DOCUMENTS + "/PasienQu/Export/";
    private String exportFileName = "";
    private final int REQUEST_CODE_ASK_PERMISSIONS = 1;

    private PatientTable patientTable;
    private RecordTable recordTable;
    private BillingTable billingTable;
    private WorkLocationTable workLocationTable ;
    private BillingItemTable billingItemTable;

    private List<PatientModel> listPatient = new ArrayList<>();
    private List<RecordModel> listRecord = new ArrayList<>();
    private List<BillingModel> listBilling = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export2);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.export_excel));
        CreateView();
        InitClass();
        EventClass();

    }

    private void CreateView(){
        btnExport = (Button) findViewById(R.id.btnExport);

        patientTable = new PatientTable(this);
        recordTable = new RecordTable(this);
        billingTable = new BillingTable(this);
        workLocationTable = new WorkLocationTable(this);
        billingItemTable = new BillingItemTable(this);
    }

    private void InitClass(){
    }

    private void EventClass(){

        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                export();
                GetPermissionExport();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    //export
    private void exportOfflines(){
        File folder = new File(locationExport);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String fileName = "PasienQu" + Global.serverNowFormated4Ekspor() + ".xls";
        String file = locationExport + fileName;
        exportFileName = file;

        //progress
        int maxProgress = 0;
        int maxPatient = patientTable.getTotalDatas();
        int maxMedic = recordTable.getTotalDatas();
        int maxBilling = billingTable.getTotalDatas();
        maxProgress = maxPatient + maxMedic + maxBilling;

        ProgressDialog progressDialog = new ProgressDialog(ExportActivity2.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage(getString(R.string.export_excel));
        progressDialog.setCancelable(false);
        progressDialog.setMax(maxProgress);
        progressDialog.setProgressNumberFormat(null);
        progressDialog.show();

        Thread mThread =
            new Thread() {
                @Override
                public void run() {
                    createExcelSheet(file, progressDialog);
                    progressDialog.dismiss();

                    Intent intent = new Intent(ExportActivity2.this, ExportSuccessActivity.class);
                    intent.putExtra("file_name", exportFileName);
                    intent.putExtra("name", fileName);
                    intent.putExtra("location", locationExport);
                    intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            };
        mThread.start();

    }


    private void createExcelSheet(String fileName, ProgressDialog progressDialog){
        int progress = 0;

        File sdCard = Environment.getExternalStorageDirectory();
        File file = new File (fileName);
        try {
            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale("in", "ID"));

            WritableCellFormat cellFormat = new WritableCellFormat();
            cellFormat.setWrap(true);

            WritableWorkbook workbook;
            int a = 1;
            workbook = Workbook.createWorkbook(file, wbSettings);
            WritableSheet sheet1 = workbook.createSheet(getString(R.string.title_home_patient_text), 0);
            WritableSheet sheet2 = workbook.createSheet(getString(R.string.title_home_medical_record_text), 1);
            WritableSheet sheet3 = workbook.createSheet(getString(R.string.title_billing), 2);
            int i;

            //sheet patient
            Label patient_id, firstName, surname, gender, age, dateofBirth, registerDate, idNumber,
                    email, occupation, contact, address1, address2, patientRemark1, patientRemark2, patientTypeId, description;

            //header cell
            patient_id      = new Label(0, 0, getString(R.string.patient_id));
            firstName       = new Label(1, 0, getString(R.string.first_name));
            surname         = new Label(2, 0, getString(R.string.surname));
            gender          = new Label(3, 0, getString(R.string.gender));
            age             = new Label(4, 0, getString(R.string.age));
            dateofBirth     = new Label(5, 0, getString(R.string.date_of_birth));
            registerDate    = new Label(6, 0, getString(R.string.register_date));
            idNumber        = new Label(7, 0, getString(R.string.identification_number));
            email           = new Label(8, 0, getString(R.string.email));
            occupation      = new Label(9, 0, getString(R.string.occupation));
            contact         = new Label(10, 0, getString(R.string.contact));
            address1        = new Label(11, 0, getString(R.string.address_line_1));
            address2        = new Label(12, 0, getString(R.string.address_line_2));
            patientRemark1  = new Label(13, 0, getString(R.string.patient_remark1));
            patientRemark2  = new Label(14, 0, getString(R.string.patient_remark2));
            patientTypeId   = new Label(15, 0, getString(R.string.patient_type));
            description     = new Label(16, 0, getString(R.string.description));

            headerCell(patient_id, sheet1);
            headerCell(firstName, sheet1);
            headerCell(surname, sheet1);
            headerCell(gender, sheet1);
            headerCell(age, sheet1);
            headerCell(dateofBirth, sheet1);
            headerCell(registerDate, sheet1);
            headerCell(idNumber, sheet1);
            headerCell(email, sheet1);
            headerCell(occupation, sheet1);
            headerCell(contact, sheet1);
            headerCell(address1, sheet1);
            headerCell(address2, sheet1);
            headerCell(patientRemark1, sheet1);
            headerCell(patientRemark2, sheet1);
            headerCell(patientTypeId, sheet1);
            headerCell(description, sheet1);

            //body cell
            //check sort default
            SharedPreferences settingPref = getSharedPreferences("setting_pref", Context.MODE_PRIVATE);
            String defaultSort = settingPref.getString("sort_patient", "");
            patientTable.setSorting(defaultSort);

            listPatient = patientTable.getRecords();
            for (i = 0; i < listPatient.size(); i++) {
                progress++;
                progressDialog.setProgress(progress);
                PatientModel patientData = listPatient.get(i);
                patient_id      = new Label(0, i + 1, patientData.getPatient_id());
                firstName       = new Label(1, i + 1, patientData.getFirst_name());
                surname         = new Label(2, i + 1, patientData.getSurname());
                gender          = new Label(3, i + 1, patientData.getGenderStr(this));
                age             = new Label(4, i + 1, String.valueOf(patientData.getAge(patientData.getDate_of_birth())));
                dateofBirth     = new Label(5, i + 1, Global.getDateFormated(patientData.getDate_of_birth(), "yyyy-MM-dd"));
                registerDate    = new Label(6, i + 1, Global.getDateFormated(patientData.getRegister_date(), "yyyy-MM-dd"));
                idNumber        = new Label(7, i + 1, patientData.getIdentification_no());
                email           = new Label(8, i + 1, patientData.getEmail());
                occupation      = new Label(9, i + 1, patientData.getOccupation());
                contact         = new Label(10, i + 1, patientData.getContact_no());
                address1        = new Label(11, i + 1, patientData.getAddress_street_1(), cellFormat);
                address2        = new Label(12, i + 1, patientData.getAddress_street_2(), cellFormat);
                patientRemark1  = new Label(13, i + 1, patientData.getPatient_remark_1(), cellFormat);
                patientRemark2  = new Label(14, i + 1, patientData.getPatient_remark_2(), cellFormat);
                patientTypeId   = new Label(15, i + 1, patientData.getpatientTypeStr(this));
                description     = new Label(16, i + 1, patientData.getDescription());

                sheet1.addCell(patient_id);
                sheet1.addCell(firstName);
                sheet1.addCell(surname);
                sheet1.addCell(gender);
                sheet1.addCell(age);
                sheet1.addCell(dateofBirth);
                sheet1.addCell(registerDate);
                sheet1.addCell(idNumber);
                sheet1.addCell(email);
                sheet1.addCell(occupation);
                sheet1.addCell(contact);
                sheet1.addCell(address1);
                sheet1.addCell(address2);
                sheet1.addCell(patientRemark1);
                sheet1.addCell(patientRemark2);
                sheet1.addCell(patientTypeId);
                sheet1.addCell(description);
            }

            for(int x = 0; x <= sheet1.getColumns(); x++)
            {
                CellView cells=sheet1.getColumnView(x);
                cells.setAutosize(true);
                sheet1.setColumnView(x, cells);
            }


            //sheet medical records
            Label record_date, work_location, patient, anamnesa, physical_exam, weight,temperature,
                    systolic, diastolic, diagnosa, therapy;

            record_date     = new Label(0, 0, getString(R.string.record_date));
            work_location   = new Label(1, 0, getString(R.string.work_location));
            patient_id      = new Label(2, 0, getString(R.string.patient_id));
            patient         = new Label(3, 0, getString(R.string.patient));
            anamnesa        = new Label(4, 0, getString(R.string.anamnesa));
            physical_exam   = new Label(5, 0, getString(R.string.physical_exam));
            weight          = new Label(6, 0, getString(R.string.weight));
            temperature     = new Label(7, 0, getString(R.string.temperature));
            systolic        = new Label(8, 0, getString(R.string.systolic));
            diastolic       = new Label(9, 0, getString(R.string.diastolic));
            diagnosa        = new Label(10, 0, getString(R.string.diagnosa));
            therapy         = new Label(11, 0, getString(R.string.therapy));
            patientTypeId   = new Label(12, 0, getString(R.string.patient_type));

            headerCell(record_date, sheet2);
            headerCell(work_location, sheet2);
            headerCell(patient_id, sheet2);
            headerCell(patient, sheet2);
            headerCell(anamnesa, sheet2);
            headerCell(physical_exam, sheet2);
            headerCell(weight, sheet2);
            headerCell(temperature, sheet2);
            headerCell(systolic, sheet2);
            headerCell(diastolic, sheet2);
            headerCell(diagnosa, sheet2);
            headerCell(therapy, sheet2);
            headerCell(patientTypeId, sheet2);

            //body cell
            recordTable.isAllDate(true);
            listRecord = recordTable.getRecordsExport();
            for (i = 0; i < listRecord.size(); i++) {
                progress += 1;
                progressDialog.setProgress(progress);
                RecordModel recordData = listRecord.get(i);
//                String location = workLocationTable.getLocationById(recordData.getWork_location_id());
//                String patientName = patientTable.getPatientNameById(recordData.getPatient_id());
//                String patientID = patientTable.getPatientFieldById(recordData.getPatient_id(), "patient_id");
                record_date     = new Label(0, i + 1, Global.getDateFormated(recordData.getRecord_date(), "yyyy-MM-dd"));
                work_location   = new Label(1, i + 1, recordData.getLocation());
                patient_id       = new Label(2, i + 1, recordData.getPatient_id_kode());
                patient         = new Label(3, i + 1, recordData.getPatient_name());
                anamnesa        = new Label(4, i + 1, recordData.getAnamnesa(), cellFormat);
                physical_exam   = new Label(5, i + 1, recordData.getPhysical_exam(), cellFormat);
                weight          = new Label(6, i + 1, df1.format(recordData.getWeight()));
                temperature     = new Label(7, i + 1, df1.format(recordData.getTemperature()));
                systolic        = new Label(8, i + 1, String.valueOf(recordData.getBlood_pressure_systolic()));
                diastolic       = new Label(9, i + 1, String.valueOf(recordData.getBlood_pressure_diastolic()));
                diagnosa        = new Label(10, i + 1, recordData.getDiagnosa(), cellFormat);
                therapy         = new Label(11, i + 1, recordData.getTherapy(), cellFormat);
                patientTypeId   = new Label(12, i + 1, recordData.getpatientTypeStr(this));


                sheet2.addCell(record_date);
                sheet2.addCell(work_location);
                sheet2.addCell(patient_id);
                sheet2.addCell(patient);
                sheet2.addCell(anamnesa);
                sheet2.addCell(physical_exam);
                sheet2.addCell(weight);
                sheet2.addCell(temperature);
                sheet2.addCell(systolic);
                sheet2.addCell(diastolic);
                sheet2.addCell(diagnosa);
                sheet2.addCell(therapy);
                sheet2.addCell(patientTypeId);
            }

            for(int y = 0; y <= sheet2.getColumns(); y++)
            {
                CellView cells=sheet2.getColumnView(y);
                cells.setAutosize(true);
                sheet2.setColumnView(y, cells);
            }


            //sheet billing
            Label billing_date, notes, items, billing_total;
            billing_date    = new Label(0, 0, getString(R.string.billing_date));
            patient_id      = new Label(1, 0, getString(R.string.patient_id));
            patient         = new Label(2, 0, getString(R.string.patient));
            notes           = new Label(3, 0, getString(R.string.notes));
            items           = new Label(4, 0, getString(R.string.items));
            billing_total   = new Label(5, 0, getString(R.string.billing_total));

            headerCell(billing_date, sheet3);
            headerCell(patient_id, sheet3);
            headerCell(patient, sheet3);
            headerCell(notes, sheet3);
            headerCell(items, sheet3);
            headerCell(billing_total, sheet3);

            //body cell
            listBilling = billingTable.getRecords();
            int row = 0;
            for (i = 0; i < listBilling.size(); i++) {
                progress ++;
                progressDialog.setProgress(progress);
                BillingModel billingData = listBilling.get(i);
                ArrayList<BillingItemModel> billingItemData = billingItemTable.getRecordsById(billingData.getId());
                String patientName = patientTable.getPatientNameById(billingData.getPatient_id());
                String patientID = patientTable.getPatientFieldById(billingData.getPatient_id(), "patient_id");

                if (billingItemData.size() > 0) {

                    for (int j = 0; j < billingItemData.size(); j++) {
                        row += 1;
                        billing_date = new Label(0, row, Global.getDateFormated(billingData.getBilling_date(), "yyyy-MM-dd"));
                        patient = new Label(1, row, patientName);
                        patient_id = new Label(2, row, patientID);
                        notes = new Label(3, row, billingData.getNotes(), cellFormat);
                        items = new Label(4, row, billingItemData.get(j).getName());
                        billing_total = new Label(5, row, String.valueOf(billingItemData.get(j).getAmount()));

                        sheet3.addCell(billing_date);
                        sheet3.addCell(patient_id);
                        sheet3.addCell(patient);
                        sheet3.addCell(notes);
                        sheet3.addCell(items);
                        sheet3.addCell(billing_total);
                    }

                }else{
                    row += 1;
                    billing_date = new Label(0, row, Global.getDateFormated(billingData.getBilling_date(), "yyyy-MM-dd"));
                    patient = new Label(1, row, patientName);
                    patient_id = new Label(2, row, patientID);
                    notes = new Label(3, row, billingData.getNotes(), cellFormat);

                    sheet3.addCell(billing_date);
                    sheet3.addCell(patient_id);
                    sheet3.addCell(patient);
                    sheet3.addCell(notes);
                }
            }

            for(int z = 0; z <= sheet3.getColumns(); z++)
            {
                CellView cells=sheet3.getColumnView(z);
                cells.setAutosize(true);
                sheet3.setColumnView(z, cells);
            }


            workbook.write();
            try {
                workbook.close();
            } catch (WriteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void headerCell(Label label, WritableSheet sheet){
        try {
            WritableCellFormat newFormat = new WritableCellFormat(label.getCellFormat());
            newFormat.setBackground(Colour.GREY_25_PERCENT);
            newFormat.setAlignment(Alignment.CENTRE);
            label.setCellFormat(newFormat);

            sheet.addCell(label);
        }catch (RowsExceededException e) {
            e.printStackTrace();
        }catch (WriteException e) {
            e.printStackTrace();
        }
    }

    private void GetPermissionExport() {
        int hasWriteStoragePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){  //kalo android 10 keatas ga butuh permission
            exportOfflines();
        }else if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS); //permisison request code is just an int
        } else {
            exportOfflines();
        }
    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    GetPermissionExport();
                    JApplication.getInstance().isFirstTimeRequest = false;
                }else {
                    // Permission Denied
                    if(JApplication.getInstance().isFirstTimeRequest) {
                        JApplication.getInstance().isFirstTimeRequest = false;
                        Toast.makeText(this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}