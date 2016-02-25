package cz.vutbr.wislab.symphony.converter;


import java.util.TimerTask;


public class ScheduleTask extends TimerTask {
	public void run() {
		JsonConverter json = new JsonConverter();
		json.sendToSFTP();
	}
}
