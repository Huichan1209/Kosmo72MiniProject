package mafia.client.panel;

import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JProgressBar;

public interface Panel
{	
	public JPanel getPanel();
	
	//로그인 판넬에 필요한것들 만들어서 붙이기
	//다른 창을 만들고싶으면 여기만 바꾸면 됨
	public void setPanel();
	
	public Dimension getSize();
}
