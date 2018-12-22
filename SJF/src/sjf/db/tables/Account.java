package sjf.db.tables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class Account {
	public static String DBName="account";
	Connection conn;

	public Account(Connection con){this.conn = con;}
	
	public void createTable() throws SQLException
	{
        String sql = String.format("DROP TABLE IF EXISTS %s ;create table %s(name string, password string, country string);",DBName,DBName);
        Statement stat = null;
        stat = conn.createStatement();
        stat.executeUpdate(sql);
    }
	
	//drop table
    public void dropTable()throws SQLException{
        String sql = String.format("drop table %s", DBName);
        Statement stat = null;
        stat = conn.createStatement();
        stat.executeUpdate(sql);
    }

    //新增
    public void insert(String name, String password, String country)throws SQLException{
        String sql = String.format("insert into %s (name,password,country) values(?,?,?)", DBName);
        PreparedStatement pst = null;
        pst = conn.prepareStatement(sql);
        int idx = 1 ; 
        pst.setString(idx++, name);
        pst.setString(idx++, password);
        pst.setString(idx++, country);
        pst.executeUpdate(); 
    }
    
    //修改
    public void updatePassword(String name, String password)throws SQLException
    {
        String sql = String.format("update %s set password = ? where name = ?", DBName);
        PreparedStatement pst = null;
        pst = conn.prepareStatement(sql);
        int idx = 1 ; 
        pst.setString(idx++, password);
        pst.setString(idx++, name);
        pst.executeUpdate();
    }
    
    //修改
    public void updateCountry(String name, String country) throws SQLException
    {
    	String sql = String.format("update %s set country = ? where name = ?", DBName);
        PreparedStatement pst = null;
        pst = conn.prepareStatement(sql);
        int idx = 1 ; 
        pst.setString(idx++, country);
        pst.setString(idx++, name);
        pst.executeUpdate();
    }
    
    //刪除
    public void delete(String name)throws SQLException{
        String sql = String.format("delete from %s where name = ?", DBName);
        PreparedStatement pst = null;
        pst = conn.prepareStatement(sql);
        int idx = 1 ; 
        pst.setString(idx++, name);
        pst.executeUpdate();
    }
}
