package yybos.hash.katarakt2.Fragments.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import yybos.hash.katarakt2.Fragments.ChatFragment;
import yybos.hash.katarakt2.Fragments.ViewHolders.ChatsViewHolder;
import yybos.hash.katarakt2.R;
import yybos.hash.katarakt2.Socket.Objects.Chat;

public class ChatsViewAdapter extends RecyclerView.Adapter<ChatsViewHolder> {
    private List<Chat> chats = new ArrayList<>();
    private ChatFragment chatFragmentInstance;

    public ChatsViewAdapter (List<Chat> history, ChatFragment chatFragment) {
        if (history != null)
            this.chats = history;

        if (chatFragment != null)
            this.chatFragmentInstance = chatFragment;
    }
    public ChatsViewAdapter (List<Chat> history) {
        if (history != null)
            this.chats = history;
    }

    public void addChat (Chat chat) {
        this.chats.add(chat);
        notifyItemChanged(this.chats.size() - 1);
    }

    @NonNull
    @Override
    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_chat, parent, false);
        return new ChatsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatsViewHolder holder, int position) {
        Chat chat = this.chats.get(position);

        holder.nameTextView.setText(chat.getName());
        holder.dateTextView.setText(String.valueOf(chat.getId()));

        holder.constraintLayout.setOnClickListener(v -> {
            this.chatFragmentInstance.updateMessageHistory(chat.getId());
            this.chatFragmentInstance.removeGeneralFrameLayout();
        });
        // Handle click events for likeButton, etc.
    }

    @Override
    public int getItemCount() {
        return this.chats.size();
    }
}
