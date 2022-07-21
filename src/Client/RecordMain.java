package Client;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
public class RecordMain extends JFrame implements ActionListener{
    private static final long serialVersionUID = -1082166342481848841L;    //Java序列化机制
    private JButton beginBtn = new JButton("开始录音");
    private JButton stopBtn = new JButton("停止录音");
    private MyRecorder Recorder1 = new MyRecorder();
    public static Boolean flag = false;
    public RecordMain(){
        Font font = new Font("微软雅黑", Font.BOLD, 30);
        this.setLayout(null);
        beginBtn.setSize(300,140);
        beginBtn.setLocation(0,0);
        beginBtn.addActionListener(this);
        beginBtn.setFont(font);
        stopBtn.addActionListener(this);
        stopBtn.setSize(300,140);
        stopBtn.setFont(font);
        stopBtn.setLocation(0,0);
        stopBtn.setVisible(false);      //初始设置只能看到开始按钮
        this.add(beginBtn);
        this.add(stopBtn);
        this.setTitle("等待开始录音……");
        this.setSize(310,176);
        this.setVisible(true);
    }
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == beginBtn){
            Recorder1.start();
            stopBtn.setVisible(true);
            beginBtn.setVisible(false);     //开始按钮消失
            this.setTitle("正在录音中……");
        }else{
            stopBtn.setVisible(false);
            beginBtn.setVisible(true);
            Recorder1.stop();
            flag = true;        //代表录完了
            this.setVisible(false);
        }
    }
}