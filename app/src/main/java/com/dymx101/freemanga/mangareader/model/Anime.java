package com.dymx101.freemanga.mangareader.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Anime implements Parcelable{

    @SerializedName("end")
    @Expose
    private Integer end;
    @SerializedName("manga")
    @Expose
    private List<Manga> manga = null;
    @SerializedName("page")
    @Expose
    private Integer page;
    @SerializedName("start")
    @Expose
    private Integer start;
    @SerializedName("total")
    @Expose
    private Integer total;

    protected Anime(Parcel in) {
        if (in.readByte() == 0) {
            end = null;
        } else {
            end = in.readInt();
        }
        if (in.readByte() == 0) {
            page = null;
        } else {
            page = in.readInt();
        }
        if (in.readByte() == 0) {
            start = null;
        } else {
            start = in.readInt();
        }
        if (in.readByte() == 0) {
            total = null;
        } else {
            total = in.readInt();
        }
    }

    public static final Creator<Anime> CREATOR = new Creator<Anime>() {
        @Override
        public Anime createFromParcel(Parcel in) {
            return new Anime(in);
        }

        @Override
        public Anime[] newArray(int size) {
            return new Anime[size];
        }
    };

    public Integer getEnd() {
        return end;
    }

    public void setEnd(Integer end) {
        this.end = end;
    }

    public List<Manga> getManga() {
        return manga;
    }

    public void setManga(List<Manga> manga) {
        this.manga = manga;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (end == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(end);
        }
        if (page == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(page);
        }
        if (start == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(start);
        }
        if (total == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(total);
        }
    }
}
