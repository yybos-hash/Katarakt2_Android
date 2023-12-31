package yybos.hash.katarakt2.Fragments.Settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import org.jetbrains.annotations.Nullable;

import yybos.hash.katarakt2.Fragments.Custom.CustomToggleFragment;
import yybos.hash.katarakt2.R;

public class StartupSetting extends Fragment {
    public StartupSetting () {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.setting_startup, container, false);
    }

    @Override
    public void onViewCreated (@NonNull View view, @Nullable Bundle savedInstanceState) {
        View root = view.getRootView();

        FrameLayout toggleA = root.findViewById(R.id.settingStartupToggle);
        FrameLayout toggleB = root.findViewById(R.id.settingStartupToggle2);

        CustomToggleFragment customToggleA = new CustomToggleFragment();
        CustomToggleFragment customToggleB = new CustomToggleFragment();

        toggleA.setOnClickListener((v) -> {
            customToggleA.toggle();
        });
        toggleB.setOnClickListener((v) -> {
            customToggleB.toggle();
        });

        toggleA.setId(View.generateViewId());
        toggleB.setId(View.generateViewId());

        FragmentManager fragmentManager;

        fragmentManager = getParentFragmentManager();
        if (fragmentManager.isStateSaved())
            fragmentManager.executePendingTransactions();

        fragmentManager.beginTransaction()
                .add(toggleA.getId(), customToggleA)
                .commit();

        // second fragment

        fragmentManager = getParentFragmentManager();
        if (fragmentManager.isStateSaved())
            fragmentManager.executePendingTransactions();

        fragmentManager.beginTransaction()
                .add(toggleB.getId(), customToggleB)
                .commit();
    }
}
