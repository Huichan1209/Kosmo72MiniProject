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
								System.out.println("������ɾ� �߰�");
								System.out.println("id >>> : " + id);
								/*
								 ex) msg : /���� [�г���1]
								 	 cmd : /����
								 	 arg : [�г���1]
								*/
								
								String cmd = msg.split(" ")[0];
								String arg = msg.split(" ")[1];
				
								if(cmd.equals("/��ȭ����"))
								{
									Game.getInstance().endTalk();
								}
								else if(cmd.equals("/��ǥ����"))
								{
									Game.getInstance().endVote();
								}
								else if(cmd.equals("/��ǥ"))
								{
									Game.getInstance().vote(id, arg);
								}
								else if(cmd.equals("/ġ��"))
								{
									Game.getInstance().heal(id, arg);
								}
								else if(cmd.equals("/����"))
								{
									Game.getInstance().investigate(id, arg);
								}
								else if(cmd.equals("/����"))
								{
									Game.getInstance().murder(id, arg);
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
		String id = "user" + new SimpleDateFormat("HHmmssSSS").format(new Date()); //����ũ�� UserId ����
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
            if(uVO.isAlive())
            {
                //sm.sendMsg(uVO.getM_Id(), "�������� �Ҵ��� ���� id >>> : " + uVO.getM_Id());
            }
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






