package com.example.todolistandroid.model;

public class Task {
    private String tanggal;
    private String namaTask;
    private String waktu;
    private String kategori;
    private String deskripsi;
    private String uid;
    private String documentID;
    private int notifID;

    private int totalKatergoriOlahraga = 0;
    private int totalKatergoriPekerjaan = 0;
    private int totalKatergoriAcara = 0;
    private int totalKatergoriMakan = 0;
    private int totalKatergoriMeeting = 0;
    private int totalKatergoriRekreasi = 0;

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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public int getNotifID() {
        return notifID;
    }

    public void setNotifID(int notifID) {
        this.notifID = notifID;
    }

    public int getTotalKatergoriOlahraga() {
        return totalKatergoriOlahraga;
    }

    public void setTotalKatergoriOlahraga(int totalKatergoriOlahraga) {
        this.totalKatergoriOlahraga += totalKatergoriOlahraga;
    }

    public int getTotalKatergoriPekerjaan() {
        return totalKatergoriPekerjaan;
    }

    public void setTotalKatergoriPekerjaan(int totalKatergoriPekerjaan) {
        this.totalKatergoriPekerjaan += totalKatergoriPekerjaan;
    }

    public int getTotalKatergoriAcara() {
        return totalKatergoriAcara;
    }

    public void setTotalKatergoriAcara(int totalKatergoriAcara) {
        this.totalKatergoriAcara += totalKatergoriAcara;
    }

    public int getTotalKatergoriMakan() {
        return totalKatergoriMakan;
    }

    public void setTotalKatergoriMakan(int totalKatergoriMakan) {
        this.totalKatergoriMakan += totalKatergoriMakan;
    }

    public int getTotalKatergoriMeeting() {
        return totalKatergoriMeeting;
    }

    public void setTotalKatergoriMeeting(int totalKatergoriMeeting) {
        this.totalKatergoriMeeting += totalKatergoriMeeting;
    }

    public int getTotalKatergoriRekreasi() {
        return totalKatergoriRekreasi;
    }

    public void setTotalKatergoriRekreasi(int totalKatergoriRekreasi) {
        this.totalKatergoriRekreasi += totalKatergoriRekreasi;
    }
}
