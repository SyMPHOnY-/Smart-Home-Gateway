package temp;

import java.io.*;
import java.net.*;


class UDPClient
{
   public static void main(String args[]) throws Exception
   {
      DatagramSocket clientSocket = new DatagramSocket();
      InetAddress IPAddress = InetAddress.getByName("localhost");
      byte[] sendData = new byte[1024];
      byte[] receiveData = new byte[1024];
     
      sendData = new String("WEPTECH;TEMPERATURE;22.5C;Humidity;55.2;HEXBODY;2E44B05C10000000021B7AD90800002F2F0A6646020AFB1A120302FD971D01002F2F2F2F2F2F2F2F2F2F2F2F2F2F2F").getBytes();
      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 44444);
      clientSocket.send(sendPacket);
     
      // intentionally wrong packet
      sendData = new String("WEPTECH;Humidity;25.5;HEXBODY;2E44B05C10000000021B7AEC08F").getBytes();
      sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 44444);
      clientSocket.send(sendPacket);
      
      sendData = new String("UKNOWN;HEXBODY;1E44EE092101000001067A4F0010051AB94C4FDA694309E347E86FA437790C").getBytes();
      sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 44444);
      clientSocket.send(sendPacket);
      
      /*DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
     
      clientSocket.receive(receivePacket);
      
      String modifiedSentence = new String(receivePacket.getData());
      
      System.out.println("FROM SERVER:" + modifiedSentence);
      */
      
      clientSocket.close();

   }
}