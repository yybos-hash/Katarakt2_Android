package yybos.hash.katarakt2.Fragments.Custom;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.Nullable;

import yybos.hash.katarakt2.R;

public class CustomToggleFragment extends Fragment {
    private boolean isTrue = false;

    private View circle;
    private FrameLayout frame;

    public CustomToggleFragment () {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.custom_toggle, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        View root = view.getRootView();

        this.frame = root.findViewById(R.id.customToggleFrame);
        this.circle = root.findViewById(R.id.customToggleCircle);

        this.frame.setId(View.generateViewId());
        this.circle.setId(View.generateViewId());

        if (this.isTrue)
            this.makeTrue();
        else
            this.makeFalse();
    }

    public void makeTrue () {
        this.isTrue = true;

        if (getContext() == null) {
            return;
        }

        Drawable shape = this.createRoundedRectangleDrawable(getResources().getColor(R.color.toggleEnabledColor, requireContext().getTheme()));
        this.frame.setBackground(shape);

        FrameLayout.LayoutParams circleParams = new FrameLayout.LayoutParams((int) getResources().getDimension(R.dimen.toggle_circle_size), (int) getResources().getDimension(R.dimen.toggle_circle_size));
        circleParams.gravity = Gravity.CENTER_VERTICAL | Gravity.END;
        this.circle.setLayoutParams(circleParams);
    }
    public void makeFalse () {
        this.isTrue = false;

        if (getContext() == null) {
            return;
        }

        Drawable shape = this.createRoundedRectangleDrawable(getResources().getColor(R.color.toggleColor, requireContext().getTheme()));
        this.frame.setBackground(shape);

        FrameLayout.LayoutParams circleParams = new FrameLayout.LayoutParams((int) getResources().getDimension(R.dimen.toggle_circle_size), (int) getResources().getDimension(R.dimen.toggle_circle_size));
        circleParams.gravity = Gravity.CENTER_VERTICAL | Gravity.START;
        this.circle.setLayoutParams(circleParams);
    }

    public void toggle () {
        if (this.isTrue)
            this.makeFalse();
        else
            this.makeTrue();
    }
    public boolean getState () {
        // do not send a reference. Edit: too late
        return this.isTrue;
    }

    private Drawable createRoundedRectangleDrawable (int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(color);
        drawable.setCornerRadius(getResources().getDimension(R.dimen.toggle_corner)); // Set your desired corner radius

        return drawable;
    }
}
