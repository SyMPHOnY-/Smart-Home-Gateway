package cz.vutbr.wislab.symphony.sip.client;

import java.util.EventObject;

public class SipEvent extends EventObject{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String content;
	public String from;
	public SipEventType type;
	public int remoteRtpPort;

	public enum SipEventType {
		MESSAGE, BYE, CALL, BUSY_HERE, ACCEPTED, SERVICE_UNAVAILABLE, CALL_CONNECTED, LOCAL_RINGING, DECLINED
	}

	public SipEvent(Object source, SipEventType type, String content,
			String from) {
		super(source);

		this.type = type;
		this.content = content;
		this.from = from;
	}

	public SipEvent(Object source, SipEventType type, String content,
			String from, int rtpPort) {
		super(source);
		this.type = type;
		this.content = content;
		this.from = from;
		this.remoteRtpPort = rtpPort;
	}
}
