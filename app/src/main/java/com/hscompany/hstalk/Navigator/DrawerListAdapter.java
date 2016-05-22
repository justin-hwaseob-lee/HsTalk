package com.hscompany.hstalk.Navigator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hscompany.hstalk.R;

import java.util.ArrayList;

/**
 * Created by hs695 on 2016-02-05.
 */
public class DrawerListAdapter extends BaseAdapter
{
    Context con;
    LayoutInflater inflater;
    ArrayList<DrawerItem> arItem;
    int layout;

    public DrawerListAdapter(Context context, int aLayout, ArrayList<DrawerItem> aarItem) {
        con = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        arItem = aarItem;
        layout = aLayout;
    }

    @Override
    public int getCount() {


        return arItem.size();
    }

    @Override
    public Object getItem(int position) {
        return arItem.get(position).getText();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(layout, parent, false);
        }
        ImageButton img = (ImageButton) convertView.findViewById(R.id.drawer_itemImage);
        img.setImageResource(arItem.get(position).getImg());

        TextView txtName = (TextView) convertView.findViewById(R.id.drawer_itemText);
        txtName.setText(arItem.get(position).getText());

        return convertView;
    }
}
