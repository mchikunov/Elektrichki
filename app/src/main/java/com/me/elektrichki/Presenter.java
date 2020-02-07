package com.me.elektrichki;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import androidx.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Build;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class Presenter implements MainContract.Presenter {

    final  Route[] route1 = new Route[1];
private TrainDatabase trainDatabase;
  private  Retrofit retrofit;
  private Context context;
  private   static List<PendingIntent> pendingIntents;
  private   static AlarmManager alarmManager;
  SharedPreferences sharedPreferences;


    Presenter(Context context) {
        this.context = context;

        if (trainDatabase==null) {
            trainDatabase = Room.databaseBuilder(context, TrainDatabase.class, "train-db")
                    .fallbackToDestructiveMigration()
                    .build();
        }


        retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();


        sharedPreferences = getDefaultSharedPreferences(context);
    }

public synchronized void getDataFromAPI (String nameFrom, String nameTo, String codeFrom, String codeTo, int routeId, MainContract.View view){




    Api api = retrofit.create(Api.class);
    ArrayList<Train> trains = new ArrayList<>();

    Map<String, String> data = new HashMap<>();
    data.put("apikey", "a9642167-d3d4-41b0-b38b-4bba4e24b6fe");
    data.put("from", codeFrom);
    data.put("to", codeTo);

    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(System.currentTimeMillis());
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH) + 1;
    int day = calendar.get(Calendar.DAY_OF_MONTH);
    String dateString = year + "-" + month + "-" + day;

    data.put("date", dateString);
    data.put("limit", "10000");

    Call<String > call = api.getNews(data);

    call.enqueue(new Callback<String>() {
        @Override
        public void onResponse(Call<String> call, Response<String> response) {
            String out  = response.body();

            String from = "from\":{\"code\":\""+codeFrom;
            String to = "to\":{\"code\":\""+codeTo;
            String uid = "\"uid\"";
            String title = "\"title\"";
            String duration = "\"duration\"";
            String arrival = "\"arrival\"";
            String stops = "\"stops\"";
            String expressType = "\"express_type\"";
            int end = 0;
            int next7 =1;

            deleteRoute(nameFrom, nameTo);

            while (next7>0){
              //  if (next7<0) continue;
                int first = out.indexOf(from, end);
                int forType  = out.indexOf(expressType, first);
                int forType1  = out.indexOf(title, forType-140);

                int forExpress1 = out.indexOf("\"", forType+16);
                int forStartTime  = out.indexOf(stops, first);
                if (forStartTime==-1) {
                    view.showToast("Маршрут не найден");
                return;
                }
                String trainType = out.substring(forType1+9, forType-3);
                String forExpress = out.substring(forType+16, forExpress1);

                if (trainType.equals("Пригородный поезд"))
                    trainType="";

                if (forExpress.equals("ull},")) forExpress = "";

                String getStartTime = out.substring(forStartTime-16, forStartTime-11);
                int beforeSecond = out.indexOf(uid, first);
                int second = out.indexOf(title, beforeSecond+4);
                int next3 = out.indexOf("\"", second+10);
                String getTitle =forExpress+" "+ trainType+ " " + out.substring(second+9, next3);
                int next4 = out.indexOf("\"", next3+12);
                String getNumber = out.substring(next3+12,next4);

                int nextStops = out.indexOf(stops, next4);
                int nextStopsNext = out.indexOf("\"", nextStops+10);
                String getStops = out.substring(nextStops+9,nextStopsNext);

                int next5 = out.indexOf(duration, next4);
                int next6 = out.indexOf(",", next5+12);
                String getDuration = out.substring(next5+11,next6);
                next7 = out.indexOf(arrival, next6);
                String getEndTime = out.substring(first-17, first-12);
                end  = next7+30;

                Train train = new Train(nameFrom, nameTo, codeFrom, codeTo, getStartTime, getTitle, getNumber, getStops, getDuration, getEndTime);

                insertTrain(train);

                trains.add(train);
            }


            view.showTrainList(trains, routeId);
            getDistinctRoute(nameFrom, nameTo, view);


          /*  Observable.timer(1000, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
            .map(o -> System.currentTimeMillis() )
                    .subscribe(v-> System.out.print(v));*/


        }

        @Override
        public void onFailure(Call<String> call, Throwable t) {

            getDataFromDatabase(nameFrom, nameTo, routeId, view);
        }


    });



}


 public void getAllRoutes(MainContract.View view){

    trainDatabase.getRouteDao().getAllRoutes()

            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(res -> view.getAllRoutes(res));




}


    public void getDistinctRoute( String nameFrom, String nameTo, MainContract.View view){

        trainDatabase.getRouteDao().getRoute(nameFrom, nameTo)

                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableMaybeObserver<Route>() {


                    @Override
                    public void onSuccess(Route route) {
                        view.getRoute(route);
                    }

                    @Override
                    public void onError(Throwable e) {
                        // ...
                    }

                    @Override
                    public void onComplete() {

                    }



                });
    }





    public void insertRoute (Route route) {

        String getFrom = route.getFromDest();
        String getTo = route.getToDest();
        List<String> routeTimes =route.getRouteTimes();
        List<Integer> alarmTimes = route.getAlarmTimes();
        Route route2 = new Route(getFrom, getTo, routeTimes, alarmTimes);



                trainDatabase.getRouteDao().getRoute(getFrom, getTo)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new DisposableMaybeObserver<Route>() {


                            @Override
                            public void onSuccess(Route route) {


                                Completable.fromAction(new Action() {
                                    @Override
                                    public void run() {
                                      //  trainDatabase.getRouteDao().updateRoute(route2);
                                        trainDatabase.getRouteDao().deleteRoute(getFrom, getTo);
                                        trainDatabase.getRouteDao().insertRoute(route2);
                                    }
                                }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.single()).subscribe();

                            }

                            @Override
                            public void onError(Throwable e) {
                                // ...
                            }

                            @Override
                            public void onComplete() {

                                Completable.fromAction(new Action() {
                                    @Override
                                    public void run() {
                                        trainDatabase.getRouteDao().insertRoute(route);
                                        int ee = trainDatabase.getRouteDao().count();
                                    }
                                }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.single()).subscribe();



                            }
                        });

    }





    public void deleteAllinRoute(MainContract.View view){

        Completable.fromAction(new Action() {
            @Override
            public void run() {


               int res = trainDatabase.getRouteDao().deleteAllRoutes();



            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.single()).subscribe(()->getAllRoutes(view));
    }



    public void deleteRoute(MainContract.View view, String nameFrom, String nameTo){

        Completable.fromAction(new Action() {
            @Override
            public void run() {


                int res = trainDatabase.getRouteDao().deleteRoute(nameFrom, nameTo);


            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.single()).subscribe();
    }





public void getDataFromDatabase(String nameFrom, String nameTo, int routeId, MainContract.View view){

        view.showToast("Нету атырнета! Загрузка из локальной базы");


    trainDatabase.getTrainDao().getTrainRoute(nameFrom, nameTo)

            .subscribeOn(Schedulers.single())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(res -> view.showTrainList(res, routeId));

}


private void deleteRoute(String nameFrom, String nameTo){

    Completable.fromAction(new Action() {
        @Override
        public void run() {


            trainDatabase.getTrainDao().deleteRoute(nameFrom, nameTo);


        }
    }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.single()).subscribe();
}





public void insertIfNotExist (String nameFrom, String nameTo, String getStartTime, Train train){

    Completable.fromAction(new Action() {
        @Override
        public void run() {

            trainDatabase.getTrainDao().getTrainDistinct(nameFrom, nameTo, getStartTime)

                    .subscribe(res->{List<Train>  rrr =res;
                        if (rrr.size()==0)
                            trainDatabase.getTrainDao().insertEmp(train);
                        // trainDatabase.getTrainDao().count();

                    });

        }
    }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.single()).subscribe();
}

public void insertTrain(Train train){

    Completable.fromAction(new Action() {
        @Override
        public void run() {

            trainDatabase.getTrainDao().insertEmp(train);

        }
    }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.single()).subscribe();

}


    public void deleteAllTrains(){

        Completable.fromAction(new Action() {
            @Override
            public void run() {


                int res = trainDatabase.getTrainDao().deleteAllTrains();



            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.single()).subscribe();
    }


    @SuppressLint("CheckResult")
    @Override
    public void getCount (final MainContract.View view) {


               trainDatabase.getCoordDao().count()

                       .subscribeOn(Schedulers.io())
                       .observeOn(AndroidSchedulers.mainThread())
                       .subscribe(res->view.count(res));


    }


    @SuppressLint("CheckResult")
    @Override
    public synchronized void loadSearchList(String search, final MainContract.View view, int itemId, int routeId) {


            trainDatabase.getCoordDao().getEmployeeDistinct(search)

                    .subscribeOn(Schedulers.single())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(res -> view.loadSearchData(res, itemId, routeId));


    }


    @SuppressLint("CheckResult")
    @Override
    public void deleteAll () {

        Completable.fromAction(new Action() {
            @Override
            public void run() {


                trainDatabase.getCoordDao().deleteAll();
                firstCodesAdded();


            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.single()).subscribe();
    }


     synchronized void firstCodesAdded() {
        List<CodeNameCoordination> codes = new ArrayList<>();
        AssetManager am = context.getAssets();


        try {
            // открываем поток для чтения

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    am.open("allStationsJsonOut.txt"), "UTF-8"));
            String str = "";
            // читаем содержимое
            while ((str = br.readLine()) != null) {
                String[] str1 = str.split("\\} ");
                CodeNameCoordination codeNameCoordination = new CodeNameCoordination(str1[0], str1[1]);
                codes.add(codeNameCoordination);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        addCodes(codes);

    }


    private synchronized void addCodes (final List<CodeNameCoordination> codes){

        Completable.fromAction(new Action() {
            @Override
            public void run() {


                    trainDatabase.getCoordDao().insertEmp(codes);




            }
        }).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
               /* .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        getCount(view);
                    }
                })*/
                .subscribe();
    }






    public void startAlarmBroadcastReceiver(Context context, List<String> times, int pos) {


        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (pendingIntents!=null){
            for (PendingIntent p : pendingIntents
                    ) {
                alarmManager.cancel(p);
            }
        }

        pendingIntents = new ArrayList<>();
        int delayMin = sharedPreferences.getInt("get1", 25);
        int hour3;
        int minute3;
        int hour2;
        int minute2;

        for (int i = 0; i <times.size() ; i++) {

            Intent _intent = new Intent(context, ShowNotification.class);
            Long time = Long.valueOf(times.get(i));

            int nextTime = i+1;
            if (nextTime<times.size()) {
                Long timeTrainNext = Long.valueOf(times.get(nextTime))+(delayMin*60*1000L);
               Calendar train = Calendar.getInstance();
                train.setTimeInMillis(timeTrainNext);
                hour2 = train.get(Calendar.HOUR_OF_DAY);
                minute2 = train.get(Calendar.MINUTE);
                _intent.putExtra("nextTime", timeTrainNext);
            }

            _intent.putExtra("get1", delayMin);
            Long timeTrainForMessage = time+(delayMin*60*1000L);
            Calendar train = Calendar.getInstance();
            train.setTimeInMillis(timeTrainForMessage);
            hour3 = train.get(Calendar.HOUR_OF_DAY);
            minute3 = train.get(Calendar.MINUTE);
            _intent.putExtra("trainTime", timeTrainForMessage);


            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, i, _intent, PendingIntent.FLAG_ONE_SHOT);
            pendingIntents.add(pendingIntent);

            // Remove any previous pending intent.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent);
            } else alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);

        }
    }


    public void startWorkManager (Context context, List<TransferTime> times) {

        int delayRes, j;
        String resNext="";
        WorkManager.getInstance(context).cancelAllWorkByTag("alarmTag");

        List<String> dat1 = new ArrayList<>();


                Long now = System.currentTimeMillis();
                for (int i = 0; i < times.size(); i++) {


                    Long time = Long.valueOf(times.get(i).getTime());


                    Calendar train = Calendar.getInstance();
                    train.setTimeInMillis(time);
                    int hour = train.get(Calendar.HOUR_OF_DAY);
                    int minute = train.get(Calendar.MINUTE);
                    int day = train.get(Calendar.DAY_OF_MONTH);

                    String dat = String.valueOf(hour)+ " "+ String.valueOf(minute)+ " "+String.valueOf(day);
                    dat1.add(dat);

                    if (times.get(i).getOrder()==1)
                        delayRes = sharedPreferences.getInt("get1", 0);
                    else
                        delayRes = sharedPreferences.getInt("get2", 0);

                    Data.Builder data = new Data.Builder();

                    Long timeTrainForMessage = time + (delayRes * 60 * 1000L);
                    data.putLong("timeTrain", timeTrainForMessage);

                    int currOrder = times.get(i).getOrder();
                    j=i;
                            do {//look for next item of same group (5 min or 25)
                                if (j==times.size()-1) break;
                                j++;
                                resNext = times.get(j).getTime();
                            } while (times.get(j).getOrder() != currOrder);



                    Long timeTrainNext = Long.valueOf(resNext) + (delayRes * 60 * 1000L);
                        data.putLong("nextTrain", timeTrainNext);



                    Long diff1 = time - now;


                    data.putInt("get1", delayRes);
                    data.putInt("delayTime", times.get(i).getOrder()); // 1  - first channel, 2  - second

                    OneTimeWorkRequest myWorkRequest = new OneTimeWorkRequest.Builder(MyWorker.class)
                            .setInitialDelay(diff1, TimeUnit.MILLISECONDS)
                            .addTag("alarmTag")
                            .setInputData(data.build())
                            .build();

                    WorkManager.getInstance(context).enqueue(myWorkRequest);


                }

                dat1=null;


    }


    void onDestroy(){


        WorkManager.getInstance(context).cancelAllWorkByTag("alarmTag");

        if (pendingIntents!=null){
            for (PendingIntent p : pendingIntents
                    ) {
                alarmManager.cancel(p);
            }
        }


    }


}
