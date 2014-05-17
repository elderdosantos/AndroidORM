package br.com.sintesis.velotrack.onboard.models;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import br.com.sintesis.velotrack.onboard.database.DbHelper;
import br.com.sintesis.velotrack.onboard.exceptions.ActiveRecordException;
import br.com.sintesis.velotrack.onboard.orm.Column;
import br.com.sintesis.velotrack.onboard.orm.PrimaryKey;
import br.com.sintesis.velotrack.onboard.orm.Table;

public class Model {
	
	private SQLiteDatabase database;
	private DbHelper dbHelper;
	public Field[] fields;
	protected Context context;
	
	public Model() {}
	
	public Model(Context context) {
		dbHelper = new DbHelper(context);
		this.getFields();
		this.context = context;
	}
	
	private void open() {
		database = dbHelper.getWritableDatabase();
	}

	private void close() {
		dbHelper.close();
	}
	
	public Model findByPk(int pk) throws ActiveRecordException, IllegalAccessException, IllegalArgumentException {
		this.open();
		String column = this.getPrimaryKeyColumnName();
		String sql = "SELECT * FROM " + this.getTableName() + " WHERE " + column + " = " + pk;
			
		Cursor cursor = database.rawQuery(sql, null);
		
		if (cursor.moveToFirst()) {
			this.setAttributes(cursor);
		}
		
		this.close();
		
		return this;
	}
	
	public ArrayList<HashMap<String, String>> findAll() throws ActiveRecordException {
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String,String>>();
		
		this.open();
			
		String sql = "SELECT * FROM " + this.getTableName();
			
		Cursor cursor = database.rawQuery(sql, null);
			
		if (cursor.moveToFirst()) {
			list.add(this.getMap(cursor));
		}
			
		while (cursor.moveToNext()) {
			list.add(this.getMap(cursor));
		}
		
		this.close();

		return list;
	}
	
	public ArrayList<HashMap<String, String>> findAll(String order) throws ActiveRecordException {
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String,String>>();
		
		this.open();
			
		String sql = "SELECT * FROM " + this.getTableName() + " order by " + this.getPrimaryKeyColumnName() + " " + order;
			
		Cursor cursor = database.rawQuery(sql, null);
			
		if (cursor.moveToFirst()) {
			list.add(this.getMap(cursor));
		}
			
		while (cursor.moveToNext()) {
			list.add(this.getMap(cursor));
		}
		
		this.close();

		return list;
	}
	
	public boolean update() throws ActiveRecordException, IllegalAccessException, IllegalArgumentException {
		this.open();
		String sql = this.createStringUpdate();
		database.execSQL(sql);
		this.close();
		return true;
	}
	
	private String createStringUpdate() throws ActiveRecordException, IllegalAccessException, IllegalArgumentException {
		String update = "UPDATE ";
		
		update += this.getTableName();
		update += " SET ";
		
		HashMap<String, String> map = this.getAttributes();
		
		Object[] columns = (Object[]) map.keySet().toArray();
			
		for (int i = 0; i < columns.length; i++) {
			if (!columns[i].toString().equals(this.getPrimaryKeyColumnName())) {
				update += columns[i] + " = " + map.get(columns[i]) + ",";
			}
		}
		
		int size = update.length();
		update = update.substring(0, (size - 1));
		
		update += " WHERE " + this.getPrimaryKeyColumnName() + " = " + this.getPrimaryKeyValue();
		
		return update;
	}
	
	public boolean insert() throws IllegalAccessException, IllegalArgumentException, ActiveRecordException {
		this.open();
		String sql = this.createStringInsert();
		database.execSQL(sql);
		this.close();
		return true;
	}
	
	private String createStringInsert() throws ActiveRecordException, IllegalAccessException, IllegalArgumentException {
		String insert = "INSERT INTO ";
		
		insert += this.getTableName();
		
		HashMap<String, String> map = this.getAttributes();
		
		String fields = "(";
		String values = "VALUES (";
		
		Object[] columns = (Object[]) map.keySet().toArray();
			
		for (int i = 0; i < columns.length; i++) {
			if (!columns[i].toString().equals(this.getPrimaryKeyColumnName())) {
				fields += columns[i] + ",";
				values += map.get(columns[i]) + ","; 
			}
		}
		
		int fsize = fields.length();
		fields = fields.substring(0, (fsize - 1)) + ")";
		
		int vsize = values.length();
		values = values.substring(0, (vsize - 1)) + ")";
		
		insert += " " + fields + " " + values;
		
		return insert;
	}
	
	private void getFields() {
		this.fields = this.getClass().getFields();
	}
	
	private String getPrimaryKeyColumnName() throws ActiveRecordException {
		for (Field f : this.fields) {
			PrimaryKey pk = f.getAnnotation(PrimaryKey.class);
			if (pk != null) {
				return f.getName();
			}
		}
		
		throw new ActiveRecordException("Model sem @PrimaryKey.");
	}
	
	private int getPrimaryKeyValue() throws IllegalAccessException, IllegalArgumentException, ActiveRecordException {
		for (Field f : this.fields) {
			PrimaryKey pk = f.getAnnotation(PrimaryKey.class);
			
			if (pk != null) {
				return f.getInt(this);
			}
		}
		
		throw new ActiveRecordException("Model sem @PrimaryKey.");
	}
	
	private String getTableName() throws ActiveRecordException {
		Table table = (Table) this.getClass().getAnnotation(Table.class);
		
		if (table != null) {
			return table.name();
		}
		
		throw new ActiveRecordException("Model sem @Table.");
	}
	
	private void setAttributes(Cursor cursor) throws IllegalAccessException, IllegalArgumentException {
		String[] columns = cursor.getColumnNames();
			
		for (int i = 0; i < columns.length; i++) {
			String column = columns[i];
				
			for (Field f : this.fields) {
				Column ac = f.getAnnotation(Column.class);
					
				if (ac != null) {
					if (ac.name().equals(column)) {
						String type = f.getType().toString();
							
						if (type.equals("int")) {
							f.set(this, cursor.getInt(i));
						} else if (type.equals("class java.lang.String")){
							f.set(this, cursor.getString(i));
						}
					}
				}
			}
		}
	}
	
	private HashMap<String, String> getMap(Cursor cursor) {
		HashMap<String, String> map = new HashMap<String, String>();
		String[] columns = cursor.getColumnNames();
		
		for (int i = 0; i < columns.length; i++) {
			String column = columns[i];
			
			for (Field f : this.fields) {
				Column ac = f.getAnnotation(Column.class);
				
				if (ac != null && ac.name().equals(column)) {
					if (f.getType().toString().equals("int")) {
						map.put(column, Integer.toString(cursor.getInt(i)));
						continue;
					} else {
						map.put(column, cursor.getString(i));
						continue;
					}
				}
			}
		}
		
		return map;
	}
	
	private HashMap<String, String> getAttributes() throws IllegalAccessException, IllegalArgumentException {
		HashMap<String, String> map = new HashMap<String, String>();

		for (Field f : this.fields) {
			Column ac = f.getAnnotation(Column.class);
				
			if (ac != null) {
				if (f.get(this) != null) {
					if (f.getType().toString().equals("class java.lang.String")) {
						if (f.get(this).toString().equals("current_timestamp")) {
							map.put(ac.name(), f.get(this).toString());
						} else {
							map.put(ac.name(), "'" + f.get(this).toString() + "'");
						}
					} else {
						map.put(ac.name(), f.get(this).toString());
					}
				}
			}
		}

		return map;
	}
	
	protected String getString (int id) {
		return this.context.getResources().getString(id);
	}
	
	public void setContext(Context context) {
		this.context = context;
	}
	
	
}