package cz.vutbr.wislab.symphony.converter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
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
        String SFTPHOST = "HOSTNAME";
        int SFTPPORT = 22;
        String SFTPUSER = "USERNAME";
        String SFTPPASS = "PASSWORD";
        String SFTPWORKINGDIR = "WORKINGFOLDER";

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
            channelSftp.cd(SFTPWORKINGDIR);
            File folder = new File(".");
    		File[] listOfFiles = folder.listFiles();
    		    for (int i = 0; i < listOfFiles.length; i++) {
    		      if (listOfFiles[i].getName().contains(".csv")) {
    		    	  channelSftp.put(new FileInputStream(listOfFiles[i]), listOfFiles[i].getName());
    		          System.out.println("File: " + listOfFiles[i].getName() + " sent.");
    		      }
    		      else if(listOfFiles[i].getName().contains(".lpex")) {
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
	// Method for writing given String to a given File					   //
	//---------------------------------------------------------------------//
	public void writeToFile(String fileName, String toWrite){
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
			readyTimeAndDate[1] = (repair[2] + "-" + repair[1] + "-" + repair[0]); 
			wrong = rightTimeAndDate[1];
			repair = wrong.split(":");
			readyTimeAndDate[2] = rightTimeAndDate[1];
			readyTimeAndDate[3] = (repair[0] + "." + repair[1] + "." + repair[2]);

			
			return readyTimeAndDate;
		}
		
	
	
	
	
		public void convertAndSave(){
			
			// ----------------------------------------------//
			// COMMON FOR ALL								 //
			//-----------------------------------------------//
			String deviceId = "AT0090000000000000000000000016971";
			String currentUnit[] = new String[5];
			String currentKWh[] = new String[5];
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
			String zPB = "AT0090000000000000000000000025029";
			//String classificationNumber = "1-1:1.9.1 P01";
			String transformerFactor = "";
			String mPduration = "15";
			String noName = "2000";
			String lpexText;
			
			
			// ----------------------------------------------//
			// FOR CSV										 //
			//-----------------------------------------------//
			String csvMsg;
			//String channel = "1-1:1.8.0";
			String timestamp;
			String csvText;
			
	currentDateDash = getTimeAndDate(outputData.get("System_Current_Time").get(0))[1];
	currentDateDot = getTimeAndDate(outputData.get("System_Current_Time").get(0))[0];
	currentTimeColon = getTimeAndDate(outputData.get("System_Current_Time").get(0))[2];
	currentTimeDot = getTimeAndDate(outputData.get("System_Current_Time").get(0))[3];
	timestamp = currentTimeDot + "-" + currentDateDot;
	
	int obisI = outputData.get("System_Obis_Code").size();
	for (int i = 0; i < obisI; i++) {
		systemObisCode[i] = (outputData.get("System_Obis_Code").get(i));
	}
	
	int j = outputData.get("Units").size();
	for (int i = 0; i < j; i++) {
		currentUnit[i] = (outputData.get("Units").get(i));
		currentKWh[i] = (outputData.get("Value")).get(i);
	}
	
	if (outputData.get("Device_Type").contains("Electricity_Consumption")) {
		
		int indexOfMRT = outputData.get("Description").indexOf("Meter reading total (A+)");
		int indexOfLPT = outputData.get("Description").indexOf("Load profile 15"+"'"+" total (A+)");

		//LPEX
		lpexMsg = "";
		lpexText = "LPEX V2.0 \nDatum;Zeit;Kundennummer;Kundenname;eindeutigeKDNr;GEId;GEKANr;KALINr;Linie;eindeutigeLINr;ZPB;Kennzahl";
		for (int i = 0; i < 1; i++) {
			lpexText = lpexText.concat(";Einheit;Wandlerfaktor;MPDauer;Werte; "); 
		}
			lpexText = lpexText.concat("\n" + currentDateDot + ";" + currentTimeColon + ";" + customerNumber + ";" + customerName + ";" + uniqueKDNr + ";" + gEid + ";" + gEKANr + ";" + kALINr + ";" + line + ";" + uniqueLINr + ";" + zPB);
		for (int i = 0; i < 1; i++) {
			lpexText = lpexText.concat(";" + systemObisCode[indexOfMRT] + ";" + currentUnit[indexOfMRT] + ";" + transformerFactor + ";" + mPduration + ";" + currentKWh[indexOfMRT] + ";" + noName); 
		}
		lpexMsg = lpexText;
		
		
	
		//CSV
		csvText = "MeteringPointId;Channel;";
		for (int i = 1; i < 2; i++) {
			csvText = csvText.concat("Unit;Value;"); 
		}
		csvText = csvText.concat("Timestamp \n" + deviceId + ";");
		for (int i = 1; i < 2; i++) {
			csvText = csvText.concat(systemObisCode[indexOfLPT] + ";" + currentUnit[indexOfLPT] + ";" + currentKWh[indexOfLPT] + ";");
		}
		csvMsg = csvText.concat(currentDateDash + " " + currentTimeColon);		
	
		findAndDeleteByDeviceID(deviceId);
		writeToFile(deviceId + "_" + timestamp + ".csv", csvMsg);
		writeToFile(deviceId + "_" + timestamp + ".lpex", lpexMsg);
	} else {
	
		
		
		
		//CSV
		csvText = "MeteringPointId;Channel;";
		for (int i = 0; i < j; i++) {
			csvText = csvText.concat("Unit;Value;"); 
		}
		csvText = csvText.concat("Timestamp \n" + deviceId + ";");
		for (int i = 0; i < j; i++) {
			csvText = csvText.concat(systemObisCode[i] + ";" + currentUnit[i] + ";" + currentKWh[i] + ";");
		}
		csvMsg = csvText.concat(currentDateDash + " " + currentTimeColon);
	
		//LPEX
		lpexMsg = "";
		lpexText = "LPEX V2.0 \nDatum;Zeit;Kundennummer;Kundenname;eindeutigeKDNr;GEId;GEKANr;KALINr;Linie;eindeutigeLINr;ZPB;Kennzahl";
		for (int i = 0; i < j; i++) {
			lpexText = lpexText.concat(";Einheit;Wandlerfaktor;MPDauer;Werte; "); 
		}
			lpexText = lpexText.concat("\n" + currentDateDot + ";" + currentTimeColon + ";" + customerNumber + ";" + customerName + ";" + uniqueKDNr + ";" + gEid + ";" + gEKANr + ";" + kALINr + ";" + line + ";" + uniqueLINr + ";" + zPB);
			for (int i = 0; i < j; i++) {
				lpexText = lpexText.concat(";" + systemObisCode[i] + ";" + currentUnit[i] + ";" + transformerFactor + ";" + mPduration + ";" + currentKWh[i] + ";" + noName); 
			}
			lpexMsg = lpexText;
			findAndDeleteByDeviceID(deviceId);
			writeToFile(deviceId + "_" + timestamp + ".csv", csvMsg);
			writeToFile(deviceId + "_" + timestamp + ".lpex", lpexMsg);	
		}
	
	

}				
}
