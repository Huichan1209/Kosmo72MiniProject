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
	//접속한 유저 DTO List
    private static List<UserVO> userList;
    //여러명의 유저한테 입력을 받아야하기 때문에 받는 쓰레드가 여러개 있어야함
    private static List<Thread> receiveThreadList;
    
    //상수
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
		//방이 꽉 찼나 boolean값 리턴
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
			System.out.println("유저가 가득차서 유저를 입장시킬 수 없음");
			userVO.closeUserSocket(); //유저 내보내기
		}
	}
	
	//유저가 나갔을 때 
    public void removeUser(Socket socket)
    {  
       for(int i=0; i<userList.size(); i++)
       {
    	   if(userList.get(i).getM_socket() == socket)
    	   {
    		   System.out.println(userList.get(i).getM_Id() + "유저리스트에서 제거");
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
               //임시로 소켓 id를 닉네임으로 사용함
               //DB연동이 구현되면 닉네임을 가져와서 사용함
               userNicks += userList.get(i).getM_Id() + ","; //닉네임1,닉네임2,닉네임3,... 형태로 만듬
            }
            userNicks = userNicks.substring(0, userNicks.length()-1); //마지막 ,제거
            sendMsgAll("/유저리스트 " + userNicks); //클라이언트에 명령어 전송
           
            System.out.println("현재 유저 수 >> : " + userList.size() + "/" + USER_MAX +"명");
            System.out.println("userList.size() >>> : " + userList.size());
            System.out.println("receiveThreadList.size() >>> : " + receiveThreadList.size());
    	}
    }
	
	//전역변수 userList에 직업 세팅
	public void setJobs()
	{
		Job.setRandomJOB_Arr(userList);
	}
	
	//특정 id를 가진 유저에게만 메시지 전송
	public void sendMsg(String _id, String _msg)
	{
		//유저들 중 m_id가 id와 일치하는 애한테 msg를 전송함
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
	
	//모든 유저에게 메시지 전송 (공지)
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
	
	//특정 직업을 가진 유저에게만 메시지 전송
	public void sendMsgByJob(int _job, String _msg)
	{
		//유저들 중 job이 _job과 일치하는 애들한테 msg를 전송함
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
								System.out.println("유저명령어 발견");
								System.out.println("id >>> : " + id);
								/*
								 ex) msg : /살인 [닉네임1]
								 	 cmd : /살인
								 	 arg : [닉네임1]
								*/
								
								String cmd = msg.split(" ")[0];
								String arg = msg.split(" ")[1];
				
								if(cmd.equals("/대화종료"))
								{
									Game.getInstance().endTalk();
								}
								else if(cmd.equals("/투표종료"))
								{
									Game.getInstance().endVote();
								}
								else if(cmd.equals("/투표"))
								{
									Game.getInstance().vote(id, arg);
								}
								else if(cmd.equals("/치료"))
								{
									Game.getInstance().heal(id, arg);
								}
								else if(cmd.equals("/조사"))
								{
									Game.getInstance().investigate(id, arg);
								}
								else if(cmd.equals("/살인"))
								{
									Game.getInstance().murder(id, arg);
								}
							}
							else
							{
								//유저가 서버커멘드를 입력해서 게임을 조종할 수 있기때문에 걸러줌
								if(!Command.isServerCommand(msg)) 
								{
									String text = "[" + id + "]" + " : " + msg; // >>> [닉네임] : (채팅내용)
									sendMsgAll(text);
								}
								else
								{
									System.out.println("[ReceiverThread.run()] error >>> : 유저가 서버커멘드를 입력함");
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
	//실제 게임을 돌리는 역할을 하는 서버매니저와 game을 싱글톤으로 관리
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
		//유니크한 규칙으로 UserVO를 생성하고 초기화해서 리턴함
		UserVO uVO = new UserVO();
		uVO.setM_socket(socket);
		//유니크한 규칙을 아직 확정하지 못해서 임시로 user + 시간으로 해놓았음
		String id = "user" + new SimpleDateFormat("HHmmssSSS").format(new Date()); //유니크한 UserId 생성
		uVO.setM_Id(id);
		uVO.setEntry_time(new Date());
		return uVO;
	}
	
	private void waitUser(ServerManager sm, int port) throws Exception
	{
		ServerSocket ss = new ServerSocket(port);
		System.out.println("[mafia.server.Server] >>> : 시작");
		while(!sm.isRoomFull())
		{
			Socket socket = ss.accept(); //클라이언트 연결요청 대기
			System.out.println(socket.getRemoteSocketAddress() + " : 연결");
			UserVO uVO = Server.getUserVO(socket); //UserVO 생성
            sm.addUser(uVO);
            if(uVO.isAlive())
            {
                //sm.sendMsg(uVO.getM_Id(), "서버에서 할당한 나의 id >>> : " + uVO.getM_Id());
            }
		}
		System.out.println("while문 빠져나옴 (유저가 " + sm.USER_MAX + "명 만큼 들어옴)");
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
			//이 아래 로직이 실행된다는건 유저가 다 들어왔다는 뜻임. 즉 게임 시작할 준비 되었음을 의미함			
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






