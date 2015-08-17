package com.haipeng.libraryforandroid.DB.OutAppDB;

import android.database.SQLException;

public class DBException extends Exception {
    public DBException(SQLException e){
        e.printStackTrace();    	
    }
}
