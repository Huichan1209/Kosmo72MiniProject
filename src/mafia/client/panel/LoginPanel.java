package mafia.client.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import mafia.client.ClientFrame;

public class LoginPanel extends JPanel implements Panel
{
   private Image background = new ImageIcon("./images/mafiaGame.jpg").getImage();
   private static JPanel panel;//로그인 패널

   public LoginPanel()
   {
	  setPanel();
      panel = this;
   }

   @Override
   public Dimension getSize() 
   {
      return new Dimension(950,600);
   }

   @Override
   public JPanel getPanel()
   {
      if(panel != null)
      {
         return panel;
      }

      System.out.println("[error] panel is null");
      return new JPanel();
   }
   
   public void login()
   {
	   
		ClientFrame.getInstance().change(ClientFrame.getInstance().getGamePanel());
   }

   @Override
   public void setPanel()
   {   
      setBounds(100,100,950,600);
      setLayout(null);
      JTextField taMid;
      JPasswordField taMpw;//id pw 입력할 텍스트필드
      JPanel all = new JPanel();
      setOpaque(false);

      all.setOpaque(false);//투명하게
      all.setSize(680, 450);
      all.setBounds(60, 190, 680, 450);
      all.setLayout(null);

      JLabel lbMid = new JLabel("E_Mail : ");//이메일 레이블
      lbMid.setFont(new Font("Serif", Font.BOLD, 12));
      lbMid.setForeground(Color.WHITE);
      lbMid.setBounds(237-30, 149, 50, 15);
      all.add(lbMid);

      taMid = new JTextField();//이메일 입력할 텍스트 필드
      taMid.setBounds(299-30, 146, 226, 21);
      all.add(taMid);
      taMid.setColumns(30);

      JLabel lbMpw = new JLabel("PW : ");//패스워드 레이블
      lbMpw.setFont(new Font("Serif", Font.BOLD, 12));
      lbMpw.setForeground(Color.WHITE);
      lbMpw.setBounds(218, 187, 69, 15);
      
      all.add(lbMpw);

      taMpw = new JPasswordField();//패스워드 입력할 텍스트 필드
      taMpw.setBounds(299-30, 184, 226, 21);
      taMpw.setColumns(30);
      all.add(taMpw);

      JButton buLogin = new JButton("로그인");//로그인 버튼
      buLogin.addActionListener(new ActionListener() 
      {
		@Override
		public void actionPerformed(ActionEvent arg0) 
		{
			login();
		}
      });
      
      buLogin.setBounds(555-30, 149, 97, 53);
      all.add(buLogin);

      JButton buMemberSet = new JButton("회원가입");//회원가입 버튼
      buMemberSet.setBounds(467+30, 238, 120, 36);
      all.add(buMemberSet);

      JButton buFindPw = new JButton("비밀번호 찾기");//비밀번호 찾기 버튼
      buFindPw.setBounds(335+30, 238, 120, 36);
      all.add(buFindPw);

      JButton buMemberReMember = new JButton("회원정보 수정");
      buMemberReMember.setBounds(203+30, 238, 120, 36);
      all.add(buMemberReMember);

      JButton buGameStart = new JButton("게임 시작!!");
      buGameStart.setBounds(253,300,250,50);
      all.add(buGameStart);
      add(all);
      
      JPanel imagePanel = new JPanel() {
         public void paintComponent(Graphics g) {
            Dimension d = getSize();//크기조정(페이지 크기에 맞춰 사이즈 자동 조정됨)
            g.drawImage(background,0,0,d.width, d.height, null);
            setOpaque(false);
            super.paintComponent(g);
            //repaint();
         }
      };

      imagePanel.setBounds(0, 0, 950,600);
      add(imagePanel);

   }

}