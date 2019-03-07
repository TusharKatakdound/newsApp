package com.androtush.newsapp.Fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androtush.newsapp.Model.EveryThingModel;
import com.androtush.newsapp.R;
import com.androtush.newsapp.Response.EveryThingNewsResponse;
import com.androtush.newsapp.Response.TopHeadlineNewsResponse;
import com.androtush.newsapp.Utils.DateUtils;
import com.androtush.newsapp.Utils.IoUtils;
import com.androtush.newsapp.View.EveryThingView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;


public class EverythingFragment extends Fragment implements EveryThingView {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private EveryThingModel everyThingModel;
    private RecyclerView mTopHeadlineRecyclerView;
    private TopHeadLineAdapter mAdapter;
    private ProgressDialog dialog;

    public EverythingFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static EverythingFragment newInstance(String param1, String param2) {
        EverythingFragment fragment = new EverythingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_everything, container, false);

        init(view);

        return view;
    }

    public void init(View view){
        dialog = IoUtils.getProgressDialog(getContext());
        dialog.show();
        everyThingModel = new EveryThingModel(this);
        everyThingModel.getEverything("bitcoin","2019-03-07","popularity","2f5d94003b614fc29af38e4356e4364f");
        mTopHeadlineRecyclerView = (RecyclerView) view.findViewById(R.id.top_headlinerecycler_view);
    }


    @Override
    public void onSuccess(EveryThingNewsResponse everyThingNewsResponse) {
        dialog.dismiss();
        mAdapter = new TopHeadLineAdapter(everyThingNewsResponse.getArticles());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mTopHeadlineRecyclerView.setLayoutManager(mLayoutManager);
        mTopHeadlineRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mTopHeadlineRecyclerView.setAdapter(mAdapter);
    }


    @Override
    public void onFailuar(String message) {
        dialog.dismiss();
        Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show();
    }

    public class TopHeadLineAdapter extends RecyclerView.Adapter<TopHeadLineAdapter.TopHeadlineViewHolder> {

        private ArrayList<EveryThingNewsResponse.Articles> articles;

        public class TopHeadlineViewHolder extends RecyclerView.ViewHolder {
            private TextView mTxtTitle,mTxtDescription,mTxtAuthor,mTxtPostedOn,mTxtShare;
            private ImageView mImgPhoto;
            public TopHeadlineViewHolder(View view) {
                super(view);
                mTxtTitle = (TextView) view.findViewById(R.id.txtTitle);
                mTxtDescription = (TextView) view.findViewById(R.id.txtDescription);
                mTxtAuthor = (TextView) view.findViewById(R.id.txtAuthor);
                mTxtPostedOn = (TextView) view.findViewById(R.id.txtPostedOn);
                mTxtShare = (TextView) view.findViewById(R.id.txtShare);
                mImgPhoto = (ImageView) view.findViewById(R.id.imgPhoto);
            }
        }


        public TopHeadLineAdapter(ArrayList<EveryThingNewsResponse.Articles> articles) {
            this.articles = articles;
        }

        @Override
        public TopHeadLineAdapter.TopHeadlineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.article_list_row, parent, false);

            return new TopHeadLineAdapter.TopHeadlineViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(TopHeadLineAdapter.TopHeadlineViewHolder holder, int position) {
            final EveryThingNewsResponse.Articles article = articles.get(position);
            holder.mTxtTitle.setText(article.getTitle());
            holder.mTxtDescription.setText(article.getDescription());
            if(article.getAuthor() != null) {
                holder.mTxtAuthor.setText(article.getAuthor());
            }else{
                holder.mTxtAuthor.setText(article.getSource().getName());

            }
            String removeT = article.getPublishedAt().replaceFirst("T"," ");
            String remoceZ = removeT.replace("Z","");

            holder.mTxtPostedOn.setText(DateUtils.getTimeAgo(remoceZ,getContext()));

            holder.mTxtShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT,
                            article.getTitle()+" \nfor more details "+article.getUrl());
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);
                }
            });

            Glide.with(getContext()).load(article.getUrlToImage()).into(holder.mImgPhoto);
        }

        @Override
        public int getItemCount() {
            return articles.size();
        }
    }

}