package mafia.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mafia.common.Command;
import mafia.common.Job;
import mafia.server.UserVO;

class ServerManager
{
	//������ ���� DTO List
    private static List<UserVO> userList;
    //�������� �������� �Է��� �޾ƾ��ϱ� ������ �޴� �����尡 ������ �־����
    private static List<Thread> receiveThreadList;
    
    //���
    public static final int USER_MAX = 8;
    
    
	public ServerManager()
	{
		userList = new ArrayList<UserVO>();
		receiveThreadList = new ArrayList<Thread>();
	}
	
	public List<UserVO> getUserList()
	{
		if(userList != null && userList.size() > 0)
		{
			return userList;
		}
		
		return new ArrayList<UserVO>();
	}
	
	public boolean isRoomFull()
	{
		//���� �� á�� boolean�� ����
		boolean isFull = (this.userList.size() >= USER_MAX);
		return isFull;
	}
	
	public void addUser(UserVO userVO)
	{
		if(!isRoomFull())
		{
			this.userList.add(userVO);
			Thread recThread = new Thread(new ReceiverThread(userVO));
			recThread.start();
			this.receiveThreadList.add(recThread);
			
			sendUserList(userVO.getM_socket());
		}
		else
		{
			System.out.println("������ �������� ������ �����ų �� ����");
			userVO.closeUserSocket(); //���� ��������
		}
		
		System.out.println("UserList.size >>> : " + userList.size());
		System.out.println("receiveThreadList.size >>> : " + receiveThreadList.size());
	}
	
	//������ ������ �� 
    public void removeUser(Socket socket)
    {  
       for(int i=0; i<userList.size(); i++)
       {
    	   if(userList.get(i).getM_socket() == socket)
    	   {
    		   System.out.println(userList.get(i).getM_Id() + "��������Ʈ���� ����");
    		   userList.remove(i);
               receiveThreadList.get(i).interrupt();;
               receiveThreadList.remove(i);
               sendUserList(socket);
    	   }
       }  
    }
    
    public void sendUserList(Socket socket)
    {
    	if(userList != null && userList.size() > 0)
    	{
            String userNicks = "";
            for(int i=0; i<userList.size(); i++)
            {
               //�ӽ÷� ���� id�� �г������� �����
               //DB������ �����Ǹ� �г����� �����ͼ� �����
               userNicks += userList.get(i).getM_Id() + ","; //�г���1,�г���2,�г���3,... ���·� ����
            }
            userNicks = userNicks.substring(0, userNicks.length()-1); //������ ,����
            sendMsgAll("/��������Ʈ " + userNicks); //Ŭ���̾�Ʈ�� ��ɾ� ����
           
            System.out.println("���� ���� �� >> : " + userList.size() + "/" + USER_MAX +"��");
            System.out.println("userList.size() >>> : " + userList.size());
            System.out.println("receiveThreadList.size() >>> : " + receiveThreadList.size());
    	}
    }
	
	//�������� userList�� ���� ����
	public void setJobs()
	{
		Job.setRandomJOB_Arr(userList);
		System.out.println("�ù� >>> : " + getAliveCitizenCnt());
		System.out.println("���� >>> : " + getAlivePoliceCnt());
		System.out.println("�ǻ� >>> : " + getAliveDoctorCnt());
		System.out.println("���Ǿ� >>> : " + getAliveMafiaCnt());
	}
	
	public int getAliveUserCnt() //����ִ� ��ü ���� ��
	{
		int cnt = 0;
		for(int i=0; i<userList.size(); i++)
		{
			if(userList.get(i).isAlive())
			{
				cnt++;
			}
		}
		return cnt;
	}
	
	public int getAliveCitizenCnt() //��Ƴ��� �ù� ��
	{
		int cnt = 0;
		for(int i=0; i<userList.size(); i++)
		{
			//������ �ù��̸鼭 �����ִ� ���
			if(userList.get(i).getJob() == Job.JOB_CITIZEN && userList.get(i).isAlive())
			{
				cnt++;
			}
		}
		return cnt;
	}
	
	public int getAlivePoliceCnt()
	{
		int cnt = 0;
		for(int i=0; i<userList.size(); i++)
		{
			//������ �����̸鼭 �����ִ� ���
			if(userList.get(i).getJob() == Job.JOB_POLICE && userList.get(i).isAlive())
			{
				cnt++;
			}
		}
		return cnt;
	}
	
	public int getAliveDoctorCnt()
	{
		int cnt = 0;
		for(int i=0; i<userList.size(); i++)
		{
			//������ �ǻ��̸鼭 �����ִ� ���
			if(userList.get(i).getJob() == Job.JOB_DOCTOR && userList.get(i).isAlive())
			{
				cnt++;
			}
		}
		return cnt;
	}
	
	public int getAliveMafiaCnt()
	{
		int cnt = 0;
		for(int i=0; i<userList.size(); i++)
		{
			//������ ���Ǿ��̸鼭 �����ִ� ���
			if(userList.get(i).getJob() == Job.JOB_MAFIA && userList.get(i).isAlive())
			{
				cnt++;
			}
		}
		return cnt;
	}
	
	public int getAliveCntByJob(int job)
	{
		int cnt = 0;
		for(int i=0; i<userList.size(); i++)
		{
			boolean isTarget = (userList.get(i).getJob() == job && userList.get(i).isAlive());
			if(isTarget)
			{
				cnt++;
			}
		}
		return cnt;
	}
	
	public String[] getAliveIdArrByJob(int job)
	{
		ArrayList<String> idStack = new ArrayList<String>();
		for(int i=0; i<userList.size(); i++)
		{
			boolean isTarget = (job == userList.get(i).getJob() && userList.get(i).isAlive());
			if(isTarget)
			{
				idStack.add(userList.get(i).getM_Id());
			}
		}
		
		return (String[])idStack.toArray();
	}
	
	//Ư�� id�� ���� �������Ը� �޽��� ����
	public void sendMsg(String _id, String _msg)
	{
		//������ �� m_id�� id�� ��ġ�ϴ� ������ msg�� ������
		for(int i=0; i<userList.size(); i++)
		{
			boolean isTarget = (userList.get(i).getM_Id().equals(_id));
			if(isTarget)
			{
				try 
				{
					BufferedWriter bw = new BufferedWriter
							(new OutputStreamWriter(userList.get(i).getM_socket().getOutputStream()));
					
					bw.write(_msg + "\n");
					bw.flush();
				} 
				//IOEXception
				catch (Exception e) 
				{
					System.out.println("[ServerManager.sendMsg()] error >>> : " + e.getMessage());
				}
			}
		}
	}
	
	//��� �������� �޽��� ���� (����)
	public void sendMsgAll(String _msg)
	{
		if(userList != null && userList.size() > 0)
		{
			System.out.println("[sendMsgAll] _msg >>> : " + _msg);
			
			for(int i=0; i<userList.size(); i++)
			{
				try 
				{
					BufferedWriter bw = new BufferedWriter
							(new OutputStreamWriter(userList.get(i).getM_socket().getOutputStream()));
					bw.write(_msg + "\n");
					bw.flush();
				} 
				catch (Exception e) 
				{
					System.out.println("[ServerManger.sendMsgAll()] error >>> : " + e);
				}
			}
		}
	}
	
	//Ư�� ������ ���� �������Ը� �޽��� ����
	public void sendMsgByJob(int _job, String _msg)
	{
		//������ �� job�� _job�� ��ġ�ϴ� �ֵ����� msg�� ������
		for(int i=0; i<userList.size(); i++)
		{
			boolean isTarget = (userList.get(i).getJob() == _job);
			if(isTarget)
			{
				try 
				{
					BufferedWriter bw = new BufferedWriter
							(new OutputStreamWriter(userList.get(i).getM_socket().getOutputStream()));
					
					bw.write(_msg + "\n");
					bw.flush();
				} 
				//IOEXception
				catch (Exception e) 
				{
					System.out.println("[sendMsgByJob] error >>> : " + e.getMessage());
				}
			}
		}
	}
	
	public void kill(String id)
	{
		for(int i=0; i<userList.size(); i++)
		{
			boolean isTarget = (userList.get(i).getM_Id().equals(id));
			if(isTarget)
			{
				userList.get(i).killUser();
				sendMsg(id, "/���");
			}
		}
	}
	
	public int getJobById(String id)
	{
		for(int i=0; i<userList.size(); i++)
		{
			boolean isTarget = (userList.get(i).getM_Id().equals(id));
			if(isTarget)
			{
				return userList.get(i).getJob();
			}
		}
		
		System.out.println("[getJobById] error");
		return 0;
	}
	
	class ReceiverThread implements Runnable
	{
		private Socket socket;
		private String id;
		
		public ReceiverThread(UserVO uvo)
		{
			System.out.println("[ReceiverThread] Constructor");
			this.socket = uvo.getM_socket();
			this.id = uvo.getM_Id();
		}
		
		@Override
		public void run()
		{
			try
			{
				BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				while(true)
				{
					if(br != null)
					{
						String msg = br.readLine();
						if(msg != null && msg.length() > 0)
						{
							if(Command.isUserCommand(msg))
							{
								System.out.println("������ɾ� �߰� >>> : " + msg);
								System.out.println("id >>> : " + id);
								/*
								 ex) msg : /���� [�г���1]
								 	 cmd : /����
								 	 arg : [�г���1]
								*/
								
								String cmd = msg.split(" ")[0];
								String arg = null;
								try
								{
									if(msg.split(" ").length > 1)
									{
										System.out.println("msg.split(\" \").length >>> : " + msg.split(" ").length);
										arg = msg.split(" ")[1];
									}
								}
								catch (ArrayIndexOutOfBoundsException e) 
								{
									System.out.println("arg is null");
									sendMsg(id, "[�߸��� ��ɾ�] : �г����� ��ȿ���� �ʽ��ϴ�. ��Ȯ�� �Է����ּ���");
								}
								
								if(cmd.equals("/��ȭ����"))
								{
									Game.getInstance().endTalk(id);
								}
								else if(cmd.equals("/��ǥ����"))
								{
									Game.getInstance().endVote(id);
								}
								else if(cmd.equals("/��ǥ"))
								{
									if(arg != null && arg.length() > 0)
									{
										Game.getInstance().vote(id, arg);
									}
									else
									{
										System.out.println("[��ǥ] arg�� ��ȿ���� ���� arg >>> : " + arg);
										sendMsg(id, "[�߸��� ��ɾ�] : �г����� ��ȿ���� �ʽ��ϴ�. ��Ȯ�� �Է����ּ���");
									}
								}
								else if(cmd.equals("/ġ��"))
								{
									if(arg != null && arg.length() > 0)
									{
										Game.getInstance().heal(id, arg);
									}
									else
									{
										System.out.println("[ġ��] arg�� ��ȿ���� ���� arg >>> : " + arg);
										sendMsg(id, "[�߸��� ��ɾ�] : �г����� ��ȿ���� �ʽ��ϴ�. ��Ȯ�� �Է����ּ���");
									}
								}
								else if(cmd.equals("/����"))
								{
									if(arg != null && arg.length() > 0)
									{
										Game.getInstance().investigate(id, arg);
									}
									else
									{
										System.out.println("[ġ��] arg�� ��ȿ���� ���� arg >>> : " + arg);
										sendMsg(id, "[�߸��� ��ɾ�] : �г����� ��ȿ���� �ʽ��ϴ�. ��Ȯ�� �Է����ּ���");
									}
								}
								else if(cmd.equals("/����"))
								{
									if(arg != null && arg.length() > 0)
									{
										Game.getInstance().investigate(id, arg);
									}
									else
									{
										System.out.println("[����] arg�� ��ȿ���� ���� arg >>> : " + arg);
										sendMsg(id, "[�߸��� ��ɾ�] : �г����� ��ȿ���� �ʽ��ϴ�. ��Ȯ�� �Է����ּ���");
									}								
								}
							}
							else
							{
								//������ ����Ŀ��带 �Է��ؼ� ������ ������ �� �ֱ⶧���� �ɷ���
								if(!Command.isServerCommand(msg)) 
								{
									String text = "[" + id + "]" + " : " + msg; // >>> [�г���] : (ä�ó���)
									sendMsgAll(text);
								}
								else
								{
									System.out.println("[ReceiverThread.run()] error >>> : ������ ����Ŀ��带 �Է���");
								}
							}
						}
					}
				}
			}
			catch (IOException e) 
			{
				System.out.println("[ReceiverThread.run.catch()] error >>> : " + e);
				removeUser(socket);
			}
		}
	}
}

public class Server
{
	//���� ������ ������ ������ �ϴ� �����Ŵ����� game�� �̱������� ����
	private static ServerManager sm = new ServerManager();
	
	public static ServerManager getServerManager()
	{
		if(sm == null)
		{
			return new ServerManager();
		}
		return sm;
	}
	
	public static UserVO getUserVO(Socket socket)
	{
		//����ũ�� ��Ģ���� UserVO�� �����ϰ� �ʱ�ȭ�ؼ� ������
		UserVO uVO = new UserVO();
		uVO.setM_socket(socket);
		//����ũ�� ��Ģ�� ���� Ȯ������ ���ؼ� �ӽ÷� user + �ð����� �س�����
		String id = "user" + new SimpleDateFormat("ssSSS").format(new Date()); //����ũ�� UserId ����
		uVO.setM_Id(id);
		uVO.setEntry_time(new Date());
		return uVO;
	}
	
	private void waitUser(ServerManager sm, int port) throws Exception
	{
		ServerSocket ss = new ServerSocket(port);
		System.out.println("[mafia.server.Server] >>> : ����");
		while(!sm.isRoomFull())
		{
			Socket socket = ss.accept(); //Ŭ���̾�Ʈ �����û ���
			System.out.println(socket.getRemoteSocketAddress() + " : ����");
			UserVO uVO = Server.getUserVO(socket); //UserVO ����
            sm.addUser(uVO);
		}
		System.out.println("while�� �������� (������ " + sm.USER_MAX + "�� ��ŭ ����)");
		System.out.println("isRoomFull >>> : " + sm.isRoomFull());
	}//end of waitUser() method
	
	private void runServer()
	{
		if(sm == null)
		{
			sm = new ServerManager();
		}
		
		int port = 1209;
		try 
		{
			waitUser(sm, port);
			//�� �Ʒ� ������ ����ȴٴ°� ������ �� ���Դٴ� ����. �� ���� ������ �غ� �Ǿ����� �ǹ���			
			Game.getInstance().runGame();
		} 
		catch (Exception e)
		{
			System.out.println("[Server.runServer()] error >>> : " + e.getMessage());
		}
	}//end of serverStart() method
	
	
	public static void main(String[] args)
	{
		new Server().runServer();
	}
	
}//end of Server class






