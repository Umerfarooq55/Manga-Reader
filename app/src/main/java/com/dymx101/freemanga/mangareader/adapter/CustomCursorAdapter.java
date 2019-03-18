package com.dymx101.freemanga.mangareader.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.CardView;
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
import com.squareup.picasso.Picasso;

public class CustomCursorAdapter extends RecyclerView.Adapter<CustomCursorAdapter.TaskViewHolder> {

    private Cursor mCursor;
    private Context mContext;

    public CustomCursorAdapter(Context mContext) {
        this.mContext = mContext;
    }


    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_display, parent, false);
        return new TaskViewHolder(view);

    }


    @Override
    public void onBindViewHolder(final TaskViewHolder holder, int position) {

//        holder.cardView.setCardBackgroundColor(getRandomColorCode());

        int idColumnIndex = mCursor.getColumnIndex(MangaContract.ImagesContract._ID);
        final int mangaId = mCursor.getColumnIndex(MangaContract.ImagesContract.MANGA_ID);
        int mangaTitle = mCursor.getColumnIndex(MangaContract.ImagesContract.IMAGE_TITLE);
        int mangaThumbnail = mCursor.getColumnIndex(MangaContract.ImagesContract.IMG_THUMBNAIL);
        int mangaImageId = mCursor.getColumnIndex(MangaContract.ImagesContract.IMAGE_ID);

        mCursor.moveToPosition(position);

        final int id = mCursor.getInt(idColumnIndex);
        final String mangaIdReal = mCursor.getString(mangaId);
        final String mangaThumbnailReal = mCursor.getString(mangaThumbnail);
        final String mangaTitleReal = mCursor.getString(mangaTitle);
        final String mangaHitIdReal = mCursor.getString(mangaImageId);


        Picasso.with(mContext)
                .load("https://cdn.mangaeden.com/mangasimg/" + mangaThumbnailReal).placeholder(R.drawable.bookicon)
                .into(holder.imageView);

        holder.mangaName.setText(mangaTitleReal);

        holder.materialRippleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), DetailActivity.class);
                intent.putExtra("ParcelId", mangaIdReal);

                v.getContext().startActivity(intent);
            }
        });


        boolean favorite = false;
        Cursor cursor = mContext.getContentResolver().query(
                MangaContract.ImagesContract.CONTENT_URI,
                null,
                MangaContract.ImagesContract.IMAGE_ID + " = " + mangaHitIdReal,
                null,
                null
        );
        if (cursor != null) {
            favorite = cursor.getCount() != 0;
            cursor.close();
        }
        boolean isFav =  favorite;



        if (isFav) {
            holder.img.setImageResource(R.drawable.ic_fav);

        } else {
            holder.img.setImageResource(R.drawable.ic_nofav);
        }

        final String finalTitle = mangaTitleReal;
        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                boolean favorite = false;
                Cursor cursor = mContext.getContentResolver().query(
                        MangaContract.ImagesContract.CONTENT_URI,
                        null,
                        MangaContract.ImagesContract.IMAGE_ID + " = " + mangaHitIdReal,
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
                    Toast.makeText(mContext, "Removed from Favorites", Toast.LENGTH_SHORT).show();

                    mContext.getContentResolver().delete(MangaContract.ImagesContract.CONTENT_URI, MangaContract.ImagesContract.IMAGE_ID + " = ? ",
                            new String[]{mangaHitIdReal + ""});


                } else {
                    holder.img.setImageResource(R.drawable.ic_fav);
                    Toast.makeText(mContext, "Added to Favorites", Toast.LENGTH_SHORT).show();

                    ContentValues values = new ContentValues();
                    values.put(MangaContract.ImagesContract.IMAGE_ID, mangaHitIdReal);
                    values.put(MangaContract.ImagesContract.IMAGE_TITLE, finalTitle);
                    values.put(MangaContract.ImagesContract.MANGA_ID, id);
                    values.put(MangaContract.ImagesContract.IMG_THUMBNAIL, mangaThumbnailReal);

                    mContext.getContentResolver().insert(MangaContract.ImagesContract.CONTENT_URI, values);

                }
            }
        });


    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }


    public Cursor swapCursor(Cursor c) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (mCursor == c) {
            return null; // bc nothing has changed
        }
        Cursor temp = mCursor;
        this.mCursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }


    // Inner class for creating ViewHolders
    class TaskViewHolder extends RecyclerView.ViewHolder {


        private ImageView imageView;
        private CardView cardView;
        private ImageView img;
        private TextView mangaName;
        private MaterialRippleLayout materialRippleLayout;

        public TaskViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.mana_photo);
            cardView = itemView.findViewById(R.id.cardViewMain);
            mangaName = itemView.findViewById(R.id.manga_name);
            img = itemView.findViewById(R.id.fav);
            materialRippleLayout = itemView.findViewById(R.id.manga_cover);

        }
    }

//    public int getRandomColorCode(){
//
//        Random random = new Random();
//
//        return Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
//
//    }
}