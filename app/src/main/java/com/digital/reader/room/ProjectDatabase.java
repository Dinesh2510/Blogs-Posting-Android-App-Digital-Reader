package com.digital.reader.room;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {PostData.class}, version = 3, exportSchema = true)
public abstract class ProjectDatabase extends RoomDatabase {

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

            database.execSQL("DROP TABLE IF EXISTS 'request'");
            database.execSQL("CREATE TABLE 'request' ('id' INTEGER NOT NULL, 'requestId' TEXT, 'senderId' TEXT, 'senderName' TEXT, 'senderContact' TEXT, 'senderLat' TEXT, 'senderLong' TEXT, 'acceptorId' TEXT, 'acceptorName' TEXT, 'acceptorContact' TEXT, 'acceptorLat' TEXT, 'acceptorLong' TEXT, 'requestFor' INTEGER NOT NULL, 'requestDoneFlag' INTEGER NOT NULL, 'safeFlag' INTEGER NOT NULL, 'requestDate' INTEGER ,PRIMARY KEY(id))");

        }
    };
    private static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

            database.execSQL("DROP TABLE IF EXISTS 'request'");
            database.execSQL("CREATE TABLE 'request' ('id' INTEGER NOT NULL, 'requestId' TEXT, 'senderId' TEXT, 'senderName' TEXT, 'senderContact' TEXT, 'senderLat' TEXT, 'senderLong' TEXT, 'acceptorId' TEXT, 'acceptorName' TEXT, 'acceptorContact' TEXT, 'acceptorLat' TEXT, 'acceptorLong' TEXT, 'requestFor' INTEGER NOT NULL, 'requestDoneFlag' INTEGER NOT NULL, 'safeFlag' INTEGER NOT NULL, 'requestDate' INTEGER ,PRIMARY KEY(id))");

        }
    };
    private static final Migration MIGRATION_1_3 = new Migration(1, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

            database.execSQL("DROP TABLE IF EXISTS 'request'");
            database.execSQL("CREATE TABLE 'request' ('id' INTEGER NOT NULL, 'requestId' TEXT, 'senderId' TEXT, 'senderName' TEXT, 'senderContact' TEXT, 'senderLat' TEXT, 'senderLong' TEXT, 'acceptorId' TEXT, 'acceptorName' TEXT, 'acceptorContact' TEXT, 'acceptorLat' TEXT, 'acceptorLong' TEXT, 'requestFor' INTEGER NOT NULL, 'requestDoneFlag' INTEGER NOT NULL, 'safeFlag' INTEGER NOT NULL, 'requestDate' INTEGER ,PRIMARY KEY(id))");

        }
    };
    private static ProjectDatabase instance;
    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

        }

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
        }
    };

    public static synchronized ProjectDatabase getDatabaseInstance(Context context) {

        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), ProjectDatabase.class, "Post_db")
                    //.fallbackToDestructiveMigration()
                    .addMigrations(MIGRATION_1_2)
                    .addMigrations(MIGRATION_1_3)
                    .addMigrations(MIGRATION_2_3)
                    //.addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    public abstract PostDao postDao();

}
