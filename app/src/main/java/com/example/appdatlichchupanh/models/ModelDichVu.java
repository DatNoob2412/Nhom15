package com.example.appdatlichchupanh.models;

public class ModelDichVu {

    String uid, id, TenDichVu, Gia, mota, Loaiid, Url;
    long timestamp, viewsCount;
    boolean datlich;

    public ModelDichVu() {
    }

    public ModelDichVu(String uid, String id, String tenDichVu, String gia, String mota, String loaiid, String url, long timestamp, long viewsCount, boolean datlich) {
        this.uid = uid;
        this.id = id;
        TenDichVu = tenDichVu;
        Gia = gia;
        this.mota = mota;
        Loaiid = loaiid;
        Url = url;
        this.timestamp = timestamp;
        this.viewsCount = viewsCount;
        this.datlich = datlich;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTenDichVu() {
        return TenDichVu;
    }

    public void setTenDichVu(String tenDichVu) {
        TenDichVu = tenDichVu;
    }

    public String getGia() {
        return Gia;
    }

    public void setGia(String gia) {
        Gia = gia;
    }

    public String getMota() {
        return mota;
    }

    public void setMota(String mota) {
        this.mota = mota;
    }

    public String getLoaiid() {
        return Loaiid;
    }

    public void setLoaiid(String loaiid) {
        Loaiid = loaiid;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getViewsCount() {
        return viewsCount;
    }

    public void setViewsCount(long viewsCount) {
        this.viewsCount = viewsCount;
    }

    public boolean isDatlich() {
        return datlich;
    }

    public void setDatlich(boolean datlich) {
        this.datlich = datlich;
    }
}
