package mafia.client.panel;

import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class LoadingPanel extends JPanel implements Panel
{
	private JPanel panel;
	
	public LoadingPanel()
	{
		//Constructor
		panel = new JPanel();
	}
	
	@Override
	public Dimension getSize() 
	{
		// TODO Auto-generated method stub
		return panel.getSize();
	}
	
	@Override
	public JPanel getPanel()
	{
		if(panel != null)
		{
			setPanel();
			return panel;
		}
		
		System.out.println("[error] panel is null");
		return new JPanel();
	}
	
	//로그인 판넬에 필요한것들 만들어서 붙이기
	//다른 창을 만들고싶으면 여기만 바꾸면 됨
	@Override
	public void setPanel()
	{	
		JProgressBar progressbar = new JProgressBar(0, 100);
		progressbar.setStringPainted(true);
		panel.add(progressbar);
	}
}
