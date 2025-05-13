package com.orion.pasienqu_2.models;

public class BackupDriveModel {
    private int id;
    private long last_backup_data;
    private long last_backup_media;

    public BackupDriveModel(int id, long last_backup_data, long last_backup_media) {
        this.id = id;
        this.last_backup_data = last_backup_data;
        this.last_backup_media = last_backup_media;
    }

    public BackupDriveModel() {
        this.id = 0;
        this.last_backup_data = 0;
        this.last_backup_media = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getLast_backup_data() {
        return last_backup_data;
    }

    public void setLast_backup_data(long last_backup_data) {
        this.last_backup_data = last_backup_data;
    }

    public long getLast_backup_media() {
        return last_backup_media;
    }

    public void setLast_backup_media(long last_backup_media) {
        this.last_backup_media = last_backup_media;
    }
}
