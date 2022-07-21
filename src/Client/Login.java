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
    private JLabel welcome = new JLabel("轻聊，从这里开始。",JLabel.CENTER);
    private JLabel zhanghao = new JLabel("账号:");
    private JLabel mima = new JLabel("密码:");
    private JLabel Picture = new JLabel();
    private JButton Login_Button = new JButton("登录");
    private JButton Register_Button = new JButton("注册");
    private FileDialog dialog = null;
    private OutputStream out = null;
    public Login() throws Exception{
        this.setLayout(null);
        Font font = new Font("微软雅黑", Font.PLAIN, 25);
        welcome.setLocation(20,50);
        welcome.setSize(600,80);
        welcome.setFont(new Font("微软雅黑", Font.BOLD, 45));
        Id.setLocation(250,200);
        Id.setSize(300,60);
        Id.setFont(font);
        Passwd.setLocation(250,300);
        Passwd.setSize(300,60);
        Passwd.setFont(font);
        zhanghao.setLocation(180,190);
        zhanghao.setSize(200,80);
        zhanghao.setFont(new Font("微软雅黑", Font.BOLD, 25));
        mima.setLocation(180,290);
        mima.setSize(200,80);
        mima.setFont(new Font("微软雅黑", Font.BOLD, 25));
        ImageIcon image = new ImageIcon("people.png");  //将图片路径作为参数传入
        image.setImage(image.getImage().getScaledInstance(100,100,Image.SCALE_DEFAULT));  //创建缩放版本图像
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
        //窗体基本设置
        this.add(welcome);
        this.add(Id);
        this.add(Picture);
        this.add(Passwd);
        this.add(zhanghao);
        this.add(mima);
        this.add(Login_Button);
        this.add(Register_Button);
        this.setTitle("欢迎登录");
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
        else {      //登录
                try{
                    String passwd1 = String.valueOf(Passwd.getPassword());
                    if(Id.getText().isEmpty()||passwd1.isEmpty()){
                        JOptionPane.showMessageDialog(this,"请检查账号或密码后重新输入!");
                    }else {
                        ps.println("LOGIN-" + Id.getText() + "-" + passwd1);
                        String message = br.readLine();
                        String[] msgs = message.split("-");
                        if (msgs[0].equals("OK")) {         //存在此人
                            String path = msgs[1];          //1是头像路径
                            new Chat(Id.getText(),path,ps,br,out);
                            this.setVisible(false);
                        } else if(message.equals("CHONG")){ //已经登录过
                            JOptionPane.showMessageDialog(this, "此账户已经登录，不能重复登录!");
                        } else {
                            JOptionPane.showMessageDialog(this, "账号或密码错误，请重新输入!");
                        }
                    }
                }catch(Exception ex){}
            }
        }
    public static void main(String[] args) throws Exception{
        new Login();
    }
}