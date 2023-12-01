package yybos.hash.katarakt2.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        TextView toast = root.findViewById(R.id.customToastMessage);

        background.setBackgroundColor(this.backgroundColor.toArgb());
        toast.setText(this.message);

        // end toast after set time
        new Handler(Looper.getMainLooper()).postDelayed(this::end, 3000);
    }

    private void end () {
        // Get the FragmentManager
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

        // Begin a transaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.setCustomAnimations(R.anim.fragment_fade_in, R.anim.fragment_fade_out);
        // Remove the current fragment
        fragmentTransaction.remove(this);

        // Commit the transaction
        fragmentTransaction.commit();
    }
}