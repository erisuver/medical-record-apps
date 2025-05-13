package com.orion.pasienqu_2.models;

import java.util.ArrayList;
import java.util.List;

public class RecordDiagnosaModel {
    private int id;
    private String uuid;

    private int record_id;
    private String record_uuid;
    private String icd_code;
    private String icd_name;
    private String remarks;
    private List<RecordDiagnosaModel> listIcd;
    private String id_condition;

    public RecordDiagnosaModel(int id, String uuid, int record_id, String record_uuid, String icd_code, String icd_name, String remarks, String id_condition) {
        this.id = id;
        this.uuid = uuid;
        this.record_id = record_id;
        this.record_uuid = record_uuid;
        this.icd_code = icd_code;
        this.icd_name = icd_name;
        this.remarks = remarks;
        this.id_condition = id_condition;
    }

    public RecordDiagnosaModel() {
        this.id = 0;
        this.uuid = "";
        this.record_id = 0;
        this.record_uuid = "";
        this.icd_code = "";
        this.icd_name = "";
        this.remarks = "";
        this.listIcd = new ArrayList<>();
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

    public String getIcd_code() {
        return icd_code;
    }

    public void setIcd_code(String icd_code) {
        this.icd_code = icd_code;
    }

    public String getIcd_name() {
        return icd_name;
    }

    public void setIcd_name(String icd_name) {
        this.icd_name = icd_name;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public List<RecordDiagnosaModel> getListIcd() {
        return listIcd;
    }

    public void setListIcd(List<RecordDiagnosaModel> listIcd) {
        this.listIcd = listIcd;
    }

    public String getId_condition() {
        return id_condition;
    }

    public void setId_condition(String id_condition) {
        this.id_condition = id_condition;
    }
}
