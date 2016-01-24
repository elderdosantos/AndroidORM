package br.com.sintesis.velotrack.onboard.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Classe auxiliar de banco de dados
 * 
 * @author Elder dos Santos <elder@dr.com.br>
 *
 */
public class DbHelper extends SQLiteOpenHelper {
	/**
	 * Vers�o do banco de dados {para controle de atualiza��o}
	 */
	public static final int DATABASE_VERSION = 1;
	/**
	 * Nome do banco de dados
	 */
	public static final java.lang.String DATABASE_NAME = "Velotrack.db";
	
	private Context context;
	
	/**
	 * Construtor
	 * 
	 * @param context
	 * 
	 */
	public DbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	/**
	 * Evento de cria��o do banco {caso n�o esteja criado ainda} <br />
 	 * Colocar nesse m�todo todos os scripts de banco de dados.  <br />
	 * Os scripts de cria��o e tamb�m popular os registros b�sicos
	 * 
	 * @param database
	 * 
	 * @return void
	 */
	@Override
	public void onCreate(SQLiteDatabase database) {
		// scripts de cria��o 
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {}

}