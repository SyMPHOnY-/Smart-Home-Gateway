package cz.vutbr.wislab.symphony.converter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import cz.vutbr.wislab.symphony.converter.JsonParser;


public class JsonConverter {

	private HashMap<String, ArrayList<String>> outputData = new HashMap<>();

	//---------------------------------------------------------------------//
	// Method for sending given files over SFTP      					   //
	// Hostname, username and password are hardcoded into this method      //
	//---------------------------------------------------------------------//
	public  void sendToSFTP () {
        String SFTPHOST = "sftp.energieburgenland.at";
        int SFTPPORT = 22;
        String SFTPUSER = "ftpkunde";
        String SFTPPASS = "tramsrotinom00#";
        //String SFTPWORKINGDIR = "MeterData";

        Session session = null;
        Channel channel = null;
        ChannelSftp channelSftp = null;
        System.out.println("preparing the host information for sftp.");
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);
            session.setPassword(SFTPPASS);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            System.out.println(session.isConnected());
            System.out.println("Host connected.");
            channel = session.openChannel("sftp");
            channel.connect();
            System.out.println("sftp channel opened and connected.");
            channelSftp = (ChannelSftp) channel;
            //channelSftp.cd(SFTPWORKINGDIR);
            File folder = new File(".");
    		File[] listOfFiles = folder.listFiles();
    		    for (int i = 0; i < listOfFiles.length; i++) {
    		      if (listOfFiles[i].getName().contains(".csv")) {
    		    	  channelSftp.put(new FileInputStream(listOfFiles[i]), listOfFiles[i].getName());
    		          System.out.println("File: " + listOfFiles[i].getName() + " sent.");
    		      }
    		      else if(listOfFiles[i].getName().contains(".txt")) {
    		    	  channelSftp.put(new FileInputStream(listOfFiles[i]), listOfFiles[i].getName());
    		          System.out.println("File: " + listOfFiles[i].getName() + " sent.");
    		      }
    		    }
            System.out.println("File transfered successfully to host.");
        } catch (Exception ex) {
             System.out.println("Exception found while tranfer the response.");
        }
        finally{

            channelSftp.exit();
            System.out.println("sftp Channel exited.");
            channel.disconnect();
            System.out.println("Channel disconnected.");
            session.disconnect();
            System.out.println("Host Session disconnected.");
        }
    } 
	
	//---------------------------------------------------------------------//
	// Method for decomposing given JSON packet, composing and             //
	// saving new LPEX and CSV files					   				   //
	//---------------------------------------------------------------------//
	public void recomposeAndSave(String message) {
	
			    final ObjectMapper mapper = new ObjectMapper();			      
			    try {
			    	JsonParser parser = new JsonParser();
					JsonNode node = mapper.readTree(message);
					System.out.println("Node content: " + node);
					outputData = parser.parse(node);
					System.out.println(outputData);
					convertAndSave();
				} catch (JsonMappingException e) {
					e.printStackTrace();
				} catch (JsonParseException ex) {
					ex.printStackTrace();
				} catch (IOException eIo){
					eIo.printStackTrace();
				}			
	}
	
	//---------------------------------------------------------------------//
	// Method for writing given String to a given File                     //
	// with default encoding					                           //
	//---------------------------------------------------------------------//
	public void writeToFileDefault(String fileName, String toWrite){
        try {
            // Assume default encoding.
            FileWriter fileWriter =
                new FileWriter(fileName);

            BufferedWriter bufferedWriter =
                new BufferedWriter(fileWriter);            
            
            System.out.println("File: "+fileName+ " toWrite: "+ toWrite);
            bufferedWriter.write(toWrite);
            bufferedWriter.close();
        }
        catch(IOException ex) {
            System.out.println(
                "Error writing to file '"
                + fileName + "'");

        }
	}
	
	//---------------------------------------------------------------------//
	// Method for writing given String to a given File                     //
	// with UTF-8 encoding					                               //
	//---------------------------------------------------------------------//
	public void writeToFileUTF8(String fileName, String toWrite){
		Writer out;
		try {
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8"));
			try {
				out.write(toWrite);
				out.close();
			} catch (IOException e) {
				System.out.println(
		                "Error writing to file '"
		                + fileName + "'");
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
		
	//---------------------------------------------------------------------//
	// Method for finding and deleting device id according to the          //
	// given string														   //
	//---------------------------------------------------------------------//
	public String findAndDeleteByDeviceID(String deviceId) {
		
		String returnedDeviceID = "";
		File folder = new File(".");
		File[] listOfFiles = folder.listFiles();			
		    for (int i = 0; i < listOfFiles.length; i++) {
		      if (listOfFiles[i].getName().contains(deviceId)) {
		        returnedDeviceID = listOfFiles[i].getName();
		        File toDelete = new File(returnedDeviceID);
		        toDelete.delete();
		        System.out.println("File: " + returnedDeviceID + " deleted.");
		      }
		    }
		return returnedDeviceID;
	}
	
		//---------------------------------------------------------------------//
		// Method for writing given String to a given File					   //
		//---------------------------------------------------------------------//
		
		public String[] getTimeAndDate (String timeAndDate){
			String[] rightTimeAndDate = timeAndDate.split("T|\\+");
			String wrong = rightTimeAndDate[0];
			String[] repair = wrong.split("-");
			String[] readyTimeAndDate = new String[4];
			readyTimeAndDate[0] = (repair[2] + "." + repair[1] + "." + repair[0]); 
			readyTimeAndDate[1] = (repair[0] + "-" + repair[1] + "-" + repair[2]); 
			wrong = rightTimeAndDate[1];
			repair = wrong.split(":");
			readyTimeAndDate[2] = rightTimeAndDate[1];
			readyTimeAndDate[3] = (repair[0] + "." + repair[1] + "." + repair[2]);
			/*System.out.println(readyTimeAndDate[0]);
			System.out.println(readyTimeAndDate[1]);
			System.out.println(readyTimeAndDate[2]);
			System.out.println(readyTimeAndDate[3]);*/
			
			return readyTimeAndDate;
		}
		

	
		//---------------------------------------------------------------------//
		// Method for converting parsed JSON to CSV and LPEX				   //
		//---------------------------------------------------------------------//
		public void convertAndSave(){
			
			// ----------------------------------------------//
			// COMMON FOR ALL								 //
			//-----------------------------------------------//
			String deviceId = "EB0000000000000000000000000000001";
			String currentUnit[] = new String[10];
			String currentValue[] = new String[10];
			String systemObisCode[] = new String[10];
			String currentTimeDot;
			String currentTimeColon;
			String currentDateDash;
			String currentDateDot;

			
			// ----------------------------------------------//
			// FOR LPEX										 //
			//-----------------------------------------------//
			String lpexMsg;
			String customerNumber = "";
			String customerName = "";
			String uniqueKDNr = "";
			String gEid = "";
			String gEKANr = "";
			String kALINr = "";
			String line = "";
			String uniqueLINr = "";
			//String zPB = "AT0090000000000000000000000025029";
			//String classificationNumber = "1-1:1.9.1 P01";
			String transformerFactor = "1";
			String mPduration = "15";
			String noName = "00000";
			String lpexText;
			
			
			// ----------------------------------------------//
			// FOR CSV										 //
			//-----------------------------------------------//
			String csvMsg;
			//String channel = "1-1:1.8.0";
			String timestamp;
			String csvText;
			String readingType = "METER_READING";
			System.out.println(outputData.get("value"));
			System.out.println(outputData.get("name").indexOf("Current Time"));
			currentDateDash = getTimeAndDate(outputData.get("value").get(outputData.get("name").indexOf("Current Time")))[1];
			currentDateDot = getTimeAndDate(outputData.get("value").get(outputData.get("name").indexOf("Current Time")))[0];
			currentTimeColon = getTimeAndDate(outputData.get("value").get(outputData.get("name").indexOf("Current Time")))[2];
			currentTimeDot = getTimeAndDate(outputData.get("value").get(outputData.get("name").indexOf("Current Time")))[3];
			timestamp = currentTimeDot + "-" + currentDateDot;
			//deviceId = outputData.get("value").get(outputData.get("name").indexOf("Metering Point ID"));
			
			int obisI = outputData.get("systemCode").size();
			for (int i = 0; i < obisI; i++) {
				systemObisCode[i] = (outputData.get("systemCode").get(i));
			}
			
			//int positionInValue = (outputData.get("value").size())-(outputData.get("units").size());
			int positionInValue = outputData.get("name").indexOf("Consumption last 15 minutes");
			int j = outputData.get("units").size();
			for (int i = 0; i < j; i++) {
				currentUnit[i] = (outputData.get("units").get(i));
				currentValue[i] = (outputData.get("value")).get(positionInValue + i);
			}

				int positionInObjectCode = (outputData.get("systemCode").size())-(outputData.get("units").size());
				int dataLength = outputData.get("units").size();
				//LPEX
				lpexMsg = "";
				lpexText = "LPEX V2.0 \r\nDatum;Zeit;Kundennummer;Kundenname;eindeutigeKDNr;GEId;GEKANr;KALINr;Linie;eindeutigeLINr;ZPB;Kennzahl;Einheit;Wandlerfaktor;MPDauer;Werte; ";
				for (int i = 0; i < dataLength; i++) {
					lpexText = lpexText.concat("\r\n" + currentDateDot + ";" + currentTimeColon + ";" + customerNumber + ";" + customerName + ";" + uniqueKDNr + ";" + gEid + ";" + gEKANr + ";" + kALINr + ";" + line + ";" + uniqueLINr + ";" + deviceId + ";" + "1-0:1.29.0"/*systemObisCode[positionInObjectCode + i]*/ + ";" + currentUnit[i] + ";" + transformerFactor + ";" + mPduration + ";" + currentValue[i] + ";" + noName);
				}
				lpexMsg = lpexMsg.concat(lpexText);
				System.out.println("LpexMsg: " + lpexMsg);
				
				//CSV
				csvText = "MeteringPointId;Channel;Unit;Value;Timestamp;ReadingType";
				for (int i = 0; i < dataLength; i++) {
					csvText = csvText.concat("\r\n" + deviceId + ";" + "1-0:1.29.0"/*systemObisCode[positionInObjectCode + i]*/ + ";" + currentUnit[i] + ";" + currentValue[i] + ";" + currentDateDash + " " + currentTimeColon + ";" + readingType);
				}
				csvMsg = csvText;		
			
				System.out.println("ValuePosition: " + positionInValue + " ,PositionInObjectCode: " + positionInObjectCode);
				System.out.println("CSV MESSAGE:" + csvMsg);
				findAndDeleteByDeviceID(deviceId);
				writeToFileUTF8(deviceId + "_" + timestamp + ".csv", csvMsg);
				writeToFileUTF8(deviceId + "_" + timestamp + ".txt", lpexMsg);
				//writeToFileDefault(deviceId + "_" + timestamp + ".csv", csvMsg);
				//writeToFileDefault(deviceId + "_" + timestamp + ".txt", lpexMsg);
}				
}
