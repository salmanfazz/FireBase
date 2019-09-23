package com.example.firebase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddTrackActivity extends AppCompatActivity {

    TextView tvArtistName;
    EditText edtTrackName;
    SeekBar seekBarRating;
    Button btnAddTrack;

    ListView lvTracks;

    DatabaseReference databaseTracks;

    List<Track> trackList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_track);

        tvArtistName = (TextView) findViewById(R.id.tvNameArtist);
        edtTrackName = (EditText) findViewById(R.id.edtTrackName);
        seekBarRating = (SeekBar) findViewById(R.id.seekbarRating);
        btnAddTrack = (Button) findViewById(R.id.btnAddTrack);

        lvTracks = (ListView) findViewById(R.id.lvTracks);

        Intent intent = getIntent();

        trackList = new ArrayList<>();

        String id = intent.getStringExtra(MainActivity.ARTIST_ID);
        String name = intent.getStringExtra(MainActivity.ARTIST_NAME);

        tvArtistName.setText(name);

        databaseTracks = FirebaseDatabase.getInstance().getReference("tracks").child(id);

        btnAddTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTrack();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseTracks.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                trackList.clear();

                for (DataSnapshot trackSnapshot : dataSnapshot.getChildren()) {
                    Track track = trackSnapshot.getValue(Track.class);
                    trackList.add(track);
                }
                TrackList trackListadapter = new TrackList(AddTrackActivity.this, trackList);
                lvTracks.setAdapter(trackListadapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void saveTrack() {
        String trackName = edtTrackName.getText().toString().trim();
        int rating = seekBarRating.getProgress();

        if(!TextUtils.isEmpty(trackName)) {
            String id = databaseTracks.push().getKey();

            Track track= new Track(id, trackName, rating);
            databaseTracks.child(id).setValue(track);

            Toast.makeText(this, "Track saved successfully", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Track name should not be empty", Toast.LENGTH_LONG).show();
        }
    }
}