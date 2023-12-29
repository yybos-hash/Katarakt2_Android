package yybos.hash.katarakt2.Fragments.Custom;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import yybos.hash.katarakt2.Fragments.Custom.Listeners.SpinnerListener;
import yybos.hash.katarakt2.R;
import yybos.hash.katarakt2.Socket.Objects.Chat;
import yybos.hash.katarakt2.Socket.Objects.Server;

public class CustomSpinnerFragment extends Fragment {
    private FrameLayout frame;
    private ImageView icon;

    private FrameLayout objectsFrame;
    private ConstraintLayout constraint;
    private TextView optionText;
    private Object option;
    private SpinnerListener listener;

    private boolean isOpen = false;

    private List<?> objectsList;

    public CustomSpinnerFragment (List<?> objects) {
        Chat[] chats = { Chat.toChat(0, "chat1"), Chat.toChat(0, "chat2"), Chat.toChat(0, "chat3") };
        this.objectsList = new ArrayList<>(Arrays.asList(chats));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.custom_spinner, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        View root = view.getRootView();

        this.constraint = root.findViewById(R.id.customSpinnerConstraint);

        this.frame = root.findViewById(R.id.customSpinnerFrame);
        this.icon = root.findViewById(R.id.customSpinnerIcon);
        this.optionText = root.findViewById(R.id.customSpinnerOptionText);

        this.frame.setZ(500f);
        this.icon.setZ(5001f);

        this.icon.setRotation(-90);

        this.frame.setOnClickListener((v) -> {
            CustomSpinnerFragment.this.rotateIcon();

            if (!CustomSpinnerFragment.this.isOpen) {
                CustomSpinnerFragment.this.displayObjects();
            }
            else {
                CustomSpinnerFragment.this.closeObjectsList();
            }
        });
    }

    private void rotateIcon () {
        // Create an ObjectAnimator for the rotation property
        ValueAnimator rotateAnimator = ObjectAnimator.ofFloat( this.icon.getRotation(), this.icon.getRotation() + 180);
        rotateAnimator.setDuration(300);
        rotateAnimator.addUpdateListener(valueAnimator -> CustomSpinnerFragment.this.icon.setRotation((float) valueAnimator.getAnimatedValue()));

        // Start the rotation animation
        rotateAnimator.start();
    }

    private void displayObjects () {
        CustomSpinnerFragment.this.isOpen = true;

        Context context = requireContext();

        LinearLayout objectsFrameLinearLayout = new LinearLayout(context);
        objectsFrameLinearLayout.setOrientation(LinearLayout.VERTICAL);

        FrameLayout.LayoutParams objectsFrameLinearLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        this.objectsFrame = new FrameLayout(context);
        this.objectsFrame.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.custom_spinner_dropdown_shape, context.getTheme()));
        this.objectsFrame.setPadding(0, 180, 0, 0);

        ConstraintLayout.LayoutParams objectsFrameParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        objectsFrameParams.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, new DisplayMetrics());

        this.constraint.addView(this.objectsFrame, objectsFrameParams);

        // animation for it to open
        ValueAnimator anim = ValueAnimator.ofInt(0, 500);
        anim.setDuration(300);
        anim.addUpdateListener((listener) -> {
            objectsFrameParams.matchConstraintMaxHeight = (int) listener.getAnimatedValue();
            this.objectsFrame.requestLayout();
        });
        anim.start();

        for (Object object : this.objectsList) {
            FrameLayout textFrame = new FrameLayout(context);
            textFrame.setBackgroundColor(getResources().getColor(R.color.spinnerColor, context.getTheme()));
            textFrame.setPadding(30, 30, 30, 30);
            textFrame.setOnClickListener((v) -> {
                CustomSpinnerFragment.this.setOption(object);
                CustomSpinnerFragment.this.closeObjectsList();
            });

            TextView text = new TextView(context);
            text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            text.setText(object.toString());

            textFrame.addView(text);
            objectsFrameLinearLayout.addView(textFrame, new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        this.objectsFrame.addView(objectsFrameLinearLayout, objectsFrameLinearLayoutParams);
    }
    private void closeObjectsList () {
        if (this.objectsFrame == null)
            return;

        this.isOpen = false;
        this.rotateIcon();

        ConstraintLayout.LayoutParams objectsFrameParams = (ConstraintLayout.LayoutParams) this.objectsFrame.getLayoutParams();

        // animation for it to open
        ValueAnimator anim = ValueAnimator.ofInt(500, 0);
        anim.setDuration(300);
        anim.addUpdateListener((listener) -> {
            objectsFrameParams.matchConstraintMaxHeight = (int) listener.getAnimatedValue();
            this.objectsFrame.requestLayout();
        });
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
                CustomSpinnerFragment.this.objectsFrame.removeAllViews();
                CustomSpinnerFragment.this.constraint.removeView(CustomSpinnerFragment.this.objectsFrame);
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {

            }
        });
        anim.start();
    }

    public void setOption (Object object) {
        this.option = object;
        this.optionText.setText(object.toString());

        this.listener.onOptionChanged(object);
    }
    public void setOptionListener (SpinnerListener listener) {
        this.listener = listener;
    }
}
