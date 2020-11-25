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
				sm.sendMsgAll("/��ȭ�ð�����");
				//��ȭ���� Ŀ��带 �Է¹޾����� '�ܺο���' Ÿ�̸� ���� >>> : Timer.stopTimer(timerKey);
				currentTimerKey = Timer.createTimer(2 * MIN); //2�� ���
				while(true)
				{
					if(!Timer.isAlive(currentTimerKey)) //��ȭ�ð��� ����Ǿ�����
					{
						sm.sendMsgAll("/��ȭ�ð�����");
						
						sm.sendMsgAll("/��ǥ�ð�����");
						//��ǥ���� Ŀ��带 �Է¹޾����� '�ܺο���' Ÿ�̸� ���� >>> : Timer.stopTimer(timerKey);
						currentTimerKey = Timer.createTimer(30 * SEC);
						while(true)
						{
							if(!Timer.isAlive(currentTimerKey))
							{
								sm.sendMsgAll("/��ǥ�ð�����");
								
								sm.sendMsgAll("/��");
								//�㿡 ������ ������ (���Ǿ�:����, �ǻ�:ġ��, ����:����) Ÿ�̸� ���� >>> : Timer.stopTimer(timerKey);
								System.out.println("�� �����");
								currentTimerKey = Timer.createTimer(1 * MIN);
								System.out.println("Ÿ�̸� ����");
								while(true)
								{
									if(!Timer.isAlive(currentTimerKey))
									{
										sm.sendMsgAll("/��");
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
		Server.getServerManager().sendMsgAll("/���ӽ���"); //���ӽ��� ��ɾ� ����
		//���� ����
		Server.getServerManager().setJobs();
		//�������� ��ɾ� ������
		//��ɾ� ���� : /�������� [�����̸�]
		Server.getServerManager().sendMsgByJob(Job.JOB_CITIZEN, "/�������� " + Job.getNameByIdx(Job.JOB_CITIZEN));
		Server.getServerManager().sendMsgByJob(Job.JOB_DOCTOR, "/�������� " + Job.getNameByIdx(Job.JOB_DOCTOR));
		Server.getServerManager().sendMsgByJob(Job.JOB_MAFIA, "/�������� " + Job.getNameByIdx(Job.JOB_MAFIA));
		Server.getServerManager().sendMsgByJob(Job.JOB_POLICE, "/�������� " + Job.getNameByIdx(Job.JOB_POLICE));
		
		gm = new GameManager();
		gm.isGaming = true;
		gm.game();
	}
	
	public void stopGame()
	{
		gm.isGaming = false;
		
	}
}//end of Game class
