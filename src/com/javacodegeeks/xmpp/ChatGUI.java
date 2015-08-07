package com.javacodegeeks.xmpp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

public class ChatGUI extends JFrame implements ActionListener{
	private static final long serialVersionUID = 1L;
	private JPanel chatPanel;
	private JPanel imgPanel;
	private JPanel img;
	private JPanel typingPanel;
	private JTextArea chat;
	private JTextArea scentspace;
	private JTextField message;
	private JButton sendbtn;
	private Message newMessage;
	private String username;
	private String query;
	
	private ChatManager chatManager;
	private MessageListener messageListener;
	private ArrayList<String> scents;

	
	public ChatGUI(String username, ChatManager chatManager, MessageListener messageListener) {
        this.setTitle("Chatroom");
        this.setSize(800, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());
        
		chatPanel = new JPanel(new BorderLayout());
		
		chat = new JTextArea();		// message display
		chat.setEditable(false);
		chat.setSelectedTextColor(Color.RED);
		Thread selectText = new Thread(new Runnable() {
			public void run() {		
				chat.addMouseListener(new MouseAdapter() {
					public void mouseReleased(MouseEvent e) {
						JTextArea s = (JTextArea) e.getSource();
			    		query = s.getSelectedText();
			    		System.out.println(query);
			    		if(query != null) {
							Thread sendQuery = new Thread(new Runnable() {
								@Override
								public void run() {
									try {
										sendGET();
									} catch (IOException e) {
										e.printStackTrace();
									}									
								}	
							});
							sendQuery.setDaemon(true);
							sendQuery.start();
						}
					}
				});
			}
	    });

		chatPanel.add(chat,BorderLayout.CENTER);

		typingPanel = new JPanel();
		typingPanel.setBackground(Color.CYAN);
		message = new JTextField(60);	// typing area
		message.addActionListener(this);
		typingPanel.add(message);		
		
		sendbtn = new JButton("Send");
		sendbtn.addActionListener(this);
		typingPanel.add(sendbtn);	
		
		chatPanel.add(typingPanel, BorderLayout.SOUTH);

		imgPanel = new JPanel(new BorderLayout());
		
		img = new JPanel(new GridLayout(3,1));
		img.setBackground(Color.GREEN);
		imgPanel.add(img, BorderLayout.CENTER);
		
		scentspace = new JTextArea();
		scentspace.setEditable(false);
		imgPanel.add(scentspace, BorderLayout.SOUTH);

		add(chatPanel, BorderLayout.CENTER);
		add(imgPanel, BorderLayout.EAST);
		
		this.username = username;
		this.chatManager = chatManager;
		this.messageListener = messageListener;
		selectText.setDaemon(true);
		selectText.start();

		scents = new ArrayList<String>();
		
        this.setVisible(true);
	}
	
	public void showMessage(String msg) {
		JLabel test = new JLabel();
		test.setForeground(Color.RED);
		chat.append(msg);
	}

    private void sendGET() throws IOException{
    	String url = "https://sheltered-scrubland-2490.herokuapp.com/query/" + query;
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        System.out.println("GET Response Code :: " + responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            String[] picURL;
            ArrayList<String> picID = new ArrayList<String>();
   
            // add to scent space
            if(scents.isEmpty() || !scents.contains(query)) {
            	scents.add(query);
            	scentspace.append(query + "\t");
            }
            
			inputLine = in.readLine();
	        System.out.println(inputLine);
	        inputLine = inputLine.substring(1, inputLine.length()-1);
	        inputLine = inputLine.replace('"', ' ');
	        picURL = inputLine.split(",");
	        int size = picURL.length;
	        /*******************/
	        for(int i = 0; i < 3; i++) {
	         	String str = picURL[i].trim();
	           	str = str.split("https://s3.amazonaws.com/peekaboom_id/")[1];
	           	picID.add(str);
	        }
	            	
	        in.close();
            displayImg(picID);
        } else {
            System.out.println("GET request not worked");
        }
    }
	
    private void displayImg(ArrayList<String> picID) {
    	Image image = null;
    	String imgURL = null;
    	img.removeAll();
    	
    	for(int i = 0; i < 3; i++) {
	        try {
	        	imgURL = "https://s3.amazonaws.com/peekaboom_id/" + picID.get(i);
	            URL url = new URL(imgURL);
	            image = ImageIO.read(url);
	        } catch (IOException e) {
	        	e.printStackTrace();
	        }
	        
	        JLabel label = new JLabel(new ImageIcon(image));
	        img.add(label);    		
    	}

        this.pack();
    }
    
	@Override
	public void actionPerformed(ActionEvent e) {
		String msg = message.getText();
		if(msg.length() != 0) {
			newMessage = new Message();
			newMessage.setBody(msg);
			/************/
			if(username.equals("testUser1"))
				newMessage.setTo("testUser2@blureze-pc");
			else
				newMessage.setTo("testUser1@blureze-pc");
			/************/
			try {
				System.out.println(String.format("msg: '%1$s' with JID: %2$s", newMessage.getBody(), newMessage.getTo()));
				Chat chat = chatManager.createChat(newMessage.getTo(), messageListener);
				chat.sendMessage(newMessage);	
			} catch (XMPPException e1) {
				e1.printStackTrace();
			}
			String showmsg = username + ":" + msg + "\n"; 
			showMessage(showmsg);
			message.setText("");
			message.requestFocus();
		}
	}
}
