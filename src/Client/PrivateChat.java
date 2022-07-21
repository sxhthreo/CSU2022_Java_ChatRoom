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
    private JButton Send = new JButton("��������");
    private JButton Send_Record = new JButton("��������");
    private JButton Send_File = new JButton("�����ļ�");
    private JButton shake = new JButton("���ڶ���");
    private JTextField Sendword = new JTextField(20);       //����������
    public JTextArea Chat = new JTextArea(10,45);     //�����¼��ʾ
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
        this.Acceptor = name2;      //˽�Ķ���
        Font font = new Font("΢���ź�", Font.PLAIN, 15);
        Chat.setFont(font);
        Chat.setLineWrap(true);         //�����Զ�����
        Chat.setLocation(0,0);
        Chat.setEditable(false);        //�����¼�޷��༭
        Chat.setMargin(new Insets(7, 7, 7, 7));     //����7mm�߾�
        JScrollPane jsp = new JScrollPane(Chat);         //����һ��������
        jsp.setBounds(0,0,500,500);    //��500��500
        Sendword.addActionListener(this);
        Sendword.setLocation(0,500);
        Sendword.setSize(300,60);
        Sendword.setFont(font);
        Send.addActionListener(this);
        Send.setSize(95,60);
        Send.setLocation(300,500);
        Send.setFont(new Font("΢���ź�", Font.PLAIN, 15));
        Send.setMargin(new Insets(0,0,0,0));   //���ð�ť�߿�ͱ�ǩ֮��հ�Ϊ0
        Send_Record.addActionListener(this);
        Send_Record.setSize(95,60);
        Send_Record.setLocation(395,500);
        Send_Record.setFont(new Font("΢���ź�", Font.PLAIN, 15));
        Send_Record.setMargin(new Insets(0,0,0,0));   //���ð�ť�߿�ͱ�ǩ֮��հ�Ϊ0
        Send_File.addActionListener(this);
        Send_File.setSize(95,60);
        Send_File.setLocation(490,500);
        Send_File.setFont(new Font("΢���ź�", Font.PLAIN, 15));
        Send_File.setMargin(new Insets(0,0,0,0));   //���ð�ť�߿�ͱ�ǩ֮��հ�Ϊ0
        shake.addActionListener(this);
        shake.setSize(85,69);
        shake.setLocation(500,429);
        shake.setFont(font);
        shake.setMargin(new Insets(0,0,0,0));   //���ð�ť�߿�ͱ�ǩ֮��հ�Ϊ0
        shake.setBackground(new Color(255,255,204));          //���ð�ť��ɫΪ����
        ImageIcon image = new ImageIcon(path);  //��ͼƬ·����Ϊ��������
        image.setImage(image.getImage().getScaledInstance(85,90,Image.SCALE_DEFAULT));  //�������Ű汾ͼ��
        JLabel Picture = new JLabel(image);
        Picture.setLocation(500,0);
        Picture.setSize(85,90);
        myself.setText(NickName);
        myself.setSize(80,20);
        myself.setLocation(530,95);
        myself.setFont(new Font("΢���ź�", Font.BOLD, 15));
        //�����������
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
        this.setTitle("�����ں�"+name2+"˽��");
        this.setSize(600,598);
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == Send) {
            ps.println("SI-" + NickName + "-" + Acceptor + "-" + Sendword.getText());
            Sendword.setText("");               //����ı���
        } else if (e.getSource() == Send_Record) {
            re = new RecordMain();           //����¼�ƽ���
            re.setLocationRelativeTo(this);  //�����ڱ�ҳ���м�
            Write();        //��range_flag��ֵд��
            WaitingThread waiting = new WaitingThread();
            waiting.start();
        } else if (e.getSource() == Send_File) {
            //���øú��������ļ�����������Ȼ����������ļ�����ת��
            FileDialog fLoader = new FileDialog(this, "ѡ��Ҫ���͵��ļ�", FileDialog.LOAD);
            fLoader.setVisible(true);
            Write();        //��range_flag��ֵд��
            path = fLoader.getDirectory() + fLoader.getFile();
            ClientFileThread.outFileToServer(path, false,Acceptor);
        }else{      //���ʹ��ڶ���
            ps.println("SID-" + NickName + "-" + Acceptor);
        }
    }

    public void Write(){
        try {
            File f = new File("range.txt");
            FileOutputStream fos = new FileOutputStream(f, false);
            PrintStream ps = new PrintStream(fos);
            ps.println("false");
            ps.println(Acceptor);   //�ַ�������д���ļ���
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
                    //�Ѿ�¼�꣬���øú��������ļ���������
                    ClientFileThread.outFileToServer(MyRecorder.path,true,Acceptor);
                    RecordMain.flag = false;    //��ֵ��Ϊfalse��������һ�η�����
                    break;
                }
            }
        }
    }
}