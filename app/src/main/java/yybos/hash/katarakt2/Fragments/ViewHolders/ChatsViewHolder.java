package yybos.hash.katarakt2.Fragments.ViewHolders;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
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

    private int defaultTextColor;

    public ChatsViewHolder(@NonNull View itemView) {
        super(itemView);

        this.nameTextView = itemView.findViewById(R.id.chatName);
        this.dateTextView = itemView.findViewById(R.id.chatDate);
        this.constraintLayout = itemView.findViewById(R.id.chatConstraintLayout);

        this.defaultTextColor = this.nameTextView.getCurrentTextColor();
    }

    public void setOptionSelected (boolean state) {
        if (state) {
            this.nameTextView.setTextColor(Color.argb(255, 46, 166, 209));
        }
        else {
            this.nameTextView.setTextColor(this.defaultTextColor);
        }
    }
    public void setChatSelected (Context context, boolean state) {
        if (state) {
            this.defaultTextColor = context.getColor(R.color.chatSelected);
            this.nameTextView.setTextColor(this.defaultTextColor);
        }
        else {
            this.nameTextView.setTextColor(context.getColor(R.color.textViewColor));
        }
    }

    public Rect getScreenPosition () {
        View itemView = this.itemView;
        Rect rect = new Rect();

        itemView.getGlobalVisibleRect(rect);

        return rect;
    }
}
