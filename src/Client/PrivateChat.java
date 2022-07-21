package Client;

import Server.ServerFileThread;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.*;
import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

class PrivateChat extends JFrame implements ActionListener{
    private String NickName = null;
    private String Acceptor = null;
    private JScrollPane jsp = new JScrollPane();
    private JButton Send = new JButton("发送文字");
    private JButton Send_Record = new JButton("发送语音");
    private JButton Send_File = new JButton("发送文件");
    private JButton shake = new JButton("窗口抖动");
    private JTextField Sendword = new JTextField(20);       //发文字区域
    public JTextArea Chat = new JTextArea(10,45);     //聊天记录显示
    private JLabel myself = new JLabel();
    public PrintStream ps = null;
    public BufferedReader br = null;
    private String path;
    private RecordMain re = null;
    public PrivateChat(String name1,String name2,String path,PrintStream ps,BufferedReader br,int X,int Y){
        this.ps = ps;
        this.br = br;
        this.path = path;
        this.NickName = name1;
        this.Acceptor = name2;      //私聊对象
        Font font = new Font("微软雅黑", Font.PLAIN, 15);
        Chat.setFont(font);
        Chat.setLineWrap(true);         //设置自动换行
        Chat.setLocation(0,0);
        Chat.setEditable(false);        //聊天记录无法编辑
        Chat.setMargin(new Insets(7, 7, 7, 7));     //设置7mm边距
        JScrollPane jsp = new JScrollPane(Chat);         //设置一个滚动条
        jsp.setBounds(0,0,500,500);    //宽500高500
        Sendword.addActionListener(this);
        Sendword.setLocation(0,500);
        Sendword.setSize(300,60);
        Sendword.setFont(font);
        Send.addActionListener(this);
        Send.setSize(95,60);
        Send.setLocation(300,500);
        Send.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        Send.setMargin(new Insets(0,0,0,0));   //设置按钮边框和标签之间空白为0
        Send_Record.addActionListener(this);
        Send_Record.setSize(95,60);
        Send_Record.setLocation(395,500);
        Send_Record.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        Send_Record.setMargin(new Insets(0,0,0,0));   //设置按钮边框和标签之间空白为0
        Send_File.addActionListener(this);
        Send_File.setSize(95,60);
        Send_File.setLocation(490,500);
        Send_File.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        Send_File.setMargin(new Insets(0,0,0,0));   //设置按钮边框和标签之间空白为0
        shake.addActionListener(this);
        shake.setSize(85,69);
        shake.setLocation(500,429);
        shake.setFont(font);
        shake.setMargin(new Insets(0,0,0,0));   //设置按钮边框和标签之间空白为0
        shake.setBackground(new Color(255,255,204));          //设置按钮颜色为淡黄
        ImageIcon image = new ImageIcon(path);  //将图片路径作为参数传入
        image.setImage(image.getImage().getScaledInstance(85,90,Image.SCALE_DEFAULT));  //创建缩放版本图像
        JLabel Picture = new JLabel(image);
        Picture.setLocation(500,0);
        Picture.setSize(85,90);
        myself.setText(NickName);
        myself.setSize(80,20);
        myself.setLocation(530,95);
        myself.setFont(new Font("微软雅黑", Font.BOLD, 15));
        //窗体基本设置
        this.setLayout(null);
        this.setLocation(X,Y);
        this.add(jsp);
        this.add(Sendword);
        this.add(Send);
        this.add(Send_Record);
        this.add(Send_File);
        this.add(shake);
        this.add(Picture);
        this.add(myself);
        this.setTitle("您正在和"+name2+"私聊");
        this.setSize(600,598);
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == Send) {
            ps.println("SI-" + NickName + "-" + Acceptor + "-" + Sendword.getText());
            Sendword.setText("");               //清空文本框
        } else if (e.getSource() == Send_Record) {
            re = new RecordMain();           //进入录制界面
            re.setLocationRelativeTo(this);  //设置在本页面中间
            Write();        //将range_flag的值写入
            WaitingThread waiting = new WaitingThread();
            waiting.start();
        } else if (e.getSource() == Send_File) {
            //利用该函数发送文件到服务器，然后服务器将文件进行转发
            FileDialog fLoader = new FileDialog(this, "选择要发送的文件", FileDialog.LOAD);
            fLoader.setVisible(true);
            Write();        //将range_flag的值写入
            path = fLoader.getDirectory() + fLoader.getFile();
            ClientFileThread.outFileToServer(path, false,Acceptor);
        }else{      //发送窗口抖动
            ps.println("SID-" + NickName + "-" + Acceptor);
        }
    }

    public void Write(){
        try {
            File f = new File("range.txt");
            FileOutputStream fos = new FileOutputStream(f, false);
            PrintStream ps = new PrintStream(fos);
            ps.println("false");
            ps.println(Acceptor);   //字符串按行写入文件中
            fos.close();
        }catch(Exception ex){}
    }

    class WaitingThread extends Thread{
        public void run(){
            while(true){
                try{
                    Thread.sleep(50);
                }catch(Exception e){}
                if(RecordMain.flag){
                    //已经录完，利用该函数发送文件到服务器
                    ClientFileThread.outFileToServer(MyRecorder.path,true,Acceptor);
                    RecordMain.flag = false;    //将值设为false，用于下一次发语音
                    break;
                }
            }
        }
    }
}