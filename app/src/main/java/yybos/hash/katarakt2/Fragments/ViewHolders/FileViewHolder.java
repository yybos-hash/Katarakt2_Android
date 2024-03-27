package yybos.hash.katarakt2.Fragments.ViewHolders;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import yybos.hash.katarakt2.R;

public class FileViewHolder extends RecyclerView.ViewHolder {
    public TextView username;
    public TextView date;
    public TextView filename;
    public FrameLayout downloadImage;

    public FileViewHolder(@NonNull View itemView) {
        super(itemView);

        this.username = itemView.findViewById(R.id.messageFileUsername);
        this.date = itemView.findViewById(R.id.messageFileDate);
        this.filename = itemView.findViewById(R.id.messageFileName);
        this.downloadImage = itemView.findViewById(R.id.messageFileDownload);
    }
}
