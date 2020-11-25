package mafia.server;

import java.sql.Time;
import java.util.UUID;

import mafia.common.Job;

class GameManager
{
	private static GameManager inst = new GameManager();
	private static UUID currentTimerKey;
	public static boolean isGaming = false;
	
	private ServerManager sm = Server.getServerManager();
	
	private static final int SEC = 1000;
	private static final int MIN = 60 * SEC;
	
	public GameManager() 
	{
		
	}
	
	public static GameManager getInstance()
	{
		if(inst != null)
		{
			return inst;
		}
		return new GameManager();
	}
	
	public UUID getCurrentTimerKey()
	{
		if(currentTimerKey != null)
		{
			return currentTimerKey;
		}
		else
		{
			System.out.println("error >>> : currentTimerKey is null");
			return null;
		}
	}
	
	public void game()
	{
		try
		{
			while(isGaming)
			{
				sm.sendMsgAll("/대화시간시작");
				//대화종료 커멘드를 입력받았을때 '외부에서' 타이머 종료 >>> : Timer.stopTimer(timerKey);
				currentTimerKey = Timer.createTimer(2 * MIN); //2분 대기
				while(true)
				{
					if(!Timer.isAlive(currentTimerKey)) //대화시간이 종료되었으면
					{
						sm.sendMsgAll("/대화시간종료");
						
						sm.sendMsgAll("/투표시간시작");
						//투표종료 커멘드를 입력받았을때 '외부에서' 타이머 종료 >>> : Timer.stopTimer(timerKey);
						currentTimerKey = Timer.createTimer(30 * SEC);
						while(true)
						{
							if(!Timer.isAlive(currentTimerKey))
							{
								sm.sendMsgAll("/투표시간종료");
								
								sm.sendMsgAll("/밤");
								//밤에 할일이 끝나면 (마피아:살인, 의사:치료, 경찰:조사) 타이머 종료 >>> : Timer.stopTimer(timerKey);
								System.out.println("밤 실행됨");
								currentTimerKey = Timer.createTimer(1 * MIN);
								System.out.println("타이머 만듬");
								while(true)
								{
									if(!Timer.isAlive(currentTimerKey))
									{
										sm.sendMsgAll("/낮");
										break;
									}
								}
								break;
							}
						}
						break;
					}
				}
			}

		}
		catch (Exception e) 
		{
			System.out.println("[GameManager.game()] error >>> : " + e);
		}
	}
}


class Game
{	
	private static Game inst = new Game();
	
	public Game()
	{
		
	}
	
	public static Game getInstance()
	{
		if(inst == null)
		{
			inst = new Game();
		}
		return inst;
	}
	
	public static GameManager getGameManager()
	{
		return GameManager.getInstance();
	}
	
	//대화종료
	public void endTalk()
	{
		System.out.println("[Game.endTalk()] 진입");
	}
	
	//투표종료
	public void endVote()
	{
		System.out.println("[Game.endVote()] 진입");
	}
	
	private boolean validateStr(String str)
	{
		return str != null && str.length() > 0;
	}
	
	private boolean validation(String my_Id, String target_Id)
	{
		boolean validation1 = validateStr(my_Id) && validateStr(target_Id);
		boolean validation2 = false;
		for(int i=0; i<Server.getServerManager().getUserList().size(); i++)
		{
			if(target_Id.equals(Server.getServerManager().getUserList().get(i).getM_Id()))
			{
				validation2 = true;
			}
		}
		
		return validation1 && validation2;
	}
	
	//투표
	public void vote(String my_Id, String target_Id)
	{
		System.out.println("[Game.vote()] 진입");
		if(validation(my_Id, target_Id))
		{
			//투표내용 구현
		}
		else
		{
			System.out.println("[Game.vote()] error, my_Id >>> : " + my_Id + ", target_Id >>> : " + target_Id);
		}
	}
	
	//치료
	public void heal(String my_Id, String target_Id)
	{
		System.out.println("[Game.heal()] 진입");
	}
	
	//조사
	public void investigate(String my_Id, String target_Id)
	{
		System.out.println("[Game.investigate()] 진입");
	}
	
	//살인
	public void murder(String my_Id, String target_Id)
	{
		System.out.println("[Game.murder()] 진입");
	}
	
	public void runGame()
	{
		System.out.println("runGame");
		Server.getServerManager().sendMsgAll("/게임시작"); //게임시작 명령어 전송
		//직업 배정
		Server.getServerManager().setJobs();
		//직업배정 명령어 날리기
		//명령어 구조 : /직업배정 [직업이름]
		Server.getServerManager().sendMsgByJob(Job.JOB_CITIZEN, "/직업배정 " + Job.getNameByIdx(Job.JOB_CITIZEN));
		Server.getServerManager().sendMsgByJob(Job.JOB_DOCTOR, "/직업배정 " + Job.getNameByIdx(Job.JOB_DOCTOR));
		Server.getServerManager().sendMsgByJob(Job.JOB_MAFIA, "/직업배정 " + Job.getNameByIdx(Job.JOB_MAFIA));
		Server.getServerManager().sendMsgByJob(Job.JOB_POLICE, "/직업배정 " + Job.getNameByIdx(Job.JOB_POLICE));
		
		GameManager gm = GameManager.getInstance();
		gm.isGaming = true;
		gm.game();
	}
	
	public void stopGame()
	{
		GameManager.getInstance().isGaming = false;
		
	}
}//end of Game class
