package yybos.hash.katarakt2.Fragments.ViewAdapters;

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
import yybos.hash.katarakt2.Socket.Objects.User;

public class ChatViewAdapter extends RecyclerView.Adapter<ChatViewHolder> {
    private List<Message> messages = new ArrayList<>();

    public ChatViewAdapter (List<Message> history) {
        if (history != null && !history.isEmpty()) {
            this.messages = new ArrayList<>(history);
        }
    }

    public void addMessage (Message message) {
        this.messages.add(message);
        notifyItemChanged(this.messages.size() - 1);
    }
    public void updateUsername (User user) {
        for (int i = 0; i < this.messages.size(); i++) {
            Message message = this.messages.get(i);

            if (message.getUser().getId() == user.getId()) {
                message.getUser().setUsername(user.getUsername());
            }

            notifyItemChanged(i);
        }
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

        holder.usernameTextView.setText(message.getUser().getUsername());
        holder.contentTextView.setText(message.getMessage());
        holder.dateTextView.setText(message.getDate().toString());

        holder.user = message.getUser();

        // Handle click events for likeButton, etc.
    }

    @Override
    public int getItemCount() {
        return this.messages.size();
    }
}
