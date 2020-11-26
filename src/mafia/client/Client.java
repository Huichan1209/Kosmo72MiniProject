package mafia.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import mafia.common.Command;
import mafia.common.Job;

public class Client 
{
	private static Client c = new Client();
	private static Socket socket;
	private static ClientFrame cf;
	public static boolean isAlive = true; //����ֳ�
	public static boolean isGaming = false; //�������ΰ��� ��Ÿ���� ���º��� (�ܺο��� ��������)
	public static boolean isDaytime = true; //���ΰ�
	public static boolean isTalkTime = false; //��ȭ�ð��ΰ�
	public static boolean isVoteTime = false; //��ǥ �ð��ΰ�
	public static int dayCnt = 1; //�������ΰ�
	
	public Client()
	{

	}
	
	public static Client getClient()
	{
		if(c == null)
		{
			c = new Client();
		}
		return c;
	}
	
	public void setSocket(Socket s)
	{
		this.socket = s;
	}
	
	public void setDayTime()
	{
		cf.getGamePanel().setChatImage("/��");
		cf.getGamePanel().appendChat("���� �Ǿ����ϴ�");
		cf.getGamePanel().resetTimer();
		isDaytime = true;
	}
	
	public void setNightTime()
	{
		cf.getGamePanel().setChatImage("/��");
		cf.getGamePanel().appendChat("���� �Ǿ����ϴ�");
		cf.getGamePanel().resetTimer();
		isDaytime = false;
	}
	
	public void startTalkTime()
	{
		isTalkTime = true;
		cf.getGamePanel().appendChat("���ݺ��� 2�а� ��ȭ�ð��Դϴ�.");
		cf.getGamePanel().resetTimer();
	}
	
	public void endTalkTime()
	{
		isTalkTime = false;
		cf.getGamePanel().appendChat("��ȭ�ð��� ����Ǿ����ϴ�.");
	}
	
	public void startVoteTime()
	{
		isVoteTime = true;
		cf.getGamePanel().appendChat("���ݺ��� 30�ʰ� ��ǥ�ð��Դϴ�.");
		cf.getGamePanel().resetTimer();
	}
	
	public void endVoteTime()
	{
		isVoteTime = false;
		cf.getGamePanel().appendChat("��ǥ�ð��� ����Ǿ����ϴ�.");
	}
	
	public void setJob(String jobName)
	{
		System.out.println("�� ���� >>> : " + jobName);
		cf.getGamePanel().appendChat("�� ������ " + jobName + "�Դϴ�!");
		cf.getGamePanel().appendMyProfile("�� ���� : " + jobName);
		cf.getGamePanel().setImage(jobName);
	}
	
	public void setUserNickList(String[] userNicks)
	{
		cf.getGamePanel().setUserList(userNicks);
	}
	
	public void startGame()
	{
		isGaming = true;
		System.out.println("���ӽ���");
		cf.getGamePanel().appendChat("������ ���۵Ǿ����ϴ�!");
		StringBuffer sb = new StringBuffer();
		sb.append("[��ɾ� ����Ʈ]\n");
		sb.append("[��ȭ�ð� ������] : /��ȭ����\n");
		sb.append("[��ǥ�ð� ������] : /��ǥ����\n");
		sb.append("[��ǥ�ϱ�] : /��ǥ [�г���]\n");
		sb.append("[ġ���ϱ�(�ǻ�)] : /ġ�� [�г���]\n");
		sb.append("[�����ϱ�(����)] : /���� [�г���]\n");
		sb.append("[�����ϱ�(���Ǿ�)] : /���� [�г���]\n");
		cf.getGamePanel().appendChat(sb.toString());
	}
	
	public void endGame()
	{
		isGaming = false;
		System.out.println("��������");
		cf.getGamePanel().appendChat("������ ����Ǿ����ϴ�!");
	}
	
	public void dead()
	{
		System.out.println("[Dead]");
		cf.getGamePanel().appendChat("����߽��ϴ�.");
		isAlive = false;
		//�״¼Ҹ� ��� ����
	}
	
	class ClientReceiveThread implements Runnable 
	{
		private Socket socket;
		
		public ClientReceiveThread(Socket socket) {
			this.socket = socket;
		}
		
		@Override
		public void run() 
		{
			try 
			{
				BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				while(true)
				{
					String msg = br.readLine();
					if(msg != null && msg.length() > 0)
					{
						if(Command.isServerCommand(msg))
						{
							//msg�� ��ɾ���
							System.out.println("������ɾ� �߰�");
							String cmd = msg.split(" ")[0];
							if(cmd.equals("/��"))
							{
								setDayTime();
							}
							else if(cmd.equals("/��"))
							{
								setNightTime();
							}
							else if(cmd.equals("/��ȭ�ð�����"))
							{
								startTalkTime();
							}
							else if(cmd.equals("/��ȭ�ð�����"))
							{
								endTalkTime();
							}
							else if(cmd.equals("/��ǥ�ð�����"))
							{
								startVoteTime();
							}
							else if(cmd.equals("/��ǥ�ð�����"))
							{
								endVoteTime();
							}
							else if(cmd.equals("/��������"))
							{
								String arg = msg.split(" ")[1];
								System.out.println("[��������] arg >>> : " + arg);
								if(arg != null && arg.length() > 0)
								{
									setJob(arg);
								}
								else
								{
									System.out.println("[��������] arg�� ��ȿ���� ���� arg >>> : " + arg);
								}
							}
							else if(cmd.equals("/��������Ʈ"))
							{
								String arg = msg.split(" ")[1];
								System.out.println("[��������Ʈ] arg >>> : " + arg);
								if(arg != null && arg.length() > 0)
								{
									String[] args = arg.split(",");
									setUserNickList(args);
								}
								else
								{
									System.out.println("[��������Ʈ] arg�� ��ȿ���� ���� arg >>> : " + arg);
								}
							}
							else if(cmd.equals("/���ӽ���"))
							{
								startGame();
							}
							else if(cmd.equals("/��������"))
							{
								endGame();
							}
							else if(cmd.equals("/���"))
							{
								dead();
							}
							else
							{
								System.out.println("�˼����� ��ɾ� cmd >>> : " + cmd);
								System.out.println("length >>> : " + cmd.length());
							}
						}
						else
						{
							System.out.println(msg);
							cf.getGamePanel().appendChat(msg);
						}
					}	
				}
			} 
			catch (Exception e) 
			{
				
			}
		}
	}
	
	public void sendMsg(String msg)
	{
		if(isAlive)
		{
			if(msg != null && msg.length() > 0)
			{
				//��ɾ� ����(/�� ������)�̶�� userCommand�̰ų� ��ȿ�� Ŀ��尡 �ƴϾ����
				if(Command.isLikeCommand(msg) && !Command.isUserCommand(msg))
				{
					cf.getGamePanel().appendChat("[�߸��� ��ɾ�] : " + msg + "�� ��ȿ�� ��ɾ �ƴմϴ�.");
				}
				else
				{
					BufferedWriter bw = null;
					
					try
					{
						bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
						bw.write(msg + "\n");
						bw.flush();
					}
					catch (Exception e) 
					{
						System.out.println("error >>> : " + e);
					}
				}
			}
			else
			{
				System.out.println("[Client.sendMsg()] >>> : msg is null");
			}
		}
		else
		{
			cf.getGamePanel().appendChat("��������� ���� �� �� �����ϴ�.");
		}
	}
	
	public void connectServer()
	{
		String ip = "127.0.0.1";
		int port = 1209;
		
		try 
		{
			Socket socket = new Socket(ip, port);
			
			setSocket(socket);
			
			ClientReceiveThread th1 = new ClientReceiveThread(socket);
			new Thread(th1).start();
		} 
		catch (Exception e) 
		{
			System.out.println("[mafia.client.Client.setSocket] >>> " + e.getMessage());
		}
	}
	
	class LocalTimer implements Runnable
	{
		private static final int SEC = 1000;
		
		@Override
		public void run() 
		{
			System.out.println("[LocalTimer] run() ����");
			try
			{
				while (true) 
				{						
					Thread.sleep(1*SEC);
					if(isGaming)
					{
						cf.getGamePanel().addTimeCnt(1);
					}
				}
			}
			catch (Exception e) 
			{
				System.out.println("[LocalTimer] error >>> : " + e);
			}
			
		}
	}
	
	public void setClientScr()
	{
		cf = new ClientFrame().getInstance();
		new Thread(new LocalTimer()).start();
	}
	
	public static void main(String[] args)
	{
		c = Client.getClient();
		c.setClientScr();
		c.connectServer();
		c.sendMsg("����");
	}

}
