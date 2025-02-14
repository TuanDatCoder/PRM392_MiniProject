package com.example.prm392_miniproject;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm392_miniproject.utilities.SoundManager;

import java.util.Arrays;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private TextView balanceTextView;
    private EditText[] horseBetEditTexts;
    private CheckBox[] horseCheckBoxes;
    private SeekBar[] horseSeekBars;
    private Button startButton, resetButton, selectHoose;
    private int balance = 1000;
    private Random random = new Random();

    private int[] betAmounts = new int[4]; // Lưu số tiền cược



    private Handler handler = new Handler();
    private SoundManager soundManager = new SoundManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        balanceTextView = findViewById(R.id.balanceTextView);
        selectHoose = findViewById(R.id.selectHorseButton);
        startButton = findViewById(R.id.startButton);
        resetButton = findViewById(R.id.resetButton);

        horseBetEditTexts = new EditText[]{
                findViewById(R.id.horse1BetEditText),
                findViewById(R.id.horse2BetEditText),
                findViewById(R.id.horse3BetEditText),
                findViewById(R.id.horse4BetEditText)
        };

        horseCheckBoxes = new CheckBox[]{
                findViewById(R.id.horse1CheckBox),
                findViewById(R.id.horse2CheckBox),
                findViewById(R.id.horse3CheckBox)
        };

        horseSeekBars = new SeekBar[]{
                findViewById(R.id.horse1SeekBar),
                findViewById(R.id.horse2SeekBar),
                findViewById(R.id.horse3SeekBar),
                findViewById(R.id.horse4SeekBar)
        };

        updateBalance();
        selectHoose.setOnClickListener(v -> showChooseHorseDialog());
        startButton.setOnClickListener(v -> startRace());
        resetButton.setOnClickListener(v -> resetRace());
        soundManager.loadSound("start", R.raw.start);
        soundManager.loadSound("epic_background", R.raw.epic_background);
        soundManager.loadSound("cheer", R.raw.cheer);
        soundManager.loadSound("losing", R.raw.losing);
        soundManager.loadSound("winning", R.raw.winning);
        soundManager.loadSound("horse_galloping", R.raw.horse_galloping);
    }

    private void updateBalance() {
        balanceTextView.setText("💰 Số dư: " + balance + " VND");
    }

    private void startRace() {
        // Kiểm tra nếu chưa đặt cược
        int totalBet = Arrays.stream(betAmounts).sum();
        if (totalBet == 0) {
            Toast.makeText(this, "Vui lòng đặt cược trước khi bắt đầu cuộc đua!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Dừng bất kỳ cuộc đua nào đang chạy trước đó
        handler.removeCallbacks(this::moveSeekBars);

        // Đặt lại tiến trình cuộc đua
        for (SeekBar seekBar : horseSeekBars) {
            seekBar.setProgress(0);
        }
        soundManager.playSound("start");
        soundManager.playSound("horse_galloping");
        // Bắt đầu cuộc đua sau 1 giây
        handler.postDelayed(this::moveSeekBars, 300);
    }


    private void determineWinner(int winningHorse) {
        Toast.makeText(this, "Ngựa " + (winningHorse + 1) + " thắng cuộc!", Toast.LENGTH_LONG).show();

        // Cộng tiền thưởng nếu người chơi đã cược vào con ngựa thắng
        int totalWinnings = 0;
        for (int i = 0; i < horseCheckBoxes.length; i++) {
            if (horseCheckBoxes[i].isChecked()) {
                int betAmount = Integer.parseInt(horseBetEditTexts[i].getText().toString());
                if (i == winningHorse) {
                    // Người chơi thắng cược, nhận gấp đôi số tiền đặt cược
                    totalWinnings += betAmount * 2;
                }
            }
        }

        balance += totalWinnings;
        updateBalance();
    }


    private void showChooseHorseDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_choose_horse);

        EditText[] betInputs = new EditText[]{
                dialog.findViewById(R.id.horse1BetEditText),
                dialog.findViewById(R.id.horse2BetEditText),
                dialog.findViewById(R.id.horse3BetEditText),
                dialog.findViewById(R.id.horse4BetEditText)
        };

        CheckBox[] checkBoxes = new CheckBox[]{
                dialog.findViewById(R.id.horse1CheckBox),
                dialog.findViewById(R.id.horse2CheckBox),
                dialog.findViewById(R.id.horse3CheckBox),
                dialog.findViewById(R.id.horse4CheckBox)
        };

        Button confirmButton = dialog.findViewById(R.id.btnConfirm);
        confirmButton.setOnClickListener(v -> {
            boolean hasBet = false;
            int totalBet = 0; // Tổng số tiền cược

            for (int i = 0; i < checkBoxes.length; i++) {
                if (checkBoxes[i].isChecked()) {
                    String betText = betInputs[i].getText().toString();
                    if (!betText.isEmpty()) {
                        int bet = Integer.parseInt(betText);
                        if (bet > 0 && bet <= balance) {
                            betAmounts[i] = bet; // Lưu tiền cược
                            totalBet += bet; // Cộng tổng tiền cược
                            hasBet = true;
                        } else {
                            Toast.makeText(this, "Số tiền cược không hợp lệ!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else {
                        Toast.makeText(this, "Vui lòng nhập số tiền cược!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    betAmounts[i] = 0; // Nếu không cược thì đặt về 0
                }
            }

            if (!hasBet) {
                Toast.makeText(this, "Vui lòng chọn ít nhất một con ngựa để đặt cược!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Trừ số tiền đã cược vào số dư
            balance -= totalBet;
            updateBalance();

            Toast.makeText(this, "Đã đặt cược " + totalBet + " VND!", Toast.LENGTH_SHORT).show();

            dialog.dismiss(); // Đóng hộp thoại sau khi xác nhận
        });


        dialog.show();
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
            soundManager.stopAllSounds();
            checkRaceResult();
        }
    }

    private void checkRaceResult() {
        int winningHorse = -1;
        int[] horsePositions = new int[horseSeekBars.length];

        // Sắp xếp vị trí của các con ngựa dựa vào tiến trình
        for (int i = 0; i < horseSeekBars.length; i++) {
            horsePositions[i] = horseSeekBars[i].getProgress();
        }

        // Tìm con ngựa về đích đầu tiên
        for (int i = 0; i < horseSeekBars.length; i++) {
            if (horseSeekBars[i].getProgress() >= 100) {
                winningHorse = i;
                break;
            }
        }

        if (winningHorse != -1) {
            int totalWinAmount = 0;
            boolean isWinner = false;

            // Kiểm tra xem người chơi có đặt cược vào con ngựa thắng không
            for (int i = 0; i < betAmounts.length; i++) {
                if (betAmounts[i] > 0 && i == winningHorse) {
                    int winAmount = betAmounts[i] * 2; // Thắng gấp đôi
                    totalWinAmount += winAmount;
                    isWinner = true;
                }
            }

            if (isWinner) {
                balance += totalWinAmount;
            }

            // Hiển thị bảng kết quả
            showRaceResultDialog(winningHorse, horsePositions, totalWinAmount, isWinner);

            // Reset tiền cược sau mỗi cuộc đua
            Arrays.fill(betAmounts, 0);
        }
    }


    private void showRaceResultDialog(int winningHorse, int[] horsePositions, int totalWinAmount, boolean isWinner) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_race_result);

        TextView resultTextView = dialog.findViewById(R.id.resultTextView);
        TextView balanceTextView = dialog.findViewById(R.id.balanceResultTextView);
        Button closeButton = dialog.findViewById(R.id.btnClose);

        // Sắp xếp thứ tự về đích của các con ngựa
        Integer[] indices = {0, 1, 2, 3};
        Arrays.sort(indices, (a, b) -> Integer.compare(horsePositions[b], horsePositions[a]));

        // Tạo chuỗi hiển thị thứ tự về đích
        StringBuilder resultText = new StringBuilder("🏆 Ngựa " + (winningHorse + 1) + " thắng cuộc!\n\nThứ tự về đích:\n");
        for (int i = 0; i < indices.length; i++) {
            resultText.append((i + 1) + ". Ngựa " + (indices[i] + 1) + "\n");
        }

        if (isWinner) {
            soundManager.playSound("cheer");
            soundManager.playSound("winning");
            resultText.append("\n🎉 Bạn thắng cược: " + totalWinAmount + " VND!");
        } else {
            soundManager.playSound("losing");
            resultText.append("\n😢 Bạn đã thua cược. Thử lại nhé!");
        }
        resultTextView.setText(resultText.toString());
        balanceTextView.setText("💰 Số dư hiện tại: " + balance + " VND");

        closeButton.setOnClickListener(v -> {
            Arrays.fill(betAmounts, 0);
            dialog.dismiss();
        });

        dialog.show();
    }


    private void resetRace() {
        // Dừng bất kỳ cuộc đua nào đang chạy
        handler.removeCallbacks(this::moveSeekBars);

        // Đặt lại tiến trình của tất cả các ngựa
        if (horseSeekBars != null) {
            for (SeekBar seekBar : horseSeekBars) {
                if (seekBar != null) {
                    seekBar.setProgress(0);
                }
            }
        }

        // Xóa nội dung nhập số tiền cược
        if (horseBetEditTexts != null) {
            for (EditText editText : horseBetEditTexts) {
                if (editText != null) {
                    editText.setText("");
                }
            }
        }

        // Bỏ chọn tất cả checkbox cược
        if (horseCheckBoxes != null) {
            for (CheckBox checkBox : horseCheckBoxes) {
                if (checkBox != null) {
                    checkBox.setChecked(false);
                }
            }
        }

        // Đặt lại số tiền cược về 0
        betAmounts = new int[4];

        // Đặt lại số dư về 1000
        balance = 1000;
        updateBalance();

        // Thông báo reset thành công
        Toast.makeText(this, "Trò chơi đã được đặt lại!", Toast.LENGTH_SHORT).show();
    }



}