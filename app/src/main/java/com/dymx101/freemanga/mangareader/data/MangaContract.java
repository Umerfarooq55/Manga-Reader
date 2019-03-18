package com.dymx101.freemanga.mangareader.data;


import android.net.Uri;
import android.provider.BaseColumns;

public class MangaContract {

    public static final String CONTENT_AUTHORITY = "com.dymx101.freemanga.mangareader";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_IMAGES = "images";

    public static final class ImagesContract implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_IMAGES).build();

        public static final String TABLE_NAME = "images";
        public static final String IMAGE_ID = "id";
        public static final String IMAGE_TITLE = "imgTitle";
        public static final String MANGA_ID = "mangaId";
        public static final String IMG_THUMBNAIL = "imgThumbnail";

    }
}