package mafia.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

abstract class Timer
{
	private static Map<UUID,Thread> timerMap = new HashMap<UUID,Thread>(); //�����ִ� Ÿ�̸ӵ�
	private static Map<UUID,Integer> remainTimeMap = new HashMap<UUID,Integer>(); //���� �ð���
	
	public static UUID createTimer(int time) //Ÿ�̸Ӹ� ������ְ� ���߿� ������ �� �ֵ��� key�� �������
	{
		if(timerMap == null)
		{
			timerMap = new HashMap<UUID,Thread>();
		}
		if(remainTimeMap == null)
		{
			remainTimeMap = new HashMap<UUID,Integer>();
		}
		
		UUID key = UUID.randomUUID();
		Thread timer = new Thread(new TimerThread(key, time));
		timer.start();
		timerMap.put(key, timer);
		remainTimeMap.put(key, time);
		
		return key;
	}
	
	public static void stopTimer(UUID key)
	{
		System.out.println("[stopTimer]");
		if(timerMap.get(key) != null)
		{
			timerMap.get(key).interrupt();
			removeTimer(key);
		}
		else
		{
			System.out.println("[stopTimer] Thread is null");
		}
	}
	
	public static void removeTimer(UUID key)
	{
		try
		{
			System.out.println("[removeTimer] ����, Length >>> : " + timerMap.size());
			if(timerMap.get(key) != null)
			{
				timerMap.remove(key);
				remainTimeMap.remove(key);
			}
			else
			{
				System.out.println("[removeTimer] key�� ��ȿ���� ����");
			}

		}
		catch (NullPointerException e) 
		{
			System.out.println("[remvoeTimer] NullPointerException");
		}
	}
	
	public static boolean isAlive(UUID key)
	{
		if(timerMap.get(key) != null)
		{
			return timerMap.get(key).isAlive();
		}
		else
		{
			return false;
		}
	}
	
	public static void setRemainTime(UUID key, int remainTime)
	{
		remainTimeMap.put(key, remainTime);
	}
}

class TimerThread implements Runnable
{
	private static final int SEC = 1000;
	private static final int MIN = 60 * SEC;
	
	private UUID myKey;
	private int goalCnt;
	private int timeCnt = 0;
	
	public TimerThread(UUID myKey, int goalCnt)
	{
		this.myKey = myKey;
		this.goalCnt = goalCnt;
	}
	
	@Override
	public void run() 
	{
		System.out.println("run ����");
		try
		{
			while(timeCnt < goalCnt)
			{
				Thread.sleep(1000);
				timeCnt += 1 * SEC;
				Timer.setRemainTime(myKey, goalCnt - timeCnt); //���� �ð� �ܺο��� ������ �� �ֵ���
			}
		}
		catch (InterruptedException e) 
		{
			System.out.println("[TimerThread.run()] InterruptedException >>> : " + e);
		}
		
		System.out.println("[TimerThread] while�� ��������");
		//�۾� �������� ����
		Timer.removeTimer(myKey);
	}
}




