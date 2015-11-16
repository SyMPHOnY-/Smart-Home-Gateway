package wmbusmessages;

public interface IWmbus {
	
	/*
	 * WMBUS Packet definition
	 */
	public static final int PACKET_LEN_INDEX			= 0; // L Field
	public static final int PACKET_TYPE_INDEX			= 1; // C Field
	public static final int PACKET_MANUFACTURER_INDEX   = 2; // M Field
	public static final int PACKET_SERIAL_NUM_INDEX		= 4; // A Field
	public static final int PACKET_DEVICE_TYPE_INDEX	= 9; // A Field
	
	public static final int PACKET_MANUFACTURER_SIZE   = 2; // 2 bytes
	
	public static final int PACKET_SNDNR_VALUE	= 0x44;	
	
	/*
	 * WMBUS Manufacturers IDs
	 */
	public static final int BONEGA_MANUFACTURER_ID  = 0x09EE;
	public static final int WEPTECH_MANUFACTURER_ID = 0x5CB0;
	public static final int PIKKERTON_MANUFACTURER_ID = 0x412B;
	
	/*
	 * WMBUS Manufacturers Strings
	 */
	public static final String BONEGA_MANUFACTURER_STRING   = "BONEGA";
	public static final String WEPTECH_MANUFACTURER_STRING  = "WEPTECH";
	public static final String PIKKERTON_MANUFACTURER_STRING  = "PIKKERTON";
	
	/*
	 * WMBUS Message Lens
	 */
	public static final int BONEGA_MESSAGE_LENGTH  = 0x1E; 
	
	/*
	 * WMBUS Device Type IDs
	 */
	public static final int DEVICE_TYPE_WARM_WATER  = 0x06;  
	public static final int DEVICE_TYPE_COLD_WATER  = 0x07; 
	

}
