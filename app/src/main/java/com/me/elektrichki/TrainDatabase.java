package com.me.elektrichki;


import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {CodeNameCoordination.class, Train.class, Route.class}, version = 6)
@TypeConverters({TypeConv.class})
public abstract class TrainDatabase extends RoomDatabase {


    public abstract MainContract.TrainDao getTrainDao();
    public abstract MainContract.CodeNameCoordinationDao getCoordDao();
    public abstract MainContract.RouteDao getRouteDao();
}
