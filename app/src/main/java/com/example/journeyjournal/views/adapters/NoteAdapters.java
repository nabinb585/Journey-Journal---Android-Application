package com.example.journeyjournal.views.adapters;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.journeyjournal.R;
import com.example.journeyjournal.views.entities.Notes;
import com.example.journeyjournal.views.listeners.NotesListeners;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NoteAdapters extends RecyclerView.Adapter<NoteAdapters.NoteViewHolder> {

    private List<Notes> notes;
    private NotesListeners notesListeners;
    private Timer timer;
    private List<Notes> journalNotesSource;

    public NoteAdapters(List<Notes> notes, NotesListeners notesListeners) {
        this.notes = notes;
        this.notesListeners = notesListeners;
        journalNotesSource = notes;
    }


    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_container_notes,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.setNote(notes.get(position));
        holder.layoutNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notesListeners.onNotesClicked(notes.get(position), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textSubtitle, textDateTime;
        RelativeLayout layoutNote;
        RoundedImageView imageNote;

        NoteViewHolder(@Nullable View itemView) {
            super(itemView);

            textTitle = itemView.findViewById(R.id.textTitle);
            textSubtitle = itemView.findViewById(R.id.textSubtitle);
            textDateTime = itemView.findViewById(R.id.textDateTime);
            layoutNote = itemView.findViewById(R.id.layoutNote);
            imageNote = itemView.findViewById(R.id.imageNote);
        }

        void setNote(Notes notes) {
            textTitle.setText(notes.getTitle());
            if (notes.getSubTitle().trim().isEmpty()) {
                textSubtitle.setVisibility(View.GONE);
            } else {
                textSubtitle.setText(notes.getSubTitle());
            }
            textDateTime.setText(notes.getDateTime());
            GradientDrawable gradientDrawable = (GradientDrawable) layoutNote.getBackground();
            if(notes.getColor() != null){
                gradientDrawable.setColor(Color.parseColor(notes.getColor()));
            }
            else {
                gradientDrawable.setColor(Color.parseColor("#333333"));
            }
            if (notes.getImagePath()!= null){
                imageNote.setImageBitmap(BitmapFactory.decodeFile(notes.getImagePath()));
                imageNote.setVisibility(View.VISIBLE);
            }
            else {
                imageNote.setVisibility(View.GONE);
            }
        }
    }
    public void searchJournalNotes(final String searchKeyword){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (searchKeyword.trim().isEmpty()){
                    notes = journalNotesSource;
                }
                else {
                    ArrayList<Notes> temp = new ArrayList<>();
                    for (Notes notes : journalNotesSource){
                        if (notes.getTitle().toLowerCase().contains(searchKeyword.toLowerCase())
                            || notes.getSubTitle().toLowerCase().contains(searchKeyword.toLowerCase())
                            || notes.getNoteText().toLowerCase().contains(searchKeyword.toLowerCase())){
                            temp.add(notes);
                        }
                    }
                    notes = temp;
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });

            }
        }, 500);
    }
    public void cancelTimer(){
        if (timer != null){
            timer.cancel();
        }
    }
}
