package yybos.hash.katarakt2.Fragments.ViewHolders;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import yybos.hash.katarakt2.R;

public class AnimeViewHolder extends RecyclerView.ViewHolder {
    public ConstraintLayout constraintLayout;

    public TextView name;
    public TextView episode;
    public EditText stopedAt;
    public TextView isDone;

    public EditText link;

    public ImageView typeIcon;

    public boolean isOpen;

    public AnimeViewHolder(@NonNull View itemView) {
        super(itemView);

        this.name = itemView.findViewById(R.id.animeName);
        this.episode = itemView.findViewById(R.id.animeEpisode);
        this.stopedAt = itemView.findViewById(R.id.animeStopedAt);
        this.isDone = itemView.findViewById(R.id.animeIsdone);
        this.typeIcon = itemView.findViewById(R.id.animeTypeIcon);
        this.link = itemView.findViewById(R.id.animeLinkEdittext);
        this.constraintLayout = itemView.findViewById(R.id.animeConstraintLayout);
    }
}
