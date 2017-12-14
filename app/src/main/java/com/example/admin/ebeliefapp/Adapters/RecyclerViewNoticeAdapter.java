package com.example.admin.ebeliefapp.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.admin.ebeliefapp.Models.NoticeList;
import com.example.admin.ebeliefapp.R;

import java.util.ArrayList;

/**
 * Created by rasp on 15/3/17.
 */

public class RecyclerViewNoticeAdapter extends RecyclerView.Adapter<RecyclerViewNoticeAdapter.RecyclerViewCategoryViewHolder> {
    public Context context;
    private ArrayList<NoticeList> arrayNoticeList;
    int row_index=-1;

    public RecyclerViewNoticeAdapter(Context context, ArrayList<NoticeList> arrayNoticeList) {
        this.arrayNoticeList = arrayNoticeList;
        this.context = context;
    }

    public class RecyclerViewCategoryViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        TextView txt_notice_url;
        LinearLayout ll_container;
        private SparseBooleanArray selectedItems = new SparseBooleanArray();
        public RecyclerViewCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            txt_notice_url = (TextView) itemView.findViewById(R.id.txt_notice_url);
            ll_container = (LinearLayout) itemView.findViewById(R.id.ll_container);
        }

        @Override
        public void onClick(View v) {

        }
    }

    @NonNull
    @Override
    public RecyclerViewCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notice_list_item, parent, false);

        RecyclerViewCategoryViewHolder recyclerViewLawyersListViewHolder = new RecyclerViewCategoryViewHolder(view);
        return recyclerViewLawyersListViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewCategoryViewHolder holder, final int position) {
        holder.txt_notice_url.setTag(position);
        holder.ll_container.setTag(position);

        holder.txt_notice_url.setText(arrayNoticeList.get(position).getID() + " : " + arrayNoticeList.get(position).getUrl());

    }

    @Override
    public int getItemCount() {
        return arrayNoticeList.size();
    }


}

