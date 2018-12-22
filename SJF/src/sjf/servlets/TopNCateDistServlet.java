package sjf.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import sjf.beans.StatJSonWrapper;
import sjf.db.SJFSQLite;

@WebServlet("/Services/TopNCate")
public class TopNCateDistServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TopNCateDistServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//System.out.printf("\t[Test] doGet!\n");
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");		
		HttpSession session = request.getSession();
		ServletContext sc = request.getServletContext();
		String sql = String.format("SELECT resp FROM query_log WHERE user_name='%s' ORDER BY sent_id DESC LIMIT 500", session.getAttribute("user"));
		String msg;
        StatJSonWrapper statJSonWrapper = null;
        HashMap<String,Integer> cateCntMap = new HashMap<String,Integer>();
		String sqlDBPath = sc.getInitParameter("SQLiteDB_Path");
		if(sqlDBPath!=null && !sqlDBPath.isEmpty())
		{
			// SQLite
			SJFSQLite sjfDB = new SJFSQLite(sqlDBPath);			
			try
			{
				sjfDB.init();
				ResultSet rs = sjfDB.executeQuery(sql);
				while(rs.next())
                {                	
                	String items[] = rs.getString(1).split("\\|");
                	if(items.length==5 && !items[1].equalsIgnoreCase("pass"))
                	{
                		//System.out.printf("\t[Info] Cate=%s\n", items[2]);
                		Integer cnt = cateCntMap.get(items[2]);
                		if(cnt==null) cateCntMap.put(items[2], 1);
                		else cateCntMap.put(items[2], cnt+1);
                	}
                }
                rs.close();                
                //System.out.printf("\t[Info] Top 500 Category Dist:\n");
                /*Iterator<Entry<String,Integer>> iter = cateCntMap.entrySet().iterator();                
                while(iter.hasNext())
                {
                	Entry<String,Integer> ety = iter.next();
                	System.out.printf("\t\t%s=%d\n", ety.getKey(), ety.getValue());                	
                }*/
                statJSonWrapper = new StatJSonWrapper(cateCntMap);
			}
			catch(SQLException e)
			{
				e.printStackTrace();
				statJSonWrapper = new StatJSonWrapper(null);
			}
			finally
			{
				//System.out.printf("\t[SQLite] Destroy DB Connection...\n");
				sjfDB.destroy();
			}
			//msg = statJSonWrapper.toString();			
			System.out.printf("\t[Test] TopNCateDist:\n%s\n", statJSonWrapper.toString());
			response.getWriter().write(statJSonWrapper.toString());
		}
		else
		{
			// MySQL
			String driver = "com.mysql.jdbc.Driver";   
	        String url = sc.getInitParameter("DB_URL"); //"jdbc:mysql://localhost:3306/sjf";   
	        String user = sc.getInitParameter("DB_USER"); //"sjf_admin";   
	        String password = sc.getInitParameter("DB_PASSWD"); //"sjf1234";   
	        
	        try {   
	            Class.forName(driver);   
	            Connection conn =   
	               DriverManager.getConnection(url,   
	                                  user, password);  
	  
	            if(conn != null && !conn.isClosed()) {  
	                //System.out.println("\t[Info] 資料庫連線測試成功！");   
	                // ServiceName|Fail|词语错误|lt-003|Suggestion
	                Statement stmt = conn.createStatement();
	                ResultSet rs = stmt.executeQuery(sql);
	                while(rs.next())
	                {                	
	                	String items[] = rs.getString(1).split("\\|");
	                	if(items.length==5 && !items[1].equalsIgnoreCase("pass"))
	                	{
	                		//System.out.printf("\t[Info] Cate=%s\n", items[2]);
	                		Integer cnt = cateCntMap.get(items[2]);
	                		if(cnt==null) cateCntMap.put(items[2], 1);
	                		else cateCntMap.put(items[2], cnt+1);
	                	}
	                }
	                conn.close();                
	                //System.out.printf("\t[Info] Top 500 Category Dist:\n");
	               /* Iterator<Entry<String,Integer>> iter = cateCntMap.entrySet().iterator();                
	                while(iter.hasNext())
	                {
	                	Entry<String,Integer> ety = iter.next();
	                	System.out.printf("\t\t%s=%d\n", ety.getKey(), ety.getValue());                	
	                }*/
	                statJSonWrapper = new StatJSonWrapper(cateCntMap);
	            }             
	        }   
	        catch(ClassNotFoundException e) {   
	            System.out.println("找不到驅動程式類別");   
	            e.printStackTrace();   
	            statJSonWrapper = new StatJSonWrapper(null);
	        }   
	        catch(SQLException e) {   
	            e.printStackTrace();   
	            statJSonWrapper = new StatJSonWrapper(null);
	        }
	        msg = statJSonWrapper.toString();
	        //System.out.printf("\t[Info] Top N Category Dist Resp:\n%s\n", msg);
			
			// Handling feedback here
			
			response.getWriter().write(msg);
		}		 
	}
}
