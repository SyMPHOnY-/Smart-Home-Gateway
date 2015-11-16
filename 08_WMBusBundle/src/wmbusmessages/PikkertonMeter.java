package wmbusmessages;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import WMBusBundle.GwUtils;

public class PikkertonMeter implements IWmbus{

	public static final int PIKKERTON_WORK_INDEX = 35;
	public static final int PIKKERTON_WORK_DIF           = 0x04;
	public static final int PIKKERTON_WORK_LOWEST_INDEX  = 40;
	public static final int PIKKERTON_WORK_LOW_INDEX     = 39;
	public static final int PIKKERTON_WORK_HIGH_INDEX    = 38;
	public static final int PIKKERTON_WORK_HIGHEST_INDEX = 37;
	
	// Hex body of received packet
	private String body;
	private byte[] bodyArray;
	
	PikkertonMeter(String packet) {
		this.body 	   = new String(packet);
		this.bodyArray = GwUtils.hexStringToByteArray(this.body); 
	}
	
	public String getManufacturerString() {
		return PIKKERTON_MANUFACTURER_STRING;
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
		&& (bodyArray[PIKKERTON_WORK_INDEX] == PIKKERTON_WORK_DIF))
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
		  Double work = null;
	      
	      String tempString = String.format("%02X%02X%02X%02X", (bodyArray[PIKKERTON_WORK_LOWEST_INDEX]&0xFF), (bodyArray[PIKKERTON_WORK_LOW_INDEX]&0xFF), (bodyArray[PIKKERTON_WORK_HIGH_INDEX]&0xFF), (bodyArray[PIKKERTON_WORK_HIGHEST_INDEX]&0xFF));
	      work = Long.valueOf(tempString, 16).doubleValue()/1000.0;
	      
	      if(work!=null)
	      {
	    	  measuredList.add(String.valueOf(work));
	      }	      
	      return measuredList;
	}
	
}
