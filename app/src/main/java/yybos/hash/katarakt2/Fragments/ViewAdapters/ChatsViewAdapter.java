package yybos.hash.katarakt2.Fragments.ViewAdapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.sql.Date;
import java.util.List;

import yybos.hash.katarakt2.Fragments.MessagesFragment;
import yybos.hash.katarakt2.Fragments.Popup.OptionFragment;
import yybos.hash.katarakt2.Fragments.ViewHolders.ChatsViewHolder;
import yybos.hash.katarakt2.R;
import yybos.hash.katarakt2.Socket.Objects.Message.Chat;

public class ChatsViewAdapter extends RecyclerView.Adapter<ChatsViewHolder> {
    private final List<Chat> chats = new ArrayList<>();
    private MessagesFragment messagesFragmentInstance;

    public ChatsViewAdapter (MessagesFragment messagesFragment) {
        if (messagesFragment != null)
            this.messagesFragmentInstance = messagesFragment;
    }

    public void addChat (Chat chat) {
        this.chats.add(chat);
        notifyItemInserted(this.chats.size() - 1);
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

        Context context = this.messagesFragmentInstance.requireContext();

        if (chat.getName() != null) {
            holder.nameTextView.setText(chat.getName());

            if (chat.getId() == this.messagesFragmentInstance.getCurrentChatId()) {
                holder.setChatSelected(context, true);
            }
        }

        if (chat.getDate() != 0)
            holder.dateTextView.setText(new Date(chat.getDate()).toString());

        holder.constraintLayout.setOnClickListener(v -> {
            this.messagesFragmentInstance.showCustomToast("Loading " + chat.getName(), Color.argb(80, 0, 153, 255));
            this.messagesFragmentInstance.updateMessageHistory(chat.getId());
            this.messagesFragmentInstance.closeChats();
        });
        holder.constraintLayout.setOnLongClickListener(v -> {
            holder.setOptionSelected(true);

            int holderX = holder.getScreenPosition().centerX();
            int holderY = holder.getScreenPosition().centerY();

            FrameLayout generalFrameLayout = this.messagesFragmentInstance.createFrameLayout();
            generalFrameLayout.setOnClickListener(view -> {
                holder.setOptionSelected(false);

                ConstraintLayout parent = (ConstraintLayout) generalFrameLayout.getParent();
                generalFrameLayout.removeAllViews();
                parent.removeView(generalFrameLayout);
            });

            LinearLayout optionsLinearLayout = new LinearLayout(context);
            optionsLinearLayout.setOrientation(LinearLayout.VERTICAL);

            // delete option
            FrameLayout deleteOption = new FrameLayout(context);
            OptionFragment deleteOptionFragment = new OptionFragment("Delete", ResourcesCompat.getDrawable(context.getResources(), R.drawable.trash_icon, context.getTheme())); // jesus fucking christ

            deleteOption.setId(View.generateViewId());

            ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                    .add(deleteOption.getId(), deleteOptionFragment)
                    .commit();

            deleteOption.setOnClickListener((shit) -> {
                this.messagesFragmentInstance.displayInfo(
                "Wait!",
                "Do you really want to delete " + chat.getName() + " and all of it's messages?",
                "Yes",
                "Missclick",
                (resultKey, result) -> {
                    int button = result.getInt("button");

                    if (button == 1) {
                        this.messagesFragmentInstance.deleteChat(chat.getId());

                        // If I use this.chats.remove(index) a lot of things will go wrong
                        for (int i = 0; i < this.chats.size(); i++) {
                            if (this.chats.get(i).getId() == chat.getId()) {
                                this.chats.remove(i);
                                notifyItemRemoved(i);
                            }
                        }
                    }

                    // the resultKey is the same as the fragment tag
                    this.messagesFragmentInstance.closeInfoPopup(resultKey);
                });
                holder.setOptionSelected(false);

                ConstraintLayout parent = (ConstraintLayout) generalFrameLayout.getParent();
                generalFrameLayout.removeAllViews();
                parent.removeView(generalFrameLayout);
            });
            //

            // edit option
            FrameLayout editOption = new FrameLayout(context);
            OptionFragment editOptionFragment = new OptionFragment("Edit", ResourcesCompat.getDrawable(context.getResources(), R.drawable.edit_icon, context.getTheme())); // jesus fucking christ

            editOption.setId(View.generateViewId());

            ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                    .add(editOption.getId(), editOptionFragment)
                    .commit();

            editOption.setOnClickListener((secondShit) -> {
                ConstraintLayout parent = (ConstraintLayout) generalFrameLayout.getParent();
                generalFrameLayout.removeAllViews();
                parent.removeView(generalFrameLayout);
            });
            //

            // it's here because of the rKey


            // set sum things
            optionsLinearLayout.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.options_shape, context.getTheme()));
            optionsLinearLayout.setPadding(10, 10, 10, 10);

            FrameLayout.LayoutParams optionsLinearLayoutParams = new FrameLayout.LayoutParams(
                    (int) context.getResources().getDimension(R.dimen.options_width), ViewGroup.LayoutParams.WRAP_CONTENT
            );
            optionsLinearLayoutParams.leftMargin = holderX - 250;
            optionsLinearLayoutParams.topMargin = holderY - 50;
            //

            // now add everything to the screen
            optionsLinearLayout.addView(editOption);
            optionsLinearLayout.addView(deleteOption);

            generalFrameLayout.addView(optionsLinearLayout, optionsLinearLayoutParams);

            return true;
        });
    }

    @Override
    public int getItemCount() {
        return this.chats.size();
    }
}
