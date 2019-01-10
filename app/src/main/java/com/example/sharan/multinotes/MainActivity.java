package com.example.sharan.multinotes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.JsonWriter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener{

    private static final int B_REQUEST_CODE = 1;

    private List<Notes> notesList = new ArrayList<>();  // Main content is here

    private RecyclerView recyclerView; // Layout's recyclerview

    private NotesAdapter nAdapter; // Data to recyclerview adapter

    private int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recycler);

        nAdapter = new NotesAdapter(notesList, this);

        recyclerView.setAdapter(nAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        //call a async method and that method should return a noteList if exist otherwise null
        new MyAsyncTask(this).execute();
    }

    @Override
    public void onClick(View v) {  // click listener called by ViewHolder clicks

        pos = recyclerView.getChildLayoutPosition(v);
        Notes extraNote = notesList.get(pos);
        //Send the content of the selected note to the edit view
        Intent intent1 = new Intent(MainActivity.this, EditActivity.class);
        intent1.putExtra("TITLE", extraNote.getTitle());
        intent1.putExtra("DATE", extraNote.getDate());
        intent1.putExtra("NOTE", extraNote.getNote());
        startActivityForResult(intent1, B_REQUEST_CODE);
    }

    @Override
    public boolean onLongClick(View v) {  // long click listener called by ViewHolder long clicks
        pos = recyclerView.getChildLayoutPosition(v);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                notesList.remove(pos);
                nAdapter.notifyDataSetChanged();
                pos = -1;
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                pos = -1;
            }
        });

        builder.setMessage("Do you want to delete the note?");
        builder.setTitle("Note Delete");

        AlertDialog dialog = builder.create();
        dialog.show();


        return false;
    }

    @Override
    protected void onResume(){
        notesList.size();
        super.onResume();
        nAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {

        //Save the notesList to the Json file
        saveNotes();
        super.onPause();
    }

    private void saveNotes() {

        try {
            FileOutputStream fos = getApplicationContext().openFileOutput(getString(R.string.file_name), Context.MODE_PRIVATE);

            JsonWriter writer = new JsonWriter(new OutputStreamWriter(fos, getString(R.string.encoding)));
            writer.setIndent("  ");
            writer.beginObject();
            writer.name("notes");
            writeNotesArray(writer);
            writer.endObject();
            writer.close();

//            Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    public void writeNotesArray(JsonWriter writer) throws IOException {
        writer.beginArray();
        for (Notes value : notesList) {
            writeNotesObject(writer, value);
        }
        writer.endArray();
    }

    public void writeNotesObject(JsonWriter writer, Notes val) throws IOException{
        writer.beginObject();
        writer.name("title").value(val.getTitle());
        writer.name("date").value(val.getDate());
        writer.name("note").value(val.getNote());
        writer.endObject();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.about:

                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
                return true;
            case R.id.addNote:

                Intent intent1 = new Intent(MainActivity.this, EditActivity.class);
                startActivityForResult(intent1, B_REQUEST_CODE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == B_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Notes edit_note = (Notes) data.getExtras().getSerializable("EDIT_NOTE");
                String status = data.getStringExtra("STATUS");

                if(status.equals("NO_CHANGE")){
                    //Do nothing
                }
                else if(status.equals("CHANGE")){
                    //remove the old data
                    notesList.remove(pos);
                    notesList.add(0, edit_note);
                }
                else if(status.equals("NEW")){
                    notesList.add(0, edit_note);
                }
//                notesList.add(edit_note);

            }

        }
    }

    public List<Notes> getNotesList() {
        return notesList;
    }

    public void setNotesList(List<Notes> notesList) {
        this.notesList = notesList;
        nAdapter.notifyDataSetChanged();
    }

}