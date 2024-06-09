package com.android.proyekakhir.Model;

public class Laporan {
    private String kategori;
    private String catatan;
    private String dampak;
    private double latitude;
    private double longitude;
    private String alamat;
    private String status;

    public Laporan() {
    }

    public Laporan(String kategori, String catatan, String dampak, double latitude, double longitude, String alamat, String status) {
        this.kategori = kategori;
        this.catatan = catatan;
        this.dampak = dampak;
        this.latitude = latitude;
        this.longitude = longitude;
        this.alamat = alamat;
        this.status = status;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }

    public String getDampak() {
        return dampak;
    }

    public void setDampak(String dampak) {
        this.dampak = dampak;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
