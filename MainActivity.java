package com.johar.Johar_Stocks;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import java.io.InputStream;
import android.database.sqlite.*;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import java.io.*;
import android.support.v7.widget.Toolbar;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    EditText EditText1;
    List<StockBuilder> stockList = new ArrayList < >();
    List< String > symbols = new ArrayList< >();
    private String search = "";
    SwipeRefreshLayout mSwipeRefreshLayout;
    public void onCreate(Bundle savedInstanceState) {
        //StockAdapter nAdapter;
        //RecyclerView stockRecycler;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        final RequestQueue queue = Volley.newRequestQueue(this);
        //stockList.clear();
        File directory;
        directory = getFilesDir();
        File[] files = directory.listFiles();
        for (File file: files) {
            final String theFile = file.getName();
            //final StockBuilder note = new StockBuilder(theFile, "qq");
            //stockList.add(0, note);
            final String name = theFile;
            String url = "https://cloud.iexapis.com/stable/stock/" + name + "/quote?token=sk_5230d45b96404424a6297b168b0d52b8";
            JsonObjectRequest thing = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        StockAdapter nAdapter;
                        RecyclerView stockRecycler;
                        stockRecycler = (RecyclerView) findViewById(R.id.notes);
                        nAdapter = new StockAdapter(stockList);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        stockRecycler.setLayoutManager(mLayoutManager);
                        stockRecycler.setItemAnimator(new DefaultItemAnimator());
                        stockRecycler.setAdapter(nAdapter);
                        String price = response.getString("latestPrice");
                        String change = response.getString("change");
                        String changeperc = response.getString("changePercent");
                        StockBuilder note = new StockBuilder(response.getString("symbol"), response.getString("companyName"),
                               Float.parseFloat(price), Float.parseFloat(change), Float.parseFloat(changeperc));
                        stockList.add(note);
                        nAdapter.setOnItemClickListener(new StockAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                String url = "https://www.marketwatch.com/investing/stock/" + stockList.get(position).getTitle();
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(url));
                                startActivity(i);
                            }
                        });
                        nAdapter.setOnLongItemClickListener(new StockAdapter.OnLongItemClickListener() {
                            @Override
                            public void onLongItemClick(final int position) {
                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("Delete Stock")
                                        .setMessage("Are you sure you want to delete this stock from your list?")

                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                String dir = getFilesDir().getAbsolutePath();
                                                String name = stockList.get(position).getTitle();
                                                File f0 = new File(dir, name);
                                                boolean d0 = f0.delete();
                                                finish();
                                                startActivity(getIntent());
                                            }
                                        })

                                        .setNegativeButton(android.R.string.no, null)
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                            }
                        });
                        Collections.sort(stockList, new Comparator<StockBuilder>() {
                            @Override
                            public int compare(StockBuilder lhs, StockBuilder rhs) {
                                return lhs.getTitle().compareTo(rhs.getTitle());
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            queue.add(thing);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    prepareNotes();
                }
            });
        }
        String url = "https://api.iextrading.com/1.0/ref-data/symbols";
        JsonArrayRequest thing2 = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++){
                    try {
                        JSONObject obj = response.getJSONObject(i);
                        String symbol = obj.getString("symbol");
                        symbols.add(symbol);
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(thing2);


    }

    public void onPause () {
        super.onPause();
    }
    public void onResume () {
        super.onResume();
        StockAdapter nAdapter;
        final RecyclerView stockRecycler;
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        stockRecycler = (RecyclerView) findViewById(R.id.notes);
        nAdapter = new StockAdapter(stockList);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        stockRecycler.setLayoutManager(mLayoutManager);
        stockRecycler.setItemAnimator(new DefaultItemAnimator());
        stockRecycler.setAdapter(nAdapter);
        Collections.sort(stockList, new Comparator<StockBuilder>() {
            @Override
            public int compare(StockBuilder lhs, StockBuilder rhs) {
                return lhs.getTitle().compareTo(rhs.getTitle());
            }
        });
        nAdapter.notifyDataSetChanged();

    }

    private void prepareNotes() {
        mSwipeRefreshLayout.setRefreshing(false);
        finish();
        startActivity(getIntent());
    }




    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public void Save(String fileName) {
        try {
            OutputStreamWriter out =
                    new OutputStreamWriter(openFileOutput(fileName, 0));
            out.close();
        } catch (Throwable t) {
            Toast.makeText(this, "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public String Open(String fileName) {
        String content = "";
        if (FileExists(fileName)) {
            try {
                InputStream in = openFileInput(fileName);
                if ( in != null) {
                    InputStreamReader tmp = new InputStreamReader( in );
                    BufferedReader reader = new BufferedReader(tmp);
                    String str;
                    StringBuilder buf = new StringBuilder();
                    while ((str = reader.readLine()) != null) {
                        buf.append(str + "\n");
                    } in .close();
                    content = buf.toString();
                }
            } catch (java.io.FileNotFoundException e) {} catch (Throwable t) {
                Toast.makeText(this, "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
            }
        }
        return content;
    }

    public boolean FileExists(String fname) {
        File file = getBaseContext().getFileStreamPath(fname);
        return file.exists();
    }

    public void getStats(){
        for (int i = 0; i < stockList.size(); i++) {
            stockList.get(i).setContent("birds");
        }
    }


    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    public void onNewClick(View view){
        final EditText search = new EditText(this);
        search.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (isConnected) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Add new stock")
                    .setMessage("Enter the stock symbol")
                    .setView(search)
                    .setPositiveButton("Search", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, int which) {
                            int matching = 0;
                            final String sym = search.getText().toString();
                            final ArrayAdapter <String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.select_dialog_singlechoice);
                            for (int i = 0; i < symbols.size(); i++){
                                if (symbols.get(i).contains(sym)){
                                    String str = symbols.get(i).substring(0,sym.length());
                                    if (str.equals(sym)){
                                        matching++;
                                        arrayAdapter.add(symbols.get(i));
                                    }
                                }
                            }

                            if (matching > 0) {
                                AlertDialog.Builder list = new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("Select stock to add");
                                        //final ArrayAdapter <String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.select_dialog_singlechoice);
                                        list.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                finish();
                                                startActivity(getIntent());
                                            }
                                        });
                                        list.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int which) {
                                                String strname = arrayAdapter.getItem(which);
                                                final String s = strname;
                                                AlertDialog.Builder inner = new AlertDialog.Builder(MainActivity.this);
                                                inner.setMessage(strname);
                                                inner.setTitle("You selected");
                                                inner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        boolean same = false;
                                                        for (int k = 0; k < stockList.size(); k++){
                                                            if (stockList.get(k).getTitle().equals(sym)) {
                                                                same = true;
                                                            }
                                                        }
                                                        if(same == true) {
                                                            AlertDialog matchFound = new AlertDialog.Builder(MainActivity.this)
                                                                    .setTitle("Stock match")
                                                                    .setMessage("Stock already in your list")
                                                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                                            finish();
                                                                            startActivity(getIntent());
                                                                        }
                                                                    })
                                                                    .create();
                                                            matchFound.show();

                                                        } else {
                                                            Save(s);
                                                            finish();
                                                            startActivity(getIntent());
                                                        }
                                                    }
                                                });
                                                inner.show();
                                            }
                                        });
                                        list.show();
                            } else {
                                AlertDialog d = new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("Stock not found")
                                        .setMessage("Stock not found: " + sym)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                finish();
                                                startActivity(getIntent());
                                            }
                                        })
                                        .create();
                                d.show();
                            }
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .create();
            dialog.show();
        } else {
            AlertDialog no = new AlertDialog.Builder(this)
                    .setTitle("Cannot add stock")
                    .setMessage("You must have a network connection to add stocks")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .create();
            no.show();
        }
    }

}
