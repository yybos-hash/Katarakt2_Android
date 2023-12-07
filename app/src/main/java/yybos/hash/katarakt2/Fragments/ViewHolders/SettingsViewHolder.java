package yybos.hash.katarakt2.Fragments.ViewHolders;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentContainerView;
import androidx.recyclerview.widget.RecyclerView;

import yybos.hash.katarakt2.Fragments.CustomToggleFragment;
import yybos.hash.katarakt2.R;
import yybos.hash.katarakt2.Socket.Objects.Server;

public class SettingsViewHolder extends RecyclerView.ViewHolder {
    public TextView serverIp;
    public TextView serverPort;
    public ConstraintLayout constraintLayout;
    public LinearLayout mainLinearLayout;
    public FragmentContainerView fragmentContainer;

    public CustomToggleFragment toggleInstance;

    public Server serverInfo;

    public boolean isDefault = false;

    public SettingsViewHolder(@NonNull View itemView) {
        super(itemView);

        View root = itemView.getRootView();

        this.serverIp = root.findViewById(R.id.serverServerIp);
        this.serverPort = root.findViewById(R.id.serverServerPort);
        this.constraintLayout = root.findViewById(R.id.serverConstraintLayout);
        this.fragmentContainer = root.findViewById(R.id.serverFragmentContainer);
        this.mainLinearLayout = root.findViewById(R.id.serverMainLinearLayout);

        this.fragmentContainer.setId(View.generateViewId());

        this.toggleInstance = new CustomToggleFragment();
    }

    public void setInfo (Server serverInfo) {
        this.serverIp.setText(serverInfo.serverIp);
        this.serverPort.setText(String.valueOf(serverInfo.serverPort));

        this.serverInfo = serverInfo;
    }
}
