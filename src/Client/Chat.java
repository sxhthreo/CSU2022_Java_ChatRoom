package Client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.*;
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;

class Chat extends JFrame implements ActionListener,Runnable, MouseListener {
    private String NickName = null;
    private JScrollPane jsp = new JScrollPane();
    private JButton Send = new JButton("发送文字");
    private JButton Send_Record = new JButton("发送语音");
    private JButton Send_File = new JButton("发送文件");
    private JButton myClock = new JButton("系统时间");
    private JButton Leave = new JButton("离开聊天室");
    private DefaultListModel<String> user = new DefaultListModel<String>();  //用户列表
    private JList<String> userList = new JList<String>(user);   //展示用户列表
    private JScrollPane listPane = new JScrollPane(userList);       //设置滚动视图
    private JTextField Sendword = new JTextField(20);       //发文字区域
    private JTextArea Chat = new JTextArea(10,45);     //聊天记录显示
    private JLabel myself = new JLabel("",JLabel.CENTER);
    private JLabel friend_list = new JLabel("好友列表",JLabel.CENTER);
    public PrintStream ps = null;
    public BufferedReader br = null;
    private String path;
    private PrivateChat pr = null;
    private RecordMain re = null;
    private OutputStream out;
    public Chat(String Nick,String path,PrintStream ps,BufferedReader br,OutputStream out) throws Exception{
        this.ps = ps;
        this.br = br;
        this.path = path;
        this.out = out;
        ps.println("NEW");
        Font font = new Font("微软雅黑", Font.PLAIN, 15);
        Chat.setFont(font);
        Chat.setLineWrap(true);         //设置自动换行
        Chat.setLocation(0,0);
        Chat.setEditable(false);        //聊天记录无法编辑
        Chat.setMargin(new Insets(7, 7, 7, 7));     //设置7mm边距
        JScrollPane jsp = new JScrollPane(Chat);         //设置一个滚动条
        jsp.setBounds(0,0,500,500);
        //设置控件位置
        Sendword.setLocation(0,500);
        Sendword.setSize(300,60);
        Sendword.setFont(font);
        Send.addActionListener(this);
        Send.setSize(95,60);
        Send.setLocation(300,500);
        Send.setFont(font);
        Send.setMargin(new Insets(0,0,0,0));   //设置按钮边框和标签之间空白为0
        myClock.addActionListener(this);
        myClock.setSize(85,69);
        myClock.setLocation(500,360);
        myClock.setFont(font);
        myClock.setBackground(new Color(255,255,204));          //设置按钮颜色为淡黄
        myClock.setMargin(new Insets(0,0,0,0));   //设置按钮边框和标签之间空白为0
        Send_Record.addActionListener(this);
        Send_Record.setSize(95,60);
        Send_Record.setLocation(395,500);
        Send_Record.setFont(font);
        Send_Record.setMargin(new Insets(0,0,0,0));   //设置按钮边框和标签之间空白为0
        Send_File.addActionListener(this);
        Send_File.setSize(95,60);
        Send_File.setLocation(490,500);
        Send_File.setFont(font);
        Send_File.setMargin(new Insets(0,0,0,0));   //设置按钮边框和标签之间空白为0
        Leave.addActionListener(this);
        Leave.setSize(85,69);
        Leave.setLocation(500,429);
        Leave.setFont(font);
        Leave.setMargin(new Insets(0,0,0,0));   //设置按钮边框和标签之间空白为0
        Leave.setBackground(new Color(255,255,204));          //设置按钮颜色为淡黄
        listPane.setSize(86,220);
        listPane.setLocation(500,140);
        userList.setFont(font);
        userList.addMouseListener(this);    //用于监听私聊
        ImageIcon image = new ImageIcon(path);  //将图片路径作为参数传入
        image.setImage(image.getImage().getScaledInstance(85,90,Image.SCALE_DEFAULT));  //创建缩放版本图像
        JLabel Picture = new JLabel(image);
        Picture.setLocation(500,0);
        Picture.setSize(85,90);
        this.NickName = Nick;        //构造函数传入参数
        myself.setText(NickName);
        myself.setSize(100,20);
        myself.setLocation(493,95);
        myself.setFont(new Font("微软雅黑", Font.BOLD, 15));
        friend_list.setSize(100,20);     //好友列表四个字
        friend_list.setLocation(493,120);
        friend_list.setFont(new Font("微软雅黑", Font.BOLD, 15));
        new Thread(this).start();
        //客户端文件读写线程启动,将自己JFrame作为参数传入
        ClientFileThread fileThread = new ClientFileThread(NickName,this,ps);
        fileThread.start();
        //窗体基本设置
        this.setLayout(null);
        this.add(jsp);
        this.add(Sendword);
        this.add(myClock);
        this.add(Send);
        this.add(Send_Record);
        this.add(listPane);
        this.add(Picture);
        this.add(myself);
        this.add(friend_list);
        this.add(Leave);
        this.add(Send_File);
        this.setTitle(NickName+"的聊天室");
        this.setLocation(200,100);
        this.setSize(600,598);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);
    }
    public void run(){
        while(true){
            try{
                String message = br.readLine();
                String[] msgs = message.split("-");
                if(msgs[0].equals("LOGOUT")){
                    ps.println("RUN-"+NickName);        //使服务器能够让别人知道消息
                    if(msgs[1].equals("1")) {
                        JOptionPane.showMessageDialog(this, "对不起，您被踢出！");
                    }else{
                        JOptionPane.showMessageDialog(this, "您已离开聊天室！再见！");
                    }
                    this.dispose();
                }else if(msgs[0].equals("SLOGIN")){
                    if(msgs[1].equals("EVE")){
                        Chat.append("晚上好,"+msgs[2]+"!欢迎加入定制聊天室!\n");
                    }else if(msgs[1].equals("MOR")){
                        Chat.append("早上好,"+msgs[2]+"!欢迎加入定制聊天室!\n");
                    }else if(msgs[1].equals("AFT")){
                        Chat.append("下午好,"+msgs[2]+"!欢迎加入定制聊天室!\n");
                    }else{
                        Chat.append("中午好,"+msgs[2]+"!欢迎加入定制聊天室!\n");
                    }
                    if(!msgs[2].equals(NickName)){
                        user.addElement(msgs[2]);       //将用户加入好友列表
                    }
                    userList.repaint();
                } else if(msgs[0].equals("USER")){
                    user.addElement(msgs[1]);           //服务器为客户端发送之前的好友列表
                } else if(msgs[0].equals("SLOGOUT")){
                    Chat.append(msgs[1]+"用户已经离开聊天室。\n");
                    user.removeElement(msgs[1]);    //将用户移除好友列表
                    userList.repaint();
                } else if(msgs[0].equals("SI")){
                    //收到私信请求，读取结果，0代表接受，1代表拒绝
                    int result = JOptionPane.showConfirmDialog(this,
                            msgs[1]+"向你提出了私聊请求，是否接受？","私聊请求",JOptionPane.YES_NO_OPTION);
                    if(result == 0){
                        ps.println("ACCEPT-"+msgs[1]);  //接受
                    }else{
                        ps.println("REFUSE-"+msgs[1]);  //拒绝
                    }
                }else if(msgs[0].equals("JUJUE")){
                    JOptionPane.showMessageDialog(this,msgs[1]+"拒绝了您的私聊请求!");
                }else if(msgs[0].equals("JIESHOU")) {
                    pr = new PrivateChat(this.NickName,msgs[1],path,ps,br,this.getX(),this.getY());    //开始私聊
                }else if(msgs[0].equals("SIMESSAGE")){
                    pr.Chat.append(msgs[1]+" "+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+"\n");
                    pr.Chat.append(msgs[2]+"\n");
                }else if(msgs[0].equals("SID")){    //收到窗口抖动提示，持续两秒窗口抖动
                    new Thread() {      //开启窗口抖动线程
                        long begin = System.currentTimeMillis();
                        long end = System.currentTimeMillis();
                        Point p = pr.getLocationOnScreen();

                        public void run() {
                            int i = 1;
                            while ((end - begin) / 1000 < 2) {
                                pr.setLocation(new Point((int) p.getX() - 5 * i, (int) p.getY() + 5 * i));  //Point函数构造并初始化点
                                end = System.currentTimeMillis();
                                try {
                                    Thread.sleep(5);
                                    i = -i;
                                    pr.setLocation(p);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }.start();
                }
                else{
                    Chat.append(message+"\n");
                }
            }catch(Exception e){}
        }
    }
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == Send) {
            ps.println(NickName + "-" + Sendword.getText());
            Sendword.setText("");               //清空文本框
        }
        else if(e.getSource() == Send_Record){
            re = new RecordMain();           //进入录制界面
            re.setLocationRelativeTo(this);  //设置在本页面中间
            Write();
            WaitingThread waiting = new WaitingThread();
            waiting.start();
        }else if(e.getSource() == Send_File){
            //利用该函数发送文件到服务器，然后服务器将文件进行转发
            FileDialog fLoader = new FileDialog(this,"选择打开的文件",FileDialog.LOAD);
            fLoader.setVisible(true);
            Write();
            path = fLoader.getDirectory() + fLoader.getFile();
            ClientFileThread.outFileToServer(path,false,"all");
        }else if(e.getSource() == Leave){      //要走了
            ps.println("LEAVE");
        }else{      //查看系统时钟
            new Clock(this.getX(),this.getY());
        }
    }

    public void Write(){
        try {
            File f = new File("range.txt");
            FileOutputStream fos = new FileOutputStream(f, false);
            PrintStream ps = new PrintStream(fos);
            ps.println("true");     //true代表公发文件，false代表私发文件
            ps.println("all");      //实际无用，填充
            fos.close();
        }catch(Exception ex){}
    }

    public void mouseClicked(MouseEvent e) {
        if(e.getClickCount() == 2) {        //监听双击事件
            ps.println("SILIAO" + "-" + userList.getSelectedValue());       //向服务器发送想私聊信息
        }
    }
    //以下均为重写方法
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}

    class WaitingThread extends Thread{
        public void run(){
            while(true){
                try{
                    Thread.sleep(50);
                }catch(Exception e){}
                if(RecordMain.flag){
                    //已经录完，利用该函数发送文件到服务器
                    ClientFileThread.outFileToServer(MyRecorder.path,true,"all");
                    RecordMain.flag = false;    //将值设为false，用于下一次发语音
                    break;
                }
            }
        }
    }
}