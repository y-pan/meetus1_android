package com.example.yun.meetup.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.yun.meetup.R;
import com.example.yun.meetup.models.Event;
import com.example.yun.meetup.models.UserInfo;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by danie on 12/22/2017.
 */

public class MemberListViewAdapter extends BaseAdapter {

    private final List<UserInfo> mUsers;
    private Context mContext;

    public class MembersListViewHolder {

        final TextView memberName;

        public MembersListViewHolder(View view) {
            memberName = (TextView) view.findViewById(R.id.txt_item_member_name);
        }
    }

    public MemberListViewAdapter(List<UserInfo> users, Context context) {
        this.mUsers = users;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mUsers.size();
    }

    @Override
    public Object getItem(int position) {
        return mUsers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return new BigInteger(mUsers.get(position).get_id(), 16).longValue();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        MembersListViewHolder holder;

        if(convertView == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.lv_members_item, parent, false);
            holder = new MembersListViewHolder(view);
            view.setTag(holder);
        }else {
            view = convertView;
            holder = (MembersListViewHolder) view.getTag();
        }

        UserInfo userInfo = mUsers.get(position);

        holder.memberName.setText(userInfo.getName());

        return view;
    }
}
