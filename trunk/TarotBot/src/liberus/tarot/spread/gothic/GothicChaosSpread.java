package liberus.tarot.spread.gothic;

import java.util.Arrays;
import java.util.List;

import liberus.tarot.android.R;
import liberus.tarot.deck.Deck;
import liberus.tarot.interpretation.BotaInt;
import liberus.tarot.interpretation.Interpretation;
import liberus.tarot.os.activity.AbstractTarotBotActivity;
import liberus.tarot.os.activity.TarotBotActivity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class GothicChaosSpread extends GothicSpread {
	
	
	private static int significatorIn;
	private int myNum;


	public GothicChaosSpread(Interpretation myInt, String[] labels) {
		super(myInt);
		myNum = labels.length;
		myLabels = labels;
	}
	
	@Override
	public void operate(Context context, boolean loading) {
		if (!loading) {
			Integer[] shuffled = myDeck.shuffle(new Integer[78],3);
			Deck.shuffled = shuffled;
			for (int i = 0; i < myNum; i++)
				GothicSpread.working.add(shuffled[i]);
			GothicSpread.circles = working;
		}		
	}

	@Override
	public int getLayout() {
		return R.layout.chaoslayout;
	}

	public View populateSpread(View layout, AbstractTarotBotActivity act, Context ctx) {
		
		ImageView card = (ImageView) layout.findViewById(R.id.chaos_red);
		placeImage(act.flipdex.get(0),card,ctx);
		card.setId(0);
		card.setOnClickListener(act);
		if (TarotBotActivity.secondSetIndex == 0)
			card.setColorFilter(0xFFFFFF00, PorterDuff.Mode.MULTIPLY);
		
		card = (ImageView) layout.findViewById(R.id.chaos_orange);
		placeImage(act.flipdex.get(1),card,ctx);
		card.setId(1);
		card.setOnClickListener(act);
		if (TarotBotActivity.secondSetIndex == 1)
			card.setColorFilter(0xFFFFFF00, PorterDuff.Mode.MULTIPLY);
		
		card = (ImageView) layout.findViewById(R.id.chaos_purple);
		placeImage(act.flipdex.get(2),card,ctx);	
		card.setId(2);
		card.setOnClickListener(act);
		if (TarotBotActivity.secondSetIndex == 2)
			card.setColorFilter(0xFFFFFF00, PorterDuff.Mode.MULTIPLY);
		
		card = (ImageView) layout.findViewById(R.id.chaos_yellow);
		placeImage(act.flipdex.get(3),card,ctx);
		card.setId(3);
		card.setOnClickListener(act);
		if (TarotBotActivity.secondSetIndex == 3)
			card.setColorFilter(0xFFFFFF00, PorterDuff.Mode.MULTIPLY);
		
		card = (ImageView) layout.findViewById(R.id.chaos_green);
		placeImage(act.flipdex.get(4),card,ctx);
		card.setId(4);
		card.setOnClickListener(act);
		if (TarotBotActivity.secondSetIndex == 4)
			card.setColorFilter(0xFFFFFF00, PorterDuff.Mode.MULTIPLY);
		
		card = (ImageView) layout.findViewById(R.id.chaos_blue);
		placeImage(act.flipdex.get(5),card,ctx);
		card.setId(5);
		card.setOnClickListener(act);
		if (TarotBotActivity.secondSetIndex == 5)
			card.setColorFilter(0xFFFFFF00, PorterDuff.Mode.MULTIPLY);
		
		card = (ImageView) layout.findViewById(R.id.chaos_black);
		placeImage(act.flipdex.get(6),card,ctx);
		card.setId(6);
		card.setOnClickListener(act);
		if (TarotBotActivity.secondSetIndex == 6)
			card.setColorFilter(0xFFFFFF00, PorterDuff.Mode.MULTIPLY);
		
		card = (ImageView) layout.findViewById(R.id.chaos_octarine);
		placeImage(act.flipdex.get(7),card,ctx);
		card.setId(7);
		card.setOnClickListener(act);
		if (TarotBotActivity.secondSetIndex == 7)
			card.setColorFilter(0xFFFFFF00, PorterDuff.Mode.MULTIPLY);
		
		return layout;
	}



}