package io.github.elderdosantos.orm;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import io.github.elderdosantos.orm.annotations.Column;
import io.github.elderdosantos.orm.annotations.PrimaryKey;
import io.github.elderdosantos.orm.annotations.Table;
import io.github.elderdosantos.orm.exceptions.ActiveRecordException;
import io.github.elderdosantos.orm.sqlite.DbHelper;

public class Model {

    private DbHelper dbHelper;
    public Field[] fields;
    protected Context context;
    private SQLiteDatabase database;

    public Model(Context context) {
        dbHelper = new DbHelper(context);
        this.getFields();
        this.context = context;
    }

    private void getFields() {
        this.fields = this.getClass().getFields();
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

    private String getPrimaryKeyColumnName() throws ActiveRecordException {
        for (Field f : this.fields) {
            PrimaryKey pk = f.getAnnotation(PrimaryKey.class);
            if (pk != null) {
                return f.getName();
            }
        }

        throw new ActiveRecordException("Model sem @PrimaryKey.");
    }

    private String getTableName() throws ActiveRecordException {
        Table table = (Table) this.getClass().getAnnotation(Table.class);

        if (table != null) {
            this.checkTableExists(table.name());

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

    private int getPrimaryKeyValue() throws IllegalAccessException, IllegalArgumentException, ActiveRecordException {
        for (Field f : this.fields) {
            PrimaryKey pk = f.getAnnotation(PrimaryKey.class);

            if (pk != null) {
                return f.getInt(this);
            }
        }

        throw new ActiveRecordException("Model sem @PrimaryKey.");
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

                if (map.get(columns[i]) == "false") {
                    values += "0,";
                } else if (map.get(columns[i]) == "true") {
                    values += "1,";
                } else {
                    values += map.get(columns[i]) + ",";
                }
            }
        }

        int fsize = fields.length();
        fields = fields.substring(0, (fsize - 1)) + ")";

        int vsize = values.length();
        values = values.substring(0, (vsize - 1)) + ")";

        insert += " " + fields + " " + values;

        return insert;
    }

    protected String getString (int id) {
        return this.context.getResources().getString(id);
    }

    public void setContext(Context context) {
        this.context = context;
    }

    private void checkTableExists(String table)
    {
        String sql_check = "SELECT name FROM sqlite_master WHERE type='table' AND name='"+table+"';";

        Cursor cursor = database.rawQuery(sql_check, null);

        if (!cursor.moveToFirst()) {
            createTable(table);
        }
    }

    private void createTable(String table)
    {
        String sql = "create table " + table + " (";

        boolean comma = false;

        for (Field field : fields) {
            Column column = field.getAnnotation(Column.class);

            if (column == null) {
                continue;
            }

            String sql_field = "";

            if (comma) {
                sql_field = ",";
            }

            if (!column.name().isEmpty()) {
                sql_field += column.name();
            } else {
                sql_field += field.getName();
            }

            PrimaryKey pk = field.getAnnotation(PrimaryKey.class);

            sql_field += " " + getSQLiteType(field);

            if (pk != null) {
                sql_field += " primary key";

                if (pk.autoincrement()) {
                    sql_field += " autoincrement";
                }
            }

            if (!column.allowNull()) {
                sql_field += " not null";
            }

            sql += sql_field;

            comma = true;
        }

        sql += ");";

        Log.d("AndroidORM", "Creating table using sql: " + sql);
        database.execSQL(sql);
    }

    private String getSQLiteType(Field field) {
        if (field.getType().toString().equals("int")) {
            return "integer";
        } else if (field.getType().toString().equals("java.lang.String")) {
            return "text";
        } else if (field.getType().toString().equals("boolean")) {
            return "int";
        } else {
            return "text";
        }
    }
}
