package com.diamondTierHuggers.hugMeCampus.friendList;

import static com.diamondTierHuggers.hugMeCampus.loginRegisterForgot.LoginFragment.appUser;
import static com.diamondTierHuggers.hugMeCampus.main.LoginRegisterActivity.database;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.diamondTierHuggers.hugMeCampus.R;
import com.diamondTierHuggers.hugMeCampus.chatBox.ChatAdapter;
import com.diamondTierHuggers.hugMeCampus.chatBox.ChatItem;
import com.diamondTierHuggers.hugMeCampus.databinding.FragmentItemBinding;
import com.diamondTierHuggers.hugMeCampus.entity.HugMeUser;
import com.diamondTierHuggers.hugMeCampus.main.OnGetDataListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MyListItemRecyclerViewAdapter extends RecyclerView.Adapter<MyListItemRecyclerViewAdapter.ViewHolder> {

    private List<HugMeUser> mValues = new ArrayList<>();
    private OnItemListener mOnItemListener;
    private int position = 0;

    public HugMeUser getItem(int i) {
        return mValues.get(i);
    }

    public void addItem(HugMeUser h) {
        mValues.add(h);
    }

    public void readData(Query ref, String uid, final OnGetDataListener listener) {

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot user : dataSnapshot.getChildren()) {
                        HugMeUser h = user.getValue(HugMeUser.class);
                        h.setUid(uid);
                        appUser.savedHugMeUsers.put(uid, h);
                        addItem(h);
                    }
                }
                listener.onSuccess("");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("ERROR: retrieving user data from db for matchmaking");
            }

        });
    }

    public MyListItemRecyclerViewAdapter(OnItemListener onItemListener, boolean friends_list) {

        if (friends_list) {
            for (String uid : appUser.getAppUser().friend_list.keySet()) {
                if (appUser.savedHugMeUsers.containsKey(uid)) {
                    addItem(appUser.savedHugMeUsers.get(uid));
                }
                else {
                    readData(database.getReference("users").orderByKey().equalTo(uid), uid, new OnGetDataListener() {
                        @Override
                        public void onSuccess(String dataSnapshotValue) {
                            MyListItemRecyclerViewAdapter.super.notifyDataSetChanged();
                        }
                    });
                }
            }

            database.getReference("users").child(appUser.getAppUser().getUid()).child("friend_list").orderByKey().addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    System.out.println(snapshot.getKey());
                    String uid = snapshot.getKey();
                    if (!appUser.getAppUser().friend_list.containsKey(uid)) {
                        if (appUser.savedHugMeUsers.containsKey(uid)) {
                            appUser.getAppUser().friend_list.put(uid, true);
                            addItem(appUser.savedHugMeUsers.get(uid));
                        } else if (!uid.equals("DO_NOT_DELETE")) {
                            appUser.getAppUser().friend_list.put(uid, true);
                            readData(database.getReference("users").orderByKey().equalTo(uid), uid, new OnGetDataListener() {
                                @Override
                                public void onSuccess(String dataSnapshotValue) {
                                    MyListItemRecyclerViewAdapter.super.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else {
//            for (String uid : appUser.getAppUser().request_list.keySet()) {
//                if (appUser.savedHugMeUsers.containsKey(uid)) {
//                    addItem(appUser.savedHugMeUsers.get(uid));
//                }
//                else {
//                    readData(database.getReference("users").orderByKey().equalTo(uid), uid, new OnGetDataListener() {
//                        @Override
//                        public void onSuccess(String dataSnapshotValue) {
//                            MyListItemRecyclerViewAdapter.super.notifyDataSetChanged();
//                        }
//                    });
//                }
//            }
            for (String uid : appUser.getAppUser().pending_list.keySet()) {
                if (appUser.savedHugMeUsers.containsKey(uid)) {
                    addItem(appUser.savedHugMeUsers.get(uid));
                }
                else {
                    readData(database.getReference("users").orderByKey().equalTo(uid), uid, new OnGetDataListener() {
                        @Override
                        public void onSuccess(String dataSnapshotValue) {
                            MyListItemRecyclerViewAdapter.super.notifyDataSetChanged();
                        }
                    });
                }
            }

            database.getReference("users").child(appUser.getAppUser().getUid()).child("request_list").orderByKey().addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                    ChatItem c = snapshot.getValue(ChatItem.class);
//                    chatItems.add(c);
//                    ChatAdapter.super.notifyDataSetChanged();
                    String uid = snapshot.getKey();
                    if (appUser.savedHugMeUsers.containsKey(uid)) {
                        appUser.getAppUser().request_list.put(uid, true);
                        addItem(appUser.savedHugMeUsers.get(uid));
                    }
                    else if (!uid.equals("DO_NOT_DELETE")) {
                        appUser.getAppUser().request_list.put(uid, true);
                        readData(database.getReference("users").orderByKey().equalTo(uid), uid, new OnGetDataListener() {
                            @Override
                            public void onSuccess(String dataSnapshotValue) {
                                MyListItemRecyclerViewAdapter.super.notifyDataSetChanged();
                            }
                        });
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }
        mOnItemListener = onItemListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(FragmentItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), mOnItemListener);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.set();
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView mProfileName, mRequestPendingText;
        public HugMeUser mItem;
        public OnItemListener onItemListener;
        public CardView mRequestPendingCard;
        public ImageView mImageView;

        public ViewHolder(FragmentItemBinding binding, OnItemListener onItemListener) {
            super(binding.getRoot());
            this.onItemListener = onItemListener;
            this.mProfileName = binding.getRoot().findViewById(R.id.profile_name);
            this.mRequestPendingText = binding.getRoot().findViewById(R.id.request_pending);
            this.mRequestPendingCard = binding.getRoot().findViewById(R.id.request_pending_card);
            this.mImageView = binding.getRoot().findViewById(R.id.list_item_profile_photo);
            binding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemListener.onItemClick(getAdapterPosition());
        }

        public void set() {
            this.mProfileName.setText(this.mItem.first_name + " " + this.mItem.last_name);
            //set profile pictures/current pictures
//            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
//
//            StorageReference profileRef = storageReference.child("profile Images/" + mItem.getUid() + "profilePic_1" + ".jpg");
//            profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                @Override
//                public void onSuccess(Uri uri) {
//                    Picasso.get().load(uri).into(mImageView);
//                }
//            });
            Picasso.get().load(mItem.getPicture("picture1")).into(mImageView);
            if (mItem.getFriendRequestPending() == 2) {
                this.mRequestPendingText.setText("Accept Request");
                this.mRequestPendingText.setTextColor(0xff34223b);
                this.mRequestPendingCard.setCardBackgroundColor(0xff03dac5);
            }
            else if (mItem.getFriendRequestPending() == 1) {
                this.mRequestPendingText.setText("Pending");
                this.mRequestPendingText.setTextColor(0xffffffff);
                this.mRequestPendingCard.setCardBackgroundColor(0xff34223b);
            }
        }
    }

    public interface OnItemListener {
        void onItemClick(int position);
    }
}