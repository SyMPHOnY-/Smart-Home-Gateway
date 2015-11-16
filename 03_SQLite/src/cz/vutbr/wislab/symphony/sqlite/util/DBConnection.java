package cz.vutbr.wislab.symphony.sqlite.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBConnection {

	public Connection c;
	public static DBConnection db;	
	private DBConnection()
	{
		try {
			Class.forName("org.sqlite.JDBC");
			//"/tiny/symphony"
			this.c = DriverManager.getConnection("jdbc:sqlite:" +"/home/student/Desktop/SQlite/symphony");// "/home/student/Desktop/SQlite/symphony");
			c.setAutoCommit(true);
		} catch (ClassNotFoundException | SQLException e) {
			DBConnection.closeDB();
			e.printStackTrace();
		}
	}
	
	public static synchronized DBConnection getDbCon()
	{
		if (db == null)
		{
			db = new DBConnection();
		}
		return db;
	}

	
	public static synchronized void closeDB()
	{
		if (db != null)
		{
			try {
				db.c.close();
				db.c = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}
}
