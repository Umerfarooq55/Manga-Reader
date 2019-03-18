package com.dymx101.freemanga.mangareader.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dymx101.freemanga.mangareader.R;
import com.dymx101.freemanga.mangareader.activity.PagesActivity;
import com.dymx101.freemanga.mangareader.database.DatabaseHandlerFroms;
import com.dymx101.freemanga.mangareader.database.TinyDB;
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

public class ChaptersAdapter
        extends RecyclerView.Adapter<ChaptersAdapter.ChapterViewHolder>
    {

        MangaRequest client = MangaUtils.getAPIService();
        Call<Pages> call;

        List<List<String>> list;
        DatabaseHandlerFroms db;
        String mychapterid;
        boolean ifloading = false;
        TinyDB tinydb;
        Boolean CancelAsync = false;
        boolean Firsttime;
        boolean forward = false;
        ArrayList<String> cache;
        ArrayList<String> PreLoaded;
        int count = 0;
        private List<List<String>> mStrings;
        private Context mContext;
        private ProgressBar mProgressBar;

        public ChaptersAdapter(Context context,
                List<List<String>> strings,
                ProgressBar mProgressBarr,
                TinyDB mtinydb
                              )
        {
            mStrings = strings;
            mContext = context;
//        mProgressBar =mProgressBarr;
            tinydb = mtinydb;

        }

        @Override
        public ChapterViewHolder onCreateViewHolder(ViewGroup parent,int viewType)
        {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.activity_chapters,parent,false);

            return new ChapterViewHolder(view);

        }

        @Override
        public void onBindViewHolder(final ChapterViewHolder holder,final int position)
        {

            final String chapterId = mStrings.get(position).get(3);


            final String chapterNumber = mStrings.get(position).get(0);
            mychapterid = chapterId;


//               Progress Bar Showing When User Click On cache And DisMiss After The Cache Completed
            Boolean progressing = tinydb.getBoolean("Progressing");
            Log.d("Progressing",String.valueOf(progressing));
            if (progressing) {
                Boolean equal = false;
                int progressingg = tinydb.getInt("progressing");


                if (progressingg == position) {

                    equal = true;
                }

                if (equal) {
                    holder.progressBar.setVisibility(View.VISIBLE);
                    holder.chache.setVisibility(View.GONE);
                } else {
                    holder.progressBar.setVisibility(View.GONE);
                    holder.chache.setVisibility(View.VISIBLE);
                }

            } else {
                holder.progressBar.setVisibility(View.GONE);
                holder.chache.setVisibility(View.VISIBLE);
            }
            holder.chapterNumber.setText("Chapter - " + chapterNumber);


            if (isdownloadedhigh(chapterId)) {

                holder.chapterNumber.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
            } else {
                holder.chapterNumber.setTextColor(mContext.getResources().getColor(android.R.color.black));
            }

            if (isdownloaded(mychapterid)) {
                Log.e("checkdownload","iftrue");

                holder.del.setVisibility(View.VISIBLE);
                holder.chache.setVisibility(View.GONE);
            } else {
                holder.del.setVisibility(View.GONE);

                Log.e("checkdownload","elsefalse");
            }


            forward = false;
            holder.itemView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {

                        ArrayList<String> highligh = new ArrayList<>();
                        tinydb.getListString("highlight");
                        highligh.add(chapterId);
                        tinydb.putListString("highlight",highligh);
                        tinydb.putBoolean("cancelasync",true);
                        ifloading = tinydb.getBoolean("ifloading");
//                            if (!ifloading) {

                        ArrayList<String> datastring = tinydb.getListString("chachechapters");

                        for (int i = 0; i < datastring.size(); i++) {

                            if (datastring.get(i).equals(chapterId)) {
                                Log.d("installed",datastring.get(i));
                                forward = true;

                            }
                        }
                        Log.d("forword",String.valueOf(forward));
                        tinydb.putString("chapternumber",chapterNumber);
                        tinydb.putInt("mStrings",mStrings.size());
                        tinydb.putBoolean("downloaded",forward);
                        Intent intent = new Intent(v.getContext(),PagesActivity.class);
                        intent.putExtra("ParcelChapter",chapterId);
                        intent.putExtra("downloaded",tinydb.getBoolean("downloaded"));
                        mychapterid = chapterId;
                        v.getContext().startActivity(intent);

                    }
//                         else {
//                        Toast.makeText(mContext,"Please wait till Loading Finish!",Toast.LENGTH_SHORT).show();
//                    }
//                        }
                });


            holder.chache.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {

                        int progressing = 0;
                        progressing = position;


                        tinydb.putBoolean("cancelasync",false);
                        ifloading = tinydb.getBoolean("ifloading");
                        tinydb.putBoolean("Progressing",true);
                        if (!ifloading) {
                            Toast.makeText(mContext,"Please Wait few minutes! Images are saving in cache...",Toast.LENGTH_SHORT).show();
                            tinydb.putInt("progressing",progressing);
                            holder.chache.setVisibility(View.GONE);
                            holder.progressBar.setVisibility(View.VISIBLE);
                            mychapterid = chapterId;
                            call = client.getPages(chapterId);
                            db = new DatabaseHandlerFroms(mContext);
                            call.enqueue(new Callback<Pages>()
                                {
                                    @Override
                                    public void onResponse(@NonNull Call<Pages> call,
                                            @NonNull Response<Pages> response
                                                          )
                                    {
                                        list = response.body().getImages();
                                        Collections.reverse(list);
                                        tinydb.putBoolean("cancelasync",false);
                                        new Get_Image().execute();

                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<Pages> call,
                                            @NonNull Throwable t
                                                         )
                                    {
                                        Toast.makeText(mContext,"Something's Wrong :(",Toast.LENGTH_SHORT).show();
                                    }
                                });

                        } else {
                            Toast.makeText(mContext,"Please wait Till Loading Finish!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            holder.del.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {

                        if (isdownloaded(chapterId)) {
                            holder.del.setVisibility(View.GONE);
                            holder.chache.setVisibility(View.VISIBLE);
                        }

                        holder.chache.setVisibility(View.VISIBLE);
                        db = new DatabaseHandlerFroms(mContext);
                        db.deleteall(chapterId);
                        for (int i = 0; i < cache.size(); i++) {

                            if (cache.get(i).equals(chapterId)) {

                                cache.remove(i);
                                tinydb.putListString("chachechapters",cache);
                                notifyDataSetChanged();
                            }
                        }


                    }
                });


        }

//

        @Override
        public int getItemCount()
        {
            return mStrings.size();
        }

        public void add(List<String> s)
        {
            mStrings.add(s);
            notifyDataSetChanged();
        }

        public void clear()
        {
            mStrings.clear();
            notifyDataSetChanged();
        }

        public boolean isdownloaded(String id)
        {
            boolean check = false;

            cache = tinydb.getListString("chachechapters");
            for (int i = 0; i < cache.size(); i++) {

                if (cache.get(i).equals(id)) {
                    Log.e("iftrue",id + ":::::" + cache.get(i));

                    check = true;
                }
            }
            return check;
        }

        public boolean isdownloadedhigh(String id)
        {
            boolean check = false;

            ArrayList highlight = tinydb.getListString("highlight");
            for (int i = 0; i < highlight.size(); i++) {

                if (highlight.get(i).equals(id)) {
                    Log.e("iftrue",id + ":::::" + highlight.get(i));

                    check = true;
                }
            }
            return check;
        }

        public class ChapterViewHolder
                extends RecyclerView.ViewHolder
            {


                TextView chapterNumber;
                ImageView check;
                ProgressBar progressBar;
                Button del, chache;

                public ChapterViewHolder(View itemView)
                {
                    super(itemView);

                    chapterNumber = itemView.findViewById(R.id.chapter_number);
                    chapterNumber = itemView.findViewById(R.id.chapter_number);
                    chache = itemView.findViewById(R.id.chache);
                    del = itemView.findViewById(R.id.del);
                    progressBar = itemView.findViewById(R.id.progressbar);
                }
            }

        public class Get_Image
                extends AsyncTask<Void, Void, Void>
            {

                @Override
                protected void onPreExecute()
                {
                    super.onPreExecute();
                    // Showing progress dialog
                    tinydb.putBoolean("ifloading",true);
//            mProgressBar.setVisibility(View.VISIBLE);
                }

                @Override
                protected Void doInBackground(Void... arg0)
                {
                    Log.e("currentsize",list.size() + "");

                    for (int i = 0; i < list.size(); i++) {
                        CancelAsync = tinydb.getBoolean("cancelasync");
                        if (!CancelAsync) {
                            Log.e("currentloop",i + "");


                            try {
                                URL url = new URL("https://cdn.mangaeden.com/mangasimg/" + list.get(i).get(1));
                                Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());


                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                image.compress(Bitmap.CompressFormat.JPEG,100,baos); //bm is the
                                byte[] b = baos.toByteArray();
                                String encodedImage = Base64.encodeToString(b,Base64.DEFAULT);

                                db.addContact(mychapterid,encodedImage);


                            } catch (IOException e) {
                                System.out.println(e);
                            }
//
                        }
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void result)
                {
                    super.onPostExecute(result);

                    tinydb.putBoolean("Progressing",false);
                    CancelAsync = tinydb.getBoolean("cancelasync");
                    if (!CancelAsync) {
                        Toast.makeText(mContext,"Cache Complete!",Toast.LENGTH_SHORT).show();
                        ArrayList<String> cacheChaterList;
                        Firsttime = tinydb.getBoolean("Firsttime");
                        tinydb.putBoolean("ifloading",false);
                        if (!Firsttime) {


                            cacheChaterList = new ArrayList<>();
                            cacheChaterList.add(mychapterid);
                            tinydb.putBoolean("Firsttime",true);
                        } else {
                            cacheChaterList = tinydb.getListString("chachechapters");
                            cacheChaterList.add(mychapterid);
                        }

                        tinydb.putListString("chachechapters",cacheChaterList);


                        notifyDataSetChanged();

                    }
                }

            }
    }