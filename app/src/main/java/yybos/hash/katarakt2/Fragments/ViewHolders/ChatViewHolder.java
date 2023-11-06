package yybos.hash.katarakt2.Fragments.ViewHolders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import yybos.hash.katarakt2.R;

public class ChatViewHolder extends RecyclerView.ViewHolder {
    public TextView usernameTextView;
    public TextView contentTextView;
    public TextView dateTextView;
    // public ImageView profileImageView;

    public ChatViewHolder(@NonNull View itemView) {
        super(itemView);

        this.usernameTextView = itemView.findViewById(R.id.usernameTextView);
        this.contentTextView = itemView.findViewById(R.id.contentTextView);
        this.dateTextView = itemView.findViewById(R.id.dateTextView);
    }
}
