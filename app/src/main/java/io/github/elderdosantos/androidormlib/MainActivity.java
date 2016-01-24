package io.github.elderdosantos.androidormlib;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import io.github.elderdosantos.orm.exceptions.ActiveRecordException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        Mensagem m = new Mensagem(this);
        try {
            m.lido = true;
            m.mensagem = "oi";
            m.usuario = "elder";
            m.insert();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ActiveRecordException e) {
            e.printStackTrace();
        }
    }

}
