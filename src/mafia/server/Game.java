package mafia.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import mafia.common.Job;

class GameManager
{
	private static GameManager inst = new GameManager();
	private static UUID currentTimerKey;
	public static boolean isGaming = false;
	public static boolean isTalkTime = false;
	public static boolean isVoteTime = false;
	public static boolean isDayTime = true;
	
	private ServerManager sm = Server.getServerManager();
	
	private static final int SEC = 1000;
	private static final int MIN = 60 * SEC;
	
	public GameManager() 
	{
		
	}//end of Default Constructor
	
	public static GameManager getInstance()
	{
		if(inst != null)
		{
			return inst;
		}
		return new GameManager();
	}//end of getInstance();
	
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
	}//end of getCurrentTimerKey()
	
	public void game()
	{
		try
		{
			while(isGaming)
			{
				sm.sendMsgAll("/��ȭ�ð�����"); isTalkTime = true;
				//��ȭ���� Ŀ��带 �Է¹޾����� '�ܺο���' Ÿ�̸� ���� >>> : Timer.stopTimer(timerKey);
				
				currentTimerKey = Timer.createTimer(2 * MIN); //2�� ���
				while(true)
				{
					if(!Timer.isAlive(currentTimerKey)) //��ȭ�ð��� ����Ǿ�����
					{
						sm.sendMsgAll("/��ȭ�ð�����"); isTalkTime = false;
						
						sm.sendMsgAll("/��ǥ�ð�����"); isVoteTime = true;
						//��ǥ���� Ŀ��带 �Է¹޾����� '�ܺο���' Ÿ�̸� ���� >>> : Timer.stopTimer(timerKey);
						currentTimerKey = Timer.createTimer(30 * SEC);
						while(true)
						{
							if(!Timer.isAlive(currentTimerKey))
							{
								sm.sendMsgAll("/��ǥ�ð�����"); isVoteTime = false;
								
								sm.sendMsgAll("/��"); Game.getInstance().endNight();
								//�㿡 ������ ������ (���Ǿ�:����, �ǻ�:ġ��, ����:����) Ÿ�̸� ���� >>> : Timer.stopTimer(timerKey);
								currentTimerKey = Timer.createTimer(1 * MIN);
								while(true)
								{
									if(!Timer.isAlive(currentTimerKey))
									{
										sm.sendMsgAll("/��"); isDayTime = true;
										break;
									}
								}
								break;
							}
						}
						break;
					}
				}
			}//end of while
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}//end of Game()
}


class Game
{	
	private static Game inst = new Game();
	private static List<String> listOfConsentersToEndTalkTime = new ArrayList<String>(); //��ȭ���Ḧ ������ ����� ����Ʈ
	private static List<String> listOfConsentersToEndVoteTime = new ArrayList<String>(); //��ǥ���Ḧ ������ ����� ����Ʈ
	private static List<String> listOfVoteParticipants = new ArrayList<String>(); //��ǥ�� ������ ��� ����Ʈ
	private static Map<String, Integer> voteCntMap = new HashMap<String, Integer>(); //��ǥ���� �� ex)(�г���1 : 3)
	private static String healedId = null; //ġ����� ���� id
	private static String investigatedId = null; //������� ���� id
	private static Map<String, String> murderedIdMap = new HashMap<String, String>(); //���ش��� Ÿ������ ����� ���� id�� (���Ǿƴ� �������ϼ��� �ֱ� ������ ���� ������ �� List�� ��� �ǰ��� ��� ��ġ�ϸ� ���δ�.)
	
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
	
	private void addConsentersToEndTalkTime(String constenter_id)
	{
		if(constenter_id != null && constenter_id.length() > 0 && !listOfConsentersToEndTalkTime.contains(constenter_id))
		{
			listOfConsentersToEndTalkTime.add(constenter_id);
			System.out.println("��ȭ���� ���� >>> : " + listOfConsentersToEndTalkTime.size() + "/" + Server.getServerManager().USER_MAX);
			Server.getServerManager().sendMsg(constenter_id, "��ȭ���ῡ �����߽��ϴ�.");
		}
		else
		{
			Server.getServerManager().sendMsg(constenter_id, "�̹� ��ȭ���ῡ �����߽��ϴ�.");
		}
	}
	
	private void addConsentersToEndVoteTime(String constenter_id)
	{
		if(constenter_id != null && constenter_id.length() > 0 && !listOfConsentersToEndVoteTime.contains(constenter_id))
		{
			listOfConsentersToEndVoteTime.add(constenter_id);
			System.out.println("��ǥ���� ���� >>> : " + listOfConsentersToEndVoteTime.size() + "/" + Server.getServerManager().USER_MAX);
		}
	}
	
	//��ǥ �� ������Ű��
	public void incrVoteCnt(String part_id, String target_id) //��ǥ ������ id, Ÿ�� id
	{
		//�̹� ��ǥ�ߴ��� ���� �˻�
		if(!listOfVoteParticipants.contains(part_id))
		{
			listOfVoteParticipants.add(part_id);
			int i = (voteCntMap.get(target_id) != null ? (voteCntMap.get(target_id)+1) : 1); //������ ��
			voteCntMap.put(target_id, i);
			System.out.println(part_id + "�� " + target_id + "���� ��ǥ��.");
			Server.getServerManager().sendMsg(part_id, target_id + "�Կ��� ��ǥ�Ͽ����ϴ�.");
			System.out.println("��ǥ : " + listOfVoteParticipants.size() + "/" + Server.getServerManager().getUserList().size());
		}
	}
	
	private boolean ttListisFull() //TalkTime ������ ����Ʈ �� á���� �˻�
	{
		return listOfConsentersToEndTalkTime.size() == Server.getServerManager().getUserList().size();
	}
	
	private boolean vtListisFull() //VoteTime ������ ����Ʈ �� á���� �˻�
	{
		return listOfConsentersToEndTalkTime.size() == Server.getServerManager().getUserList().size();
	}
	
	private boolean voteIsDone() //��ǥ�� �Ϸ�Ǿ����� �˻�
	{
		System.out.println("[voteIsDone] voteCntMap.size() >>> : " + voteCntMap.size());
		System.out.println("[voteIsDone] AliveUserCnt >>> : " + Server.getServerManager().getAliveUserCnt());
		//���� : voteCntMap�� ����� 1��
		return listOfVoteParticipants.size() == Server.getServerManager().getAliveUserCnt();
	}
	
	//��ȭ�ð� ����
	public void endTalk(String _id)
	{
		System.out.println("[Game.endTalk()] ����");
		if(GameManager.isTalkTime)
		{
			addConsentersToEndTalkTime(_id);
			
			if(ttListisFull()) //��� ����� �����ߴٸ�
			{
				//��ȭ�ð� �������
				Timer.stopTimer(GameManager.getInstance().getCurrentTimerKey());
				GameManager.isTalkTime = false;
				listOfConsentersToEndTalkTime.clear();//�ʱ�ȭ
			}
		}
		else
		{
			System.out.println("��ȭ����� ��ȭ�ð����� �Է��� �� �ֽ��ϴ�.");
			Server.getServerManager().sendMsg(_id, "[�߸��� ��ɾ�] : ��ȭ����� ��ȭ�ð����� �Է��� �� �ֽ��ϴ�.");
		}
	}
	
	//��ǥ�ð� ����
	public void endVote(String _id)
	{
		System.out.println("[Game.endVote()] ����");
		if(GameManager.isVoteTime)
		{
			addConsentersToEndVoteTime(_id);
			
			if(vtListisFull()) //��� ����� �����ߴٸ�
			{
				//��ǥ�ð� ���� ����
				Timer.stopTimer(GameManager.getInstance().getCurrentTimerKey());
				GameManager.isVoteTime = false;
				listOfConsentersToEndVoteTime.clear();
			}
		}
		else
		{
			System.out.println("��ǥ����� ��ǥ�ð����� �Է��� �� �ֽ��ϴ�.");
			Server.getServerManager().sendMsg(_id, "[�߸��� ��ɾ�] : ��ǥ����� ��ȭ�ð����� �Է��� �� �ֽ��ϴ�.");
		}
	}
	
	public void endNight()
	{
		ServerManager sm = Server.getServerManager();
		
		//�㵿�� �Ͼ �� ó��
		//1. ���ǾƵ��� ���̷��� ����� ��� ��ġ�ߴ°�
		boolean isCoinCide = true;
		String[] values = (String[])murderedIdMap.values().toArray();
		String targetId = "";
		for(int i=0; i<values.length; i++)
		{
			try
			{
				if(!values[i].equals(values[i+1]))
				{
					//��ġ���� �ʴ� ���� �߰߉������� false
					System.out.println("[endNight()] values[i] >>> : " + values[i]);
					System.out.println("[endNight()] values[i+1] >>> : " + values[i+1]);
					isCoinCide = false;
				}
				else
				{
					targetId = values[i];
				}
			}
			catch(ArrayIndexOutOfBoundsException ignore)
			{
				//for�� ����
				break;
			}
		}
		
		//2.��ġ�ߴٸ� �ǻ簡 �츰����� ���̷��� ����� ��ġ�ϴ°�? �ƴϸ� ���̱�
		if(isCoinCide)
		{
			if(healedId == null)
			{
				sm.kill(targetId);
			}
			else
			{
				if(targetId.equals(healedId)) //�ǻ簡 ��ȴٸ�
				{
					sm.sendMsgAll("���Ǿư� �������� ���̷�������, �ǻ簡 ��Ƚ��ϴ�.");
				}
				else
				{
					sm.kill(targetId);
				}
			}
		}
		else
		{
			sm.sendMsgByJob(Job.JOB_MAFIA, "���ǾƵ��� �ǰ��� ��ġ���� �ʾ� ������ ��ȿ �Ǿ����ϴ�.");
		}
		
		//3.������ �����Ѱ� �˷��ֱ�
		if(investigatedId != null && investigatedId.length() > 0)
		{
			if(sm.getJobById(investigatedId) == Job.JOB_MAFIA)
			{
				//������ ����� ���Ǿư� �´ٸ�
				sm.sendMsgByJob(Job.JOB_POLICE, targetId + "���� ���Ǿư� �½��ϴ�.");
			}
			else
			{
				sm.sendMsgByJob(Job.JOB_POLICE, targetId + "���� ���Ǿư� �ƴմϴ�.");
			}
		}
		
		//���� �ʱ�ȭ
		GameManager.isDayTime = false;
		healedId = null;
		investigatedId = null;
		murderedIdMap.clear();
	}
	
	//��ǥ
	public void vote(String my_Id, String target_Id)
	{
		if(GameManager.isVoteTime)
		{
			System.out.println("[Game.vote()] ����");
			if(validation(my_Id, target_Id))
			{
				incrVoteCnt(my_Id, target_Id);
				
				System.out.println("[vote] isDone >>> : " + voteIsDone());
				if(voteIsDone()) //��ǥ�� �Ϸ�Ǿ��ٸ�
				{
					boolean isInvalidityVote = false; //��ǥ���� ��ǥ�� ���Գ� (��ȿǥ)
					String maxKey = ""; //���� ���� ��ǥ�� ���� id
					int maxValue = 0; //���� ���� ��ǥ Cnt
					for(int i=0; i<Server.getServerManager().getUserList().size(); i++)
					{
						if(Server.getServerManager().getUserList().get(i).isAlive()) 
						{
							String key = Server.getServerManager().getUserList().get(i).getM_Id();
							int value = voteCntMap.get(key) != null ? voteCntMap.get(key) : 0;
							if(value == maxValue)
							{
								isInvalidityVote = true;
							}
							if(value > maxValue)
							{
								isInvalidityVote = false;
								maxKey = key;
								maxValue = value;
							}
						}
					}
					
					if(!isInvalidityVote) //��ȿǥ�� �ƴ϶��
					{
						String msg = "[��ǥ]" + maxKey + "���� " + maxValue + "ǥ�� �޾� " + "����ϼ̽��ϴ�";
						Server.getServerManager().sendMsgAll(msg);
						Server.getServerManager().kill(maxKey);
					}
					else
					{
						String msg = "[��ǥ] ��ȿǥ(����)�� ���� ��ȿ�� �Ǿ����ϴ�.";
					}
					
					//��ǥ���� ����
					listOfVoteParticipants.clear();
					voteCntMap.clear();
					Timer.stopTimer(GameManager.getInstance().getCurrentTimerKey());
					GameManager.isVoteTime = false;
				}
			}
			else
			{
				System.out.println("[Game.vote()] error, my_Id >>> : " + my_Id + ", target_Id >>> : " + target_Id);
			}
		}
	}
	
	//ġ��
	public void heal(String my_Id, String target_Id)
	{
		System.out.println("[Game.heal()] ����");
		if(!GameManager.isDayTime)
		{
			if(validation(my_Id, target_Id)) //��ȿ�� id������ �˻�
			{
				//ġ�Ḧ ����� ����� �ǻ��ΰ� �˻�
				if(Job.JOB_DOCTOR == Server.getServerManager().getJobById(my_Id))
				{
					healedId = target_Id;
				}
				else
				{
					Server.getServerManager().sendMsg(my_Id, "[�߸��� ��ɾ�] : ġ��� �ǻ縸 �� �� �ֽ��ϴ�.");
				}
			}
			else
			{
				System.out.println("[Game.vote()] error, my_Id >>> : " + my_Id + ", target_Id >>> : " + target_Id);
			}
		}
	}
	
	//����
	public void investigate(String my_Id, String target_Id)
	{
		System.out.println("[Game.investigate()] ����");
		if(!GameManager.isDayTime)
		{
			if(validation(my_Id, target_Id)) //��ȿ�� id������ �˻�
			{
				//���縦 ����� ����� �����ΰ� �˻�
				if(Job.JOB_POLICE == Server.getServerManager().getJobById(target_Id))
				{
					investigatedId = target_Id;
				}
				else
				{
					Server.getServerManager().sendMsg(my_Id, "[�߸��� ��ɾ�] : ����� ������ �� �� �ֽ��ϴ�.");
				}
			}
			else
			{
				System.out.println("[Game.vote()] error, my_Id >>> : " + my_Id + ", target_Id >>> : " + target_Id);
			}
		}
	}
	
	//����
	public void murder(String my_Id, String target_Id)
	{
		System.out.println("[Game.murder()] ����");
		if(!GameManager.isDayTime)
		{
			if(validation(my_Id, target_Id)) //��ȿ�� id������ �˻�
			{
				//������ ����� ����� ���Ǿ����� �˻�
				if(Job.JOB_MAFIA == Server.getServerManager().getJobById(target_Id))
				{
					murderedIdMap.put(my_Id, target_Id);
				}
				else
				{
					Server.getServerManager().sendMsg(my_Id, "[�߸��� ��ɾ�] : ����� ������ �� �� �ֽ��ϴ�.");
				}
			}
			else
			{
				System.out.println("[Game.vote()] error, my_Id >>> : " + my_Id + ", target_Id >>> : " + target_Id);
			}
		}
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
		
		GameManager gm = GameManager.getInstance();
		gm.isGaming = true;
		gm.game();
	}
	
	public void stopGame()
	{
		GameManager.getInstance().isGaming = false;
		
	}
}//end of Game class
