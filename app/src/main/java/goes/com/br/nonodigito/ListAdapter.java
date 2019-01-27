package goes.com.br.nonodigito;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by matheusgoes on 11/07/15.
 */
public class ListAdapter extends ArrayAdapter<Contato> {
    TextView name;
    TextView type;
    TextView oldNumber;
    TextView newNumber;

    private List<Contato> values;
    private Context context;

    public ListAdapter(Context context, List<Contato> objects) {
        super(context, R.layout.adapter, objects);
        this.values = objects;
        this.context = context;
    }

    public List<Contato> getValues() {
        return values;
    }

    public void setValues(List<Contato> values) {
        this.values = values;
    }

    @Override
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.adapter, parent, false);
        name = (TextView) convertView.findViewById(R.id.nome_lista);
        type = (TextView) convertView.findViewById(R.id.type);
        oldNumber = (TextView) convertView.findViewById(R.id.oldNumber);
        newNumber = (TextView) convertView.findViewById(R.id.newNumber);
        Contato item = getItem(position);
        if (item!= null) {
            name.setText(String.valueOf(item.getName()));
            switch (item.getType()){
                case 1:
                    type.setText("Tipo: Residencial");
                    break;
                case 2:
                    type.setText("Tipo: Celular");
                    break;
                case 3:
                    type.setText("Tipo: Trabalho");
                    break;
            }
            oldNumber.setText("Antigo número: " + String.valueOf(item.getOldNumber()));
            newNumber.setText("Novo número: " + String.valueOf(item.getNewNumber()));
        }
        return convertView;

    }
}
