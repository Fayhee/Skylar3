package com.example.skylar.hedera.ui;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.skylar.R;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk. PrecheckStatusException;
import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.TransferTransaction;
import com.hedera.hashgraph.sdk.PrivateKey;

import java.math.BigDecimal;
import java.util.concurrent.TimeoutException;

public class CryptoTransferFragment extends Fragment {
    private EditText recipientAccountId;
    private EditText amountToSend;
    private TextView resultText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_crypto_transfer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recipientAccountId = view.findViewById(R.id.recipientAccountId);
        amountToSend = view.findViewById(R.id.amountToSend);
        resultText = view.findViewById(R.id.transferResult);

        view.findViewById(R.id.sendHbar).setOnClickListener(v -> {
            try {
                final AccountId operatorId = AccountId.fromString(view.getResources().getString(R.string.operator_id));
                final PrivateKey operatorKey = PrivateKey.fromString(view.getResources().getString(R.string.operator_key));
                Client client = Client.forTestnet().setOperator(operatorId, operatorKey);

                final AccountId recipientId = AccountId.fromString(recipientAccountId.getText().toString());
                final Hbar amount = new Hbar(new BigDecimal(amountToSend.getText().toString()).longValueExact());

                new TransferAsyncTask(operatorId, recipientId, amount).execute(client);
            } catch (Throwable e) {
                resultText.setText("Error: " + e.getMessage());
            }
        });
    }

    private class TransferAsyncTask extends AsyncTask<Client, Void, String> {
        private AccountId sender;
        private AccountId recipient;
        private Hbar amount;

        TransferAsyncTask(AccountId sender, AccountId recipient, Hbar amount) {
            this.sender = sender;
            this.recipient = recipient;
            this.amount = amount;
        }

        @Override
        protected String doInBackground(Client... clients) {
            try {
                return new TransferTransaction()
                    .addHbarTransfer(sender, amount)
                    .addHbarTransfer(recipient, amount)
                    .execute(clients[0])
                    .toString();
            } catch (PrecheckStatusException | TimeoutException e) {
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            resultText.setText(result);
        }
    }
}
