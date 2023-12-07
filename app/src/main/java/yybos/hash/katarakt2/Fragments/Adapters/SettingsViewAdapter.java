package yybos.hash.katarakt2.Fragments.Adapters;

import static androidx.recyclerview.widget.RecyclerView.NO_POSITION;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import yybos.hash.katarakt2.Fragments.LoginFragment;
import yybos.hash.katarakt2.Fragments.ViewHolders.SettingsViewHolder;
import yybos.hash.katarakt2.R;
import yybos.hash.katarakt2.Socket.Objects.Server;

public class SettingsViewAdapter extends RecyclerView.Adapter<SettingsViewHolder> {
    private final List<Server> servers = new ArrayList<>();
    private final LoginFragment loginFragmentInstance;

    public SettingsViewAdapter (LoginFragment fragment) {
        this.loginFragmentInstance = fragment;
    }

    public void addServer (Server server) {
        this.servers.add(server);
        notifyItemChanged(this.servers.size() - 1);
    }
    public void updateServer(SettingsViewHolder serverViewHolder) {
        int position = serverViewHolder.getAdapterPosition();
        if (position == NO_POSITION) {
            this.addServer(serverViewHolder.serverInfo);
            return;
        }

        Server server = this.servers.get(position);
        server.set(serverViewHolder.serverInfo);

        notifyItemChanged(position);
    }

    public List<Server> getServers() {
        return new ArrayList<>(this.servers);
    }

    @NonNull
    @Override
    public SettingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_server, parent, false);
        return new SettingsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SettingsViewHolder holder, int position) {
        Server server = this.servers.get(position);

        holder.setInfo(server);

        // Dynamically add CustomToggleFragment to the FragmentContainerView
        FragmentTransaction transaction = ((FragmentActivity) holder.itemView.getContext()).getSupportFragmentManager().beginTransaction();
        transaction.replace(holder.fragmentContainer.getId(), holder.toggleInstance);
        transaction.commit();

        holder.mainLinearLayout.setOnLongClickListener((v) -> {
            this.loginFragmentInstance.openInfo(holder);
            return true;
        });
        holder.mainLinearLayout.setOnClickListener((v) -> {
            this.loginFragmentInstance.tryConnection(server);
        });
        holder.fragmentContainer.setOnClickListener((v) -> {
            holder.toggleInstance.toggle();
        });
    }

    @Override
    public int getItemCount() {
        return this.servers.size();
    }
}
