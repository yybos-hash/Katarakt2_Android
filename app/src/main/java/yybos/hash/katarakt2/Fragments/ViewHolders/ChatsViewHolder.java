package yybos.hash.katarakt2.Fragments.ViewHolders;


import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import yybos.hash.katarakt2.R;

public class ChatsViewHolder extends RecyclerView.ViewHolder {
    public TextView dateTextView;
    public TextView nameTextView;
    public ConstraintLayout constraintLayout;
    // public ImageView profileImageView;

    public ChatsViewHolder(@NonNull View itemView) {
        super(itemView);

        this.nameTextView = itemView.findViewById(R.id.chatName);
        this.dateTextView = itemView.findViewById(R.id.chatDate);
        this.constraintLayout = itemView.findViewById(R.id.chatConstraintLayout);
    }
}
