package com.techtown.mygraduationprojectwithjava;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ContactListView extends RelativeLayout {
    private ImageView contactImageView;
    private TextView nameTextView;
    private TextView timeTextView;

    public ContactListView(Context context) {
        super(context);
        init(context);
    }

    public ContactListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.item_contact_list, this, true);
        contactImageView = (ImageView) findViewById(R.id.contactImageView);
        nameTextView = (TextView) findViewById(R.id.nameTextView);
        timeTextView = (TextView) findViewById(R.id.timeTextView);
    }

    public void setContactImageView(int contactImage){
        contactImageView.setImageResource(contactImage);
    }
    public void setNameTextView(String name){
        nameTextView.setText(name);
    }
    public void setTimeTextView(String time){
        timeTextView.setText(time);
    }
}
