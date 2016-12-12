package com.jiaying.workstation.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jiaying.workstation.R;
import com.jiaying.workstation.entity.NurseEntity;

import java.util.List;

/**
 * 作者：lenovo on 2016/1/20 00:16
 * 邮箱：353510746@qq.com
 * 功能：护士列表adapter
 */
public class NurseAdapter extends BaseAdapter {
    private List<NurseEntity> mList;
    private Context mContext;

    public NurseAdapter(List<NurseEntity> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyHolder holder = null;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.nurse_item,null);
            holder = new MyHolder();
            holder.head_image = (SimpleDraweeView) convertView.findViewById(R.id.head_image);
            holder.name_txt = (TextView) convertView.findViewById(R.id.name_txt);
            holder.id_txt = (TextView) convertView.findViewById(R.id.id_txt);
            convertView.setTag(holder);
        }else{
            holder = (MyHolder) convertView.getTag();
        }
        holder.name_txt.setText(mList.get(position).getName());
        holder.id_txt.setText(mList.get(position).getId());

        if(!TextUtils.isEmpty(mList.get(position).getPhoto())){
            Uri uri = Uri.parse(mList.get(position).getPhoto());
            holder.head_image.setImageURI(uri);
        }
        return convertView;
    }

    static final  class MyHolder{
        SimpleDraweeView head_image;
        TextView name_txt;
        TextView id_txt;
    }
}
