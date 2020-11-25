package mafia.client.panel;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class LoginPanel extends JPanel implements Panel
{
	private JPanel panel;
	
	public LoginPanel()
	{
		//Constructor
		panel = new JPanel();
	}
	
	@Override
	public Dimension getSize() 
	{
		panel.setSize(300, 400);
		return panel.getSize();
	}
	
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
	public void setPanel()
	{	
		JPanel idBox = new JPanel(); //���̵� �Է�â �ǳ�
		idBox.setLayout(new BoxLayout(idBox, BoxLayout.X_AXIS));
		idBox.add(new JLabel("ID"));
		idBox.add(new JTextField());
		
		JPanel pwBox = new JPanel(); //��й�ȣ �Է�â �ǳ�
		pwBox.setLayout(new BoxLayout(pwBox, BoxLayout.X_AXIS));
		pwBox.add(new JLabel("PW"));
		pwBox.add(new JTextField());
		
		JPanel emailBox = new JPanel(); //�̸��� �Է�â �ǳ�
		emailBox.setLayout(new BoxLayout(emailBox, BoxLayout.Y_AXIS));
		emailBox.add(new JLabel("EMAIL"));
		emailBox.add(new JTextField());
		
		this.panel.setLayout(new BoxLayout(this.panel, BoxLayout.Y_AXIS));
		this.panel.add(idBox);
		this.panel.add(pwBox);
		this.panel.add(emailBox);
	}
}
