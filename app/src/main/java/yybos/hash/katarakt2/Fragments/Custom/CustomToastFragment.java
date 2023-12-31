package yybos.hash.katarakt2.Fragments.Custom;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.jetbrains.annotations.Nullable;

import yybos.hash.katarakt2.R;

public class CustomToastFragment extends Fragment {
    private String message;
    private Color backgroundColor;
    private TextView toast;

    public CustomToastFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.message = getArguments().getString("message");
            int colorInt = getArguments().getInt("background");

            this.backgroundColor = Color.valueOf(colorInt);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.custom_toast, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getView() == null)
            return;

        View root = getView();

        LinearLayout background = root.findViewById(R.id.customToastBackground);
        this.toast = root.findViewById(R.id.customToastMessage);

        background.setBackground(this.createRoundedRectangleDrawable(this.backgroundColor.toArgb()));
        this.toast.setText(this.message);

        new Handler(Looper.getMainLooper()).postDelayed(() -> this.toast.setVisibility(View.VISIBLE), 150);

        // end toast after set time
        new Handler(Looper.getMainLooper()).postDelayed(this::end, 2500);
    }

    private void end () {
        View fragmentView = getView();
        if (!isAdded())
            return;

        if (fragmentView != null) {
            Animation fadeOutAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.custom_toast_contract);
            fragmentView.startAnimation(fadeOutAnimation);

            fadeOutAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    CustomToastFragment.this.toast.setAlpha(0f);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (CustomToastFragment.this.isDetached())
                        return;

                    // Animation ended, remove the fragment
                    FragmentManager fragmentManager = getParentFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.remove(CustomToastFragment.this);
                    fragmentTransaction.commit();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    // Animation repeated
                }
            });
        } else {
            FragmentManager fragmentManager = getParentFragmentManager();
            // transaction.setCustomAnimations(R.anim.custom_toast_expand, R.anim.custom_toast_contract);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(this);
            fragmentTransaction.commit();
        }
    }
    private Drawable createRoundedRectangleDrawable(int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(color);
        drawable.setCornerRadius(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, requireContext().getResources().getDisplayMetrics())); // Set your desired corner radius

        return drawable;
    }
}