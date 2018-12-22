package sjf.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import sjf.db.SJFSQLite;
import sjf.utils.TKit;

@WebServlet("/Login.do")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();        
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.printf("\t[Info] doGet!\n");
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.printf("\t[Info] doPost!\n");
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		HttpSession session = request.getSession();
		ServletContext sc = request.getServletContext();
		String userName = request.getParameter("userName");
		String passwd = request.getParameter("passwd");
		System.out.printf("\t[Info] User='%s' Login....(%s)\n", userName, passwd);
		//RequestDispatcher loginPage = request.getRequestDispatcher("/login.jsp");
		if(userName!=null && userName.equalsIgnoreCase("guest"))
		{
			session.setAttribute("user", "guest");
			session.setMaxInactiveInterval(10*60); // 10 minutes			
			response.sendRedirect(String.format("%s/Demo/index.jsp", request.getContextPath()));
		}
		else
		{
			String sql = String.format("SELECT * FROM account WHERE name='%s'", userName);
			if(passwd==null || passwd.isEmpty())
			{
				response.getWriter().write("{success:false,errors:{passwd:'Empty password!'}}");
				request.setAttribute("error", "Empty password!");
				return;
			}
			
			// If "SQLiteDB_Path" Context param exist, using SQLite DB, otherwise MySQL
			String sqlDBPath = sc.getInitParameter("SQLiteDB_Path");
			if(sqlDBPath!=null && !sqlDBPath.isEmpty())
			{
				// SQLite
				SJFSQLite sjfDB = new SJFSQLite(sqlDBPath);
				try
				{
					sjfDB.init();
					ResultSet rs = sjfDB.executeQuery(sql);
					if(rs.next())
	                {
	                	if(passwd.equals(rs.getString("password")))
	                	{	                		
	                		String from = TKit.GetClientIpAddr(request);
	                		session.setAttribute("user", userName);
	                		session.setAttribute("from", from);
	        				session.setMaxInactiveInterval(30*60);
	        				response.getWriter().write("{success:true}");
	        				System.out.printf("\t[SQLite] %s login from %s...\n", userName, from);
	        				return;
	                	}
	                	else
	                	{
	                		response.getWriter().write("{success:false,errors:{passwd:'Wrong password!'}}");
	        				request.setAttribute("error", "Wrong password!");
	        				return;
	                	}
	                }	                
	                rs.close();   
	                response.getWriter().write("{success:false,errors:{userName:'Unknown account!'}}");
	    			request.setAttribute("error", "Unknown account!");
				}
				catch(SQLException e)
				{
					System.err.printf("\t[Error] %s\n", e.getMessage());   
		            e.printStackTrace();   
		            response.getWriter().write(String.format("{success:false,errors:{userName:'%s!'}}", e.getMessage()));
					request.setAttribute("error", e.getMessage());
				}
				finally
				{
					//System.out.printf("\t[SQLite] Destroy DB Connection...\n");
					sjfDB.destroy();
				}
			}
			else  
			{
				// MySQL
				String driver = "com.mysql.jdbc.Driver";   
		        String db_url = sc.getInitParameter("DB_URL"); //"jdbc:mysql://localhost:3306/sjf";   
		        String db_user = sc.getInitParameter("DB_USER"); //"sjf_admin";   
		        String db_password = sc.getInitParameter("DB_PASSWD"); //"sjf1234";
		        try {   
		            Class.forName(driver);   
		            Connection conn = DriverManager.getConnection(db_url,db_user,db_password);  
		  
		            if(conn != null && !conn.isClosed()) {  
		                //System.out.println("\t[Info] 資料庫連線測試成功！");   
		                // ServiceName|Fail|词语错误|lt-003|Suggestion
		                Statement stmt = conn.createStatement();
		                ResultSet rs = stmt.executeQuery(sql);
		                if(rs.next())
		                {
		                	if(passwd.equals(rs.getString("password")))
		                	{	                		
		                		String from = TKit.GetClientIpAddr(request);
		                		session.setAttribute("user", userName);
		                		session.setAttribute("from", from);
		        				session.setMaxInactiveInterval(30*60);
		        				response.getWriter().write("{success:true}");
		        				System.out.printf("\t[MySQL] %s login from %s...\n", userName, from);
		        				conn.close();
		        				return;
		                	}
		                	else
		                	{
		                		response.getWriter().write("{success:false,errors:{passwd:'Wrong password!'}}");
		        				request.setAttribute("error", "Wrong password!");
		        				conn.close();
		        				return;
		                	}
		                }	                
		                conn.close();   
		                response.getWriter().write("{success:false,errors:{userName:'Unknown account!'}}");
		    			request.setAttribute("error", "Unknown account!");
		            }  else {
		            	response.getWriter().write(String.format("{success:false,errors:{userName:'DB 連線錯誤!'}}"));
		            	request.setAttribute("error", "DB 連線錯誤!");
		            }
		        }   
		        catch(ClassNotFoundException e) {   
		            System.err.println("\t[Error] 找不到驅動程式類別");   
		            e.printStackTrace();   
		            response.getWriter().write(String.format("{success:false,errors:{userName:'找不到驅動程式類別!'}}"));
					request.setAttribute("error", e.getMessage());
		        }   
		        catch(SQLException e) {   
		            System.err.printf("\t[Error] %s\n", e.getMessage());   
		            e.printStackTrace();   
		            response.getWriter().write(String.format("{success:false,errors:{userName:'%s!'}}", e.getMessage()));
					request.setAttribute("error", e.getMessage());
		        }
			}
			
		}		
	}
}
