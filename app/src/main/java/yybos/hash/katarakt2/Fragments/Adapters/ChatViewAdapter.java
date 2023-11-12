package yybos.hash.katarakt2.Fragments.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import yybos.hash.katarakt2.Fragments.ViewHolders.ChatViewHolder;
import yybos.hash.katarakt2.R;
import yybos.hash.katarakt2.Socket.Objects.Message;

public class ChatViewAdapter extends RecyclerView.Adapter<ChatViewHolder> {
    private List<Message> messages = new ArrayList<>();

    public ChatViewAdapter (List<Message> history) {
        if (history != null && !history.isEmpty()) {
            this.messages = history;
        }
    }

    public void addMessage (Message message) {
        this.messages.add(message);
        notifyItemChanged(this.messages.size() - 1);
    }
    public void clear () {
        this.messages.clear();
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_message, parent, false);
        return new ChatViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Message message = this.messages.get(position);

        holder.usernameTextView.setText(message.getUser());
        holder.contentTextView.setText(message.getContent());
        holder.dateTextView.setText(message.getDate().toString());

        // Handle click events for likeButton, etc.
    }

    @Override
    public int getItemCount() {
        return this.messages.size();
    }
}
