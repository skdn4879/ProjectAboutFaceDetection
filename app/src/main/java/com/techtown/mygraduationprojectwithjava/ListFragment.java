package com.techtown.mygraduationprojectwithjava;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    protected Context mContext;
    private DatabaseReference databaseRef;
    private ContactListAdapter adapter;

    public ListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListFragment newInstance(String param1, String param2) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        this.mContext = context;
        this.databaseRef = FirebaseDatabase.getInstance().getReference();
        this.adapter = new ContactListAdapter();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_list, container, false);
        ImageView list_close_button = (ImageView) rootView.findViewById(R.id.list_close_button);
        Button show_more_button = (Button) rootView.findViewById(R.id.show_more_button);
        RecyclerView list_recyclerView = (RecyclerView) rootView.findViewById(R.id.list_recyclerView);

        databaseRef.orderByKey().limitToFirst(5).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ContactListItem contactListItem = snapshot.getValue(ContactListItem.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("firebaseError", error.toException().toString());
            }
        });

        list_close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return rootView;
    }

    class ContactListAdapter extends BaseAdapter {
        ArrayList<ContactListItem> items = new ArrayList<ContactListItem>();
        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int i) {
            return items.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        public void addItem(ContactListItem contactListItem){
            items.add(contactListItem);
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ContactListView contactListView = new ContactListView(mContext);

            ContactListItem contactListItem = items.get(i);
            //contactListView.setContactImageView();
            contactListView.setNameTextView(contactListItem.getName());
            contactListView.setTimeTextView(contactListItem.getTime());
            return contactListView;
        }
    }

    private void loadContactList(DataSnapshot dataSnapshot){
        Iterator<DataSnapshot> collectionIterator = dataSnapshot.getChildren().iterator();
        if(collectionIterator.hasNext()){
            adapter.items.clear();

            DataSnapshot contact = collectionIterator.next();
            Iterator<DataSnapshot> itemsIterator = contact.getChildren().iterator();
            while(itemsIterator.hasNext()){
                DataSnapshot currentItem = itemsIterator.next();

                String key = currentItem.getKey();

                Object currentItemData = currentItem.getValue();
                //String name = currentItemData
            }
        }
    }
}