package yybos.hash.katarakt2.Fragments.ViewAdapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import yybos.hash.katarakt2.Fragments.MessagesFragment;
import yybos.hash.katarakt2.Fragments.ViewHolders.FileViewHolder;
import yybos.hash.katarakt2.Fragments.ViewHolders.MessageViewHolder;
import yybos.hash.katarakt2.R;
import yybos.hash.katarakt2.Socket.Objects.Media.MediaFile;
import yybos.hash.katarakt2.Socket.Objects.Message.Message;
import yybos.hash.katarakt2.Socket.Objects.Message.User;
import yybos.hash.katarakt2.Socket.Objects.PacketObject;

public class MessagesViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<PacketObject> objects = new ArrayList<>();
    private final List<Message> unsentMessages = new ArrayList<>();
    private final Context context; // we need this shit
    private final MessagesFragment fragment;

    public MessagesViewAdapter (MessagesFragment fragment, List<PacketObject> history) {
        this.context = fragment.requireContext();
        this.fragment = fragment;

        if (history != null && !history.isEmpty()) {
            this.objects = new ArrayList<>(history);
        }
    }

    public void addMessage (Message message) {
        for (Message unsentMessage : this.unsentMessages) {
            // checks if the timestamps are equal
            if (unsentMessage.getDate() == message.getDate()) {
                if (!this.objects.contains(unsentMessage)) // if the objects doesnt have this key (this should not happen)
                    break;

                // notify the item change to update the view
                for (int i = this.objects.size() - 1; i > 0; i--) {
                    PacketObject packetObject = this.objects.get(i);

                    if (packetObject.getDate() == message.getDate()) {
                        packetObject.setId(message.getId());
                        notifyItemChanged(i);
                    }
                }

                // remove from the unsent messages
                this.unsentMessages.remove(unsentMessage);
                return;
            }
        }

        // if the user sent this message
        if (message.getId() == -1)
            this.unsentMessages.add(message); // message will be considered undelivered until said otherwise

        this.objects.add(message);
        notifyItemInserted(this.objects.size() - 1);
    }
    public void addFile (MediaFile file) {
        this.objects.add(file);
        notifyItemChanged(this.objects.size() - 1);
    }

    public void updateUsername (User user) {
        for (int i = 0; i < this.objects.size(); i++) {
            PacketObject object = this.objects.get(i);

            if (object.getType() == PacketObject.Type.Message.getValue()) {
                Message message = (Message) object;

                if (message.getUser().getId() == user.getId()) {
                    message.getUser().setUsername(user.getUsername());
                    notifyItemChanged(i);
                }
            }
            else if (object.getType() == PacketObject.Type.File.getValue()) {
                MediaFile file = (MediaFile) object;

                if (file.getUser().getId() == user.getId()) {
                    file.getUser().setUsername(user.getUsername());
                    notifyItemChanged(i);
                }
            }
        }
    }
    public void clear () {
        this.objects.clear();
        notifyDataSetChanged();
        // no other way, must use notifyDataSetChanged()
    }

    @Override
    public int getItemViewType (int position) {
        PacketObject.Type objectType = PacketObject.Type.getEnumByValue(this.objects.get(position).getType());

        switch (objectType) {
            case File: {
                return PacketObject.Type.File.getValue();
            }
            // by default it will return Message
            case Message:
            default: {
                return PacketObject.Type.Message.getValue();
            }
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
        switch (PacketObject.Type.getEnumByValue(viewType)) {
            case File: {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_file, parent, false);
                return new FileViewHolder(itemView);
            }
            // by default it is a Message
            case Message:
            default: {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_message, parent, false);
                return new MessageViewHolder(itemView);
            }
        }
    }

    @Override
    public void onBindViewHolder (@NonNull RecyclerView.ViewHolder holder, int position) {
        PacketObject object = this.objects.get(position);

        switch (PacketObject.Type.getEnumByValue(object.getType())) {
            case Message: {
                Message message = (Message) object;
                MessageViewHolder messageViewHolder = new MessageViewHolder(holder.itemView);

                messageViewHolder.usernameTextView.setText(message.getUsername());
                messageViewHolder.contentTextView.setText(message.getMessage());
                messageViewHolder.dateTextView.setText(new Date(message.getDate()).toString());

                // when we receive the message it will base the value of the background on the packetObject id, so when it rebinds again and it's been delivered, it will change color
                int color = (object.getId() == -1) ? 60 : 255; // if the message doesnt have an ID it's because the user sent this message and it is an unsent message
                messageViewHolder.messageSlider.setBackground(this.createRoundedRectangleDrawable(Color.argb(255, color, color, color)));

                break;
            }
            case File: {
                MediaFile mediaFile = (MediaFile) object;
                FileViewHolder fileViewHolder = new FileViewHolder(holder.itemView);

                fileViewHolder.username.setText(mediaFile.getUser().getUsername());
                fileViewHolder.date.setText(new Date(mediaFile.getDate()).toString());
                fileViewHolder.filename.setText(mediaFile.getFilename());

                fileViewHolder.downloadImage.setOnClickListener((v) -> {
                    MessagesViewAdapter.this.fragment.downloadFile(mediaFile);
                });

                break;
            }
        }
    }

    @Override
    public long getItemId (int position) {
        // Retrieve the data item at the specified position
        PacketObject item = this.objects.get(position);

        // Return a stable identifier for the item
        // For example, you could use a unique ID from your data model
        return item.getId(); // Assuming YourData has a method getId() returning a long
    }

    @Override
    public int getItemCount () {
        return this.objects.size();
    }

    // this will be used to create the message slider
    private Drawable createRoundedRectangleDrawable (int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(color);
        drawable.setCornerRadius(this.dpToPixels(this.context, 22)); // Set your desired corner radius

        return drawable;
    }
    private int dpToPixels (Context context, int dp) {
        // Get the display metrics of the device
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

        // Calculate the pixels from density-independent pixels (dp)
        float pixels = dp * (displayMetrics.densityDpi / 160f);

        // Return the result as an integer
        return Math.round(pixels);
    }
}
