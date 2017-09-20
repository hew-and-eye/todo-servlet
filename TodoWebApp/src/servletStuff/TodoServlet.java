package servletStuff;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import entities.Task;
import jdbc.DAOService;

/**
 * Servlet implementation class TodoServlet
 */
@WebServlet("/TodoServlet")
public class TodoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public TodoServlet() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		// handle cors preflight stuff /////////////////////////////////////////////////
		response.addHeader("Access-Control-Allow-Origin","*");
	    response.addHeader("Access-Control-Allow-Methods","GET,POST");
	    response.addHeader("Access-Control-Allow-Headers","Origin, X-Requested-With, Content-Type, Accept");
	    if ( request.getMethod().equals("OPTIONS") ) {
	        response.setStatus(HttpServletResponse.SC_OK);
	        return;
	    }
	    ////////////////////////////////////////////////////////////////////////////////
	    
	    // check if connection works ///////////
	    if((new DAOService()).getDao().checkConn())
	    	System.out.println("successfully connected to database");
	    else
	    	System.out.println("could not connect");
	    ////////////////////////////////////////
	    // check that tables exist and create them if necessary ////
	    try {
	    	//(new DAOService()).deleteTables();
			(new DAOService()).makeTables();
		} catch (ClassNotFoundException | SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	    
	    ////////////////////////////////////////////////////////////
	    
		// prep response //////////////////////////
	    response.setContentType("application/json");// set content to json
	    response.setCharacterEncoding("utf-8");
	    PrintWriter out = response.getWriter();
		
		///////////////////////////////////////////
		
		
		// optype dispatcher //////////////////////////////
	    String optype = request.getParameter("optype");
	    System.out.println("optype is" + optype);
		if (optype.equals("login")) {
			System.out.println("trying to log in");
			// get user credentials from request
			String username = request.getParameter("username");
			int password = 0;
			if(request.getParameter("password") != null) 
				password = request.getParameter("password").hashCode(); // password is only processed as text in the client side
			// for now, don't bother with the JDBC stuff. Just check some dummy values to test the servlet
			String jsonString = "";
			String jsonUsers = "[]";
			String jsonTasks = "[]";
			try {
				ArrayList<Task> tasks = (new DAOService()).login(username, password);
				jsonUsers = (new DAOService()).fetchAllUsers();
				jsonTasks = "[";
				for(int i = 0; i < tasks.size(); i++) {
					jsonTasks += tasks.get(i).getJSON();
					if(i < tasks.size()-1) jsonTasks += ",";
				}
				jsonTasks += "]";
				System.out.println(jsonTasks);
				jsonString = "{\"error\":\"false\", \"data\":" + jsonTasks + ",\"users\":" + jsonUsers + "}";
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				jsonString = "{\"error\":\"true\", \"data\":\"Unable to log in. Check your username and password.\" }";
			} finally {
				out.print(jsonString);
				out.flush();
			}
			
		}
		else if (optype.equals("adduser")) {
			System.out.println("reached the adduser block");
			// get user credentials
			String username = request.getParameter("username");
			int password = 0;
			if(request.getParameter("password") != null) 
				password = request.getParameter("password").hashCode(); // password is only processed as text in the client side
			// add to user table
			String jsonString = "{\"error\":\"true\", \"data\":\"Database error: Could not add user :(\" }";
			String jsonUsers = "[]";
			try {
				(new DAOService()).addUser(username, password);
				jsonUsers = (new DAOService()).fetchAllUsers();
				jsonString = "{\"error\":\"false\", \"data\":[],\"users\":" + jsonUsers + "}";
				
			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
			} finally {
				out.print(jsonString);
				out.flush();
			}
		}
		else if (optype.equals("createtask")) {
			// get name, description, and users
			String name = request.getParameter("name");
			String description = request.getParameter("description");
			String[] users = request.getParameterValues("users");
			ArrayList<String> userList = new ArrayList<String>();
			for(int i = 0; i < users.length; i++) {
				userList.add(users[i]);
			}
			// make task out of parameters
			Task task = new Task();
			task.setName(name);
			task.setDescription(description);
			task.complete = false;
			task.setAssignedUsers(userList);
			
			// call DAOService to send task to database
			try {
				(new DAOService()).createTask(task);
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (optype.equals("deletetask")) {
			System.out.print("deleting...");
			// get the task id and delete all rows where it appears in both the task and task_assignment tables
			try {
				(new DAOService()).deleteIdFromTable(Integer.parseInt(request.getParameter("id")), "tasks");
				(new DAOService()).deleteIdFromTable(Integer.parseInt(request.getParameter("id")), "task_assignments");
			} catch (NumberFormatException | ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("deleted");
		}
		else if (optype.equals("edittask") || optype.equals("completetask")) {
			System.out.println("editing the task");
			
			String[] users = request.getParameterValues("users");
			ArrayList<String> userList = new ArrayList<String>();
			for(int i = 0; i < users.length; i++) {
				userList.add(users[i]);
			}
			// make task out of parameters
			Task task = new Task();
			task.setName(request.getParameter("name"));
			task.setDescription(request.getParameter("description"));
			System.out.println(request.getParameter("complete"));
			task.complete = request.getParameter("complete").equals("true");
			task.setAssignedUsers(userList);
			task.setId(Integer.parseInt(request.getParameter("id")));
			try {
				(new DAOService()).editTask(task, optype.equals("edittask"));
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
}
