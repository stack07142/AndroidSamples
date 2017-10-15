package io.github.stack07142.sample_firestore_db;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Random;


public class MainActivity extends AppCompatActivity {

    ListView listView;
    EditText editText;
    Button sendButton;

    ArrayAdapter<String> adapter;

    String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final CollectionReference mMessageRef = FirebaseFirestore.getInstance().collection("message");

        Log.d("TEST", "onCreate()");

        listView = (ListView) findViewById(R.id.listview);
        editText = (EditText) findViewById(R.id.editText);
        sendButton = (Button) findViewById(R.id.btn_send);

        userName = "user" + new Random().nextInt(10000);  // 랜덤한 유저 이름 설정 ex) user1234

        // 기본 Text를 담을 수 있는 simple_list_item_1을 사용해서 ArrayAdapter를 만들고 listview에 설정
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        listView.setAdapter(adapter);

        // Add Data
        sendButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                ChatData chatData = new ChatData(userName, editText.getText().toString());  // 유저 이름과 메세지로 chatData 만들기

                mMessageRef.add(chatData)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {

                                Log.d("TEST", "onSuccess()");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Log.d("TEST", "onFailure()");
                            }
                        });
                editText.setText("");
            }
        });

        // Get Realtime Updates
        mMessageRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                for (DocumentChange dc : documentSnapshots.getDocumentChanges()) {

                    switch (dc.getType()) {

                        case ADDED:

                            Log.d("TEST", "Added: " + dc.getDocument().getData().get("msg"));

                            ChatData chatData = dc.getDocument().toObject(ChatData.class);
                            adapter.add(chatData.getUserName() + ": " + chatData.getMsg());  // adapter에 추가

                            break;

                        case MODIFIED:

                            Log.d("TEST", "Modified: " + dc.getDocument().getData());
                            break;

                        case REMOVED:
                            Log.d("TEST", "Removed: " + dc.getDocument().getData());
                            break;
                    }
                }
            }
        });
    } // ~onCreate
}
