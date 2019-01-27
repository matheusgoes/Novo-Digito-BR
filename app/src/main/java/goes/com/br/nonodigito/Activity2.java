package goes.com.br.nonodigito;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.OperationApplicationException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Activity2 extends MainActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);
        final ListView list_view = (ListView) findViewById(R.id.listView);
        Bundle bundle = getIntent().getExtras();
        final ArrayList<Contato> contatos = bundle.getParcelableArrayList("Lista");
        final ListAdapter adapter = new ListAdapter(getApplicationContext(), contatos);
        TextView total = (TextView) findViewById(R.id.total);
        if(contatos.isEmpty()){
            total.setText("Total de contatos alterados: 0");
        }else {
            total.setText("Total de contatos alterados: " + contatos.size());
        }
        list_view.setAdapter(adapter);
        list_view.setDivider(new ColorDrawable(Color.parseColor("#BBBBBB")));
        list_view.setDividerHeight(2);
        list_view.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(Activity2.this)
                        .setTitle("Deseja desfazer a alteração do contato " + contatos.get(position).getName() + "?")
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
                                        String selectPhone = ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "='" +
                                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "'" + " AND " + ContactsContract.CommonDataKinds.Phone.TYPE + "=?";
                                        String[] phoneArgs = new String[]{contatos.get(position).getId(), String.valueOf(contatos.get(position).getType())};
                                        ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                                                .withSelection(selectPhone, phoneArgs)
                                                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contatos.get(position).getOldNumber())
                                                .build());
                                        Log.i(contatos.get(position).getName(), "id "+contatos.get(position).getId());

                                        try {
                                            final ContentResolver cr = getContentResolver();
                                            cr.applyBatch(ContactsContract.AUTHORITY, ops);
                                            db_acesso.remove_contact(contatos.get(position).getId());
                                            contatos.remove(position);
                                            adapter.setValues(contatos);
                                            list_view.setAdapter(adapter);
                                            mNotificationManager.cancelAll();
                                            TextView total = (TextView) findViewById(R.id.total);
                                            total.setText("Total de contatos alterados: " + contatos.size());
                                        } catch (RemoteException e) {
                                            e.printStackTrace();
                                        } catch (OperationApplicationException e) {
                                            e.printStackTrace();
                                        }
                                        mNotificationManager.cancelAll();
                                    }
                                })
                        .setNegativeButton("Cancelar",null)
                        .show();
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
