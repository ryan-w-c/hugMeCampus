package com.diamondTierHuggers.hugMeCampus.main;

import androidx.annotation.NonNull;

import com.diamondTierHuggers.hugMeCampus.data.AcceptListModel;
import com.diamondTierHuggers.hugMeCampus.entity.HugMeUser;
import com.diamondTierHuggers.hugMeCampus.entity.HugRating;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class AppUser {

    private HugMeUser appUser;
    public static HashMap<String, HugMeUser> savedHugMeUsers = new HashMap<>();
    public static ArrayList<HugRating> savedHugRatings = new ArrayList<>();
    public static AcceptListModel acceptListModel = new AcceptListModel();
    public static Double lat, lng;


    public AppUser(){
    }

    public void readData(Query ref, final OnGetDataListener listener) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    appUser = dataSnapshot.getValue(HugMeUser.class);
//                    FirebaseAuth auth = FirebaseAuth.getInstance();
//                    appUser.setUid(auth.getUid());
                    appUser.setUid(auth.getUid());
//                    mq = new MatchMakingQueue();
                }
                listener.onSuccess("");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("ERROR: retrieving user data from db for matchmaking");
            }

        });
    }

    public HugMeUser getAppUser() {
        return appUser;
    }

}
