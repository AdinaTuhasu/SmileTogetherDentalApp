package com.example.smiletogether_dentalapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smiletogether_dentalapp.Model.Message;

import java.util.List;
import com.example.smiletogether_dentalapp.R;
import com.google.firebase.auth.FirebaseAuth;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
public static final int RECEIVE_MESSAGE= 0;
public static final int SEND_MESSAGE= 1;
private final List<Message> messages;
private final Context context;

private String idUserConnected;

public ChatAdapter(List<Message> messages, Context context) {
        this.messages = messages;
        this.context = context;
        }

@NonNull
@Override
public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == SEND_MESSAGE) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.send_message, parent, false);
        } else {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.received_message, parent, false);
        }
        return new ChatViewHolder(view);
        }

@Override
public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Message message = messages.get(position);
        if (message != null) {
        holder.tvMessage.setText(message.getText());

        if (position == messages.size() - 1 && getItemViewType(position) == SEND_MESSAGE) {
        holder.tvMessageStatus.setVisibility(View.VISIBLE);
        if (message.isMessageRead()) {
        holder.tvMessageStatus.setText("Vazut");
        holder.tvMessageStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_read_message_24, 0);
        } else {
        holder.tvMessageStatus.setText("Livrat");
        holder.tvMessageStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_send_message_24, 0);
        }
        } else {
        holder.tvMessageStatus.setVisibility(View.GONE);
        }
        }
        }

@Override
public int getItemCount() {
        return messages.size();
        }

@Override
public int getItemViewType(int position) {
        idUserConnected = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (messages.get(position).getIdTransmitter().equals(idUserConnected)) {
        return SEND_MESSAGE;
        } else {
        return RECEIVE_MESSAGE;
        }
        }

public class ChatViewHolder extends RecyclerView.ViewHolder {
    TextView tvMessage;
    TextView tvMessageStatus;

    public ChatViewHolder(@NonNull View itemView) {
        super(itemView);
        tvMessage = itemView.findViewById(R.id.tvMessage);
        tvMessageStatus = itemView.findViewById(R.id.tvStatusMessage);
    }
}
}
