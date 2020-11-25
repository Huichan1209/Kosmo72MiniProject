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
	
	//�������� �ð��� �޾ƿ��� �񵿱� ������
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
						//������ ���⿡�� �������� ������ Ÿ�̸Ӹ� �޾ƿ��� ������ �ۼ��ؾ���. 
						//����  ������ �̿ϼ��̴ϱ� �׳� �ӽ� Ÿ�̸� ������
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
