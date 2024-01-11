package com.example.appdatlichchupanh.models;

public class ModelLoaidv {

    String id, Loaidv,uid;
    long timestamp;

    public ModelLoaidv() {
    }

    public ModelLoaidv(String id, String loaidv, String uid, long timestamp) {
        this.id = id;
        this.Loaidv = loaidv;
        this.uid = uid;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLoaidv() {
        return Loaidv;
    }

    public void setLoaidv(String loaidv) {
        Loaidv = loaidv;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
