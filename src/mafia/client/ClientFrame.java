package mafia.client;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;

import mafia.client.panel.GamePanel;
import mafia.client.panel.LoadingPanel;
import mafia.client.panel.LoginPanel;
import mafia.client.panel.Panel;
import mafia.server.UserVO;

public class ClientFrame extends JFrame
{
	private static ClientFrame clientFrame;
	private static LoginPanel loginPanel;
	private static LoadingPanel loadingPanel;
	private static GamePanel gamePanel;
	
	public ClientFrame() 
	{
		super("마피아 게임");
		loginPanel = new LoginPanel();
		loadingPanel = new LoadingPanel();
		gamePanel = new GamePanel();
		
		change(loginPanel);
		setVisible(true);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JButton loginBtn = new JButton("로그인");
		loginBtn.addActionListener
		(
			new ActionListener() 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					change(gamePanel);
				}
			}
		);
		add(loginBtn, BorderLayout.SOUTH);
		pack();
		setIconImage(getToolkit().createImage("./images/frameIcon.jpg"));
				
	}
	
	public ClientFrame getInstance()
	{
		clientFrame = this;
		return clientFrame;
	}
	
	public LoginPanel getLoginPanel()
	{
		return loginPanel;
	}
	
	public LoadingPanel getLoadingPanel()
	{
		return loadingPanel;
	}
	
	public GamePanel getGamePanel()
	{
		return gamePanel;
	}
	
	public void change(Panel panel)
	{
		getContentPane().removeAll();
		getContentPane().add(panel.getPanel(), BorderLayout.CENTER);
		revalidate();
		repaint();
		setSize(panel.getSize());
		int locX = (int)( ( Toolkit.getDefaultToolkit().getScreenSize().getWidth() - panel.getSize().getWidth() )/2 );
		int locY = (int)( ( Toolkit.getDefaultToolkit().getScreenSize().getHeight() - panel.getSize().getHeight() )/2 );
		setLocation(locX, locY);
	}	
}
