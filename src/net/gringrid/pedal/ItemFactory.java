package net.gringrid.pedal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.view.View;

public abstract class ItemFactory {

	private static final Map<String, ItemFactory> factoryMap = 
	    Collections.unmodifiableMap(new HashMap<String, ItemFactory>() {{
	        put("Meow", new ItemFactory() { public Animal create() { return new Cat(); }});
	        put("Woof", new ItemFactory() { public Animal create() { return new Dog(); }});
	    }});

	public final View create(String itemName, Context context){

		View view = createView(itemName);
		return view;
	}

	public abstract View createView(String itemName);
}
