package cz.vutbr.wislab.symphony.sip.client;

import gov.nist.javax.sip.SipStackExt;
import gov.nist.javax.sip.clientauthutils.AuthenticationHelper;
import gov.nist.javax.sip.message.SIPMessage;



import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;
import java.util.Timer;


import javax.sip.ClientTransaction;
import javax.sip.DialogTerminatedEvent;
import javax.sip.IOExceptionEvent;
import javax.sip.InvalidArgumentException;
import javax.sip.ListeningPoint;
import javax.sip.ObjectInUseException;
import javax.sip.PeerUnavailableException;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.ServerTransaction;
import javax.sip.SipException;
import javax.sip.SipFactory;
import javax.sip.SipListener;
import javax.sip.SipProvider;
import javax.sip.SipStack;
import javax.sip.TimeoutEvent;
import javax.sip.TransactionTerminatedEvent;
import javax.sip.TransactionUnavailableException;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.URI;
import javax.sip.header.CSeqHeader;
import javax.sip.header.ExpiresHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.header.ProxyAuthorizationHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;

import cz.vutbr.wislab.symphony.sip.clientAAA.AccountManagerImpl;
import cz.vutbr.wislab.symphony.sip.clientAAA.DigestClientAuthenticationMethod;
import cz.vutbr.wislab.symphony.sip.messages.MessageMessage;
import cz.vutbr.wislab.symphony.sip.messages.RegisterMessage;
import cz.vutbr.wislab.symphony.converter.JsonConverter;
import cz.vutbr.wislab.symphony.converter.ScheduleTask;



public class SipManager implements SipListener, SipManagerInterface {
	/**
	 * 
	 */
	
	public static String localIp;
	public static int localPort;
	public static String transport;
	public static String proxyIp;
	public static int proxyPort;
	public static String registrarIp;
	public static String sipUserName; 													
	public static String sipPassword;
	
	public String actualTime;
	
	Timer timer;
	Request messageAuth; // request before proxy authorization

	private static SipStack sipStack;
	public SipProvider sipProvider;
	public HeaderFactory headerFactory;
	public AddressFactory addressFactory;
	public MessageFactory messageFactory;
	private ClientTransaction inviteTid;
	
	private SipFactory sipFactory;
	private ListeningPoint udpListeningPoint;
	private boolean isRegistered = false;

	private boolean initialized;
	

	private long cseqNumber = (long) (Math.random() * 2000); // random start sequence number

	private ExpiresHeader expiresRegistration = null;

	public ExpiresHeader getExpiresRegistration() {
		return expiresRegistration;
	}

	public void setExpiresRegistration(ExpiresHeader expiresRegistration) {
		this.expiresRegistration = expiresRegistration;
	}

	public boolean getIsRegistered() {
		return isRegistered;
	}

	public long getCseqNumber() {
		return cseqNumber;
	}

	public void setCseqNumber(long cseqNumber) {
		this.cseqNumber = cseqNumber;
	}

	

	public SipManager() {
		initialize();
	}

	private void initialize() {
		AccountManagerImpl.password = sipPassword;
		AccountManagerImpl.registrarIp = registrarIp;
		AccountManagerImpl.username = sipUserName + "@" + registrarIp;		
		
		// Create the SIP factory and set the path name.
		sipFactory = SipFactory.getInstance();
		sipFactory.resetFactory();
		sipFactory.setPathName("gov.nist");

		// A1 is set to proxy-registration (there exist outbound proxy)
		Properties properties = new Properties();
		/*properties
				.setProperty(
						"javax.sip.OUTBOUND_PROXY",
						sipProfile.getProxyEndpoint() + "/"
								+ sipProfile.getTransport());*/
		properties
		.setProperty(
				"javax.sip.OUTBOUND_PROXY",
				proxyIp + ":" + proxyPort + "/"
						+ transport);
		
		properties.setProperty("javax.sip.STACK_NAME", "SyMPHOnY-SIP-Client");

		try {
			if (udpListeningPoint != null) {
				// Binding again
				sipStack.deleteListeningPoint(udpListeningPoint);
				sipProvider.removeSipListener(this);
			}
			sipStack = sipFactory.createSipStack(properties);
			System.out.println("createSipStack " + sipStack);
		} catch (PeerUnavailableException e) {
			e.printStackTrace();
		} catch (ObjectInUseException e) {
			e.printStackTrace();
		}
		try {
			headerFactory = sipFactory.createHeaderFactory();
			addressFactory = sipFactory.createAddressFactory();
			messageFactory = sipFactory.createMessageFactory();
			/*udpListeningPoint = sipStack.createListeningPoint(
					sipProfile.getLocalIp(), sipProfile.getLocalPort(),
					sipProfile.getTransport());*/
			udpListeningPoint = sipStack.createListeningPoint(
					localIp, localPort,transport);
			sipProvider = sipStack.createSipProvider(udpListeningPoint);
			sipProvider.addSipListener(this);
			initialized = true;
			// sipManagerState = SipManagerState.READY;
		} catch (PeerUnavailableException e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		/*// Instantiate Timer Object
		Timer time = new Timer(); 
		// Instantiate SheduledTask class
		ScheduleTask st = new ScheduleTask(); 
		// Create Repetitively task for every 15 min
		time.schedule(st, (getTimeOfFirstSchedule()*1000)+1, 900000); */
	}

	public Address createContactAddress() {
		try {
			/*return this.addressFactory.createAddress("sip:"
					+ getSipProfile().getSipUserName() + "@"
					+ getSipProfile().getLocalEndpoint() + ";transport=udp");*/
			return this.addressFactory.createAddress("sip:"
					+ sipUserName + "@"
					+ localIp + ":" + localPort + ";transport="+transport);
		} catch (ParseException e) {
			return null;
		}
	}

	public ArrayList<ViaHeader> createViaHeader() {
		ArrayList<ViaHeader> viaHeaders = new ArrayList<ViaHeader>();
		ViaHeader myViaHeader;
		try {
			/*myViaHeader = this.headerFactory.createViaHeader(
					sipProfile.getLocalIp(), sipProfile.getLocalPort(),
					sipProfile.getTransport(), null);*/
			myViaHeader = this.headerFactory.createViaHeader(
					localIp, localPort,transport, null);
			myViaHeader.setRPort();
			viaHeaders.add(myViaHeader);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (InvalidArgumentException e) {
			e.printStackTrace();
		}
		return viaHeaders;
	}

	public void register() {
		//initialize();
		cseqNumber = cseqNumber + 1;
		RegisterMessage registerRequest = new RegisterMessage();
		try {
			Request r = registerRequest.makeRequest(this, cseqNumber, 3600);
			final ClientTransaction transaction = this.sipProvider
					.getNewClientTransaction(r);
			try {
				transaction.sendRequest();
			} catch (SipException e) {
				// TODO Auto-generated catch block
				System.out.println("ERROR  during registration");
				e.printStackTrace();
			}

		} catch (ParseException e) {
			System.out.println("ERROR  during registration");
			e.printStackTrace();
		} catch (InvalidArgumentException e) {
			System.out.println("ERROR  during registration");
			e.printStackTrace();
		} catch (TransactionUnavailableException e) {
			System.out.println("ERROR  during registration");
			e.printStackTrace();
		}

	}

	public void deregister() {
		//initialize();
		RegisterMessage registerRequest = new RegisterMessage();
		try {
			Request r = registerRequest.makeRequest(this, cseqNumber, 0);
			final ClientTransaction transaction = this.sipProvider
					.getNewClientTransaction(r);
			// Send the request statefully, through the client transaction.
			try {
				transaction.sendRequest();
			} catch (SipException e) {
				System.out.println("ERROR  during de-registration");
				e.printStackTrace();
			}
			
			if (udpListeningPoint != null) {
				// Binding again
				try {
					sipStack.deleteListeningPoint(udpListeningPoint);
				} catch (ObjectInUseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				sipProvider.removeSipListener(this);				
			}
			timer.cancel();
			timer = null;
		} catch (ParseException e) {
			System.out.println("ERROR  during de-registration");
			e.printStackTrace();
		} catch (InvalidArgumentException e) {
			System.out.println("ERROR  during de-registration");
			e.printStackTrace();
		} catch (TransactionUnavailableException e) {
			System.out.println("ERROR  during de-registration");
			e.printStackTrace();
		}

	}

	@Override
	public void processDialogTerminated(DialogTerminatedEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void processIOException(IOExceptionEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("processIOException");

	}

	@Override
	public void processRequest(RequestEvent arg0) {
		System.out.println("Request is accepted");
		Request request = (Request) arg0.getRequest();
		if (request.getMethod().contains("MESSAGE")) {
			
			
			SIPMessage sp = (SIPMessage) request;
			System.out.println(request.getMethod());
			sendOk(arg0);
			try {
				String message = sp.getMessageContent();
				System.out.println("MESSAGE TO RECOMPOSE: " + message);
				//System.out.println(sp);
				if (!message.contains("<isComposing xmlns='urn:ietf:params:xml:ns:im-iscomposing'\n") && message.contains("Consumption last 15 minutes")) {
					System.out.println("Inside if");
					if(message.contains("<")){ 
					message = crop(message);
					}
					System.out.println(message);
					JsonConverter jsonConv = new JsonConverter();
					jsonConv.recomposeAndSave(message);
					jsonConv.sendToSFTP();
				}
				
				
				//***********send event to user
				//HashMap<String, String> properties = new HashMap<String, String>();
				//properties.put("SIP-incomming-message", message);
				//Activator.event = new Event("symphony/event", properties);
				//Activator.admin.postEvent(Activator.event);
				//********************************
				
				
				 /*dispatchSipEvent(new SipEvent(this, SipEventType.MESSAGE,
				 message, sp.getFrom().getAddress().toString()));*/
				 
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		if (request.getMethod().contains("PUBLISH")) {
			sendOk(arg0);
		}
	}

	@Override
	public void processResponse(ResponseEvent arg0) {
		// TODO Auto-generated method stub
		Response response = (Response) arg0.getResponse();
		System.out.println(response);
		ClientTransaction tid = arg0.getClientTransaction();
		CSeqHeader cseq = (CSeqHeader) response.getHeader(CSeqHeader.NAME);
		this.cseqNumber = cseq.getSeqNumber();

		// response 401
		if (response.getStatusCode() == Response.UNAUTHORIZED) {
			AuthenticationHelper authenticationHelper = ((SipStackExt) sipStack)
					.getAuthenticationHelper(new AccountManagerImpl(),
							headerFactory);
			try {
				inviteTid = authenticationHelper.handleChallenge(response, tid,
						sipProvider, 5);
				inviteTid.sendRequest();

			} catch (NullPointerException e) {
				e.printStackTrace();
			} catch (SipException e) {
				e.printStackTrace();
			}
		}
		// response 407
		else if (response.getStatusCode() == Response.PROXY_AUTHENTICATION_REQUIRED) {
			ProxyAuthorizationHeader test = createProxyAHeader(response, tid
					.getRequest().getRequestURI()); // create PROXY
													// authorization field from
													// response
			AuthenticationHelper authenticationHelper = ((SipStackExt) sipStack)
					.getAuthenticationHelper(new AccountManagerImpl(),
							headerFactory);

			try {
				inviteTid = authenticationHelper.handleChallenge(response, tid,
						sipProvider, 5);
				inviteTid.getRequest().addHeader(test);
				System.out.println(inviteTid.getRequest());
				inviteTid.sendRequest();

			} catch (NullPointerException e) {
				e.printStackTrace();
			} catch (SipException e) {
				e.printStackTrace();
			}
		}
		// response 200 OK (REGISTER request)
		else if (response.getStatusCode() == Response.OK
				&& response.getHeader(CSeqHeader.NAME).toString()
						.contains("REGISTER")) {
			//  ContactHeader c = (ContactHeader)
			// (response.getHeader(ContactHeader.NAME));
			
			
				timer = new Timer(true);
				timer.schedule(new RegisterReminder(this), (long) (20000)); // registration is repeated every 20 seconds because of NAT
			
			
		}
	}

	@Override
	public void processTimeout(TimeoutEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("Transaction Time out");
	}

	@Override
	public void processTransactionTerminated(TransactionTerminatedEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("Transaction Terminated Event");
		System.out.println(arg0.toString());

	}

	private void sendOk(RequestEvent requestEvt) {
		Response response;
		try {
			response = messageFactory.createResponse(200,
					requestEvt.getRequest());
			ServerTransaction serverTransaction = requestEvt
					.getServerTransaction();
			if (serverTransaction == null) {
				serverTransaction = sipProvider
						.getNewServerTransaction(requestEvt.getRequest());
			}
			serverTransaction.sendResponse(response);

		} catch (ParseException e) {
			e.printStackTrace();
		} catch (SipException e) {
			e.printStackTrace();
		} catch (InvalidArgumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendMessage(String to, String message)
			throws NotInitializedException {
		if (!initialized)
			throw new NotInitializedException("Sip Stack not initialized");
		MessageMessage inviteRequest = new MessageMessage();
		try {
			Request r = inviteRequest.MakeRequest(this, to, message);
			this.messageAuth = r;

			final ClientTransaction transaction = this.sipProvider
					.getNewClientTransaction(r);
			try {
				transaction.sendRequest();
			} catch (SipException e) {
				e.printStackTrace();
			}

		} catch (TransactionUnavailableException e) {
			e.printStackTrace();
		} catch (ParseException e1) {
			e1.printStackTrace();
		} catch (InvalidArgumentException e1) {
			e1.printStackTrace();
		}
	}


	private ProxyAuthorizationHeader createProxyAHeader(Response response,
			URI uriReq) {
		// get all necessary information from Proxy-authenticate header
		ProxyAuthorizationHeader m_proxyAuthHeader = null;
		String authHeaderString = response.toString();		
		authHeaderString = response.toString().substring(
				response.toString().indexOf("Proxy-Authenticate"));
		String schema = "Digest";
		// Get nonce from response
		String nonce = authHeaderString.substring(authHeaderString
				.indexOf("nonce") + 7);
		nonce = nonce.substring(0, nonce.indexOf("\""));
		// Get realm from response
		String realm = authHeaderString.substring(authHeaderString
				.indexOf("realm") + 7);
		realm = realm.substring(0, realm.indexOf("\""));
		// Get qop from response
		String qop = authHeaderString
				.substring(authHeaderString.indexOf("qop") + 5);
		qop = qop.substring(0, qop.indexOf("\""));
		String algortihm = "MD5";

		try {
			m_proxyAuthHeader = headerFactory
					.createProxyAuthorizationHeader(schema);
			// set the proper credentials
			m_proxyAuthHeader.setUsername("tag.smarthome@a1.net");
			m_proxyAuthHeader.setRealm(realm);
			m_proxyAuthHeader.setNonce(nonce);
			m_proxyAuthHeader.setURI(uriReq);
			m_proxyAuthHeader.setResponse(response.toString());
			m_proxyAuthHeader.setAlgorithm("MD5");
			DigestClientAuthenticationMethod digest = new DigestClientAuthenticationMethod();
			digest.initialize(realm, AccountManagerImpl.username, uriReq
					.toString(), nonce, AccountManagerImpl.password,
					((CSeqHeader) response.getHeader(CSeqHeader.NAME))
							.getMethod(), null, algortihm);
			String respuestaM = digest.generateResponse();
			m_proxyAuthHeader.setResponse(respuestaM);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (m_proxyAuthHeader);
	}
	
	
		// ----------------------------------------------//
		// Method for cropping HTML tags off the message //
		//-----------------------------------------------//
		
		private String crop (String message){
			
			message = message.substring(message.indexOf(">") + 1);
			message = message.substring(0, message.indexOf("<"));
			return message;
		}
		
		// ----------------------------------------------//
		// Method for getting the first time of schedule //
		//-----------------------------------------------//
		
		
		/*TODO
		 * Reimplement using crone
		 */
		public int getTimeOfFirstSchedule(){
			
			DateFormat dateFormat = new SimpleDateFormat("mm:ss");
			Calendar cal = Calendar.getInstance();
			String time = dateFormat.format(cal.getTime());
			String timeSep[] = time.split(":");
			int min = Integer.parseInt(timeSep[0]);
			int sec = Integer.parseInt(timeSep[1]);
			System.out.println("Mins: " + min + " Secs: " + sec);
			if (min >= 0 && min < 15){
				min = 15 - min;
				if (sec > 0){
					min = min -1;
					sec = 60-sec;
					return (min * 60) + sec;
				}return min*60;	
			}else if (min >= 15 && min < 30){
				min = 30 - min;
				if (sec > 0){
					min = min -1;
					sec = 60-sec;
					return (min * 60) + sec;
				}return min*60;
				
			}else if (min >= 30 && min < 45){
				min = 45 - min;
				if (sec > 0){
					min = min -1;
					sec = 60-sec;
					return (min * 60) + sec;
				}return min*60;	
				
			}else if (min >= 45 && min < 60){
				min = 60 - min;
				if (sec > 0){
					min = min -1;
					sec = 60-sec;
					return (min * 60) + sec;
				}return min*60;	
				
			}
				
			return -1;
		}		
}