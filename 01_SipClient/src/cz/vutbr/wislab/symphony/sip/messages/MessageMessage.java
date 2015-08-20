package cz.vutbr.wislab.symphony.sip.messages;

import java.text.ParseException;
import java.util.*;

import javax.sip.InvalidArgumentException;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.address.URI;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.header.RouteHeader;
import javax.sip.header.SupportedHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.Request;

import cz.vutbr.wislab.symphony.sip.client.SipManager;
/**
 * 
 * @author Petr Cika
 * @
 *
 */
public class MessageMessage {
	/**
	 * 
	 * @param sipManager - SipManager object
	 * @param to - identifies receiver IP
	 * @param message is message that should be send
	 * @return
	 * @throws ParseException
	 * @throws InvalidArgumentException
	 */
	public Request MakeRequest(SipManager sipManager, String to, String message) throws ParseException, InvalidArgumentException {
		AddressFactory addressFactory = sipManager.addressFactory;
		sipManager.setCseqNumber((long)sipManager.getCseqNumber()+1l);
		//SipURI from = sipManager.addressFactory.createSipURI(sipManager.getSipProfile().getSipUserName(), sipManager.getSipProfile().getLocalEndpoint());
		//Address fromNameAddress = sipManager.addressFactory.createAddress(from);
		Address fromNameAddress = addressFactory.createAddress("sip:"+ SipManager.sipUserName + "@"+ SipManager.registrarIp);

		
		// fromNameAddress.setDisplayName(sipUsername);
		FromHeader fromHeader = sipManager.headerFactory.createFromHeader(fromNameAddress, "SyMPHOnY");
		URI toAddress = sipManager.addressFactory.createURI(to);
		Address toNameAddress = sipManager.addressFactory.createAddress(toAddress);
		// toNameAddress.setDisplayName(username);
		ToHeader toHeader = sipManager.headerFactory.createToHeader(toNameAddress, null);
		URI requestURI = sipManager.addressFactory.createURI(to);
		// requestURI.setTransportParam("udp");
		ArrayList<ViaHeader> viaHeaders = sipManager.createViaHeader();
		CallIdHeader callIdHeader = sipManager.sipProvider.getNewCallId();
		
		CSeqHeader cSeqHeader = sipManager.headerFactory.createCSeqHeader(sipManager.getCseqNumber(),Request.MESSAGE);
		

		MaxForwardsHeader maxForwards = sipManager.headerFactory
				.createMaxForwardsHeader(70);

		Request request = sipManager.messageFactory.createRequest(requestURI,
				Request.MESSAGE, callIdHeader, cSeqHeader, fromHeader,
				toHeader, viaHeaders, maxForwards);
		SupportedHeader supportedHeader = sipManager.headerFactory
				.createSupportedHeader("replaces, outbound");
		request.addHeader(supportedHeader);

		SipURI routeUri = sipManager.addressFactory.createSipURI(null,
				SipManager.proxyIp);
		routeUri.setTransportParam(SipManager.transport);
		routeUri.setLrParam();
		routeUri.setPort(SipManager.proxyPort);

		Address routeAddress = sipManager.addressFactory
				.createAddress(routeUri);
		RouteHeader route = sipManager.headerFactory
				.createRouteHeader(routeAddress);
		request.addHeader(route);
		ContentTypeHeader contentTypeHeader = sipManager.headerFactory
				.createContentTypeHeader("text", "plain");
		request.setContent(message, contentTypeHeader);
		System.out.println(request);
		return request;

	}
}
