package com.me.elektrichki;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.method.ScrollingMovementMethod;
import android.util.ArraySet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class MainActAdapter extends BaseAdapter {
    Context mContext;
    List<Train> objects;
    LayoutInflater lInflater;
    private MainContract.View cm;

    List<Integer> notifList;






    MainActAdapter(Context context, List<Train> trains, List<Integer> notifList) {
        this.notifList = notifList;
        mContext = context;
        objects = trains;
        lInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.cm = cm;









    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub


        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.item_main_act, parent, false);
        }





        Train train = objects.get(position);
        String startTime = train.getGetStartTime();
        String endTime = train.getGetEndTime();
        String titleTrain = train.getGetNumber()+" " +train.getGetTitle();

        String stops1;
        String stops = train.getGetStops();
        if (stops.length()>30)
            stops1 = stops.substring(0, 25) + "\n" + stops.substring(26, stops.length());

        else stops1 = stops;


        double durationMin = Double.valueOf(train.getGetDuration())/60;
        DecimalFormat myFormatter = new DecimalFormat("###");
        String durMin = myFormatter.format(durationMin);
        String duration = durMin+" мин";

        ImageView imageTrain = view.findViewById(R.id.icon);
        ImageView notification = view.findViewById(R.id.notif_in_list);
        TextView viewStartTime =  view.findViewById(R.id.startTime);
        TextView viewEndTime =  view.findViewById(R.id.endTime);
        TextView viewTitle =  view.findViewById(R.id.titleTrain);
        TextView viewStops =  view.findViewById(R.id.stops);
        TextView viewDuration =  view.findViewById(R.id.duration);
        String route1 = train.getFromDest() + " - " + train.getToDest();





                if (notifList.contains(position))

                    notification.setImageResource(R.drawable.ic_notifications_active_green_24dp);

                else notification.setImageResource(R.drawable.ic_notifications_active_grey_24dp);




        viewStartTime.setText(startTime);
        viewEndTime.setText(endTime);
        viewTitle.setText(titleTrain);
        if (titleTrain.contains("стандарт плюс"))
            imageTrain.setImageResource(R.drawable.ic_directions_transit_orange_24dp);
        else
            {if (titleTrain.contains("express"))
            imageTrain.setImageResource(R.drawable.ic_directions_transit_red_24dp);
        else imageTrain.setImageResource(R.drawable.ic_directions_transit_black_24dp);
        }

        viewStops.setText(stops1);
        viewStops.setMovementMethod(new ScrollingMovementMethod());
        viewDuration.setText(duration);





        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if (notification.getDrawable().getConstantState() ==
                        mContext.getResources().getDrawable(R.drawable.ic_notifications_active_green_24dp).getConstantState()) {

                    notifList.set(position, -2);
                    notification.setImageResource(R.drawable.ic_notifications_active_grey_24dp);
                }
                         else {

                   notifList.set(position, position);
                    notification.setImageResource(R.drawable.ic_notifications_active_green_24dp);

                }

            }
        });




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

    public List<Integer> getNotifList(){
        return notifList;
    }


}
