package com.me.elektrichki;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import androidx.core.app.NotificationCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class MainActivity extends AppCompatActivity implements MainContract.View {

    private Presenter presenter;

    ListView searchList;
    SearchView userFilter, userFilter1;
    Disposable disposable;
    TextView userText, userText1;
    List<CodeNameCoordination> codeListSearch;
    LinearLayout linearLayout;
    String codeFrom, codeTo, nameFrom, nameTo;
    Button button;
    Boolean fromCommonView;
    ImageView add_fav;
    ImageView putNotification;
    List<DataForRetrofit> dataForRetrofitList;
    List<Train> globalTrains;
    int globalRouteId;
    int posInList;
    boolean isNotification;
    Set<String> notificationTimesPos;
    SharedPreferences sharedPreferences;
    List<Integer> notifList;
    List<String> times;
    MainActAdapter mainActAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchList = findViewById(R.id.searchList);
        userFilter = findViewById(R.id.userFilter);
        userFilter1 = findViewById(R.id.userFilter1);
        userText = findViewById(R.id.userText);
        userText1 = findViewById(R.id.userText1);
        linearLayout = findViewById(R.id.root_id);
        add_fav = findViewById(R.id.add_fav);


        putNotification = findViewById(R.id.put_notification);
        isNotification = true;
        notificationTimesPos = new HashSet<>();

        dataForRetrofitList = new ArrayList<>();
        notifList = new ArrayList<>();
        presenter = new Presenter(this);
        fromCommonView = false;



        Intent fromCommView = getIntent();
       nameFrom =  fromCommView.getStringExtra("from");
        nameTo =  fromCommView.getStringExtra("to");
        posInList =  fromCommView.getIntExtra("pos", 0);

        if (isNull(nameFrom) && isNull(nameTo)) {
            presenter.loadSearchList("%" + nameFrom + "%", MainActivity.this, 1, 0 );
            presenter.loadSearchList("%" + nameTo + "%", MainActivity.this, 2, 0 );
            fromCommonView=true;
        }


        add_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                notifList = mainActAdapter.getNotifList();
                Route route = new Route(nameFrom, nameTo, getFullTimes(globalTrains), notifList);
                presenter.insertRoute(route);


                Intent i = new Intent(getApplicationContext(),
                        CommonView.class);
                i.putExtra("routeSearch", nameFrom+" - "+nameTo);
                startActivity(i);
                MainActivity.this.finish();
            }
        });


        putNotification.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                notifList = new ArrayList<>();
                if (putNotification.getDrawable().getConstantState()==
                        getResources().getDrawable(R.drawable.ic_notifications_active_grey_24dp).getConstantState()){
                    int i =0;
                    for (Train t: globalTrains
                         ) {
                        notifList.add(i);
                        i++;
                    }

                    showTrainList(globalTrains, globalRouteId);
                    putNotification.setImageResource(R.drawable.ic_notifications_active_green_24dp);
                }

                else {


                    int i =0;
                    for (Train t: globalTrains
                            ) {
                        notifList.add(-2);
                        i++;
                    }


                    showTrainList(globalTrains, globalRouteId);
                    putNotification.setImageResource(R.drawable.ic_notifications_active_grey_24dp);
                }

            }
        });




      // presenter.deleteAll();
     //  firstCodesAdded();
  // presenter.getCount(this);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy HH:mm");




List<Train> routes = new ArrayList<>(Arrays.asList(new Train("", "", "", "","21:23", "", "",
                                                                "", "", ""),
                                                    new Train("", "", "", "","21:27", "", "",
                                                            "", "", "")));
times = getTime(routes, sdf); //convert from train to time to start times in alarm


       // Handler handler = new Handler();
       // showNewNotification(handler);

      //  startAlarmBroadcastReceiver(this, times, 0);


        observeSearch(userFilter, 1);
        observeSearch(userFilter1, 2);










        //https://api.rasp.yandex.net/v3.0/search/?apikey={ключ}&format=json&from=c146&to=c213&lang=ru_RU&page=1&date=2015-09-02


    }




    private void observeSearch(SearchView userFilter, int itemId) {
        int routeId = 0;

        disposable = RxSearch.fromView(userFilter)
                .debounce(300, TimeUnit.MILLISECONDS)
                .filter(text -> !text.isEmpty() && text.length() >= 2)
                .map(text -> text.toLowerCase().trim())
                .distinctUntilChanged()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {

                    presenter.loadSearchList("%" + result + "%", MainActivity.this, itemId, routeId );

                });
    }


    @Override
    public synchronized void loadSearchData(List<CodeNameCoordination> trainList, int itemId, int routeId) {


        if (fromCommonView) {

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


            if (dataForRetrofitList.size() == 2) {
                DataForRetrofit dataPrev, dataCurrent = null;

                Collections.sort(dataForRetrofitList, (o1, o2) -> {
                    int res1 = o1.getRouteId().compareTo(o2.getRouteId());

                    if (res1 != 0) {
                        return res1;
                    }
                    return o1.getItemId().compareTo(o2.getItemId());
                });

                String fromData = dataForRetrofitList.get(0).getNameFrom();
                String toData = dataForRetrofitList.get(1).getNameTo();

                userText.setText(fromData);
                userText1.setText(toData);


                for (int i = 0; i < dataForRetrofitList.size(); i++) {
                    dataPrev = dataForRetrofitList.get(i);
                    dataCurrent = dataForRetrofitList.get(i + 1);

                    dataCurrent.setNameFrom(dataPrev.getNameFrom());
                    dataCurrent.setCodeFrom(dataPrev.getCodeFrom());

                    dataForRetrofitListOUT.add(dataCurrent);
                    i++;
                }


                fromCommonView = false;

                for (DataForRetrofit data : dataForRetrofitListOUT
                        ) {
                    presenter.getDataFromAPI(data.getNameFrom(), data.getNameTo(), data.getCodeFrom(), data.getCodeTo(), data.getRouteId(), MainActivity.this);
                }
            }

            return;
        }

        List<String> nameList = new ArrayList<>();
        for (CodeNameCoordination code: trainList
             ) {
           nameList.add(code.getNameE());
        }

       ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.item, nameList);
        searchList.setAdapter(adapter);
        searchList.setVisibility(View.VISIBLE);


        searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                CodeNameCoordination coordination = trainList.get(position);
                if (itemId==1) {
                    userText.setText(coordination.getNameE());
                    userFilter.setQuery("", false);
                    userFilter.clearFocus();
                    linearLayout.requestFocus();
                    searchList.setVisibility(View.INVISIBLE);
                    add_fav.setImageResource(R.drawable.ic_grade_light_grey_24dp);

                    codeFrom = coordination.getCodeE();
                    nameFrom = coordination.getNameE();
                }
                else {
                    userText1.setText(coordination.getNameE());
                    userFilter1.setQuery("", false);
                    userFilter1.clearFocus();
                    linearLayout.requestFocus();
                    searchList.setVisibility(View.INVISIBLE);
                    add_fav.setImageResource(R.drawable.ic_grade_light_grey_24dp);

                    codeTo = coordination.getCodeE();
                    nameTo = coordination.getNameE();

                    presenter.getDataFromAPI(nameFrom, nameTo, codeFrom, codeTo, routeId, MainActivity.this);
                }


            }
        });
    }

    @Override
    public void showTrainList(List<Train> trains, int routeId){


            globalTrains = trains;
            globalRouteId = routeId;


            if (notifList.size()==0) {
                int i = 0;
                for (Train t : globalTrains
                        ) {
                    notifList.add(i);
                    i++;
                }
            }

       mainActAdapter = new MainActAdapter(this, trains, notifList);
        searchList.setAdapter(mainActAdapter);
        searchList.setSelection(posInList);
        searchList.setVisibility(View.VISIBLE);
       // startAlarmBroadcastReceiver(this, getLeftTimes(trains), 0);


    }

//забор данных всех станций кодов-названий!!! один раз делать







public void getNotification(){

    NotificationCompat.Builder builder =
            new NotificationCompat.Builder(this)
                    .setSmallIcon(android.R.drawable.ic_dialog_email)
                    .setContentTitle("Title change")
                    .setContentText("Notification text change")
    .setWhen(System.currentTimeMillis()+600000L);

    NotificationManager notificationManager =
            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);


    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        String channelId = "Your_channel_id";
        NotificationChannel channel = null;

            channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title", NotificationManager.IMPORTANCE_HIGH
            );

            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }


    notificationManager.notify(0, builder.build());




}


    public void showNewNotification(Handler handler) {

        handler.postDelayed(() -> {
            getNotification();
            showNewNotification(handler);
        }, 10000);
    }

public List<String> getTime (List<Train> routes, SimpleDateFormat sdf) {
        List<String> times = new ArrayList<>();

    for (Train train: routes
         ) {
        String time  = train.getGetStartTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String dateString = day + "-" + month + "-" + year + " "+time;
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

@Override
    public void count(int count) {

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
    public void updateList(int position) {

    }

    @Override
    public void prepareRoutes() {

    }

    @Override
    public void getAllRoutes(List<Route> routes) {

    }

    @Override
    public void getRoute(Route route) {
        notifList = route.getAlarmTimes();
        if (notifList.size()==0){

            int i =0;
            for (Train t: globalTrains
                    ) {
                notifList.add(i);
                i++;
            }


        }
        mainActAdapter = new MainActAdapter(this, globalTrains, notifList);
        searchList.setAdapter(mainActAdapter);
        searchList.setSelection(posInList);
        searchList.setVisibility(View.VISIBLE);
    }

    @Override
    public void deleteRoute(String nameFrom, String nameT) {

    }

    @Override
    public void runAlarm(Integer pos) {

    }

    @Override
    public void destroyAlarms() {

    }

    @Override
    public void updateAdapter() {

    }

    private boolean isNull(Object obj) {
        return obj != null;
    }

    void putToSharedPreferences(){
        sharedPreferences = getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("size", notificationTimesPos.size());
        editor.putStringSet("times", notificationTimesPos);
        editor.commit();

    }


    public List<String> getFullTimes(List<Train> trains) {

        List<String> trainDepartureTimes = getTime(trains, new SimpleDateFormat("dd-M-yyyy HH:mm"));
        return trainDepartureTimes;

    }





    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

}
