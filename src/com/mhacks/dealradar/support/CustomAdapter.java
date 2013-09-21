package com.mhacks.dealradar.support;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mhacks.dealradar.DealRadar;
import com.mhacks.dealradar.R;

import java.util.ArrayList;

/**
 * Created by Niraj Venkat on 9/21/13.
 */
public class CustomAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<String> values;

    public CustomAdapter(Context context, ArrayList<String> values) {
        super(context, R.layout.drawer_list_item, values);
        this.context = context;
        this.values = values;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.drawer_list_item, parent, false);

        String filter = values.get(position);

        TextView textView1 = (TextView) rowView.findViewById(R.id.drawer_textview);
        textView1.setTypeface(DealRadar.myriadProSemiBold);
        textView1.setText(filter);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.drawer_imageview);
        if (filter.equalsIgnoreCase("Food")) {
            imageView.setImageResource(R.drawable.food_menu_item);
        } else if (filter.equalsIgnoreCase("Clothing")) {
            imageView.setImageResource(R.drawable.clothing_menu_item);
        } else if (filter.equalsIgnoreCase("Tech")) {
            imageView.setImageResource(R.drawable.tech_menu_item);
        } else if (filter.equalsIgnoreCase("Pets")) {
            imageView.setImageResource(R.drawable.pets_menu_item);
        } else if (filter.equalsIgnoreCase("Movies")) {
            imageView.setImageResource(R.drawable.movies_menu_icon);
        } else if (filter.equalsIgnoreCase("Games")) {
            imageView.setImageResource(R.drawable.games_menu_item);
        } else if (filter.equalsIgnoreCase("Toys")) {
            imageView.setImageResource(R.drawable.toys_menu_item);
        }
        else
        {
            imageView.setImageResource(R.drawable.all_menu_item);
        }

        return rowView;
    }
}

