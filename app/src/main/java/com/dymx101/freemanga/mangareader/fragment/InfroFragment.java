package com.dymx101.freemanga.mangareader.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dymx101.freemanga.mangareader.R;
import com.dymx101.freemanga.mangareader.Service.MessageEvent;
import com.dymx101.freemanga.mangareader.activity.DetailActivity;
import com.dymx101.freemanga.mangareader.model.Detail;
import com.dymx101.freemanga.mangareader.retrofit.MangaRequest;
import com.dymx101.freemanga.mangareader.retrofit.MangaUtils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InfroFragment extends Fragment {

    private TextView mangaName;
    private TextView authorName;
    private TextView status;
    private TextView categories;
    private TextView description;
    private ImageView mangaCover;
    private TextView lastUpdated;
    private TextView dateCreated;
    private TextView chapterTotal;
    private Snackbar snackbar, snackbarjson;
    private Boolean snackbool = true;
    private ProgressBar progressBar;
    private CoordinatorLayout coordinatorLayout;
    public InfroFragment() {
    }

    @Override
    public void onStart()
    {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event)
    {

//        if (event.message.equals("nointernet")) {
//            if (snackbool) {
//                snackbar();
//            }
//        }
//        if (event.message.equals("internet")) {
//            if (!snackbool) {
//                snackbar.dismiss();
//                snackbool = true;
//
//            }
//
//        }

    }
    public void snackbar()
    {
        snackbar = Snackbar.make(coordinatorLayout,R.string.snackmsg,Snackbar.LENGTH_INDEFINITE);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(getResources().getColor(R.color.snackBarInternet));
        snackbar.show();

        snackbool = false;

    }

    public void setSnackbarjson()
    {
        snackbarjson = Snackbar.make(coordinatorLayout,R.string.snackJsonMsg,Snackbar.LENGTH_LONG);
        View snackBarView = snackbarjson.getView();
        snackBarView.setBackgroundColor(getResources().getColor(R.color.snackBarjson));
        snackbarjson.show();



    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_infro, container, false);
    }


    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.coordinatorLayout);
        mangaName = view.findViewById(R.id.mangaName);
        authorName = view.findViewById(R.id.author_name);
        mangaCover = view.findViewById(R.id.image_cover);
        status = view.findViewById(R.id.status);
        categories = view.findViewById(R.id.categories);
        description = view.findViewById(R.id.description);
        lastUpdated = view.findViewById(R.id.dateUpdated);
        dateCreated = view.findViewById(R.id.dateCreated);
        chapterTotal = view.findViewById(R.id.chaptNumbers);
        progressBar = view.findViewById(R.id.progressbar);

        //get the manga id from activity
        DetailActivity activity = (DetailActivity) getActivity();
        String getData = activity.sendData();

        AdView mAdView = view.findViewById(R.id.adDetailView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        MangaRequest client = MangaUtils.getAPIService();
        Call<Detail> call = client.getDetail(getData);

        call.enqueue(new Callback<Detail>() {
            @Override
            public void onResponse(@NonNull Call<Detail> call, @NonNull Response<Detail> response) {


                progressBar.setVisibility(View.GONE);

                Detail detail = response.body();
                String cover = "https://cdn.mangaeden.com/mangasimg/" + detail.getImage();

                mangaName.setText(detail.getTitle());
                authorName.setText(detail.getAuthor());
                Picasso.with(getContext()).load(cover).placeholder(R.drawable.bookicon).into(mangaCover);
                if (detail.getStatus() == 1) {
                    status.setText("Ongoing");
                }else {
                    status.setText("Completed");
                }


                for (int i = 0; i < detail.getCategories().size(); i++) {
                    categories.append(detail.getCategories().get(i) + ", ");
                }

                description.setText(detail.getDescription());

                if (detail.getLastChapterDate() == null){
                    lastUpdated.setText("-----");
                }else {
                    lastUpdated.setText(formatDate(detail.getLastChapterDate()));
                }

                if (detail.getCreated() == null){
                    dateCreated.setText("-----");
                }else {
                    dateCreated.setText(formatDate(detail.getCreated()));
                }

                chapterTotal.setText(String.valueOf(detail.getChaptersLen()));

            }

            @Override
            public void onFailure(@NonNull Call<Detail> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                snackbar();
            }
        });

    }

    public String formatDate(long format) {
        Date date = new Date(format * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-4"));
        return sdf.format(date);
    }

}

