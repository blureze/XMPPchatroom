package com.javacodegeeks.xmpp;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

public class Client{
	
	private static final int packetReplyTimeout = 500; // millis
	
	private String server;
	private int port;
	
	private ConnectionConfiguration config;
	private XMPPConnection connection;
	
	private ChatManager chatManager;
	private MessageListener messageListener;
	private ChatGUI chatGUI;

	public Client(String server, int port) {
		this.server = server;
		this.port = port;
	}
	
	public void init() throws XMPPException {
		
		System.out.println(String.format("Initializing connection to server %1$s port %2$d", server, port));

		SmackConfiguration.setPacketReplyTimeout(packetReplyTimeout);
		
		// Create a connection to the igniterealtime.org XMPP server.
		config = new ConnectionConfiguration(server, port);
		config.setSASLAuthenticationEnabled(false);
		config.setSecurityMode(SecurityMode.disabled);
		
		connection = new XMPPConnection(config);
		connection.connect();
		
		System.out.println("Connected: " + connection.isConnected());
		
		chatManager = connection.getChatManager();
		messageListener = new MyMessageListener();
		
	}
	
	public void performLogin() throws InterruptedException{
		
		if (connection!=null && connection.isConnected()) {
			LoginGUI login = new LoginGUI();
			while(!login.ready());	// wait until user typing is finished		
			System.out.println("ready to login");
			String username = login.getUser();
			String password = login.getPwd();
			try {
				connection.login(username, password);
				System.out.println("Login successfully.");
				login.dispose();
				chatGUI = new ChatGUI(username);
			} catch (XMPPException e) {
				e.printStackTrace();
				System.out.println("Wrong username or password.");
			}
		}
	}
	
	public void destroy() {
		if (connection!=null && connection.isConnected()) {
			connection.disconnect();
		}
	}
	
	public void sendMessage() throws XMPPException, InterruptedException {
		System.out.println("ready to send.");
		Message newMessage = chatGUI.getMessage();
		while(newMessage.getBody() == null && newMessage.getTo() == null) {
			Thread.sleep(3000);
			newMessage = chatGUI.getMessage();
		}
		System.out.println(String.format("msg: '%1$s' with JID: %2$s", newMessage.getBody(), newMessage.getTo()));
		Chat chat = chatManager.createChat(newMessage.getTo(), messageListener);
		chat.sendMessage(newMessage);	
	}
	
	class MyMessageListener implements MessageListener {

		@Override
		public void processMessage(Chat chat, Message message) {
			String from = message.getFrom();
			String body = message.getBody();
			System.out.println(String.format("Received message '%1$s' from %2$s", body, from));		
			
			if(body != null) {
				String user = from.split("@")[0];
				String showmsg = user + ":" + body + "\n";
				chatGUI.showMessage(showmsg);				
			}
		}
	}	
}
