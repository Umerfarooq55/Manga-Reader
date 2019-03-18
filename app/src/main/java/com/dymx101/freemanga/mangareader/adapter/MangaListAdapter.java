package com.dymx101.freemanga.mangareader.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.dymx101.freemanga.mangareader.R;
import com.dymx101.freemanga.mangareader.activity.DetailActivity;
import com.dymx101.freemanga.mangareader.data.MangaContract;
import com.dymx101.freemanga.mangareader.database.TinyDB;
import com.dymx101.freemanga.mangareader.model.Manga;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MangaListAdapter extends RecyclerView.Adapter<MangaListAdapter.MangaListHolder> {

    private ArrayList<Manga> mManga;
    private Context context;

    public MangaListAdapter(Context context, ArrayList<Manga> manga) {
        this.context = context;
        mManga = manga;
    }

    @Override
    public MangaListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.activity_display, parent, false);
        return new MangaListHolder(view);
    }

    @Override
    public void onBindViewHolder(final MangaListHolder holder, int position) {

        final Manga manga = mManga.get(position);
        final String image = manga.getIm();
        String title = manga.getT();
        final String id = manga.getI();
        final String hits = String.valueOf(manga.getH());

//        title = title.substring(0, Math.min(title.length(), 14));

        holder.mangaName.setText(title);

        Picasso.with(context).load("https://cdn.mangaeden.com/mangasimg/" + image).placeholder(R.drawable.bookicon).into(holder.imageView);

        holder.materialRippleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DetailActivity.class);
                TinyDB tinydb = new TinyDB(context);

                intent.putExtra("position", 0);
                intent.putExtra("ParcelHits", hits);
                tinydb.putString("ParcelId",id);
                tinydb.putString("ParcelHits",hits);
                v.getContext().startActivity(intent);

            }
        });


        boolean favorite = false;
        Cursor cursor = context.getContentResolver().query(
                MangaContract.ImagesContract.CONTENT_URI,
                null,
                MangaContract.ImagesContract.IMAGE_ID + " = " + hits,
                null,
                null
        );
        if (cursor != null) {
            favorite = cursor.getCount() != 0;
            cursor.close();
        }
        boolean isFav = favorite;


        if (isFav) {
            holder.img.setImageResource(R.drawable.ic_fav);

        } else {
            holder.img.setImageResource(R.drawable.ic_nofav);
        }

        final String finalTitle = title;
        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                boolean favorite = false;
                Cursor cursor = context.getContentResolver().query(
                        MangaContract.ImagesContract.CONTENT_URI,
                        null,
                        MangaContract.ImagesContract.IMAGE_ID + " = " + hits,
                        null,
                        null
                );
                if (cursor != null) {
                    favorite = cursor.getCount() != 0;
                    cursor.close();
                }


                boolean inFavorites = favorite;
                if (inFavorites) {
                    holder.img.setImageResource(R.drawable.ic_nofav);
                    Toast.makeText(context, "Removed from Favorites", Toast.LENGTH_SHORT).show();

                    context.getContentResolver().delete(MangaContract.ImagesContract.CONTENT_URI, MangaContract.ImagesContract.IMAGE_ID + " = ? ",
                            new String[]{hits + ""});


                } else {
                    holder.img.setImageResource(R.drawable.ic_fav);
                    Toast.makeText(context, "Added to Favorites", Toast.LENGTH_SHORT).show();

                    ContentValues values = new ContentValues();
                    values.put(MangaContract.ImagesContract.IMAGE_ID, hits);
                    values.put(MangaContract.ImagesContract.IMAGE_TITLE, finalTitle);
                    values.put(MangaContract.ImagesContract.MANGA_ID, id);
                    values.put(MangaContract.ImagesContract.IMG_THUMBNAIL, image);

                    context.getContentResolver().insert(MangaContract.ImagesContract.CONTENT_URI, values);

                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return mManga.size();
    }

    public void add(Manga manga) {
        mManga.add(manga);
        notifyDataSetChanged();
    }


    public class MangaListHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private ImageView img;
        private MaterialRippleLayout materialRippleLayout;
        private TextView mangaName;

        public MangaListHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.mana_photo);
            mangaName = itemView.findViewById(R.id.manga_name);
            img = itemView.findViewById(R.id.fav);
            materialRippleLayout = itemView.findViewById(R.id.manga_cover);


        }
    }

    public void filterList(ArrayList<Manga> filteredList) {
        mManga = filteredList;
        notifyDataSetChanged();
    }

}
