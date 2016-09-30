package com.example.seq;

import com.example.seq.Home.PlaceholderFragment;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class Help extends ActionBarActivity{
	
	int count=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		Button prev = (Button) findViewById(R.id.button3);
		prev.setEnabled(false);
		TextView tv = (TextView) findViewById(R.id.textV);
		tv.setText("Welcome to Music Sequencer!\n\n"
				+"Go to \"Create\" to make songs.\n"
				+"Go to\"Record\" to record new sounds to be used in your songs.\n\n");
	}
	
	public void next(View v){
		if (count < 4){
			Button prev = (Button) findViewById(R.id.button3);
			prev.setEnabled(true);
			count ++;
			displayPage();

			if (count == 3){
				Button next = (Button) findViewById(R.id.button2);
				next.setEnabled(false);
			}
		}
	}
	
	public void prev(View v){
		if (count > 0){
			Button next = (Button) findViewById(R.id.button2);
			next.setEnabled(true);
			count --;
			displayPage();

			if (count == 0){
				Button prev = (Button) findViewById(R.id.button3);
				prev.setEnabled(false);
			}
		}
	}
	
	public void displayPage(){
		TextView tv = (TextView) findViewById(R.id.textV);
		
		if (count==0){
			tv.setText("Welcome to Music Sequencer!\n\n"
					+"Go to \"Create\" to make songs.\n"
					+"Go to\"Record\" to record new sounds to be used in your songs.\n\n");
			return;
		}
		
		if (count==1){
			tv.setText("In \"Create\", select sounds using the two spinners. The top spinner selects an instrument, the bottom a sound within that instrument.\n"
				+"Each instrument has 9 sounds. Where applicable, the first 8 sounds repesent an octave, with the last sound being a \"special\" sound in some way.\n"
				+"Hit \"Sample\" to play the currently selected sound.\n"
				+"Place the sound on the page by hitting a button on the grid to the right.\n"
				+"Each page is represented by an 8x8 grid of buttons.\n"
				+"Only 4 rows of the grid are displayed at a time. To view the other 4, use the up or down arrow buttons.\n"
				+"Sounds on the grid will be played in sequence, column by column. Sounds in the same column will be played at the same time.\n"
				);
			return;
		}
		if (count==2){
			tv.setText(
					"Pages can be added or removed useing the labeled buttons. Pages are always added to or removed from the end.\n"
					+"Pages can be navigated using the left and right arrow buttons.\n"
					+"Pages can be played in loop indivualully or in order using \"Play Page\" and \"Play All\".\n"
					+"Sounds can be removed per page or all at once similarly using the reset buttons.\n"
					+"Sounds can also be removed indivualully using the \"Erase\" button. Once pressed, any button on the grid pressed will be cleared. Press \"Erase\" again to add sounds as normal.\n"
					+"Adjust playback speed using the \"+\" and \"- \"buttons.\n"
					+"Some editting fuctions will be unavabile during playback.\n");
				return;
		}
		if (count==3){
			tv.setText("In \"Record\" up to 9 new sounds can be recorded using the android microphone to be used in \"Create\".\n"
					+"All recordings made will be one second long.\n"
					+"Select a sound to record over with the spinner.\n"
					+"Record a new sound with \"Record\". Play your recorded sound with \"Play New\".\n"
					+"\"Play Old\" can be used to compare the sounds.\n"
					+"Hit \"Save Over\" to overwrite the old sound.\n"
					+"Recordings can be accessed in \"Create\" with the \"Recording\" instrument.\n"
					+"If there is no recording for a given slot, it will default to Drum 9.");
				return;
		}
	}
	
	public void back(View v){
		super.onBackPressed();
	}
}
