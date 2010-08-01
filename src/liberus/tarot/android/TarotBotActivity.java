package liberus.tarot.android;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;

import liberus.tarot.android.R;
import liberus.tarot.android.R.array;
import liberus.tarot.android.R.id;
import liberus.tarot.android.R.layout;
import liberus.tarot.android.R.string;
import liberus.tarot.deck.RiderWaiteDeck;
import liberus.tarot.interpretation.BotaInt;
import liberus.tarot.querant.Querant;
import liberus.utils.WebUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;


public class TarotBotActivity extends Activity  implements OnClickListener, View.OnClickListener, OnItemSelectedListener, OnDateChangedListener {
	private DatePicker dp;
	private Spinner statusspin;
	private Button initbutton;

	String whatUrl = "http://liber.us/tarotbot/what-is-this";
	String howUrl = "http://liber.us/tarotbot/how-does-it-work";
	String tarotUrl = "http://liber.us/tarotbot/what-is-tarot";
	String[] helpUrls = new String[]{whatUrl,howUrl,tarotUrl};
	
	private Querant aq;
	//private RelativeLayout mainlayout;
	private boolean male = false;
	private boolean partnered = false;
	private boolean unselected = true;
	private boolean firstpass=true;

	private int secondSetIndex=79;
	protected RelativeLayout secondlayout;

	private static final int SWIPE_MIN_DISTANCE = 80;
	private static final int SWIPE_MAX_OFF_PATH = 180;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private static final int MENU_SAVE = 0;
	private static final int MENU_SHARE = 1;
	private static final int MENU_LOAD = 2;
	private static final int MENU_HELP = 3;
	private GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;
	//private ViewFlipper display;
	protected LayoutInflater inflater;
	protected View showing;
	//private RelativeLayout wasShowing;
	private TextView infotext;
	private Button agreebutton;
	private Button disagreebutton;
	protected TextView closure;

	private SharedPreferences querantPrefs;
	private SharedPreferences readingPrefs;
	private int statusselected;
	private ViewFlipper flipper = null;
	private ArrayList<Integer> type = new ArrayList<Integer>();
	private ArrayList<Integer> flipdex = new ArrayList<Integer>();
	private ArrayList<RelativeLayout> laidout = new ArrayList<RelativeLayout>();
	private static BotaInt myInt;
	private String saveTitle;
	private EditText input;
	private boolean infoDisplayed;
	private int myRandomCard;
	private CheckBox reversalCheck;
	private boolean sharing;
	private ViewFlipper oldFlipper;
	private String saveResult;
	private boolean begun = false;
	private ArrayList<String[]> savedReadings;
	public boolean loaded = false;
	private boolean helping;
	public static boolean cardfortheday = false;



	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setFullscreen();
		setContentView(R.layout.main);		
		
		querantPrefs = getSharedPreferences("tarotbot", 0);
		readingPrefs = getSharedPreferences("tarotbot.reading", 0);
		
		sharing = false;
		
		statusspin = (Spinner) findViewById(R.id.statusspinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.status_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		statusspin.setAdapter(adapter);
		statusspin.setOnItemSelectedListener(this);
		statusspin.setSelection(3);
		statusspin.setSelection(querantPrefs.getInt("querantstatus", 0));
		
		
		
		dp = (DatePicker)this.findViewById(R.id.birthdatepicker);

		reversalCheck = (CheckBox)this.findViewById(R.id.reversalcheck);
		
		Calendar today = Calendar.getInstance();
		 
		if (querantPrefs.contains("birthyear"))
			dp.init(querantPrefs.getInt("birthyear", today.get(Calendar.YEAR)), querantPrefs.getInt("birthmonth", today.get(Calendar.MONTH)), querantPrefs.getInt("birthday", today.get(Calendar.DAY_OF_MONTH)), this);        
		else
			dp.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH), this);

		//dp.init(1976, 9, 4, this);

		gestureDetector = new GestureDetector(new MyGestureDetector());
		gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (gestureDetector.onTouchEvent(event)) {
					return true;
				}
				return false;
			} 
		};
		changeQuerant();		
		inflater = LayoutInflater.from(this);
		if (canBeRestored()) {
			myInt = new BotaInt(new RiderWaiteDeck(), aq);
			restoreMe();
			restoreSecondStage();
		} else {
			secondSetIndex = 0;
			laidout.add((RelativeLayout) this.findViewById(R.id.mainlayout));
			myRandomCard = getRandomCard();
			((ImageView) this.findViewById(R.id.randomcard)).setBackgroundDrawable(getResources().getDrawable(myRandomCard));
			
	
			initbutton = (Button) this.findViewById(R.id.initbutton);
			initbutton.setOnClickListener(this);
			Toast.makeText(this, R.string.questionprompt, Toast.LENGTH_LONG).show(); 
		}
		
		
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	  super.onConfigurationChanged(newConfig);
	  if (!firstpass) {
		  oldFlipper = flipper;
		  redisplaySecondStage(secondSetIndex);
		  if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
				Toast.makeText(this, R.string.portraitnavigation, Toast.LENGTH_LONG).show();
			else
				Toast.makeText(this, R.string.landscapenavigation, Toast.LENGTH_LONG).show();
	  } else {
		  redisplayMain();
		  //setContentView(R.layout.main);
	  }
	  //setContentView(R.layout.myLayout);
	}
	
	private void restoreMe() {
		ArrayList<String> keys = new ArrayList<String>();
		ArrayList<Integer> positions = new ArrayList<Integer>();
		ArrayList<Boolean> reversals = new ArrayList<Boolean>();
		keys.addAll(readingPrefs.getAll().keySet());
		Collections.sort(keys);
		for (int i = 0; i < (keys.size()-78); i++)
			positions.add(readingPrefs.getInt(keys.get(i),-1));
		for (int i = positions.size(); i < keys.size(); i++)
			reversals.add(readingPrefs.getBoolean(keys.get(i),false));
		BotaInt.working = positions;
		BotaInt.myDeck.reversed = reversals.toArray(new Boolean[0]);
		secondSetIndex = querantPrefs.getInt("activeCard", 0);
	}

	/* Creates the menu items */
	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_LOAD, 0, R.string.load_menu).setIcon(android.R.drawable.ic_menu_set_as);
	    menu.add(0, MENU_SAVE, 1, R.string.save_menu).setIcon(android.R.drawable.ic_menu_save);
	    menu.add(0, MENU_SHARE, 2, R.string.share_menu).setIcon(android.R.drawable.ic_menu_share);
	    return true;
	}*/
	
	@Override

	public boolean onPrepareOptionsMenu(Menu menu) {

	menu.clear();
	
	menu.add(0, MENU_LOAD, 0, R.string.load_menu).setIcon(android.R.drawable.ic_menu_set_as);
    menu.add(0, MENU_SAVE, 1, R.string.save_menu).setIcon(android.R.drawable.ic_menu_save);
    menu.add(0, MENU_SHARE, 2, R.string.share_menu).setIcon(android.R.drawable.ic_menu_share);
    menu.add(0, MENU_HELP, 3, R.string.help_menu).setIcon(android.R.drawable.ic_menu_help);
    

	if(!begun) {
		menu.findItem(MENU_SAVE).setEnabled(false);
		if (!loaded)
			menu.findItem(MENU_SHARE).setEnabled(false);
	} else {
		menu.findItem(MENU_SAVE).setEnabled(true);
		menu.findItem(MENU_SHARE).setEnabled(true);
	}

	return super.onPrepareOptionsMenu(menu);

	}


	/* Handles item selections */
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case MENU_SAVE:
	        save(false);
	        return true;
	    case MENU_SHARE:
	    	save(true);
	        return true;
	    case MENU_LOAD:
	        displaySaved();
	        return true;
	    case MENU_HELP:
	        launchHelp();
	        return true;
	    }
	    return false;
	}
	
	private void launchHelp() {
		helping = true;
		String what = "What is this?";
		String how = "How does this work?";
		String tarot = "What is tarot?";
		String[] items = new String[]{what,how,tarot};				
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("What do you need to know?");
		builder.setItems(items, this);
		AlertDialog alert = builder.create();
		alert.show();		
	}

	public void share(String subject,String text) {
		sharing = true;
		 Intent intent = new Intent(Intent.ACTION_SEND);
		 intent.setType("text/plain");
		 intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		 intent.putExtra(Intent.EXTRA_TEXT, text);
		 startActivityForResult(Intent.createChooser(intent, getString (R.string.share_intent)),1);
		}

	private void displaySaved() {
		savedReadings = WebUtils.loadTarotBot(getApplicationContext());
		ArrayList<String> readingLabels = new ArrayList<String>();
		for (String[] reading: savedReadings) {
			
			if (reading[1].length() > 0)
				readingLabels.add(reading[1]+" - "+reading[2]);
			else
				readingLabels.add(reading[2]);
		}
		String[] items = readingLabels.toArray(new String[0]);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Select a reading to load");
		builder.setItems(items, this);
		AlertDialog alert = builder.create();
		alert.show();
	}

	private String reversalsToString() {
		String toReturn = "";
		
	    for (boolean reversal: BotaInt.myDeck.reversed) {
	    	String represent;
	    	if (reversal)
	    		represent = "R";
	    	else
		        represent = "U";
		      toReturn += represent+",";
	    }
	    return toReturn;
	}
	
	public String spreadToString(){
	    String toReturn = "";
	    for (int card: BotaInt.working) {
	      String represent = String.valueOf(card);
	      if (represent.length() < 2)
	    	  represent = "10"+represent;
	      else
	    	  represent = "1"+represent;
	      toReturn += represent+",";
	    }
	    return toReturn;
	  }
	
	public String deckToString(){
	    String toReturn = "";
	    for (int card: BotaInt.yod) {
	      String represent = String.valueOf(card);
	      if (represent.length() < 2)
	    	  represent = "10"+represent;
	      else
	    	  represent = "1"+represent;
	      toReturn += represent+",";
	    }
	    for (int card: BotaInt.heh1) {
		     String represent = String.valueOf(card);
		     if (represent.length() < 2)
		    	 represent = "10"+represent;
		     else
		    	 represent = "1"+represent;
		     toReturn += represent+",";
		}
	    for (int card: BotaInt.vau) {
		      String represent = String.valueOf(card);
		      if (represent.length() < 2)
		    	  represent = "10"+represent;
		      else
		    	  represent = "1"+represent;
		      toReturn += represent+",";
		    }
		for (int card: BotaInt.heh2) {
			String represent = String.valueOf(card);
			if (represent.length() < 2)
				 represent = "10"+represent;
			else
				 represent = "1"+represent;
			toReturn += represent+",";
		}
	    return toReturn;
	  }

	private void save(boolean share) {
		sharing = share;
		saveTitle = "";
		saveResult = "";
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle(getString(R.string.save_label));
		//alert.setMessage("Message");

		// Set an EditText view to get user input 
		input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton(getString(R.string.save_label_ok), this);
		alert.setNegativeButton(getString(R.string.save_label_no), this); 
			 
		alert.show(); 	
	}

	private void changeQuerant() {

					
			if (statusspin.getSelectedItem().toString().contains(getString(R.string.status_sm))) {
				male = true;
				partnered=false;
			} else if (statusspin.getSelectedItem().toString().contains(getString(R.string.status_pf))) {
				male=false;
				partnered = true;    	
			} else if (statusspin.getSelectedItem().toString().contains(getString(R.string.status_pm))) {
				male = true;
				partnered = true;
			} else {
				male = false;
				partnered = false;
			}
		
		//Toast.makeText(this, dp.getMonth(), Toast.LENGTH_SHORT).show();
		aq = new Querant(male,partnered,new GregorianCalendar(dp.getYear(),dp.getMonth(),dp.getDayOfMonth()));
		

		//mainlayout.setBackgroundDrawable(getSignificatorImage(aq));
		//if(firstpass)
			//showInfo();
		
	}
	private void showInfo(int type) {
		if (type == Configuration.ORIENTATION_PORTRAIT) {
			infoDisplayed = true;
			int i = BotaInt.circles.get(secondSetIndex);
			String interpretation = BotaInt.secondOperationInterpretation(i,getApplicationContext());
			showing = inflater.inflate(R.layout.interpretation, null);
			infotext = (TextView) showing.findViewById(R.id.interpretation);		
			infotext.setText("\n\n\n\n"+getString(R.string.position)+" " +secondSetIndex + "\n\n"+interpretation);			
			ArrayList<View> toShow = new ArrayList<View>();
			toShow.add(showing);
			View v = flipper.getCurrentView();
			((RelativeLayout)v.findViewById(R.id.secondsetlayout)).addView(showing);
		} else if (type == Configuration.ORIENTATION_LANDSCAPE) {
			int i = BotaInt.circles.get(secondSetIndex);
			String interpretation = BotaInt.secondOperationInterpretation(i,getApplicationContext());
			View v = flipper.getCurrentView();
			infotext = (TextView)v.findViewById(R.id.interpretation);		
			infotext.setText("\n"+getString(R.string.position)+" " + secondSetIndex + "\n"+interpretation);
			//((RelativeLayout)v.findViewById(R.id.secondsetlayout)).addView(infotext);
		}
		/*closure = (Button) showing.findViewById(R.id.closeinterpretation);
		closure.setClickable(true);
		closure.setOnClickListener(this);*/
				
	}

	protected void setFullscreen() { 
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN); 
	}  

	private void incrementSecondSet(int index) {		
		int previous = index;
		if (index >= (BotaInt.circles.size()-1)) 
			secondSetIndex=0; 
		else 
			secondSetIndex++;
		flipper.startFlipping();
		preFlip(flipper.getChildAt(secondSetIndex));
		if (flipper != null) {
			flipper.setInAnimation(inFromRightAnimation());
	        flipper.setOutAnimation(outToLeftAnimation());	        
	        flipper.showNext();
		}
		
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
			showInfo(getResources().getConfiguration().orientation);
			
		
		postFlip(flipper.getChildAt(previous));
		flipper.stopFlipping();
	}


	private void decrementSecondSet(int index) {		
		int previous = index;
		if (index-1 < 0) {
			secondSetIndex = (BotaInt.circles.size()-1);
		}  else
			secondSetIndex--;
		flipper.startFlipping();
		preFlip(flipper.getChildAt(secondSetIndex));
		if (flipper != null) {
			flipper.setInAnimation(inFromLeftAnimation());
	        flipper.setOutAnimation(outToRightAnimation());
	        flipper.showPrevious();
		}
		
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
			showInfo(getResources().getConfiguration().orientation);
		
		postFlip(flipper.getChildAt(previous));
		flipper.stopFlipping();
	}
	private void beginSecondStage() {
		firstpass=false;
		if (!reversalCheck.isChecked() &! loaded) {
			BotaInt.myDeck.reversed = BotaInt.myDeck.noreversal;
		} else {
			BotaInt.myDeck.establishReversal();
		}
		if (!loaded) {
			BotaInt.firstOperation();
		}
		BotaInt.secondOperation(getApplicationContext());
		
		displaySecondStage(secondSetIndex);		
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
			Toast.makeText(this, R.string.portraitnavigation, Toast.LENGTH_LONG).show();
		else
			Toast.makeText(this, R.string.landscapenavigation, Toast.LENGTH_LONG).show();
	}
	
	private void restoreSecondStage() {
		BotaInt.secondOperation(getApplicationContext());
		Toast.makeText(this, String.valueOf(secondSetIndex), 60).show();
		displaySecondStage(secondSetIndex);
	}

	private void displaySecondStage(int indexin) {
		setContentView(R.layout.transition);
		flipper = (ViewFlipper) this.findViewById(R.id.flipper);
		
		int counter = 1;  
		for (int index:BotaInt.circles) {			
			View activeView = inflater.inflate(R.layout.reading, null);
			if (activeView.findViewById(R.id.secondsetlayout) != null)
				laidout.add((RelativeLayout) activeView.findViewById(R.id.secondsetlayout));
			//else continue;
			counter++;
			ImageView divine = (ImageView) activeView.findViewById(R.id.divine);
			divine.setClickable(true);
			divine.setOnTouchListener(gestureListener);
			//divine.setOnClickListener(this);									
			
			flipper.addView(activeView);
			type.add(0);
			flipdex.add(index);
		}
		
		flipper.setClickable(true);
		//flipper.setOnClickListener(this);
		flipper.setOnTouchListener(gestureListener);
		View v = flipper.getChildAt(indexin);
			
		
		
		ImageView divine = (ImageView) v.findViewById(R.id.divine);
		
		if (BotaInt.myDeck.reversed[flipdex.get(flipper.indexOfChild(v))]) {			
			Bitmap bmp = BitmapFactory.decodeResource(getResources(), BotaInt.getCard(flipdex.get(flipper.indexOfChild(v))));
			int w = bmp.getWidth();
			int h = bmp.getHeight();
			Matrix mtx = new Matrix();
			mtx.postRotate(180);
			Bitmap rotatedBMP = Bitmap.createBitmap(bmp, 0, 0, w, h, mtx, true);
			BitmapDrawable bmd = new BitmapDrawable(rotatedBMP);			
			divine.setBackgroundDrawable(bmd);
		} else
			divine.setBackgroundDrawable(getResources().getDrawable(BotaInt.getCard(flipdex.get(flipper.indexOfChild(v)))));
		
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
			showInfo(getResources().getConfiguration().orientation);
		
		flipper.setAnimationCacheEnabled(false);
		flipper.setPersistentDrawingCache(ViewGroup.PERSISTENT_NO_CACHE);
		flipper.setDrawingCacheEnabled(false);
		flipper.setDisplayedChild(0);
		
		
	}

	private void redisplayMain() {
		View activeView = inflater.inflate(R.layout.main, null);
		setContentView(activeView);
		
		statusspin = (Spinner) findViewById(R.id.statusspinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.status_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		statusspin.setAdapter(adapter);
		statusspin.setOnItemSelectedListener(this);
		statusspin.setSelection(3);
		statusspin.setSelection(querantPrefs.getInt("querantstatus", 0));

		dp = (DatePicker)this.findViewById(R.id.birthdatepicker);

		Calendar today = Calendar.getInstance();
		 
		if (querantPrefs.contains("birthyear"))
			dp.init(querantPrefs.getInt("birthyear", today.get(Calendar.YEAR)), querantPrefs.getInt("birthmonth", today.get(Calendar.MONTH)), querantPrefs.getInt("birthday", today.get(Calendar.DAY_OF_MONTH)), this);        
		else
			dp.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH), this);
		
		inflater = LayoutInflater.from(this);
		
			secondSetIndex = 0;
			laidout.add((RelativeLayout) this.findViewById(R.id.mainlayout));			
			((ImageView) this.findViewById(R.id.randomcard)).setBackgroundDrawable(getResources().getDrawable(myRandomCard));
			
	
			initbutton = (Button) this.findViewById(R.id.initbutton);
			initbutton.setOnClickListener(this);
		
	}
	
	private void redisplaySecondStage(int indexin) {
		setContentView(R.layout.transition);
		flipper = (ViewFlipper) this.findViewById(R.id.flipper);
		
		int counter = 1;  
		for (int index:BotaInt.circles) {			
			View activeView = inflater.inflate(R.layout.reading, null);
			if (activeView.findViewById(R.id.secondsetlayout) != null)
				laidout.add((RelativeLayout) activeView.findViewById(R.id.secondsetlayout));
			//else continue;
			counter++;
			ImageView divine = (ImageView) activeView.findViewById(R.id.divine);
			divine.setClickable(true);
			divine.setOnTouchListener(gestureListener);
			//divine.setOnClickListener(this);									

			flipper.addView(activeView);
			type.add(0);
			flipdex.add(index);
		}
		
		flipper.setClickable(true);
		//flipper.setOnClickListener(this);
		flipper.setOnTouchListener(gestureListener);
		View v = flipper.getChildAt(indexin);
		//View oldV = oldFlipper.getChildAt(indexin);		
		ImageView divine = (ImageView) v.findViewById(R.id.divine);
		if (BotaInt.myDeck.reversed[flipdex.get(flipper.indexOfChild(v))]) {			
			Bitmap bmp = BitmapFactory.decodeResource(getResources(), BotaInt.getCard(flipdex.get(flipper.indexOfChild(v))));
			int w = bmp.getWidth();
			int h = bmp.getHeight();
			Matrix mtx = new Matrix();
			mtx.postRotate(180);
			Bitmap rotatedBMP = Bitmap.createBitmap(bmp, 0, 0, w, h, mtx, true);
			BitmapDrawable bmd = new BitmapDrawable(rotatedBMP);			
			divine.setBackgroundDrawable(bmd);
		} else
			divine.setBackgroundDrawable(getResources().getDrawable(BotaInt.getCard(flipdex.get(flipper.indexOfChild(v)))));
		
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
			showInfo(getResources().getConfiguration().orientation);
		
		flipper.setAnimationCacheEnabled(false);
		flipper.setPersistentDrawingCache(ViewGroup.PERSISTENT_NO_CACHE);
		flipper.setDrawingCacheEnabled(false);
		flipper.setDisplayedChild(indexin);
		
		
	}

	private int getRandomCard() {
		return BotaInt.getCard((int)(Math.random() * 78));
	}


	public void onClick(View v) {	
		if (v.equals(this.findViewById(R.id.initbutton))) {
			begun = true;
			myInt = new BotaInt(new RiderWaiteDeck(), aq);
			beginSecondStage();//beginReading();	
		} else
			incrementSecondSet(secondSetIndex);
	}
	protected void redisplay() {
		infoDisplayed = false;
		View v = flipper.getCurrentView();
		View toRemove = v.findViewById(R.id.readinglayout);
		if (toRemove != null) {
			RelativeLayout lay = (RelativeLayout)v.findViewById(R.id.secondsetlayout);
			lay.removeView(toRemove);
		}
		
	}

	private Animation inFromRightAnimation() {	
		Animation inFromRight = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT,  +1.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
				Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
		);
		inFromRight.setDuration(250);
		inFromRight.setInterpolator(new AccelerateInterpolator());
		return inFromRight;
	}
	private Animation outToLeftAnimation() {
		Animation outtoLeft = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,  -1.0f,
				Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
		);
		outtoLeft.setDuration(250);
		outtoLeft.setInterpolator(new AccelerateInterpolator());
		return outtoLeft;
	}

	private Animation inFromLeftAnimation() {
		Animation inFromLeft = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT,  -1.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
				Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
		);
		inFromLeft.setDuration(250);
		inFromLeft.setInterpolator(new AccelerateInterpolator());
		return inFromLeft;
	}
	private Animation outToRightAnimation() {
		Animation outtoRight = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,  +1.0f,
				Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
		);
		outtoRight.setDuration(250);
		outtoRight.setInterpolator(new AccelerateInterpolator());
		return outtoRight;
	}

	public void onClick(DialogInterface dialog, int which) {
		
		if (dialog instanceof AlertDialog) {
				switch (which) {
				case -1: { 					
					saveTitle = input.getText().toString();
					saveTitle = saveTitle.replaceAll("\\s+", "+");					
					saveResult = WebUtils.saveTarotBot(spreadToString(),deckToString(),reversalsToString(),saveTitle,getApplicationContext());
					
					break; 
				}
				case -2: { 
					saveResult = WebUtils.saveTarotBot(spreadToString(),deckToString(),reversalsToString(),saveTitle,getApplicationContext());
					break; 
				}
				default: {
					if (helping) {
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setData(Uri.parse(helpUrls[which]));
						startActivityForResult(intent,1);
						break;
					} else {
						ArrayList<String[]> deck = WebUtils.loadTarotBotReading(getApplicationContext(),Integer.valueOf(savedReadings.get(which)[0]));
				    	ArrayList<Boolean> reversals = new ArrayList<Boolean>(); 
				    	ArrayList<Integer> working = new ArrayList<Integer>();
				    	int significator = 0;
				    	for(String[] card: deck) {
				    		if (card[2] == "1")
				    			reversals.add(true);
				    		else
				    			reversals.add(false);
				    		if (card[3].equals("1"))
				    			working.add(Integer.valueOf(card[0])-100);
				    		
				    		significator = Integer.valueOf(card[4]);
				    	}
				    	loaded=true;
				    	BotaInt.myDeck = new RiderWaiteDeck(reversals.toArray(new Boolean[0]));
				    	BotaInt.myQuerant = new Querant(significator);
				    	BotaInt.working = working;
				    	BotaInt.loaded = true;
				    	//new BotaInt(new RiderWaiteDeck(reversals.toArray(new Boolean[0])),new Querant(significator),working);
				    	beginSecondStage();
				    	break;
					}
				}
			}
				//Toast.makeText(this, saveResult, Toast.LENGTH_LONG);
				if (sharing) {
					String url = WebUtils.bitly(saveResult);
					share(getString(R.string.share_subject),WebUtils.bitly(saveResult));
				}
		} else {
			switch (which) {
				case -1: beginSecondStage(); break;
				case -2: Toast.makeText(this, "The cards are not ready to answer your question wait at least two hours before trying again.", Toast.LENGTH_LONG).show(); break;
			}		
		}
	}
	

	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		
		statusselected = statusspin.getSelectedItemPosition();
		changeQuerant();

	}

	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	public void onDateChanged(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		changeQuerant();

	}
	

	protected void onStop() {
		super.onStop();
		if (!sharing) {
		if (flipper != null) {
			for (int i = 0; i < flipper.getChildCount(); i++) {
				View v = flipper.getChildAt(i);
				v.destroyDrawingCache();
				v = null;
			}
			flipper.removeAllViews();
		}
		//BotaInt.nullify();
		laidout = null;
		inflater=null;
		gestureDetector = null;
		gestureListener = null;
		flipper = null;
		myInt = null;
		aq=null;
		System.gc();
		finish();
		}
	}

	protected void onPause() {
		super.onPause();
		
		SharedPreferences.Editor ed = querantPrefs.edit();
		ed.putInt("birthyear", aq.birth.get(Calendar.YEAR));
		ed.putInt("birthmonth", aq.birth.get(Calendar.MONTH));
		ed.putInt("birthday", aq.birth.get(Calendar.DAY_OF_MONTH));
		ed.putInt("querantstatus", statusselected);
		ed.putInt("activeCard", secondSetIndex);		
		ed.commit();
		/*if (flipper != null) {
			SharedPreferences.Editor read = readingPrefs.edit();
			read.clear();
			for(Integer card: BotaInt.working) 
				read.putInt("position"+String.valueOf(BotaInt.working.indexOf(card)), card);	
			
			for(int i = 0; i < BotaInt.myDeck.reversed.length; i++) 
				read.putBoolean("reversal"+String.valueOf(i), BotaInt.isReversed(i));
			
			read.commit();
			flipper.stopFlipping();
		}*/
		
		
				
	}

	
	protected void onResume() {
		super.onResume();	
		
	}
	
	class MyGestureDetector extends SimpleOnGestureListener implements OnGestureListener {

		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			//try {
			if (Math.abs(e1.getY() - e2.getY()) < SWIPE_MAX_OFF_PATH) {
				// right to left swipe
				if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					/*if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE && laidout.get(secondSetIndex+1).findViewById(R.id.interpretation) != null)						
						redisplay();*/
					incrementSecondSet(secondSetIndex);
					return true;
					// left to right swipe
				}  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					/*if (laidout.get(secondSetIndex+1).findViewById(R.id.interpretation) != null)						
						redisplay();*/
					decrementSecondSet(secondSetIndex);
					return true;
				}
			}

			if (Math.abs(e1.getX() - e2.getX()) < SWIPE_MAX_OFF_PATH) {
				if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
					if (!infoDisplayed)
						showInfo(getResources().getConfiguration().orientation);
					else
						redisplay();		
				}
				
				return true;		         
			}
			//} catch (Exception e) {
			// System.out.println(e.getMessage());
			//}
			return false;
		}

	}

	
	public void preFlip(View v) {
		
				/*ImageView lefty = (ImageView)v.findViewById(R.id.left);
				ImageView ls = (ImageView)v.findViewById(R.id.leftshadow);
				{
					
					Bitmap bmp = BitmapFactory.decodeResource(getResources(), BotaInt.getCard(BotaInt.getCardToTheLeft(flipdex.get(flipper.indexOfChild(v)))));
					int w = bmp.getWidth();
					int h = bmp.getHeight();
					Matrix mtx = new Matrix();
					mtx.postScale(.3f, .3f);
		
					if (BotaInt.myDeck.reversed[BotaInt.getCardToTheLeft(flipdex.get(flipper.indexOfChild(v)))]) 			
						mtx.postRotate(180);
		
					Bitmap scaledBMP = Bitmap.createBitmap(bmp, 0, 0, w, h, mtx, true);
					BitmapDrawable bmd = new BitmapDrawable(scaledBMP);
					lefty.setBackgroundDrawable(bmd);
					//lefty.setClickable(true);
					//lefty.setOnClickListener(this);
					ls.setBackgroundColor(Color.BLACK);
					ls.setMinimumHeight(bmd.getBitmap().getHeight());
					ls.setMinimumWidth(bmd.getBitmap().getWidth());
					ls.setAlpha(100);
				}
		
				ImageView righty = (ImageView)v.findViewById(R.id.right);
				ImageView rs = (ImageView)v.findViewById(R.id.rightshadow);
				{
					
					Bitmap bmp = BitmapFactory.decodeResource(getResources(), BotaInt.getCard(BotaInt.getCardToTheRight(flipdex.get(flipper.indexOfChild(v)))));
					int w = bmp.getWidth();
					int h = bmp.getHeight();
					Matrix mtx = new Matrix();
					mtx.postScale(.3f, .3f);
					if (BotaInt.myDeck.reversed[BotaInt.getCardToTheRight(flipdex.get(flipper.indexOfChild(v)))]) 			
						mtx.postRotate(180);
		
					Bitmap scaledBMP = Bitmap.createBitmap(bmp, 0, 0, w, h, mtx, true);
					BitmapDrawable bmd = new BitmapDrawable(scaledBMP);	
					righty.setBackgroundDrawable(bmd);
					//righty.setClickable(true);
					//righty.setOnClickListener(this);
					rs.setBackgroundColor(Color.BLACK);
					rs.setMinimumHeight(bmd.getBitmap().getHeight());
					rs.setMinimumWidth(bmd.getBitmap().getWidth());
					rs.setAlpha(100);
				}*/
				ImageView divine = (ImageView) v.findViewById(R.id.divine);
				if (BotaInt.myDeck.reversed[flipdex.get(flipper.indexOfChild(v))]) {			
					
					Bitmap bmp = BitmapFactory.decodeResource(getResources(), BotaInt.getCard(flipdex.get(flipper.indexOfChild(v))));
					int w = bmp.getWidth();
					int h = bmp.getHeight();
					Matrix mtx = new Matrix();
					mtx.postRotate(180);
					Bitmap rotatedBMP = Bitmap.createBitmap(bmp, 0, 0, w, h, mtx, true);
					BitmapDrawable bmd = new BitmapDrawable(rotatedBMP);			
					divine.setBackgroundDrawable(bmd);
				} else {
					
					divine.setBackgroundDrawable(getResources().getDrawable(BotaInt.getCard(flipdex.get(flipper.indexOfChild(v)))));
				}			
				
	}

		
	public void postFlip(View v) {

		if (type.get(flipper.indexOfChild(v)) == 0) {
			v = inflater.inflate(R.layout.reading, null);
		} 
	}
	
	@Override
	public Object onRetainNonConfigurationInstance() {
		return 1;
	}
	
	private boolean canBeRestored() {
		if (getLastNonConfigurationInstance()!=null) {
			return true;
		}
		return false;
	}
}