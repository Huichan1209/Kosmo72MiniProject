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
	
	//�α��� �ǳڿ� �ʿ��Ѱ͵� ���� ���̱�
	//�ٸ� â�� ���������� ���⸸ �ٲٸ� ��
	@Override
	public void setPanel()
	{	
		JProgressBar progressbar = new JProgressBar(0, 100);
		progressbar.setStringPainted(true);
		panel.add(progressbar);
	}
}
