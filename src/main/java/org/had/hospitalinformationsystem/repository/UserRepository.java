package org.had.hospitalinformationsystem.repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.had.hospitalinformationsystem.model.User;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class UserRepository {

    public String createUser(User user) throws ExecutionException, InterruptedException {
        Firestore dbFireStore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> collectionApiFuture = dbFireStore.collection("users").document(user.getDocumentId()).set(user);
        return collectionApiFuture.get().getUpdateTime().toString();
    }


    public User getUser(String userName) throws ExecutionException, InterruptedException {
        Firestore dbFireStore = FirestoreClient.getFirestore();

        CollectionReference usersCollection = dbFireStore.collection("users");
        Query query = usersCollection.whereEqualTo("userName", userName);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();

        for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
            if (document.exists()) {
                return document.toObject(User.class);
            }
        }
        return null;
    }
}
