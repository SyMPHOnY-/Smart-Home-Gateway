package cz.vutbr.wislab.symphony.sqlite;

import java.util.Map;


public interface SQLiteservice {
	
	/**
	 * Set database file location.
	 *
	 *@param Path to database file
	 */	
	/*void setDatabazeFile(String filePath);*/
	
	/**
	 * Store value to database.
	 *
	 *@param Name of table.
	 *@param Serial number of data.
	 *@param Value.
	 *@param Type of data for example hot, cold, tarif1, tarif2.
	 */	
	void storeData(String tableName, String serial, String value, String type);
	
	/**
	 * Set database file location.
	 *
	 *@param tableName Name of table.
	 *@param serial Serial number of data.
	 *@param period Period of returned data. Available values: 5m, 1h, 1D, 2D, 1W, 2W, 1M, 1Y, 2Y.
	 *@param type Type of data for example hot, cold, tarif1, tarif2.
	 *
	 *@return Return TreeMap <Time Stamp, Value>
	 */	
	Map<Long, String> getStoredData(String tableName, String serial, String period, String type);
	
	/**
	 * Return actual value.
	 *
	 *@param serial Serial number of data.
	 *@param type Type of data for example hot, cold, tarif1, tarif2.
	 *
	 */	
	String actualValue(String serial, String type);

	/**
	 * Return maximum value for given period.
	 *
	 *@param tableName Name of table.
	 *@param serial Serial number of data.
	 *@param period Period of returned data. Available values: 5m, 1h, 1D, 2D, 1W, 2W, 1M, 1Y, 2Y.
	 *@param type Type of data for example hot, cold, tarif1, tarif2.
	 *
	 */	
	String maxValue(String tableName, String serial, String period, String type);
	
	/**
	 * Return minimum value for given period
	 *
	 *@param tableName Name of table.
	 *@param serial Serial number of data.
	 *@param period Period of returned data. Available values: 5m, 1h, 1D, 2D, 1W, 2W, 1M, 1Y, 2Y.
	 *@param type Type of data for example hot, cold, tarif1, tarif2.
	 *
	 */	
	String minValue(String tableName, String serial, String period, String type);
	
	/**
	 * Return average value for given period. This method is for meters that show actual value.
	 *
	 *@param tableName Name of table.
	 *@param serial Serial number of data.
	 *@param period Period of returned data. Available values: 5m, 1h, 1D, 2D, 1W, 2W, 1M, 1Y, 2Y.
	 *@param type Type of data for example hot, cold, tarif1, tarif2.
	 *
	 */	
	String avgValue(String tableName, String serial, String period, String type);
	
	/**
	 * Return average value for given period. This method is for meters that show state from start of measuring.
	 *
	 *@param tableName Name of table.
	 *@param serial Serial number of data.
	 *@param period Period of returned data. Available values: 5m, 1h, 1D, 2D, 1W, 2W, 1M, 1Y, 2Y.
	 *@param type Type of data for example hot, cold, tarif1, tarif2.
	 *
	 */	
	String avgValueWE(String tableName, String serial, String period, String type);
	
	/**
	 * Return today's first value stored in database.
	 *
	 *@param tableName Name of table.
	 *@param serial Serial number of data.
	 *@param type Type of data for example hot, cold, tarif1, tarif2.
	 *
	 */	
	String todayValue(String tableName, String serial, String type);
	
	/**
	 * Return yesterday's first value stored in database.
	 *
	 *@param tableName Name of table.
	 *@param serial Serial number of data.
	 *@param type Type of data for example hot, cold, tarif1, tarif2.
	 *
	 */	
	String yesterdayValue(String tableName, String serial, String type);
	
	/**
	 * Return yesterday's last value stored in database.
	 *
	 *@param tableName Name of table.
	 *@param serial Serial number of data.
	 *@param type Type of data for example hot, cold, tarif1, tarif2.
	 *
	 */	
	String yesterdayLastValue(String tableName, String serial, String type);
	
	/**
	 * Adds UPNP Device to database.
	 *
	 *@param uid of device.
	 *@param serial Serial number of data.
	 *
	 */	
	void addUPNPDevice(String uid, String options);
	
	/**
	 * Adds Map of UPNP Device to database.
	 *
	 *@param Map of UPNP Devices in format Map<uid, options>.
	 *
	 */	
	void addUPNPDevices(Map<String, String> devices);
	
	/**
	 * Return Map<uid, options> of all UPNP devices stored in database.
	 *
	 *
	 */	
	Map<String,String>getAllUPNPDevices();
	
	/**
	 * Return options of UPNP Device.
	 *
	 *@param uid of device.
	 *
	 */	
	String getUPNPDevice(String uid);
}
