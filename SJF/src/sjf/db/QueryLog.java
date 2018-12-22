package sjf.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class QueryLog {
	public static String DBName = "query_log";
	
	Connection conn;

	public QueryLog(Connection con){this.conn = con;}
	
	public void createTable() throws SQLException
	{
        String sql = String.format("DROP TABLE IF EXISTS %s ;create table %s(sent_id string, user_name string, sent string, resp string, topic string, ip string);",DBName,DBName);
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
    public void insert(String sent_id, String user_name, String sent, String resp, String topic, String ip)throws SQLException{
    	String sql = String.format("insert into %s (sent_id,user_name,sent,resp,topic,ip) values(?,?,?,?,?,?)", DBName);
        PreparedStatement pst = null;
        pst = conn.prepareStatement(sql);
        int idx = 1 ; 
        pst.setString(idx++, sent_id);
        pst.setString(idx++, user_name);
        pst.setString(idx++, sent);
        pst.setString(idx++, resp);
        pst.setString(idx++, topic);
        pst.setString(idx++, ip);        
        pst.executeUpdate();
    }
}
