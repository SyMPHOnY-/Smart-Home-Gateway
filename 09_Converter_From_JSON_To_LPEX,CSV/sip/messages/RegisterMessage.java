package cz.vutbr.wislab.symphony.sip.messages;

import java.text.ParseException;
import java.util.*;

import javax.sip.InvalidArgumentException;
import javax.sip.SipProvider;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.URI;
import javax.sip.header.ExpiresHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.header.ViaHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;

import cz.vutbr.wislab.symphony.sip.client.SipManager;


public class RegisterMessage {

	public Request makeRequest(SipManager sipManager, long cseqNumber, int expiresTime) throws ParseException, InvalidArgumentException {

		AddressFactory addressFactory = sipManager.addressFactory;
		SipProvider sipProvider = sipManager.sipProvider;
		MessageFactory messageFactory = sipManager.messageFactory;
		HeaderFactory headerFactory = sipManager.headerFactory;
		
		// Create addresses and via header for the request
		Address fromAddress = addressFactory.createAddress("sip:"+ SipManager.sipUserName + "@"+ SipManager.registrarIp);
		fromAddress.setDisplayName(SipManager.sipUserName);
		Address toAddress = addressFactory.createAddress("sip:"	+ SipManager.sipUserName + "@"	+ SipManager.registrarIp);
		toAddress.setDisplayName(SipManager.sipUserName);
		Address contactAddress = sipManager.createContactAddress();
		ArrayList<ViaHeader> viaHeaders = sipManager.createViaHeader();
		URI requestURI = addressFactory.createAddress("sip:" + SipManager.registrarIp).getURI();
		
		// Build the request
		final Request request = messageFactory.createRequest(requestURI,
				Request.REGISTER, sipProvider.getNewCallId(),
				headerFactory.createCSeqHeader(cseqNumber, Request.REGISTER),
				headerFactory.createFromHeader(fromAddress, "c3ff411e"),
				headerFactory.createToHeader(toAddress, null), viaHeaders,
				headerFactory.createMaxForwardsHeader(70));
		
		// Add the contact header
		request.addHeader(headerFactory.createContactHeader(contactAddress));
		ExpiresHeader eh = headerFactory.createExpiresHeader(expiresTime);
		request.addHeader(eh);
		
		// Print the request
		System.out.println(request.toString());
		return request;
	}

}
