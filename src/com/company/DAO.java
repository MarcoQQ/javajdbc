//Dao设计模式
//Data Access Object
//数据（库）访问对象
//访问数据信息的类，包含了对数据的(Create,Read,Update,Delete),而不包含任何业务相关的信息
package com.company;

import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.sql.Date;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.text.SimpleDateFormat;

import java.util.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

/**
 * Created by marco sun on 2017/11/16.
 */
public class DAO {
    //Insert,Update,Delete
    //PreparedStatement可以防注入，因为避免了sql语句的拼接
    //String username = "a' or password = "
    //String password = "or '1'='1' "
    //select * from table where username = 'a' or password = ' and password = ' or '1'='1';
    public void update( String sql, Object ... args) throws Exception{
        Connection Conn = null;
        PreparedStatement preparedStatement = null;
        try{
            Conn = JDBCTools.getConnection2();
            preparedStatement = Conn.prepareStatement(sql);
            for(int i = 0; i < args.length; i++){
                preparedStatement.setObject(i+1, args[i]);
            }
            preparedStatement.executeUpdate();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            JDBCTools.release(null, preparedStatement, Conn);
        }
    }

    public <T> T get(Class<T> cl, String sql, Object... args) throws Exception {
        List<T> list = getforList(cl, sql, args);
        if(!list.isEmpty())
            return list.get(0);
        return null;
    }

    public <T> List<T> getforList(Class<T> cl, String sql, Object ... args) throws Exception{
        List<T> ret = new ArrayList();
        Connection Conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try {
            Conn = JDBCTools.getConnection2();
            preparedStatement = Conn.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                preparedStatement.setObject(i+1, args[i]);
            }
            rs = preparedStatement.executeQuery();
            List<Map<String, Object>> list = handleResultSetToMapList(rs);
            ret = transferMapListToBeanList(cl, list);
            }catch (Exception e) {
            e.printStackTrace();
            } finally {
            JDBCTools.release(rs, preparedStatement, Conn);
            }
        return ret;
    }

    //把map列表变为对象列表，每个map的key变为对象的属性名，每个map的value变为对象的属性值
    private <T> List<T> transferMapListToBeanList(Class<T> cl,  List<Map<String, Object>> list) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        List<T> ret = new ArrayList<>();
        T o;
        if(!list.isEmpty()){
            for(Map<String, Object> m : list){
                o = cl.newInstance();
                for(Map.Entry<String, Object> entry : m.entrySet()){
                    BeanUtils.setProperty(o, entry.getKey(), entry.getValue());
                }
                ret.add(o);
            }
        }
        return ret;
    }

    //处理结果集，变成一个map列表，每个map对应一条记录，map的key代表列的别名，value代表一个值
    private List<Map<String, Object>> handleResultSetToMapList(ResultSet rs) throws Exception {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = null;
        List<String> columnLabels = getColumnLabel(rs);
        while(rs.next()) {
            //利用反射创建对象
            map = new HashMap<>();
            for (String columnLabel : columnLabels) {
                Object columnsValue = rs.getObject(columnLabel);
                map.put(columnLabel, columnsValue);
            }
            list.add(map);
        }
        return list;
    }

    //获得列的别名集合
    public List<String> getColumnLabel(ResultSet rs) throws Exception{
        List<String> labels = new ArrayList<>();
        ResultSetMetaData rsmd = rs.getMetaData();
        for(int i = 0; i < rsmd.getColumnCount(); i++){
            labels.add(rsmd.getColumnLabel(i+1));
        }
        return labels;
    }

    //返回查询的值 或 一个统计的值
    public <E> E getforValue(String sql, Object ... args) throws Exception{
        Connection Conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try{
            Conn = JDBCTools.getConnection2();
            preparedStatement = Conn.prepareStatement(sql);
            for(int i = 0; i < args.length; i++){
                preparedStatement.setObject(i+1, args[i]);
            }
            rs = preparedStatement.executeQuery();
            if(rs.next())
                return (E)rs.getObject(1);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCTools.release(rs, preparedStatement, Conn);
        }
        return null;
    }
}
