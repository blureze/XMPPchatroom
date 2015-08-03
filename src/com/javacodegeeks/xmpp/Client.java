package com.javacodegeeks.xmpp;

import java.util.Collection;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;

public class Client{
	
	private static final int packetReplyTimeout = 500; // millis
	
	private String server;
	private int port;
	
	private ConnectionConfiguration config;
	private XMPPConnection connection;
	
	private ChatManager chatManager;
	private MessageListener messageListener;
	private ChatGUI chatGUI;
	
	private Message newMessage;

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
		
		chatManager.addChatListener( new ChatManagerListener() {
			@Override
			public void chatCreated(Chat chat, boolean createdLocally) {
				if (!createdLocally)
					//chat.addMessageListener(new MyNewMessageListener());;
					chat.addMessageListener(messageListener);;
			}
		});
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
	
    public void setStatus(boolean available, String status) {
        
        Presence.Type type = available? Type.available: Type.unavailable;
        Presence presence = new Presence(type);
        
        presence.setStatus(status);
        connection.sendPacket(presence);
        
    }
	
	public void destroy() {
		if (connection!=null && connection.isConnected()) {
			connection.disconnect();
		}
	}
	
	public void sendMessage() throws XMPPException, InterruptedException {
		while(!chatGUI.isReady());
		System.out.println("ready to send.");
		
		newMessage = chatGUI.getMessage();
		while(newMessage.getBody() == null || newMessage.getTo() == null) {
			Thread.sleep(3000);
			newMessage = chatGUI.getMessage();			
		}
		System.out.println(String.format("msg: '%1$s' with JID: %2$s", newMessage.getBody(), newMessage.getTo()));
		Chat chat = chatManager.createChat(newMessage.getTo(), messageListener);
		chat.sendMessage(newMessage);			
	}
	
    public void createEntry(String user, String name) throws Exception {
        System.out.println(String.format("Creating entry for buddy '%1$s' with name %2$s", user, name));
        Roster roster = connection.getRoster();
        roster.createEntry(user, name, null);
    }
	
    public void printRoster() throws Exception {
    	 Roster roster = connection.getRoster();
    	 Collection<RosterEntry> entries = roster.getEntries();  
    	 for (RosterEntry entry : entries) {
    		 System.out.println(entry);
    	 }
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
