package io.github.elderdosantos.androidormlib;

import android.content.Context;

import java.util.Date;

import io.github.elderdosantos.orm.Model;
import io.github.elderdosantos.orm.annotations.Column;
import io.github.elderdosantos.orm.annotations.PrimaryKey;
import io.github.elderdosantos.orm.annotations.Table;

@Table(name = "mensagem")
public class Mensagem extends Model {

    @PrimaryKey(autoincrement = true)
    @Column(name = "id")
    public int id;
    @Column(name = "usuario")
    public String usuario;
    @Column(name = "mensagem")
    public String mensagem;
    @Column(name = "lido", allowNull = true)
    public boolean lido;

    public Mensagem (Context mContext) {
        super(mContext);
    }
}
