package Server;

import java.math.RoundingMode;
import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
class Server extends JFrame implements Runnable,ActionListener{
    private static String url = "jdbc:mysql://localhost:3306/login?useSSL=false&allowPublicKeyRetrieval=true";    //连接数据库的url,login是数据库名
    private static String user = "root";        //mysql登录名
    private static String pass = "qzc789654";   //mysql登录密码
    private static Connection con;              //建立连接
    private JLabel explain = new JLabel("在线用户列表",JLabel.CENTER);
    private List users = new List();
    private JScrollPane listPane = new JScrollPane(users);       //设置滚动视图
    private JButton jbt = new JButton("远程关闭");
    private JButton Send_Button = new JButton("群发消息");
    private JTextField Sendword = new JTextField(20);       //发文字区域
    private ServerSocket ss = null;
    private Font font = new Font("微软雅黑", Font.BOLD, 25);
    private HashMap<String,ChatThread> users_connect = new HashMap<String,ChatThread>();
    private boolean chongfu = true;
    private String Record_path = null;
    private InputStream in = null;
    public Server() throws Exception{
        this.setLayout(null);
        Class.forName("com.mysql.cj.jdbc.Driver");     //Class.forName查找并加载指定的类,加载数据库连接驱动并连接
        con = DriverManager.getConnection(url,user,pass);
        explain.setLocation(0,0);
        explain.setSize(430,50);
        explain.setFont(font);
        listPane.setLocation(0,50);
        listPane.setSize(430,320);
        users.setFont(new Font("Consolas", Font.PLAIN, 25));
        Sendword.addActionListener(this);
        Sendword.setLocation(0,370);
        Sendword.setSize(250,57);
        Sendword.setFont(new Font("微软雅黑", Font.PLAIN, 25));
        jbt.addActionListener(this);
        jbt.setSize(85,56);
        jbt.setLocation(250,370);
        jbt.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        jbt.addActionListener(this);
        Send_Button.addActionListener(this);
        Send_Button.setSize(85,56);
        Send_Button.setLocation(335,370);
        Send_Button.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        this.setTitle("服务器");
        this.add(explain);
        this.add(listPane);
        this.add(jbt);
        this.add(Sendword);
        this.add(Send_Button);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(435,465);
        this.setVisible(true);
        ss = new ServerSocket(9999);    //信息传送使用端口
        new Thread(this).start();
    }
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == jbt){
            //远程关闭按钮
            Leave(1,null);
        }else if(e.getSource() == Sendword || e.getSource() == Send_Button){
            //群发消息按钮
            for(String ct : users_connect.keySet()){    //利用key值遍历哈希表
                users_connect.get(ct).Send.println("系统消息 "+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                users_connect.get(ct).Send.println(Sendword.getText());
            }
            Sendword.setText("");       //清空输入框
        }
    }

    public void Leave(int num,String selectedUser){
        if(num == 1){
            selectedUser = users.getSelectedItem();
        }
        String msg = "LOGOUT-"+num+"-"+selectedUser;        //1是服务器t的，0是自己主动走的
        ChatThread ct = users_connect.get(selectedUser);
        ct.Send.println(msg);
        users.remove(selectedUser);
        users_connect.remove(selectedUser);
    }
    public void run(){
        while(true){
            try{
                Socket s = ss.accept();
                ChatThread ct = new ChatThread(s);
            }catch(Exception e){}
        }
    }
    class ChatThread extends Thread{
        PrintStream Send = null;
        BufferedReader Read = null;
        String NickName = null;
        ChatThread(Socket s) throws Exception{
            Send = new PrintStream(s.getOutputStream());
            in = s.getInputStream();
            Read = new BufferedReader(new InputStreamReader(in));
            this.start();
        }
        public void run(){
            while(true){
                try{
                    String message = Read.readLine();
                    String[] msgs = message.split("-");
                    if(msgs[0].equals("REG1")){
                        if(msgs[1].equals(msgs[2])){
                            Send.println("YES");
                        }else{
                            Send.println("NO");
                        }
                    }else if(msgs[0].equals("REG2"))
                    {
                        try{
                            String sql = "select username from client where username=?";  //?为占位符
                            PreparedStatement ptmt = con.prepareStatement(sql);     //加入预编译语句
                            ptmt.setString(1,msgs[1]);  //1是ID
                            ResultSet rs = ptmt.executeQuery();
                            if(rs.next()){
                                Send.println("EXISTS");
                            }else{
                                Send.println("INSERT");
                                sql = "insert into client (username,password,picture_path) values(?,?,?)";  //?为占位符
                                ptmt = con.prepareStatement(sql);     //加入预编译语句
                                ptmt.setString(1,msgs[1]);
                                ptmt.setString(2,msgs[2]);
                                ptmt.setString(3,msgs[3]);
                                ptmt.execute();     //执行sql语句
                                ptmt.close();
                            }
                        }catch(Exception exce){}
                    }else if(msgs[0].equals("LOGIN")){
                            String sql= "select username,password,picture_path from client where username=? and password=?";
                            PreparedStatement ptmt = con.prepareStatement(sql);
                            ptmt.setString(1,msgs[1]);
                            ptmt.setString(2,msgs[2]);
                            ResultSet rs = ptmt.executeQuery();
                            if(rs.next()) {      //存在此人
                                String path = rs.getString(3);
                                for (String ct : users_connect.keySet()) {
                                    if(msgs[1].equals(ct)){  //是否重复登录
                                        Send.println("CHONG");
                                        chongfu = false;
                                        break;
                                    }
                                }
                                if(chongfu) {      //未重复
                                    Send.println("OK-"+path);     //同意登录
                                    NickName = msgs[1];
                                    users.add(NickName);
                                    users_connect.put(NickName, this);
                                    ServerFileThread serverFileThread = new ServerFileThread();  //服务器文件读写进程启动
                                    serverFileThread.start();
                                }else{
                                    chongfu = true;  //将重复标记重新设置为true
                                }
                            }else{      //拒绝登录
                                    Send.println("NO");
                        }
                    }else if(msgs[0].equals("NEW")){
                        Calendar c = Calendar.getInstance();
                        for (String ct : users_connect.keySet()) {    //利用key值遍历哈希表
                            users_connect.get(ct).Send.println("系统消息 " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                            c.get(Calendar.HOUR_OF_DAY);
                            if (c.get(Calendar.HOUR_OF_DAY) <= 5 || c.get(Calendar.HOUR_OF_DAY) >= 18)
                                users_connect.get(ct).Send.println("SLOGIN-EVE-" + NickName);
                            else if (c.get(Calendar.HOUR_OF_DAY) >= 6 && c.get(Calendar.HOUR_OF_DAY) <= 10)
                                users_connect.get(ct).Send.println("SLOGIN-MOR-" + NickName);
                            else if (c.get(Calendar.HOUR_OF_DAY) >= 14 && c.get(Calendar.HOUR_OF_DAY) <= 17)
                                users_connect.get(ct).Send.println(("SLOGIN-AFT-" + NickName));
                            else
                                users_connect.get(ct).Send.println("SLOGIN-MOO-" + NickName);
                            if (!ct.equals(NickName)) {
                                Send.println("USER-" + ct);       //服务器发送之前的好友列表
                            }
                        }
                    }else if(msgs[0].equals("RUN")){
                        for(String ct : users_connect.keySet()){    //利用key值遍历哈希表
                            users_connect.get(ct).Send.println("系统消息 "+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                            users_connect.get(ct).Send.println("SLOGOUT-"+msgs[1]);
                        }
                    }else if(msgs[0].equals("LEAVE")){
                        Leave(0,NickName);
                    }else if(msgs[0].equals("SILIAO")){
                        users_connect.get(msgs[1]).Send.println("SI-"+NickName);
                    }else if(msgs[0].equals("ACCEPT")) {
                        users_connect.get(NickName).Send.println("JIESHOU-" + msgs[1]); //接受方显示对方
                        users_connect.get(msgs[1]).Send.println("JIESHOU-" + NickName); //发出方显示接受方
                    }else if(msgs[0].equals("REFUSE")){
                        users_connect.get(msgs[1]).Send.println("JUJUE-"+NickName);
                    }
                    else if(msgs[0].equals("SI")){
                        //1为发送方，2为接收方，3为文字
                        users_connect.get(msgs[2]).Send.println("SIMESSAGE-"+msgs[1]+"-"+msgs[3]);
                        //自己也显示自己发了这个消息
                        users_connect.get(msgs[1]).Send.println("SIMESSAGE-"+msgs[1]+"-"+msgs[3]);
                    }
                    else if(msgs[0].equals("SID")){
                        users_connect.get(msgs[2]).Send.println("SIMESSAGE-"+msgs[1]+"-"+"[窗口抖动提醒]我发送了一条窗口抖动。");
                        users_connect.get(msgs[2]).Send.println("SID");
                        //自己也显示自己发了这个消息
                        users_connect.get(msgs[1]).Send.println("SIMESSAGE-"+msgs[1]+"-"+"[窗口抖动提醒]我发送了一条窗口抖动。");

                    }
                    else{   //普通消息发送
                        for(String ct : users_connect.keySet()){    //利用key值遍历哈希表
                            users_connect.get(ct).Send.println(msgs[0]+" "+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                            users_connect.get(ct).Send.println(msgs[1]);
                        }
                    }
                }catch(Exception ex){}
            }
        }
    }

    public static void main(String[] args) throws Exception {
        new Server();
    }
}