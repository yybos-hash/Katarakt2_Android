package yybos.hash.katarakt2.Fragments.ViewAdapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import yybos.hash.katarakt2.Fragments.ViewHolders.AnimeViewHolder;
import yybos.hash.katarakt2.R;
import yybos.hash.katarakt2.Socket.Objects.Anime;

public class AnimeViewAdapter extends RecyclerView.Adapter<AnimeViewHolder> {
    private final List<Anime> animeList = new ArrayList<>();
    private final Context context;

    public AnimeViewAdapter (Fragment fragment) {
        this.context = fragment.requireContext();
    }

    public void addAnime (Anime anime) {
        this.animeList.add(anime);
        notifyItemInserted(this.getItemCount() - 1);
    }

    @NonNull
    @Override
    public AnimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_anime, parent, false);
        return new AnimeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AnimeViewHolder holder, int position) {
        Anime anime = this.animeList.get(position);

        String episode = "" + anime.getEpisode() + " Episode";

        holder.name.setText(anime.getName());
        holder.episode.setText(episode);
        holder.stopedAt.setText(anime.getStopedAt());
        holder.link.setText(anime.getLink());

        holder.episode.setClickable(true);

        if (anime.getAnimeType() == Anime.AnimeType.Anime)
            holder.typeIcon.setImageDrawable(AppCompatResources.getDrawable(this.context, R.drawable.anime_icon));
        else if (anime.getAnimeType() == Anime.AnimeType.Hentai)
            holder.typeIcon.setImageDrawable(AppCompatResources.getDrawable(this.context, R.drawable.hentai_icon));

        if (anime.isDone())
            holder.isDone.setVisibility(View.VISIBLE);
        else
            holder.isDone.setVisibility(View.INVISIBLE);

        if (holder.isOpen)
            holder.link.setVisibility(View.VISIBLE);
        else
            holder.link.setVisibility(View.GONE);

        // handles the episode swap
        GestureDetector episodeGesture = new GestureDetector(this.context, new GestureDetector.SimpleOnGestureListener() {
            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onFling(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
                float distanceX = e2.getX() - e1.getX();
                float distanceY = e2.getY() - e1.getY();

                if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (distanceX > 0) {
                        // Right swipe
                        anime.setEpisode(anime.getEpisode() + 1);
                    } else {
                        // Left swipe
                        anime.setEpisode(anime.getEpisode() - 1);
                    }

                    holder.isOpen = true; // if I dont do this, it will be opening and closing
                    notifyItemChanged(holder.getAdapterPosition());
                    return true;
                }

                return false;
            }
        });

        holder.link.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                holder.link.clearFocus();

                FragmentManager fragmentManager = ((FragmentActivity) this.context).getSupportFragmentManager();
                if (!fragmentManager.isStateSaved()) {
                    InputMethodManager inputMethodManager = (InputMethodManager) this.context.getSystemService(Context.INPUT_METHOD_SERVICE);

                    if (inputMethodManager != null)
                        inputMethodManager.hideSoftInputFromWindow(holder.itemView.getWindowToken(), 0);

                    // do something

                    return true;
                }
            }

            return false;
        });
        holder.link.setOnLongClickListener((v) -> {
            holder.link.setText("");
            return true;
        });

        holder.episode.setOnTouchListener((view, motionEvent) -> episodeGesture.onTouchEvent(motionEvent)); // ignore the warning
        holder.stopedAt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                holder.stopedAt.clearFocus();

                FragmentManager fragmentManager = ((FragmentActivity) this.context).getSupportFragmentManager();
                if (!fragmentManager.isStateSaved()) {
                    InputMethodManager inputMethodManager = (InputMethodManager) this.context.getSystemService(Context.INPUT_METHOD_SERVICE);

                    if (inputMethodManager != null)
                        inputMethodManager.hideSoftInputFromWindow(holder.itemView.getWindowToken(), 0);

                    // do something

                    return true;
                }
            }

            return false;
        });

        holder.constraintLayout.setOnLongClickListener((v) -> {
            // Create an Intent with the url
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(holder.link.getText().toString()));

            // Check if there's a browser app available to handle the intent
            if (intent.resolveActivity(this.context.getPackageManager()) != null) {
                // Start the browser activity
                this.context.startActivity(intent);
            }
            return true;
        });
        holder.constraintLayout.setOnClickListener((v) -> {
            if (holder.isOpen) {
                holder.link.setText(anime.getLink());

                holder.link.setVisibility(View.GONE);
                holder.isOpen = false;
            }
            else {
                holder.link.setVisibility(View.VISIBLE);
                holder.isOpen = true;
            }
        });
    }

    @Override
    public long getItemId(int position) {
        // Retrieve the data item at the specified position
        Anime item = this.animeList.get(position);

        // Return a stable identifier for the item
        // For example, you could use a unique ID from your data model
        return item.getId(); // Assuming YourData has a method getId() returning a long
    }

    @Override
    public int getItemCount() {
        return this.animeList.size();
    }
}
