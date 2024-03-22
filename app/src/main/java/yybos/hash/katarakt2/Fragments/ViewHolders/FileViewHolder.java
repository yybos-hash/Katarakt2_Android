package yybos.hash.katarakt2.Fragments.ViewHolders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import yybos.hash.katarakt2.R;
import yybos.hash.katarakt2.Socket.Objects.Media.MediaFile;
import yybos.hash.katarakt2.Socket.Objects.Message.User;

public class FileViewHolder extends RecyclerView.ViewHolder {
    public TextView username;
    public TextView date;
    public TextView filename;

    public MediaFile file;

    public User user;

    public FileViewHolder(@NonNull View itemView) {
        super(itemView);

        this.username = itemView.findViewById(R.id.messageFileUsername);
        this.date = itemView.findViewById(R.id.messageFileDate);
        this.filename = itemView.findViewById(R.id.messageFileName);
    }
}
