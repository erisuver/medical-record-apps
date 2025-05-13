package com.orion.pasienqu_2.models.blob;

public class PrescriptionFileModel {
    private int id;
    private String uuid;
    private int record_id;
    private String record_uuid;
    private String prescription_file;

    public PrescriptionFileModel(int id, String uuid, int record_id, String record_uuid, String prescription_file) {
        this.id = id;
        this.uuid = uuid;
        this.record_id = record_id;
        this.record_uuid = record_uuid;
        this.prescription_file = prescription_file;
    }

    public PrescriptionFileModel() {
        this.id = 0;
        this.uuid = "";
        this.record_id = 0;
        this.record_uuid = "";
        this.prescription_file = "";
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

    public int getRecord_id() {
        return record_id;
    }

    public void setRecord_id(int record_id) {
        this.record_id = record_id;
    }

    public String getRecord_uuid() {
        return record_uuid;
    }

    public void setRecord_uuid(String record_uuid) {
        this.record_uuid = record_uuid;
    }

    public String getPrescription_file() {
        return prescription_file;
    }

    public void setPrescription_file(String prescription_file) {
        this.prescription_file = prescription_file;
    }
}
