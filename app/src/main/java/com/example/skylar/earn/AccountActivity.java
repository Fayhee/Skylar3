package com.example.skylar.earn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.ClipboardManager;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.skylar.R;
import com.example.skylar.learn.CourseActivity;
import com.example.skylar.learn.QuizActivity;
import com.example.skylar.model.CurrencyRVAdapter;
import com.example.skylar.model.CurrencyRVModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk. PrecheckStatusException;
import com.hedera.hashgraph.sdk.AccountBalanceQuery;
import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TransferTransaction;

public class AccountActivity extends AppCompatActivity {


    private ArrayList<CurrencyRVModel> currencyRVModelArrayList;
    private CurrencyRVAdapter currencyRVAdapter;
    private RecyclerView currencies;
    private ProgressBar loadingPB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        ClipboardManager clipboardManager;

        currencies = (RecyclerView) findViewById(R.id.currencies);
        loadingPB = findViewById(R.id.progress_check);
        currencyRVModelArrayList = new ArrayList<>();
        currencyRVAdapter = new CurrencyRVAdapter(currencyRVModelArrayList,this);
        currencies.setLayoutManager(new LinearLayoutManager(this));
        currencies.setAdapter(currencyRVAdapter);

        SharedPreferences sharedPreferences = getSharedPreferences("myKeys", MODE_PRIVATE);
        String accountid = sharedPreferences.getString("publicKey","");


        TextView theAccountid = findViewById(R.id.text_key);
        theAccountid.setText(accountid);

        theAccountid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboardManager = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                String copy_data = theAccountid.getText().toString();
                ClipData clipData = ClipData.newPlainText("copied", copy_data);

                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(AccountActivity.this, "Public address copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });

        try {
            final AccountId operatorId = AccountId.fromString(String.valueOf(R.string.operator_id));
            final PrivateKey operatorKey = PrivateKey.fromString(String.valueOf(R.string.operator_key));
            Client client = Client.forTestnet().setOperator(operatorId, operatorKey);

            final AccountId id = AccountId.fromString(theAccountid.getText().toString());


            Thread thread = new Thread(new Runnable() {


                @Override
                public void run() {
                    String asset, code, balance1;
                    try  {
                        String  balance;
                       balance = new AccountBalanceQuery()
                                .setAccountId(id)
                                .execute(client)
                                .toString();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }


            });

            thread.start();

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        LinearLayout send_layout = findViewById(R.id.send_layout);
        send_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final AccountId id = AccountId.fromString(theAccountid.getText().toString());
                    showBottomSheet(id);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getCurrencyData(String balance){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                loadingPB.setVisibility(View.VISIBLE);


            }
        });

        String url = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        loadingPB = findViewById(R.id.progress_check);
                        loadingPB.setVisibility(View.GONE);


                    }
                });

                try {
                    JSONArray dataArray = response.getJSONArray("data");
                    for (int k = 0; k < dataArray.length(); k++){
                        JSONObject dataObj = dataArray.getJSONObject(k);
                        String name = dataObj.getString("name");
                        String symbol = dataObj.getString("symbol");
                        JSONObject quote = dataObj.getJSONObject("quote");
                        JSONObject USD = quote.getJSONObject("USD");
                        double price = USD.getDouble("price");

                        if (name.equalsIgnoreCase("Hedera")){
                            double balanceDB = Double.parseDouble(balance);
                            double balanceDollar = balanceDB/price;
                            currencyRVModelArrayList.add(new CurrencyRVModel(name,symbol,balanceDB,price,balanceDollar));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    DecimalFormat df2 = new DecimalFormat("#.##");
                                    String balanceDollar2 = "$" + df2.format(balanceDollar);
                                    TextView dollar_balance = findViewById(R.id.dollar_balance);
                                    dollar_balance.setText(balanceDollar2);


                                    currencyRVAdapter.notifyDataSetChanged();
                                    SharedPreferences sharedPreferences = getSharedPreferences("accountDetail", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    DecimalFormat df3 = new DecimalFormat("#.##");
                                    String price2 = "" + df3.format(price);
                                    editor.putString("price",price2 );
                                    editor.putString("balanceDB", balanceDB+"");
                                    editor.apply();


                                }
                            });
                        }

                    }


                }catch (JSONException e){
                    e.printStackTrace();
                    Toast.makeText(AccountActivity.this, "Failed to extract json data", Toast.LENGTH_SHORT).show();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        loadingPB.setVisibility(View.GONE);
                    }
                });

                Toast.makeText(AccountActivity.this, "Failed to get the data", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String,String> headers = new HashMap<>();
                headers.put("X-CMC_PRO_API_KEY", "fe46c0c7-5a97-403a-8b45-6c13fa012959");
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);



    }

    private void showBottomSheet(AccountId id)  throws IOException{
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(AccountActivity.this);
        View bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.send_tray_sheet1, (LinearLayout) findViewById(R.id.idSendLayout));

        EditText receiverAddress = findViewById(R.id.receiverAddress);
        TextView idCancel = bottomSheetView.findViewById(R.id.idCancel);
        Button confirmBTN = bottomSheetView.findViewById(R.id.confirmBTN);

        AccountId receiver =AccountId.fromString(" ");
        try {
            receiver = AccountId.fromString(receiverAddress.getText().toString());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        final AccountId recipientId = AccountId.fromString(receiverAddress.getText().toString());



        idCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

        confirmBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                BottomSheetDialog bottomSheetDialog2 = new BottomSheetDialog(AccountActivity.this);
                View bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.send_tray_sheet2, (LinearLayout) findViewById(R.id.idSendLayout2));
                EditText sendAmount = bottomSheetView.findViewById(R.id.sendAmount);
                TextView useMax = bottomSheetView.findViewById(R.id.useMax);
                TextView sendConversion = bottomSheetView.findViewById(R.id.sendConversion);
                TextView sendBalance = bottomSheetView.findViewById(R.id.sendBalance);
                Button cinfirmBTN2 = bottomSheetView.findViewById(R.id.cinfirmBTN2);


                SharedPreferences sharedPreferences = getSharedPreferences("accountDetail", MODE_PRIVATE);
                String price = sharedPreferences.getString("price","");
                String balanceDB1 = sharedPreferences.getString("balanceDB","");
                Double balanceDB2 = Double.parseDouble(balanceDB1);
                DecimalFormat df3 = new DecimalFormat("#.##");
                String balanceDB = "" + df3.format(balanceDB2);

                String balanceDB3 = "Balance: " + balanceDB + "HBar";
                sendBalance.setText(balanceDB3);


                useMax.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            sendAmount.setText(balanceDB);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                });

                String theAmount = "0";

                Double exchangeRate2 = Double.parseDouble(price);
                Double theAmount2 = Double.parseDouble(theAmount);
                Double conversion = theAmount2/exchangeRate2;
                String conversion2 = "$" + df3.format(conversion);
                sendConversion.setText(conversion2);

                TextView sendCancel = bottomSheetView.findViewById(R.id.sendCancel);
                TextView idback = bottomSheetView.findViewById(R.id.idback);


                sendCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog2.dismiss();
                    }
                });

                idback.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog2.dismiss();
                        bottomSheetDialog.setCancelable(false);
                        bottomSheetDialog.show();
                    }
                });

                sendAmount.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        EditText sendAmount = bottomSheetView.findViewById(R.id.sendAmount);
                        String theAmount = "0";
                        try {
                            theAmount = sendAmount.getText().toString();
                            if (theAmount.equals("")){theAmount = "0";}
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }

                        Double exchangeRate2 = Double.parseDouble(price);

                        try {
                            Double theAmount2 = Double.parseDouble(theAmount);
                            Double conversion = theAmount2/exchangeRate2;
                            String conversion2 = "$" + df3.format(conversion);
                            sendConversion.setText(conversion2);
                        }catch(NumberFormatException ex){
                            Toast.makeText(AccountActivity.this, "Invalid amount" , Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                cinfirmBTN2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final AccountId operatorId = AccountId.fromString(String.valueOf(R.string.operator_id));
                        final PrivateKey operatorKey = PrivateKey.fromString(String.valueOf(R.string.operator_key));
                        Client client = Client.forTestnet().setOperator(operatorId, operatorKey);

                        TextView theAccountid = findViewById(R.id.text_key);




                        final Hbar amount = new Hbar(new BigDecimal(sendAmount.getText().toString()).longValueExact());

                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    AccountId receiver = AccountId.fromString(receiverAddress.getText().toString());
                                    new TransferTransaction()
                                            .addHbarTransfer(id, amount)
                                            .addHbarTransfer(receiver, amount)
                                            .execute(client);

                                } catch (PrecheckStatusException | TimeoutException e) {
                                    e.printStackTrace();
                                }



                            }
                        });
                        thread.start();
                    }
                });


                bottomSheetDialog2.setCancelable(false);
                bottomSheetDialog2.setContentView(bottomSheetView);
                bottomSheetDialog2.show();
            }
        });





    }

}
