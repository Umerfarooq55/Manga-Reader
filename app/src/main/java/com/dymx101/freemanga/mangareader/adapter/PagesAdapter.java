package com.dymx101.freemanga.mangareader.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dymx101.freemanga.mangareader.R;
import com.dymx101.freemanga.mangareader.database.TinyDB;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PagesAdapter
        extends RecyclerView.Adapter<PagesAdapter.PagesViewHolder>
    {

        Boolean tap = true;
        ArrayList<Bitmap> img = new ArrayList<Bitmap>();
        TinyDB tinydb;
        TextView middletextt;
        Boolean download;
        ArrayList<Bitmap> images1 = new ArrayList<Bitmap>();
        private List<List<String>> mList;
        private Context mContext;
        private String chapternumber, size;

        public PagesAdapter(Context context,
                List<List<String>> list,
                ArrayList<Bitmap> images,
                TinyDB tinyDB,
                Boolean mdownload,
                TextView midddletex
                           )
        {
            mList = list;
            mContext = context;
            img = images;
            tinydb = tinyDB;
            download = mdownload;
            middletextt = midddletex;

        }

        @Override
        public PagesViewHolder onCreateViewHolder(ViewGroup parent,int viewType)
        {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.pages_adapter,parent,false);
            return new PagesViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final PagesViewHolder holder,int position)
        {

            final String pages = mList.get(position).get(1);


            if (!download) {
                Picasso.with(mContext).load("https://cdn.mangaeden.com/mangasimg/" + pages).placeholder(R.drawable.progress_drawable).into(holder.imageView);

            } else {
                holder.imageView.setImageBitmap(img.get(position));
            }


            chapternumber = tinydb.getString("chapternumber");


            int pos = position + 1;
            holder.empty_view.setText("Chapter - " + chapternumber + "  " + pos + "/" + mList.size());
            holder.imageView.setOnPhotoTapListener(new OnPhotoTapListener()
                {
                    @Override
                    public void onPhotoTap(ImageView view,float x,float y)
                    {

                        if (tap) {

                            holder.empty_view.setVisibility(View.VISIBLE);
                            tap = false;
                        } else {
                            holder.empty_view.setVisibility(View.GONE);
                            tap = true;
                        }

                    }
                });

        }

        @Override
        public int getItemCount()
        {
            return mList.size();
        }

        public void add(List<String> s)
        {
            mList.add(s);

            notifyDataSetChanged();
        }

        public class PagesViewHolder
                extends RecyclerView.ViewHolder
            {

                PhotoView imageView;
                TextView empty_view;


                public PagesViewHolder(View itemView)
                {
                    super(itemView);

                    imageView = itemView.findViewById(R.id.pages_image);
//            empty_view= itemView.findViewById(R.id.empty_view);
                    empty_view = middletextt;
                }
            }

    }