package com.orchid.counter.allgames;

/**
 * Created by TDKJ02 on 2017/6/1.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.AppUtils;

import java.util.List;

/**
 * Created by TDKJ02 on 2017/5/26.
 */

public class GridViewAdapter extends BaseAdapter {
    private List<AppUtils.AppInfo> datalist;
    private Context                context;

    public GridViewAdapter(List<AppUtils.AppInfo> datalist, Context context) {
        this.datalist = datalist;
        this.context = context;
    }

    @Override
    public int getCount() {
        return datalist.size();
    }

    @Override
    public AppUtils.AppInfo getItem(int position) {
        return datalist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       final AppUtils.AppInfo appInfo = datalist.get(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_item, null);
            viewHolder.iv_item = (ImageView) convertView.findViewById(R.id.iv_item);
            viewHolder.tv_item = (TextView) convertView.findViewById(R.id.tv_item);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tv_item.setText( appInfo.getName());
        viewHolder.iv_item.setImageDrawable(appInfo.getIcon());


        return convertView;
    }

    private class ViewHolder {
        ImageView iv_item;
        TextView tv_item;
    }

}

