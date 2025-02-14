package com.example.prm392_miniproject.utilities;

import android.content.Context;

import com.example.prm392_miniproject.models.Account;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class AccountManager {
    private static final String FILENAME = "accounts.json";
    private Context context;

    public AccountManager(Context context) {
        this.context = context;
    }

    // Save multiple accounts
    public void saveAccounts(List<Account> accounts) {
        StringBuilder jsonBuilder = new StringBuilder("[");
        for (int i = 0; i < accounts.size(); i++) {
            jsonBuilder.append(accounts.get(i).toJson());
            if (i < accounts.size() - 1) {
                jsonBuilder.append(",");
            }
        }
        jsonBuilder.append("]");

        try (FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE)) {
            fos.write(jsonBuilder.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load multiple accounts
    public List<Account> loadAccounts() {
        List<Account> accounts = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        try (FileInputStream fis = context.openFileInput(FILENAME);
             InputStreamReader isr = new InputStreamReader(fis);
             BufferedReader bufferedReader = new BufferedReader(isr)) {

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }

            String jsonString = sb.toString();
            String[] accountJsons = jsonString.substring(1, jsonString.length() - 1).split("\\},\\{");

            for (String json : accountJsons) {
                // Ensure proper JSON format
                if (!json.startsWith("{")) json = "{" + json;
                if (!json.endsWith("}")) json += "}";
                accounts.add(Account.fromJson(json));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return accounts;
    }

    // Register a new account
    public boolean register(Account newAccount) {
        List<Account> accounts = loadAccounts();

        // Check if the username or email already exists
        for (Account account : accounts) {
            if (account.getUsername().equals(newAccount.getUsername()) ||
                    account.getEmail().equals(newAccount.getEmail())) {
                return false; // Username or email already exists
            }
        }

        // Add the new account and save
        accounts.add(newAccount);
        saveAccounts(accounts);
        return true; // Registration successful
    }

    // Login with email and password
    public boolean login(String email, String password) {
        List<Account> accounts = loadAccounts();

        for (Account account : accounts) {
            if (account.getEmail().equals(email) && account.getPassword().equals(password)) {
                return true; // Login successful
            }
        }
        return false; // Login failed
    }
}