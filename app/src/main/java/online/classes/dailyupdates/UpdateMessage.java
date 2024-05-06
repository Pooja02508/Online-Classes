package online.classes.dailyupdates;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.Objects;
import online.classes.R;

public class UpdateMessage extends AppCompatActivity {

    EditText message;
    TextView msg;
    Button updateMsg, deleteMsg;
    FirebaseFirestore db;
    String Message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_message);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        db = FirebaseFirestore.getInstance();

        Message = getIntent().getStringExtra("message");

        updateMsg = findViewById(R.id.updateMsg);
        deleteMsg = findViewById(R.id.deleteMsg);
        message = findViewById(R.id.message);
        msg=findViewById(R.id.msg);

        message.setText(Message);

        updateMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String updatedMessageText = message.getText().toString();
                if (!updatedMessageText.isEmpty()) {
                    updateMessageInFirestore(updatedMessageText);
                } else {
                    Toast.makeText(UpdateMessage.this, "Please enter a message to update", Toast.LENGTH_SHORT).show();
                }
            }
        });


        deleteMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deleteMessageFromFirestore(message.getText().toString());
            }
        });

    }

    private void updateMessageInFirestore(String updatedMessageText) {
        db.collection("DailyUpdates")
                .document("Messages")
                .collection("MessageList")
                .whereEqualTo("message", Message)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String messageId = document.getId();
                                DocumentReference messageRef = db.collection("DailyUpdates")
                                        .document("Messages")
                                        .collection("MessageList")
                                        .document(messageId);

                                messageRef.update("message", updatedMessageText)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(UpdateMessage.this, "Message updated successfully!", Toast.LENGTH_SHORT).show();
                                                finish(); // Finish the activity after successful update
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(UpdateMessage.this, "Failed to update message. Please try again.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(UpdateMessage.this, "Error finding message in Firestore.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



    private void deleteMessageFromFirestore(final String updateMsg) {
        // Query Firestore to find the document containing the given imageUrl
        db.collection("DailyUpdates").document("Messages").collection("MessageList")
                .whereEqualTo("message", updateMsg)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@android.support.annotation.NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Delete the document containing the imageUrl
                                document.getReference().delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@android.support.annotation.NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    // Document deleted successfully
                                                    // deletePhotoFromStorage(photoUrl);
                                                    Toast.makeText(UpdateMessage.this, "Message deleted successfully", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(UpdateMessage.this, "Failed to delete message", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(UpdateMessage.this, "Error finding message document", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
