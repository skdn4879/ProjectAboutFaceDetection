package com.techtown.mygraduationprojectwithjava;

import android.widget.TextView;

public class ShowMessage implements Runnable{

    private String message;
    private TextView showTextView;

    public TextView getShowTextView() {
        return showTextView;
    }

    public void setShowTextView(TextView showTextView) {
        this.showTextView = showTextView;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ShowMessage(String str) {
        this.message = str;
    }

    public ShowMessage() {
    }

    @Override
    public void run() {
        showTextView.setText("회수된 메시지: " + message + "\n"); //메시지 출력
    }
}
