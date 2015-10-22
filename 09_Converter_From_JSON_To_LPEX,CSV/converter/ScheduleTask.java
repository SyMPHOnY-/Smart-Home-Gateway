package cz.vutbr.wislab.symphony.converter;


import java.util.TimerTask;
import cz.vutbr.wislab.symphony.converter.JsonConverter.*;

public class ScheduleTask extends TimerTask {
	public void run() {
		JsonConverter json = new JsonConverter();
		json.sendToSFTP();			}
}
