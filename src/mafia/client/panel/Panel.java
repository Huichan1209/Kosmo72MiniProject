package mafia.client.panel;

import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JProgressBar;

public interface Panel
{	
	public JPanel getPanel();
	
	//�α��� �ǳڿ� �ʿ��Ѱ͵� ���� ���̱�
	//�ٸ� â�� ���������� ���⸸ �ٲٸ� ��
	public void setPanel();
	
	public Dimension getSize();
}
