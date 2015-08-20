package wmbusmessages;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import WMBusBundle.GwUtils;

public class WeptechTemperatureMeter implements IWmbus {

	public static final int WEPTECH_TEMP_INDEX		= 18;
	public static final int WEPTECH_TEMP_ID 		= 0x66;
	public static final int WEPTECH_TEMP_HIGH_INDEX = 19;
	public static final int WEPTECH_TEMP_LOW_INDEX  = 20;
	public static final int WEPTECH_HUM_HIGH_INDEX  = 24;
	public static final int WEPTECH_HUM_LOW_INDEX   = 25; 
	
	
	// Hex body of received packet
	private String body;
	private byte[] bodyArray;
	
	WeptechTemperatureMeter(String packet) {
		this.body 	   = new String(packet);
		this.bodyArray = GwUtils.hexStringToByteArray(this.body); 
	}	
	
	public String getManufacturerString() {
		return WEPTECH_MANUFACTURER_STRING;
	}
	
	public int getSerialNumber() {
		byte[] temp = Arrays.copyOfRange(bodyArray,PACKET_SERIAL_NUM_INDEX, PACKET_SERIAL_NUM_INDEX+4);			
		ByteBuffer wrapped = ByteBuffer.wrap(temp); 
		wrapped.order(ByteOrder.LITTLE_ENDIAN); // big-endian by default
		return wrapped.getInt(); // 4B SN
	}
	
	public boolean isValid() {
		if((bodyArray[PACKET_TYPE_INDEX] == PACKET_SNDNR_VALUE) 
		&& (bodyArray.length == bodyArray[PACKET_LEN_INDEX]+1)
		&& (bodyArray[WEPTECH_TEMP_INDEX] == WEPTECH_TEMP_ID))
			return true;
		else
			return false;
	}
	
	public int getDeviceType() {
		return bodyArray[PACKET_DEVICE_TYPE_INDEX];
	}	
	
	public List<String> getPhenomenaList() 
	{
		  List<String> measuredList = new LinkedList<String>();
		  Double temperature = null;
		  Double humidity = null;
	      
		  // make string from hex values, 0x86 0x02 -> 0286
	      String tempString = String.format("%02X%02X", (bodyArray[WEPTECH_TEMP_LOW_INDEX]&0xFF), (bodyArray[WEPTECH_TEMP_HIGH_INDEX]&0xFF));
	      // return divided by 10 -> 28.6
	      temperature = Double.valueOf(tempString) / 10.0;
	      
	      if(temperature!=null)
	      {
	    	  measuredList.add(String.valueOf(temperature));
	    	  //measuredList.add("TEMPERATURE:"+String.valueOf(temperature));
	      }
	      
	      
	      String humString = String.format("%02X%02X", (bodyArray[WEPTECH_HUM_LOW_INDEX]&0xFF), (bodyArray[WEPTECH_HUM_HIGH_INDEX]&0xFF));
	      humidity = Double.valueOf(humString) / 10.0;
	      
	      if(humidity!=null)
	      {
	    	  measuredList.add(String.valueOf(humidity));
	    	  //measuredList.add("HUMIDITY:"+String.valueOf(humidity));
	      }
	      
	      return measuredList;
	}
}
