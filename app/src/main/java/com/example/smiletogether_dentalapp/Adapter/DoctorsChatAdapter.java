package com.example.smiletogether_dentalapp.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.smiletogether_dentalapp.Model.Chat;
import com.example.smiletogether_dentalapp.Model.Doctor;
import com.example.smiletogether_dentalapp.Model.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.example.smiletogether_dentalapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorsChatAdapter extends RecyclerView.Adapter<DoctorsChatAdapter.DoctorsConversationViewHolder> {
    private final List<Doctor> doctors;
    private final Context context;
    private final DoctorAdapter.OnDoctorClickListener onDoctorClickListener;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://smiletogetherdentalapp-default-rtdb.firebaseio.com/");
    private String idUserConnected;

    public DoctorsChatAdapter(List<Doctor> doctors, Context context, DoctorAdapter.OnDoctorClickListener onDoctorClickListener) {
        this.doctors = doctors;
        this.context = context;
        this.onDoctorClickListener = onDoctorClickListener;
        idUserConnected = auth.getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public DoctorsConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_of_messages, parent, false);
        return new DoctorsConversationViewHolder(view, onDoctorClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorsConversationViewHolder holder, int position) {
        Doctor doctor=doctors.get(position);
        if (doctor != null) {
            if (doctor.getUrlProfilePhoto()!=null && !doctor.getUrlProfilePhoto().equals("") ) {
                Glide.with(context).load(doctor.getUrlProfilePhoto()).into(holder.ciwProfilePhoto);
            } else {
                Glide.with(context).load(R.drawable.profile_photo).into(holder.ciwProfilePhoto);
            }

            String name = "Dr. " +doctor.getlastname() + " " +doctor.getFirstname() ;
            holder.tvName.setText(name);

            reference.child("Conversatii").addValueEventListener(takesConversation(doctor.getId(), holder));
        }
    }

    private ValueEventListener takesConversation(String idDoctor, DoctorsConversationViewHolder holder) {
        return new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    // daca in conversatia cu user-ul concetat si cu medicul respectiv ultimul mesaj e trimis de pacient
                    // atunci setez textul pt tvUltimulMesaj cu "Dvs: text mesaj";
                    Message lastMessage = chat.getMessages().get(chat.getMessages().size() - 1);
                    String text;

                    if (lastMessage.getIdTransmitter().equals(idUserConnected) && lastMessage.getIdReceiver().equals(idDoctor))
                    {
                        text = lastMessage.getText();
                        if (text.length() > 35) {
                            text = text.substring(0, 32) + "...";
                        }
                        holder.tvLastMessage.setText(text);
                        if (lastMessage.isMessageRead()) {
                            holder.tvLastMessage.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_read_message_24, 0, 0, 0);
                        } else {
                            holder.tvLastMessage.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_send_message_24, 0, 0, 0);
                        }
                    } else if (lastMessage.getIdTransmitter().equals(idDoctor) && lastMessage.getIdReceiver().equals(idUserConnected))
                    {
                        text = lastMessage.getText();
                        if (text.length() > 35) {
                            text = text.substring(0, 32) + "...";
                        }
                        holder.tvLastMessage.setText(text);
                        if (!lastMessage.isMessageRead()) {
                            holder.tvLastMessage.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
                            //int unreadMessage = (int) chat.getMessages().stream().filter(message -> !message.isMessageRead()).count();
                            int unreadMessage = 0;
                            List<Message> messages = chat.getMessages();

                            for (Message message : messages) {
                                if (!message.isMessageRead()) {
                                    unreadMessage++;
                                }
                            }

                            holder.btnUnreadMessage.setVisibility(View.VISIBLE);
                            holder.btnUnreadMessage.setText(String.valueOf(unreadMessage));
                        } else {
                            holder.tvLastMessage.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
                            holder.btnUnreadMessage.setVisibility(View.GONE);
                        }
                    }
//                    break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

    @Override
    public int getItemCount() {
        return doctors.size();
    }

    public class DoctorsConversationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CircleImageView ciwProfilePhoto;
        TextView tvName;
        TextView tvLastMessage;
        AppCompatButton btnUnreadMessage;

        DoctorAdapter.OnDoctorClickListener onDoctorClickListener;

        public DoctorsConversationViewHolder(@NonNull View itemView, DoctorAdapter.OnDoctorClickListener onDoctorClickListener) {
            super(itemView);
            ciwProfilePhoto = itemView.findViewById(R.id.ciwProfilePhoto_user);
            tvName = itemView.findViewById(R.id.tvName_user);
            tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
            btnUnreadMessage = itemView.findViewById(R.id.btnUnreadMessages);

            this.onDoctorClickListener = onDoctorClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onDoctorClickListener.onDoctorClick(getAdapterPosition());
        }
    }
}
