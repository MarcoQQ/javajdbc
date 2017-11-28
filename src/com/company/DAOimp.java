package com.company;

import com.sun.deploy.util.ReflectionUtil;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.util.List;

/**
 * Created by marco sun on 2017/11/23.
 * QueryRunner实现具体
 */
public class DAOimp<T> implements MyDAO{
    private QueryRunner queryRunner = null;
    private Class<T> type;

    public DAOimp(){
        queryRunner = new QueryRunner();
        //TODO
        // 得到type
    }
    @Override
    public void batch(Connection Conn, String sql, Object[]... args) {

    }

    @Override
    public List getForList(Connection Conn, String sql, Object... args) {
        return null;
    }

    @Override
    public Object get(Connection Conn, String sql, Object... args) throws Exception{
        return queryRunner.query(Conn, sql, new BeanHandler<Object>(type), args);
    }

    @Override
    public Object getForValue(Connection Conn, String sql, Object... args) {
        return null;
    }

    @Override
    public void update(Connection Conn, String sql, Object... args) {

    }
}
