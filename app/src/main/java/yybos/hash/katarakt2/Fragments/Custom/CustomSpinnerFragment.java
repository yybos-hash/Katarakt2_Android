package yybos.hash.katarakt2.Fragments.Custom;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.Nullable;

import yybos.hash.katarakt2.Fragments.Custom.Listeners.SpinnerListener;
import yybos.hash.katarakt2.Fragments.Custom.Objects.NullObject;
import yybos.hash.katarakt2.Fragments.Custom.ViewAdapters.SpinnerViewAdapter;
import yybos.hash.katarakt2.R;

public class CustomSpinnerFragment extends Fragment {
    private ImageView icon;

    private FrameLayout objectsFrame;
    private ConstraintLayout constraint;
    private TextView optionText;

    private boolean isOpen = false;

    private SpinnerListener spinnerListener;

    private SpinnerViewAdapter adapter;

    private final int dropdownSize = 180;

    // requires empty constructor
    public CustomSpinnerFragment () {}
    public CustomSpinnerFragment (SpinnerListener listener) {
        this.spinnerListener = listener;
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

        FrameLayout frame = root.findViewById(R.id.customSpinnerFrame);
        this.icon = root.findViewById(R.id.customSpinnerIcon);
        this.optionText = root.findViewById(R.id.customSpinnerOptionText);

        frame.setZ(500f);
        this.icon.setZ(5001f);

        this.icon.setRotation(-90);

        frame.setOnClickListener((v) -> {
            CustomSpinnerFragment.this.rotateIcon();

            if (!CustomSpinnerFragment.this.isOpen) {
                this.isOpen = true;
                this.spinnerListener.onSpinnerExpand();
                CustomSpinnerFragment.this.displayObjects();
            }
            else {
                this.isOpen = false;
                this.spinnerListener.onSpinnerContract();
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
        Context context = requireContext();

        // frame layout and parameters
        this.objectsFrame = new FrameLayout(context);
        this.objectsFrame.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.custom_spinner_dropdown_shape, context.getTheme()));
        this.objectsFrame.setPadding(0, this.getDp(50), 0, this.getDp(10));
        this.objectsFrame.setId(View.generateViewId());

        ConstraintLayout.LayoutParams objectsFrameParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        this.constraint.addView(this.objectsFrame, objectsFrameParams);

        LinearLayout objectsFrameLinearLayout = new LinearLayout(context);
        objectsFrameLinearLayout.setOrientation(LinearLayout.VERTICAL);

        // recycler view
        this.adapter = new SpinnerViewAdapter(this);

        RecyclerView recyclerView = new RecyclerView(context);
        recyclerView.setAdapter(this.adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        this.adapter.addObject(new NullObject());

        // add to screen
        objectsFrameLinearLayout.addView(recyclerView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        this.objectsFrame.addView(objectsFrameLinearLayout, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // animation for it to open
        ValueAnimator anim = ValueAnimator.ofInt(this.getDp(40), this.getDp(this.dropdownSize));
        anim.setDuration(300);
        anim.addUpdateListener((listener) -> {
            objectsFrameParams.height = (int) listener.getAnimatedValue();
            this.objectsFrame.setLayoutParams(objectsFrameParams);
        });
        anim.start();
    }
    private void closeObjectsList () {
        if (this.objectsFrame == null)
            return;

        this.rotateIcon();

        ConstraintLayout.LayoutParams objectsFrameParams = (ConstraintLayout.LayoutParams) this.objectsFrame.getLayoutParams();

        // animation for it to open
        ValueAnimator anim = ValueAnimator.ofInt(this.getDp(this.dropdownSize), this.getDp(40));
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

    public void objectSelected (Object object) {
        this.isOpen = false;
        this.closeObjectsList();

        this.optionText.setText(object.toString());

        this.spinnerListener.onObjectSelected(object);
    }

    public void setOption (Object object) {
        this.optionText.setText(object.toString());
    }

    private int getDp (int pixel) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixel, requireContext().getResources().getDisplayMetrics());
    }

    // add object to adapter > recyclerView
    public void addObject (Object object) {
        if (this.adapter != null)
            this.adapter.addObject(object);
    }
}
