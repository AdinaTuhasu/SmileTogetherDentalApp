package com.example.smiletogether_dentalapp.Adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smiletogether_dentalapp.Model.Doctor;
import com.example.smiletogether_dentalapp.Model.Notification;
import com.example.smiletogether_dentalapp.Model.Patient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.smiletogether_dentalapp.R;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationAdapterViewHolder> {
    public static final String PATIENT = "Pacient";
    public static final String DOCTOR="Medic";



    DatabaseReference reference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://smiletogetherdentalapp-default-rtdb.firebaseio.com/");
    private final List<Notification> notifications;
    private final String user_type;
    private final Context context;

    public NotificationAdapter(List<Notification> notifications, String user_type, Context context) {
        this.notifications = notifications;
        this.user_type = user_type;
        this.context = context;

    }

    @NonNull
    @Override
    public NotificationAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notifications_list, parent, false);
        return new NotificationAdapterViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull NotificationAdapterViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        if (notification != null) {
            if (!notification.isNoticeRead()) {
                holder.cwNotification.setCardBackgroundColor(ContextCompat.getColor(context,R.color.unread_notifications));
            } else {
                holder.cwNotification.setCardBackgroundColor(ContextCompat.getColor(context,R.color.gray));
            }

            holder.tvTitleNotification.setText(notification.getTitle());
            holder.tvDateNotification.setText(notification.getDate());

            if (user_type.equals(PATIENT)) {
                // daca e conectat un pacient atunci el e receptorul iar doctorul e emitatorul
                // deci trebuie sa preiau notificarile care au id-ul pacientului ca receptor
                // si toate notificarile contin ca emitator diferiti medici deci trebuie sa preiau numele medicului
              reference.child("user").child(notification.getIdTransmitter()).addListenerForSingleValueEvent(takeDoctor(holder, notification));
            } else if (user_type.equals(DOCTOR)) {
                reference.child("user").child(notification.getIdTransmitter()).addListenerForSingleValueEvent(takePatient(holder, notification));
            }

        }
    }


    private ValueEventListener takePatient(NotificationAdapterViewHolder holder, Notification notification) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Patient patient = snapshot.getValue(Patient.class);
                String full_name = patient.getlastname() + " " + patient.getFirstname();
                String message = full_name;
                if (notification.getTitle().equals(context.getString(R.string.programare_anulata))) {
                    message += " a anulat programarea din " + notification.getAppointmentDate() + " de la ora " + notification.getAppointmentTime() + ".";
                } else if (notification.getTitle().equals(context.getString(R.string.programare_noua))) {
                    message += " a adăugat o nouă programare pe " + notification.getAppointmentDate() + " la ora " + notification.getAppointmentTime() + ".";
                }
                holder.tvMessageNotification.setText(message);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

    private ValueEventListener takeDoctor(NotificationAdapterViewHolder holder, Notification notification) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               Doctor doctor = snapshot.getValue(Doctor.class);
                String full_name = "Dr. " + doctor.getlastname() + " " + doctor.getFirstname();
                String message = full_name;
                if (notification.getTitle().equals(context.getString(R.string.programare_anulata))) {
                    message += " a anulat programarea din " + notification.getAppointmentDate() + " de la ora " + notification.getAppointmentTime() + ".";
                    holder.tvMessageNotification.setText(message);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }


    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class NotificationAdapterViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitleNotification;
        TextView tvMessageNotification;
        TextView tvDateNotification;
        CardView cwNotification;

        public NotificationAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitleNotification = itemView.findViewById(R.id.tvTitleNotification);
            tvMessageNotification = itemView.findViewById(R.id.tvNotificationMessage);
            tvDateNotification = itemView.findViewById(R.id.tvNotificationsDate);
            cwNotification = itemView.findViewById(R.id.cwNotification);
        }
    }
}