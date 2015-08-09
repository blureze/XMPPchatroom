package com.javacodegeeks.xmpp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
	private static ServerSocket server;
	private static Socket s;
	private static ObjectInputStream in;
	
	public static void main(String[] args) throws InterruptedException {
       /*Thread socketServer = new Thread(new Runnable() {
		@Override
		public void run() {
			try {
	            server = new ServerSocket(5678);
	            System.out.println("Server start");
	        } catch (java.io.IOException e) {
	            System.out.println("Socket啟動有問題 !");
	            System.out.println("IOException :" + e.toString());
	        }
	        
			try {
		        synchronized(server) 
		        {
		          s = server.accept();
		        }
		        System.out.println("取得連線 : InetAddress = " + s.getInetAddress()  );
		        s.setSoTimeout(15000);
				in = new ObjectInputStream(s.getInputStream());
		        //DataTest data = (DataTest)in.readObject();
		        //System.out.println("我取得的值:"+data.getP());
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}
       });*/
        new Server().start();
		new LoginGUI();
		
		/*Client xmppManager = new Client("140.114.204.76", 5222);
		xmppManager.init();
		xmppManager.performLogin();	// login
		xmppManager.setStatus(true, "Hello");
		xmppManager.createEntry("testUser2@140.114.204.76", "testUser2");

		xmppManager.printRoster();*/
		
		//socketServer.start();
		/*boolean isRunning = true;
		while (isRunning) {
			Thread.sleep(50);
			MouseCoordinate coord;
			try {
				coord = (MouseCoordinate) in.readObject();
				System.out.println(coord);
				// do something with coordinate...				
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// Or use unshared variant as in client:
			// MouseCoordinate coord = (MouseCoordinate) in.readUnsharedObject();
		}*/
		//xmppManager.destroy();
	}
}
