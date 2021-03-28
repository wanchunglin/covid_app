package com.example.loginregis;

import android.content.Context;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import android.widget.Toast;

public class MysqlCon {

    // 資料庫定義
    String mysql_ip = "140.113.123.58";
    int mysql_port = 3306; // Port 預設為 3306
    String db_name = "test";
    String url = "jdbc:mysql://"+mysql_ip+":"+mysql_port+"/"+db_name;
    String db_user = "android";
    String db_password = "Android,1234";

    public void run() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Log.v("DB","加載驅動成功");
        }catch( ClassNotFoundException e) {
            Log.e("DB","加載驅動失敗");
            return;
        }

        // 連接資料庫
        try {
            Connection con = DriverManager.getConnection(url,db_user,db_password);
            Log.v("DB","遠端連接成功");
        }catch(SQLException e) {
            Log.e("DB","遠端連接失敗");
            Log.e("DB", e.toString());
        }
    }

    public String getData() {
        String data = "";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, db_user, db_password);
            String sql = "SELECT * FROM person ";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next())
            {
                String id = rs.getString("stu_id");
                String name = rs.getString("na_me");
                String phone = rs.getString("phnum");
                String mail = rs.getString("email");
                data += id + ", " + name + ", " + phone + ", " + mail + "\n";
            }
            st.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return data;
    }
    public boolean log_in(String stu, String pa) {
        boolean login = false;
        Connection con = null;
        Statement st = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(url, db_user, db_password);
            String sql = String.format("SELECT if(pass_word = '%s',true,false) as login FROM person where stu_id = '%s'", pa, stu);
            Log.v("OK", sql);
            st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                login = rs.getBoolean("login");
            }
            st.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return login;
    }
    public boolean verifying(String stu, String pa) {
        boolean login = false;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, db_user, db_password);
            String sql = String.format("SELECT if(verify = '%s',true,false) as login FROM test.temperson where stu_id = '%s'", pa, stu);
            Log.v("OK", sql);
            PreparedStatement st = con.prepareStatement(sql);
            ResultSet rs = st.executeQuery(sql);
            while (rs.next())
                login = rs.getBoolean("login");

            if(login){
                sql = "insert into person select na_me,phnum,email,stu_id,pass_word from temperson where stu_id = '"+ stu +"'";
                Log.v("OK", sql);
                st.executeUpdate(sql);

                sql = "DELETE FROM temperson WHERE stu_id = '" + stu + "'";
                Log.v("OK", sql);
                st.executeUpdate(sql);
            }
            st.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return login;
    }

    public void insertData(String[] data) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, db_user, db_password);

            String sql2=
                    "INSERT INTO TEMPERSON (`na_me`, `phnum`, `email`, `stu_id`,`pass_word`) VALUES ('" + data[0]
                            + "', '" + data[1] + "', '" + data[2] + "', '" + data[3] + "', '" + data[4] + "');";
            Log.v("DB", sql2 );
            Statement st = con.createStatement();

            st.executeUpdate(sql2);
            st.close();
            Log.v("DB", "寫入資料完成：" + data);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            Log.e("DB", "寫入資料失敗");
            Log.e("DB", e.toString());
        }
    }
    public void uploadimg(String[] data){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, db_user, db_password);

            String sql2=
                    "INSERT INTO image (`stu_id`,`file_data`) VALUES ('" + data[0]
                            + "', '" + data[1] + "');";
            Log.v("DB", sql2 );
            Statement st = con.createStatement();

            st.executeUpdate(sql2);
            st.close();
            Log.v("DB", "寫入資料完成：" + data);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            Log.e("DB", "寫入資料失敗");
            Log.e("DB", e.toString());
        }
    }
}