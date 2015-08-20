package wmbusmessages;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import WMBusBundle.GwUtils;

/*
 * UDP DATAGRAM STRUCTURE 
 * 
 * (all fields are in ASCII format)
 * 
 * MANUFACTURER;PHENOMENON1_NAME:VALUE1;PHENOMENON2_NAME:VALUE2;PHENOMENON3_NAME:VALUE3;...PHENOMENONn_NAME:VALUEn;HEX_BODY_WMBUS_MESSAGE;
 * 
 */

public class WmbusPacket implements IWmbus {
		
	// instance variables (all in ASCII format)
	private String manufacturer = "UKNOWN";
	private List<String> phenomenaList = new LinkedList<String>();
	private String rawbody;
	private byte[] rawbodyBytes;
	private boolean valid = false;
	
	
	public String getManufacturer() {
		return manufacturer;
	}

	public List<String> getPhenomenaList() {
		return phenomenaList;
	}


	public String getRawbody() {
		return rawbody;
	}

	public String getRawBody()
	{
		return this.rawbody;
	}
	
	public boolean isValid()
	{
		return this.valid;
	}
	
	public String getSerialNumber() {
		  byte[] temp = Arrays.copyOfRange(rawbodyBytes, PACKET_SERIAL_NUM_INDEX, PACKET_SERIAL_NUM_INDEX+4);   
		  ByteBuffer wrapped = ByteBuffer.wrap(temp); 
		  wrapped.order(ByteOrder.LITTLE_ENDIAN); // big-endian by default
		  return String.valueOf(wrapped.getInt()); // 4B SN
	}
	
	public WmbusPacket(String str)
	{	
			String[] parts = str.split(";");
		
			// first string is Manufacturer
			this.manufacturer = new String(parts[0]);
		
			// second and more items are measured values
			for(int i=1; i<parts.length-2;i+=2)
			{
				this.phenomenaList.add(new String(parts[i]+":"+parts[i+1]));
			}
		
			// last one is ASCII hex body
			this.rawbody = new String(parts[parts.length-1]);
		
			// convert to bytes as well
			this.rawbodyBytes = GwUtils.hexStringToByteArray(this.rawbody);
			
			if(this.rawbodyBytes!=null)
				this.valid = true;
			else
				this.valid = false;
	}	
	
	public int getManufacturerId()
	{
		if(isValid())
		{
			byte[] temp = Arrays.copyOfRange(this.rawbodyBytes, PACKET_MANUFACTURER_INDEX, PACKET_MANUFACTURER_INDEX+PACKET_MANUFACTURER_SIZE);			
			ByteBuffer wrapped = ByteBuffer.wrap(temp); 
			wrapped.order(ByteOrder.LITTLE_ENDIAN); // big-endian by default
			return wrapped.getShort(); // manufacturer has 2B
		}
		return -1;
	}
	
	public int getPacketLen()
	{
		if(isValid())
			return this.rawbodyBytes[PACKET_LEN_INDEX];
		else
			return -1;
	}
	
	public int getDeviceType()
	{
		if(isValid())
			return this.rawbodyBytes[PACKET_DEVICE_TYPE_INDEX];
		else
			return -1;
	}
	
	// decode message packet according to list of wmbusmessages
	public void processMeasuredData()
	{
		int manufacturerId=getManufacturerId();
		
		switch(manufacturerId) 
		{
			case BONEGA_MANUFACTURER_ID:
				BonegaWaterMeter bonegaWaterMeter = new BonegaWaterMeter(rawbody);
				if(bonegaWaterMeter.isValid())
				{
					this.manufacturer  = bonegaWaterMeter.getManufacturerString();
					this.phenomenaList = bonegaWaterMeter.getPhenomenaList();	
				}
			break;
			
			case WEPTECH_MANUFACTURER_ID:
				WeptechTemperatureMeter weptechTemperatureMeter = new WeptechTemperatureMeter(rawbody);
				if(weptechTemperatureMeter.isValid())
				{
					this.manufacturer  = weptechTemperatureMeter.getManufacturerString();
					this.phenomenaList = weptechTemperatureMeter.getPhenomenaList();	
				}
			break; 
		}
	}
	
	public void print() 
	{
		System.out.println("MANUFACTURER:"+this.manufacturer);
		
		for(int i=0; i<this.phenomenaList.size();i++)
		{
			System.out.println(this.phenomenaList.get(i));
		}
		
		System.out.println("HEXBODY:"+this.rawbody+"\n");
	}
	
}
