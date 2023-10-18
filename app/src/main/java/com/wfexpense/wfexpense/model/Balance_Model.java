package com.wfexpense.wfexpense.model;

public class Balance_Model {
    public String id;
    public String nama;
    public String jenisTambah;
    public String jenisExpense;
    public String tanggal;
    public String bulan;

    public String getBulan() {
        return bulan;
    }

    public void setBulan(String bulan) {
        this.bulan = bulan;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String harga;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getJenisTambah() {
        return jenisTambah;
    }

    public void setJenisTambah(String jenisTambah) {
        this.jenisTambah = jenisTambah;
    }

    public String getJenisExpense() {
        return jenisExpense;
    }

    public void setJenisExpense(String jenisExpense) {
        this.jenisExpense = jenisExpense;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public Balance_Model() {
    }

    public Balance_Model(String id, String nama, String jenisTambah, String jenisExpense, String tanggal, String harga, String bulan) {
        this.id = id;
        this.nama = nama;
        this.jenisTambah = jenisTambah;
        this.jenisExpense = jenisExpense;
        this.tanggal = tanggal;
        this.harga = harga;
        this.bulan = bulan;
    }
}
