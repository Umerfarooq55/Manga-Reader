package com.dymx101.freemanga.mangareader.retrofit;

import com.dymx101.freemanga.mangareader.model.Anime;
import com.dymx101.freemanga.mangareader.model.Detail;
import com.dymx101.freemanga.mangareader.model.Pages;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface MangaRequest {

    @GET("/api/list/0/")
    Call<Anime> getAnime();

    @GET("/api/manga/{id}/")
    Call<Detail> getDetail(@Path("id") String id);


    @GET("/api/chapter/{chapterId}/")
    Call<Pages> getPages(@Path("chapterId") String chapterId);

}

// https://www.mangaeden.com/api/list/0/
// https://www.mangaeden.com/api/manga/5372389645b9ef5a0b1d20d8/
// https://www.mangaeden.com/api/chapter/5aad5eca719a1676fbad40fe/
// https://www.mangaeden.com//api/manga/5372389645b9ef5a0b1d20d8/
