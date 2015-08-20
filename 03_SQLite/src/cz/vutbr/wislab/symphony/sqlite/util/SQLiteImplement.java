package cz.vutbr.wislab.symphony.sqlite.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import cz.vutbr.wislab.symphony.sqlite.SQLiteservice;


public class SQLiteImplement implements SQLiteservice {
	
	private Connection c = null;
	private PreparedStatement stmt = null;
	private static String databaseFile = null;
	
	private static final long MINUTE = 60L;
	private static final long FIVEMINUTE = 300L;
	private static final long HOUR = 3600L;
	private static final long DAY = 86400L;
	private static final long TWODAY = 172800L;
	private static final long WEEK = 604800L;
	private static final long TWOWEEK = 1209600L;
	private static final long MONTH = 2592000L;
	private static final long YEAR = 31536000L;
	private static final long TWOYEAR = 63072000L;
		
	private Map<String, Long> term = new HashMap<String, Long>();
	private Map<String, Long> res = new HashMap<String, Long>();
	
	
	public SQLiteImplement()
	{
		term.put("2D", TWODAY);
		term.put("1Y", YEAR);
		term.put("2Y", TWOYEAR);
		
		res.put("2D", MINUTE);
		res.put("1Y", HOUR);
		res.put("2Y", DAY);
	}
	
	public  void connectToDatabase()
	{
		//Connecting to database
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + SQLiteImplement.databaseFile);
			c.setAutoCommit(false);
		} 
		catch (ClassNotFoundException e) {
			System.out.println("Package with class org.sqlite.JDBC not running!");
		} 
		catch (SQLException e)
		{
			System.out.println("DriverManager error!");
		} 
		catch (Exception e) {
			System.out.println("shit hapens...");
			e.printStackTrace();
		}
	}
	
	private void deleteOld(String tableName, String period, String type, String serial, long timeStamp) throws SQLException
	{
		//Delete records older then now - period resolution from database
		String deleteSQL = "DELETE from " + tableName + " where serial='" + serial + "' AND type='" + type +
							"' AND period='" + period + "' AND timestamp < " + (timeStamp-term.get(period));
		stmt = c.prepareStatement(deleteSQL);
		stmt.execute();
	}
	
	public void initializeDatabase() throws SQLException
	{
		this.connectToDatabase();
		String table = null;
		String serial = null;
		String type = null;
		long lastTimestamp = 0;
		//Get all table names
		String sqlQueryTables = "SELECT * FROM sqlite_master WHERE type='table'";
		stmt = c.prepareStatement(sqlQueryTables);
		ResultSet rsTables = stmt.executeQuery();
		
		while(rsTables.next())
		{
			table = rsTables.getString(2);
			if (!table.equals("actual_values") && !table.equals("upnp_devices"))
			{
				//Get all types and serial from table
				String sqlQueryTypes = "SELECT DISTINCT type, serial FROM " + table;
				stmt = c.prepareStatement(sqlQueryTypes);
				ResultSet rsTypes = stmt.executeQuery();
				
				while (rsTypes.next())
				{
					serial = rsTypes.getString("serial");
					type = rsTypes.getString("type");
					
					for (String period : res.keySet())
					{
						long i = 0;
						lastTimestamp = getLastTimestamp(table, serial, type, period);
						if (lastTimestamp != 0)
						{
							//Initialize data - store null value to database when it was switch of
							for (i = lastTimestamp + res.get(period); i < generateTimeStamp(period); i+=res.get(period)) {
								storeToDatabase(table, serial, "NULL", type, i, period);
							}
							//delete old values
							deleteOld(table, period, type, serial, i);
						}
					}
				}
			}
			else
			{
				String sqlQuery = null;
				sqlQuery = "DELETE FROM " + table;
				try {
					stmt = c.prepareStatement(sqlQuery);
					stmt.execute();
				} catch (SQLException e) {
					System.out.println("Can not write to database!");
					e.printStackTrace();
				}
			}
		}
		this.closeDatabase();
	}
	
	public void closeDatabase()
	{
		try {
			stmt.close();
			c.commit();
			c.close();
			c = null;
		} catch (SQLException e) {
			System.out.println("Can not close database!");
			e.printStackTrace();
		}
	}
	
	private String getQueryPart(String tableName, String serial, String period, String type)
	{
		String lPeriod = null;
		long startTime = 0;
		
		//generate query start times
		switch (period) {
		case "5m":
			lPeriod = "2D";
			startTime = generateTimeStamp(lPeriod) - FIVEMINUTE;
			break;
			
		case "1h":
			lPeriod = "2D";
			startTime = generateTimeStamp(lPeriod) - HOUR;
			break;
			
		case "1D":
			lPeriod = "2D";
			startTime = generateTimeStamp(lPeriod) - DAY;
			break;
		
		case "2D":
			lPeriod = "2D";
			startTime = generateTimeStamp(lPeriod) - TWODAY;
			break;
			
		case "1W":
			lPeriod = "1Y";
			startTime = generateTimeStamp(lPeriod) - WEEK;
			break;
			
		case "2W":
			lPeriod = "1Y";
			startTime = generateTimeStamp(lPeriod) - TWOWEEK;
			break;
			
		case "1M":
			lPeriod = "1Y";
			startTime = generateTimeStamp(lPeriod) - MONTH;
			break;
			
		case "1Y":
			lPeriod = "1Y";
			startTime = generateTimeStamp(lPeriod) - YEAR;
			break;
			
		case "2Y":
			lPeriod = "2Y";
			startTime = generateTimeStamp(period) - TWOYEAR;
			break;
		
		case "tMid":
			lPeriod = "2D";
			startTime = generateTimeStamp("2Y");
			break;
			
		case "yMid":
			lPeriod = "2D";
			startTime = generateTimeStamp("2Y") - DAY;
			break;

		default:
				startTime = generateTimeStamp("2Y") - TWOYEAR;
			break;
		}
		return "FROM "+tableName+" WHERE serial='"+serial+"' AND type='"+type+"' AND period='"+lPeriod+"' AND timestamp >=" + startTime;
	}
	
	private long generateTimeStamp(String period)
	{
		//Generate time stamps from Calendar class
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		if (period == "2D") return cal.getTimeInMillis()/1000; //resolution minutes
		cal.set(Calendar.MINUTE, 0);
		if (period == "1Y") return cal.getTimeInMillis()/1000; //resolution hours
		cal.set(Calendar.HOUR_OF_DAY, 0);
		if (period == "2Y") return cal.getTimeInMillis()/1000; //resolution days
		return 0;
	}
	
	private void storeToDatabase(String tableName, String serial, String value, String type, long timeStamp, String period) throws SQLException
	{
		//store prepared values to database
		String sqlString = "INSERT INTO " + tableName + " (serial, timestamp, period, value, type) VALUES(?,?,?,?,?)";
		stmt = c.prepareStatement(sqlString);
		stmt.setString(1, serial);
		stmt.setLong(2, timeStamp);
		stmt.setString(3, period);
		stmt.setString(4, value);
		stmt.setString(5, type);
		stmt.execute();
	}
	
	private long getLastTimestamp(String tableName, String serial, String type, String period) throws SQLException
	{
		//return last time stamp
		String sqlStringTimes = "SELECT * FROM " + tableName + " WHERE period='"+period+"' AND type='"+type+"' ORDER BY timestamp  DESC LIMIT 1";
		stmt = c.prepareStatement(sqlStringTimes);
		ResultSet rsTimes = stmt.executeQuery();
		long timeStamp = 0;
		while (rsTimes.next()) {
			timeStamp = rsTimes.getLong("timestamp");
		}
		return timeStamp;
	}
	
	private String getStoredValue(String sqlQuery)
	{
		this.connectToDatabase();
		String value = "NULL";
		try {
			stmt = c.prepareStatement(sqlQuery);
			ResultSet rsVal = stmt.executeQuery();
			if(rsVal.next()){
				value = rsVal.getString("value");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.closeDatabase();
		return value;
	}
	
	private void setActualValue(String serial, String type, String value)
	{
		this.connectToDatabase();
		String sqlQuery = null;
		sqlQuery = "DELETE FROM actual_values WHERE serial='" + serial + "' AND type='" + type + "'";
		try {
			stmt = c.prepareStatement(sqlQuery);
			stmt.execute();
			sqlQuery = "INSERT INTO actual_values(serial, value, type) VALUES(?,?,?)";
			stmt = c.prepareStatement(sqlQuery);
			stmt.setString(1, serial);
			stmt.setString(2, value);
			stmt.setString(3, type);
			stmt.execute();
		} catch (SQLException e) {
			System.out.println("Can not write to database!");
			e.printStackTrace();
		}
		this.closeDatabase();
	}
	
	private static Double getDateDiff(Long date1, Long date2) {
	    long diffInSeconds = date2 - date1;
	    Double timeDiff;
	    timeDiff = TimeUnit.MINUTES.convert(diffInSeconds,TimeUnit.SECONDS)/1440.0;
	    return timeDiff;
	}
	
	private TreeMap<Long, String> getStoredDataWONull(String tableName, String serial, String period, String type) {
		TreeMap<Long, String> data = new TreeMap<Long, String>();
		this.connectToDatabase();
		String sqlQuery = null;
		sqlQuery = "SELECT * " + getQueryPart(tableName, serial, period, type) + " AND value !='NULL'";
		try {
			stmt = c.prepareStatement(sqlQuery);
			ResultSet rsData = stmt.executeQuery();
			while (rsData.next())
			{
				data.put(rsData.getLong("timestamp"), rsData.getString("value"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.closeDatabase();
		return data;
	}
	
	@Override
	public Map<Long, String> getStoredData(String tableName, String serial, String period, String type) {
		Map<Long, String> data = new TreeMap<Long, String>();
		this.connectToDatabase();
		String sqlQuery = null;
		sqlQuery = "SELECT * " + getQueryPart(tableName, serial, period, type);
		try {
			stmt = c.prepareStatement(sqlQuery);
			ResultSet rsData = stmt.executeQuery();
			while (rsData.next())
			{
				data.put(rsData.getLong("timestamp"), rsData.getString("value"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.closeDatabase();
		return data;
	}

	@Override
	public void storeData(String tableName, String serial, String value, String type) {
		long currentTime = Calendar.getInstance().getTimeInMillis()/1000; 
		
		try {
			setActualValue(serial, type, value);
			this.connectToDatabase();
			for (String period : res.keySet()) {
				long lastTimestamp = this.getLastTimestamp(tableName, serial, type, period);
				long timeStamp = 0;
				
				//empty database
				if (lastTimestamp == 0)
				{
					timeStamp = generateTimeStamp(period);
					storeToDatabase(tableName, serial, value, type, timeStamp, period);
				}
				//last time stamp is in resolution
				else if (currentTime - lastTimestamp > res.get(period) && currentTime - lastTimestamp < res.get(period) * 2)
				{
					timeStamp = lastTimestamp + res.get(period);
					storeToDatabase(tableName, serial, value, type, timeStamp, period);
					deleteOld(tableName, period, type, serial, timeStamp);
				}
				//if last time stamp is older then now - 2 * resolution 
				else if (currentTime - lastTimestamp > res.get(period) * 2)
				{
					long i = 0;
					for (i = lastTimestamp + res.get(period); i < generateTimeStamp(period); i+=res.get(period)) {
						storeToDatabase(tableName, serial, "NULL", type, i, period);
					}
					timeStamp = i;
					storeToDatabase(tableName, serial, value, type, timeStamp, period);
					deleteOld(tableName, period, type, serial, timeStamp);
				}
			}
		} catch (SQLException e) {
			System.out.println("Can not write to database.");
			e.printStackTrace();
		}
		this.closeDatabase();
		
	}

	@Override
	public String maxValue(String tableName, String serial, String period, String type) {
		String sqlQuery = null;
		sqlQuery = "SELECT MAX(value) AS value " + getQueryPart(tableName, serial, period, type) + " AND value !='NULL'";
		return getStoredValue(sqlQuery);
	}

	@Override
	public String minValue(String tableName, String serial, String period, String type) {
		String sqlQuery = null;
		sqlQuery = "SELECT MIN(value) AS value " + getQueryPart(tableName, serial, period, type)+ " AND value !='NULL'";
		return getStoredValue(sqlQuery);
	}

	@Override
	public String avgValue(String tableName, String serial, String period, String type) {
		String sqlQuery = null;
		sqlQuery = "SELECT AVG(value) AS value " + getQueryPart(tableName, serial, period, type)+ " AND value !='NULL'";
		return getStoredValue(sqlQuery);
	}

	@Override
	public void setDatabazeFile(String filePath) {
		SQLiteImplement.databaseFile = filePath;
		try {
			initializeDatabase();
		} catch (SQLException e) {
			System.out.println("Can not write to database!");
			e.printStackTrace();
		}
	}

	@Override
	public String todayValue(String tableName, String serial, String type) {
		String sqlQuery = null;
		sqlQuery = "SELECT value AS value " + getQueryPart(tableName, serial, "tMid", type)+ " AND value !='NULL' LIMIT 1";
		return getStoredValue(sqlQuery);
	}

	@Override
	public String yesterdayValue(String tableName, String serial, String type) {
		String sqlQuery = null;
		sqlQuery = "SELECT value AS value " + getQueryPart(tableName, serial, "yMid", type)+
				   " AND timestamp <"+generateTimeStamp("2Y") + " AND value !='NULL' LIMIT 1";
		return getStoredValue(sqlQuery);
	}

	@Override
	public String actualValue(String serial, String type) {
		String sqlQuery = null;
		sqlQuery = "SELECT value AS value FROM actual_values WHERE serial='" + serial + "' AND type='" + type+ "'"; 
		return getStoredValue(sqlQuery);
	}

	@Override
	public String avgValueWE(String tableName, String serial, String period, String type) {
		Double timeDiff;
		Double valDiff;
		String value = "NULL";
		TreeMap<Long, String> data =  this.getStoredDataWONull(tableName, serial, period, type);
	
		if(data.size() !=0) {
			valDiff = Double.parseDouble(data.lastEntry().getValue()) - Double.parseDouble(data.firstEntry().getValue());
			timeDiff = SQLiteImplement.getDateDiff(data.firstKey(), data.lastKey());
			value = Double.toString(valDiff/timeDiff);
		}
		return value;
	}

	@Override
	public String yesterdayLastValue(String tableName, String serial, String type) {
		String sqlQuery = null;
		sqlQuery = "SELECT value AS value " + getQueryPart(tableName, serial, "yMid", type)+
				   " AND timestamp <"+generateTimeStamp("2Y") + " AND value !='NULL' ORDER BY value DESC LIMIT 1";
		return getStoredValue(sqlQuery);
	}

	@Override
	public void addUPNPDevice(String uid, String options) {
		String sqlQuery = null;
		sqlQuery = "INSERT INTO upnp_devices(uid, value) VALUES(?,?)";
		this.connectToDatabase();
		try {
			stmt = c.prepareStatement(sqlQuery);
			stmt.setString(1, uid);
			stmt.setString(2, options);
			stmt.execute();
			
		} catch (SQLException e) {
			System.out.println("Can not write to database!");
			e.printStackTrace();
		}
		this.closeDatabase();
	}

	@Override
	public void addUPNPDevices(Map<String, String> devices) {
		String sqlQuery = null;
		sqlQuery = "INSERT INTO upnp_devices(uid, value) VALUES(?,?)";
		this.connectToDatabase();
		try {
			for (Map.Entry<String, String> entry : devices.entrySet()) {
				stmt = c.prepareStatement(sqlQuery);
				stmt.setString(1, entry.getKey());
				stmt.setString(2, entry.getValue());
				stmt.execute();
			}	
		} catch (SQLException e) {
			System.out.println("Can not write to database!");
			e.printStackTrace();
		}
		this.closeDatabase();
	}

	@Override
	public Map<String, String> getAllUPNPDevices() {
		String sqlQuery = null;
		Map<String, String> devices = new HashMap<String, String>();
		sqlQuery = "SELECT * FROM upnp_devices";
		this.connectToDatabase();
		try {
			stmt = c.prepareStatement(sqlQuery);
			ResultSet rsVal = stmt.executeQuery();
			while(rsVal.next()){
				devices.put(rsVal.getString("uid"), rsVal.getString("value"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.closeDatabase();
		return devices;
	}

	@Override
	public String getUPNPDevice(String uid) {
		String sqlQuery = null;
		sqlQuery = "SELECT value AS value FROM upnp_devices WHERE uid ='"+uid+"'";
		return getStoredValue(sqlQuery);
	}

}
