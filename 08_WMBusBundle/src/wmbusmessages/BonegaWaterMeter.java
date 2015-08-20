package wmbusmessages;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import WMBusBundle.GwUtils;

public class BonegaWaterMeter implements IWmbus {

	// Local Constants
	static final public int ACCESS_NUMBER_INDEX  		= 11;  
	static final public int CIPHER_PAYLOD_START_INDEX  	= 15;  
	static final public int CIPHER_PAYLOD_END_INDEX  	= 30;  

	static final public int DECRYPTED_PAYLOAD_START_VALUE_INDEX  	= 4;
	static final public int DECRYPTED_PAYLOAD_END_VALUE_INDEX		= 7; 
	
	public String implicitAESKey	   = "2B7E151628AED2A6ABF7158809CF4F3C";
	
	// Hex body of received packet
	private String body;
	private byte[] bodyArray;
	
	// AES related arrays
	private	byte [] cipherBytes 	= null; 
	private byte [] iv				= null;
	private byte [] keyBytes 		= GwUtils.hexStringToByteArray(implicitAESKey);
	private byte [] decryptedBytes  = null;
	
	// value of water meter
	private int value;
		
	BonegaWaterMeter(String packet) {
		this.body 	   = new String(packet);
		this.bodyArray = GwUtils.hexStringToByteArray(this.body); 
		
		this.extractAesInitVector();
		this.extractCipherPayload();
		this.decryptData();
	}
	
	public String getManufacturerString() {
		return BONEGA_MANUFACTURER_STRING;
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
		&& (bodyArray[PACKET_DEVICE_TYPE_INDEX] == DEVICE_TYPE_WARM_WATER || bodyArray[PACKET_DEVICE_TYPE_INDEX] == DEVICE_TYPE_COLD_WATER))
			return true;
		else
			return false;
	}
	
	public int getDeviceType() {
		return bodyArray[PACKET_DEVICE_TYPE_INDEX];
	}
	
	public List<String> getPhenomenaList() {
		
		if(this.isValid())
		{
			List<String> measuredList = new LinkedList<String>();
			if(this.getDeviceType() == DEVICE_TYPE_WARM_WATER)
			{
				measuredList.add("WARM");
				measuredList.add(String.valueOf(this.value));
			}
			else if(this.getDeviceType() == DEVICE_TYPE_COLD_WATER)
			{
				measuredList.add("COLD");
				measuredList.add(String.valueOf(this.value));
			}
			return measuredList;
		}
		else
			return null;
	}
	
	public String getAesKey()
	{
		return GwUtils.byteArrayToHexString(this.keyBytes);
	}
	
	public void setAesKey(String key)
	{
		this.keyBytes = GwUtils.hexStringToByteArray(key);
	}

	private void extractAesInitVector()
	{
		// AES IV = Manufacturer + Identification + 8x Access Number
		byte [] subArray = Arrays.copyOfRange(bodyArray,WmbusPacket.PACKET_MANUFACTURER_INDEX , WmbusPacket.PACKET_DEVICE_TYPE_INDEX+1);
		byte [] subArray1 = new byte[8];
		
		for(int i=0;i<8;i++)
			subArray1[i] = bodyArray[ACCESS_NUMBER_INDEX];
		
		this.iv = new byte[subArray.length + subArray1.length];
		System.arraycopy(subArray, 0, this.iv, 0, subArray.length);
		System.arraycopy(subArray1, 0, this.iv, subArray.length, subArray1.length);		
	}
	
	private void extractCipherPayload()
	{
		this.cipherBytes = Arrays.copyOfRange(bodyArray, CIPHER_PAYLOD_START_INDEX, CIPHER_PAYLOD_END_INDEX+1);
	}
	
	
	private void decryptData()
	{ 	    
 	    SecretKey aesKey = new SecretKeySpec(keyBytes, "AES");
 	    Cipher cipher;
 	    
		try {
			
			cipher = Cipher.getInstance("AES/CBC/NOPADDING");
			cipher.init(Cipher.DECRYPT_MODE, aesKey, new IvParameterSpec(iv));
			decryptedBytes = cipher.doFinal(cipherBytes);
			
			// extract bonega value
			byte[] temp = Arrays.copyOfRange(decryptedBytes,DECRYPTED_PAYLOAD_START_VALUE_INDEX, DECRYPTED_PAYLOAD_END_VALUE_INDEX+1);			
			ByteBuffer wrapped = ByteBuffer.wrap(temp); 
			wrapped.order(ByteOrder.LITTLE_ENDIAN); // big-endian by default
			this.value = wrapped.getInt(); // 4B water meter value
			
			//System.out.println("BONEGA DECRYPTED PAYLOAD: "+ GwUtils.byteArrayToHexString(decryptedBytes));
			//System.out.println("BONEGA VALUE: " + this.value);
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
