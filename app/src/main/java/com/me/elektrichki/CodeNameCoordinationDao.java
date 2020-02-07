package com.me.elektrichki;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface CodeNameCoordinationDao {

    @Query("select * from coordination")
    Flowable<List<CodeNameCoordination>> getAllCoord();

    @Query("select * from coordination where nameE LIKE :nameFromView ")
    Flowable<List<CodeNameCoordination>> getEmployeeDistinct(String  nameFromView);


    @Query("DELETE FROM coordination")
    void deleteAll();


    @Insert
    void insertEmp(CodeNameCoordination coord);

    @Query("SELECT COUNT(*) FROM coordination")
    int count();
}
