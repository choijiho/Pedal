package net.gringrid.pedal;

import net.gringrid.pedal.db.vo.DisplayVO;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class ItemTextView extends ItemFactory{

	@Override
	public View create(Context context, DisplayVO vo) {
		Log.d("jiho", "ItemTextView create");
		LinearLayout ll = new LinearLayout(context);
		ll.setOrientation(LinearLayout.VERTICAL);
		ll.setLayoutParams(vo.params);
		
		TextView title = new TextView(context);	
		title.setText(vo.itemName);
		title.setTag(vo);
		title.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.item_unselected));
		title.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		TextView content = new TextView(context);
		content.setText("CONTENT");
		content.setTag(vo);
		content.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.item_unselected));
		content.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1f));
		
		ll.addView(title);
		ll.addView(content);
		
		return ll;
	}

}
