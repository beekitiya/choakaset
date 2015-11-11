package com.f55160175.choakaset;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by User on 10/29/2015.
 */
public class AccountAdapter extends BaseAdapter {

    private List<HashMap<String, String>> list;
    private Context context;
    private int[] type;

    public AccountAdapter(Context context, List<HashMap<String,String>> list, int[] type) {
        this.context = context;
        this.list = list;
        this.type = type;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        LayoutInflater mInflater = LayoutInflater.from(context);
        //View view = null;

        if (type[position] == 0) {
            convertView = mInflater.inflate(R.layout.acc_header, null);
            // Gets the name of the city
            String date = list.get(position).get("data");
            ImageView image = (ImageView) convertView.findViewById(R.id.calendar);

            /*if (cityName.equals("20/05/2015")) {
                image.setImageResource(R.drawable.beijing);
            } else if (cityName.equals("23/06/2015")) {
                image.setImageResource(R.drawable.shanghai);

            } */
            TextView acc_date = (TextView) convertView.findViewById(R.id.date);
            acc_date.setText(date);
        } else {
            convertView = mInflater.inflate(R.layout.content_item, null);
            // Get data
            String content = list.get(position).get("data");
            // Separate data
            String[] items = content.split(",");

            System.out.println(items);

            TextView weather = (TextView) convertView.findViewById(R.id.detail);
            weather.setText(items[0]);
            TextView date = (TextView) convertView.findViewById(R.id.price);
            date.setText(items[0]);
        }
        return convertView;
    }
}
