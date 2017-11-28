package com.company;

import java.io.*;
import java.sql.*;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.*;
import com.mysql.jdbc.*;
//数据源，通常我们管它叫做连接池
import javax.sql.DataSource;
import com.mysql.jdbc.Driver;
import jdk.nashorn.internal.scripts.JD;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;


public class Main {

    public static int a = 0;
    static{
        a = 1;
    }

    public static void main(String[] args) throws Exception {
        // write your code here
//        Driver driver = new Driver();
//
//        String url = "jdbc:mysql://47.94.172.62:3306/webdb";
//        Properties info = new Properties();
//
//        info.put("user", "mdd");
//        info.put("password", "m123456");
//
//        Connection Conn = driver.connect(url, info);
//        Main m = new Main();
//        System.out.println(Main.class);
//        m.testJavaBeanGetProperty();
//        m.testJavaBeanSetProperty();

//        System.out.print(new java.util.Date().getTime());
//        testDBCPWithDataSourceFactory();
//        Connection Conn = JDBCTools.getConnection2();
//        System.out.println(Conn);
        testDbUtilsQueryRunnerQuery();
//        DAO d =new DAO();
//        List<News> news = d.getforList(News.class, "select newsid id, newstitle title, newscontent content, newstime time, adminname adminName from news where newsid = ? or newsid = ?", 2,3);
//        for(int i = 0; i < news.size(); i++)
//          System.out.println(news.get(i));
//        long s = d.getforValue("select count(*) from news where newsid > ?", 1);
//        System.out.print(s);
//        Map<String, Object>map = new HashMap<>();
//        map.put("hah", 1);
//        for(Map.Entry<String, Object> entry:map.entrySet()){
//            System.out.print(entry.getKey());
//            System.out.print(entry.getValue());
//        }
    }


    public Connection getConnection() throws Exception {
        String driverClass = null;
        String jdbcUrl = null;
        String user = null;
        String password = null;
        Properties properties = new Properties();
        Properties info = new Properties();

        InputStream in = getClass().getClassLoader().getResourceAsStream("jdbc.properties");
        properties.load(in);
        driverClass = properties.getProperty("driverClass");
        jdbcUrl = properties.getProperty("jdbcUrl");
        user = properties.getProperty("user");
        password = properties.getProperty("password");

        info.put("user", user);
        info.put("password", password);

        Driver driver = (Driver) Class.forName(driverClass).newInstance();

        Connection Conn = driver.connect(jdbcUrl, info);

        return Conn;
    }

    //DriverManager是驱动的管理类,好处是它可以注册多个数据库
    public void testDriverManager() throws Exception {
        //驱动的全类名
        String driverClass = null;
        String jdbcUrl = null;
        String user = null;
        String password = null;
        Properties properties = new Properties();
        Properties info = new Properties();

        InputStream in = getClass().getClassLoader().getResourceAsStream("jdbc.properties");
        properties.load(in);
        driverClass = properties.getProperty("driverClass");
        jdbcUrl = properties.getProperty("jdbcUrl");
        user = properties.getProperty("user");
        password = properties.getProperty("password");

        //加载数据库驱动程序(注册驱动的实现类中有注册驱动的静态代码块)
        //DriverManager.registerDriver((Driver) Class.forName(driverClass).newInstance());
        Class.forName(driverClass);

        Connection Conn = DriverManager.getConnection(jdbcUrl, user, password);
        System.out.print(Conn);
    }

    public void testStatement() throws Exception {
        Connection Conn = null;
        Statement statement = null;
        try {
            //1.数据库连接
            Conn = JDBCTools.getConnection2();
            //2.准备更新语句
            String sql = "insert into news(NewsTitle) values('haha')";
            //3.获取Statement对象，执行语句
            statement = Conn.createStatement();
            statement.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCTools.release(null, statement, Conn);
        }
    }

    //ResultSet返回指向第一行前的指针
    //next()相当于Iterator对象的hasNext()和Next()方法
    public void testResultSet() throws Exception {
        Connection Conn = null;
        Statement statement = null;
        ResultSet rs = null;
        try {
            Conn = JDBCTools.getConnection2();
            String sql = "select* from news";
            statement = Conn.createStatement();
            rs = statement.executeQuery(sql);
            while (rs.next()) {
                String newsid = rs.getString(1);
                String newstitle = rs.getString("NewsTitle");
                System.out.println(newsid);
                System.out.println(newstitle);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCTools.release(rs, statement, Conn);
        }
    }

    public void testPreparedStatement() throws Exception {
        String sql = "insert into news(NewsTitle) values(?)";
        DAO d = new DAO();
        d.update(sql, "haha");

    }

    //int getColumnCount()  String getColumnLabel(int column)
    public void testResultSetMetaData() throws Exception{
        Connection Conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try {
            String sql = "select newsid ni, newstitle nt from news";
            Conn = JDBCTools.getConnection2();
            preparedStatement = Conn.prepareStatement(sql);

            rs = preparedStatement.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            for(int i = 0; i < rsmd.getColumnCount(); i++) {
                String columnLabel = rsmd.getColumnLabel(i+1);
                System.out.print(columnLabel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCTools.release(rs, preparedStatement, Conn);
        }
    }

    public void testJavaBeanSetProperty() throws Exception{
        Object news = new News();
        //BeansUtil会自动转换value的类型，并且ResultSet的getProperty返回的数据库的Date类型是String类型
        BeanUtils.setProperty(news, "time", "1997-01-12");
        System.out.println(news);
    }

    public void testJavaBeanGetProperty() throws Exception{
        Object news = new News();
        System.out.println(BeanUtils.getProperty(news, "id")) ;
    }

    //得到主键
    public void testGetKeyValue() throws Exception{
        Connection Conn = null;
        PreparedStatement preparedStatement = null;
        try{
            String sql = "insert into news(newstitle, newscontent, newstime, adminname) values(?,?,?,?), (?,?,?,?) ";
            //使用重载的prepareStatement(sql, flag)函数来生成PreparedStatement对象
            Conn = JDBCTools.getConnection2();
            preparedStatement = Conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, "lala");
            preparedStatement.setString(2, "lala");
            preparedStatement.setDate(3, new Date(new java.util.Date().getTime()));
            preparedStatement.setString(4, "lala");
            preparedStatement.setString(5, "lala");
            preparedStatement.setString(6, "lala");
            preparedStatement.setDate(7, new Date(new java.util.Date().getTime()));
            preparedStatement.setString(8, "lala");
            preparedStatement.executeUpdate();

            //ResultSet中只有一列
            ResultSet rs = preparedStatement.getGeneratedKeys();
            while(rs.next())
            System.out.print(rs.getObject(1));

        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            JDBCTools.release(null, preparedStatement, Conn);
        }
    }

//    //插入blob类型的数据必须使用PreparedStatement，因为blob无法使用字符串拼写
//    public void testInsertBlob() throws Exception{
//        Connection Conn = null;
//        PreparedStatement preparedStatement = null;
//        try{
//            Conn = JDBCTools.getConnection2();
//            String sql = "insert into Pictures(picture) values(?) ";
//            preparedStatement = Conn.prepareStatement(sql);
//            InputStream inputSt`\ream = getClass().getClassLoader().getResourceAsStream("1.jpg");
//
//            byte[] bt = InputStreamToByte(inputStream);
//
////            preparedStatement.setBlob(1,);
////            preparedStatement.execute();
//
//        } catch(Exception e) {
//            e.printStackTrace();
//        } finally {
//            JDBCTools.release(null, preparedStatement, Conn);
//        }
//    }

    public byte[] InputStreamToByte(InputStream in) throws IOException {
        ByteArrayOutputStream bain = new ByteArrayOutputStream();
        int ch;
        while((ch = in.read()) != -1) {
            bain.write(ch);
        }
        byte[] ret = bain.toByteArray();
        bain.close();
        in.close();
        return ret;
    }

    //使用dbcp数据库连接池,dbcp依赖于pool
    public static void testDBCP() throws Exception{
        BasicDataSource dataSource = null;
        //创建dbcp源实例
        dataSource = new BasicDataSource();
        //制定属性
        dataSource.setUsername("mdd");
        dataSource.setPassword("m123456");
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://47.94.172.62:3306/webdb");
        dataSource.setInitialSize(10);
        dataSource.setMaxActive(20);
        Connection Conn = dataSource.getConnection();
        System.out.println(Conn);
    }

    //加载dbcp.properties,里面的键是BasicDataSource中的属性
    //调用BasicDataSourceFactory的createDataSource方法
    //从DataSource中获取数据库连接
    public static void testDBCPWithDataSourceFactory() throws Exception{
        Properties properties = new Properties();
        InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("dbcp.properties");
        properties.load(inputStream);
        DataSource dataSource = BasicDataSourceFactory.createDataSource(properties);
        Connection Conn = dataSource.getConnection();
        System.out.println(Conn);
        BasicDataSource basicDataSource = (BasicDataSource)dataSource;
        System.out.println(basicDataSource.getInitialSize());

    }

    public static void testDbUtilsQueryRunnerUpdate() throws Exception{
        QueryRunner queryRunner = new QueryRunner();
        String sql = "delete from news where newsid in (?, ?)";
        Connection Conn = null;
        try{
            Conn = JDBCTools.getConnection2();
            queryRunner.update(Conn, sql, 2, 3);
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            JDBCTools.release(null, null, Conn);
        }

    }

    //ScalarHandler,自定义Handler,MapHandler,MapListHandler,MapHandler
    public static void  testDbUtilsQueryRunnerQuery() throws Exception{
        Connection Conn = null;
        QueryRunner queryRunner = new QueryRunner();
//        class MyBeanHandler implements ResultSetHandler{
//           public Object handle(ResultSet rs) throws SQLException{
//               while(rs.next()){
//                   Integer id = rs.getInt(1);
//                   String title = rs.getString(2);
//                   String content = rs.getString(3);
//                   Date time = rs.getDate(4);
//                   String adminName = rs.getString(5);
//
//               }
//           }
//        }
        String sql = "select newsid id, newstitle title, newscontent content, newstime time, adminname adminName from news where newsid = 1";
        try {
            Conn = JDBCTools.getConnection2();
            //返回值为handle方法的返回值
            News obj = queryRunner.query(Conn, sql, new BeanHandler<News>(News.class));
            System.out.println(obj);
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            JDBCTools.release(null, null, Conn);
        }

    }


}



