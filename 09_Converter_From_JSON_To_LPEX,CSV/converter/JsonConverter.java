package cz.vutbr.wislab.symphony.converter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.TimerTask;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class JsonConverter {

			//---------------------------------------------------------------------//
			// Method for sending given files over SFTP      					   //
			// Hostname, username and password are hardcoded into this method      //
			//---------------------------------------------------------------------//
			public  void sendToSFTP () {
		        String SFTPHOST = "sftp.energieburgenland.at";
		        int SFTPPORT = 22;
		        String SFTPUSER = "ftpkunde";
		        String SFTPPASS = "tramsrotinom00#";
		        String SFTPWORKINGDIR = "MeterData";

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
		             ex.printStackTrace();
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
				
				
				String currentTime;
				String currentDate;
				// ----------------------------------------------//
				// COMMON FOR ALL								 //
				//-----------------------------------------------//
				String deviceId = "AT0090000000000000000000000016971";
				String currentUnit[] = new String[5];
				String currentKWh[] = new String[5];
				
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
				String classificationNumber = "1-1:1.9.1 P01";
				String transformerFactor = "";
				String mPduration = "15";
				String noName = "2000";
				String lpexText;
				
				
				// ----------------------------------------------//
				// FOR CSV										 //
				//-----------------------------------------------//
				String csvMsg;
				String channel = "1-1:1.8.0";
				String timestamp;
				String csvText;
							
					JSONParser jsonParser = new JSONParser();
					try {
						//create the JSON object
						JSONObject jsonObject = (JSONObject) jsonParser.parse(message);
						
						//get value from JSON object
						String time = (String) jsonObject.get("System_Current_Time");
						System.out.println("Current latt: " + time);
						
						//get values from JSON structure
						JSONObject position = (JSONObject) jsonObject.get("Device_Location");
						System.out.println("Lat: " + position.get("Latitude")+ " Lon " + position.get("Longitude"));
						
						
						//get array from JSON object
						JSONArray loc = (JSONArray) jsonObject.get("Values");
						Iterator it = loc.iterator();				
						int j = 0;
						while(it.hasNext()){				
							JSONObject innerObj = (JSONObject) it.next();
							currentKWh[j] = (String) innerObj.get("Value");
							currentUnit[j] = (String) innerObj.get("Units");
							j++;
						}
						
						currentDate = getTimeAndDate(time)[0];
						currentTime = getTimeAndDate(time)[1];
						timestamp = getTimeAndDate(time)[2] + "-" + currentDate;
						
						//CSV
						csvText = "MeteringPointId;Channel;";
						for (int i = 0; i < j; i++) {
							csvText = csvText.concat("Unit;Value;"); 
						}
						csvText = csvText.concat("Timestamp \n" + deviceId + ";" + channel + ";");
						for (int i = 0; i < j; i++) {
							csvText = csvText.concat(currentUnit[i] + ";" + currentKWh[i] + ";");
						}
						
						csvMsg = csvText.concat(currentDate + " " + currentTime);
						
						//LPEX
						lpexMsg = "";
						lpexText = "LPEX V2.0 \nDatum;Zeit;Kundennummer;Kundenname;eindeutigeKDNr;GEId;GEKANr;KALINr;Linie;eindeutigeLINr;ZPB;Kennzahl";
						for (int i = 0; i < j; i++) {
							lpexText = lpexText.concat(";Einheit;Wandlerfaktor;MPDauer;Werte; "); 
						}
							lpexText = lpexText.concat("\n" + currentDate + ";" + currentTime + ";" + customerNumber + ";" + customerName + ";" + uniqueKDNr + ";" + gEid + ";" + gEKANr + ";" + kALINr + ";" + line + ";" + uniqueLINr + ";" + zPB + ";" + classificationNumber);
							for (int i = 0; i < j; i++) {
								lpexText = lpexText.concat(";" + currentUnit[i] + ";" + transformerFactor + ";" + mPduration + ";" + currentKWh[i] + ";" + noName); 
							}
						lpexMsg = lpexText;
						findAndDeleteByDeviceID(deviceId);
						writeToFile(deviceId + "_" + timestamp + ".csv", csvMsg);
						writeToFile(deviceId + "_" + timestamp + ".lpex", lpexMsg);	
					} catch (org.json.simple.parser.ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
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
				       
				     /* } else if (listOfFiles[i].isDirectory()) {
				        System.out.println("Directory " + listOfFiles[i].getName());
				      */}
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
				String[] readyTimeAndDate = new String[3];
				readyTimeAndDate[0] = (repair[2] + "." + repair[1] + "." + repair[0]); 
				wrong = rightTimeAndDate[1];
				repair = wrong.split(":");
				readyTimeAndDate[1] = rightTimeAndDate[1];
				readyTimeAndDate[2] = (repair[0] + "." + repair[1] + "." + repair[2]);
						
				return readyTimeAndDate;
			}
				
}
