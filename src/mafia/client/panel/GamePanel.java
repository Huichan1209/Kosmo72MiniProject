package mafia.client.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import mafia.client.Client;

public class GamePanel extends JPanel implements Panel
{
	private static JPanel panel;
	private static JTextField sendMsgTF; //ä�� �Է��ϴ� TextField
	private static JTextField textField; 
	private static JTextArea userListTA;
	private static JTextArea myInfoTf; //�� ����
	private static JTextField timer; //Ÿ�̸� TextField
	private static JTextArea chatTxtArea; //ä�� �����ִ� chatTextArea
	private static JScrollPane scrollPane; //��ũ�ѹ�
	private static JPanel imagePanel; //����̹���
	private static Image img = new ImageIcon("./images/background.jpg").getImage();
	private static Image policeImg = new ImageIcon("./images/police.jpg").getImage();
	private static Image citizenImg = new ImageIcon("./images/citizen.jpg").getImage();;
	private static Image doctorImg = new ImageIcon("./images/doctor.jpg").getImage();
	private static Image mafiaImg = new ImageIcon("./images/mafia.jpg").getImage();
	private static Image chatBackImg = new ImageIcon("./images/day.gif").getImage();
	private static Image day = new ImageIcon("./images/day.gif").getImage();
	private static Image night = new ImageIcon("./images/night.gif").getImage();

	public GamePanel()
	{
		//Constructor
		setPanel();
		panel = this;
	}

	@Override
	public Dimension getSize()
	{
		return new Dimension(831, 593);
	}

	@Override
	public JPanel getPanel() //getInstance
	{
		if(panel != null)
		{
			return panel;
		}

		System.out.println("[error] panel is null");
		return new JPanel();
	}

	//����̹��� �ٲٱ�
	public void setImage(String msg) 
	{
		switch(msg) {
		case "�ù�": 
			img = citizenImg;
			break;

		case "����":
			img = policeImg;
			break;

		case "�ǻ�":
			img = doctorImg;
			break;

		case "���Ǿ�":
			img = mafiaImg;
			break;
		}
	}

	public Image getImage() 
	{
		if(img != null) 
		{
			return img;
		}
		else
			return img;
	}

	public Image getChatImage() 
	{
		return chatBackImg;
	}


	//�㳷 ����̹��� �ٲٱ�
	public void setChatImage(String msg) 
	{
		System.out.println("msg >>> : " + msg);
		switch(msg) 
		{
		case "/��":
			chatBackImg = day;
			chatTxtArea.setForeground(Color.BLACK);
			break;
		case "/��":
			chatBackImg = night;
			chatTxtArea.setForeground(Color.WHITE);
			break;
		default :
			
			break;
		}
	}

	//ä��â�� ä�� append
	public void appendChat(String msg)
	{
		System.out.println("[GamePanel] appendChat()");
		chatTxtArea.append(msg + "\n");
		scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
	}

	//���� �������� ���� ����Ʈ ����
	public void setUserList(String[] nicks)
	{
		userListTA.setText("[�������� ����]\n");
		for(int i=0; i<nicks.length; i++)
		{
			userListTA.append(nicks[i] + "\n");
		}
	}

	//�� ���� �ؽ�Ʈ set
	public void appendMyProfile(String txt)
	{
		myInfoTf.append(txt+"\n");
	}

	private static int timeCnt = 0;
	
	public void addTimeCnt(int inc)
	{
		timeCnt += inc;
		updateTimer();
	}
	
	public void resetTimer()
	{
		timeCnt = 0;
		updateTimer();
	}
	
	//Ÿ�̸� ������Ʈ
	private void updateTimer()
	{
		String fTime = String.valueOf(timeCnt); //����� Ÿ��

		if(timeCnt > 60)
		{
			int min = timeCnt / 60;
			String sec = (timeCnt % 60) < 10 ? "0" + timeCnt%60 : String.valueOf(timeCnt % 60);
			fTime = min + ":" + sec;
		}
		timer.setText(fTime);
	}
	
	public void sendMsg()
	{
		if(Client.getClient() != null)
		{
			System.out.println("���� �Է� >>> : " + sendMsgTF.getText() + "(Length >>> : " + sendMsgTF.getText().length() +")");
			Client.getClient().sendMsg(sendMsgTF.getText());
			sendMsgTF.setText("");
		}
		else
		{
			System.out.println("[GamePanel.sendMsg] client is null");
		}
	}

	@Override
	public void setPanel() 
	{
		setBounds(100, 100, 831, 593);
		setLayout(null);

		JPanel chatPanel = new JPanel();
		chatPanel.setBounds(86, 58, 317, 442);
		chatPanel.setLayout(null);
		add(chatPanel);

		sendMsgTF = new JTextField();
		sendMsgTF.setBounds(12, 411, 293, 21);
		sendMsgTF.setFocusable(true);
		sendMsgTF.setColumns(10);
		sendMsgTF.addKeyListener
		(
			new KeyListener() 
			{
				
				@Override
				public void keyTyped(KeyEvent e) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void keyReleased(KeyEvent e) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void keyPressed(KeyEvent e) {
					if(e.getKeyCode() == KeyEvent.VK_ENTER)
					{
						System.out.println("����Ű ����");
						sendMsg();
					}
				}
			}
		);
		
		chatPanel.add(sendMsgTF);

		chatTxtArea = new JTextArea()
		{
			//�̹����� �׸��� �޼���
			public void paintComponent(Graphics g) {
				Rectangle rect = getVisibleRect();
				g.drawImage(getChatImage(), rect.x, rect.y, 317, 442, null);
				setOpaque(false);
				super.paintComponent(g);
				repaint();
			}
		};
		chatTxtArea.setEditable(false);//�Է±���
		chatTxtArea.setLineWrap(true);//�ڵ��ٹٲ�

		scrollPane = new JScrollPane(chatTxtArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS , JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
		scrollPane.setBounds(12, 10, 293, 395);
		chatPanel.add(scrollPane);

		JPanel userListPanel = new JPanel();
		userListPanel.setBackground(SystemColor.menu);
		userListPanel.setBounds(413, 58, 322, 173);
		add(userListPanel);
		userListPanel.setLayout(null);

		userListTA = new JTextArea();
		userListTA.setBounds(12, 37, 298, 126);
		userListTA.setEditable(false);
		userListPanel.add(userListTA);

		textField = new JTextField();
		textField.setFont(UIManager.getFont("List.font"));
		textField.setHorizontalAlignment(SwingConstants.CENTER);
		textField.setText("���� ���� ���� ������");
		textField.setForeground(SystemColor.window);
		textField.setEditable(false);
		textField.setBackground(SystemColor.desktop);
		textField.setBounds(12, 10, 298, 26);
		userListPanel.add(textField);
		textField.setColumns(10);

		JPanel myInfoPanel = new JPanel();
		myInfoPanel.setLayout(null);
		myInfoPanel.setBackground(SystemColor.menu);
		myInfoPanel.setBounds(415, 241, 320, 126);
		add(myInfoPanel);

		myInfoTf = new JTextArea();
		myInfoTf.setBounds(12, 38, 298, 77);
		myInfoTf.setEditable(false);
		myInfoPanel.add(myInfoTf);

		JTextField myInfo = new JTextField();
		myInfo.setText("�� ����");
		myInfo.setHorizontalAlignment(SwingConstants.CENTER);
		myInfo.setForeground(Color.WHITE);
		myInfo.setFont(UIManager.getFont("List.font"));
		myInfo.setEditable(false);
		myInfo.setColumns(10);
		myInfo.setBackground(Color.BLACK);
		myInfo.setBounds(12, 10, 298, 26);
		myInfoPanel.add(myInfo);

		timer = new JTextField();
		timer.setHorizontalAlignment(SwingConstants.CENTER);
		timer.setEditable(false);
		timer.setText("Ÿ�̸�");
		timer.setFont(new Font("����", Font.PLAIN, 25));
		timer.setBounds(415, 380, 320, 120);
		add(timer);
		timer.setColumns(10);

		//����̹���
		imagePanel = new JPanel() {
			//�̹����� �׸��� �޼���
			public void paintComponent(Graphics g) {
				Dimension d = getSize();//ũ������(������ ũ�⿡ ���� ������ �ڵ� ������)
				g.drawImage(getImage() ,0,0,d.width, d.height, null);
				setOpaque(false);
				super.paintComponent(g);
				repaint();
			}
		};
		imagePanel.setBounds(0, 0, 815, 554);
		add(imagePanel);

	}


}
