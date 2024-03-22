package yybos.hash.katarakt2.Fragments.ViewHolders;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import yybos.hash.katarakt2.R;

public class MessageViewHolder extends RecyclerView.ViewHolder {
    public TextView usernameTextView;
    public TextView contentTextView;
    public TextView dateTextView;
    public LinearLayout messageLayout;
    public View messageSlider;
    // public ImageView profileImageView;

    public MessageViewHolder(@NonNull View itemView) {
        super(itemView);

        this.usernameTextView = itemView.findViewById(R.id.messageUsername);
        this.contentTextView = itemView.findViewById(R.id.messageContent);
        this.dateTextView = itemView.findViewById(R.id.messageDate);
        this.messageLayout = itemView.findViewById(R.id.messageLayout);
        this.messageSlider = itemView.findViewById(R.id.messageSlider);
    }
}
