package com.company;

import jdk.nashorn.internal.scripts.JD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by marco sun on 2017/11/21.
 * 如果多个操作，每个操作是用自己单独的连接，则无法保证事务，所以一个事务中的多个操作使用一个连接
 */
public class TranscationTest {
    //1给2汇款500
    public static void main(String args[]) throws Exception{
//        Connection Conn = null;
//        try{
//            Conn = JDBCTools.getConnection2();
//            //开始事务，取消默认提交
//            JDBCTools.beginTx(Conn);
//            String sql = "update user set balance =" + "balance - 100 where id = 1";
//            String sql1 = "update user set balance =" + "balance + 500 where id = 2";
//            update(Conn, sql);
//            update(Conn, sql1);
//            //提交事务
//            JDBCTools.commit(Conn);
//        } catch (Exception e) {
//            e.printStackTrace();
//            JDBCTools.rollback(Conn);
//        } finally {
//            JDBCTools.release(null, null, Conn);
//        }
//        testTransactionIsolationUpdate();
        batch();

    }

    public static void testTransactionIsolationUpdate() throws Exception{
        Connection Conn = null;
        try{
            Conn = JDBCTools.getConnection2();
            Conn.setAutoCommit(false);
            String sql = "update user set balance =" + "balance - 100 where id = 1";
            update(Conn, sql);
            testTransactionIsolationRead();
            Conn.commit();
        } catch(Exception e) {
            e.printStackTrace();
        } finally{
            JDBCTools.release(null, null, Conn);
        }
    }

    public static void testTransactionIsolationRead() throws Exception{
        String sql = "select balance from user where id = 1";
        int balance = getforValue(sql);
        System.out.println(balance);
    }

    public static void update(Connection Conn, String sql, Object ... args) throws Exception{
        PreparedStatement preparedStatement = null;
        try{
            preparedStatement = Conn.prepareStatement(sql);
            for(int i = 0; i < args.length; i++){
                preparedStatement.setObject(i+1, args[i]);
            }
            preparedStatement.executeUpdate();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            JDBCTools.release(null, preparedStatement, null);
        }
    }

    public static <E> E getforValue(String sql, Object ... args) throws Exception{
        Connection Conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try{
            Conn = JDBCTools.getConnection2();
            Conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
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

    //PreparedStatement预编译，更快
    //批处理可以减少通信开销，从而提高性能能
    public static void batch() throws Exception{
        Connection Conn = null;

        PreparedStatement  preparedStatement = null;
        try{
            Conn = JDBCTools.getConnection2();
            JDBCTools.beginTx(Conn);
            String sql = "insert into user(balance) values(?)";
            preparedStatement = Conn.prepareStatement(sql);
            long begin = System.currentTimeMillis();
            for(int i = 0; i < 10000; i++){
                preparedStatement.setObject(1, i);
                //积攒sql
                preparedStatement.addBatch();
                //每300条执行一次，并且清空之前积攒的sql
                if((i+1) % 300 == 0){
                    preparedStatement.executeBatch();
                    preparedStatement.clearBatch();
                }
            }
            //总条数不是300的整倍数，最后还需要再执行一次
            if(10000 % 300 != 0){
                preparedStatement.executeBatch();
                preparedStatement.clearBatch();
            }
            long end = System.currentTimeMillis();
            System.out.println("time: "+(end-begin));
            JDBCTools.commit(Conn);
        }catch (Exception e){
            e.printStackTrace();
            JDBCTools.rollback(Conn);
        }finally {
            JDBCTools.release(null, preparedStatement, Conn);
        }
    }
}
