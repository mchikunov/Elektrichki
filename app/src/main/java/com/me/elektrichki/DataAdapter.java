package com.me.elektrichki;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class DataAdapter extends BaseAdapter {

    Context mContext;
    ArrayList<Route> objects;
    LayoutInflater lInflater;
    List<String> leftTime;
    private TextView route;
    private LinearLayout linearLayout;
    private MainContract.View cm;
    List<Integer> posinList;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;





    DataAdapter(Context context, ArrayList<Route> routes, MainContract.View cm) {
        mContext = context;
        objects = routes;
        lInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.cm = cm;
        posinList = new ArrayList<>();

        for (Route r: objects
             ) {
         List<String> l =   r.getRouteTimes();
            int s = l.size()-1;
            posinList.add(Integer.parseInt(l.get(s)));//last element is the pos in list for main view

            sharedPreferences = getDefaultSharedPreferences(mContext);

        }







    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub


        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.item_horiz_scroll, parent, false);
        }





        Route route = objects.get(position);
        String from = route.getFromDest();
        String to = route.getToDest();
        List<String> timesList = route.getRouteTimes();

        if (timesList.size()!=0)
        timesList.remove(timesList.size()-1);

        ImageView imageNotif = view.findViewById(R.id.common_notif);
      LinearLayout linearLayout = view.findViewById(R.id.scr_layout);
      linearLayout.removeAllViews();
      TextView textView =  view.findViewById(R.id.route1);
      textView.setText(from+" - "+to);
       for (String str: timesList
             ) {
           TextView tv = new TextView(mContext);
           tv.setText(str);
           tv.setTextSize(1, 16);
           tv.setTextColor(mContext.getResources().getColor(R.color.green_color));

           if (str.substring(0, 1).equals("R")) {

               str = str.substring(1, str.length());
               tv.setText(str);
               tv.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
           }


           linearLayout.addView(tv);


       }


        linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Integer n = sharedPreferences.getInt("posNotifCommon1", -2);
                if (n==position) {
                    putToShareedPrefs(-2);
                    cm.destroyAlarms();
                }
                if (n>position) putToShareedPrefs(n-1);


                cm.updateList(position);
                String getFrom = route.getFromDest();
                String getTo = route.getToDest();
                cm.deleteRoute(getFrom, getTo);

                return true;
            }
        });


        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Integer n = sharedPreferences.getInt("posNotifCommon1", -2);
                if (n==position) {
                    putToShareedPrefs(-2);
                    cm.destroyAlarms();
                }
                if (n>position) putToShareedPrefs(n-1);


                cm.updateList(position);
                String getFrom = route.getFromDest();
                String getTo = route.getToDest();
                cm.deleteRoute(getFrom, getTo);

                return true;
            }
        });




           linearLayout.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Intent i = new Intent(mContext, MainActivity.class);
                   i.putExtra("from", from);
                   i.putExtra("to", to);
                   i.putExtra("pos", posinList.get(position));
                   mContext.startActivity(i);

               }
           });


        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, MainActivity.class);
                i.putExtra("from", from);
                i.putExtra("to", to);
                i.putExtra("pos", posinList.get(position));
                mContext.startActivity(i);

            }
        });





           imageNotif.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {



                   if (imageNotif.getDrawable().getConstantState() ==
                           mContext.getResources().getDrawable(R.drawable.ic_notifications_active_green_24dp).getConstantState()) {

                       putToShareedPrefs(-2);
                       cm.destroyAlarms();
                       imageNotif.setImageResource(R.drawable.ic_notifications_active_grey_24dp);
                       cm.updateAdapter();
                   }
                   else {


                        putToShareedPrefs(position);
                       cm.runAlarm(position);
                       imageNotif.setImageResource(R.drawable.ic_notifications_active_green_24dp);
                       cm.updateAdapter();

                   }



               }
           });


            Integer n = sharedPreferences.getInt("posNotifCommon1", -2);

            if (n==position) {
                imageNotif.setImageResource(R.drawable.ic_notifications_active_green_24dp);
                cm.runAlarm(n);
            }

            else {
                imageNotif.setImageResource(R.drawable.ic_notifications_active_grey_24dp);
            }













    return view;
    }


    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int i) {
        return objects.get(i);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void putToShareedPrefs(int nn){

        editor = sharedPreferences.edit();
        editor.putInt("posNotifCommon1", nn);
        editor.commit();

    }




}