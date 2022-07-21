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
    private JButton Send = new JButton("��������");
    private JButton Send_Record = new JButton("��������");
    private JButton Send_File = new JButton("�����ļ�");
    private JButton myClock = new JButton("ϵͳʱ��");
    private JButton Leave = new JButton("�뿪������");
    private DefaultListModel<String> user = new DefaultListModel<String>();  //�û��б�
    private JList<String> userList = new JList<String>(user);   //չʾ�û��б�
    private JScrollPane listPane = new JScrollPane(userList);       //���ù�����ͼ
    private JTextField Sendword = new JTextField(20);       //����������
    private JTextArea Chat = new JTextArea(10,45);     //�����¼��ʾ
    private JLabel myself = new JLabel("",JLabel.CENTER);
    private JLabel friend_list = new JLabel("�����б�",JLabel.CENTER);
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
        Font font = new Font("΢���ź�", Font.PLAIN, 15);
        Chat.setFont(font);
        Chat.setLineWrap(true);         //�����Զ�����
        Chat.setLocation(0,0);
        Chat.setEditable(false);        //�����¼�޷��༭
        Chat.setMargin(new Insets(7, 7, 7, 7));     //����7mm�߾�
        JScrollPane jsp = new JScrollPane(Chat);         //����һ��������
        jsp.setBounds(0,0,500,500);
        //���ÿؼ�λ��
        Sendword.setLocation(0,500);
        Sendword.setSize(300,60);
        Sendword.setFont(font);
        Send.addActionListener(this);
        Send.setSize(95,60);
        Send.setLocation(300,500);
        Send.setFont(font);
        Send.setMargin(new Insets(0,0,0,0));   //���ð�ť�߿�ͱ�ǩ֮��հ�Ϊ0
        myClock.addActionListener(this);
        myClock.setSize(85,69);
        myClock.setLocation(500,360);
        myClock.setFont(font);
        myClock.setBackground(new Color(255,255,204));          //���ð�ť��ɫΪ����
        myClock.setMargin(new Insets(0,0,0,0));   //���ð�ť�߿�ͱ�ǩ֮��հ�Ϊ0
        Send_Record.addActionListener(this);
        Send_Record.setSize(95,60);
        Send_Record.setLocation(395,500);
        Send_Record.setFont(font);
        Send_Record.setMargin(new Insets(0,0,0,0));   //���ð�ť�߿�ͱ�ǩ֮��հ�Ϊ0
        Send_File.addActionListener(this);
        Send_File.setSize(95,60);
        Send_File.setLocation(490,500);
        Send_File.setFont(font);
        Send_File.setMargin(new Insets(0,0,0,0));   //���ð�ť�߿�ͱ�ǩ֮��հ�Ϊ0
        Leave.addActionListener(this);
        Leave.setSize(85,69);
        Leave.setLocation(500,429);
        Leave.setFont(font);
        Leave.setMargin(new Insets(0,0,0,0));   //���ð�ť�߿�ͱ�ǩ֮��հ�Ϊ0
        Leave.setBackground(new Color(255,255,204));          //���ð�ť��ɫΪ����
        listPane.setSize(86,220);
        listPane.setLocation(500,140);
        userList.setFont(font);
        userList.addMouseListener(this);    //���ڼ���˽��
        ImageIcon image = new ImageIcon(path);  //��ͼƬ·����Ϊ��������
        image.setImage(image.getImage().getScaledInstance(85,90,Image.SCALE_DEFAULT));  //�������Ű汾ͼ��
        JLabel Picture = new JLabel(image);
        Picture.setLocation(500,0);
        Picture.setSize(85,90);
        this.NickName = Nick;        //���캯���������
        myself.setText(NickName);
        myself.setSize(100,20);
        myself.setLocation(493,95);
        myself.setFont(new Font("΢���ź�", Font.BOLD, 15));
        friend_list.setSize(100,20);     //�����б��ĸ���
        friend_list.setLocation(493,120);
        friend_list.setFont(new Font("΢���ź�", Font.BOLD, 15));
        new Thread(this).start();
        //�ͻ����ļ���д�߳�����,���Լ�JFrame��Ϊ��������
        ClientFileThread fileThread = new ClientFileThread(NickName,this,ps);
        fileThread.start();
        //�����������
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
        this.setTitle(NickName+"��������");
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
                    ps.println("RUN-"+NickName);        //ʹ�������ܹ��ñ���֪����Ϣ
                    if(msgs[1].equals("1")) {
                        JOptionPane.showMessageDialog(this, "�Բ��������߳���");
                    }else{
                        JOptionPane.showMessageDialog(this, "�����뿪�����ң��ټ���");
                    }
                    this.dispose();
                }else if(msgs[0].equals("SLOGIN")){
                    if(msgs[1].equals("EVE")){
                        Chat.append("���Ϻ�,"+msgs[2]+"!��ӭ���붨��������!\n");
                    }else if(msgs[1].equals("MOR")){
                        Chat.append("���Ϻ�,"+msgs[2]+"!��ӭ���붨��������!\n");
                    }else if(msgs[1].equals("AFT")){
                        Chat.append("�����,"+msgs[2]+"!��ӭ���붨��������!\n");
                    }else{
                        Chat.append("�����,"+msgs[2]+"!��ӭ���붨��������!\n");
                    }
                    if(!msgs[2].equals(NickName)){
                        user.addElement(msgs[2]);       //���û���������б�
                    }
                    userList.repaint();
                } else if(msgs[0].equals("USER")){
                    user.addElement(msgs[1]);           //������Ϊ�ͻ��˷���֮ǰ�ĺ����б�
                } else if(msgs[0].equals("SLOGOUT")){
                    Chat.append(msgs[1]+"�û��Ѿ��뿪�����ҡ�\n");
                    user.removeElement(msgs[1]);    //���û��Ƴ������б�
                    userList.repaint();
                } else if(msgs[0].equals("SI")){
                    //�յ�˽�����󣬶�ȡ�����0������ܣ�1����ܾ�
                    int result = JOptionPane.showConfirmDialog(this,
                            msgs[1]+"���������˽�������Ƿ���ܣ�","˽������",JOptionPane.YES_NO_OPTION);
                    if(result == 0){
                        ps.println("ACCEPT-"+msgs[1]);  //����
                    }else{
                        ps.println("REFUSE-"+msgs[1]);  //�ܾ�
                    }
                }else if(msgs[0].equals("JUJUE")){
                    JOptionPane.showMessageDialog(this,msgs[1]+"�ܾ�������˽������!");
                }else if(msgs[0].equals("JIESHOU")) {
                    pr = new PrivateChat(this.NickName,msgs[1],path,ps,br,this.getX(),this.getY());    //��ʼ˽��
                }else if(msgs[0].equals("SIMESSAGE")){
                    pr.Chat.append(msgs[1]+" "+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+"\n");
                    pr.Chat.append(msgs[2]+"\n");
                }else if(msgs[0].equals("SID")){    //�յ����ڶ�����ʾ���������봰�ڶ���
                    new Thread() {      //�������ڶ����߳�
                        long begin = System.currentTimeMillis();
                        long end = System.currentTimeMillis();
                        Point p = pr.getLocationOnScreen();

                        public void run() {
                            int i = 1;
                            while ((end - begin) / 1000 < 2) {
                                pr.setLocation(new Point((int) p.getX() - 5 * i, (int) p.getY() + 5 * i));  //Point�������첢��ʼ����
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
            Sendword.setText("");               //����ı���
        }
        else if(e.getSource() == Send_Record){
            re = new RecordMain();           //����¼�ƽ���
            re.setLocationRelativeTo(this);  //�����ڱ�ҳ���м�
            Write();
            WaitingThread waiting = new WaitingThread();
            waiting.start();
        }else if(e.getSource() == Send_File){
            //���øú��������ļ�����������Ȼ����������ļ�����ת��
            FileDialog fLoader = new FileDialog(this,"ѡ��򿪵��ļ�",FileDialog.LOAD);
            fLoader.setVisible(true);
            Write();
            path = fLoader.getDirectory() + fLoader.getFile();
            ClientFileThread.outFileToServer(path,false,"all");
        }else if(e.getSource() == Leave){      //Ҫ����
            ps.println("LEAVE");
        }else{      //�鿴ϵͳʱ��
            new Clock(this.getX(),this.getY());
        }
    }

    public void Write(){
        try {
            File f = new File("range.txt");
            FileOutputStream fos = new FileOutputStream(f, false);
            PrintStream ps = new PrintStream(fos);
            ps.println("true");     //true�������ļ���false����˽���ļ�
            ps.println("all");      //ʵ�����ã����
            fos.close();
        }catch(Exception ex){}
    }

    public void mouseClicked(MouseEvent e) {
        if(e.getClickCount() == 2) {        //����˫���¼�
            ps.println("SILIAO" + "-" + userList.getSelectedValue());       //�������������˽����Ϣ
        }
    }
    //���¾�Ϊ��д����
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
                    //�Ѿ�¼�꣬���øú��������ļ���������
                    ClientFileThread.outFileToServer(MyRecorder.path,true,"all");
                    RecordMain.flag = false;    //��ֵ��Ϊfalse��������һ�η�����
                    break;
                }
            }
        }
    }
}