package yybos.hash.katarakt2.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.Nullable;

import yybos.hash.katarakt2.MainActivity;
import yybos.hash.katarakt2.R;

public class SettingsFragment extends Fragment {
    private MainActivity mainActivityInstance;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.mainActivityInstance = ((MainActivity) requireActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated (@NonNull View view, @Nullable Bundle savedInstanceState) {
        // move selection tab (I'm doing it from the fragment cause it will fix the issue where if I used the back stack trace the selectionTab wouldnt move)
        this.mainActivityInstance.moveSelectionTab(this);
    }
}