package com.javacodegeeks.xmpp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread{
	private boolean OutServer = false;
	private ServerSocket server ;
	private final int ServerPort = 5678;
	
	public Server() {
	    try {
	      server = new ServerSocket(ServerPort);
	    } catch(java.io.IOException e) {
	      System.out.println("Socket�Ұʦ����D !" );
	      System.out.println("IOException :" + e.toString());
	    }
	}
	
	public void run() {
		Socket socket;
		ObjectInputStream in;
		
		System.out.println("Server start!");
		while(!OutServer) {
			socket = null;
			try {
		        synchronized(server) 
		        {
		          socket = server.accept();
		        }
		        System.out.println("���o�s�u : InetAddress = " + socket.getInetAddress());
		        socket.setSoTimeout(15000);
				in = new ObjectInputStream(socket.getInputStream());
				MouseCoordinate mouse = (MouseCoordinate)in.readObject();
		        System.out.println("�ڨ��o����:"+ mouse.getMouse());
		        in.close();
		        in = null ;
		        socket.close();
			} catch (IOException e) {
		        System.out.println("Socket�s�u�����D !" );
		        System.out.println("IOException :" + e.toString());
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
