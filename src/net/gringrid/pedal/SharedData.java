package net.gringrid.pedal;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedData {
	private SharedPreferences m_preference;
	private SharedPreferences.Editor m_editor;
	private static Context mContext;
	private static SharedData instance;

	SharedData(){
		if ( m_preference == null ){
			m_preference = mContext.getApplicationContext().getSharedPreferences("", Context.MODE_PRIVATE);
		}
		if ( m_editor == null ){
			m_editor = m_preference.edit();
		}
	}
	
	public static SharedData getInstance(Context context){
		mContext = context;
		if ( instance == null ){
			instance = new SharedData();
		}
		return instance;
	}
	
	public void setGlobalData(String _key, String _val)
	{
		m_editor.putString(_key, _val);
    }
	
	public void setGlobalData(String _key, int _val)
	{
		m_editor.putInt(_key, _val);
    }
    
    public void setGlobalData(String _key, long _val)
    {
    	m_editor.putLong(_key, _val);
    }
    
    public void setGlobalData(String _key, float _val)
    {
    	m_editor.putFloat(_key, _val);
    }

    public void setGlobalData(String _key, boolean _val)
    {
    	m_editor.putBoolean(_key, _val);
    }
    
    public void insertGlobalData(String _key, String _val)
	{
		m_editor.putString(_key, _val);
		m_editor.commit();
    }

    public void insertGlobalData(String _key, int _val)
	{
		m_editor.putInt(_key, _val);
		m_editor.commit();
    }
    
    public void insertGlobalData(String _key, long _val)
    {
    	m_editor.putLong(_key, _val);
		m_editor.commit();
    }
    
    public void insertGlobalData(String _key, float _val)
    {
    	m_editor.putFloat(_key, _val);
		m_editor.commit();
    }

    public void insertGlobalData(String _key, boolean _val)
    {
    	m_editor.putBoolean(_key, _val);
		m_editor.commit();
    }
    
    public String getGlobalDataString(String _key){
    	return m_preference.getString(_key, null);
    }

    public int getGlobalDataInt(String _key){
    	return m_preference.getInt(_key, 0);
    }
    
    public int getGlobalDataInt(String _key, int _default){
    	return m_preference.getInt(_key, _default);
    }
    
    public long getGlobalDataLong(String _key){
    	return m_preference.getLong(_key, 0);
    }

    public long getGlobalDataLong(String _key, long _default){
    	return m_preference.getLong(_key, _default);
    }

    public boolean getGlobalDataBoolean(String _key){
    	return m_preference.getBoolean(_key, false);
    }
    
    public void commit(){
    	m_editor.commit();
    }
}
