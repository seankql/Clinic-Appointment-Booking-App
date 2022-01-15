package com.example.b07project;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Model {
    //contains interaction with database
    List<String> userAndPass;
    private String path;
    public Model(String path){
        Log.i("user/pass", "model created");
        this.path = path;
        userAndPass = new ArrayList<String>();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference(path);
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot d : dataSnapshot.getChildren()){
                    if(!userAndPass.contains(d.child("username").getValue().toString())){
                        Log.i("user/pass", d.child("username").getValue().toString() + " " + d.child("password").getValue().toString());
                        userAndPass.add(d.child("username").getValue().toString());
                        userAndPass.add(d.child("password").getValue().toString());
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("onCancelled", " cancelled");
            }
        });
    }

    public boolean authenticateUser(String username, String password){
        int i = userAndPass.indexOf(username);
        if(userAndPass==null){
            Log.i("null", "userAndPass is null");
            return false;
        }
        if(userAndPass.get(i+1).equals(password)){
            return true;
        }
        return false;
    }

    public boolean createUser(User user){
        if(userAndPass.contains(user.getUsername())){
            return false;
        }
        else{
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference(path);
            rootRef.child(user.getUsername()).setValue(user);
            return true;
        }
    }
}
