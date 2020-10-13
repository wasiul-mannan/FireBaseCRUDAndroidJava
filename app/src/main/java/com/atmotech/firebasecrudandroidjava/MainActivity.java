package com.atmotech.firebasecrudandroidjava;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.atmotech.firebasecrudandroidjava.adapter.NotesAdapter;
import com.atmotech.firebasecrudandroidjava.model.Notes;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fbtn_add;
    private RecyclerView recyclerView;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference( "Notes");

    private List<Notes> list= new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fbtn_add=findViewById(R.id.fbtn_add);
        recyclerView=findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        fbtn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogAddNote();
            }
        });

        read_data();


    }

    private void showDialogAddNote() {
        final Dialog dialog=new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_note);

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams ();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);

        ImageButton btnClose = dialog.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        final EditText etNote = dialog.findViewById(R.id.etNote);
        Button btnAdd=dialog.findViewById(R.id.btn_add);


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(etNote.getText())) {

                    etNote.setError("This field can't be empty!");

                } else {

                    addDataToFirebase (etNote.getText().toString());
                    dialog.dismiss();
                }
            }
        });

        dialog.show();

    }

    private void addDataToFirebase(final String text){



        String id=myRef.push().getKey();
        Notes notes=new Notes(id,text);

        myRef.child(id).setValue(notes).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(),"Successfully",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void read_data() {
        // Read from the database
        list.clear();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Notes value = snapshot.getValue(Notes.class);
                    list.add(value);
                }

                recyclerView.setAdapter(new NotesAdapter(MainActivity.this,list));

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException());

            }
        });
    }
}