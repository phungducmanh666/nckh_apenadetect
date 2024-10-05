package com.example.apenadetect.helpers.firebase.firestore;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public interface I_OnTracking {
    public void exec(QuerySnapshot snapshot, FirebaseFirestoreException exception);
}
