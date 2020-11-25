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
	
	//로그인 판넬에 필요한것들 만들어서 붙이기
	//다른 창을 만들고싶으면 여기만 바꾸면 됨
	public void setPanel()
	{	
		JPanel idBox = new JPanel(); //아이디 입력창 판넬
		idBox.setLayout(new BoxLayout(idBox, BoxLayout.X_AXIS));
		idBox.add(new JLabel("ID"));
		idBox.add(new JTextField());
		
		JPanel pwBox = new JPanel(); //비밀번호 입력창 판넬
		pwBox.setLayout(new BoxLayout(pwBox, BoxLayout.X_AXIS));
		pwBox.add(new JLabel("PW"));
		pwBox.add(new JTextField());
		
		JPanel emailBox = new JPanel(); //이메일 입력창 판넬
		emailBox.setLayout(new BoxLayout(emailBox, BoxLayout.Y_AXIS));
		emailBox.add(new JLabel("EMAIL"));
		emailBox.add(new JTextField());
		
		this.panel.setLayout(new BoxLayout(this.panel, BoxLayout.Y_AXIS));
		this.panel.add(idBox);
		this.panel.add(pwBox);
		this.panel.add(emailBox);
	}
}
