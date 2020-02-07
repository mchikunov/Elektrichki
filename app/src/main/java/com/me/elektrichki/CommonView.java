package com.me.elektrichki;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class CommonView extends AppCompatActivity implements MainContract.View {

    private TextView route;
    private LinearLayout linearLayout;
    private ListView routeList;
    private LinearLayout routeLinearLayout;
    List<String> routeTimes = null;
    ImageView imageView, settingsButton;


    List<String> trainDepartureTimes;
    Long currTime;
    Presenter presenter;
    ArrayList<Route> routes1;
    List<String> routesSource = new ArrayList<>();
    List<DataForRetrofit> dataForRetrofitList;
    DataAdapter dataAdapter;
    List<List<Train>> savedTrains;
    List<Integer> routeIds;
    List<Route> allRoutes;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;


    String nameFrom, nameTo, codeFrom, codeTo;
    int routeId;
    int whereFrom;
    int ids;
    int posCheckforNotMultipleRunAlarm;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_view);

        routeList = findViewById(R.id.routeList);

        route = (TextView) findViewById(R.id.route);

        imageView = findViewById(R.id.addButt);
        settingsButton = findViewById(R.id.addSettings);

        posCheckforNotMultipleRunAlarm = -1;


        whereFrom = 0;
        ids = 0;
        sharedPreferences = getDefaultSharedPreferences(getApplicationContext());

        //  isFirst(); //run


        //   boolean isFirst = sharedPreferences.getBoolean("isFirst", false);
        // searchRoute = "Кунцево - Инновационный центр (бывш. Трёхгорка)";


        savedTrains = new ArrayList<>();
        routeIds = new ArrayList<>();
        routesSource = new ArrayList<>();

        presenter = new Presenter(this);

        //  if (isFirst) {
        //      editor = sharedPreferences.edit();
        //      editor.putBoolean("isFirst", false);
        //      editor.commit();
    //}
            presenter.getCount(this); //counts and run add codes if 0





        presenter.getAllRoutes(CommonView.this);


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int delayMin = sharedPreferences.getInt("get1", 25);
                int delayMin2 = sharedPreferences.getInt("get2", 5);
                int delayInterval = sharedPreferences.getInt("getInterval", 25);



                AlertDialog.Builder builderRul = new AlertDialog.Builder(CommonView.this);
                ScrollView view = (ScrollView) getLayoutInflater()
                        .inflate(R.layout.settings_layout, null);


                EditText get1 = view.findViewById(R.id.get1);
                get1.setText(String.valueOf(delayMin));



                get1.setTypeface(Typeface.createFromAsset(getAssets(), "nick.ttf"));

                EditText get2 = view.findViewById(R.id.get2);
                get2.setText(String.valueOf(delayMin2));



                get2.setTypeface(Typeface.createFromAsset(getAssets(), "nick.ttf"));

                EditText getInterval = view.findViewById(R.id.getInterval);
                getInterval.setText(String.valueOf(delayInterval));



                getInterval.setTypeface(Typeface.createFromAsset(getAssets(), "nick.ttf"));

                builderRul

                        .setView(view)
                        .setCancelable(true)

                        .setPositiveButton("Сохранить..",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {

                                        String stGet1 = get1.getText().toString();
                                        int iget1 = stGet1.equals("") ? 0 : Integer.parseInt(stGet1);

                                        String stGet2 = get2.getText().toString();
                                        int iget2 = stGet2.equals("") ? 0 : Integer.parseInt(stGet2);

                                        String interval = getInterval.getText().toString();
                                        int interv = interval.equals("") ? 0 : Integer.parseInt(interval);

                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putInt("get1", iget1);
                                        editor.putInt("get2", iget2);
                                        editor.putInt("getInterval", interv);
                                        editor.apply();
                                        posCheckforNotMultipleRunAlarm = -1;
                                    }
                                }
                        )

                        .setNegativeButton("Close",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });


                AlertDialog alertRul = builderRul.create();
                alertRul.show();
                alertRul.getWindow().setBackgroundDrawableResource(R.color.yellow_color);
               // alertRul.getWindow().setLayout(400, 700);
            }
        });

    }


    public void prepareRoutes() {


        Timer timerTask = new Timer();
        timerTask.schedule(new TimerTask() {
            @Override
            public synchronized void run() {

                savedTrains = new ArrayList<>();
                routeIds = new ArrayList<>();
                routes1 = new ArrayList<>();

                showRoutes(routesSource);


            }


        }, 40000);


    }


    public void prepareRoutesQuickLoad() {
        int i = 0;
        for (Route r : allRoutes
                ) {
            presenter.getDataFromDatabase(r.getFromDest(), r.getToDest(), i, CommonView.this);

            i++;
        } //first quick load


    }


    public void getAllRoutes(List<Route> routes) { // this comes from presenter on first call from this activity
        allRoutes = new ArrayList<>();
        routesSource = new ArrayList<>();
        savedTrains = new ArrayList<>();
        allRoutes = routes;
        if (allRoutes == null || allRoutes.size() == 0) {// if no routes start mainact to search, else start this with routes
            Intent i = new Intent(getApplicationContext(),
                    MainActivity.class);
            startActivity(i);
        } else {
            for (Route r : allRoutes
                    ) {
                StringBuilder sb = new StringBuilder();
                routesSource.add(sb.append(r.getFromDest()).append(" - ").append(r.getToDest()).toString());
            } //added all routes to string


            prepareRoutesQuickLoad();
            prepareRoutes();
        }
    }

    @Override
    public void getRoute(Route route) {

    }

    public synchronized void showRoutes(List<String> routes) {
        //  routes1 = new ArrayList<>();
        dataForRetrofitList = new ArrayList<>();
        routeId = 0;


        for (String rout : routes
                ) {
            String[] arr = rout.split(" - ");

            presenter.loadSearchList(arr[0], CommonView.this, 1, routeId);
            presenter.loadSearchList(arr[1], CommonView.this, 2, routeId);
            routeId++;


        }


    }


    public void showToast(String text) {
        //создаём и отображаем текстовое уведомление
        Toast toast = Toast.makeText(getApplicationContext(),
                text,
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @Override
    public synchronized void loadSearchData(List<CodeNameCoordination> trainList, int itemId, int routeId) {
        List<DataForRetrofit> dataForRetrofitListOUT = new ArrayList<>();

        if (itemId == 1) {
            nameFrom = trainList.get(0).getNameE();
            codeFrom = trainList.get(0).getCodeE();
            DataForRetrofit data = new DataForRetrofit(nameFrom, "", codeFrom, "", itemId, routeId);
            dataForRetrofitList.add(data);
        } else {
            nameTo = trainList.get(0).getNameE();
            codeTo = trainList.get(0).getCodeE();
            DataForRetrofit data = new DataForRetrofit("", nameTo, "", codeTo, itemId, routeId);
            dataForRetrofitList.add(data);


        }


        if (dataForRetrofitList.size() == routesSource.size() * 2) {

            DataForRetrofit dataPrev, dataCurrent = null;

            Collections.sort(dataForRetrofitList, (o1, o2) -> {
                int res1 = o1.getRouteId().compareTo(o2.getRouteId());

                if (res1 != 0) {
                    return res1;
                }
                return o1.getItemId().compareTo(o2.getItemId());
            });

            for (int i = 0; i < dataForRetrofitList.size(); i++) {
                dataPrev = dataForRetrofitList.get(i);
                dataCurrent = dataForRetrofitList.get(i + 1);

                dataCurrent.setNameFrom(dataPrev.getNameFrom());
                dataCurrent.setCodeFrom(dataPrev.getCodeFrom());

                dataForRetrofitListOUT.add(dataCurrent);
                i++;
            }

            for (DataForRetrofit data : dataForRetrofitListOUT
                    ) {

                presenter.getDataFromAPI(data.getNameFrom(), data.getNameTo(), data.getCodeFrom(), data.getCodeTo(), data.getRouteId(), CommonView.this);
            }


        }


    }

    @Override
    public synchronized void showTrainList(List<Train> trains, int routeId) {

        if (trains.size()!=0) {
            savedTrains.add(trains);

            routeIds.add(routeId);
        }

        if (ids == routesSource.size() - 1) {
            ids = -1;
            //run in circle autoupdate list
            Timer timerTask = new Timer();
            timerTask.schedule(new TimerTask() {
                @Override
                public synchronized void run() {
                    routeTimes = new ArrayList<>();
                    routes1 = new ArrayList<>();
                    int i = 0, rId;
                    for (List<Train> route : savedTrains
                            ) {
                        Train train0 = route.get(0);
                        String from = train0.getFromDest();
                        String to = train0.getToDest();

                        rId = routeIds.get(i);

                        routes1.add(new Route(from, to, getLeftTimes(route), rId));
                        i++;

                    }


                    Collections.sort(routes1, (o1, o2) -> o1.getRouteId().compareTo(o2.getRouteId()));
                    dataAdapter = new DataAdapter(getApplicationContext(), routes1, CommonView.this);
                    //posCheckforNotMultipleRunAlarm=-1;


                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            setDataAdapter();

                        }
                    });

                }


            }, 0, 60000);

        }


        ids++;
    }


    public List<String> getLeftTimes(List<Train> trains) {

        int delayInterval = sharedPreferences.getInt("getInterval", 25);

        routeTimes = new ArrayList<>();
        int i = 0;
        int posCurr = 0;
        int posIn = 0;
        String resultTimeLeft = "";
        Long t0 = 0L;

        for (Train train : trains) {
            currTime = System.currentTimeMillis();
            trainDepartureTimes = getTime(trains, new SimpleDateFormat("dd-M-yyyy HH:mm"));
            Long t1 = Long.valueOf(trainDepartureTimes.get(i));

            if (i >= 1) {
                t0 = Long.valueOf(trainDepartureTimes.get(i - 1));
            }

            if ((t1 - t0) < 0) t1 += (24 * 60 * 60 * 1000);
            Long timeLeft = t1 - currTime;


            long diffMinutes = timeLeft / (60 * 1000) % 60;
            long diffHours = timeLeft / (60 * 60 * 1000) % 24;


            if (diffHours >= 0 && diffMinutes > 0) {

                posIn++;
                if (posIn == 1) posCurr = i; //pos in List for mainActivity

                String formattedMinutes = String.format("%02d", diffMinutes);
                resultTimeLeft = diffHours + ":" + formattedMinutes;


                if ((t1 - t0) > (delayInterval * (60 * 1000)) && routeTimes.size() >= 1) {
                    String red = "R" + routeTimes.get(routeTimes.size() - 1);
                    routeTimes.set(routeTimes.size() - 1, red);


                }

                routeTimes.add("   " + train.getStartTime + "\n   " + resultTimeLeft);


            }


            i++;

        }
        routeTimes.add(String.valueOf(posCurr));
        return routeTimes;
    }


    public void setDataAdapter() {
        routeList.setAdapter(dataAdapter);
        dataAdapter.notifyDataSetChanged();
        route.setText("");

    }


    public void updateList(int position) {
        String fromDest = routes1.get(position).getFromDest();
        String toDest = routes1.get(position).getToDest();
        for (Iterator<List<Train>> iterator = savedTrains.iterator(); iterator.hasNext(); ) {
            List<Train> l = iterator.next();
            if (l.get(0).getFromDest().equals(fromDest) && l.get(0).getToDest().equals(toDest)) {
                iterator.remove();
            }
        }
        routes1.remove(position);
        dataAdapter.notifyDataSetChanged();
    }

    @Override
    public synchronized void count(int count) {
       // route.setText(String.valueOf(count));
        route.setText("Загрузка...");

        if (count<16039) {
            presenter.deleteAllTrains();
            presenter.deleteAll(); //deletes and add codes
            presenter.deleteAllinRoute(CommonView.this);
        }

    }

    @Override
    public List<String> getTime(List<Train> routes, SimpleDateFormat sdf) {
        List<String> times = new ArrayList<>();

        for (Train train : routes
                ) {
            String time = train.getGetStartTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            String dateString = day + "-" + month + "-" + year + " " + time;
            // add time from arraylist!!

            try {
                //formatting the dateString to convert it into a Date
                Date date = sdf.parse(dateString);
                String startTime = String.valueOf(date.getTime());
                times.add(startTime);


            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


        return times;
    }

    private boolean isNull(Object obj) {
        return obj != null;
    }


    public void deleteRoute(String nameFrom, String nameTo) {

        presenter.deleteRoute(CommonView.this, nameFrom, nameTo);


    }


    public void runAlarm(Integer pos) {

        if (pos!=posCheckforNotMultipleRunAlarm) {
            List<TransferTime> timesFinal = new ArrayList<>();
            List<String> times = new ArrayList<>();



            for (Route r : routes1
                    ) {
                if (pos.equals(r.getRouteId())) {

                    String frD = r.getFromDest();
                    String toD = r.getToDest();
                    for (List<Train> tr : savedTrains
                            ) {

                        if (tr.get(0).getFromDest().equals(frD) && tr.get(0).getToDest().equals(toD)) {


                           if  (sharedPreferences.getInt("get1", 0)!=0) {

                               times = getTimesAlarms(tr, 1);
                               for (String t : times
                                       ) {
                                   TransferTime transferTime = new TransferTime(t, 1);
                                   timesFinal.add(transferTime);
                               }
                           }


                            if  (sharedPreferences.getInt("get2", 0)!=0) {

                                times = getTimesAlarms(tr, 2);
                                for (String t : times
                                        ) {
                                    TransferTime transferTime = new TransferTime(t, 2);
                                    timesFinal.add(transferTime);
                                }
                            }


                            if (timesFinal.size()==0) {
                                posCheckforNotMultipleRunAlarm = pos;
                               return;
                            }



                            Collections.sort(timesFinal, (o1, o2) -> {
                                int res1 = o1.getTime().compareTo(o2.getTime());

                                if (res1 != 0) {
                                    return res1;
                                }
                                return o1.getOrder().compareTo(o2.getOrder());
                            });


                        }

                    }

                }
            }


            presenter.startWorkManager(CommonView.this, timesFinal);

            // presenter.startAlarmBroadcastReceiver(CommonView.this, times, 0);

        }
        posCheckforNotMultipleRunAlarm = pos;

    }


    public List<String> getTimesAlarms(List<Train> trains, int idTime) {
        int delayMin=1;
        List<String> routeTimes = new ArrayList<>();
        int i = 0;
        Long timePostCurrent = null;
        Long currTime = System.currentTimeMillis();
        Long t0 = 0L;
        List<String> trainDepartureTimes = getTime(trains, new SimpleDateFormat("dd-M-yyyy HH:mm"));

        if (idTime==1)
            delayMin = sharedPreferences.getInt("get1", 0);
        else
            delayMin = sharedPreferences.getInt("get2", 0);


        Long delay = (delayMin * 60 * 1000L); //25 min
        for (Train train : trains) {


            Long t1 = Long.valueOf(trainDepartureTimes.get(i)) - delay;
            if (t1 > currTime) {
                timePostCurrent = t1;


                Calendar train1 = Calendar.getInstance();
                train1.setTimeInMillis(timePostCurrent);
                int hour = train1.get(Calendar.HOUR_OF_DAY);
                int minute = train1.get(Calendar.MINUTE);
                int day = train1.get(Calendar.DAY_OF_MONTH);



                if (i >= 1) {
                    t0 = Long.valueOf(trainDepartureTimes.get(i - 1)) - delay;
                }

                if ((t1 - t0) < 0) {
                    t1 += (24 * 60 * 60 * 1000);
                    timePostCurrent = t1;
                }

                routeTimes.add(String.valueOf(timePostCurrent));

            }

            i++;


        }


        return routeTimes;
    }


    public void destroyAlarms() {

        presenter.onDestroy();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //     presenter.onDestroy();


    }

    public void updateAdapter() {
        dataAdapter.notifyDataSetChanged();
    }

    public void isFirst() {


    editor =sharedPreferences.edit();
        editor.putBoolean("isFirst",true);
        editor.commit();
}
}
