package com.javacodegeeks.xmpp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
	private JPanel typingPanel;
	private JTextArea chat;
	private JTextArea queryRecord;
	private JTextField message;
	private JButton sendbtn;
	private Message newMessage;
	private String username;
	
	private ChatManager chatManager;
	private MessageListener messageListener;
	
	public ChatGUI(String username, ChatManager chatManager, MessageListener messageListener) {
        this.setTitle("Chatroom");
        this.setSize(800, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());
        
		chatPanel = new JPanel(new BorderLayout());
		
		chat = new JTextArea();		// message display
		chat.setEditable(false);
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
		imgPanel.setBackground(Color.BLUE);
		
		queryRecord = new JTextArea("scent");
		queryRecord.setEditable(false);
		imgPanel.add(queryRecord, BorderLayout.SOUTH);

		add(chatPanel, BorderLayout.CENTER);
		add(imgPanel, BorderLayout.EAST);
		
		this.username = username;
		this.chatManager = chatManager;
		this.messageListener = messageListener;
		
        this.setVisible(true);
	}
	
	public void showMessage(String msg) {
		JLabel test = new JLabel();
		test.setForeground(Color.RED);
		chat.append(msg);
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
