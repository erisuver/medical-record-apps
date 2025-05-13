package com.orion.pasienqu_2;

/**
 * Created by User on 19/12/2016.
 */

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBConnV1 extends SQLiteOpenHelper {
    //private Context context;
    Context ctx;
    public DBConnV1(Context context){
        super(context, context.getString(R.string.database_name_v1), null, 1);
        ctx = context;
    }

    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS data_pasien(_seq INTEGER PRIMARY KEY, id_pasien varchar(7), tgl_daftar integer, nama varchar(50), jenis_kelamin varchar(1), alamat varchar(255), no_telp varchar(20), tgl_lahir integer, keterangan_1 varchar(255), keterangan_2 varchar(255), non_aktif varchar(1), pekerjaan varchar(50))");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS catatan_medis(_seq INTEGER PRIMARY KEY, seq_pasien integer, tanggal integer, anamnesa varchar(255), pemeriksaan_fisik varchar(255), diagnosa varchar(255), therapi varchar(255), berat real, temperatur real, tensi_1 real, tensi_2 real)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        this.onCreate(sqLiteDatabase);
    }

    public String getDatabaseLocation() {
        String hasil = ctx.getDatabasePath(ctx.getString(R.string.database_name_v1)).toString();
        return hasil;
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.setVersion(oldVersion);
    }
}
