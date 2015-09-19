package net.gringrid.pedal;

import net.gringrid.pedal.db.vo.DisplayVO;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class ItemTextView extends ItemFactory{

	@Override
	public View create(Context context, DisplayVO vo) {
		Log.d("jiho", "ItemTextView create");
		TextView tv = new TextView(context);	
		tv.setText(vo.itemName);
		tv.setTag(vo);
		tv.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.item_unselected));
		tv.setLayoutParams(vo.params);
		return tv;
	}

}
