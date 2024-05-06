package online.classes.dailyupdates;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import online.classes.R;
import online.classes.adapter.MessageAdapter;

public class MessagesActivity extends AppCompatActivity {
    private ListView listView;
    private FirebaseFirestore db;
    private List<String> messages;
    private MessageAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        listView = findViewById(R.id.listView);
        db = FirebaseFirestore.getInstance();
        messages = new ArrayList<>();
        adapter = new MessageAdapter(this, R.layout.list_item_message, messages);
        listView.setAdapter(adapter);

        retrieveMessages();
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
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;

            default:return super.onOptionsItemSelected(item);
        }
    }
}