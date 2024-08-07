package com.nkcdev.chatapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.mlkit.nl.smartreply.SmartReply;
import com.google.mlkit.nl.smartreply.SmartReplyGenerator;
import com.google.mlkit.nl.smartreply.SmartReplySuggestion;
import com.google.mlkit.nl.smartreply.SmartReplySuggestionResult;
import com.google.mlkit.nl.smartreply.TextMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyChatActivity extends AppCompatActivity {

    private ImageView imageViewBack;
    private TextView textViewChat;
    private EditText editTextMessage;
    private FloatingActionButton fab;
    private RecyclerView rvChat, rvSmartReplies;

    String userName, otherName;
    FirebaseDatabase database;
    DatabaseReference reference;

    MessageAdapter adapter;
    List<ModelClass> list;

    //AI - Smart Reply
    private List<TextMessage> conversation = new ArrayList<>();
    private SmartReplyAdapter smartReplyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imageViewBack = findViewById(R.id.imageViewBack);
        textViewChat = findViewById(R.id.textViewChat);
        editTextMessage = findViewById(R.id.editTextMessage);
        fab = findViewById(R.id.fab);
        rvChat = findViewById(R.id.rvChat);

        rvChat.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        userName = getIntent().getStringExtra("userName");
        otherName = getIntent().getStringExtra("otherName");
        textViewChat.setText(otherName);

        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MyChatActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = editTextMessage.getText().toString();
//                if (!message.equals("")) {
//                    sendMessage(message);
//                    editTextMessage.setText("");
//                }
                //AI
                if (!message.isEmpty()) {
                    sendMessage(message);
                    addMessageToConversation(message, true);
                    editTextMessage.setText("");
                    suggestReplies();
                }
            }
        });

        getMessage();

        //AI
        rvSmartReplies = findViewById(R.id.rvSmartReplies);
        rvSmartReplies.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        smartReplyAdapter = new SmartReplyAdapter(new ArrayList<>(), this::onSmartReplyClicked);
        rvSmartReplies.setAdapter(smartReplyAdapter);

    }

    private void getMessage() {
        reference.child("Messages").child(userName).child(otherName).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ModelClass modelClass = snapshot.getValue(ModelClass.class);
                list.add(modelClass);
                adapter.notifyDataSetChanged();
                rvChat.scrollToPosition(list.size() - 1);

                // AI - Add received message to conversation
                addMessageToConversation(modelClass.getMessage(), false);
                suggestReplies();
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

        adapter = new MessageAdapter(list, userName);
        rvChat.setAdapter(adapter);
    }

    public void sendMessage(String message) {
        String key = reference.child("Messages").child(userName).child(otherName).push().getKey();
        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("message", message);
        messageMap.put("from", userName);
        reference.child("Messages").child(userName).child(otherName).child(key).setValue(messageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    reference.child("Messages").child(otherName).child(userName).child(key).setValue(messageMap);
                }
            }
        });
    }

    private void addMessageToConversation(String message, boolean isLocal) {
        TextMessage textMessage = isLocal ? TextMessage.createForLocalUser(message, System.currentTimeMillis()) :
                TextMessage.createForRemoteUser(message, System.currentTimeMillis(), "user");
        conversation.add(textMessage);
    }

    private void suggestReplies() {
        SmartReplyGenerator smartReply = SmartReply.getClient();
        smartReply.suggestReplies(conversation)
                .addOnSuccessListener(new OnSuccessListener<SmartReplySuggestionResult>() {
                    @Override
                    public void onSuccess(SmartReplySuggestionResult result) {
                        if (result.getStatus() == SmartReplySuggestionResult.STATUS_SUCCESS) {
                            List<SmartReplySuggestion> suggestions = result.getSuggestions();
                            List<String> replies = new ArrayList<>();
                            for (SmartReplySuggestion suggestion : suggestions) {
                                replies.add(suggestion.getText());
                            }
                            smartReplyAdapter.updateReplies(replies);
                        }
                    }
                });
    }

    private void onSmartReplyClicked(String reply) {
        sendMessage(reply);
        addMessageToConversation(reply, true);
        suggestReplies();
    }
}