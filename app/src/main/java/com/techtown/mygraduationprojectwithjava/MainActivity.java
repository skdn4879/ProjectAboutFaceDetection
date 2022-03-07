package com.techtown.mygraduationprojectwithjava;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.techtown.mygraduationprojectwithjava.dialog.BaseDialog;
import com.techtown.mygraduationprojectwithjava.dialog.ListDialog;
import com.techtown.mygraduationprojectwithjava.valueschanges.ChangeVectorColor;

import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    private long backBtnTime = 0;
    BaseDialog baseDialog;
    ImageView btn_dialog_on;
    ImageView btn_dialog_off;
    BaseDialog cctvDialog;
    ImageView btn_cctv_close;
    Button btn_cctv_open;
    Button btn_cctv_reject;
    WebView frame_cctv;
    WebSettings frame_cctv_settings;

    ListDialog contactsListDialog;
    ImageView list_close_button;
    Button show_more_button;
    RecyclerView list_recyclerView;

    String message = "";
    ConnectThread connectThread;
    TextView statusMessage;
    String serverAddress = "192.168.0.19";
    int portNumber = 9999;

    String fcmId;

    DatabaseReference databaseRef;
    int dbStackCount;
    ContactListAdapter adapter;
    LinearLayoutManager linearLayoutManager;

    FirebaseStorage storage;

    ListDialog imageFrameDialog;
    ImageView btn_frame_close;
    ImageView image_frame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(!task.isSuccessful()){
                    Log.w("Fcm Error", "Fetching FCM registration token failed", task.getException());
                    return;
                }

                fcmId = task.getResult();
                Log.d("fcmId", fcmId);
            }
        });

        ImageView img_led1 = (ImageView) findViewById(R.id.img_led1);
        ImageView img_led2 = (ImageView) findViewById(R.id.img_led2);
        ImageView img_door = (ImageView) findViewById(R.id.img_door);
        ImageView img_circular = (ImageView) findViewById(R.id.img_circular);
        ImageView img_allDevice = (ImageView) findViewById(R.id.img_allDevice);
        BottomNavigationView bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottom_navigation);

        baseDialog = new BaseDialog(this, R.layout.dialog_led);
        btn_dialog_on = (ImageView) baseDialog.findViewById(R.id.btn_dialog_on);
        btn_dialog_off = (ImageView) baseDialog.findViewById(R.id.btn_dialog_off);

        cctvDialog = new BaseDialog(this, R.layout.dialog_cctv);
        btn_cctv_close = (ImageView) cctvDialog.findViewById(R.id.btn_cctv_close);
        btn_cctv_open = (Button) cctvDialog.findViewById(R.id.btn_cctv_open);
        //btn_cctv_reject = (Button) cctvDialog.findViewById(R.id.btn_cctv_reject);
        frame_cctv = (WebView) cctvDialog.findViewById(R.id.frame_cctv);

        contactsListDialog = new ListDialog(this, R.layout.fragment_list);
        list_close_button = (ImageView) contactsListDialog.findViewById(R.id.list_close_button);
        show_more_button = (Button) contactsListDialog.findViewById(R.id.show_more_button);
        list_recyclerView = (RecyclerView) contactsListDialog.findViewById(R.id.list_recyclerView);
        linearLayoutManager = new LinearLayoutManager(this);

        databaseRef = FirebaseDatabase.getInstance().getReference();
        adapter = new ContactListAdapter();

        storage = FirebaseStorage.getInstance();

        imageFrameDialog = new ListDialog(this, R.layout.contact_image_frame);
        btn_frame_close = (ImageView) imageFrameDialog.findViewById(R.id.btn_frame_close);
        image_frame = (ImageView) imageFrameDialog.findViewById(R.id.image_frame);

        ChangeVectorColor cvc = new ChangeVectorColor();
        cvc.changeColor(img_led1, R.color.sandyBrown, this);
        cvc.changeColor(img_led2, R.color.sandyBrown, this);
        cvc.changeColor(img_door, R.color.white, this);
        cvc.changeColor(img_circular, R.color.lawnGreen, this);
        cvc.changeColor(img_allDevice, R.color.aqua, this);

        statusMessage = (TextView)findViewById(R.id.statusMessage);

        img_led1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicker(baseDialog, btn_dialog_on, btn_dialog_off, 1);
            }
        });

        img_led2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicker(baseDialog, btn_dialog_on, btn_dialog_off, 2);
            }
        });

        img_door.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicker(baseDialog, btn_dialog_on, btn_dialog_off, 3);
            }
        });

        img_circular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicker(baseDialog, btn_dialog_on, btn_dialog_off, 4);
            }
        });

        img_allDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicker(baseDialog, btn_dialog_on, btn_dialog_off, 5);
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.btn_cctv:
                        frame_cctv.setWebViewClient(new WebViewClient());
                        frame_cctv_settings = frame_cctv.getSettings();
                        frame_cctv_settings.setJavaScriptEnabled(true);
                        frame_cctv_settings.setLoadWithOverviewMode(true);
                        frame_cctv_settings.setUseWideViewPort(true);
                        frame_cctv_settings.setCacheMode(frame_cctv_settings.LOAD_NO_CACHE);
                        frame_cctv_settings.setDomStorageEnabled(true);
                        frame_cctv.loadUrl("http://"+ serverAddress + ":8080/stream/video.mjpeg");
                        cctvDialog.show();
                        btn_cctv_close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                cctvDialog.cancel();
                            }
                        });
                        btn_cctv_open.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                message = "Door ON";
                                startConnectThread(message);
                                cctvDialog.cancel();
                            }
                        });
                        break;

                    case R.id.btn_contactsList:
                        dbStackCount = 0;
                        databaseRef.child("contacts").orderByKey().limitToLast(5).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                loadContactList(snapshot);
                                dbStackCount += 5;
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.d("firebaseError", error.toException().toString());
                            }
                        });
                        linearLayoutManager.setReverseLayout(true);
                        linearLayoutManager.setStackFromEnd(true);
                        list_recyclerView.setLayoutManager(linearLayoutManager);
                        list_recyclerView.setAdapter(adapter);
                        adapter.setOnItemClickListener(new OnContactsItemClickListener() {
                            @Override
                            public void onItemClick(ContactListAdapter.ItemViewHoler holder, View view, int position) {
                                ContactListItem pickedItem = adapter.items.get(position);
                                loadContactImageForFrame(pickedItem.getTime(), imageFrameDialog.findViewById(R.id.image_frame));
                                imageFrameDialog.show();
                                btn_frame_close.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        imageFrameDialog.cancel();
                                    }
                                });
                            }
                        });
                        contactsListDialog.show();
                        list_close_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                contactsListDialog.cancel();
                            }
                        });
                        show_more_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                databaseRef.child("contacts").orderByKey().limitToLast(dbStackCount + 5).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        loadContactList(snapshot);
                                        dbStackCount += 5;
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.d("firebaseError", error.toException().toString());
                                    }
                                });
                            }
                        });
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        long curTime = System.currentTimeMillis();
        long gapTime = curTime - backBtnTime;

        if(gapTime >= 0 && gapTime <= 2000){
            super.onBackPressed();
        } else{
            backBtnTime = curTime;
            Toast.makeText(this, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public void clicker(final BaseDialog dialog, ImageView dialog_on, ImageView dialog_off, int things){
        //다이얼로그를 띄우고 On버튼과 Off버튼, 그리고 기기를 구분하는 함수
        dialog.show();
        switch (things){    //정수 값에 따라 기기를 구분함
            case 1:
                message = "Led1";
                break;

            case 2:
                message = "Led2";
                break;

            case 3:
                message = "Door";
                break;

            case 4:
                message = "Circular";
                break;

            case 5:
                message = "All";
                break;
        }
        dialog_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                message = message + " ON";
                //on 버튼이 눌리면 메시지에 ON을 추가하고 서버에 전달
                startConnectThread(message);
                dialog.cancel();
            }
        });
        dialog_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                message = message + " OFF";
                //off 버튼이 눌리면 메시지에 OFF를 추가하고 서버에 전달
                startConnectThread(message);
                dialog.cancel();
            }
        });
    }

    public void startConnectThread(String message){
        connectThread = new ConnectThread(new Handler(), serverAddress, portNumber, statusMessage);
        connectThread.setMsg(message);
        connectThread.start();
    }

    class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ItemViewHoler> implements OnContactsItemClickListener {
        ArrayList<ContactListItem> items = new ArrayList<ContactListItem>();
        OnContactsItemClickListener listener;

        @NonNull
        @Override
        public ItemViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.item_contact_list, parent, false);
            return new ItemViewHoler(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHoler holder, int position) {
            holder.onBind(items.get(position));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public void setOnItemClickListener(OnContactsItemClickListener listener){
            this.listener = listener;
        }

        @Override
        public void onItemClick(ItemViewHoler holder, View view, int position) {
            if(listener != null){
                listener.onItemClick(holder, view, position);
            }
        }

        class ItemViewHoler extends RecyclerView.ViewHolder{
            private ImageView contactImageView;
            private TextView nameTextView;
            private TextView timeTextView;

            public ItemViewHoler(View itemView){
                super(itemView);

                contactImageView = (ImageView) itemView.findViewById(R.id.contactImageView);
                nameTextView = (TextView) itemView.findViewById(R.id.nameTextView);
                timeTextView = (TextView) itemView.findViewById(R.id.timeTextView);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();
                        if(listener != null){
                            listener.onItemClick(ItemViewHoler.this, view, position);
                        }
                    }
                });
            }

            void onBind(ContactListItem contactListItem){
                nameTextView.setText(contactListItem.getName());
                timeTextView.setText(contactListItem.getTime());
                loadContactImage(contactListItem.getTime(), contactImageView);
            }
        }
    }

    private void loadContactList(DataSnapshot dataSnapshot){
        /*Iterator<DataSnapshot> collectionIterator = dataSnapshot.getChildren().iterator();
        if(collectionIterator.hasNext()){
            adapter.items.clear();

            DataSnapshot contact = collectionIterator.next();
            Iterator<DataSnapshot> itemsIterator = contact.getChildren().iterator();
            while(itemsIterator.hasNext()){
                DataSnapshot currentItem = itemsIterator.next();

                String key = currentItem.getKey();

                ContactListItem currentItemData = currentItem.getValue(ContactListItem.class);
                String name = currentItemData.getName();
                String time = currentItemData.getTime();

                ContactListItem addToList = new ContactListItem(name, time);
                addToList.setObjectId(key);

                adapter.items.add(addToList);
            }

            adapter.notifyDataSetChanged();
        }*/

        Iterator<DataSnapshot> itemsIterator = dataSnapshot.getChildren().iterator();
        if(itemsIterator.hasNext()){
            adapter.items.clear();

            while(itemsIterator.hasNext()){
                DataSnapshot currentItem = itemsIterator.next();

                String key = currentItem.getKey();

                ContactListItem currentItemData = currentItem.getValue(ContactListItem.class);
                String name = currentItemData.getName();
                String time = currentItemData.getTime();

                ContactListItem addToList = new ContactListItem(name, time);
                addToList.setObjectId(key);

                adapter.items.add(addToList);
            }

            adapter.notifyDataSetChanged();
        }
    }

    private void loadContactImage(String time, ImageView contactImageFrame){
        String imageName = time.replace(':', '-');
        StorageReference storageRef = storage.getReference();
        storageRef.child("contactsImage/" + imageName + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(MainActivity.this).load(uri).into(contactImageFrame);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void loadContactImageForFrame(String time, ImageView frame){
        String imageName = time.replace(':', '-');
        StorageReference storageRef = storage.getReference();
        storageRef.child("contactsImage/" + imageName + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(MainActivity.this).load(uri).into(frame);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }
}