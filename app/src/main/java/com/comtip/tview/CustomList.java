package com.comtip.tview;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by TipRayong on 12/7/2559.
 */
public class CustomList extends ArrayAdapter {
    private ArrayList<String> headerNews;
    private Activity context;


    public CustomList(Activity context, ArrayList<String> headerNews) {
        super(context, R.layout.listview_custom , headerNews);
        this.context = context;
        this.headerNews = headerNews;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater =  context.getLayoutInflater();
        View listviewItem = inflater.inflate(R.layout.listview_custom,null,true);
        TextView textviewCustom = (TextView) listviewItem.findViewById(R.id.textviewCustom);
        textviewCustom.setText(headerNews.get(position));

        return  listviewItem;
    }
}




