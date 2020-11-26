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
	public static boolean isAlive = true; //살아있나
	public static boolean isGaming = false; //게임중인가를 나타내는 상태변수 (외부에서 참조가능)
	public static boolean isDaytime = true; //낮인가
	public static boolean isTalkTime = false; //대화시간인가
	public static boolean isVoteTime = false; //투표 시간인가
	public static int dayCnt = 1; //몇일차인가
	
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
		cf.getGamePanel().setChatImage("/낮");
		cf.getGamePanel().appendChat("낮이 되었습니다");
		cf.getGamePanel().resetTimer();
		isDaytime = true;
	}
	
	public void setNightTime()
	{
		cf.getGamePanel().setChatImage("/밤");
		cf.getGamePanel().appendChat("밤이 되었습니다");
		cf.getGamePanel().resetTimer();
		isDaytime = false;
	}
	
	public void startTalkTime()
	{
		isTalkTime = true;
		cf.getGamePanel().appendChat("지금부터 2분간 대화시간입니다.");
		cf.getGamePanel().resetTimer();
	}
	
	public void endTalkTime()
	{
		isTalkTime = false;
		cf.getGamePanel().appendChat("대화시간이 종료되었습니다.");
	}
	
	public void startVoteTime()
	{
		isVoteTime = true;
		cf.getGamePanel().appendChat("지금부터 30초간 투표시간입니다.");
		cf.getGamePanel().resetTimer();
	}
	
	public void endVoteTime()
	{
		isVoteTime = false;
		cf.getGamePanel().appendChat("투표시간이 종료되었습니다.");
	}
	
	public void setJob(String jobName)
	{
		System.out.println("내 직업 >>> : " + jobName);
		cf.getGamePanel().appendChat("내 직업은 " + jobName + "입니다!");
		cf.getGamePanel().appendMyProfile("내 직업 : " + jobName);
		cf.getGamePanel().setImage(jobName);
	}
	
	public void setUserNickList(String[] userNicks)
	{
		cf.getGamePanel().setUserList(userNicks);
	}
	
	public void startGame()
	{
		isGaming = true;
		System.out.println("게임시작");
		cf.getGamePanel().appendChat("게임이 시작되었습니다!");
		StringBuffer sb = new StringBuffer();
		sb.append("[명령어 리스트]\n");
		sb.append("[대화시간 끝내기] : /대화종료\n");
		sb.append("[투표시간 끝내기] : /투표종료\n");
		sb.append("[투표하기] : /투표 [닉네임]\n");
		sb.append("[치료하기(의사)] : /치료 [닉네임]\n");
		sb.append("[조사하기(경찰)] : /조사 [닉네임]\n");
		sb.append("[살인하기(마피아)] : /살인 [닉네임]\n");
		cf.getGamePanel().appendChat(sb.toString());
	}
	
	public void endGame()
	{
		isGaming = false;
		System.out.println("게임종료");
		cf.getGamePanel().appendChat("게임이 종료되었습니다!");
	}
	
	public void dead()
	{
		System.out.println("[Dead]");
		cf.getGamePanel().appendChat("사망했습니다.");
		isAlive = false;
		//죽는소리 재생 구현
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
							//msg가 명령어라면
							System.out.println("서버명령어 발견");
							String cmd = msg.split(" ")[0];
							if(cmd.equals("/낮"))
							{
								setDayTime();
							}
							else if(cmd.equals("/밤"))
							{
								setNightTime();
							}
							else if(cmd.equals("/대화시간시작"))
							{
								startTalkTime();
							}
							else if(cmd.equals("/대화시간종료"))
							{
								endTalkTime();
							}
							else if(cmd.equals("/투표시간시작"))
							{
								startVoteTime();
							}
							else if(cmd.equals("/투표시간종료"))
							{
								endVoteTime();
							}
							else if(cmd.equals("/직업배정"))
							{
								String arg = msg.split(" ")[1];
								System.out.println("[직업배정] arg >>> : " + arg);
								if(arg != null && arg.length() > 0)
								{
									setJob(arg);
								}
								else
								{
									System.out.println("[직업배정] arg가 유효하지 않음 arg >>> : " + arg);
								}
							}
							else if(cmd.equals("/유저리스트"))
							{
								String arg = msg.split(" ")[1];
								System.out.println("[유저리스트] arg >>> : " + arg);
								if(arg != null && arg.length() > 0)
								{
									String[] args = arg.split(",");
									setUserNickList(args);
								}
								else
								{
									System.out.println("[유저리스트] arg가 유효하지 않음 arg >>> : " + arg);
								}
							}
							else if(cmd.equals("/게임시작"))
							{
								startGame();
							}
							else if(cmd.equals("/게임종료"))
							{
								endGame();
							}
							else if(cmd.equals("/사망"))
							{
								dead();
							}
							else
							{
								System.out.println("알수없는 명령어 cmd >>> : " + cmd);
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
				//명령어 형식(/로 시작함)이라면 userCommand이거나 유효한 커멘드가 아니어야함
				if(Command.isLikeCommand(msg) && !Command.isUserCommand(msg))
				{
					cf.getGamePanel().appendChat("[잘못된 명령어] : " + msg + "는 유효한 명령어가 아닙니다.");
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
			cf.getGamePanel().appendChat("죽은사람은 말을 할 수 없습니다.");
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
			System.out.println("[LocalTimer] run() 진입");
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
		c.sendMsg("입장");
	}

}
