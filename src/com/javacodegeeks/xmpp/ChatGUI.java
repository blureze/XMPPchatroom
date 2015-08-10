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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

public class ChatGUI extends JFrame implements ActionListener {
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
	private String selectedWord;
	private final String server;
	private final int port;
	private int mouseX;
	private int mouseY;

	private boolean queryFinished;
	private boolean drag;

	private ChatManager chatManager;
	private MessageListener messageListener;
	private ArrayList<String> scents;
	private Thread sendQuery;
	private Thread translation;

	private Socket client;
	private ObjectOutputStream out;

	private Highlighter highlighter;
	private HighlightPainter painter;

	public ChatGUI(String username, ChatManager chatManager,
			MessageListener messageListener, String server, int port) {
		this.setTitle("Chatroom");
		this.setSize(800, 600);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());

		this.username = username;
		this.chatManager = chatManager;
		this.messageListener = messageListener;
		this.server = server;
		this.port = port;

		queryFinished = true;
		drag = false;
		scents = new ArrayList<String>();

		init();
		// getSelectedWord();
	}

	private void init() {
		chatPanel = new JPanel(new BorderLayout());
		
		chat = new JTextArea(); // message display
		chat.setEditable(false);
		chat.setSelectedTextColor(Color.RED);
		highlighter = chat.getHighlighter();
		painter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);

		chat.addMouseMotionListener(new MouseAdapter() {
			public void mouseDragged(MouseEvent e) {
				drag = true;
				/*
				 * final JTextArea s = (JTextArea) e.getSource(); selectedWord =
				 * s.getSelectedText();
				 * 
				 * if(selectedWord != null && !selectedWord.equals(" ")
				 * &&!selectedWord.equals("\n") &&!selectedWord.equals("")) {
				 * System.out.println("selectedWord: "+ selectedWord); //final
				 * InetSocketAddress isa = new InetSocketAddress(server,port);
				 * Thread socketClient = new Thread(new Runnable() {
				 * 
				 * @Override public void run() { try { client = new
				 * Socket(server,port); //client.connect(isa,10000);
				 * //System.out.println("client connect to server.");
				 * 
				 * // send to server Word word = new Word(s.getSelectionStart(),
				 * s.getSelectionEnd()); out = new
				 * ObjectOutputStream(client.getOutputStream());
				 * out.writeObject(word); out.flush(); out.close(); out = null ;
				 * Thread.sleep(10); } catch (UnknownHostException e) {
				 * e.printStackTrace(); } catch (IOException e) {
				 * System.out.println("Socket is wrong!" );
				 * System.out.println("IOException :" + e.toString());
				 * e.printStackTrace(); } catch (InterruptedException e) {
				 * e.printStackTrace(); } } }); socketClient.setDaemon(true);
				 * socketClient.start(); }
				 */
			}
		});

		chat.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				final JTextArea s = (JTextArea) e.getSource();
				mouseX = e.getX();
				mouseY = e.getY();
				// System.out.println("query: " + query);
				// System.out.println(s.getSelectionStart() + " " +
				// s.getSelectionEnd());

				sendQuery = new Thread(new Runnable() {
					@Override
					public void run() {
						query = s.getSelectedText();
						//System.out.println("query: " + query);
						if (query != null && !query.equals(" ") && !query.equals("\n")) {
							try {
								highlighter.addHighlight(s.getSelectionStart(),
										s.getSelectionEnd(), painter);
							} catch (BadLocationException e1) {
								e1.printStackTrace();
							}

							try {
								addScent();
								// translate();
								sendGET();
							} catch (IOException e) {
								e.printStackTrace();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				});

				translation = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							translate();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});

				sendQuery.setDaemon(true);
				sendQuery.start();
				translation.setDaemon(true);
				translation.start();
			}
		});

		chatPanel.add(chat, BorderLayout.CENTER);

		typingPanel = new JPanel();
		typingPanel.setBackground(Color.CYAN);
		message = new JTextField(60); // typing area
		message.addActionListener(this);
		typingPanel.add(message);

		sendbtn = new JButton("Send");
		sendbtn.addActionListener(this);
		typingPanel.add(sendbtn);

		chatPanel.add(typingPanel, BorderLayout.SOUTH);

		imgPanel = new JPanel(new BorderLayout());

		img = new JPanel(new GridLayout(3, 1));
		img.setBackground(Color.GREEN);
		imgPanel.add(img, BorderLayout.CENTER);

		scentspace = new JTextArea();
		scentspace.setEditable(false);
		imgPanel.add(scentspace, BorderLayout.SOUTH);

		add(chatPanel, BorderLayout.CENTER);
		add(imgPanel, BorderLayout.EAST);

		this.setVisible(true);
	}

	private void getSelectedWord() {
		Thread getWord = new Thread(new Runnable() {
			ObjectInputStream in;

			@Override
			public void run() {
				try {
					in = new ObjectInputStream(client.getInputStream());
					if (in != null) {
						Word word = (Word) in.readObject();
						System.out.println("Client get:" + word.getStart()
								+ ", " + word.getEnd());
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		});

		getWord.setDaemon(true);
		getWord.start();
	}

	public void showMessage(String msg) {
		chat.append(msg);
	}

	private void addScent() { // add to scent space
		if (scents.isEmpty() || !scents.contains(query)) {
			scents.add(query);
			scentspace.append(query + "\t");
			queryFinished = true;
		}
	}

	private void sendGET() throws IOException {
		String url = "https://sheltered-scrubland-2490.herokuapp.com/query/"
				+ query;
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		int responseCode = con.getResponseCode();
		System.out.println("GET Response Code :: " + responseCode);
		if (responseCode == HttpURLConnection.HTTP_OK) { // success
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String inputLine;
			String[] picURL;
			ArrayList<String> picID = new ArrayList<String>();

			inputLine = in.readLine();
			System.out.println(inputLine);
			inputLine = inputLine.substring(1, inputLine.length() - 1);
			inputLine = inputLine.replace('"', ' ');
			picURL = inputLine.split(",");
			int size = picURL.length;
			/*******************/
			for (int i = 0; i < 3; i++) {
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

		for (int i = 0; i < 3; i++) {
			try {
				imgURL = "https://s3.amazonaws.com/peekaboom_id/"
						+ picID.get(i);
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

	private void translate() throws Exception {
		Translator translator = new Translator(query);
		String translatedText = translator.getTranslatedText();

		JLabel translationlb = new JLabel();
		translationlb.setText(translatedText);
		translationlb.setLocation(mouseX, mouseY + 15);

		img.add(translationlb);
		img.revalidate();
		img.repaint();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String msg = message.getText();
		if (msg.length() != 0) {
			newMessage = new Message();
			newMessage.setBody(msg);
			/************/
			if (username.equals("testUser1"))
				newMessage.setTo("testUser2@blureze-pc");
			else
				newMessage.setTo("testUser1@blureze-pc");
			/************/
			try {
				System.out.println(String.format("msg: '%1$s' with JID: %2$s",
						newMessage.getBody(), newMessage.getTo()));
				Chat chat = chatManager.createChat(newMessage.getTo(),
						messageListener);
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
