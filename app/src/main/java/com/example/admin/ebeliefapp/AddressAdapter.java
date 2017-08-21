package com.example.admin.ebeliefapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

class NoticeListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<String> noticeArray;
    boolean flag = false;
    int int_primary;

    public NoticeListAdapter(Context mContext, ArrayList<String> noticeArray) {
        this.mContext = mContext;
        this.noticeArray = noticeArray;
    }

    @Override
    public int getCount() {
        return noticeArray.size();
    }

    @Override
    public Object getItem(int position) {
        return noticeArray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder;
        if (convertView == null) {
            convertView = ((LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.list_item, null);
            holder = new Holder();
            holder.notice_link = (TextView) convertView.findViewById(R.id.notice_link);
            SpannableString content = new SpannableString(noticeArray.get(position));
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            holder.notice_link.setText(content);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        return convertView;
    }

    class Holder {
        public TextView notice_link;
    }

}
