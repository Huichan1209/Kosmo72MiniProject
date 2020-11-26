package mafia.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mafia.server.UserVO;

public class Job {
	public static final int JOB_CITIZEN = 0;
	public static final int JOB_POLICE = 1;
	public static final int JOB_DOCTOR = 2;
	public static final int JOB_MAFIA = 3;
	
	public static final String NAME_CITIZEN = "시민";
	public static final String NAME_POLICE = "경찰";
	public static final String NAME_DOCTOR = "의사";
	public static final String NAME_MAFIA = "마피아";
	
	public static final int CNT_CITIZEN = 4;
	public static final int CNT_POLICE = 1;
	public static final int CNT_DOCTOR = 1;
	public static final int CNT_MAFIA = 2;
	
	public static String getNameByIdx(int jobIdx)
	{
		HashMap<Integer, String> jobMap = new HashMap<Integer, String>();
		jobMap.put(JOB_CITIZEN, NAME_CITIZEN);
		jobMap.put(JOB_POLICE, NAME_POLICE);
		jobMap.put(JOB_DOCTOR, NAME_DOCTOR);
		jobMap.put(JOB_MAFIA, NAME_MAFIA);
		
		return jobMap.get(jobIdx);
	}
	
	//직업배열 렌덤 리턴
	public static void setRandomJOB_Arr(List<UserVO> userList)
	{		
		int cMax = CNT_CITIZEN; int pMax = CNT_POLICE; int dMax = CNT_DOCTOR; int mMax = CNT_MAFIA;
		int cCnt = 0; 			int pCnt = 0; 		   int dCnt = 0; 		  int mCnt = 0;
		
		int maxSum = (cMax + pMax + dMax + mMax);
		
		//총 배정되어야하는 직업의 수와 지금 접속해있는 유저의 수가 일치하는지 검사
		if(maxSum == userList.size())
		{
			ArrayList<Integer> jobList = new ArrayList<Integer>(); 
			
			while(true)
			{
				int r = (int)(Math.random() * 4);
				
				switch (r) {
				case Job.JOB_CITIZEN : 
					if(cCnt < cMax)
					{
						jobList.add(Job.JOB_CITIZEN);
						cCnt++;
						System.out.println("cCnt >>> : " + cCnt);
					}
					break;
					
				case Job.JOB_POLICE : 
					if(pCnt < pMax)
					{
						jobList.add(Job.JOB_POLICE);
						pCnt++;
						System.out.println("pCnt >>> : " + cCnt);
					}
					break;
					
				case Job.JOB_DOCTOR :
					if(dCnt < dMax)
					{
						jobList.add(Job.JOB_DOCTOR);
						dCnt++;
						System.out.println("dCnt >>> : " + cCnt);
					}
					break;
					
				case Job.JOB_MAFIA : 
					if(mCnt < mMax)
					{
						jobList.add(Job.JOB_MAFIA);
						mCnt++;
						System.out.println("mCnt >>> : " + cCnt);
					}
					break;

				default:
					System.out.println("Error r >>> : " + r);
					break;
				}
				
				boolean isDone = (cCnt == cMax && pCnt == pMax && dCnt == dMax && mCnt == mMax);  
				if(isDone) break;
			}//end of while
			
			//jobList와 userList의 size가 같은지 검사
			if(jobList.size() == userList.size())
			{
				System.out.println("직업 배정");
				
				for (int i = 0; i<jobList.size(); i++) 
				{
					userList.get(i).setJob(jobList.get(i));
					System.out.println("job(i) >>> : " + getNameByIdx(jobList.get(i)));
				}
			}
			else
			{
				System.out.println("[error] jobList와 userList의 size가 일치하지 않음");
				System.out.println("jobList.size() >>> : " + jobList.size());
				for (int i=0; i<userList.size(); i++) 
				{
					System.out.println(jobList.get(i));
				}
				System.out.println("userList.size() >>> : " + userList.size());
			}
			
			
		}
		else
		{
			StringBuffer errorMsgBuffer = new StringBuffer("[error] maxSum과 userList.size()가 일치하지 않음.\n>>> :");
			errorMsgBuffer.append("maxSum >>> : \n" + maxSum);
			errorMsgBuffer.append("userList.size() >>> : " + userList.size());
			
			System.out.println(errorMsgBuffer.toString());
		}		
		

	}
}
