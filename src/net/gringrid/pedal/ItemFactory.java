package net.gringrid.pedal;

import android.content.Context;
import android.view.View;

public abstract class ItemFactory {
	public final View create(String itemName, Context context){
		View view = createView(itemName);
		return view;
	}

	public abstract View createView(String itemName);
}
