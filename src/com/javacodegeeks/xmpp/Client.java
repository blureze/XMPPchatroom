package com.javacodegeeks.xmpp;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;
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

public class Client implements Serializable{
	private static final long serialVersionUID = 1L;

	private static final int packetReplyTimeout = 500; // millis
	
	private String server;
	private int port;	// port number for chatting
	private int socketPort = 5678;	// port number for send object
	private String username;
	private String password;
	
	private ConnectionConfiguration config;
	private XMPPConnection connection;
	
	private ChatManager chatManager;
	private MessageListener messageListener;
	private ChatGUI chatGUI;
	
	public Client(String server, int port, String username, String password) throws XMPPException, InterruptedException, UnknownHostException, IOException {
		this.server = server;
		this.port = port;
		this.username = username;
		this.password = password;
		
		init();
	}
	
	public void init() throws XMPPException, InterruptedException {
		
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
					chat.addMessageListener(messageListener);;
			}
		});
		
		/*Thread socketClient = new Thread(new Runnable() {
			@Override
			public void run() {
				Socket client;
				try {
					client = new Socket(server, port);
					System.out.println("client connect to server.");
					ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
			          //°e¥Xobject
			          out.writeObject(data);
			          out.flush();
			          out.close();
			          out = null ;
			          data = null ;
			          client.close();
			          client = null ;
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}				
			}			
		});
	   
	    socketClient.start();*/
		performLogin();
	}
	
	public void performLogin() throws InterruptedException{
		
		if (connection!=null && connection.isConnected()) {
			System.out.println("ready to login");
			try {
				connection.login(username, password);
				System.out.println("Login successfully.");
				chatGUI = new ChatGUI(username, chatManager, messageListener, server, socketPort);
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
