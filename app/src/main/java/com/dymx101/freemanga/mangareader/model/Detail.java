package com.dymx101.freemanga.mangareader.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Detail {


    @SerializedName("alias")
    @Expose
    private String alias;
    @SerializedName("artist")
    @Expose
    private String artist;
    @SerializedName("author")
    @Expose
    private String author;
    @SerializedName("autoManga")
    @Expose
    private Boolean autoManga;
    @SerializedName("chapters_len")
    @Expose
    private Integer chaptersLen;
    @SerializedName("created")
    @Expose
    private Long created;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("hits")
    @Expose
    private Integer hits;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("imageURL")
    @Expose
    private String imageURL;
    @SerializedName("language")
    @Expose
    private Integer language;
    @SerializedName("last_chapter_date")
    @Expose
    private Long lastChapterDate;
    @SerializedName("released")
    @Expose
    private Integer released;
    @SerializedName("startsWith")
    @Expose
    private String startsWith;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("type")
    @Expose
    private Integer type;
    @SerializedName("updatedKeywords")
    @Expose
    private Boolean updatedKeywords;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("categories")
    @Expose
    private List<String> categories = null;
    @SerializedName("title_kw")
    @Expose
    private List<String> titleKw = null;
    @SerializedName("chapters")
    @Expose
    private List<List<String>> chapters;


    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Boolean getAutoManga() {
        return autoManga;
    }

    public void setAutoManga(Boolean autoManga) {
        this.autoManga = autoManga;
    }

    public Integer getChaptersLen() {
        return chaptersLen;
    }

    public void setChaptersLen(Integer chaptersLen) {
        this.chaptersLen = chaptersLen;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getHits() {
        return hits;
    }

    public void setHits(Integer hits) {
        this.hits = hits;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public Integer getLanguage() {
        return language;
    }

    public void setLanguage(Integer language) {
        this.language = language;
    }

    public Long getLastChapterDate() {
        return lastChapterDate;
    }

    public void setLastChapterDate(Long lastChapterDate) {
        this.lastChapterDate = lastChapterDate;
    }

    public Integer getReleased() {
        return released;
    }

    public void setReleased(Integer released) {
        this.released = released;
    }

    public String getStartsWith() {
        return startsWith;
    }

    public void setStartsWith(String startsWith) {
        this.startsWith = startsWith;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Boolean getUpdatedKeywords() {
        return updatedKeywords;
    }

    public void setUpdatedKeywords(Boolean updatedKeywords) {
        this.updatedKeywords = updatedKeywords;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public List<String> getTitleKw() {
        return titleKw;
    }

    public void setTitleKw(List<String> titleKw) {
        this.titleKw = titleKw;
    }


    public List<List<String>> getChapters() {
        return chapters;
    }

    public void setChapters(List<List<String>> chapters) {
        this.chapters = chapters;
    }
}



