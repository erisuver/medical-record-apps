package com.orion.pasienqu_2.models.v1;


/**
 * Created by User on 19/12/2016.
 */
public class CatatanMedisModel {
    private long seq;
    private long seq_pasien;
    private long tanggal;
    private String anamnesa;
    private String pemeriksaan_fisik;
    private String diagnosa;
    private String therapy;
    private float berat;
    private float temperatur;
    private float tensi_1;
    private float tensi_2;


    public CatatanMedisModel(long seq, long seq_pasien, long tanggal, String anamnesa, String pemeriksaan_fisik, String diagnosa, String therapy,
                             float berat, float temperatur, float tensi_1, float tensi_2) {
        this.seq = seq;
        this.seq_pasien = seq_pasien;
        this.tanggal = tanggal;
        this.anamnesa = anamnesa;
        this.pemeriksaan_fisik = pemeriksaan_fisik;
        this.diagnosa = diagnosa;
        this.therapy = therapy;
        this.berat = berat;
        this.temperatur = temperatur;
        this.tensi_1 = tensi_1;
        this.tensi_2 = tensi_2;

    }

    public long getSeq() {
        return seq;
    }

    public void setSeq(long seq) {
        this.seq = seq;
    }

    public long getSeq_pasien() {
        return seq_pasien;
    }

    public void setSeq_pasien(long seq_pasien) {
        this.seq_pasien = seq_pasien;
    }

    public long getTanggal() {
        return tanggal;
    }



    public void setTanggal(long tanggal) {
        this.tanggal = tanggal;
    }

    public String getAnamnesa() {
        return anamnesa;
    }

    public void setAnamnesa(String anamnesa) {
        this.anamnesa = anamnesa;
    }

    public String getPemeriksaan_fisik() {
        return pemeriksaan_fisik;
    }

    public void setPemeriksaan_fisik(String pemeriksaan_fisik) {
        this.pemeriksaan_fisik = pemeriksaan_fisik;
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

    public float getBerat() {
        return berat;
    }

    public void setBerat(float berat) {
        this.berat = berat;
    }

    public float getTemperatur() {
        return temperatur;
    }

    public void setTemperatur(float temperatur) {
        this.temperatur = temperatur;
    }

    public float getTensi_1() {
        return tensi_1;
    }

    public void setTensi_1(float tensi_1) {
        this.tensi_1 = tensi_1;
    }

    public float getTensi_2() {
        return tensi_2;
    }

    public void setTensi_2(float tensi_2) {
        this.tensi_2 = tensi_2;
    }
}
