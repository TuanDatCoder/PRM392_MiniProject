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
    private int balance = 1000; // Số dư ban đầu
    private Random random = new Random();
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ánh xạ các view
        balanceTextView = findViewById(R.id.balanceTextView);
        startButton = findViewById(R.id.startButton);
        resetButton = findViewById(R.id.resetButton);

        // Khởi tạo mảng cho các con ngựa
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

        // Cập nhật số dư ban đầu
        updateBalance();

        // Xử lý sự kiện khi nhấn nút Bắt đầu
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRace();
            }
        });

        // Xử lý sự kiện khi nhấn nút Reset
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetRace();
            }
        });
    }

    private void updateBalance() {
        balanceTextView.setText("💰 Số dư: " + balance + " VND");
    }

    private void startRace() {
        // Kiểm tra xem người dùng đã chọn ngựa và nhập tiền cược hợp lệ chưa
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

        // Trừ số tiền cược khỏi số dư
        balance -= totalBet;
        updateBalance();

        // Bắt đầu cuộc đua
        for (SeekBar seekBar : horseSeekBars) {
            seekBar.setProgress(0);
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                moveSeekBars();
            }
        }, 1000);
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
        // Kiểm tra kết quả cuộc đua và cập nhật số dư
        for (int i = 0; i < horseSeekBars.length; i++) {
            if (horseSeekBars[i].getProgress() >= 100 && horseCheckBoxes[i].isChecked()) {
                String betText = horseBetEditTexts[i].getText().toString();
                int betAmount = Integer.parseInt(betText);
                balance += betAmount * 2; // Giả sử tỷ lệ thắng là 2:1
                updateBalance();
                Toast.makeText(this, "Ngựa " + (i + 1) + " thắng! Bạn nhận được " + (betAmount * 2) + " VND", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void resetRace() {
        // Reset tất cả các trạng thái về ban đầu
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