package goes.com.br.nonodigito;

/**
 * Created by matheusgoes on 11/07/15.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Database extends SQLiteOpenHelper{
    // Versão do db
    private static final int versao_db = 1;

    // Nome do db
    private static final String nome_db = "9digito_db";

    // Nome da tabela
    private static final String table1 = "contact";

    // Nomes das colunas da tabela phone
    private static final String id = "id";
    private static final String name = "name";
    private static final String oldNumber = "oldNumber";
    private static final String newNumber = "newNumber";
    private static final String type = "type";



    //construtor do banco
    public Database(Context context) {
        super(context, nome_db, null, versao_db);
    }
    // Criando as tabelas
    //caso a versao seja a mesma da já criada o construtor executará essa função
    @Override
    public void onCreate(SQLiteDatabase db) {
        //tabela phone
        //na String abaixo o comando sql que responsável pela criação da tabela
        String criarTabela = "CREATE TABLE " + table1 + "("
                + id + " TEXT PRIMARY KEY," + name + " TEXT,"
                + oldNumber + " TEXT," + newNumber + " TEXT,"
                + type + " INTEGER)";

        //execução do comando SQL
        db.execSQL(criarTabela);
    }

    //caso a versão seja diferente da já criada o construtor executa essa função
    @Override
    public void onUpgrade(SQLiteDatabase db, int versao_ant, int versao_nv) {
        //mensagem de log para avisar sobre a mudança da versão
        Log.w(Database.class.getName(),
                "Atualizando o banco de dados da versão " + versao_ant + " para "
                        + versao_nv + ", isso apagará os dados antigos.");
        //comando para deletar a tabela
        db.execSQL("DROP TABLE IF EXISTS " + table1 + ";");
        //criar novamente a tabela
        onCreate(db);
    }

}
