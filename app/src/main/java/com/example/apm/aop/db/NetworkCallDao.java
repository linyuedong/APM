package com.example.apm.aop.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * HttpCall Dao 层
 *
 * Created by Nelson on 2019-11-29.
 */
@Dao
public interface NetworkCallDao {

    @Query("SELECT SUM(total_bytes) FROM networkCall WHERE user_id = :userId AND (timestamp BETWEEN :startTimestamp AND :endTimestamp)")
    long findBytesByTimeInterval(String userId, long startTimestamp, long endTimestamp);

    @Query("SELECT * FROM networkCall WHERE user_id IN (:userIds)")
    List<NetworkCallEntity> getAllByUserIds(String[] userIds);

    @Query("SELECT * FROM networkCall WHERE user_id = :userId AND timestamp < :timestamp")
    List<NetworkCallEntity> findLessThan(String userId, long timestamp);

    @Query("SELECT * FROM networkCall WHERE user_id = :userId AND (timestamp BETWEEN :startTimestamp AND :endTimestamp)")
    List<NetworkCallEntity> findByTimeInterval(String userId, long startTimestamp, long endTimestamp);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertEntity(NetworkCallEntity entity);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateEntiy(NetworkCallEntity entity);

    //@Delete
    @Query("DELETE FROM networkCall WHERE user_id = :userId AND (timestamp BETWEEN :startTimestamp AND :endTimestamp)")
    void deleteByTimeInterval(String userId, long startTimestamp, long endTimestamp);

    //@Delete
    @Query("DELETE FROM networkCall WHERE user_id = :userId AND timestamp < :timestamp")
    void deleteLessThan(String userId, long timestamp);

    //@Delete
    @Query("DELETE FROM networkCall")
    void deleteAll();
}
