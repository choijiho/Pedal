package net.gringrid.pedal.db;

import java.util.List;

public interface MasterDao<T> {

	public void createTable();
	public void dropTable();
	public boolean existsTable();
	public long insert(T object);
	public long[] insert(List<T> objects);
	public int delete(int id);
	public int update(T object);
	public int deleteAll();
	public T find(int id);
	public List<T> findAll();
	public int getCount();
	
}
