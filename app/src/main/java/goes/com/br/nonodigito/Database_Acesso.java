package goes.com.br.nonodigito;

/**
 * Created by matheusgoes on 11/07/15.
 */


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;

public class Database_Acesso{
    private static SQLiteDatabase db;
    private static final String table1 = "contact";

    //construtor
    public Database_Acesso(Context context) {
        Database aux_db = new Database(context);
        //um objeto dessa classe é um banco de dados onde se é possível escrever
        db = aux_db.getWritableDatabase();
    }

    //função inserir
    /*
    A função recebe como parâmetro um objeto da classe Phone e em seguida, add cada um dos seus
    valores na devida coluna do banco de dados
     */
    public void inserir_contato (Contato contato) {
        ContentValues valores = new ContentValues();
        valores.put("id",contato.getId());
        valores.put("name",contato.getName());
        valores.put("oldNumber",contato.getOldNumber());
        valores.put("newNumber",contato.getNewNumber());
        valores.put("type", contato.getType());
        db.insert("contact", null, valores);
    }

    //função buscar
    /*
    o intuito dessa função é retornar todos os dados salvos no bd
    para isso, ela deverá retornar uma lista de objetos phone
     */
    public ArrayList<Contato> getContatos () {
        ArrayList<Contato> lista = new ArrayList<>();

        //nessa string iremos salvar os nomes das colunas da tabela
        String[] colunas = new String[]{"id", "name", "oldNumber", "newNumber", "type"};

        //a função query, que foi importada pelo SQLiteDatabeHelper retorna um cursor, o qual vai do primeiro ao ultimo
        //elemento encontrado na tabela segundo as caracteristicas da busca
        Cursor cursor = db.query("contact", colunas, null, null, null, null,"id ASC");
        if (cursor.getCount() > 0) {
            //agora iremos adaptar esse cursor a uma lista de objetos phone
            cursor.moveToFirst(); //move o cursor para a primeira linha retornada na consulta
            do {
                //cria um objeto da classe phone, apenas para auxiliar o processo
                Contato c = new Contato();
                //insere-se no objeto da classe phone os dados obtidos na consulta de acordo com a posição do tipo na String colunas
                c.setId(cursor.getString(0));
                c.setName(cursor.getString(1));
                c.setOldNumber(cursor.getString(2));
                c.setNewNumber(cursor.getString(3));
                c.setType(cursor.getInt(4));
                lista.add(c); //insere o objeto aux na lista

            } while (cursor.moveToNext()); //repete os passos anteriores enquanto o cursor puder ser movido a uma próxima posição
        }

        return lista;
    }

    //função clear
    //deleta todos os dados salvos no banco de dados.
    void clear(){
        db.delete("contact", null, null);
    }
    void remove_contact(String id){
       // db.delete("contact", "id=?", new String [] { id });
        String sql = "DELETE FROM contact WHERE id="+id;
        db.execSQL(sql);
    }

}
