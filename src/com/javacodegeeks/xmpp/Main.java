package com.javacodegeeks.xmpp;

public class Main {
	
	public static void main(String[] args) throws Exception {
		
		//String username = "testUser1";
		//String password = "testUser1";

		Client xmppManager = new Client("localhost", 5222);
		xmppManager.init();
		xmppManager.performLogin();	// login
		
		xmppManager.sendMessage();
		/*while (true) {
			Thread.sleep(50);
			xmppManager.sendMessage();
		}*/
		
		//xmppManager.destroy();
	}
}
