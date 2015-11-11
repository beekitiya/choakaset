package com.f55160175.choakaset;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by User on 10/31/2015.
 */
public class ProblemAdapter extends BaseAdapter {
    private Context context;
    private Problem[] problem;

    public ProblemAdapter(Context c,Problem[] g){
        context = c;
        problem = g;
    }

    @Override
    public int getCount() {
        return problem.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater =(LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        if (view == null){
            view = inflater.inflate(R.layout.listview_problem,parent,false);
        }
        TextView detail = (TextView) view.findViewById(R.id.detail);
        TextView type = (TextView) view.findViewById(R.id.topicType);
        TextView status = (TextView) view.findViewById(R.id.statusProb);
        TextView date = (TextView) view.findViewById(R.id.dateProb);

        Problem m = problem[position];
        detail.setText(m.getTopic());
        type.setText(m.getType());
        status.setText(m.getStatus());
        date.setText(m.getDate());

        return view;
    }
}
