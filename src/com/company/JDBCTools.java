package com.company;

import com.sun.xml.internal.ws.api.pipe.SyncStartForAsyncFeature;
import jdk.nashorn.internal.scripts.JD;
import org.apache.commons.dbcp.BasicDataSourceFactory;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;
import java.sql.ResultSetMetaData;
import java.sql.ResultSet;
import javax.sql.DataSource;
/**
 * Created by marco sun on 2017/11/13.
 */
public class JDBCTools {

    private static DataSource dataSource = null;

    static {
        try {
//            System.out.println("1");
            Properties properties = new Properties();
            InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("dbcp.properties");
            properties.load(inputStream);
            dataSource = BasicDataSourceFactory.createDataSource(properties);
//            System.out.println("1");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Connection getConnection2() throws Exception{
//        Properties properties = new Properties();
//        InputStream in = JDBCTools.class.getClassLoader().getResourceAsStream("jdbc.properties");
//        properties.load(in);
//        String driverClass = properties.getProperty("driverClass");
//        String jdbcUrl = properties.getProperty("jdbcUrl");
//        String user = properties.getProperty("user");
//        String password = properties.getProperty("password");
//
//        Class.forName(driverClass);
//
//        Connection Conn = DriverManager.getConnection(jdbcUrl, user, password);
//        System.out.print(Conn);
        Connection Conn = dataSource.getConnection();
        return Conn;
    }

    public static void release(ResultSet rs, Statement statement, Connection Conn) throws Exception {
        if (rs != null) {
            try {
                rs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (statement != null) {
            try {
                statement.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (Conn != null) {
            try {
                Conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void commit(Connection Conn){
        if(Conn != null){
            try{
                Conn.commit();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void rollback(Connection Conn){
        if(Conn != null){
            try{
                Conn.rollback();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void beginTx(Connection Conn){
        if(Conn != null){
            try {
                Conn.setAutoCommit(false);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }



}
