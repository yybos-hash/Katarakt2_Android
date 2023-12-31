package yybos.hash.katarakt2.Fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.Nullable;

import yybos.hash.katarakt2.Fragments.Popup.InputPopupFragment;
import yybos.hash.katarakt2.Fragments.ViewAdapters.ChatsViewAdapter;
import yybos.hash.katarakt2.MainActivity;
import yybos.hash.katarakt2.R;
import yybos.hash.katarakt2.Socket.Interfaces.ClientInterface;
import yybos.hash.katarakt2.Socket.Objects.Chat;
import yybos.hash.katarakt2.Socket.Objects.Message;

public class ChatsFragment extends Fragment implements ClientInterface {
    private MainActivity mainActivityInstance;
    private ChatFragment chatFragmentInstance;

    private ChatsViewAdapter chatsAdapter;
    private LinearLayout innerLinearLayout;
    private LinearLayout linearLayout;
    private ConstraintLayout constraintLayout;

    private ProgressBar progressBar;

    public ChatsFragment () {

    }
    public ChatsFragment (ChatFragment instance) {
        this.chatFragmentInstance = instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.layout_chats_list, container, false);
    }

    @Override
    public void onViewCreated (@NonNull View view, @Nullable Bundle savedInstanceState) {
        View root = getView();
        if (root == null)
            return;

        this.mainActivityInstance = ((MainActivity) requireActivity());
        this.mainActivityInstance.addClientListener(this);

        ImageView addButton = root.findViewById(R.id.chatsAdd);
        this.progressBar = root.findViewById(R.id.chatsProgressBar);
        this.linearLayout = root.findViewById(R.id.chatsLinearLayout);
        this.innerLinearLayout = root.findViewById(R.id.chatsInnerLinearLayout);
        this.constraintLayout = root.findViewById(R.id.chatsConstraintLayout);

        this.chatsAdapter = new ChatsViewAdapter(chatFragmentInstance);

        RecyclerView recyclerView = root.findViewById(R.id.chatsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(this.chatsAdapter);
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        // animation
        // this is the linearLayout params width (just so I dont need to call getDp every cycle)
        int y = this.getDp(70);

        ValueAnimator animator = ValueAnimator.ofInt(this.getDp(450), this.getDp(180));
        animator.setDuration(300);
        animator.addUpdateListener((valueAnimator) -> {
            int val = (int) valueAnimator.getAnimatedValue();

            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) ChatsFragment.this.linearLayout.getLayoutParams();
            params.leftMargin = val;
            params.setMargins(val, y, 0, y);

            ChatsFragment.this.linearLayout.setLayoutParams(params);

            // Also update the constraints
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(ChatsFragment.this.constraintLayout);
            constraintSet.connect(ChatsFragment.this.linearLayout.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, val);
            constraintSet.applyTo(ChatsFragment.this.constraintLayout);
        });
        animator.start();
        //

        addButton.setOnClickListener((v) -> {
            if (this.mainActivityInstance.getClient().isConnected())
                ChatsFragment.this.addNewChat();
            else
                ChatsFragment.this.mainActivityInstance.showCustomToast("No.", Color.argb(90, 235, 64, 52));
        });

        // make things visible again after the animation ends
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            ChatsFragment.this.innerLinearLayout.setVisibility(View.VISIBLE);
        }, 300);

        if (!this.mainActivityInstance.getClient().isConnected())
            this.progressBar.setVisibility(View.INVISIBLE);

        this.chatFragmentInstance.getChats();
    }

    private void addNewChat () {
        if (this.getContext() == null)
            return;

        String resultKey = "chatsNewChat" + System.currentTimeMillis();

        // create frame layout for popup fragment
        FrameLayout generalFrameLayout = new FrameLayout(this.getContext());
        generalFrameLayout.setId(View.generateViewId());
        generalFrameLayout.setOnClickListener(view -> {
            ValueAnimator frameFadeOut = ValueAnimator.ofInt(100, 0);
            frameFadeOut.setDuration(200);
            frameFadeOut.addUpdateListener((animator) -> {
                int value = (int) animator.getAnimatedValue();
                generalFrameLayout.setBackgroundColor(Color.argb(value, 0, 0, 0));
            });
            frameFadeOut.start();

            InputPopupFragment inputPopupFragment = (InputPopupFragment) getParentFragmentManager().findFragmentByTag(resultKey);

            if (inputPopupFragment == null)
                return;

            inputPopupFragment.destroy();
        });

        ValueAnimator frameFadeIn = ValueAnimator.ofInt(0, 100);
        frameFadeIn.setDuration(200);
        frameFadeIn.addUpdateListener((animator) -> {
            int value = (int) animator.getAnimatedValue();
            generalFrameLayout.setBackgroundColor(Color.argb(value, 0, 0, 0));
        });
        frameFadeIn.start();

        FrameLayout.LayoutParams fragmentFrameLayout = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        fragmentFrameLayout.gravity = Gravity.CENTER;

        InputPopupFragment inputPopupFragment = new InputPopupFragment();

        Bundle args = new Bundle();
        args.putString("title", "BOOBS");
        args.putString("text", "May you please insert the chat name?");
        args.putString("hint", "Right Here");
        args.putString("resultKey", resultKey);

        inputPopupFragment.setArguments(args);

        // initiate fragment manager and
        FragmentTransaction fragmentManager = getParentFragmentManager().beginTransaction();
        fragmentManager.setCustomAnimations(R.anim.fragment_fade_in, R.anim.fragment_fade_out, R.anim.fragment_fade_in, R.anim.fragment_fade_out);
        fragmentManager.add(generalFrameLayout.getId(), inputPopupFragment, resultKey);
        fragmentManager.commit();

        getParentFragmentManager().setFragmentResultListener(resultKey, this, (requestKey, result) -> {
            String data = result.getString("inputResult");

            ChatsFragment.this.closeInputPopup(resultKey);

            if (data == null || data.trim().isEmpty())
                return;

            ChatsFragment.this.chatFragmentInstance.createChat(data);
        });

        // add frame layout to constraintLayout
        this.constraintLayout.addView(generalFrameLayout, fragmentFrameLayout);
    }

    public void closeInputPopup (String tag) {
        InputPopupFragment inputPopupFragment = (InputPopupFragment) getParentFragmentManager().findFragmentByTag(tag);

        if (inputPopupFragment == null)
            return;

        View inputPopupView = inputPopupFragment.getView();
        if (inputPopupView == null)
            return;

        // remove the frameLayout (parent) from the constraintLayout (xuxu beleza)
        FrameLayout generalFrameLayout = (FrameLayout) inputPopupView.getParent();

        ValueAnimator frameFadeOut = ValueAnimator.ofInt(100, 0);
        frameFadeOut.setDuration(150);
        frameFadeOut.addUpdateListener((animator) -> {
            int value = (int) animator.getAnimatedValue();
            generalFrameLayout.setBackgroundColor(Color.argb(value, 0, 0, 0));
        });
        frameFadeOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                generalFrameLayout.removeAllViews();
                ChatsFragment.this.constraintLayout.removeView(generalFrameLayout);

                super.onAnimationEnd(animation);
            }
        });
        frameFadeOut.start();
    }

    private void addChat (Chat chat) {
        // if it's inside the main thread
        this.chatsAdapter.addChat(chat);
        this.progressBar.setVisibility(View.INVISIBLE);
    }
    public void destroy () {
        // this is the linearLayout params width (just so I dont need to call getDp every cycle)
        int y = this.getDp(70);

        ValueAnimator animator = ValueAnimator.ofInt(this.getDp(180), this.getDp(450));
        animator.setDuration(150);
        animator.addUpdateListener((valueAnimator) -> {
            int val = (int) valueAnimator.getAnimatedValue();

            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) ChatsFragment.this.linearLayout.getLayoutParams();
            params.leftMargin = val;
            params.setMargins(val, y, 0, y);

            ChatsFragment.this.linearLayout.setLayoutParams(params);

            // Also update the constraints
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(ChatsFragment.this.constraintLayout);
            constraintSet.connect(ChatsFragment.this.linearLayout.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, val);
            constraintSet.applyTo(ChatsFragment.this.constraintLayout);
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.remove(ChatsFragment.this);
                transaction.commit();

                // doesnt need an actual result, just something to notify the destruction to the chatFragment
                getParentFragmentManager().setFragmentResult("chatsFragment", new Bundle());
            }
        });
        animator.start();
    }

    @Override
    public void onMessageReceived(Message message) {
        // chatsFragment doesnt have anything to do with messages
    }

    @Override
    public void onChatReceived(Chat chat) {
        // if it's inside the main thread
        if (Looper.myLooper() == Looper.getMainLooper()) {
            this.addChat(chat);
        }
        else {
            // Call a function on the UI thread
            mainActivityInstance.runOnUiThread(() -> {
                this.addChat(chat);
            });
        }
    }

    @Override
    public void onDestroyView () {
        this.mainActivityInstance.removeClientListener(this);

        super.onDestroyView();
    }

    private int getDp (int pixel) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixel, requireContext().getResources().getDisplayMetrics());
    }
}