package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerFileThread extends Thread{
	ServerSocket server = null;
	Socket socket = null;
	static List<Socket> list = new ArrayList<Socket>();  // �洢�ͻ���
	
	public void run() {
		try {
			server = new ServerSocket(9990);
			while(true) {
				socket = server.accept();
				list.add(socket);
				// �����ļ������߳�
				FileReadAndWrite fileReadAndWrite = new FileReadAndWrite(socket);
				fileReadAndWrite.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

class FileReadAndWrite extends Thread {
	private Socket nowSocket = null;
	private DataInputStream input = null;
	private DataOutputStream output = null;
	
	public FileReadAndWrite(Socket socket) {
		this.nowSocket = socket;
	}
	public void run() {
		try {
			input = new DataInputStream(nowSocket.getInputStream());  // ������
			while (true) {
				// ��ȡ�ļ����ֺ��ļ�����
				String textName = input.readUTF();
				long textLength = input.readLong();
				// �����ļ����ֺ��ļ����ȸ����пͻ���
				for(Socket socket: ServerFileThread.list) {
					output = new DataOutputStream(socket.getOutputStream());  // �����
					if(socket != nowSocket) {  // ���͸������ͻ���
						output.writeUTF(textName);
						output.flush();		   //��������
						output.writeLong(textLength);	//д���ļ�����
						output.flush();
					}
				}
				// �����ļ�����
				int length = -1;
				long curLength = 0;
				byte[] buff = new byte[1024];
				while ((length = input.read(buff)) > 0) {
					curLength += length;
					for(Socket socket: ServerFileThread.list) {
						output = new DataOutputStream(socket.getOutputStream());  // �����
						if(socket != nowSocket) {  // ���͸������ͻ���
							output.write(buff, 0, length);
							output.flush();
						}
					}
					if(curLength == textLength) {  // ǿ���˳����Ѿ�������
						break;
					}
				}
			}
		} catch (Exception e) {
			ServerFileThread.list.remove(nowSocket);  // �̹߳رգ��Ƴ���Ӧ�׽���
		}
	}
}