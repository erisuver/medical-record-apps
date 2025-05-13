package com.orion.pasienqu_2.models.v1;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by user1 on 19/12/2016.
 */
public class DataPasienModel {
    private long seq;
    private String id_pasien;
    private long tgl_daftar;
    private String nama;
    private int jenis_kelamin;
    private String alamat;
    private String no_telp;
    private long tgl_lahir;
    private int Keterangan1;
    private String Keterangan2;
    private String non_aktif;
    private String pekerjaan;

    public DataPasienModel(long seq, String id_pasien, long tgl_daftar, String nama, int jenis_kelamin, String alamat, String no_telp, long tgl_lahir, int keterangan1, String keterangan2, String non_aktif, String pekerjaan) {
        this.seq = seq;
        this.id_pasien = id_pasien;
        this.tgl_daftar = tgl_daftar;
        this.nama = nama;
        this.jenis_kelamin = jenis_kelamin;
        this.alamat = alamat;
        this.no_telp = no_telp;
        this.tgl_lahir = tgl_lahir;
        this.Keterangan1 = keterangan1;
        this.Keterangan2 = keterangan2;
        this.non_aktif = non_aktif;
        this.pekerjaan = pekerjaan;
    }

    public String getNon_aktif() {
        return non_aktif;
    }

    public void setNon_aktif(String non_aktif) {
        this.non_aktif = non_aktif;
    }

    public long getSeq() {
        return seq;
    }

    public void setSeq(long seq) {
        this.seq = seq;
    }

    public String getId_pasien() {
        return id_pasien;
    }

    public void setId_pasien(String id_pasien) {
        this.id_pasien = id_pasien;
    }

    public long getTgl_daftar() {
        return tgl_daftar;
    }

    public void setTgl_daftar(long tgl_daftar) {
        this.tgl_daftar = tgl_daftar;
    }

    public String getTgl_daftarString() {
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

        long miliSecond = this.tgl_daftar;

        Calendar calender = Calendar.getInstance();
        calender.setTimeInMillis(miliSecond);
        return formatter.format(calender.getTime());
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public int getJenis_kelamin() {
        return jenis_kelamin;
    }

    public void setJenis_kelamin(int jenis_kelamin) {
        this.jenis_kelamin = jenis_kelamin;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getNo_telp() {
        return no_telp;
    }

    public void setNo_telp(String no_telp) {
        this.no_telp = no_telp;
    }

    public long getTgl_lahir() {
        return tgl_lahir;
    }

    public void setTgl_lahir(long tgl_lahir) {
        this.tgl_lahir = tgl_lahir;
    }

    public String getTgl_lahirString() {
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

        long miliSecond = this.tgl_lahir;

        Calendar calender = Calendar.getInstance();
        calender.setTimeInMillis(miliSecond);
        return formatter.format(calender.getTime());
    }

    public int getKeterangan1() {
        return Keterangan1;
    }

    public void setKeterangan1(int keterangan1) {
        Keterangan1 = keterangan1;
    }

    public String getKeterangan2() {
        return Keterangan2;
    }

    public void setKeterangan2(String keterangan2) {
        Keterangan2 = keterangan2;
    }

    public String toString(){
        if (this.jenis_kelamin == 2) {
            return "Laki - laki";
        } else {
            return "Perempuan";
        }
    }

    public String keteranganToStr(){
        if (this.Keterangan1 == 2) {
            return "Asma";
        } else if (this.Keterangan1 == 3) {
            return "Alergi";
        } else if (this.Keterangan1 == 4) {
            return "Sensitif";
        } else if (this.Keterangan1 == 5) {
            return "Gastritis";
        } else {
            return "-";
        }
    }

    public String getPekerjaan() {
        return pekerjaan;
    }

    public void setPekerjaan(String pekerjaan) {
        this.pekerjaan = pekerjaan;
    }
}
