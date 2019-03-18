package com.dymx101.freemanga.mangareader.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Manga implements Parcelable{

    @SerializedName("a")
    @Expose
    private String a;
    @SerializedName("c")
    @Expose
    private List<String> c = null;
    @SerializedName("h")
    @Expose
    private Integer h;
    @SerializedName("i")
    @Expose
    private String i;
    @SerializedName("im")
    @Expose
    private String im;
    @SerializedName("ld")
    @Expose
    private Double ld;
    @SerializedName("s")
    @Expose
    private Integer s;
    @SerializedName("t")
    @Expose
    private String t;

    protected Manga(Parcel in) {
        a = in.readString();
        c = in.createStringArrayList();
        if (in.readByte() == 0) {
            h = null;
        } else {
            h = in.readInt();
        }
        i = in.readString();
        im = in.readString();
        if (in.readByte() == 0) {
            ld = null;
        } else {
            ld = in.readDouble();
        }
        if (in.readByte() == 0) {
            s = null;
        } else {
            s = in.readInt();
        }
        t = in.readString();
    }

    public static final Creator<Manga> CREATOR = new Creator<Manga>() {
        @Override
        public Manga createFromParcel(Parcel in) {
            return new Manga(in);
        }

        @Override
        public Manga[] newArray(int size) {
            return new Manga[size];
        }
    };

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public List<String> getC() {
        return c;
    }

    public void setC(List<String> c) {
        this.c = c;
    }

    public Integer getH() {
        return h;
    }

    public void setH(Integer h) {
        this.h = h;
    }

    public String getI() {
        return i;
    }

    public void setI(String i) {
        this.i = i;
    }

    public String getIm() {
        return im;
    }

    public void setIm(String im) {
        this.im = im;
    }

    public Double getLd() {
        return ld;
    }

    public void setLd(Double ld) {
        this.ld = ld;
    }

    public Integer getS() {
        return s;
    }

    public void setS(Integer s) {
        this.s = s;
    }

    public String getT() {
        return t;
    }

    public void setT(String t) {
        this.t = t;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(a);
        dest.writeStringList(c);
        if (h == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(h);
        }
        dest.writeString(i);
        dest.writeString(im);
        if (ld == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(ld);
        }
        if (s == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(s);
        }
        dest.writeString(t);
    }
}
