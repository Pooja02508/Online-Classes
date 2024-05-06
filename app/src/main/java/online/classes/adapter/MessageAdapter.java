package online.classes.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import online.classes.R;

public class MessageAdapter extends ArrayAdapter<String> {

    private Context context;
    private List<String> messages;

    public MessageAdapter(@NonNull Context context, int resource, @NonNull List<String> messages) {
        super(context, resource, messages);
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(R.layout.list_item_message, parent, false);
        }

        String message = messages.get(position);

        TextView messageTextView = listItemView.findViewById(R.id.messageTextView);
        messageTextView.setText(message);

        return listItemView;
    }
}
