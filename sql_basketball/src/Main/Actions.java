package Main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import Panels.Games_Panel_After_Search;
import Panels.Management_Panel;
import Panels.ONE_Team_Panel;
import Panels.Players_Panel;
import Panels.Teams_Panel;
import nba_objects.Team;
import sql_package.JDBC;
import sql_package.SQL_FUNCTIONS;
import sql_package.SQL_TABLES;
import sql_package.SQL_TYPES;

public class Actions implements SQL_FUNCTIONS, SQL_TABLES, SQL_TYPES {
	public static JDBC jdbc = new JDBC();
	public static JFrame totalFrame;
	public static JPanel insidePanel;
	public static JPanel playersPanel;
	public static JPanel playersPanelCache;
	public static boolean isFirstTime = true;

	public static ActionListener connectToDatabase(JFrame loginGui) {
		ActionListener connectToDB = new ActionListener() {

			private final String CONNECTION_FAILED = "Failed to connect";
			private final String DATABASE_DRIVER_ERROR = "Driver Error";

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String user = ((LoginGUI) loginGui).getJtfUsername().getText();
					String password = ((LoginGUI) loginGui).getJtfPassword().getText();
					jdbc.loadDriverAndConnnect(user, password);

					// The next 2 lines are for caching the players
					ArrayList<String> result = jdbc.runDBFunctionTableTypeReturn(GET_PLAYERS, ALL, PLAYERS_TYPE);
					playersPanel = new Players_Panel(result);

					/**
					 * !!!CHANGE TO MAIN PAGE!!!
					 */
					totalFrame = new AppGUI();

					loginGui.dispose(); // closing loginGui
				} catch (SQLException e1) {
					printErrorOnGui(CONNECTION_FAILED, e1);
				} catch (ClassNotFoundException e1) {
					printErrorOnGui(DATABASE_DRIVER_ERROR, e1);
				} catch (HeadlessException e1) {
					printErrorOnGui(CONNECTION_FAILED, e1);
				} catch (IOException e1) {
					printErrorOnGui(DATABASE_DRIVER_ERROR, e1);
				}
			}

			public void printErrorOnGui(String error, Exception e) {
				// error print on status
				((LoginGUI) loginGui).getStatusField().setForeground(Color.red);
				((LoginGUI) loginGui).getStatusField().setText(error);
				SwingUtilities.updateComponentTreeUI(loginGui);
			}
		};

		return connectToDB;
	}

	public static ActionListener changeToTeamsPannel(JFrame frame) {
		ActionListener action = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ArrayList<String> result = jdbc.runDBFunction(GET_TABLE_TO_STRING, TEAMS_TABLE);
				totalFrame = frame;
				try {
					if (insidePanel != null)
						frame.remove(insidePanel);
					insidePanel = new Teams_Panel(result);
					frame.add(insidePanel, BorderLayout.CENTER);
					SwingUtilities.updateComponentTreeUI(frame);
				} catch (HeadlessException e1) {
					e1.printStackTrace();
				} catch (SQLException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		};
		return action;
	}

	public static ActionListener changeToGamesPannel(JFrame frame, int homeTeam, int visitTeam) {
		ActionListener action = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ArrayList<String> result = jdbc.runDBFunctionTableTypeReturn(GET_GAMES, homeTeam, visitTeam,
						GAMES_TYPE);
				totalFrame = frame;
				try {
					if (insidePanel != null)
						frame.remove(insidePanel);
					System.out.println("Result is! : " + result);
					insidePanel = new Games_Panel_After_Search(result);
					frame.add(insidePanel, BorderLayout.CENTER);
					SwingUtilities.updateComponentTreeUI(frame);
				} catch (HeadlessException e1) {
					e1.printStackTrace();
				} catch (SQLException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		};
		return action;
	}

	public static ActionListener changeToPlayersPannel() {
		ActionListener action = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// using it because of massive data
				String searchString;
				try{
					searchString = ((Players_Panel) insidePanel).getTextFromSearch();
				}catch(Exception e1){
					searchString = ((Players_Panel) playersPanel).getTextFromSearch();
				}
				if(searchString.equals("Enter name for search:")){
					searchString="";
				}
				
				ArrayList<String> result;
				try {
					if (searchString.equals("") && !isFirstTime) {
						totalFrame.remove(insidePanel);
						insidePanel = playersPanelCache;
					} else {
						result = jdbc.runDBFunctionTableTypeReturn(GET_PLAYERS, searchString, PLAYERS_TYPE);
						if (insidePanel != null) {
							totalFrame.remove(insidePanel);
						}
						if (isFirstTime) {
							playersPanelCache = playersPanel;
							insidePanel = playersPanel;
							isFirstTime = false;
						} else {
							insidePanel = new Players_Panel(result);
							playersPanel = insidePanel;
						}
					}
					totalFrame.add(insidePanel, BorderLayout.CENTER);
					insidePanel.revalidate();
					SwingUtilities.updateComponentTreeUI(totalFrame);
				} catch (HeadlessException e1) {
					e1.printStackTrace();
				} catch (SQLException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		};
		return action;
	}

	public static MouseListener clickLabelChangeColor(Component c) {
		MouseListener action = new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				System.out.println("hello123123");
				Component c = e.getComponent();
				c.setBackground(Color.GRAY);
				((JComponent) c).setOpaque(true);
			}

			public void mouseReleased(MouseEvent e) {
				Component c = e.getComponent();
				c.setBackground(null);
				((JComponent) c).setOpaque(true);
			}
			
		public void mouseClicked(MouseEvent e) {
			if (e.getComponent().getName().equals("player_label"))
				System.out.println("player_label");
			else{
				System.out.println("team_label");
				System.out.println(((JLabel) e.getComponent()).getText());
			}
			
		}
		};
		return action;
	}

	public static FocusListener textDisappeardOnclick(JTextField textField) {
		FocusListener action = new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				textField.setText("Enter name for search:");
			}

			@Override
			public void focusGained(FocusEvent e) {
				textField.setText("");

			}
		};
		return action;
	}

	public static MouseListener changeTo1TeamPanel(JFrame frame, Team team) {
		MouseListener action = new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// totalFrame = frame;
				try {
					new ONE_Team_Panel(team);
					// if (insidePanel != null)
					// frame.remove(insidePanel);
					// insidePanel = new ONE_Team_Panel(team);
					// frame.add(insidePanel, BorderLayout.CENTER);
					// SwingUtilities.updateComponentTreeUI(frame);
				} catch (HeadlessException e1) {
					e1.printStackTrace();
				} catch (SQLException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		};
		return action;
	}

	public static ActionListener changeToManagementPanel(JFrame frame) {
		ActionListener action = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (insidePanel != null)
					frame.remove(insidePanel);
				insidePanel = new Management_Panel();
				frame.add(insidePanel, BorderLayout.CENTER);
				SwingUtilities.updateComponentTreeUI(frame);
			}
		};
		return action;
	}

	// public static void main(String[] args) {
	//
	// try {
	// jdbc.loadDriverAndConnnect("Administrator", "Admin11");
	// } catch (ClassNotFoundException | SQLException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// ArrayList<String> result =
	// jdbc.runDBFunctionTableTypeReturn(GET_TEAM,1610612766,null);
	// ArrayList<String> result = jdbc.runDBFunctionTableTypeReturn(GET_PLAYERS,
	// "s",
	// PLAYERS_TYPE);
	// System.out.println(result.get(1));
	// }

}
