package com.mark.qpsaralmclock.gpsaralmclock;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by tushkevich_m on 15.08.2016.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    // названия столбцов
    public static final String ID = "id";
    public static final String NAME_COLUMN = "name";
    public static final String LATITUDE_COLUMN = "lat";
    public static final String LONGITUDE_COLUMN = "long";
    public static final String RUN = "run";
    // имя базы данных
    private static final String DATABASE_NAME = "databaselocations.db";
    // версия базы данных
    private static final int DATABASE_VERSION = 1;
    // имя таблицы
    public static final String DATABASE_TABLE = "locations";
    private static final String DATABASE_CREATE_SCRIPT = "create table "
            + DATABASE_TABLE + " (" + ID
            + " integer primary key autoincrement, " + NAME_COLUMN
            + " text not null, " + RUN + " integer, "+ LATITUDE_COLUMN
            + " float, " + LONGITUDE_COLUMN + " float);";

    //+ BaseColumns._ID
  //  + " integer primary key autoincrement, "

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(DATABASE_CREATE_SCRIPT);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
