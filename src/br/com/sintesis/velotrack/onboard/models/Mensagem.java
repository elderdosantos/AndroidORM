package br.com.sintesis.velotrack.onboard.models;

import br.com.sintesis.velotrack.onboard.orm.Column;
import br.com.sintesis.velotrack.onboard.orm.PrimaryKey;
import br.com.sintesis.velotrack.onboard.orm.Table;
import android.content.Context;

@Table(name="mensagem")
public class Mensagem extends Model{
	@PrimaryKey
	@Column(name="id")
	public int id;
	@Column(name="dthr_criacao")
	public String dthr_criacao;
	@Column(name="conteudo")
	public String conteudo;
	@Column(name="indr_origem")
	public String indr_origem;
	@Column(name="indr_leitura")
	public String indr_leitura;
	@Column(name="dthr_leitura")
	public String dthr_leitura;
	@Column(name="command")
	public String command;

	public Mensagem(Context context) {
		super(context);
	}

}