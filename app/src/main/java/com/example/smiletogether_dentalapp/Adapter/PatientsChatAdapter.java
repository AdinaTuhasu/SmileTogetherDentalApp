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

import com.example.smiletogether_dentalapp.Model.Chat;
import com.example.smiletogether_dentalapp.Model.Message;
import com.example.smiletogether_dentalapp.Model.Patient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.smiletogether_dentalapp.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PatientsChatAdapter extends RecyclerView.Adapter<PatientsChatAdapter.PatientsConversationViewHolder> {
    private final List<Patient> patients;
    private final Context context;
    private final PatientAdapter.OnPatientClickListener onPatientClickListener;
    private String idUserConnected;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://smiletogetherdentalapp-default-rtdb.firebaseio.com/");

    public PatientsChatAdapter(List<Patient> patients, Context context, PatientAdapter.OnPatientClickListener onPatientClickListener) {
        this.patients = patients;
        this.context = context;
        this.onPatientClickListener = onPatientClickListener;
        idUserConnected = auth.getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public PatientsConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_of_messages, parent, false);
        return new PatientsChatAdapter.PatientsConversationViewHolder(view, onPatientClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientsConversationViewHolder holder, int position) {
        Patient patient = patients.get(position);
        if (patient != null) {
           /* if (!pacient.getUrlPozaProfil().equals(""))
                Glide.with(context).load(pacient.getUrlPozaProfil()).into(holder.ciwPozaProfil);
*/
            String name = patient.getlastname() + " " + patient.getFirstname();
            holder.tvName.setText(name);

            reference.child("Conversatii").addValueEventListener(takesConversation(patient.getId(), holder));
        }
    }

    private ValueEventListener takesConversation(String idPatient, PatientsConversationViewHolder holder) {
        return new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    Message lastMessage = chat.getMessages().get(chat.getMessages().size() - 1);

                    String text;

                    if (lastMessage.getIdTransmitter().equals(idUserConnected) && lastMessage.getIdReceiver().equals(idPatient)) {
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
                    } else if (lastMessage.getIdTransmitter().equals(idPatient) && lastMessage.getIdReceiver().equals(idUserConnected)) {
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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

    @Override
    public int getItemCount() {
        return patients.size();
    }

    public class PatientsConversationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CircleImageView ciwProfilePhoto;
        TextView tvName;
        TextView tvLastMessage;
        AppCompatButton btnUnreadMessage;

        PatientAdapter.OnPatientClickListener onPatientClickListener;

        public PatientsConversationViewHolder(@NonNull View itemView, PatientAdapter.OnPatientClickListener onPatientClickListener) {
            super(itemView);
            ciwProfilePhoto = itemView.findViewById(R.id.ciwProfilePhoto_user);
            tvName = itemView.findViewById(R.id.tvName_user);
            tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
            btnUnreadMessage = itemView.findViewById(R.id.btnUnreadMessages);

            this.onPatientClickListener = onPatientClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onPatientClickListener.onPatientClick(getAdapterPosition());
        }
    }
}
