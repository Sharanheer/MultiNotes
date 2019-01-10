package com.example.sharan.multinotes;

import android.os.AsyncTask;
import android.util.JsonReader;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MyAsyncTask extends AsyncTask<Void, Void, Void> { //  <Parameter, Progress, Result>

//    private static final String TAG = "MyAsyncTask";

    private MainActivity mainActivity;

//    public List<Notes> tempNoteList = new ArrayList<>();

    public MyAsyncTask(MainActivity ma) {
        mainActivity = ma;
    }

    @Override
    protected Void doInBackground(Void... voids) {


        try {

            InputStream is = mainActivity.getApplicationContext().openFileInput(mainActivity.getString(R.string.file_name));
            JsonReader reader = new JsonReader(new InputStreamReader(is, mainActivity.getString(R.string.encoding)));
            String name;

            reader.beginObject();

            while (reader.hasNext()) {
                name = reader.nextName();
                if (name.equals("notes")) {

                    reader.beginArray();
                    while (reader.hasNext()) {
                        Notes tempNotes = new Notes();
                        reader.beginObject();
                        while(reader.hasNext()) {
                            name = reader.nextName();
                            if (name.equals("title")) {
                                tempNotes.setTitle(reader.nextString());
                            } else if (name.equals("date")) {
                                tempNotes.setDate(reader.nextString());
                            } else if (name.equals("note")) {
                                tempNotes.setNote(reader.nextString());
                            } else {
                                reader.skipValue();
                            }
                        }
                        reader.endObject();
                        mainActivity.getNotesList().add(tempNotes);

                    }
                    reader.endArray();
                }
                else{
                    reader.skipValue();
                }

            }
            reader.endObject();

        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error: "+e);
        }
//        return note_list;
        return null;
    }

//    @Override
//    protected void onPostExecute(List<Notes> temp_list) {
//        // This method is almost always used - handles the results of doInBackground
//        super.onPostExecute(temp_list);
//        if(temp_list.size() > 0){
//            Collections.reverse(temp_list);
//            mainActivity.setNotesList(temp_list);
//        }
//
//    }

}

