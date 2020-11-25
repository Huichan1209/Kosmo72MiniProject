package mafia.common;

import java.util.ArrayList;

class ServerCommand
{
	private ArrayList<String> cmdList = new ArrayList<String>();
	
	public ServerCommand() //Constructor
	{
		cmdList.add("/밤"); //밤으로 바꿈 : /밤
		cmdList.add("/낮"); //낮으로 바꿈 : /낮
		cmdList.add("/대화시간시작"); //대화시간 시작 : /대화시간시작
		cmdList.add("/대화시간종료"); //대화시간 종료 : /대화시간종료
		cmdList.add("/투표시간시작"); //투표시간 시작 : /투표시간시작
		cmdList.add("/투표시간종료"); //투표시간 종료 : /투표시간종료
		cmdList.add("/직업배정"); //클라이언트한테 직업 배정해줌 : /직업배정 [직업]
		cmdList.add("/유저리스트"); //클라이언트한테 유저리스트전달 : /유저리스트 [닉네임1,닉네임2,닉네임3]
		cmdList.add("/게임시작"); //클라이언트한테 게임시작을 알림 : /게임시작
		cmdList.add("/게임종료"); //클라이언트한테 게임종료를 알림 : /게임종료
	}
	
	public boolean isValidCmd(String cmd)
	{
		System.out.println("isValiedCmd 진입");
		for(int i=0; i < cmdList.size(); i++)
		{
			if(cmdList.get(i).equals(cmd))
			{
				return true;
			}
		}
		
		System.out.println("size >>> : " + cmdList.size());
		
		return false;
	}
}

class UserCommand
{
	private ArrayList<String> cmdList = new ArrayList<String>();
	
	public UserCommand() 
	{
		cmdList.add("/대화종료"); //공통 (대화시간에만) : /대화종료
		cmdList.add("/투표종료"); //공통 (투표시간에만) : /투표종료
		cmdList.add("/투표"); //공통 (투표시간에만) : /투표 [닉네임]
		cmdList.add("/치료"); //밤에 의사만 : /치료 [닉네임]
		cmdList.add("/조사"); //밤에 경찰만 : /조사 [닉네임]
		cmdList.add("/살인"); //밤에 마피아만 : /살인 [닉네임]
	}
	
	public boolean isValidCmd(String cmd)
	{	
		for(int i=0; i < cmdList.size(); i++)
		{
			if(cmdList.get(i).equals(cmd))
			{
				return true;
			}
		}
		
		System.out.println("size >>> : " + cmdList.size());
		
		return false;
	}
}

public abstract class Command
{	
	public static boolean isServerCommand(String cmd) //유효한 명령어인가?
	{
		boolean a = cmd.charAt(0) == '/'; //조건 a : 앞글자가 /로 시작하나
		if(a)
		{
			String[] tempArr = cmd.split(" "); //명령어가 투표 [닉네임] 이런식으로 올 수 있기 때문에 앞에만 뜯어서 유효한 명령어인지 검사
			cmd = tempArr[0];
			boolean b = new ServerCommand().isValidCmd(cmd);//조건 b : 프로그램에 등록된 명령어인가 
			
			return a && b; //두가지 조건 곱	
		}
		return false;
	}
	
	public static boolean isUserCommand(String cmd) //유효한 명령어인가?
	{
		boolean a = cmd.charAt(0) == '/'; //조건 a : 앞글자가 /로 시작하나
		if(a)
		{
			String[] tempArr = cmd.split(" "); //명령어가 투표 [닉네임] 이런식으로 올 수 있기 때문에 앞에만 뜯어서 유효한 명령어인지 검사
			cmd = tempArr[0];
			boolean b = new UserCommand().isValidCmd(cmd);//조건 b : 프로그램에 등록된 명령어인가 
			
			return a && b; //두가지 조건 곱	
		}
		return false;
	}
	
	public static boolean isCommand(String cmd) //유효한 명령어인가?
	{
		boolean a = cmd.charAt(0) == '/'; //조건 a : 앞글자가 /로 시작하나
		if(a)
		{
			String[] tempArr = cmd.split(" "); //명령어가 투표 [닉네임] 이런식으로 올 수 있기 때문에 앞에만 뜯어서 유효한 명령어인지 검사
			cmd = tempArr[0];
			System.out.println("cmd >>> : " + cmd);
			System.out.println("cmd.length >>> : " + cmd.length());
			boolean b = new ServerCommand().isValidCmd(cmd);
			System.out.println("중간");
			b = b || new UserCommand().isValidCmd(cmd);//조건 b : 프로그램에 등록된 명령어인가 
			System.out.println("끝");

			return b; //두가지 조건 곱	
		}
		
		System.out.println("끝");
		return false;
	}
}
