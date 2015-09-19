package net.gringrid.pedal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.gringrid.pedal.db.vo.DisplayVO;
import android.content.Context;
import android.view.View;

public abstract class ItemFactory {

	private static Map<String, ItemFactory> factorys = 
		Collections.unmodifiableMap(new HashMap<String, ItemFactory>(){{
		{
			put("TextView", new ItemTextView());
			put("Chronometer", new ItemChronometerView());
		}
	}});

	public static View createView(Context context, DisplayVO vo){
		ItemFactory factory = factorys.get(vo.viewType);
		return factory.create(context, vo);
	}

	public abstract View create(Context context, DisplayVO vo);
	
}
