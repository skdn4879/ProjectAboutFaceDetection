package com.techtown.mygraduationprojectwithjava;

import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class ConnectThread extends Thread{

    //소켓 통신을 위한 스레드
    private Handler mHandler;
    Socket socket;
    private String ip; //서버의 ip주소
    private int port; //port번호를 꼭 맞춰주어야 한다.
    private String msg;
    private TextView showMsg;

    public ConnectThread(Handler handler, String addr, int portNum, TextView show){
        this.mHandler = handler;
        this.ip = addr;
        this.port = portNum;
        this.showMsg = show;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public void run() {
        super.run();
        try{
            InetAddress serverAddr = InetAddress.getByName(ip);
            socket = new Socket(serverAddr, port); //소켓 생성

            String sendMsg = msg; //입력 메시지
            Log.d("==========", sendMsg);

            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            out.println(sendMsg); //데이터 전송

            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String read = input.readLine(); //데이터 수신

            ShowMessage showMessage = new ShowMessage();
            showMessage.setShowTextView(showMsg);
            showMessage.setMessage(read);

            mHandler.post(showMessage); //화면에 표시
            Log.d("==========", read);
            socket.close(); //사용이 끝난 뒤 닫아주어야 한다.
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
