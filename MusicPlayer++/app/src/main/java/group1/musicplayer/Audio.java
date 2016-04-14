package group1.musicplayer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by shawn on 10/11/2015.
 */
public class Audio extends Activity {

    private ArrayList<String> selectedItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        selectedItems = new ArrayList<String>();
        displayFilteredAudioChoices();
    }

    //display a multiple choice list of audio files that are not already in the default list
    private void displayFilteredAudioChoices(){

        ArrayList<String> list = getIntent().getStringArrayListExtra("filteredAudioList");

        //populate the multiple choices with the list
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.select_dialog_multichoice,
                list);

        final AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(
                this,android.R.style.Theme_Holo_Dialog))
                .setTitle("Select Items to Add to the Player")
                .setAdapter(arrayAdapter, null)

                        //set action for the okay button
                .setPositiveButton("Add to List", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        //send the list of audio titles back to the main activity
                        Intent intent = new Intent();
                        intent.putStringArrayListExtra("additionalSongs", selectedItems);
                        setResult(RESULT_OK, intent);
                        finish();//go back to the previous page
                    }
                })

                        //set action for the cancel button
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        finish();//go back to the previous page
                    }
                })
                .create();
        dialog.getListView().setItemsCanFocus(false);
        dialog.getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        dialog.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                CheckedTextView textView = (CheckedTextView) view;
                if (textView.isChecked()) {//the user selected an item to add
                    selectedItems.add(textView.getText().toString());
                }
                //the user has unselected the item, so remove it from the list
                else if (selectedItems.contains(textView.getText().toString())) {
                    selectedItems.remove(textView.getText().toString());
                }
            }
        });//end of dialog

        //have the activity end if the user presses the back button
        dialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    finish();
                    dialog.dismiss();
                }
                return true;
            }
        });

        dialog.show();
    }
}