package yybos.hash.katarakt2.Fragments.Custom;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.Nullable;

import java.util.List;

import yybos.hash.katarakt2.R;

public class CustomSpinnerFragment extends Fragment {
    private FrameLayout frame;
    private ImageView icon;

    private FrameLayout objectsFrame;
    private ConstraintLayout constraint;

    private boolean isOpen = false;

    private final List<?> objectsList;

    public CustomSpinnerFragment (List<?> objects) {
        this.objectsList = objects;
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

        this.frame.setZ(500f);
        this.icon.setZ(5001f);

        this.icon.setRotation(-90);

        this.frame.setOnClickListener((v) -> {
            CustomSpinnerFragment.this.rotateIcon();

            if (!CustomSpinnerFragment.this.isOpen) {
                CustomSpinnerFragment.this.isOpen = true;
                CustomSpinnerFragment.this.displayObjects();
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
        Context context = requireContext();

        this.objectsFrame = new FrameLayout(context);
        this.objectsFrame.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.custom_spinner_dropdown_shape, context.getTheme()));
        this.objectsFrame.setPadding(0, 180, 0, 0);

        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, new DisplayMetrics());

        for (Object object : this.objectsList) {
            FrameLayout textFrame = new FrameLayout(context);
            textFrame.setBackgroundColor(getResources().getColor(R.color.spinnerColor, context.getTheme()));
            textFrame.setPadding(30, 30, 30, 30);

            TextView text = new TextView(context);
            text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            text.setText(object.toString());

            textFrame.addView(text);
            this.objectsFrame.addView(textFrame, new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        this.constraint.addView(this.objectsFrame, params);
    }
}
