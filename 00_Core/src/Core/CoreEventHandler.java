package Core;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import cz.vutbr.wislab.symphony.TVScreenGenerator.util.SlideGenerator;
import cz.vutbr.wislab.symphony.dlna.util.Push;
import cz.vutbr.wislab.symphony.sip.util.SipInterface;
import cz.vutbr.wislab.symphony.sqlite.SQLiteservice;
import cz.vutbr.wislab.symphony.upnp.util.Discovery;

public class CoreEventHandler implements EventHandler {
	
	ServiceReference ref = null;
	
	SQLiteservice sqlite = null;
	SlideGenerator slideGenerator = null;
	Push push = null;
	SipInterface sipInterface = null;
	
	Discovery discovery = null;

	//String path = "//home/student/Desktop/SQLite/symphony";	
	String path = "/tiny/symphony";
	String UDN = null;
	String bonega = "281";
	String weptech = null;
	String pikketron = null;
	
	Double lastCosumption = 0.0;
	Integer warm = 19586;
	Integer cold = 34110;
		
	ArrayList<String[]> deviceArrayList;
	
	boolean generated = false;
	boolean warnning = false;
	
	public CoreEventHandler(BundleContext context) {
		// TODO Auto-generated constructor stub
		
		ref = context.getServiceReference(Push.class.getName()); 
		push = (Push) context.getService(ref);
		
		ref = context.getServiceReference(SlideGenerator.class.getName());
		slideGenerator = (SlideGenerator) context.getService(ref);
		
		ref = context.getServiceReference(SQLiteservice.class.getName());        
		sqlite = (SQLiteservice) context.getService(ref);
		//sqlite.setDatabazeFile(path);

		
		ref = context.getServiceReference(SipInterface.class.getName());
		sipInterface = (SipInterface) context.getService(ref);

		sipInterface.setProfile(new String[] { getLocalIP(), "5065", "udp","23.97.214.3", "5060", 
				"23.97.214.3", "petr","petr" });
		sipInterface.register();
		
		//sipInterface.sendMessage("sip:pavel@23.97.214.3", "Test: sip message from SipBundle");
				
		try {
			ref = context.getServiceReference(Discovery.class.getName()); 		
			discovery = (Discovery) context.getService(ref);	
			discovery.start();			
			syncDeviceList();
			discovery.printDevices();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
	}
	public void syncDeviceList (){

		System.out.println("{UPNP Discovery}:Device list:");
		deviceArrayList = discovery.getDevices();
		
		for (int i=0; i<deviceArrayList.size(); i++){
			System.out.println("#"+i+":");
			System.out.println("Device: "+ deviceArrayList.get(i)[0]);
			System.out.println("Type: "+ deviceArrayList.get(i)[1]);
			System.out.println("UDN: "+ deviceArrayList.get(i)[2]);
			
			Map obj=new LinkedHashMap();
			obj.put("Device", deviceArrayList.get(i)[0]);
			obj.put("Type", deviceArrayList.get(i)[1]);

			
            if (deviceArrayList.get(i).length>3)
                for (int j=3; j<deviceArrayList.get(i).length; j++)
                {
                	System.out.println("Service nr." +(j-3) +": "+ deviceArrayList.get(i)[j]);
                	obj.put(("Service "+(j-3)), deviceArrayList.get(i)[j]);
                }
            
			String jsonText = JSONValue.toJSONString(obj);
			
			if (sqlite.getUPNPDevice(deviceArrayList.get(i)[2]).equals("NULL"))
			{
				sqlite.addUPNPDevice(deviceArrayList.get(i)[2], jsonText);
			}			
		}
	}
	
	private String getLocalIP () { 
	 	String localIP = null;
		NetworkInterface ni = null;
		try {
			ni = NetworkInterface.getByName("eth0");
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Local IP address can not be resolved");
		}

		Enumeration<InetAddress> inetAddresses = ni.getInetAddresses();
		while (inetAddresses.hasMoreElements()) {
			InetAddress ia = inetAddresses.nextElement();
			if (!ia.isLinkLocalAddress()) {
				localIP = ia.getHostAddress();
				System.out.println("Local IP address is: " + localIP);// this is not loopback
			}
		}
		return localIP;		
	}

	@Override
	public void handleEvent(Event event) {
		
		
		System.out.println("{EVENT Handler}:Incoming event:");
		
		TimeZone.setDefault(TimeZone.getTimeZone("CET"));
		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy | HH:mm");
		SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

		System.out.println("{EVENT Handler}:Date: " + df.format(date));	
		
		Runnable startClock = new Runnable(){
		    public void run(){
		    	try {
					Thread.currentThread().sleep(300000);
			    	generated = false;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		};
		
		Runnable startClock_ = new Runnable(){
		    public void run(){
		    	try {
					Thread.currentThread().sleep(300000);
			    	warnning = false;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		};
		Runnable startSlideshow = new Runnable(){
		    public void run(){
				System.out.println("{EVENT Handler}: Sending picture to UDN: " + UDN);					
				String[] uris = {"http://192.168.2.1:8200/MediaItems/19.jpg","http://192.168.2.1:8200/MediaItems/23.jpg","http://192.168.2.1:8200/MediaItems/24.jpg", "http://192.168.2.1:8200/MediaItems/25.jpg","http://192.168.2.1:8200/MediaItems/26.jpg"};
				try {
					push.playSlideshow(UDN, uris, 15000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					System.out.println("{EVENT Handler}: Slideshow over!");	
				}
		    }
		};
		//Upnp Event
		if (event.getProperty("UpnpEvent") != null) {
			System.out.println("{EVENT Handler}:Event Type: "+event.getProperty("UpnpEvent"));	
			System.out.println("{EVENT Handler}:Device Name: "+event.getProperty("Name"));	
			System.out.println("{EVENT Handler}:Device Type: "+event.getProperty("Type"));	
			System.out.println("{EVENT Handler}:Device UDN: "+event.getProperty("UDN"));	
			
			syncDeviceList();			
			
			if (event.getProperty("UpnpEvent").equals("Device_Added")) {
				if (event.getProperty("Type").equals("MediaRenderer")) {
					System.out.println("{EVENT Handler}:Discovery found MediaRenderer with UDN: "+event.getProperty("UDN"));	
					UDN = (String)event.getProperty("UDN");
					if (!generated) 
					{						
						generated = true;
						Thread thread = new Thread(startClock);
						thread.start();
						
						System.out.println("{EVENT Handler}:TVScreenGenerator starting...");
						slideGenerator.initialize(df.format(date));
						
						double waterTodayWarm = 0;// 220;
						double waterTodayCold = 0;// 450;
						if (!sqlite.todayValue("water", bonega, "warm").equals("NULL") && !sqlite.todayValue("water", bonega, "cold").equals("NULL") && bonega!=null) {
							waterTodayWarm = (Double.valueOf(sqlite.actualValue(bonega, "warm")) - Double.valueOf(sqlite.todayValue("water", bonega, "warm")));
							waterTodayCold = (Double.valueOf(sqlite.actualValue(bonega, "cold")) - Double.valueOf(sqlite.todayValue("water", bonega, "cold")));
						}
						double waterToday = waterTodayWarm + waterTodayCold;
						
						double waterYesterdayWarm = 0;// 210;
						double waterYesterdayCold = 0;// 430;
						if (!sqlite.yesterdayLastValue("water", bonega, "warm").equals("NULL") && !sqlite.yesterdayValue("water", bonega, "warm").equals("NULL") && bonega!=null) {
							waterYesterdayWarm = (Double.valueOf(sqlite.yesterdayLastValue("water", bonega, "warm")) - Double.valueOf(sqlite.yesterdayValue("water", bonega, "warm")));				
						}
						if (!sqlite.yesterdayLastValue("water", bonega, "warm").equals("NULL") && !sqlite.yesterdayValue("water", bonega, "warm").equals("NULL") && bonega!=null) {
							waterYesterdayCold = (Double.valueOf(sqlite.yesterdayLastValue("water", bonega, "cold")) - Double.valueOf(sqlite.yesterdayValue("water", bonega, "cold")));
						}
					
						double waterYesterday = waterYesterdayWarm + waterYesterdayCold;
						double waterAverageSevenDays = 0;// 650;
						if (!sqlite.avgValueWE("water", bonega, "1W", "cold").equals("NULL") && !sqlite.avgValueWE("water", bonega, "1W", "warm").equals("NULL") && bonega!=null) {
							waterAverageSevenDays = (Double.valueOf(sqlite.avgValueWE("water", bonega, "1W", "cold")) + Double.valueOf(sqlite.avgValueWE("water", bonega,"1W", "warm")));
						}

						boolean isChecked = true;
						double temperature = 0;
						double humidity = 0;
							temperature = Double.valueOf(sqlite.actualValue(weptech, "temp"));
							humidity = Double.valueOf(sqlite.actualValue(weptech, "hum"));

						double waterAvarage = 0;// 670;
						if (!sqlite.avgValueWE("water", bonega, "1W", "cold").equals("NULL") && !sqlite.avgValueWE("water", bonega, "1W", "warm").equals("NULL") && bonega!=null) {
							waterAvarage = Double.valueOf(sqlite.avgValueWE("water", bonega, "1W", "cold")) + Double.valueOf(sqlite.avgValueWE("water", bonega, "1W", "warm"));
						}
						boolean isMoreThanAverage = waterToday > waterAvarage;

						
						boolean isElectricityBalancePlus = true;
						double electricity = 0;// 480;
						if (!sqlite.todayValue("electricity_cons", pikketron, "T1").equals("NULL")) {
							electricity = (Double.valueOf(sqlite.actualValue(pikketron, "T1")) - Double.valueOf(sqlite.todayValue("electricity_cons", pikketron, "T1")));
						}
						double electrityBalanceEur = electricity*0.25;

						
						boolean isArmed = true;	
						double minTemperature = 0;
						double maxTemperature = 0;
						double minHumidity = 0;
						double maxHumidity = 0;
						if (weptech!=null) {
							minTemperature = Double.valueOf(sqlite.minValue("temp_hum", weptech, "1D", "temp"));
							maxTemperature = Double.valueOf(sqlite.maxValue("temp_hum", weptech, "1D", "temp"));
							minHumidity = Double.valueOf(sqlite.minValue("temp_hum", weptech, "1D", "hum"));
							maxHumidity = Double.valueOf(sqlite.maxValue("temp_hum", weptech, "1D", "hum"));							
						}

		
						double consumption = 0;// 480;
						if (!sqlite.todayValue("electricity_cons", pikketron, "T1").equals("NULL")) {
							consumption = (Double.valueOf(sqlite.actualValue(pikketron, "T1")) - Double.valueOf(sqlite.todayValue("electricity_cons", pikketron, "T1")));;
						}
						double production = consumption*0.5;//0;
						/*if (!sqlite.todayValue("electricity_prod", "1234", "Prod").equals("NULL")) {
							production = (Double.valueOf(sqlite.actualValue("1234", "Prod")) - Double.valueOf(sqlite.todayValue("electricity_prod", "1234", "Prod")));;
						}*/
						double difference = consumption - production;												
						double eurConsumption = difference*0.005;
						
						boolean isMessage = false;
						String alertDate = df.format(date);
						String alertMessage = "";
						
						slideGenerator.setFirstSlide(isChecked, temperature, humidity, waterToday, isMoreThanAverage, isElectricityBalancePlus, electricity, electrityBalanceEur, isArmed);
						thread = new Thread(startSlideshow);
						thread.start();
						slideGenerator.setSecondSlide(temperature, minTemperature, maxTemperature, humidity, minHumidity, maxHumidity);
						slideGenerator.setThirdSlide(consumption, production, difference, eurConsumption);
						slideGenerator.setFourthSlide((int)waterToday, (int)waterYesterday, (int)waterAverageSevenDays);
						slideGenerator.setFifthSlide(isArmed, isMessage, alertDate, alertMessage);
					}
					else {
						Thread thread = new Thread(startSlideshow);
						thread.start();
					}
					

				}			
			}			
		}
		
		//SIP Event
		if (event.getProperty("SIP-incomming-message") != null) {
			System.out.println("{EVENT Handler}:SIP Message: "+event.getProperty("SIP-incomming-message"));
		}
		
		//WMBus Event
		if (event.getProperty("WMBus-incomming-packet") != null) {
			
			System.out.println("{EVENT Handler}:WMBus packet from: "+event.getProperty("WMBus-incomming-packet"));
			
			String timestamp = dfs.format(date);	
			timestamp = timestamp + "+01:00";
			
			if (event.getProperty("WMBus-incomming-packet").equals("WEPTECH")) {
				System.out.println("{EVENT Handler}:TEMP: "+event.getProperty("TEMP"));
				System.out.println("{EVENT Handler}:HUM: "+event.getProperty("HUM"));	
				weptech = (String)event.getProperty("SERIAL");
				sqlite.storeData("temp_hum", weptech, (String)event.getProperty("TEMP"), "temp");
				sqlite.storeData("temp_hum", weptech, (String)event.getProperty("HUM"), "hum");
				
				/*double value = Double.parseDouble((String)event.getProperty("TEMP"));
				if (value > 30) {
					if (!warnning) {
						warnning = true;
						Thread thread = new Thread(startClock_);
						thread.start();
						String message = "Temperature in the kitchen is too high! (" + (String)event.getProperty("TEMP") + ")";
						//sipInterface.sendMessage("sip:tag.smartcenter@a1.net", message);						
					}	
				}
				value = Double.parseDouble((String)event.getProperty("HUM"));
				if (value > 50) {
					if (!warnning) {
						warnning = true;
						Thread thread = new Thread(startClock_);
						thread.start();
						System.out.println("{EVENT Handler}: Pushing picture to UDN: " + UDN);					
						String uri = "http://192.168.2.1:8200/MediaItems/19.jpg";
						try {
							push.pushPicture(UDN, uri, 15000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally {
							System.out.println("{EVENT Handler}: Push over!");	
						}							
					}	
				}*/
				
				System.out.println("{EVENT Handler}: Creating JSON..");	
				
				Map json=new LinkedHashMap();
				Map obj1=new LinkedHashMap();
				Map obj2=new LinkedHashMap();
				Map loc=new LinkedHashMap();
				JSONArray list = new JSONArray();
				
				obj1.put("Description", "Ambient Temperature");
				obj1.put("System_Obis_Code", "0-1:96.9.0");
				obj1.put("Units", "°C");
				obj1.put("Value", (String)event.getProperty("TEMP"));				
				
				obj2.put("Description", "Relative Humidity");
				obj2.put("System_Obis_Code", "0-1:96.9.2");
				obj2.put("Units", "%");
				obj2.put("Value", (String)event.getProperty("HUM"));	
				
				loc.put("Latitude", new Double(49.48));
				loc.put("Longitude", new Double(17.19));
				loc.put("Level", new Integer(5));
				
				list.add(obj1);
				list.add(obj2);								
				
				json.put("System_Current_Time", timestamp);
				json.put("Device_Type", "Temperature_Humidity");
				json.put("Message", "Warning - Relative humidity too high: 75%");
				json.put("Device_Serial_Number", Integer.parseInt((String)event.getProperty("SERIAL")));
				json.put("Device_Location",loc);
				json.put("Values",list);
				
				String jsonText = JSONValue.toJSONString(json);
				
				System.out.println("{EVENT Handler}:WEBTECH JSON FORMAT: " + jsonText);
				
				sipInterface.sendMessage("sip:pavel@23.97.214.3", jsonText);
				
				//sqlite.storeData("electricity_prod", "1234", "480", "Prod");
				obj1.clear();			
				obj1.put("Description", "Meter reading total (A-)");
				obj1.put("System_Obis_Code", "1-1:2.8.0");
				obj1.put("Units", "kWh");
				obj1.put("Value", "12.7");
								
				list.clear();
				list.add(obj1);
				//list.add(obj2);								
				
				json.put("System_Current_Time", timestamp);
				json.put("Device_Type", "Electricity_Production");
				json.put("Message", "Actual self supply: 25%");
				json.put("Device_Serial_Number", 7125244);
				json.put("Device_Location",loc);
				json.put("Values",list);
				
				jsonText = JSONValue.toJSONString(json);
				
				System.out.println("{EVENT Handler}:ELECTRICITY PROD JSON FORMAT: " + jsonText);

				sipInterface.sendMessage("sip:pavel@23.97.214.3", jsonText);
				
				
			}
			
			if (event.getProperty("WMBus-incomming-packet").equals("PIKKERTON")) {
				System.out.println("{EVENT Handler}:CONSUMPTION: "+event.getProperty("WORK"));
				pikketron = (String)event.getProperty("SERIAL");
				sqlite.storeData("electricity_cons", pikketron, (String)event.getProperty("WORK"), "T1");
				
				Map json=new LinkedHashMap();
				Map obj1=new LinkedHashMap();
				Map obj2=new LinkedHashMap();
				Map loc=new LinkedHashMap();
				JSONArray list = new JSONArray();
				
				obj1.put("Description", "Meter reading total (A+)");
				obj1.put("System_Obis_Code", "1-1:1.8.0");
				obj1.put("Units", "kWh");
				obj1.put("Value", (String)event.getProperty("WORK"));
				
				obj2.put("Description", "Load profile 15' total (A+)");
				obj2.put("System_Obis_Code", "1-1:1.29.0");
				obj2.put("Units", "kWh");
				obj2.put("Value", "6.5");	
				
				list.clear();
				list.add(obj1);
				list.add(obj2);								
				
				loc.put("lat", new Double(49.48));
				loc.put("lng", new Double(17.19));
				
				json.put("System_Current_Time", timestamp);
				json.put("Device_Type", "Electricity_Consumption");
				json.put("Message", "Actual self supply: 25%");
				json.put("Device_Serial_Number", 12679344);
				json.put("Device_Location",loc);
				json.put("Values",list);				
				
				String jsonText = JSONValue.toJSONString(json);
				
				System.out.println("{EVENT Handler}:ELECTRICITY CONS JSON FORMAT: " + jsonText);
				sipInterface.sendMessage("sip:pavel@23.97.214.3", jsonText);
				
			}
			
			
			if (event.getProperty("WMBus-incomming-packet").equals("BONEGA")) {
				
				Integer value = Integer.valueOf((String) event.getProperty("VALUE"));
				String strValue = String.valueOf(value);
					if (event.getProperty("TYPE").equals("WARM")) {
						if (value > warm && value < 50000.0)
						{
							warm = value;
							System.out.println("{EVENT Handler}:WARM: "+event.getProperty("VALUE"));
							sqlite.storeData("water", bonega, strValue, "warm");
							
							Map json=new LinkedHashMap();
							Map obj1=new LinkedHashMap();
							Map loc=new LinkedHashMap();
							JSONArray list = new JSONArray();
												
							obj1.put("Description", "Water hot, meter reading, total");
							obj1.put("System_Obis_Code", "9-1:1.0.0");
							obj1.put("Units", "l");
							obj1.put("Value", strValue);				
							
							loc.put("Latitude", new Double(49.48));
							loc.put("Longitude", new Double(17.19));
							loc.put("Level", new Integer(5));
							
							list.add(obj1);							
							
							json.put("System_Current_Time", timestamp);
							json.put("Device_Type", "Water");
							json.put("Message", "Warning - Unexpected water consumption: 1000 liters last hour!");
							json.put("Device_Serial_Number", 121);
							json.put("Device_Location",loc);
							json.put("Values",list);
							String jsonText = JSONValue.toJSONString(json);
							
							System.out.println("{EVENT Handler}:WATER HOT JSON FORMAT: " + jsonText);
		
							sipInterface.sendMessage("sip:pavel@23.97.214.3", jsonText);
						}
						
					}
					if (event.getProperty("TYPE").equals("COLD")) {
						if (value > cold && value < 50000.0)
						{
							cold = value;
							System.out.println("{EVENT Handler}:COLD: "+event.getProperty("VALUE"));
							sqlite.storeData("water", bonega, strValue, "cold");
							
							Map json=new LinkedHashMap();
							Map obj1=new LinkedHashMap();
							Map loc=new LinkedHashMap();
							JSONArray list = new JSONArray();
							
							obj1.put("Description", "Water cold, meter reading, total");
							obj1.put("System_Obis_Code", "8-1:1.0.0");
							obj1.put("Units", "l");
							obj1.put("Value", strValue);				
							
							loc.put("Latitude", new Double(49.48));
							loc.put("Longitude", new Double(17.19));
							loc.put("Level", new Integer(5));
							
							list.add(obj1);							
							
							json.put("System_Current_Time", timestamp);
							json.put("Device_Type", "Water");
							json.put("Message", "Warning - Unexpected water consumption: 700 liters last hour!");
							json.put("Device_Serial_Number", 121);
							json.put("Device_Location",loc);
							json.put("Values",list);
							
							String jsonText = JSONValue.toJSONString(json);
							
							System.out.println("{EVENT Handler}:WATER COLD JSON FORMAT: " + jsonText);
							
							sipInterface.sendMessage("sip:pavel@23.97.214.3", jsonText);
						}
						
					}				
			}
		}
	}
}
