package com.company;

import java.sql.Connection;
import java.util.List;
/**
 * Created by marco sun on 2017/11/23.
 */
//访问数据的DAO接口，里面定义好访问数据表的各种方法

public interface MyDAO <T>{
    //返回一个T
    T get(Connection Conn, String sql, Object ... args) throws Exception;
    //返回T的一个集合
    List<T> getForList(Connection Conn, String sql, Object ... args);
    //返回具体的一个值
    <E> E getForValue(Connection Conn, String sql, Object ... args);
    //args:填充sql占位符可变参数
    void update(Connection Conn, String sql, Object ... args);
    void batch(Connection Conn, String sql, Object[] ... args);
}
