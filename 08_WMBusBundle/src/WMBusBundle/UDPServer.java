package WMBusBundle;

import java.io.*;
import java.net.*;
import java.util.LinkedList;
import java.util.Queue;

import wmbusmessages.WmbusPacket;

class UDPServer implements Runnable 
{
	private DatagramSocket serverSocket;
	private byte[] receiveData 	  = new byte[1024];
	private byte[] sendData	  	  = new byte[1024];
	private Queue<WmbusPacket> queue = new LinkedList<WmbusPacket>();

	public UDPServer() {
		try 
		{
			serverSocket = new DatagramSocket(44444);
			System.out.println("GW UDP SERVER STARTED\n");
		} catch (SocketException e) {
			System.out.println("GW UDP SERVER START FAILED!\n");
			e.printStackTrace();
		}
	}

	public void run() 
	{
		DatagramPacket receiveUdp = new DatagramPacket(receiveData, receiveData.length);
		WmbusPacket gwPacket;	
		
		while (true) {

			try {
				serverSocket.receive(receiveUdp);
				
				gwPacket = new WmbusPacket(new String(receiveUdp.getData(), 0, receiveUdp.getLength()));
				
				if(gwPacket.isValid()) {
					queue.add(gwPacket);
					//System.out.println("ACCEPT PACKET, BODY:" + gwPacket.getRawBody() + "\n");
				}
				//else
				//	 System.out.println("DROPPED WRONG PACKET, BODY:" + gwPacket.getRawBody() + "\n");

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public WmbusPacket getGwPacket() 
	{
		return queue.poll();
	}
	
	
	public void Send() {
		/*
		 * InetAddress IPAddress = receivePacket.getAddress(); int port =
		 * receivePacket.getPort(); String capitalizedSentence =
		 * sentence.toUpperCase(); sendData = capitalizedSentence.getBytes();
		 * DatagramPacket sendPacket = new DatagramPacket(sendData,
		 * sendData.length, IPAddress, port); serverSocket.send(sendPacket);
		 */

	}
}