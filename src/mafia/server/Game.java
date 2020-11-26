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
				sm.sendMsgAll("/대화시간시작"); isTalkTime = true;
				//대화종료 커멘드를 입력받았을때 '외부에서' 타이머 종료 >>> : Timer.stopTimer(timerKey);
				
				currentTimerKey = Timer.createTimer(2 * MIN); //2분 대기
				while(true)
				{
					if(!Timer.isAlive(currentTimerKey)) //대화시간이 종료되었으면
					{
						sm.sendMsgAll("/대화시간종료"); isTalkTime = false;
						
						sm.sendMsgAll("/투표시간시작"); isVoteTime = true;
						//투표종료 커멘드를 입력받았을때 '외부에서' 타이머 종료 >>> : Timer.stopTimer(timerKey);
						currentTimerKey = Timer.createTimer(30 * SEC);
						while(true)
						{
							if(!Timer.isAlive(currentTimerKey))
							{
								sm.sendMsgAll("/투표시간종료"); isVoteTime = false;
								
								sm.sendMsgAll("/밤"); Game.getInstance().endNight();
								//밤에 할일이 끝나면 (마피아:살인, 의사:치료, 경찰:조사) 타이머 종료 >>> : Timer.stopTimer(timerKey);
								currentTimerKey = Timer.createTimer(1 * MIN);
								while(true)
								{
									if(!Timer.isAlive(currentTimerKey))
									{
										sm.sendMsgAll("/낮"); isDayTime = true;
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
	private static List<String> listOfConsentersToEndTalkTime = new ArrayList<String>(); //대화종료를 동의한 사람들 리스트
	private static List<String> listOfConsentersToEndVoteTime = new ArrayList<String>(); //투표종료를 동의한 사람들 리스트
	private static List<String> listOfVoteParticipants = new ArrayList<String>(); //투표에 참여한 사람 리스트
	private static Map<String, Integer> voteCntMap = new HashMap<String, Integer>(); //투표당한 수 ex)(닉네임1 : 3)
	private static String healedId = null; //치료받은 유저 id
	private static String investigatedId = null; //조사받은 유저 id
	private static Map<String, String> murderedIdMap = new HashMap<String, String>(); //살해당할 타겟으로 지목된 유저 id들 (마피아는 여러명일수도 있기 때문에 각자 지목한 걸 List에 담고 의견이 모두 일치하면 죽인다.)
	
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
			System.out.println("대화종료 동의 >>> : " + listOfConsentersToEndTalkTime.size() + "/" + Server.getServerManager().USER_MAX);
			Server.getServerManager().sendMsg(constenter_id, "대화종료에 동의했습니다.");
		}
		else
		{
			Server.getServerManager().sendMsg(constenter_id, "이미 대화종료에 동의했습니다.");
		}
	}
	
	private void addConsentersToEndVoteTime(String constenter_id)
	{
		if(constenter_id != null && constenter_id.length() > 0 && !listOfConsentersToEndVoteTime.contains(constenter_id))
		{
			listOfConsentersToEndVoteTime.add(constenter_id);
			System.out.println("투표종료 동의 >>> : " + listOfConsentersToEndVoteTime.size() + "/" + Server.getServerManager().USER_MAX);
		}
	}
	
	//투표 수 증가시키기
	public void incrVoteCnt(String part_id, String target_id) //투표 참여자 id, 타겟 id
	{
		//이미 투표했는지 여부 검사
		if(!listOfVoteParticipants.contains(part_id))
		{
			listOfVoteParticipants.add(part_id);
			int i = (voteCntMap.get(target_id) != null ? (voteCntMap.get(target_id)+1) : 1); //증가된 값
			voteCntMap.put(target_id, i);
			System.out.println(part_id + "가 " + target_id + "에게 투표함.");
			Server.getServerManager().sendMsg(part_id, target_id + "님에게 투표하였습니다.");
			System.out.println("투표 : " + listOfVoteParticipants.size() + "/" + Server.getServerManager().getUserList().size());
		}
	}
	
	private boolean ttListisFull() //TalkTime 동의자 리스트 꽉 찼는지 검사
	{
		return listOfConsentersToEndTalkTime.size() == Server.getServerManager().getUserList().size();
	}
	
	private boolean vtListisFull() //VoteTime 동의자 리스트 꽉 찼는지 검사
	{
		return listOfConsentersToEndTalkTime.size() == Server.getServerManager().getUserList().size();
	}
	
	private boolean voteIsDone() //투표가 완료되었는지 검사
	{
		System.out.println("[voteIsDone] voteCntMap.size() >>> : " + voteCntMap.size());
		System.out.println("[voteIsDone] AliveUserCnt >>> : " + Server.getServerManager().getAliveUserCnt());
		//버그 : voteCntMap의 사이즈가 1임
		return listOfVoteParticipants.size() == Server.getServerManager().getAliveUserCnt();
	}
	
	//대화시간 종료
	public void endTalk(String _id)
	{
		System.out.println("[Game.endTalk()] 진입");
		if(GameManager.isTalkTime)
		{
			addConsentersToEndTalkTime(_id);
			
			if(ttListisFull()) //모든 사람이 동의했다면
			{
				//대화시간 종료로직
				Timer.stopTimer(GameManager.getInstance().getCurrentTimerKey());
				GameManager.isTalkTime = false;
				listOfConsentersToEndTalkTime.clear();//초기화
			}
		}
		else
		{
			System.out.println("대화종료는 대화시간에만 입력할 수 있습니다.");
			Server.getServerManager().sendMsg(_id, "[잘못된 명령어] : 대화종료는 대화시간에만 입력할 수 있습니다.");
		}
	}
	
	//투표시간 종료
	public void endVote(String _id)
	{
		System.out.println("[Game.endVote()] 진입");
		if(GameManager.isVoteTime)
		{
			addConsentersToEndVoteTime(_id);
			
			if(vtListisFull()) //모든 사람이 동의했다면
			{
				//투표시간 종료 로직
				Timer.stopTimer(GameManager.getInstance().getCurrentTimerKey());
				GameManager.isVoteTime = false;
				listOfConsentersToEndVoteTime.clear();
			}
		}
		else
		{
			System.out.println("투표종료는 투표시간에만 입력할 수 있습니다.");
			Server.getServerManager().sendMsg(_id, "[잘못된 명령어] : 투표종료는 대화시간에만 입력할 수 있습니다.");
		}
	}
	
	public void endNight()
	{
		ServerManager sm = Server.getServerManager();
		
		//밤동안 일어난 일 처리
		//1. 마피아들이 죽이려는 대상이 모두 일치했는가
		boolean isCoinCide = true;
		String[] values = (String[])murderedIdMap.values().toArray();
		String targetId = "";
		for(int i=0; i<values.length; i++)
		{
			try
			{
				if(!values[i].equals(values[i+1]))
				{
					//일치하지 않는 값이 발견됬을때만 false
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
				//for문 종료
				break;
			}
		}
		
		//2.일치했다면 의사가 살린사람과 죽이려는 사람이 일치하는가? 아니면 죽이기
		if(isCoinCide)
		{
			if(healedId == null)
			{
				sm.kill(targetId);
			}
			else
			{
				if(targetId.equals(healedId)) //의사가 살렸다면
				{
					sm.sendMsgAll("마피아가 누군가를 죽이려했지만, 의사가 살렸습니다.");
				}
				else
				{
					sm.kill(targetId);
				}
			}
		}
		else
		{
			sm.sendMsgByJob(Job.JOB_MAFIA, "마피아들의 의견이 일치하지 않아 살인이 무효 되었습니다.");
		}
		
		//3.경찰이 조사한거 알려주기
		if(investigatedId != null && investigatedId.length() > 0)
		{
			if(sm.getJobById(investigatedId) == Job.JOB_MAFIA)
			{
				//조사한 사람이 마피아가 맞다면
				sm.sendMsgByJob(Job.JOB_POLICE, targetId + "님은 마피아가 맞습니다.");
			}
			else
			{
				sm.sendMsgByJob(Job.JOB_POLICE, targetId + "님은 마피아가 아닙니다.");
			}
		}
		
		//변수 초기화
		GameManager.isDayTime = false;
		healedId = null;
		investigatedId = null;
		murderedIdMap.clear();
	}
	
	//투표
	public void vote(String my_Id, String target_Id)
	{
		if(GameManager.isVoteTime)
		{
			System.out.println("[Game.vote()] 진입");
			if(validation(my_Id, target_Id))
			{
				incrVoteCnt(my_Id, target_Id);
				
				System.out.println("[vote] isDone >>> : " + voteIsDone());
				if(voteIsDone()) //투표가 완료되었다면
				{
					boolean isInvalidityVote = false; //투표에서 동표가 나왔나 (무효표)
					String maxKey = ""; //가장 많은 투표를 밭은 id
					int maxValue = 0; //가장 많은 투표 Cnt
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
					
					if(!isInvalidityVote) //무효표가 아니라면
					{
						String msg = "[투표]" + maxKey + "님이 " + maxValue + "표를 받아 " + "사망하셨습니다";
						Server.getServerManager().sendMsgAll(msg);
						Server.getServerManager().kill(maxKey);
					}
					else
					{
						String msg = "[투표] 무효표(동점)이 나와 무효가 되었습니다.";
					}
					
					//투표종료 로직
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
	
	//치료
	public void heal(String my_Id, String target_Id)
	{
		System.out.println("[Game.heal()] 진입");
		if(!GameManager.isDayTime)
		{
			if(validation(my_Id, target_Id)) //유효한 id들인지 검사
			{
				//치료를 명령한 사람이 의사인가 검사
				if(Job.JOB_DOCTOR == Server.getServerManager().getJobById(my_Id))
				{
					healedId = target_Id;
				}
				else
				{
					Server.getServerManager().sendMsg(my_Id, "[잘못된 명령어] : 치료는 의사만 할 수 있습니다.");
				}
			}
			else
			{
				System.out.println("[Game.vote()] error, my_Id >>> : " + my_Id + ", target_Id >>> : " + target_Id);
			}
		}
	}
	
	//조사
	public void investigate(String my_Id, String target_Id)
	{
		System.out.println("[Game.investigate()] 진입");
		if(!GameManager.isDayTime)
		{
			if(validation(my_Id, target_Id)) //유효한 id들인지 검사
			{
				//조사를 명령한 사람이 경찰인가 검사
				if(Job.JOB_POLICE == Server.getServerManager().getJobById(target_Id))
				{
					investigatedId = target_Id;
				}
				else
				{
					Server.getServerManager().sendMsg(my_Id, "[잘못된 명령어] : 조사는 경찰만 할 수 있습니다.");
				}
			}
			else
			{
				System.out.println("[Game.vote()] error, my_Id >>> : " + my_Id + ", target_Id >>> : " + target_Id);
			}
		}
	}
	
	//살인
	public void murder(String my_Id, String target_Id)
	{
		System.out.println("[Game.murder()] 진입");
		if(!GameManager.isDayTime)
		{
			if(validation(my_Id, target_Id)) //유효한 id들인지 검사
			{
				//살인을 명령한 사람이 마피아인지 검사
				if(Job.JOB_MAFIA == Server.getServerManager().getJobById(target_Id))
				{
					murderedIdMap.put(my_Id, target_Id);
				}
				else
				{
					Server.getServerManager().sendMsg(my_Id, "[잘못된 명령어] : 조사는 경찰만 할 수 있습니다.");
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
