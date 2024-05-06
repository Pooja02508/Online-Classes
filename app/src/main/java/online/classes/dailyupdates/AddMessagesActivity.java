package online.classes.dailyupdates;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import online.classes.R;
import online.classes.adapter.MessageAdapter;

public class AddMessagesActivity extends AppCompatActivity {

    EditText message;
    Button add;
    FirebaseFirestore db;
    private ListView listView;
    private List<String> messages;
    private MessageAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_messages);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        message=findViewById(R.id.message);
        add=findViewById(R.id.add);
        listView = findViewById(R.id.listView);

        db = FirebaseFirestore.getInstance();
        messages = new ArrayList<>();
        adapter = new MessageAdapter(this, R.layout.list_item_message, messages);
        listView.setAdapter(adapter);

        retrieveMessages();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String clickedMessage = messages.get(position);
                openMessageActivity(clickedMessage);
            }
        });


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the message text
                String messageText = message.getText().toString();
                final String randomKey = UUID.randomUUID().toString();
                // Create a new message map
                Map<String, Object> messageMap = new HashMap<>();
                messageMap.put("message", messageText);

                // Add the message to Firestore
                db.collection("DailyUpdates")
                        .document("Messages") // Generate a new document ID
                        .collection("MessageList")
                        .document(randomKey)
                        .set(messageMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // Handle successful addition
                                    // For example, show a success message
                                    Toast.makeText(AddMessagesActivity.this, "Message added successfully!", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Handle failure
                                    // For example, show an error message
                                    Toast.makeText(AddMessagesActivity.this, "Failed to add message. Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    private void openMessageActivity(String message) {
        // Open new activity to display the message
        Intent intent = new Intent(this, UpdateMessage.class);
        intent.putExtra("message", message);
        startActivity(intent);
    }
    private void retrieveMessages() {
        db.collection("DailyUpdates")
                .document("Messages")
                .collection("MessageList")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String message = document.getString("message");
                                messages.add(message);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            // Handle failure
                        }
                    }
                });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}