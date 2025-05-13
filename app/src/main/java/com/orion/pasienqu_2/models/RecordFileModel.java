package com.orion.pasienqu_2.models;

import android.database.Cursor;

public class RecordFileModel {
    private int id;
    private String uuid;

    private int record_id;
    private String record_uuid;
    private String file_name;
    private String record_file;
    private String mime_type;
    private long create_date;
    private long write_date;
    
    String mode;

    public RecordFileModel(int id, String uuid, int record_id, String record_uuid, String file_name, String record_file, String mime_type, long create_date, long write_date) {
        this.id = id;
        this.uuid = uuid;
        this.record_id = record_id;
        this.record_uuid = record_uuid;
        this.file_name = file_name;
        this.record_file = record_file;
        this.mime_type = mime_type;
        this.create_date = create_date;
        this.write_date = write_date;
    }

    public RecordFileModel() {
        this.id = 0;
        this.uuid = "";
        this.record_id = 0;
        this.record_uuid = "";
        this.file_name = "";
        this.record_file = "";
        this.mime_type = "";
        this.mode = "";
        this.create_date = 0;
        this.write_date = 0;
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

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getRecord_file() {
        return record_file;
    }

    public void setRecord_file(String record_file) {
        this.record_file = record_file;
    }

    public String getMime_type() {
        return mime_type;
    }

    public void setMime_type(String mime_type) {
        this.mime_type = mime_type;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
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
}