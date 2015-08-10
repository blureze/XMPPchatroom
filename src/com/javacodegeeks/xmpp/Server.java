package com.javacodegeeks.xmpp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
	      System.out.println("Socket啟動有問題 !" );
	      System.out.println("IOException :" + e.toString());
	    }
	}
	
	public void run() {
		Socket socket;
		ObjectInputStream in;
		ObjectOutputStream out;
		
		System.out.println("Server start!");
		while(!OutServer) {
			socket = null;
			try {
		        synchronized(server) 
		        {
		          socket = server.accept();
		        }
		        System.out.println("取得連線 : InetAddress = " + socket.getInetAddress());
		        //socket.setSoTimeout(10000);
		        
		        in = new ObjectInputStream(socket.getInputStream());
				Word getWord = (Word)in.readObject();
		        System.out.println("Server get:"+ getWord.getStart() + ", " + getWord.getEnd());
		        in.close();
		        
		        out = new ObjectOutputStream(socket.getOutputStream());
				/*out.writeObject(getWord);				
		        out.flush();
		        out.close();*/
		        
		        
		        //socket.close();
			} catch (IOException e) {
		        System.out.println("Socket連線有問題 !" );
		        System.out.println("IOException :" + e.toString());
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}
