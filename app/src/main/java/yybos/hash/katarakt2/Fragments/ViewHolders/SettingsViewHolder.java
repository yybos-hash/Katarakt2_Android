package yybos.hash.katarakt2.Fragments.ViewHolders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import yybos.hash.katarakt2.R;
import yybos.hash.katarakt2.Socket.Objects.Server;

public class SettingsViewHolder extends RecyclerView.ViewHolder {
    public TextView serverIp;
    public TextView serverPort;
    public ConstraintLayout constraintLayout;

    public Server serverInfo;

    public SettingsViewHolder(@NonNull View itemView) {
        super(itemView);

        this.serverIp = itemView.getRootView().findViewById(R.id.serverServerIp);
        this.serverPort = itemView.getRootView().findViewById(R.id.serverServerPort);
        this.constraintLayout = itemView.getRootView().findViewById(R.id.serverConstraintLayout);
    }

    public void setInfo (Server serverInfo) {
        this.serverIp.setText(serverInfo.serverIp);
        this.serverPort.setText(String.valueOf(serverInfo.serverPort));

        this.serverInfo = serverInfo;
    }
}
