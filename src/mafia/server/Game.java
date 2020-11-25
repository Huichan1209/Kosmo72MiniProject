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
	
	public GameManager getInstance()
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
	private GameManager gm;
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
		
		gm = new GameManager();
		gm.isGaming = true;
		gm.game();
	}
	
	public void stopGame()
	{
		gm.isGaming = false;
		
	}
}//end of Game class
