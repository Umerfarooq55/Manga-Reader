package com.dymx101.freemanga.mangareader.retrofit;

public class MangaUtils {

    public static final String BASE_URL = "https://www.mangaeden.com";

    public static  MangaRequest getAPIService() {

        return MangaClient.getClient(BASE_URL).create(MangaRequest.class);
    }
}

// https://www.mangaeden.com/api/list/0/