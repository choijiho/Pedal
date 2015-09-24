package net.gringrid.pedal;

import net.gringrid.pedal.db.vo.DisplayVO;
import net.gringrid.pedal.view.ItemView;
import android.content.Context;
import android.util.Log;
import android.view.View;

public class ItemViewFactory extends ItemFactory{

	@Override
	public View create(Context context, DisplayVO vo) {
		Log.d("jiho", "ItemViewFactory create : "+vo.itemName);
		ItemView view = new ItemView(context, vo);
		
		return view;
	}

}