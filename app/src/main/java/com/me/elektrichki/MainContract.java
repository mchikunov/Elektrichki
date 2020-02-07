package com.me.elektrichki;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.List;

import io.reactivex.Maybe;

public interface MainContract {
    interface View {
        void loadSearchData(List<CodeNameCoordination> trainList, int itemId, int routeId);
        void showTrainList(List<Train> trains, int routeId);
        void count(int count);
        List<String> getTime (List<Train> routes, SimpleDateFormat sdf);
        void showToast(String text);
        void updateList(int position);
        void prepareRoutes();
        void getAllRoutes(List<Route> routes);
        void getRoute(Route route);
        void deleteRoute(String nameFrom, String nameT);
        void runAlarm(Integer pos);
        void destroyAlarms();
        public void updateAdapter();
    }



    interface Presenter {
        void loadSearchList(String search, MainContract.View view, int itemId, int object);
        void getCount (final MainContract.View view);
        void deleteAll();
        public void getDataFromAPI (String nameFrom, String nameTo, String codeFrom, String codeTo, int routeId, MainContract.View view);
      //  void onDestroy();
       void getAllRoutes(MainContract.View view);
       void deleteAllinRoute(MainContract.View view);
        void getDistinctRoute( String nameFrom, String nameTo, MainContract.View view);
         void startAlarmBroadcastReceiver(Context context, List<String> times, int pos);
        void getDataFromDatabase(String nameFrom, String nameTo, int routeId, MainContract.View view);
        void startWorkManager (Context context, List<TransferTime> times);
         void deleteAllTrains();


    }


    @Dao
    interface CodeNameCoordinationDao {

        @Query("select * from coordination")
        Maybe<List<CodeNameCoordination>> getAllCoord();

        @Query("select * from coordination where nameE LIKE :nameFromView ")
        Maybe<List<CodeNameCoordination>> getEmployeeDistinct(String  nameFromView);


        @Query("DELETE FROM coordination")
        void deleteAll();


        @Insert
        void insertEmp(List<CodeNameCoordination> coord);

        @Query("SELECT COUNT(*) FROM coordination")
        Maybe<Integer> count();
    }



    @Dao
    interface TrainDao {

        @Query("select * from train")
        Maybe<List<Train>> getAllTrains();

        @Query("select * from train where fromDest=:nameFrom AND toDest=:nameTo")
        Maybe<List<Train>>  getTrainRoute(String  nameFrom, String nameTo);

        @Query("select * from train where fromDest=:nameFrom AND toDest=:nameTo AND getStartTime=:startTime")
        Maybe<List<Train>>  getTrainDistinct(String  nameFrom, String nameTo, String startTime);


        @Query("DELETE FROM train where fromDest=:nameFrom AND toDest=:nameTo")
        void deleteRoute(String  nameFrom, String nameTo);


        @Insert
        void insertEmp(Train train);

        @Query("SELECT COUNT(*) FROM train")
        int count();


        @Query("DELETE FROM train")
        int deleteAllTrains();
    }


    @Dao
    interface RouteDao {

        @Query("select * from route")
        Maybe<List<Route>> getAllRoutes();

        @Query("select * from route where fromDest=:nameFrom AND toDest=:nameTo")
        Maybe<Route> getRoute(String  nameFrom, String nameTo);


        @Query("DELETE FROM route where fromDest=:nameFrom AND toDest=:nameTo")
        int deleteRoute(String  nameFrom, String nameTo);

        @Query("DELETE FROM route")
        int deleteAllRoutes();

       /* @Query("UPDATE route SET  routeTimes=(:times), alarmTimes=(:notif) where fromDest=:nameFrom AND toDest=:nameTo")
        @TypeConverters({TypeConv.class})
        int updateRoute(String  nameFrom, String nameTo, List<String> times, List<Integer> notif);

        List is not supported by update statement in room for typeconverter, use arraylist!! bug
*/


        @Insert
        void insertRoute(Route route);

        @Query("SELECT COUNT(*) FROM route")
        int count();

    }

}
