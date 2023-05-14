package com.example.apm.aop.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {NetworkCallEntity.class}, version = NetworkCallDBManager.DB_VERSION, exportSchema = true)
public abstract class NetworkCallDatabase extends RoomDatabase {

    public abstract NetworkCallDao getNetworkCallDao();
}
