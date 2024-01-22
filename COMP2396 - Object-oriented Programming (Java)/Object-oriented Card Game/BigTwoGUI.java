import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * The BigTwoGUI class implements the CardGameUI interface. It is used to build a GUI for the Big Two card game and handle all user actions.
 */
public class BigTwoGUI implements CardGameUI {
	// instance variables
	/** a Big Two card game associated with this GUI*/
	private BigTwo game;
	/** a BigTwoClient object communicating with the Big Two game server.*/
	private BigTwoClient client;
	/** a boolean array indicating which cards are being selected.*/
	private boolean[] selected;
	/** an integer specifying the index of the active player.*/
	private int activePlayer=-1;
	/** an integer specifying the index of the local player.*/
	private int localPlayer=-1;
	/** a CardGamePlayer array list storing the players.*/
	private ArrayList<CardGamePlayer> playerList;
	/** a Hand array list storing the hands on table.*/
	private ArrayList<Hand> handsOnTable;
	
	/** the main window of the application.*/
	private JFrame frame;
	/** a panel for showing the cards of each player and the cards played on the table.*/
	private JPanel bigTwoPanel;
	/**  a “Play” button for the active player to play the selected cards.*/
	private JButton playButton;
	/** a “Pass” button for the active player to pass his/her turn to the next player.*/
	private JButton passButton;
	/** a “Send” button for the active player to send his/her message to the chat room.*/
	private JButton chatSendButton;
	
	/** a text area for showing the current game status as well as end of game messages*/
	private ScrollTextArea msgArea;
	/** a text area for showing chat messages sent by the players.*/
	private ScrollTextArea chatArea;
	/** a text field for players to input chat messages.*/
	private JTextField chatInput;
	
	// constructor
	/**
	 * a constructor for creating a BigTwoGUI.
	 * @param game reference to a Big Two card game associated with this GUI.
	 */
	public BigTwoGUI(BigTwo game) {
		// init the game
		this.game = game;
		playerList = this.game.getPlayerList();
		handsOnTable = this.game.getHandsOnTable();
		
		// create the new frame
		frame = new BigTwoFrame();
		disable();
	}
	// methods
	/**
	 * a method for setting the client.
	 * @param client a reference to a BigTwoClient object.
	 */
	public void setClient(BigTwoClient client) {
		this.client = client;
	}
	/**
	 * a method for setting the index of the active player (i.e., the player having control of the GUI).
	 * @param activePlayer the active player of the current round
	 */
	public void setActivePlayer(int activePlayer) {
		if (activePlayer < 0 || activePlayer >= 4) {
			this.activePlayer = -1;
		} else {
			this.activePlayer = activePlayer;
		}
		if (activePlayer==localPlayer) enable();
		else disable();
		resetSelected();
	}
	/** a method for repainting the GUI.*/
	public synchronized void repaint() {
		// update the handsOnTable list
		handsOnTable = this.game.getHandsOnTable();
		// repaint the table
		bigTwoPanel.repaint();
		frame.pack();
	}
	/**  a method for printing the specified string to the message area of the GUI.
	 * @param msg the msg String will be printed on the msg area
	 */
	public void printMsg(String msg) {
		String timeStamp = new SimpleDateFormat("[HH:mm:ss]").format(new java.util.Date());
		msgArea.append(timeStamp+" "+msg);
		// scroll the textArea to the last row
		msgArea.scrollText();
	}
	/**
	 * a method for printing the chat message on the chat area of the GUI
	 * @param text the text will be printed on the chat area
	 */
	public void printChat(String text) {
		String timeStamp = new SimpleDateFormat("[HH:mm:ss]").format(new java.util.Date());
		chatArea.append(timeStamp+" "+text);
		// scroll the textArea to the last row
		chatArea.scrollText();
	}
	/** a method for clearing the message area of the GUI.*/
	public void clearMsgArea() {
		msgArea.setText(null);
	}
	/** a method for resetting the GUI.*/
	public void reset() {
		setActivePlayer(-1);
		resetSelected();
		clearMsgArea();
		enable();
	}
	/** a method for enabling user interactions with the GUI.*/
	public void enable() {
		playButton.setEnabled(true);
		passButton.setEnabled(true);
	}
	/** a method for disabling user interactions with the GUI.*/
	public void disable() {
		playButton.setEnabled(false);
		passButton.setEnabled(false);
	}
	/** a method for prompting the end message to the player.
	 * 
	 * @param winner winner's player index
	 */
	public void promptEnd(int winner) {
		String endMsg = "";
		for (int i=0;i<this.playerList.size();i++) {
			if (i==winner) {
				endMsg += String.format("Player %d wins. \n", i);
			}
			else
				endMsg += String.format("Player %d has %d cards in hand. \n", i,game.getPlayerList().get(i).getCardsInHand().size());
		}
		
		JOptionPane.showMessageDialog(frame,endMsg,"Game ends",JOptionPane.PLAIN_MESSAGE);
		client.sendReady();
	}
	/**  method for prompting the active player to select cards and make his/her move.*/
	public void promptActivePlayer() {
		printMsg(game.getPlayerList().get(activePlayer).getName() + "'s turn: \n");
	}
	/**
	 * Returns an array of indices of the cards selected through the UI.
	 * 
	 * @return an array of indices of the cards selected, or null if no valid cards
	 *         have been selected
	 */
	private int[] getSelected() {
		int[] cardIdx = null;
		int count = 0;
		for (int j = 0; j < selected.length; j++) {
			if (selected[j]) {
				count++;
			}
		}

		if (count != 0) {
			cardIdx = new int[count];
			count = 0;
			for (int j = 0; j < selected.length; j++) {
				if (selected[j]) {
					cardIdx[count] = j;
					count++;
				}
			}
		}
		return cardIdx;
	}
	/**
	 * Resets the list of selected cards to an empty list.
	 */
	private void resetSelected() {
		if (activePlayer != -1)
			selected = new boolean[game.getPlayerList().get(activePlayer).getCardsInHand().size()];
	}
	// inner classes
	/**
	 * an inner class that extends the JPanel class and implements the MouseListener interface. 
	 */
	class BigTwoFrame extends JFrame {
		// constructor
		/**
		 * a constructor for creating a BigTwoFrame.
		 */
		public BigTwoFrame() {
			// init the frame
			setTitle("Big Two");
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setLayout(new BorderLayout());
			
			// add menu bar
			MenuBar menuBar = new MenuBar();
			setJMenuBar(menuBar);
			
			// add the left panel for the game
			GamePanel gamePanel = new GamePanel();
			add(gamePanel,BorderLayout.CENTER);

			// add the right panel for the text displays
			TextPanel textPanel = new TextPanel();
			add(textPanel,BorderLayout.EAST);
			
			pack();
			setMinimumSize(getBounds().getSize());
			setVisible(true);
		}
	}
	/**
	 * an inner class that extends the JMenuBar 
	 */
	class MenuBar extends JMenuBar {
		/**
		 * a constructor for creating a MenuBar.
		 */
		// constructor
		public MenuBar() {
			super();
			JMenu gameMenu = new JMenu("Game");
			add(gameMenu);
			
			// restart button
			JMenuItem connect = new JMenuItem("Connect");
			connect.addActionListener(new ConnectMenuItemListener());
			gameMenu.add(connect);
			
			// quit button
			JMenuItem quit = new JMenuItem("Quit");
			quit.addActionListener(new QuitMenuItemListener());
			gameMenu.add(quit);
		}
	}
	/**
	 * an inner class that extends the JPanel. Container for the bigTwoPanel and controlPanel
	 */
	class GamePanel extends JPanel {
		// constructor
		/**
		 * a constructor for creating a GamePanel.
		 */
		public GamePanel() {
			setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			//setBackground(Color.red);
			setLayout(new BorderLayout());
			
			// add the big two game table
			bigTwoPanel = new BigTwoPanel();
			add(bigTwoPanel,BorderLayout.CENTER);
			
			// add the control bar
			ControlPanel controlPanel = new ControlPanel();
			add(controlPanel,BorderLayout.SOUTH);	
		}
		// methods
		/** overriddien the method to paint the background */
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
	        g.drawImage(new ImageIcon("images/backgrounds/wood.png").getImage(),0, 0, null);
		}

		/**
		 * an inner class that extends the JPanel. The game table
		 */
		class BigTwoPanel extends JPanel {
			// constructor
			/**
			 * a constructor for creating a BigTwoPanel.
			 */
			public BigTwoPanel() {
				setPreferredSize(new Dimension(600,600));
				setOpaque(false);
				setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
				setForeground(Color.white);
				setLayout(new GridLayout(5,1));
				
				// add player panels to the table
				for (int i=0;i<game.getNumOfPlayers();i++) {
					PlayerPanel panel = new PlayerPanel(i);
					add(panel);
				}
				
				// add the hands on table panel to the bottom
				Hand lastHandOnTable = (handsOnTable.isEmpty()) ? null : handsOnTable.get(handsOnTable.size() - 1);
				TablePanel tablePanel = new TablePanel(lastHandOnTable);
				add(tablePanel);
			}
			/**
			 * a method for repainting the game table.
			 */
			public void repaint() {
				// remove all components and add the new components according to current state of the game
				for (Component component : this.getComponents()) {
				    this.remove(component);  
				}
				for (int i=0;i<game.getPlayerList().size();i++) {
					//System.out.println(game.getNumOfPlayers());
					//System.out.println(game.getPlayerList().get(i).getName());
					PlayerPanel panel = new PlayerPanel(i);
					add(panel);
				}
				
				Hand lastHandOnTable = (handsOnTable.isEmpty()) ? null : handsOnTable.get(handsOnTable.size() - 1);
				
				TablePanel tablePanel = new TablePanel(lastHandOnTable);
				add(tablePanel);
				//System.out.println(game.getNumOfPlayers());
			}
			/**
			 * an inner class that extends the JPanel. Container of the player's name, avatar, cards in hand
			 */
			class PlayerPanel extends JPanel {
				// constructor
				/**
				 * a constructor for creating a PlayerPanel.
				 * @param playerIdx the index of the player to track his/her status, cards in hand
				 */
				public PlayerPanel(int playerIdx) {
					CardGamePlayer player = game.getPlayerList().get(playerIdx);
					String name = player.getName();
					Boolean active = (playerIdx==activePlayer);
					Boolean local = (playerIdx==localPlayer);

					setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
					setLayout(new BorderLayout());
					setOpaque(false);
					
					if (name!=null) {
						// add player's name and avatar
						String text = name;
						if (local) text += " (you)";
						
						JLabel label = new JLabel(text);
						label.setHorizontalTextPosition(JLabel.CENTER);
						label.setVerticalTextPosition(JLabel.TOP);
						if (local)
							label.setForeground(Color.white);
						if (active) 
							label.setForeground(Color.cyan);
						
						String avatarPath = String.format("images/avatars/%s.png", name);
						File f = new File(avatarPath);
						if (!f.exists()&name!=null) avatarPath = "images/avatars/unknown.png";
						
						ImageIcon avatar = new ImageIcon(new ImageIcon(avatarPath).getImage().getScaledInstance(75, 75, Image.SCALE_DEFAULT));
						label.setIcon(avatar);
						add(label,BorderLayout.WEST);

						// add player's cards in hand
						CardsLayeredPane cardsLayeredPane = new CardsLayeredPane(player.getCardsInHand(),active,local);
						add(cardsLayeredPane,BorderLayout.CENTER);
					}
					else {
						//System.out.println(playerIdx+" no player");
						JLabel label = new JLabel("Waiting for player...",JLabel.CENTER);
						//label.setOpaque(false);
						//label.setBackground(new Color(0, 0, 0, 100));
						label.setForeground(Color.white);
						add(label,BorderLayout.CENTER);
						//label.setOpaque(true);
					}
				}
				/**
				 * an inner class that extends the JLayeredPane. Container of player's cards in hand
				 */
				class CardsLayeredPane extends JLayeredPane {
					// constructor
					/**
					 * a constructor for creating a CardsLayeredPane.
					 * @param cards the cards to paint on the board
					 * @param active boolean to indicate whether the owner of the cards is the active player
					 */
					public CardsLayeredPane(CardList cards,Boolean active, Boolean local) {
						setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
						setPreferredSize(new Dimension(180,70));
						int x = 0;
						int y = 23;
						
						// add the cards by order
						for (int i=0;i<cards.size();i++) {
							x += 25;
							Card card = cards.getCard(i);
							String name = card.toString();
							CardLabel cardLabel = new CardLabel(name,i,active,local,x,y);
							
							cardLabel.setBounds(x,y,50,70);
							
							// the first(last) card is on the lowest(highest) layer
							add(cardLabel,Integer.valueOf(i));
						}
					}
					/**
					 * an inner class that extends the JLabel implementing the MouseListener.
					 * This models the cards and reacts to player's mouse action.
					 */
					class CardLabel extends JLabel implements MouseListener {
						/** x-coordinate of the card relative to the CardsLayeredPane*/
						int x;
						/** y-coordinate of the card relative to the CardsLayeredPane*/
						int y;
						/** index of the card in the selected array*/
						int cardIdx;
						// constructor
						/**
						 * a constructor for creating a CardLabel.
						 * @param name the name of the card
						 * @param cardIdx the index of the card
						 * @param active the status of the owner of the card
						 * @param x x-coordinate of the card relative to the CardsLayeredPane
						 * @param y y-coordinate of the card relative to the CardsLayeredPane
						 */
						public CardLabel(String name,int cardIdx,Boolean active,Boolean local,int x,int y) {
							super();
							this.x = x;
							this.y = y;
							this.cardIdx = cardIdx;
							
							String imagePath;
							// if the player is active, show the face of the card and make it react to the mouse actions
							// else show the back only
							if (local) imagePath = String.format("images/cards/%s.png", name);
							else imagePath = "images/cards/back.png";
							
							if (local & active) addMouseListener(this);
							
							ImageIcon image = new ImageIcon(new ImageIcon(imagePath).getImage().getScaledInstance(50,70, Image.SCALE_DEFAULT));
							setIcon(image);
							
							//if (selected[cardIdx]) setBounds(x,y+5,50,70);
						}
						/**
						 * a method to react the mouse click. It will toggle the status of the card (selected/not selected) and rerender its position
						 */
						@Override
						public void mouseClicked(MouseEvent e) {
							if (!selected[cardIdx]) {
								y -= 5;
								selected[cardIdx] = true;
							}
							else {
								y += 5;
								selected[cardIdx] = false;
							}
							setBounds(x,y,50,70);
							//System.out.println(Integer.toString(cardIdx)+" selected.");
						}
						/**
						 * a method to react the mouse press.
						 */
						@Override
						public void mousePressed(MouseEvent e) {
						}
						/**
						 * a method to react the mouse release.
						 */
						@Override
						public void mouseReleased(MouseEvent e) {
						}
						/**
						 * a method to react the mouse enter. The card will shift up.
						 */
						@Override
						public void mouseEntered(MouseEvent e) {
							if (!selected[cardIdx]) {
								setBounds(x,y-2,50,70);
							}
						}
						/**
						 * a method to react the mouse exit. The card will shift down.
						 */
						@Override
						public void mouseExited(MouseEvent e) {
							if (!selected[cardIdx]) {
								setBounds(x,y,50,70);
							}
						}
					}
				}
			}
			/**
			 * an inner class that extends the JLabel. Container of the last hand on table
			 */
			class TablePanel extends JPanel {
				// constructor
				/**
				 * a constructor for creating a TablePanel.
				 * @param lastHandOnTable the card list of last hand on table
				 */
				public TablePanel(Hand lastHandOnTable) {
					setOpaque(false);
					setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
					setLayout(new BorderLayout());

					if (lastHandOnTable!=null) {
						String playerName = lastHandOnTable.getPlayer().getName();
						String text = String.format("%s played by %s",lastHandOnTable.getType(),playerName);
						JLabel label = new JLabel();
						label.setText(text);
						label.setHorizontalTextPosition(JLabel.LEFT);
						add(label,BorderLayout.NORTH);
						
						TableCardsPanel tableCardsPanel = new TableCardsPanel(lastHandOnTable);
						add(tableCardsPanel,BorderLayout.CENTER);
					}
				}
				/**
				 * an inner class that extends the JLabel. Container of the cards
				 */
				class TableCardsPanel extends JPanel {
					// constructor
					/**
					 * a constructor for creating a TableCardsPanel.
					 * @param cards the cards to paint on the board
					 */
					public TableCardsPanel(CardList cards) {
						setOpaque(false);
						setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
						setPreferredSize(new Dimension(180,100));
						setLayout(new FlowLayout(FlowLayout.LEADING));
						
						for (int i=0;i<cards.size();i++) {
							Card card = cards.getCard(i);
							String name = card.toString();
							TableCardLabel tableCardLabel = new TableCardLabel(name);
							add(tableCardLabel);
						}
					}
					/**
					 * an inner class that extends the JLabel.
					 * This models the cards on table.
					 */
					class TableCardLabel extends JLabel {
						// constructor
						/**
						 * a constructor for creating a TableCardLabel.
						 * @param name name of the card
						 */
						public TableCardLabel(String name) {
							super();
							String imagePath;
							imagePath = String.format("images/cards/%s.png", name);
							ImageIcon image = new ImageIcon(new ImageIcon(imagePath).getImage().getScaledInstance(50,70, Image.SCALE_DEFAULT));
							setIcon(image);
						}
					}
				}
			}
			
		}
		/**
		 * an inner class that extends the JLabel. Container of the controlling buttons
		 */
		class ControlPanel extends JPanel {
			// constructor
			/**
			 * a constructor for creating a ControlPanel.
			 */
			public ControlPanel() {
				setOpaque(false);
				setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
				setBackground(Color.gray);
				setLayout(new FlowLayout());
				
				// add the play button
				playButton = new JButton("Play");
				playButton.addActionListener(new PlayButtonListener());
				add(playButton);

				// add the pass button
				passButton = new JButton("Pass");
				passButton.addActionListener(new PassButtonListener());
				add(passButton);
			}
		}
	}
	/**
	 * an inner class that extends the JLabel. Container of the text areas
	 */
	class TextPanel extends JPanel {
		// constructor
		/**
		 * a constructor for creating a TextPanel.
		 */
		public TextPanel() {
			setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			//setBackground(Color.lightGray);
			setLayout(new BorderLayout());
			
			MsgPanel msgPanel = new MsgPanel();
			add(msgPanel,BorderLayout.NORTH);
			
			ChatPanel chatPanel = new ChatPanel();
			add(chatPanel,BorderLayout.SOUTH);
		}
		/**
		 * an inner class that extends the JLabel. Container of message area
		 */
		class MsgPanel extends JPanel {
			// constructor
			/**
			 * a constructor for creating a MsgPanel.
			 */
			public MsgPanel() {
				setOpaque(false);
				setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
				setLayout(new BorderLayout());

				// add the message area
				JLabel label = new JLabel("Message");
				label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
				add(label,BorderLayout.NORTH);
				
				msgArea = new ScrollTextArea(15,30);
				msgArea.setPreferredSize(getPreferredSize());
				msgArea.setEditable(false);
				msgArea.setForeground(Color.blue);
				add(msgArea,BorderLayout.CENTER);
			}
		}
		/**
		 * an inner class that extends the JLabel. Container of chat area
		 */
		class ChatPanel extends JPanel {
			/**
			 * a constructor for creating a ChatPanel.
			 */
			public ChatPanel() {
				setOpaque(false);
				setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
				setLayout(new BorderLayout());
				
				// add chat room
				JLabel label = new JLabel("Chat Room");
				label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
				add(label,BorderLayout.NORTH);
				
				chatArea = new ScrollTextArea(15,30);
				chatArea.setPreferredSize(getPreferredSize());
				chatArea.setEditable(false);
				add(chatArea,BorderLayout.CENTER);
				
				// add the chat input
				ChatInputPanel chatInputPanel = new ChatInputPanel();
				add(chatInputPanel,BorderLayout.SOUTH);
			}
			/**
			 * an inner class that extends the JLabel. Container of chat input
			 */
			class ChatInputPanel extends JPanel {
				// constructor
				/**
				 * a constructor for creating a ChatInputPanel.
				 */
				public ChatInputPanel() {
					setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
					setLayout(new BorderLayout());
					
					JLabel label = new JLabel("Chat");
					label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
					add(label,BorderLayout.WEST);
					
					// add the chat input
					chatInput = new JTextField();
					add(chatInput,BorderLayout.CENTER);
					chatInput.addActionListener(new ChatInputListener());
					
					// add the send button
					chatSendButton = new JButton("Send");
					chatSendButton.addActionListener(new ChatSendButtonListener());
					add(chatSendButton,BorderLayout.EAST);
					
				}
				
				
			}
		}
	}
	/**
	 * a class that extends the JTextArea.
	 */
	class ScrollTextArea extends JTextArea {
		// constructor
		/**
		 * a constructor for creating a ScollTextArea.
		 */
		public ScrollTextArea(int r,int c) {
			super(r,c);
		}
		/**
		 * an extended method to scroll the text area to show the last row in the area
		 */
		public void scrollText() {
			String[] lines = getText().split("\n");
			String text = "";
			if (lines.length>getRows()) {
				int startRow = lines.length-getRows();
				for (int i=startRow;i<lines.length;i++) {
					text += lines[i]+"\n";
				}
				setText(text);
			}
		}
	}
	/**
	 * an inner class that implements the ActionListener interface.
	 * handle button-click events for the “Play” button.
	 */
	class PlayButtonListener implements ActionListener {
		/**
		 * a method to react the button click on the "Play" button.
		 */
		@Override
		public synchronized void actionPerformed(ActionEvent e) {
			if (e.getSource()==playButton) {
				//System.out.println("Pressed");
				int[] cardIdx = getSelected();
				if (getSelected()!=null) {
					resetSelected();
					game.makeMove(activePlayer, cardIdx);
				}
			}
		}
	}
	/**
	 * an inner class that implements the ActionListener interface.
	 * handle button-click events for the “Pass” button.
	 */
	class PassButtonListener implements ActionListener {
		/**
		 * a method to react the button click on the "Pass" button.
		 */
		@Override
		public synchronized void actionPerformed(ActionEvent e) {
			if (e.getSource()==passButton) {
				resetSelected();
				game.makeMove(activePlayer,null);
			}
		}
	}
	/**
	 * an inner class that implements the ActionListener interface.
	 * handle button-click events for the “Send” button.
	 */
	class ChatSendButtonListener implements ActionListener {
		/**
		 * a method to react the button click on the "Send" button.
		 */
		@Override
		public synchronized void actionPerformed(ActionEvent e) {
			if (e.getSource()==chatSendButton) {
				String playerName = game.getPlayerList().get(localPlayer).getName();
				String chatMsg = chatInput.getText();
				
				if (chatMsg.length() > 0) {
					String text = chatMsg + "\n";
					// send CHAT message to the server
					client.sendChat(text);
				}
				chatInput.setText(null);
			}
		}
	}
	/**
	 * an inner class that implements the ActionListener interface.
	 * handle "Enter" events for the ChatInput.
	 */
	class ChatInputListener implements ActionListener {
		/**
		 * a method to react the button click on the "Send" button.
		 */
		@Override
		public synchronized void actionPerformed(ActionEvent e) {
			String playerName = game.getPlayerList().get(localPlayer).getName();
			String chatMsg = chatInput.getText();
				
			if (chatMsg.length() > 0) {
				String text = chatMsg + "\n";
				// send CHAT message to the server
				client.sendChat(text);
			}
			chatInput.setText(null);
			
		}
	}
	/**
	 * an inner class that implements the ActionListener interface.
	 * handle menu-item-click events for the “Connect” menu item.
	 * connect to server.
	 */
	class ConnectMenuItemListener implements ActionListener {
		/**
		 * a method to react the button click on the "Connect" button.
		 */
		@Override
		public synchronized void actionPerformed(ActionEvent e) {
			// connect to the server
			client.connect();
		}
	}
	/**
	 * an inner class that implements the ActionListener interface.
	 * handle menu-item-click events for the “Quit” menu item.
	 * terminate the application
	 */
	class QuitMenuItemListener implements ActionListener {
		/**
		 * a method to react the button click on the "Quit" button.
		 */
		@Override
		public synchronized void actionPerformed(ActionEvent e) {
			frame.dispose();
			System.exit(0);
		}
		
	}
	/**
	 * a method for setting the index of local player
	 * @param playerID local player's index in the playerList
	 */
	public void setLocalPlayer(int playerID) {
		this.localPlayer = playerID;
	}

}
