package mafia.common;

import java.util.ArrayList;

class ServerCommand
{
	private ArrayList<String> cmdList = new ArrayList<String>();
	
	public ServerCommand() //Constructor
	{
		cmdList.add("/��"); //������ �ٲ� : /��
		cmdList.add("/��"); //������ �ٲ� : /��
		cmdList.add("/��ȭ�ð�����"); //��ȭ�ð� ���� : /��ȭ�ð�����
		cmdList.add("/��ȭ�ð�����"); //��ȭ�ð� ���� : /��ȭ�ð�����
		cmdList.add("/��ǥ�ð�����"); //��ǥ�ð� ���� : /��ǥ�ð�����
		cmdList.add("/��ǥ�ð�����"); //��ǥ�ð� ���� : /��ǥ�ð�����
		cmdList.add("/��������"); //Ŭ���̾�Ʈ���� ���� �������� : /�������� [����]
		cmdList.add("/��������Ʈ"); //Ŭ���̾�Ʈ���� ��������Ʈ���� : /��������Ʈ [�г���1,�г���2,�г���3]
		cmdList.add("/���ӽ���"); //Ŭ���̾�Ʈ���� ���ӽ����� �˸� : /���ӽ���
		cmdList.add("/��������"); //Ŭ���̾�Ʈ���� �������Ḧ �˸� : /��������
	}
	
	public boolean isValidCmd(String cmd)
	{
		System.out.println("isValiedCmd ����");
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
		cmdList.add("/��ȭ����"); //���� (��ȭ�ð�����) : /��ȭ����
		cmdList.add("/��ǥ����"); //���� (��ǥ�ð�����) : /��ǥ����
		cmdList.add("/��ǥ"); //���� (��ǥ�ð�����) : /��ǥ [�г���]
		cmdList.add("/ġ��"); //�㿡 �ǻ縸 : /ġ�� [�г���]
		cmdList.add("/����"); //�㿡 ������ : /���� [�г���]
		cmdList.add("/����"); //�㿡 ���ǾƸ� : /���� [�г���]
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
	public static boolean isServerCommand(String cmd) //��ȿ�� ��ɾ��ΰ�?
	{
		boolean a = cmd.charAt(0) == '/'; //���� a : �ձ��ڰ� /�� �����ϳ�
		if(a)
		{
			String[] tempArr = cmd.split(" "); //��ɾ ��ǥ [�г���] �̷������� �� �� �ֱ� ������ �տ��� �� ��ȿ�� ��ɾ����� �˻�
			cmd = tempArr[0];
			boolean b = new ServerCommand().isValidCmd(cmd);//���� b : ���α׷��� ��ϵ� ��ɾ��ΰ� 
			
			return a && b; //�ΰ��� ���� ��	
		}
		return false;
	}
	
	public static boolean isUserCommand(String cmd) //��ȿ�� ��ɾ��ΰ�?
	{
		boolean a = cmd.charAt(0) == '/'; //���� a : �ձ��ڰ� /�� �����ϳ�
		if(a)
		{
			String[] tempArr = cmd.split(" "); //��ɾ ��ǥ [�г���] �̷������� �� �� �ֱ� ������ �տ��� �� ��ȿ�� ��ɾ����� �˻�
			cmd = tempArr[0];
			boolean b = new UserCommand().isValidCmd(cmd);//���� b : ���α׷��� ��ϵ� ��ɾ��ΰ� 
			
			return a && b; //�ΰ��� ���� ��	
		}
		return false;
	}
	
	public static boolean isCommand(String cmd) //��ȿ�� ��ɾ��ΰ�?
	{
		boolean a = cmd.charAt(0) == '/'; //���� a : �ձ��ڰ� /�� �����ϳ�
		if(a)
		{
			String[] tempArr = cmd.split(" "); //��ɾ ��ǥ [�г���] �̷������� �� �� �ֱ� ������ �տ��� �� ��ȿ�� ��ɾ����� �˻�
			cmd = tempArr[0];
			System.out.println("cmd >>> : " + cmd);
			System.out.println("cmd.length >>> : " + cmd.length());
			boolean b = new ServerCommand().isValidCmd(cmd);
			System.out.println("�߰�");
			b = b || new UserCommand().isValidCmd(cmd);//���� b : ���α׷��� ��ϵ� ��ɾ��ΰ� 
			System.out.println("��");

			return b; //�ΰ��� ���� ��	
		}
		
		System.out.println("��");
		return false;
	}
}
