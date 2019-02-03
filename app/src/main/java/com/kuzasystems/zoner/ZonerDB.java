package com.kuzasystems.zoner;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by victor on 29-Sep-18.
 */

public class ZonerDB extends SQLiteOpenHelper {
    public ZonerDB(Context context) {
        super(context, new Config().getDbName(), null, new Config().getDbVersion());
    }
    @Override
    public void onCreate(SQLiteDatabase sqlDB) {
        //create users table,
            String usersTablesSql = "CREATE TABLE `users` (\n" +
                    "  `id` int(11) NOT NULL,\n" +
                    "  `Name` text NOT NULL,\n" +
                    "  `PhoneNumber` varchar(15) NOT NULL,\n" +
                    "  `Email` varchar(100) NOT NULL,\n" +
                    "  `Website` text NOT NULL,\n" +
                    "  `Location` text NOT NULL,\n" +
                    "  `Latitude` double NOT NULL,\n" +
                    "  `Longitude` double NOT NULL,\n" +
                    "  `Logo` text  NULL,\n" +
                    "  `Username` text NOT NULL,\n" +
                    "  `Password` text NOT NULL,\n" +
                    "  `Usertype` int(11) NOT NULL,\n" +
                    "  `RegistrationDate` date NOT NULL,\n" +
                    "  `Status` int(11) NOT NULL\n" +
                    ")";
        sqlDB.execSQL(usersTablesSql);
        String businesses = "CREATE TABLE `businesses` (\n" +
                    "  `id` int(11) NOT NULL,\n" +
                    "  `Name` text NOT NULL,\n" +
                    "  `PhoneNumber` varchar(15) NOT NULL,\n" +
                    "  `Email` varchar(100) NOT NULL,\n" +
                    "  `Website` text NOT NULL,\n" +
                    "  `Location` text NOT NULL,\n" +
                    "  `Latitude` double NOT NULL,\n" +
                    "  `Longitude` double NOT NULL,\n" +
                    "  `Logo` text  NULL,\n" +
                    "  `Username` text NOT NULL,\n" +
                    "  `Password` text NOT NULL,\n" +
                    "  `Usertype` int(11) NOT NULL,\n" +
                    "  `RegistrationDate` date NOT NULL,\n" +
                    "  `Status` int(11) NOT NULL,\n" +
                "  `BusinessStatus` varchar(50) NULL\n" +
                    ")";
        sqlDB.execSQL(businesses);


        //create favourites table
        String favouritesTableSql = "CREATE TABLE `Favourites` (\n" +
                "  `Id` int(11) NOT NULL,\n" +
                "  `UserId` int(11) NOT NULL,\n" +
                "  `BusinessId` int(11) NOT NULL\n" +
                ") ";
        sqlDB.execSQL(favouritesTableSql);
        //create dms table
        String dmsTableSql="CREATE TABLE `Messages` (\n" +
                "  `Id` int(11) NOT NULL,\n" +
                "  `Sender` int(11) NOT NULL,\n" +
                "  `Recipient` int(11) NOT NULL,\n" +
                "  `RecipientName` text NOT NULL,\n" +
                "  `Message` text NOT NULL,\n" +
                "  `Sent On` datetime NOT NULL,\n" +
                "  `Status` int(11) NOT NULL\n" +
                ") ";

        sqlDB.execSQL(dmsTableSql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqlDB, int i, int i2) {
          sqlDB.execSQL("DROP TABLE IF EXISTS businesses");
          sqlDB.execSQL("DROP TABLE IF EXISTS users");
          sqlDB.execSQL("DROP TABLE IF EXISTS Favourites");
          sqlDB.execSQL("DROP TABLE IF EXISTS Messages");
        onCreate(sqlDB);
    }

}
