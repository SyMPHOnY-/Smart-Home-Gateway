package cz.vutbr.wislab.symphony.sip.client;

import java.util.EventListener;

public interface ISipEventListener extends EventListener {
	public void onSipMessage(SipEvent sipEvent);
}
