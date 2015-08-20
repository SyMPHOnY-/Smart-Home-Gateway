package Core;

import java.io.IOException;

public class InitHandler{
	
	private Process miniDLNA;
	private Process ntpClient;
	private Runtime runTime = Runtime.getRuntime();
	
	public void startMiniDLNA(String configPath) {		
		try{
			System.out.println("Starting MiniDLNA");
			miniDLNA = runTime.exec(configPath);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void setDate(String ntpServer ) throws InterruptedException {		
		try{
			System.out.println("Set Date");
			ntpClient = runTime.exec("ntpclient -c 1 -s -h " + ntpServer ); 
			//Thread.sleep(1000);
			//ntpClient.destroy();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void stopMiniDLNA() {
		System.out.println("Stoping MiniDLNA");	
		miniDLNA.destroy();
	}
}
