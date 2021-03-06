package liberus.tarot.spread.gothic;

import java.util.Arrays;
import java.util.List;

import liberus.tarot.deck.Deck;
import liberus.tarot.interpretation.BotaInt;
import liberus.tarot.interpretation.Interpretation;
import liberus.tarot.os.activity.AbstractTarotBotActivity;
import liberus.tarot.spread.Spread;
import liberus.tarot.android.noads.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class GothicCelticSpread extends GothicSpread {
	
	
	private static int significatorIn;
	private int myNum;


	public GothicCelticSpread(Interpretation myInt, String[] labels) {
		super(myInt);
		myNum = labels.length;
		myLabels = labels;
	}
	
	@Override
	public void operate(Context context, boolean loading) {
		if (!loading) {
			Deck.cards = myDeck.shuffle(Deck.cards,3);			
			for (int i = 0; i < myNum; i++)
				Spread.working.add(Deck.cards[i]);
			Spread.circles = working;
		}		
	}
	
	@Override
	public int getLayout() {
		return R.layout.celticlayout;
	}

	public View populateSpread(View layout, AbstractTarotBotActivity act, Context ctx) {
		
		ImageView card = (ImageView) layout.findViewById(R.id.celtic_heart);
		placeImage(act.flipdex.get(0),card,ctx);
		card.setId(0);
		card.setOnClickListener(act);
		if (act.secondSetIndex == 0)
			layout.findViewById(R.id.celtic_heart_back).setBackgroundColor(Color.RED);
		
		card = (ImageView) layout.findViewById(R.id.celtic_opposition);
		placeLandscapeImage(act.flipdex.get(1),card,ctx);		
		card.setId(1);
		card.setOnClickListener(act);
		if (act.secondSetIndex == 1)
			layout.findViewById(R.id.celtic_opposition_back).setBackgroundColor(Color.RED);
		
		card = (ImageView) layout.findViewById(R.id.celtic_root);
		placeImage(act.flipdex.get(2),card,ctx);
		card.setId(2);
		card.setOnClickListener(act);
		if (act.secondSetIndex == 2)
			layout.findViewById(R.id.celtic_root_back).setBackgroundColor(Color.RED);
		
		card = (ImageView) layout.findViewById(R.id.celtic_past);
		placeImage(act.flipdex.get(3),card,ctx);
		card.setId(3);
		card.setOnClickListener(act);
		if (act.secondSetIndex == 3)
			layout.findViewById(R.id.celtic_past_back).setBackgroundColor(Color.RED);
		
		card = (ImageView) layout.findViewById(R.id.celtic_belief);
		placeImage(act.flipdex.get(4),card,ctx);
		card.setId(4);
		card.setOnClickListener(act);
		if (act.secondSetIndex == 4)
			layout.findViewById(R.id.celtic_belief_back).setBackgroundColor(Color.RED);
		
		card = (ImageView) layout.findViewById(R.id.celtic_future);
		placeImage(act.flipdex.get(5),card,ctx);	
		card.setId(5);
		card.setOnClickListener(act);
		if (act.secondSetIndex == 5)
			layout.findViewById(R.id.celtic_future_back).setBackgroundColor(Color.RED);
		
		card = (ImageView) layout.findViewById(R.id.celtic_you);
		placeImage(act.flipdex.get(6),card,ctx);	
		card.setId(6);
		card.setOnClickListener(act);
		if (act.secondSetIndex == 6)
			layout.findViewById(R.id.celtic_you_back).setBackgroundColor(Color.RED);
		
		card = (ImageView) layout.findViewById(R.id.celtic_environment);
		placeImage(act.flipdex.get(7),card,ctx);	
		card.setId(7);
		card.setOnClickListener(act);
		if (act.secondSetIndex == 7)
			layout.findViewById(R.id.celtic_environment_back).setBackgroundColor(Color.RED);
		
		card = (ImageView) layout.findViewById(R.id.celtic_guidance);
		placeImage(act.flipdex.get(8),card,ctx);
		card.setId(8);
		card.setOnClickListener(act);
		if (act.secondSetIndex == 8)
			layout.findViewById(R.id.celtic_guidance_back).setBackgroundColor(Color.RED);
		
		card = (ImageView) layout.findViewById(R.id.celtic_outcome);
		placeImage(act.flipdex.get(9),card,ctx);
		card.setId(9);
		card.setOnClickListener(act);
		if (act.secondSetIndex == 9)
			layout.findViewById(R.id.celtic_outcome_back).setBackgroundColor(Color.RED);
		
		return layout;
	}
}
