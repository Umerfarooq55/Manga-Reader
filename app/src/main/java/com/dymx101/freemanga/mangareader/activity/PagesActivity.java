package com.dymx101.freemanga.mangareader.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dymx101.freemanga.mangareader.R;
import com.dymx101.freemanga.mangareader.adapter.PagesAdapter;
import com.dymx101.freemanga.mangareader.database.DatabaseHandlerFroms;
import com.dymx101.freemanga.mangareader.database.TinyDB;
import com.dymx101.freemanga.mangareader.fragment.ChaptersFragment;
import com.dymx101.freemanga.mangareader.model.Pages;
import com.dymx101.freemanga.mangareader.retrofit.MangaRequest;
import com.dymx101.freemanga.mangareader.retrofit.MangaUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PagesActivity
        extends AppCompatActivity
    {

        public static ArrayList<Bitmap> temp = new ArrayList<Bitmap>();
        Boolean CancelAsync = false;
        List<List<String>> list;
        DatabaseHandlerFroms db = new DatabaseHandlerFroms(PagesActivity.this);
        String chapterId;

        Boolean downloaded;

        String Chapternumber, Size;
        TextView empty_view;
        TinyDB tinydb;
        ArrayList PreLoaded = new ArrayList<>();
        private CoordinatorLayout coordinatorLayout;
        private Snackbar snackbar;
        private RecyclerView recyclerView;
        private PagesAdapter pagesAdapter;
        private FrameLayout upper, bottom;
        private int position = 0;
        private ProgressBar progressBar;

        @Override
        protected void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.activity_pages);

            progressBar = findViewById(R.id.progressbar);
            chapterId = getIntent().getStringExtra("ParcelChapter");
            Chapternumber = getIntent().getStringExtra("chapterNumber");
            Size = getIntent().getStringExtra("mStrings");
            tinydb = new TinyDB(this);
            tinydb.putBoolean("cancelasyncPage",false);


            Log.d("chapterid",chapterId);
            empty_view = findViewById(R.id.empty_view);

            recyclerView = findViewById(R.id.recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
            recyclerView.setHasFixedSize(true);
            coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
            upper = findViewById(R.id.upper);
            bottom = findViewById(R.id.bottom);
            downloaded = getIntent().getBooleanExtra("downloaded",false);
            if (!downloaded) {
                snackbar("Images are Saving into cache for better reading experince :)",R.color.colorAccent);

            } else {
                progressBar.setVisibility(View.VISIBLE);
//                snackbar("Images are Saved Justing getting from saved cache",R.color.snackBarjson);


            }

            DownloadFromServer(chapterId);
            upper.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        position--;
                        if (position > -1) {
                            recyclerView.smoothScrollToPosition(position);

                        } else {
                            position = 0;
                        }
                    }
                });
            bottom.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        position++;
                        if (position < list.size()) {

                            recyclerView.smoothScrollToPosition(position);
                        } else {
                            position = list.size() - 1;
                        }


                    }
                });


        }

        @Override
        protected void onDestroy()
        {
            super.onDestroy();
            tinydb.putBoolean("cancelasyncPage",true);
        }

        @Override
        public void onBackPressed()
        {
            super.onBackPressed();
            Intent intent = new Intent(PagesActivity.this,DetailActivity.class);
            ChaptersFragment.adapter.notifyDataSetChanged();
            intent.putExtra("position",1);
            tinydb.putBoolean("cancelasyncPage",true);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        @Override
        protected void onPause()
        {
            super.onPause();
            tinydb.putBoolean("cancelasyncPage",true);
        }

        public void DownloadFromServer(String chapterId)

        {


            MangaRequest client = MangaUtils.getAPIService();
            Call<Pages> call = client.getPages(chapterId);

            call.enqueue(new Callback<Pages>()
                {
                    @Override
                    public void onResponse(@NonNull Call<Pages> call,
                            @NonNull Response<Pages> response
                                          )
                    {
                        list = response.body().getImages();
                        Collections.reverse(list);

                        new Get_Image().execute();

                    }

                    @Override
                    public void onFailure(@NonNull Call<Pages> call,@NonNull Throwable t)
                    {
                        Toast.makeText(PagesActivity.this,"Something's Wrong :(",Toast.LENGTH_SHORT).show();

                    }
                });


        }

        public void snackbar(String msg,int colore)
        {
            snackbar = Snackbar.make(coordinatorLayout,msg,Snackbar.LENGTH_LONG);
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(getResources().getColor(colore));
            snackbar.show();


        }

        public class Get_Image
                extends AsyncTask<Void, Void, Void>
            {

                @Override
                protected void onPreExecute()
                {
                    super.onPreExecute();


                    if (!downloaded) {

                        pagesAdapter = new PagesAdapter(PagesActivity.this,list,null,tinydb,false,empty_view);
                        recyclerView.setAdapter(pagesAdapter);
                    }
                }


                @Override
                protected Void doInBackground(Void... arg0)
                {

                    if (!downloaded) {

                        CancelAsync = tinydb.getBoolean("cancelasyncPage");
                        Log.d("asynccancel",String.valueOf(CancelAsync));
                        if (!CancelAsync) {
                            Log.e("currentsize",list.size() + "");
                            temp = new ArrayList<Bitmap>();
                            for (int i = 0; i < list.size(); i++) {


                                Log.e("currentloop",i + "");


                                try {
                                    URL url = new URL("https://cdn.mangaeden.com/mangasimg/" + list.get(i).get(1));
                                    Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                                    //to encode string

                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    image.compress(Bitmap.CompressFormat.JPEG,100,baos); //bm is the bitmap object
                                    byte[] b = baos.toByteArray();
                                    String encodedImage = Base64.encodeToString(b,Base64.DEFAULT);
//                temp.add(image);
                                    Log.e("currentdb",db.addContact(chapterId,encodedImage) + "");


                                } catch (IOException e) {
                                    System.out.println(e);
                                }

                            }
                            ArrayList<String> cacheChaterList = tinydb.getListString("chachechapters");
                            cacheChaterList.add(chapterId);
                            tinydb.putListString("chachechapters",cacheChaterList);

//                list.add(list.get(i));
//                pagesAdapter.add(list.get(i));
                        }

                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void result)
                {
                    super.onPostExecute(result);


                    if (downloaded) {
                        Log.d("downloaded",String.valueOf(downloaded));
                        Log.d("cachesyn",String.valueOf(CancelAsync = tinydb.getBoolean("cancelasyncPage")));
                        new ImagesFromDB().execute();

                    } else {
//                        Toast.makeText(PagesActivity.this,"helloo",Toast.LENGTH_SHORT).show();
//                        snackbar("Images are Saved into cache Successfully",R.color.colorAccent);
//                        downloaded = true;
//                        DownloadFromServer(chapterId);

                    }


                }

                class ImagesFromDB
                        extends AsyncTask<Void, Integer, ArrayList<Bitmap>>
                    {
                        String TAG = getClass().getSimpleName();

                        protected void onPreExecute()
                        {
                            super.onPreExecute();
                            tinydb.putBoolean("cancelasyncPage",false);
                            progressBar.setVisibility(View.VISIBLE);

                        }

                        protected ArrayList<Bitmap> doInBackground(Void... arg0)
                        {

                            CancelAsync = tinydb.getBoolean("cancelasyncPage");

                            if (!CancelAsync) {
                                ArrayList<String> imgstring = new ArrayList<String>();
                                imgstring = db.getformdata(chapterId);
                                Log.e("currentfetch",imgstring.size() + "");
                                ArrayList<Bitmap> bitmap_forwardarray = new ArrayList<Bitmap>();
                                for (int i = 0; i < imgstring.size(); i++) {

                                    byte[] encodeByte = Base64.decode(imgstring.get(i),Base64.DEFAULT);
                                    Bitmap profile = BitmapFactory.decodeByteArray(encodeByte,0,encodeByte.length);
                                    bitmap_forwardarray.add(profile);
                                }
                                return bitmap_forwardarray;
                            }
                            return null;
                        }

                        protected void onProgressUpdate(Integer... a)
                        {
                            super.onProgressUpdate(a);

                        }

                        protected void onPostExecute(ArrayList<Bitmap> result)
                        {
                            super.onPostExecute(result);
//                            Toast.makeText(PagesActivity.this,"From Cache ",Toast.LENGTH_SHORT).show();
                            CancelAsync = tinydb.getBoolean("cancelasyncPage");
                            if (!CancelAsync) {
                                progressBar.setVisibility(View.GONE);

                                pagesAdapter = new PagesAdapter(PagesActivity.this,list,result,tinydb,true,empty_view);
                                recyclerView.setAdapter(pagesAdapter);


                                ChaptersFragment.adapter.notifyDataSetChanged();

                            }
                        }
                    }

            }
    }