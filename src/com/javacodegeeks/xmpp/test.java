package com.javacodegeeks.xmpp;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.jivesoftware.smack.packet.Message;

public class test extends JFrame {
	private JPanel imgPanel;
	private JTextArea chat;
	private JTextArea queryRecord;
	private JTextArea message;
	private JButton sendbtn;
	
    public test() {
        this.setTitle("Chatroom");
        this.setSize(800, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GridBagLayout layout = new GridBagLayout();
        this.setLayout(layout);
		GridBagConstraints s= new GridBagConstraints();
        s.fill = GridBagConstraints.BOTH;
        
		chat = new JTextArea();		// message display
		chat.setEditable(false);
		chat.setBackground(Color.black);
		this.add(chat);
        s.gridwidth = 7;
        s.weightx = 0;
        s.weighty = 1;
        layout.setConstraints(chat, s);
		
		imgPanel = new JPanel();
		imgPanel.setBackground(Color.blue);
		this.add(imgPanel);
        s.gridwidth = 0;
        s.weightx = 1;
        s.weighty = 1;
        layout.setConstraints(imgPanel, s);		
        
		message = new JTextArea("123");	// typing area
		this.add(message);
		s.gridwidth = 6;
		s.weightx = 1;
		s.weighty = 0;
		layout.setConstraints(message, s);
		
		sendbtn = new JButton("Send");
		this.add(sendbtn);
		s.gridwidth = 1;
		s.weightx = 0;
		s.weighty = 0;
		layout.setConstraints(sendbtn, s);		
		
		queryRecord = new JTextArea("scent");
		queryRecord.setEditable(false);
		this.add(queryRecord); 
		s.gridwidth = 3;
		s.weightx = 0;
		s.weighty = 0;
		layout.setConstraints(queryRecord, s);
		
        this.setVisible(true);
    }
}
