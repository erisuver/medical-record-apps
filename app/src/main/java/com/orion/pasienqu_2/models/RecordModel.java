package com.orion.pasienqu_2.models;

import android.content.Context;
import android.content.res.loader.ResourcesProvider;

import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.globals.JConst;

import java.util.ArrayList;
import java.util.List;

public class RecordModel {
    private int id;
    private String uuid;
    private long record_date;
    private int work_location_id;
    private int patient_id;
    private String name;
    private double weight;
    private double temperature;
    private int blood_pressure_systolic;
    private int blood_pressure_diastolic;
    private String anamnesa;
    private String physical_exam;
    private String diagnosa;
    private String therapy;
    private List<RecordDiagnosaModel> icd_ids;
    private String prescription_file;
    private List<RecordFileModel> file_ids;
    private double total_billing;
    private int billing_id;
    private List<BillingModel> billing_ids;
    private int patient_type_id;
    private long create_date;
    private long write_date;
    private String location;
    private String patient_name;
    private String patient_id_kode;

    public RecordModel() {
        this.id = 0;
        this.uuid = "";
        this.record_date = 0;
        this.work_location_id = 0;
        this.patient_id = 0;
        this.name = "";
        this.weight = 0;
        this.temperature = 0;
        this.blood_pressure_systolic = 0;
        this.blood_pressure_diastolic = 0;
        this.anamnesa = "";
        this.physical_exam = "";
        this.diagnosa = "";
        this.therapy = "";
        this.prescription_file = "";
        this.total_billing = 0;
        this.billing_id = 0;
        this.icd_ids = new ArrayList<>();
        this.file_ids = new ArrayList<>();
        this.billing_ids = new ArrayList<>();
        this.patient_type_id = 0;
        this.create_date = 0;
        this.write_date = 0;
    }


    public RecordModel(int id, String uuid, long record_date, int work_location_id, int patient_id, String name,
                       double weight, double temperature, int blood_pressure_systolic, int blood_pressure_diastolic,
                       String anamnesa, String physical_exam, String diagnosa, String therapy, String prescription_file,
                       double total_billing, int billing_id, int patient_type_id, long create_date, long write_date) {
        this.id = id;
        this.uuid = uuid;
        this.record_date = record_date;
        this.work_location_id = work_location_id;
        this.patient_id = patient_id;
        this.name = name;
        this.weight = weight;
        this.temperature = temperature;
        this.blood_pressure_systolic = blood_pressure_systolic;
        this.blood_pressure_diastolic = blood_pressure_diastolic;
        this.anamnesa = anamnesa;
        this.physical_exam = physical_exam;
        this.diagnosa = diagnosa;
        this.therapy = therapy;
        this.prescription_file = prescription_file;
        this.total_billing = total_billing;
        this.billing_id = billing_id;
        this.icd_ids = new ArrayList<>();
        this.file_ids = new ArrayList<>();
        this.billing_ids = new ArrayList<>();
        this.patient_type_id = patient_type_id;
        this.create_date = create_date;
        this.write_date = write_date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public long getRecord_date() {
        return record_date;
    }

    public void setRecord_date(long record_date) {
        this.record_date = record_date;
    }

    public int getWork_location_id() {
        return work_location_id;
    }

    public void setWork_location_id(int work_location_id) {
        this.work_location_id = work_location_id;
    }

    public int getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(int patient_id) {
        this.patient_id = patient_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public int getBlood_pressure_systolic() {
        return blood_pressure_systolic;
    }

    public void setBlood_pressure_systolic(int blood_pressure_systolic) {
        this.blood_pressure_systolic = blood_pressure_systolic;
    }

    public int getBlood_pressure_diastolic() {
        return blood_pressure_diastolic;
    }

    public void setBlood_pressure_diastolic(int blood_pressure_diastolic) {
        this.blood_pressure_diastolic = blood_pressure_diastolic;
    }

    public String getAnamnesa() {
        return anamnesa;
    }

    public void setAnamnesa(String anamnesa) {
        this.anamnesa = anamnesa;
    }

    public String getPhysical_exam() {
        return physical_exam;
    }

    public void setPhysical_exam(String physical_exam) {
        this.physical_exam = physical_exam;
    }

    public String getDiagnosa() {
        return diagnosa;
    }

    public void setDiagnosa(String diagnosa) {
        this.diagnosa = diagnosa;
    }

    public String getTherapy() {
        return therapy;
    }

    public void setTherapy(String therapy) {
        this.therapy = therapy;
    }

    public List<RecordDiagnosaModel> getIcd_ids() {
        return icd_ids;
    }

    public void setIcd_ids(List<RecordDiagnosaModel> icd_ids) {
        this.icd_ids = icd_ids;
    }

    public String getPrescription_file() {
        return prescription_file;
    }

    public void setPrescription_file(String prescription_file) {
        this.prescription_file = prescription_file;
    }

    public List<RecordFileModel> getFile_ids() {
        return file_ids;
    }

    public void setFile_ids(List<RecordFileModel> file_ids) {
        this.file_ids = file_ids;
    }

    public double getTotal_billing() {
        return total_billing;
    }

    public void setTotal_billing(double total_billing) {
        this.total_billing = total_billing;
    }

    public int getBilling_id() {
        return billing_id;
    }

    public void setBilling_id(int billing_id) {
        this.billing_id = billing_id;
    }

    public List<BillingModel> getBilling_ids() {
        return billing_ids;
    }

    public void setBilling_ids(List<BillingModel> billing_ids) {
        this.billing_ids = billing_ids;
    }

    public int getpatient_type_id() {
        return patient_type_id;
    }

    public void setpatient_type_id(int patient_type_id) {
        this.patient_type_id = patient_type_id;
    }

    public String getpatientTypeStr(Context context) {
        if (this.patient_type_id == 1) {
            return (context.getString(R.string.patient_type_general));
        }else if (this.patient_type_id == 2) {
            return (context.getString(R.string.patient_type_bpjs));
        }else if (this.patient_type_id == 3) {
            return (context.getString(R.string.patient_type_inhealth));
        }else if (this.patient_type_id == 4) {
            return (context.getString(R.string.patient_type_insurance));
        }
        return null;
    }

    public long getCreate_date() {
        return create_date;
    }

    public void setCreate_date(long create_date) {
        this.create_date = create_date;
    }

    public long getWrite_date() {
        return write_date;
    }

    public void setWrite_date(long write_date) {
        this.write_date = write_date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPatient_name() {
        return patient_name;
    }

    public void setPatient_name(String patient_name) {
        this.patient_name = patient_name;
    }

    public String getPatient_id_kode() {
        return patient_id_kode;
    }

    public void setPatient_id_kode(String patient_id_kode) {
        this.patient_id_kode = patient_id_kode;
    }
}
