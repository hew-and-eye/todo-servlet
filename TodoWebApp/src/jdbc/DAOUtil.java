package jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DAOUtil {
	private Connection conn;
	public DAOUtil() {
		conn = null;
	}
	public void makeConn() throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		conn = DriverManager.getConnection(DUUP.url, DUUP.user, DUUP.password);
	}
	public void closeConn() throws SQLException {
		System.out.println("closing connection...");
		if(conn != null)
			conn.close();
		else System.out.println("connection was null");
	}
	public Connection getConn() {
		return conn;
	}
	public boolean checkConn() {
		try {
			makeConn();
			closeConn();
			return true;
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			return false;
		}
	}
}
