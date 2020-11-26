package mafia.server;

import java.net.Socket;
import java.util.Date;

public class UserVO {
	
	private Socket m_socket; //���� ����
	private String m_Id; //���� ���̵�
	private Date entry_time; //���� ������ �ð�
	private int job; //����
	private boolean isAliveVar = true; //������ ����ֳ�(����)
	
	
	public UserVO()
	{
		System.out.println("[���] UserVO �⺻ �����ڷ� ��üȭ��");
	}//default Constructor
	
	public UserVO(Socket m_socket, String m_Id, Date entry_time, int job) 
	{
		this.m_socket = m_socket;
		this.m_Id = m_Id;
		this.entry_time = entry_time;
		this.job = job;
	}

	public boolean isAlive()
	{
		return isAliveVar;
	}
	
	public void killUser()
	{
		isAliveVar = false;
	}
		
	public void closeUserSocket()
	{
		try 
		{
			m_socket.close();
			m_socket = null;
		} 
		//IOException
		catch (Exception e) 
		{
			System.out.println("error >>> : " + e.getMessage());
		}
		finally
		{
			if(m_socket != null)
			{
				try {m_socket.close(); m_socket = null;} catch(Exception ignore) {}
			}
		}
		
		isAliveVar = false;
	}
	
	//getters
	public Socket getM_socket() {
		return m_socket;
	}

	public String getM_Id() {
		return m_Id;
	}

	public Date getEntry_time() {
		return entry_time;
	}
	
	public int getJob()
	{
		return job;
	}
	
	//setters
	public void setM_socket(Socket m_socket) {
		this.m_socket = m_socket;
	}

	public void setM_Id(String m_Id) {
		this.m_Id = m_Id;
	}

	public void setEntry_time(Date entry_time) {
		this.entry_time = entry_time;
	}
	
	public void setJob(int job)
	{
		this.job = job;
	}
}
