package mafia.client;

public class ServerTime 
{
	private int timeCnt = 0;
	private static ServerTime inst = new ServerTime();
	
	public ServerTime() 
	{
		
	}
	
	public static ServerTime getInstance()
	{
		if(inst == null)
		{
			inst = new ServerTime();
		}
		return inst;
	}
	
	public void startThread()
	{
		new Thread(new TimeSetter()).start();
	}
	
	public int getServerTimeCnt()
	{
		return timeCnt;
	}
	
	//서버에서 시간을 받아오는 비동기 쓰레드
	class TimeSetter implements Runnable
	{
		@Override
		public void run() 
		{
			try 
			{
				while(true)
				{
					if(Client.isGaming)
					{
						//실제로 여기에는 서버에서 돌리는 타이머를 받아오는 로직을 작성해야함. 
						//아직  서버가 미완성이니까 그냥 임시 타이머 구현함
						timeCnt++;
						//System.out.println("timeCnt >>> : " + timeCnt);
					}
					Thread.sleep(1 * 1000);
				}
			} 
			catch (Exception e) 
			{
				System.out.println("error >>> : " + e);
			}
		}
	}
}
