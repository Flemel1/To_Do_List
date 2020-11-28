package com.example.todolistandroid.model;

public class Task {
    private String tanggal;
    private String namaTask;
    private String waktu;
    private String kategori;
    private String deskripsi;

    public Task(){

    }
    public Task(String judul, String tanggal, String waktu){
        this.namaTask = judul;
        this.tanggal = tanggal.split(" ")[1];
        this.waktu = waktu;
    }


    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getNamaTask() {
        return namaTask;
    }

    public void setNamaTask(String namaTask) {
        this.namaTask = namaTask;
    }

    public String getWaktu() {
        return waktu;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }
}
