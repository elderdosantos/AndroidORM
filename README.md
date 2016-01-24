AndroidORM
==========

Simples ORM que desenvolvi para um cliente e agora estou disponibilizando para vocês.
A utilização dele é bem tranquila.
Vejam a classe Mensagem dentro do package models. Ela extende a classe Model e tem todas as anotações necessárias.
Com isso feito podemos utilizar a classe da seguinte forma:

// para inserção

Mensagem model = new Mensagem(); // instância do model

model.conteudo = "COnteudo da mensagem"; // atribuição dos valores

model.save(); // gera o sql 'insert into mensagem (conteudo) values ("COnteudo da mensagem"); e o executa no banco 

// para edição

Mensagem model = new Mensagem();

model.findByPk(1); // gera o sql 'select * from mensagem where id = 1' e retorna seu resultado


model.conteudo = 'Nova mensagem';

model.update(); // gera o sql 'update mensagem set conteudo = 'Nova mensagem' where id = 1' e o executa no banco

// para consultar tudo

Mensagem model = new Mensagem();

ArrayList<HashMap<String, String>> results = model.findAll(); // select * from mensagem



