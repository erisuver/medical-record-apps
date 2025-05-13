package com.orion.pasienqu_2.globals;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.activities.more.export.ExportActivity;
import com.orion.pasienqu_2.activities.more.export.ExportSuccessActivity;
import com.orion.pasienqu_2.adapter.BillingInputAdapter;
import com.orion.pasienqu_2.data_table.BillingItemTable;
import com.orion.pasienqu_2.data_table.BillingTable;
import com.orion.pasienqu_2.data_table.PatientTable;
import com.orion.pasienqu_2.data_table.RecordTable;
import com.orion.pasienqu_2.data_table.WorkLocationTable;
import com.orion.pasienqu_2.models.BillingItemModel;
import com.orion.pasienqu_2.models.BillingModel;
import com.orion.pasienqu_2.models.PatientModel;
import com.orion.pasienqu_2.models.RecordModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
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

public class ExportExcel extends AppCompatActivity {

    private static Activity act;

    public static void exportOffline(Context context){
        GetPermission(((Activity)context));
    }

    public static void exportOfflines(Context context){
        act = ((Activity)context);
        GetPermission(((Activity)context));
        final String locationExport = Environment.getExternalStorageDirectory() + Environment.DIRECTORY_DOCUMENTS + "/PasienQu/Export/";
        String exportFileName = "";
        boolean isAndroid10 = true ;

//        //cek versi android
//        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q){
//            isAndroid10 = false;
//        }
//
//        if (!isAndroid10) {
            File folder = new File(locationExport);
            if (!folder.exists()) {
                folder.mkdirs();
            } else {
                String fileName = "PasienQu" + Global.serverNowFormated4Ekspor() + ".xls";
                String file = locationExport + fileName;
                exportFileName = file;
                createExcelSheet(file, context);
            }
//        }else{
//            File folder = new File(locationExport10);
//            if (!folder.exists()) {
//                folder.mkdirs();
//            } else {
//                String fileName = "PasienQu" + Global.serverNowFormated4Ekspor() + ".xls";
//                String file = locationExport10 + fileName;
//                exportFileName = file;
//                createExcelSheet(file, context);
//            }
//        }
        Intent intent = new Intent(context, ExportSuccessActivity.class);
        intent.putExtra("file_name", exportFileName);
        context.startActivity(intent);
    }


    private static void createExcelSheet(String fileName, Context context){
        PatientTable patientTable = new PatientTable(context);
        RecordTable recordTable = new RecordTable(context);
        BillingTable billingTable = new BillingTable(context);
        WorkLocationTable workLocationTable = new WorkLocationTable(context);
        BillingItemTable billingItemTable = new BillingItemTable(context);

        File sdCard = Environment.getExternalStorageDirectory();
        File file = new File (fileName);
        try {
            WorkbookSettings wbSettings = new WorkbookSettings();

            wbSettings.setLocale(new Locale("en", "EN"));

            WritableWorkbook workbook;
            int a = 1;
            workbook = Workbook.createWorkbook(file, wbSettings);
            WritableSheet sheet1 = workbook.createSheet(context.getString(R.string.title_home_patient_text), 0);
            WritableSheet sheet2 = workbook.createSheet(context.getString(R.string.title_home_medical_record_text), 1);
            WritableSheet sheet3 = workbook.createSheet(context.getString(R.string.title_billing), 2);
            int i;

            //sheet patient
            Label patient_id, firstName, surname, gender, age, dateofBirth, registerDate, idNumber,
                    email, occupation, contact, address1, address2, patientRemark1, patientRemark2, patientTypeId, description;

            //header cell
            patient_id      = new Label(0, 0, context.getString(R.string.patient_id));
            firstName       = new Label(1, 0, context.getString(R.string.first_name));
            surname         = new Label(2, 0, context.getString(R.string.surname));
            gender          = new Label(3, 0, context.getString(R.string.gender));
            age             = new Label(4, 0, context.getString(R.string.age));
            dateofBirth     = new Label(5, 0, context.getString(R.string.date_of_birth));
            registerDate    = new Label(6, 0, context.getString(R.string.register_date));
            idNumber        = new Label(7, 0, context.getString(R.string.identification_number));
            email           = new Label(8, 0, context.getString(R.string.email));
            occupation      = new Label(9, 0, context.getString(R.string.occupation));
            contact         = new Label(10, 0, context.getString(R.string.contact));
            address1        = new Label(11, 0, context.getString(R.string.address_line_1));
            address2        = new Label(12, 0, context.getString(R.string.address_line_2));
            patientRemark1  = new Label(13, 0, context.getString(R.string.patient_remark1));
            patientRemark2  = new Label(14, 0, context.getString(R.string.patient_remark2));
            patientTypeId   = new Label(15, 0, context.getString(R.string.patient_type));
            description     = new Label(16, 0, context.getString(R.string.description));

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
            for (i = 0; i < patientTable.getRecords().size(); i++) {
                PatientModel patientData = patientTable.getRecords().get(i);
                patient_id      = new Label(0, i + 1, patientData.getPatient_id());
                firstName       = new Label(1, i + 1, patientData.getFirst_name());
                surname         = new Label(2, i + 1, patientData.getSurname());
                gender          = new Label(3, i + 1, patientData.getGenderStr(context));
                age             = new Label(4, i + 1, String.valueOf(patientData.getAge()));
                dateofBirth     = new Label(5, i + 1, Global.getDateFormated(patientData.getDate_of_birth()));
                registerDate    = new Label(6, i + 1, Global.getDateFormated(patientData.getRegister_date()));
                idNumber        = new Label(7, i + 1, patientData.getIdentification_no());
                email           = new Label(8, i + 1, patientData.getEmail());
                occupation      = new Label(9, i + 1, patientData.getOccupation());
                contact         = new Label(10, i + 1, patientData.getContact_no());
                address1        = new Label(11, i + 1, patientData.getAddress_street_1());
                address2        = new Label(12, i + 1, patientData.getAddress_street_2());
                patientRemark1  = new Label(13, i + 1, patientData.getPatient_remark_1());
                patientRemark2  = new Label(14, i + 1, patientData.getPatient_remark_2());
                patientTypeId   = new Label(15, i + 1, patientData.getpatientTypeStr(context));
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

            record_date     = new Label(0, 0, context.getString(R.string.record_date));
            work_location   = new Label(1, 0, context.getString(R.string.work_location));
            patient         = new Label(2, 0, context.getString(R.string.patient));
            anamnesa        = new Label(3, 0, context.getString(R.string.anamnesa));
            physical_exam   = new Label(4, 0, context.getString(R.string.physical_exam));
            weight          = new Label(5, 0, context.getString(R.string.weight));
            temperature     = new Label(6, 0, context.getString(R.string.temperature));
            systolic        = new Label(7, 0, context.getString(R.string.systolic));
            diastolic       = new Label(8, 0, context.getString(R.string.diastolic));
            diagnosa        = new Label(9, 0, context.getString(R.string.diagnosa));
            therapy         = new Label(10, 0, context.getString(R.string.therapy));
            patientTypeId   = new Label(11, 0, context.getString(R.string.patient_type));

            headerCell(record_date, sheet2);
            headerCell(work_location, sheet2);
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
            for (i = 0; i < recordTable.getRecords().size(); i++) {
                RecordModel recordData = recordTable.getRecords().get(i);
                String location = workLocationTable.getLocationById(recordData.getWork_location_id());

                record_date     = new Label(0, i + 1, Global.getDateFormated(recordData.getRecord_date()));
                work_location   = new Label(1, i + 1, location);
                patient         = new Label(2, i + 1, String.valueOf(recordData.getName()));
                anamnesa        = new Label(3, i + 1, recordData.getAnamnesa());
                physical_exam   = new Label(4, i + 1, recordData.getPhysical_exam());
                weight          = new Label(5, i + 1, String.valueOf(recordData.getWeight()));
                temperature     = new Label(6, i + 1, String.valueOf(recordData.getTemperature()));
                systolic        = new Label(7, i + 1, String.valueOf(recordData.getBlood_pressure_systolic()));
                diastolic       = new Label(8, i + 1, String.valueOf(recordData.getBlood_pressure_diastolic()));
                diagnosa        = new Label(9, i + 1, recordData.getDiagnosa());
                therapy         = new Label(10, i + 1, recordData.getTherapy());
                patientTypeId   = new Label(11, i + 1, recordData.getpatientTypeStr(context));


                sheet2.addCell(record_date);
                sheet2.addCell(work_location);
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
            billing_date    = new Label(0, 0, context.getString(R.string.billing_date));
            patient         = new Label(1, 0, context.getString(R.string.patient));
            notes           = new Label(2, 0, context.getString(R.string.notes));
            items           = new Label(3, 0, context.getString(R.string.items));
            billing_total   = new Label(4, 0, context.getString(R.string.billing_total));

            headerCell(billing_date, sheet3);
            headerCell(patient, sheet3);
            headerCell(notes, sheet3);
            headerCell(items, sheet3);
            headerCell(billing_total, sheet3);

            //body cell
            int row = 0;
            for (i = 0; i < billingTable.getRecords().size(); i++) {
                BillingModel billingData = billingTable.getRecords().get(i);
                ArrayList<BillingItemModel> billingItemData = billingItemTable.getRecordsById(billingData.getId());

                if (billingItemData.size() > 0) {

                    for (int j = 0; j < billingItemData.size(); j++) {
                        row += 1;
                        billing_date = new Label(0, row, Global.getDateFormated(billingData.getBilling_date()));
                        patient = new Label(1, row, String.valueOf(billingData.getName()));
                        notes = new Label(2, row, billingData.getNotes());
                        items = new Label(3, row, billingItemData.get(j).getName());
                        billing_total = new Label(4, row, String.valueOf(billingItemData.get(j).getAmount()));

                        sheet3.addCell(billing_date);
                        sheet3.addCell(patient);
                        sheet3.addCell(notes);
                        sheet3.addCell(items);
                        sheet3.addCell(billing_total);
                    }

                }else{
                    row += 1;
                    billing_date = new Label(0, row, Global.getDateFormated(billingData.getBilling_date()));
                    patient = new Label(1, row, String.valueOf(billingData.getName()));
                    notes = new Label(2, row, billingData.getNotes());

                    sheet3.addCell(billing_date);
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

    private static void headerCell(Label label, WritableSheet sheet){
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

    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;

    private static void GetPermission(Activity activity) {
        int hasWriteStoragePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!activity.shouldShowRequestPermissionRationale(Manifest.permission.WRITE_CONTACTS)) {
                    showMessageOKCancel(activity,"You need to allow access to Storage",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                REQUEST_CODE_ASK_PERMISSIONS);
                                    }
                                }
                            });
                    return;
                }

                activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
            }
            return;
        } else {
            exportOfflines(activity.getApplicationContext());
        }
    }


    private static void showMessageOKCancel(Activity activity, String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(activity)
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
                    GetPermission(act);
                } else {
                    // Permission Denied
                    Log.w("export","gagal ngab");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
