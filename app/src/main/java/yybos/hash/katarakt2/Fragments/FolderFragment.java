package yybos.hash.katarakt2.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.Nullable;

import java.util.Hashtable;
import java.util.Map;

import yybos.hash.katarakt2.MainActivity;
import yybos.hash.katarakt2.R;
import yybos.hash.katarakt2.Socket.Client;
import yybos.hash.katarakt2.Socket.Interfaces.ClientInterface;
import yybos.hash.katarakt2.Socket.Objects.Anime;
import yybos.hash.katarakt2.Socket.Objects.Media.Directory;
import yybos.hash.katarakt2.Socket.Objects.Media.DirectoryObject;
import yybos.hash.katarakt2.Socket.Objects.Media.MediaFile;
import yybos.hash.katarakt2.Socket.Objects.Message.Chat;
import yybos.hash.katarakt2.Socket.Objects.Message.Command;
import yybos.hash.katarakt2.Socket.Objects.Message.Message;

public class FolderFragment extends Fragment implements ClientInterface {
    private MainActivity mainActivityInstance;
    private LinearLayout baseLinearLayout;
    private Client client;

    private LinearLayout.LayoutParams itemParams;

    private Map<DirectoryObject, LinearLayout> items;

    private final int itemTopMargin = 15; // dp
    private final int branchMultiplier = 30;

    public FolderFragment () {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.itemParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.itemParams.topMargin = this.dpToPixels(this.itemTopMargin);

        this.items = new Hashtable<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_folder, container, false);
    }

    @Override
    public void onViewCreated (@NonNull View view, @Nullable Bundle savedInstanceState) {
        View root = view.getRootView();

        this.mainActivityInstance = ((MainActivity) requireActivity());

        // move selection tab (I'm doing it from the fragment cause it will fix the issue where if I used the back stack trace the selectionTab wouldnt move)
        this.mainActivityInstance.moveSelectionTab(this);
        this.mainActivityInstance.addClientListener(this);

        this.client = this.mainActivityInstance.getClient();

        this.baseLinearLayout = root.findViewById(R.id.folderInnerLayout);

        this.client.sendCommand(Command.getDirectories("C:/"), true);
    }

    // creates an item and returns its linear layout
    public LinearLayout createItem (ItemType itemType, String name) {
        Context context = this.requireContext();

        Drawable itemDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.folder_icon, context.getTheme());
        if (itemType == ItemType.FILE)
            itemDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.file_icon2, context.getTheme());

        // item linear layout
        LinearLayout itemLinearLayout = new LinearLayout(context);
        itemLinearLayout.setOrientation(LinearLayout.VERTICAL);
        //

        // inner item linear layout
        LinearLayout innerLinearLayout = new LinearLayout(context);
        innerLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        innerLinearLayout.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams innerParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //

        // item branch
        View branchView = new View(context);
        branchView.setBackground(this.createRoundedRectangleDrawable());

        LinearLayout.LayoutParams branchParams = new LinearLayout.LayoutParams(this.dpToPixels(30), this.dpToPixels(3));
        branchParams.setMarginStart(this.dpToPixels(5));
        //

        // item image
        ImageView imageView = new ImageView(context);
        imageView.setBackground(itemDrawable);

        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(this.dpToPixels(40), this.dpToPixels(40));
        //

        // item text
        TextView textView = new TextView(context);
        textView.setText(name);
        textView.setTextColor(getResources().getColor(R.color.textViewColor, context.getTheme()));

        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textParams.setMarginStart(this.dpToPixels(5));
        //

        innerLinearLayout.addView(branchView, branchParams);
        innerLinearLayout.addView(imageView, imageParams);
        innerLinearLayout.addView(textView, textParams);

        itemLinearLayout.addView(innerLinearLayout, innerParams);

        return itemLinearLayout;
    }

    // creates a branch on an item and then returns the branch linear layout
    public LinearLayout createBranch (LinearLayout item) {
        Context context = this.requireContext();

        // Branch Linear Layout
        LinearLayout branchLinearLayout = new LinearLayout(context);
        branchLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams branchParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        branchParams.setMarginStart(this.dpToPixels(50));
        //

        // branch View (it's below item linear layout because I need the amount of children from it)
        View branchView = new View(context);
        branchView.setBackground(this.createRoundedRectangleDrawable());

        LinearLayout.LayoutParams branchViewParams = new LinearLayout.LayoutParams(this.dpToPixels(3), ViewGroup.LayoutParams.MATCH_PARENT);
        //

        // Items Linear Layout
        LinearLayout itemsLinearLayout = new LinearLayout(context);
        itemsLinearLayout.setOrientation(LinearLayout.VERTICAL);
//        itemsLinearLayout.addOnLayoutChangeListener((view, i, i1, i2, i3, i4, i5, i6, i7) -> {
//            LinearLayout.LayoutParams newBranchViewParams = new LinearLayout.LayoutParams(this.dpToPixels(3), ViewGroup.LayoutParams.MATCH_PARENT);
//            branchView.setLayoutParams(newBranchViewParams);
//        });

        LinearLayout.LayoutParams itemsParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //

        branchLinearLayout.addView(branchView, branchViewParams);
        branchLinearLayout.addView(itemsLinearLayout, itemsParams);

        item.addView(branchLinearLayout, branchParams);

        return itemsLinearLayout;
    }

    private int dpToPixels (int dp) {
        // Get the display metrics of the device
        DisplayMetrics displayMetrics = this.requireContext().getResources().getDisplayMetrics();

        // Calculate the pixels from density-independent pixels (dp)
        float pixels = dp * (displayMetrics.densityDpi / 160f);

        // Return the result as an integer
        return Math.round(pixels);
    }
    private Drawable createRoundedRectangleDrawable () {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(Color.WHITE);
        drawable.setCornerRadius(this.dpToPixels(5)); // Set your desired corner radius

        return drawable;
    }

    @Override
    public void onCommandReceived (Command command) {

    }

    @Override
    public void onMessageReceived (Message message) {

    }

    @Override
    public void onChatReceived (Chat chat) {

    }

    @Override
    public void onAnimeReceived (Anime anime) {

    }

    @Override
    public void onFileReceived (MediaFile mediaFile) {

    }

    @Override
    public void onDirectoryReceived (Directory directory) {
        // Call a function on the UI thread
        this.mainActivityInstance.runOnUiThread(() -> {
            String name = directory.path.split("/")[directory.path.split("/").length - 1];

            // if there is nothing on the linearLayout
            if (this.baseLinearLayout.getChildCount() == 0) {
                LinearLayout folder = this.createItem(ItemType.FOLDER, name);
                LinearLayout folderBranch = this.createBranch(folder);

                for (DirectoryObject obj : directory.objects) {
                    String objName = obj.path.split("/")[obj.path.split("/").length - 1];

                    ItemType itemType = (obj.isFolder) ? ItemType.FOLDER : ItemType.FILE;
                    LinearLayout item = this.createItem(itemType, objName);

                    // request this directory subdirectories
                    if (itemType == ItemType.FOLDER) {
                        item.setOnClickListener((v) -> {
                            // if there is no branch (1 child)
                            if (item.getChildCount() == 1) {
                                this.client.sendCommand(Command.getDirectories(obj.path), true);
                                return;
                            }

                            // avoid java.util.ConcurrentModificationException
                            Map<DirectoryObject, LinearLayout> itemsCopy = new Hashtable<>(this.items);

                            // remove the branch's children from Map
                            for (DirectoryObject directoryObject : itemsCopy.keySet()) {
                                if (directoryObject.path.startsWith(obj.path) && directoryObject.path.length() != obj.path.length()) {
                                    this.items.remove(directoryObject);
                                }
                            }

                            // remove the branch
                            item.removeView(item.getChildAt(1));
                        });
                    }

                    folderBranch.addView(item, this.itemParams);
                    this.items.put(obj, item);
                }

                this.baseLinearLayout.addView(folder, this.itemParams);
            }
            else {
                for (DirectoryObject dir : this.items.keySet()) {
                    if (dir.path.equals(directory.path)) {
                        LinearLayout parentItem = this.items.get(dir);

                        // if it already had children (could happen)
                        if (parentItem == null || parentItem.getChildCount() > 1) {
                            return;
                        }

                        LinearLayout parentBranch = this.createBranch(parentItem);

                        for (DirectoryObject obj : directory.objects) {
                            String objName = obj.path.split("/")[obj.path.split("/").length - 1];

                            ItemType itemType = (obj.isFolder) ? ItemType.FOLDER : ItemType.FILE;
                            LinearLayout item = this.createItem(itemType, objName);

                            // request this directory subdirectories
                            if (itemType == ItemType.FOLDER) {
                                item.setOnClickListener((v) -> {
                                    // if there is no branch (1 child)
                                    if (item.getChildCount() == 1) {
                                        this.client.sendCommand(Command.getDirectories(obj.path), true);
                                        return;
                                    }

                                    // avoid java.util.ConcurrentModificationException
                                    Map<DirectoryObject, LinearLayout> itemsCopy = new Hashtable<>(this.items);

                                    // remove the branch's children from Map
                                    for (DirectoryObject directoryObject : itemsCopy.keySet()) {
                                        if (directoryObject.path.startsWith(obj.path) && directoryObject.path.length() != obj.path.length()) {
                                            this.items.remove(directoryObject);
                                        }
                                    }

                                    // remove the branch
                                    item.removeView(item.getChildAt(1));
                                });
                            }

                            parentBranch.addView(item, this.itemParams);
                            this.items.put(obj, item);
                        }

                        break;
                    }
                }
            }
        });
    }

    @Override
    public void onDestroyView () {
        this.mainActivityInstance.removeClientListener(this);

        super.onDestroyView();
    }

    private enum ItemType {
        FOLDER,
        FILE
    }
}
