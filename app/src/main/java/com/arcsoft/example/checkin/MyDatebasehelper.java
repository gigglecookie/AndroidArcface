package com.arcsoft.example.checkin;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class MyDatebasehelper {
        public static void main( String args[] )
        {
            Connection c = null;
            Statement stmt = null;
            try {
                Class.forName("org.sqlite.JDBC");
                c = DriverManager.getConnection("jdbc:sqlite:D:\\AndroidTools\\userdb.db");
                c.setAutoCommit(false);
                System.out.println("Opened database successfully");

                stmt = c.createStatement();
                ResultSet rs = stmt.executeQuery( "SELECT * FROM USERTABLE;" );
                while ( rs.next() ) {
                    String id = rs.getString("id");
                    String name = rs.getString("usersex");
                    String sex = rs.getString("sex");
                    String time = rs.getString("time");
                    System.out.println( "ID = " + id );
                    System.out.println( "NAME = " + name );
                    System.out.println( "sex = " + sex );
                    System.out.println( "TIME = " + time );
                    System.out.println();
                }
                rs.close();
                stmt.close();
                c.close();
            } catch ( Exception e ) {
                System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                System.exit(0);
            }
            System.out.println("Operation done successfully");
        }
}


