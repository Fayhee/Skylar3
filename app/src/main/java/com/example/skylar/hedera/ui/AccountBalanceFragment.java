package com.example.skylar.hedera.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.skylar.R;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk. PrecheckStatusException;
import com.hedera.hashgraph.sdk.AccountBalanceQuery;
import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.PrivateKey;

import java.util.concurrent.TimeoutException;

public class AccountBalanceFragment extends Fragment {
    private TextView accountBalance;
    private EditText accountId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstance) {
        return inflater.inflate(R.layout.fragment_account_balance, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        accountBalance = view.findViewById(R.id.accountBalance);
        accountId = view.findViewById(R.id.accountId);

        view.findViewById(R.id.balanceButton).setOnClickListener(v -> {
            try {
                final AccountId operatorId = AccountId.fromString(view.getResources().getString(R.string.operator_id));
                final PrivateKey operatorKey = PrivateKey.fromString(view.getResources().getString(R.string.operator_key));
                Client client = Client.forTestnet().setOperator(operatorId, operatorKey);

                final AccountId id = AccountId.fromString(accountId.getText().toString());

                new AccountBalanceAsyncTask(id).execute(client);

            } catch (IllegalArgumentException e) {
                accountBalance.setText("Error: " + e.getMessage());
            }
        });
    }

    private class AccountBalanceAsyncTask extends AsyncTask<Client, Void, String> {
        final AccountId accountId;

        AccountBalanceAsyncTask(AccountId accountId) {
            this.accountId = accountId;
        }

        @Override
        protected String doInBackground(Client... clients) {
            try {
                String balance = new AccountBalanceQuery()
                        .setAccountId(this.accountId)
                        .execute(clients[0])
                        .toString();

                return "Account balance: " + balance;
            } catch (PrecheckStatusException | TimeoutException e) {
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            accountBalance.setText(result);
        }
    }
}
