package sjf.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;

import sjf.db.tables.Account;

public class SJFSQLite {
	SQLiteConfig config=null;
	SQLiteDataSource ds=null;
	Connection con=null;
	String dbPath;
	boolean isInit=false;
	
	public SJFSQLite(String dbPath)
	{
		this(dbPath, true, true);
	}
	
	public SJFSQLite(String dbPath, boolean sharedCache, boolean recursiveTriggers)
	{
		config = new SQLiteConfig();
        // config.setReadOnly(true);   
        config.setSharedCache(true);
        config.enableRecursiveTriggers(true);
        this.dbPath = dbPath;
	}

	public boolean init() throws SQLException
	{
		if(!openDatasource(dbPath)) return false;
		if(!openConnection()) return false;
		isInit = true;
		return isInit;
	}
	
	public void destroy() 
	{
		try
		{
			if(con!=null) con.close();
			con = null;
			ds = null;	
			isInit = false;
		}
		catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	protected boolean openDatasource(String dbPath)
	{
		ds = new SQLiteDataSource(config); 
        ds.setUrl(String.format("jdbc:sqlite:%s", dbPath));
        return true;
	}
	
	protected boolean openConnection() throws SQLException
	{
		if(ds!=null) {
			con = ds.getConnection();
			return true;
		}
		return false;
	}
	
	public Connection getConnection() throws SQLException
	{
		if(ds!=null) return ds.getConnection();
		return null;
	}
	
	public synchronized ResultSet executeQuery(String sql) throws SQLException
	{
		if(isInit)
		{
			return con.createStatement().executeQuery(sql);
		}
		return null;
	}
	
	/**
	 * BD: Check if given table exist or not.
	 * REF:
	 * 		- http://stackoverflow.com/questions/1601151/how-do-i-check-in-sqlite-whether-a-table-exists
	 * @param tableName
	 * @return True if given table exist, False otherwise.
	 */
	public boolean isTableExist(String tableName) throws SQLException
	{
		if(isInit)
		{
			String sql = String.format("SELECT name FROM sqlite_master WHERE type='table' AND name='%s';", tableName);
			Statement stat = null;
	        ResultSet rs = null;
			stat = con.createStatement();
			rs = stat.executeQuery(sql);
			boolean isExist = rs.next();
			rs.close();
			return isExist;
		}
		return false;
	}
	
	public Account fetchAccount() throws SQLException
	{
		if(isInit)
		{
			Account account = new Account(con);
			if(!isTableExist(Account.DBName))
			{
				account.createTable();
			}
			return account;
		}
		return null;
	}
	
	public static void main(String args[]) throws Exception
	{
		SJFSQLite sjfSQL = new SJFSQLite("sjf.db");
		sjfSQL.init();
		Account act = sjfSQL.fetchAccount();
		act.insert("john", "1234", "Taiwan");
		sjfSQL.destroy();
	}
}
