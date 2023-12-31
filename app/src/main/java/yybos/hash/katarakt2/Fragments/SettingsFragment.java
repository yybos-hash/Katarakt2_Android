package yybos.hash.katarakt2.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import org.jetbrains.annotations.Nullable;

import yybos.hash.katarakt2.Fragments.Settings.ChatSetting;
import yybos.hash.katarakt2.Fragments.Settings.StartupSetting;
import yybos.hash.katarakt2.MainActivity;
import yybos.hash.katarakt2.R;

public class SettingsFragment extends Fragment {
    private MainActivity mainActivityInstance;

    private LinearLayout linearLayoutA;
    private LinearLayout linearLayoutB;
    private LinearLayout linearLayoutC;

    private FrameLayout frame;

    private NestedScrollView nestedScrollView;
    private GestureDetector gestureDetector;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        this.nestedScrollView = view.findViewById(R.id.settingsNestedScroll);

        // Initialize GestureDetector
        this.gestureDetector = new GestureDetector(getContext(), new GestureListener());

        // Set onTouchListener on NestedScrollView
        nestedScrollView.setOnTouchListener((v, event) -> {
            // v.performClick();

            // Pass the touch event to GestureDetector
            gestureDetector.onTouchEvent(event);
            return true;
        });

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated (@NonNull View view, @Nullable Bundle savedInstanceState) {
        View root = view.getRootView();

        this.mainActivityInstance = (MainActivity) requireActivity();

        // move selection tab (I'm doing it from the fragment cause it will fix the issue where if I used the back stack trace the selectionTab wouldnt move)
        this.mainActivityInstance.moveSelectionTab(this);

        this.linearLayoutA = root.findViewById(R.id.settingsLinearLayoutA);
        this.linearLayoutB = root.findViewById(R.id.settingsLinearLayoutB);
        this.linearLayoutC = root.findViewById(R.id.settingsLinearLayoutC);

        this.frame = root.findViewById(R.id.settingsFrameLayout);

        this.addSettingTo(new StartupSetting(), this.linearLayoutA);
        this.addSettingTo(new ChatSetting(), this.linearLayoutA);
        this.addSettingTo(new StartupSetting(), this.linearLayoutA);

        // Scroll to the center point
        this.nestedScrollView.post(() -> {
            // Determine the center point
            int centerX = (this.frame.getWidth() / 3);
            int centerY = (this.frame.getHeight() / 4);

            nestedScrollView.scrollTo(centerX, centerY);
        });
    }

    private void addSettingTo (Fragment setting, LinearLayout linearLayout) {
        FrameLayout frame = new FrameLayout(this.requireContext());
        frame.setId(View.generateViewId());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        int margin = (int) getResources().getDimension(R.dimen.settings_fragment_margin);
        params.setMargins(margin, margin, margin, margin);

        ((FragmentActivity) requireContext()).getSupportFragmentManager().beginTransaction()
                .add(frame.getId(), setting)
                .commit();

        linearLayout.addView(frame, params);
    }

    // Custom GestureListener to handle fling gestures
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            // Return true to indicate that you have consumed the event
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            // Adjust the scroll position based on the scroll distance
            SettingsFragment.this.nestedScrollView.smoothScrollBy((int) distanceX, (int) distanceY);
            return true;
        }
    }
}