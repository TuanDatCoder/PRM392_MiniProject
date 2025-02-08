package com.example.prm392_miniproject;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private TextView balanceTextView;
    private EditText[] horseBetEditTexts;
    private CheckBox[] horseCheckBoxes;
    private SeekBar[] horseSeekBars;
    private Button startButton, resetButton;
    private int balance = 1000;
    private Random random = new Random();
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        balanceTextView = findViewById(R.id.balanceTextView);
        startButton = findViewById(R.id.startButton);
        resetButton = findViewById(R.id.resetButton);

        horseBetEditTexts = new EditText[]{
                findViewById(R.id.horse1BetEditText),
                findViewById(R.id.horse2BetEditText),
                findViewById(R.id.horse3BetEditText),
                findViewById(R.id.horse4BetEditText),
                findViewById(R.id.horse5BetEditText)
        };

        horseCheckBoxes = new CheckBox[]{
                findViewById(R.id.horse1CheckBox),
                findViewById(R.id.horse2CheckBox),
                findViewById(R.id.horse3CheckBox),
                findViewById(R.id.horse4CheckBox),
                findViewById(R.id.horse5CheckBox)
        };

        horseSeekBars = new SeekBar[]{
                findViewById(R.id.horse1SeekBar),
                findViewById(R.id.horse2SeekBar),
                findViewById(R.id.horse3SeekBar),
                findViewById(R.id.horse4SeekBar),
                findViewById(R.id.horse5SeekBar)
        };

        updateBalance();

        startButton.setOnClickListener(v -> startRace());
        resetButton.setOnClickListener(v -> resetRace());
    }

    private void updateBalance() {
        balanceTextView.setText("💰 Số dư: " + balance + " VND");
    }

    private void startRace() {
        boolean validBet = false;
        int totalBet = 0;

        for (int i = 0; i < horseCheckBoxes.length; i++) {
            if (horseCheckBoxes[i].isChecked()) {
                String betText = horseBetEditTexts[i].getText().toString();
                if (!betText.isEmpty()) {
                    int betAmount = Integer.parseInt(betText);
                    if (betAmount > 0 && betAmount <= balance) {
                        totalBet += betAmount;
                        validBet = true;
                    } else {
                        Toast.makeText(this, "Số tiền cược không hợp lệ cho Ngựa " + (i + 1), Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    Toast.makeText(this, "Vui lòng nhập số tiền cược cho Ngựa " + (i + 1), Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }

        if (!validBet) {
            Toast.makeText(this, "Vui lòng chọn ít nhất một con ngựa và nhập tiền cược", Toast.LENGTH_SHORT).show();
            return;
        }

        balance -= totalBet;
        updateBalance();

        for (SeekBar seekBar : horseSeekBars) {
            seekBar.setProgress(0);
        }

        handler.postDelayed(this::moveSeekBars, 1000);
    }

    private void moveSeekBars() {
        boolean raceFinished = false;

        for (int i = 0; i < horseSeekBars.length; i++) {
            if (horseSeekBars[i].getProgress() < 100) {
                int progress = horseSeekBars[i].getProgress() + random.nextInt(5);
                horseSeekBars[i].setProgress(Math.min(progress, 100));
                if (horseSeekBars[i].getProgress() >= 100) {
                    raceFinished = true;
                }
            }
        }

        if (!raceFinished) {
            handler.postDelayed(this::moveSeekBars, 100);
        } else {
            checkRaceResult();
        }
    }

    private void checkRaceResult() {
        int winningHorse = -1;

        for (int i = 0; i < horseSeekBars.length; i++) {
            if (horseSeekBars[i].getProgress() >= 100) {
                winningHorse = i + 1;
                break;
            }
        }

        if (winningHorse != -1) {
            String message = "🏆 Ngựa " + winningHorse + " đã chiến thắng!";
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();

            boolean isWinner = false;
            for (int i = 0; i < horseCheckBoxes.length; i++) {
                if (horseCheckBoxes[i].isChecked() && (i + 1) == winningHorse) {
                    String betText = horseBetEditTexts[i].getText().toString();
                    int betAmount = Integer.parseInt(betText);
                    balance += betAmount * 2;
                    isWinner = true;
                }
            }

            if (isWinner) {
                Toast.makeText(this, "🎉 Bạn đã đặt cược đúng! Tiền thưởng đã được cộng vào tài khoản.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "😢 Bạn đã thua cược. Thử lại nhé!", Toast.LENGTH_LONG).show();
            }

            updateBalance();
        }
    }

    private void resetRace() {
        for (SeekBar seekBar : horseSeekBars) {
            seekBar.setProgress(0);
        }

        for (EditText editText : horseBetEditTexts) {
            editText.setText("");
        }

        for (CheckBox checkBox : horseCheckBoxes) {
            checkBox.setChecked(false);
        }

        balance = 1000;
        updateBalance();
    }
}