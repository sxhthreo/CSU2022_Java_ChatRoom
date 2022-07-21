package Client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import javax.swing.*;
class Login extends JFrame implements ActionListener {
    public PrintStream ps = null;
    public BufferedReader br = null;
    private JTextField Id = new JTextField(20);
    private JPasswordField Passwd =new JPasswordField(20);
    private JLabel welcome = new JLabel("���ģ������￪ʼ��",JLabel.CENTER);
    private JLabel zhanghao = new JLabel("�˺�:");
    private JLabel mima = new JLabel("����:");
    private JLabel Picture = new JLabel();
    private JButton Login_Button = new JButton("��¼");
    private JButton Register_Button = new JButton("ע��");
    private FileDialog dialog = null;
    private OutputStream out = null;
    public Login() throws Exception{
        this.setLayout(null);
        Font font = new Font("΢���ź�", Font.PLAIN, 25);
        welcome.setLocation(20,50);
        welcome.setSize(600,80);
        welcome.setFont(new Font("΢���ź�", Font.BOLD, 45));
        Id.setLocation(250,200);
        Id.setSize(300,60);
        Id.setFont(font);
        Passwd.setLocation(250,300);
        Passwd.setSize(300,60);
        Passwd.setFont(font);
        zhanghao.setLocation(180,190);
        zhanghao.setSize(200,80);
        zhanghao.setFont(new Font("΢���ź�", Font.BOLD, 25));
        mima.setLocation(180,290);
        mima.setSize(200,80);
        mima.setFont(new Font("΢���ź�", Font.BOLD, 25));
        ImageIcon image = new ImageIcon("people.png");  //��ͼƬ·����Ϊ��������
        image.setImage(image.getImage().getScaledInstance(100,100,Image.SCALE_DEFAULT));  //�������Ű汾ͼ��
        Picture = new JLabel(image);
        Picture.setLocation(60, 230);
        Picture.setSize(100, 100);
        Register_Button.setLocation(120,400);
        Register_Button.setSize(160,60);
        Register_Button.setFont(font);
        Register_Button.addActionListener(this);
        Login_Button.setLocation(300, 400);
        Login_Button.setSize(160,60);
        Login_Button.setFont(font);
        Login_Button.addActionListener(this);
        //Socket s = new Socket("172.23.96.1", 9999);
        Socket s = new Socket("localhost", 9999);
        out = s.getOutputStream();
        ps = new PrintStream(out);
        br = new BufferedReader(new InputStreamReader(s.getInputStream()));
        //�����������
        this.add(welcome);
        this.add(Id);
        this.add(Picture);
        this.add(Passwd);
        this.add(zhanghao);
        this.add(mima);
        this.add(Login_Button);
        this.add(Register_Button);
        this.setTitle("��ӭ��¼");
        this.setLocation(200,100);
        this.setSize(600,550);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);
    }
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == Register_Button){
            try{
                new Register(ps,br);
            }catch(Exception exc){}
        }
        else {      //��¼
                try{
                    String passwd1 = String.valueOf(Passwd.getPassword());
                    if(Id.getText().isEmpty()||passwd1.isEmpty()){
                        JOptionPane.showMessageDialog(this,"�����˺Ż��������������!");
                    }else {
                        ps.println("LOGIN-" + Id.getText() + "-" + passwd1);
                        String message = br.readLine();
                        String[] msgs = message.split("-");
                        if (msgs[0].equals("OK")) {         //���ڴ���
                            String path = msgs[1];          //1��ͷ��·��
                            new Chat(Id.getText(),path,ps,br,out);
                            this.setVisible(false);
                        } else if(message.equals("CHONG")){ //�Ѿ���¼��
                            JOptionPane.showMessageDialog(this, "���˻��Ѿ���¼�������ظ���¼!");
                        } else {
                            JOptionPane.showMessageDialog(this, "�˺Ż������������������!");
                        }
                    }
                }catch(Exception ex){}
            }
        }
    public static void main(String[] args) throws Exception{
        new Login();
    }
}