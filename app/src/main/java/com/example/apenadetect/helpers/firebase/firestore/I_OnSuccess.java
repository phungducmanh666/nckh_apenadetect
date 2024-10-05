package com.example.apenadetect.helpers.firebase.firestore;

import com.google.firebase.firestore.DocumentReference;

public interface I_OnSuccess {
    public void exec(DocumentReference reference);
}
