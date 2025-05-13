package com.orion.pasienqu_2.data_table;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.models.BackupDriveModel;
import com.orion.pasienqu_2.models.RecordFileModel;
import com.orion.pasienqu_2.models.WorkLocationModel;

import java.util.ArrayList;

public class BackupDriveTable {
    private SQLiteDatabase db;
    private ArrayList<BackupDriveModel> records;
    private Context context;

    public BackupDriveTable(Context context) {
        this.context = context;
        this.db = JApplication.getInstance().db;
        this.records = new ArrayList<BackupDriveModel>();
    }

    private ContentValues setValues(BackupDriveModel backupDriveModel){
        ContentValues cv = new ContentValues();
        cv.put("last_backup_data", backupDriveModel.getLast_backup_data());
        cv.put("last_backup_media", backupDriveModel.getLast_backup_media());
        return cv;
    }

    public void insert(BackupDriveModel backupDriveModel) {
        ContentValues cv = this.setValues(backupDriveModel);
        this.db.insert("backup_drive", null, cv);
    }

    public void update(BackupDriveModel backupDriveModel){
        ContentValues cv = this.setValues(backupDriveModel);
        this.db.update("backup_drive", cv, "id = " + backupDriveModel.getId(), null);
    }

    public void delete(String id){
        this.db.delete("backup_drive",  "id = " + id, null);
    }
    public boolean deleteAll() {
        this.db.delete("backup_drive", "", null);
        return true;
    }

    private void reloadList(){
        this.records.clear();
        Cursor cr = this.db.rawQuery("SELECT * from backup_drive", null);

        BackupDriveModel tempData;
        if (cr != null && cr.moveToFirst()){
            do {
                tempData = new BackupDriveModel(
                        cr.getInt(cr.getColumnIndexOrThrow("id")),
                        cr.getLong(cr.getColumnIndexOrThrow("last_backup_data")),
                        cr.getLong(cr.getColumnIndexOrThrow("last_backup_media"))
                );
                this.records.add(tempData);
            } while(cr.moveToNext());
        }
    }

    public ArrayList<BackupDriveModel> getRecords(){
        this.reloadList();
        return this.records;
    }

    public BackupDriveModel getRecord(){
        Cursor cr = this.db.rawQuery("SELECT * from backup_drive", null);
        BackupDriveModel tempData;
        if (cr != null && cr.moveToFirst()){
            tempData = new BackupDriveModel(
                    cr.getInt(cr.getColumnIndexOrThrow("id")),
                    cr.getLong(cr.getColumnIndexOrThrow("last_backup_data")),
                    cr.getLong(cr.getColumnIndexOrThrow("last_backup_media"))
            );
            return tempData;
        }else{
            return null;
        }
    }


    public long getLastUpdateData(){
        Cursor cr = this.db.rawQuery("SELECT * FROM backup_drive", null);
        if (cr != null && cr.moveToFirst()){
            return cr.getLong(cr.getColumnIndexOrThrow("last_backup_data"));
        }else{
            return 0;
        }
    }

    public long getLastUpdateMedia(){
        Cursor cr = this.db.rawQuery("SELECT * FROM backup_drive", null);
        if (cr != null && cr.moveToFirst()){
            return cr.getLong(cr.getColumnIndexOrThrow("last_backup_media"));
        }else{
            return 0;
        }
    }

}
