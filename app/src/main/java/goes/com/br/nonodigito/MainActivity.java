package goes.com.br.nonodigito;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity {
    private InterstitialAd mInterstitialAd;
    ArrayList<Contato> contatos_db;
    static Database_Acesso db_acesso;

    static NotificationManager mNotificationManager;
    static Notification n;
    MyReceiver alarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        db_acesso = new Database_Acesso(getApplicationContext());

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //this.getApplicationContext().getContentResolver().registerContentObserver (ContactsContract.Contacts.CONTENT_URI, true, contentObserver);

        Button botao = (Button) findViewById(R.id.botao);
        final Button botao2 = (Button) findViewById(R.id.botao2);
        final Button botao3 = (Button) findViewById(R.id.botao3);
        botao2.setEnabled(false);
        botao3.setEnabled(false);

        alarm = new MyReceiver();
        alarm.setAlarm(this);

        final RelativeLayout rl2 = (RelativeLayout) findViewById(R.id.relativeLayout2);
        final ProgressBar progress = (ProgressBar) findViewById(R.id.progressBar);

        final ContentResolver cr = getContentResolver();

        if(db_acesso.getContatos().size() > 0){
            rl2.setAlpha(1);
            botao2.setEnabled(true);
            botao3.setEnabled(true);
        }

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-8231621972196832/1710662306");

        AdRequest adRequest2 = new AdRequest.Builder()
                .addTestDevice("ca-app-pub-8231621972196832/1710662306")
                .build();

        mInterstitialAd.loadAd(adRequest2);

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mInterstitialAd.show();
                requestNewInterstitial();
            }
        });

        /*AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);*/

        botao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
                final String ddd = ((EditText) findViewById(R.id.ddd_padrao)).getText().toString();
                final String ddi = "+" + ((EditText) findViewById(R.id.ddi_padrao)).getText().toString();
                if (ddd.isEmpty() || ddi.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Preencher DDI e DDD padrão", Toast.LENGTH_SHORT).show();
                } else {
                    new Thread(new Runnable() {
                        public void run() {
                            Contato contato = new Contato();
                            final Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
                                    null, null, null);
                            while (cursor.moveToNext()) {
                                String contactId =
                                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                                String nome =
                                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                                Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                                while (phones.moveToNext()) {
                                    String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                    int type = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                                    switch (type) {
                                        case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                                            String newNumber = "";

                                            char number_array[] = number.toCharArray();

                                            Log.i("Clicked!", contactId + " " + nome + " Old number -> " + number);

                                            newNumber = "";

                                            number_array = number.toCharArray();

                                            for (int i = 0; i < number_array.length; i++) {
                                                if (number_array[i] == '0' || number_array[i] == '1' || number_array[i] == '2' || number_array[i] == '3'
                                                        || number_array[i] == '4' || number_array[i] == '5' || number_array[i] == '6' || number_array[i] == '7'
                                                        || number_array[i] == '8' || number_array[i] == '9' || number_array[i] == '+') {
                                                    newNumber = newNumber + number_array[i];
                                                }
                                            }
                                            if (newNumber.length() >=8 ) {
                                                if (newNumber.charAt(newNumber.length() - 8) == '9' ||
                                                        newNumber.charAt(newNumber.length() - 8) == '8' ||
                                                        newNumber.charAt(newNumber.length() - 8) == '7' ||
                                                        newNumber.charAt(newNumber.length() - 8) == '6') {

                                                    if (((newNumber.length() == 8 || newNumber.length() == 10 || (newNumber.length() == 12 &&
                                                            ((newNumber.substring(1, 3).equals("55")) && (newNumber.charAt(0) == '+'))) || newNumber.length() == 14)
                                                            && (newNumber.charAt(0) != '0' && newNumber.charAt(0) != '+')) ||
                                                            ((newNumber.length() == 9 || newNumber.length() == 11 || (newNumber.length() == 13 && ((newNumber.substring(1, 3).equals("55")) && (newNumber.charAt(0) == '+'))) || newNumber.length() == 15)
                                                                    && ((newNumber.charAt(0) == '0' || newNumber.charAt(0) == '+')))) {

                                                        if (newNumber.length() - 8 > 0) {
                                                            newNumber = newNumber.substring(0, newNumber.length() - 8) + "9" + newNumber.substring(newNumber.length() - 8, newNumber.length());
                                                        } else {
                                                            newNumber = "9" + newNumber;
                                                        }

                                                        switch (newNumber.length()) {
                                                            case 9:
                                                                newNumber = ddi + " " + ddd + " " + newNumber;
                                                                newNumber = newNumber.substring(0, newNumber.length() - 4) + "-" + newNumber.substring(newNumber.length() - 4, newNumber.length());
                                                                break;
                                                            case 10:
                                                                if (newNumber.charAt(0) == '0') {
                                                                    newNumber = ddi + " " + ddd + " " + newNumber.substring(1, newNumber.length());
                                                                    newNumber = newNumber.substring(0, newNumber.length() - 4) + "-" + newNumber.substring(newNumber.length() - 4, newNumber.length());
                                                                }
                                                                break;
                                                            case 11:
                                                                newNumber = ddi + " " + newNumber.substring(0, newNumber.length() - 9) + " " + newNumber.substring(newNumber.length() - 9, newNumber.length());
                                                                newNumber = newNumber.substring(0, newNumber.length() - 4) + "-" + newNumber.substring(newNumber.length() - 4, newNumber.length());
                                                                break;
                                                            case 12:
                                                                if (!newNumber.substring(0, 4).equals("9090")) {
                                                                    if (newNumber.charAt(0) == '0') {
                                                                        newNumber = ddi + " " + newNumber.substring(1, newNumber.length() - 9) + " " + newNumber.substring(newNumber.length() - 9, newNumber.length());
                                                                        newNumber = newNumber.substring(0, newNumber.length() - 4) + "-" + newNumber.substring(newNumber.length() - 4, newNumber.length());
                                                                    }
                                                                }
                                                                break;
                                                            case 13:
                                                                if (!newNumber.substring(0, 4).equals("9090")) {
                                                                    newNumber = "+" + newNumber.substring(0, newNumber.length() - 11) + " "
                                                                            + newNumber.substring(newNumber.length() - 11, newNumber.length() - 9) + " " +
                                                                            newNumber.substring(newNumber.length() - 9, newNumber.length() - 4) +
                                                                            "-" + newNumber.substring(newNumber.length() - 4, newNumber.length());
                                                                }
                                                                break;
                                                            case 14:
                                                                if (!newNumber.substring(0, 4).equals("9090")) {
                                                                    if (newNumber.charAt(0) == '+') {
                                                                        newNumber = newNumber.substring(0, newNumber.length() - 11) + " "
                                                                                + newNumber.substring(newNumber.length() - 11, newNumber.length() - 9) + " " +
                                                                                newNumber.substring(newNumber.length() - 9, newNumber.length() - 4) +
                                                                                "-" + newNumber.substring(newNumber.length() - 4, newNumber.length());
                                                                    }
                                                                }
                                                                break;
                                                            default:
                                                                Log.i(nome, "erro no padrão");
                                                        }

                                                        Log.i("Clicked!", contactId + " " + nome + " new number -> " + newNumber);

                                                        contato.setId(contactId);
                                                        contato.setName(nome);
                                                        contato.setOldNumber(number);
                                                        contato.setNewNumber(newNumber);
                                                        contato.setType(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
                                                        db_acesso.inserir_contato(contato);

                                                        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
                                                        String selectPhone = ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "='" +
                                                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "'" + " AND " + ContactsContract.CommonDataKinds.Phone.TYPE + "=?";
                                                        String[] phoneArgs = new String[]{contactId, String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)};
                                                        ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                                                                .withSelection(selectPhone, phoneArgs)
                                                                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, newNumber)
                                                                .build());
                                                        try {
                                                            cr.applyBatch(ContactsContract.AUTHORITY, ops);
                                                            Log.i("Clicked!UPDATE CONTACT " + nome, "OK");
                                                        } catch (RemoteException e) {
                                                            e.printStackTrace();
                                                        } catch (OperationApplicationException e) {
                                                            e.printStackTrace();
                                                        }
                                                    } else {
                                                        Log.i("Clicked!", contactId + " " + nome + " new number -> " + newNumber);
                                                        Log.i("Clicked!UPDATE CONTACT " + nome, "UNNECESSARY");
                                                    }
                                                } else {
                                                    Log.i("CONTACT " + nome, "RESIDENTIAL");
                                                }
                                            }else{
                                                Log.i("CONTACT " + nome, "Service Number");
                                            }
                                                break;

                                                case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                                                    Log.i("Clicked!", contactId + " " + nome + " Old number -> " + number);
                                                    newNumber = "";
                                                    number_array = number.toCharArray();

                                                    for (int i = 0; i < number_array.length; i++) {
                                                        if (number_array[i] == '0' || number_array[i] == '1' || number_array[i] == '2' || number_array[i] == '3'
                                                                || number_array[i] == '4' || number_array[i] == '5' || number_array[i] == '6' || number_array[i] == '7'
                                                                || number_array[i] == '8' || number_array[i] == '9' || number_array[i] == '+') {
                                                            newNumber = newNumber + number_array[i];
                                                        }
                                                    }

                                                    if (newNumber.length() >=8 ) {
                                                        if (newNumber.charAt(newNumber.length() - 8) == '9' ||
                                                            newNumber.charAt(newNumber.length() - 8) == '8' ||
                                                            newNumber.charAt(newNumber.length() - 8) == '7' ||
                                                            newNumber.charAt(newNumber.length() - 8) == '6') {

                                                        if (((newNumber.length() == 8 || newNumber.length() == 10 || (newNumber.length() == 12 &&
                                                                ((newNumber.substring(1, 3).equals("55")) && (newNumber.charAt(0) == '+'))) || newNumber.length() == 14)
                                                                && (newNumber.charAt(0) != '0' && newNumber.charAt(0) != '+')) ||
                                                                ((newNumber.length() == 9 || newNumber.length() == 11 || (newNumber.length() == 13 && ((newNumber.substring(1, 3).equals("55")) && (newNumber.charAt(0) == '+'))) || newNumber.length() == 15)
                                                                        && ((newNumber.charAt(0) == '0' || newNumber.charAt(0) == '+')))) {

                                                            if (newNumber.length() - 8 > 0) {
                                                                newNumber = newNumber.substring(0, newNumber.length() - 8) + "9" + newNumber.substring(newNumber.length() - 8, newNumber.length());
                                                            } else {
                                                                newNumber = "9" + newNumber;
                                                            }

                                                            switch (newNumber.length()) {
                                                                case 9:
                                                                    newNumber = ddi + " " + ddd + " " + newNumber;
                                                                    newNumber = newNumber.substring(0, newNumber.length() - 4) + "-" + newNumber.substring(newNumber.length() - 4, newNumber.length());
                                                                    break;
                                                                case 10:
                                                                    if (newNumber.charAt(0) == '0') {
                                                                        newNumber = ddi + " " + ddd + " " + newNumber.substring(1, newNumber.length());
                                                                        newNumber = newNumber.substring(0, newNumber.length() - 4) + "-" + newNumber.substring(newNumber.length() - 4, newNumber.length());
                                                                    }
                                                                    break;
                                                                case 11:
                                                                    newNumber = ddi + " " + newNumber.substring(0, newNumber.length() - 9) + " " + newNumber.substring(newNumber.length() - 9, newNumber.length());
                                                                    newNumber = newNumber.substring(0, newNumber.length() - 4) + "-" + newNumber.substring(newNumber.length() - 4, newNumber.length());
                                                                    break;
                                                                case 12:
                                                                    if (!newNumber.substring(0, 4).equals("9090")) {

                                                                        if (newNumber.charAt(0) == '0') {
                                                                            newNumber = ddi + " " + newNumber.substring(1, newNumber.length() - 9) + " " + newNumber.substring(newNumber.length() - 9, newNumber.length());
                                                                            newNumber = newNumber.substring(0, newNumber.length() - 4) + "-" + newNumber.substring(newNumber.length() - 4, newNumber.length());
                                                                        }
                                                                    }
                                                                    break;
                                                                case 13:
                                                                    if (!newNumber.substring(0, 4).equals("9090")) {

                                                                        newNumber = "+" + newNumber.substring(0, newNumber.length() - 11) + " "
                                                                                + newNumber.substring(newNumber.length() - 11, newNumber.length() - 9) + " " +
                                                                                newNumber.substring(newNumber.length() - 9, newNumber.length() - 4) +
                                                                                "-" + newNumber.substring(newNumber.length() - 4, newNumber.length());
                                                                    }
                                                                    break;
                                                                case 14:
                                                                    if (!newNumber.substring(0, 4).equals("9090")) {

                                                                        if (newNumber.charAt(0) == '+') {
                                                                            newNumber = newNumber.substring(0, newNumber.length() - 11) + " "
                                                                                    + newNumber.substring(newNumber.length() - 11, newNumber.length() - 9) + " " +
                                                                                    newNumber.substring(newNumber.length() - 9, newNumber.length() - 4) +
                                                                                    "-" + newNumber.substring(newNumber.length() - 4, newNumber.length());
                                                                        }
                                                                    }
                                                                    break;
                                                                default:
                                                                    Log.i(nome, "erro no padrão");
                                                            }

                                                            Log.i("Clicked!", contactId + " " + nome + " new number -> " + newNumber);

                                                            contato.setId(contactId);
                                                            contato.setName(nome);
                                                            contato.setOldNumber(number);
                                                            contato.setNewNumber(newNumber);
                                                            contato.setType(ContactsContract.CommonDataKinds.Phone.TYPE_WORK);
                                                            db_acesso.inserir_contato(contato);

                                                            ArrayList<ContentProviderOperation> ops = new ArrayList<>();
                                                            String selectPhone = ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "='" +
                                                                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "'" + " AND " + ContactsContract.CommonDataKinds.Phone.TYPE + "=?";
                                                            String[] phoneArgs = new String[]{contactId, String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_WORK)};
                                                            ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                                                                    .withSelection(selectPhone, phoneArgs)
                                                                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, newNumber)
                                                                    .build());
                                                            try {
                                                                cr.applyBatch(ContactsContract.AUTHORITY, ops);
                                                                Log.i("Clicked!UPDATE CONTACT " + nome, "OK");
                                                            } catch (RemoteException e) {
                                                                e.printStackTrace();
                                                            } catch (OperationApplicationException e) {
                                                                e.printStackTrace();
                                                            }
                                                            mNotificationManager.cancelAll();
                                                        } else {
                                                            Log.i("Clicked!", contactId + " " + nome + " new number -> " + newNumber);
                                                            Log.i("Clicked!UPDATE CONTACT " + nome, "UNNECESSARY");
                                                        }
                                                    } else {
                                                        Log.i("CONTACT " + nome, "RESIDENTIAL");
                                                    }
                                            }else{
                                                Log.i("CONTACT " + nome, "Service Number");
                                            }

                                            break;
                                    }
                                }
                                phones.close();
                            }
                            cursor.close();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progress.setIndeterminate(false);
                                    progress.setProgress(100);
                                    rl2.setAlpha(1);
                                    botao2.setEnabled(true);
                                    botao3.setEnabled(true);
                                    Toast.makeText(getApplicationContext(), "Concluído.", Toast.LENGTH_SHORT).show();
                                    mNotificationManager.cancelAll();
                                }
                            });

                            mNotificationManager.cancelAll();

                        }
                    }).start();
                    progress.setIndeterminate(true);

                }
            }
        });


        botao2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Button 2", "Clicked");
                        contatos_db = db_acesso.getContatos();
                        Log.i("SIZE ", String.valueOf(contatos_db.size()));
                        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
                        for (int i = 0; i < contatos_db.size() ; i++) {
                            String selectPhone = ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "='" +
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "'" + " AND " + ContactsContract.CommonDataKinds.Phone.TYPE + "=?";
                            String[] phoneArgs = new String[]{contatos_db.get(i).getId(), String.valueOf(contatos_db.get(i).getType())};
                            ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                                    .withSelection(selectPhone, phoneArgs)
                                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contatos_db.get(i).getOldNumber())
                                    .build());
                                Log.i(contatos_db.get(i).getName(), "id "+contatos_db.get(i).getId());
                            try {
                                cr.applyBatch(ContactsContract.AUTHORITY, ops);
                                Log.i("UNDO", "OK");
                                mNotificationManager.cancelAll();
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            } catch (OperationApplicationException e) {
                                e.printStackTrace();
                            }
                        }
                db_acesso.clear();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Concluído.", Toast.LENGTH_SHORT).show();
                    }

                        });
                rl2.setAlpha(0);
                botao2.setEnabled(false);
                botao3.setEnabled(false);
                progress.setProgress(0);
                mNotificationManager.cancelAll();
            }
        });

        botao3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contatos_db = db_acesso.getContatos();
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("Lista", contatos_db);
                Intent i = new Intent(getApplicationContext(),Activity2.class);
                i.putExtras(bundle);
                startActivity(i);
            }
        });
    }

    public void onShareButtonClick(View view){
        String shareBody = "Recomendo o aplicativo 9º digito para adicionar rapidamente o 9º digito em seus contatos. O aplicativo realiza um filtro que identifica os contatos que precisam da inclusão do dígito (https://goo.gl/VUeFGZ)";
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Adicione o 9º digito em seus contatos");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Compartilhar em: "));
    }

    public void onAvaliationClick(View view){
        Intent i = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=goes.com.br.nonodigito"));
        startActivity(i);
    }

    public void onOtherAppClick(View view){
        Intent i = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=goes.com.br.minhacontadeluz"));
        startActivity(i);
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("ca-app-pub-8231621972196832/1710662306")
                .build();
        mInterstitialAd.loadAd(adRequest);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    private class MyContentObserver extends ContentObserver {

        public MyContentObserver() {
            super(null);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            Log.i("Content observer","Contact changed");
            Intent i = new Intent( getApplicationContext() , MainActivity.class);
            //Define acão da intent
            i.setAction("contacts_edited");
            PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), 0, i, 0);
            //Constroi notificação
            n  = new Notification.Builder(getApplicationContext())
                    .setContentTitle("Sua lista de contatos está diferente")
                    .setContentText("Clique aqui para verificar se o 9º digito deve ser adiciona em algum contato")
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setContentIntent(pIntent)
                    .setAutoCancel(true)
                    .addAction(R.drawable.abc_ic_clear_mtrl_alpha, "Cancelar", pIntent).build();
            //Exibe notificação
            mNotificationManager.notify(0, n);
        }

        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }

    }

}
/*

                        ADICIONAR CONTATO

                        String DisplayName = "XYZ";
                        String MobileNumber = "123456";
                        String HomeNumber = "1111";
                        String WorkNumber = "2222";
                        String emailID = "email@nomail.com";
                        String company = "bad";
                        String jobTitle = "abcd";

                        ArrayList < ContentProviderOperation > ops = new ArrayList < ContentProviderOperation > ();

                        ops.add(ContentProviderOperation.newInsert(
                                ContactsContract.RawContacts.CONTENT_URI)
                                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                                .build());

                        //------------------------------------------------------ Names
                        if (DisplayName != null) {
                            ops.add(ContentProviderOperation.newInsert(
                                    ContactsContract.Data.CONTENT_URI)
                                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                    .withValue(ContactsContract.Data.MIMETYPE,
                                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                                    .withValue(
                                            ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                                            DisplayName).build());
                        }

                        //------------------------------------------------------ Mobile Number
                        if (MobileNumber != null) {
                            ops.add(ContentProviderOperation.
                                    newInsert(ContactsContract.Data.CONTENT_URI)
                                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                    .withValue(ContactsContract.Data.MIMETYPE,
                                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, MobileNumber)
                                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                                    .build());
                        }

                        //------------------------------------------------------ Home Numbers
                        if (HomeNumber != null) {
                            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                    .withValue(ContactsContract.Data.MIMETYPE,
                                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, HomeNumber)
                                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                            ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                                    .build());
                        }

                        //------------------------------------------------------ Work Numbers
                        if (WorkNumber != null) {
                            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                    .withValue(ContactsContract.Data.MIMETYPE,
                                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, WorkNumber)
                                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                            ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
                                    .build());
                        }

                        //------------------------------------------------------ Email
                        if (emailID != null) {
                            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                    .withValue(ContactsContract.Data.MIMETYPE,
                                            ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                                    .withValue(ContactsContract.CommonDataKinds.Email.DATA, emailID)
                                    .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                                    .build());
                        }

                        //------------------------------------------------------ Organization
                        if (!company.equals("") && !jobTitle.equals("")) {
                            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                    .withValue(ContactsContract.Data.MIMETYPE,
                                            ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                                    .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, company)
                                    .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
                                    .withValue(ContactsContract.CommonDataKinds.Organization.TITLE, jobTitle)
                                    .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
                                    .build());
                        }

                        // Asking the Contact provider to create a new contact
                        try {
                            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext() , "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        */