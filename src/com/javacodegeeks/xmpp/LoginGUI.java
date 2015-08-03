package com.javacodegeeks.xmpp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.jivesoftware.smack.XMPPException;


public class LoginGUI extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	// to hold the username, password and the host
	private JLabel lblName;
	private JLabel lblPwd;
	private JLabel lblHost;

	private JTextField txtName;
	private JTextField txtPwd;
	private JTextField txtHost;
	
	// to login
	private JButton btnlogin;
	
	private boolean finished;

	// Constructor connection receiving a socket number
	public LoginGUI() {

        this.setTitle("Login");
        this.setSize(320, 240);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);
		
        lblName = new JLabel("User: ");
        lblName.setBounds(10, 10, 90, 21);
        add(lblName);

        txtName = new JTextField();
        txtName.setBounds(105, 10, 90, 21);
        txtName.addActionListener(this);
        add(txtName);
        
        lblPwd = new JLabel("Password: ");
        lblPwd.setBounds(10, 50, 90, 21);
        add(lblPwd);

        txtPwd = new JTextField();
        txtPwd.setBounds(105, 50, 90, 21);
        txtPwd.addActionListener(this);
        add(txtPwd);

        lblHost = new JLabel("Host: ");
        lblHost.setBounds(10, 90, 90, 21);
        add(lblHost);

        txtHost = new JTextField("140.114.204.76");
        txtHost.setBounds(105, 90, 90, 21);
        txtHost.addActionListener(this);
        add(txtHost);
        
		// the buttons
		btnlogin = new JButton("Login");
		btnlogin.addActionListener(this);
		btnlogin.setBounds(100, 140, 100, 20);
		add(btnlogin);
		
		// login is not finished
		finished = false;
		
		setVisible(true);
	}
	
	public String getUser() {
		return txtName.getText();
	}
	
	public String getPwd() {
		return txtPwd.getText();
	}
	
	public String getHost() {
		return txtHost.getText();
	}
	
	public boolean ready() throws InterruptedException {
		Thread.sleep(50);
		return finished;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String username = txtName.getText();
		if(username.length() == 0) {	// empty username ignore it	
			System.out.println("User can't be empty.");
			return;
		}
		
		String password = txtPwd.getText();
		if(password.length() == 0) {	// empty password ignore it
			System.out.println("Password can't be empty.");
			return;
		}
		
		String server = txtHost.getText();
		if(server.length() == 0) {	// empty host ignore it
			System.out.println("Server can't be empty.");
			return;
		}
		
		try {
			Client xmppManager = new Client(server, 5222, username, password);
			this.dispose();
		} catch (XMPPException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		//finished = true;
	}
}