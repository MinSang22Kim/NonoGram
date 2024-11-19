package com.example.nonograms;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private TableLayout tableLayout;
    private LinearLayout rowClueContainer;
    private LinearLayout columnClueContainer;
    private TextView lifeTextView;
    private ToggleButton toggleButton;
    private Button resetButton;
    private int lives = 3;
    private boolean[][] blackSquares;
    private int numBlackSquares;
    private final int size = 5; // 보드 크기
    private ArrayList<int[]> rowClues;
    private ArrayList<int[]> colClues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI 요소 초기화
        tableLayout = findViewById(R.id.tableLayout);
        rowClueContainer = findViewById(R.id.rowClueContainer);
        columnClueContainer = findViewById(R.id.columnClueContainer);
        lifeTextView = findViewById(R.id.lifeTextView);
        toggleButton = findViewById(R.id.toggleButton);
        resetButton = findViewById(R.id.resetButton);

        // Reset 버튼 클릭 이벤트
        resetButton.setOnClickListener(v -> resetGame());

        // 초기 게임 세팅
        resetGame();
    }

    private void resetGame() {
        lives = 3; // 생명 초기화
        updateLifeText();
        blackSquares = new boolean[size][size];
        generateRandomBlackSquares(); // 무작위 검은 사각형 생성
        generateClues(); // 힌트 생성
        displayClues(); // 힌트 UI 업데이트
        initBoard(); // 게임 보드 초기화
    }

    private void generateRandomBlackSquares() {
        Random random = new Random();
        numBlackSquares = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                blackSquares[i][j] = random.nextBoolean();
                if (blackSquares[i][j]) numBlackSquares++;
            }
        }
    }

    private void generateClues() {
        rowClues = new ArrayList<>();
        colClues = new ArrayList<>();

        // 행 힌트 생성
        for (int i = 0; i < size; i++) {
            rowClues.add(makeClues(blackSquares[i]));
        }

        // 열 힌트 생성
        for (int j = 0; j < size; j++) {
            colClues.add(makeClues(getColumn(j)));
        }
    }

    private int[] makeClues(boolean[] line) {
        ArrayList<Integer> clues = new ArrayList<>();
        int count = 0;

        for (boolean cell : line) {
            if (cell) {
                count++;
            } else if (count > 0) {
                clues.add(count);
                count = 0;
            }
        }
        if (count > 0) clues.add(count); // 마지막 클루 추가

        // 클루가 없는 경우 0 추가
        if (clues.isEmpty()) {
            clues.add(0);
        }

        return clues.stream().mapToInt(Integer::intValue).toArray();
    }

    private boolean[] getColumn(int colIndex) {
        boolean[] column = new boolean[size];
        for (int i = 0; i < size; i++) {
            column[i] = blackSquares[i][colIndex];
        }
        return column;
    }

    private void displayClues() {
        // 행 힌트 표시
        rowClueContainer.removeAllViews(); // 기존 힌트 제거
        for (int[] rowClue : rowClues) {
            TextView textView = new TextView(this);
            StringBuilder clueText = new StringBuilder();
            clueText.append("\n"); // 줄바꿈 처리
            // 행 힌트 데이터 생성 및 줄바꿈 추가
            for (int clue : rowClue) {
                clueText.append(clue).append(" "); // 줄바꿈 처리
            }

            // TextView 속성 설정
            textView.setText(clueText.toString().trim()); // 줄바꿈 처리 후 텍스트 설정
            textView.setTextSize(14);
            textView.setGravity(Gravity.CENTER);
            textView.setPadding(4, 26, 4, 26);

            rowClueContainer.addView(textView); // 컨테이너에 추가
        }

        // 열 힌트 표시
        columnClueContainer.removeAllViews(); // 기존 힌트 제거
        for (int[] colClue : colClues) {
            TextView textView = new TextView(this);
            StringBuilder clueText = new StringBuilder();

            // 열 힌트 데이터 생성 및 줄바꿈 추가
            for (int clue : colClue) {
                clueText.append(clue).append("\n"); // 줄바꿈 처리
            }

            // TextView 속성 설정
            textView.setText(clueText.toString().trim()); // 줄바꿈 처리 후 텍스트 설정
            textView.setTextSize(14);
            textView.setGravity(Gravity.CENTER);
            textView.setPadding(62, 4, 62, 4);

            columnClueContainer.addView(textView); // 컨테이너에 추가
        }
    }


    private void initBoard() {
        tableLayout.removeAllViews();

        for (int i = 0; i < size; i++) {
            TableRow row = new TableRow(this);
            for (int j = 0; j < size; j++) {
                Cell cell = new Cell(this, blackSquares[i][j]);
                cell.setLayoutParams(new TableRow.LayoutParams(120, 120));
                int finalI = i, finalJ = j;
                cell.setOnClickListener(v -> handleCellClick(cell, finalI, finalJ));
                row.addView(cell);
            }
            tableLayout.addView(row);
        }
    }

    // 테스트용 X 표시
    private void handleCellClick(Cell cell, int row, int col) {
        if (toggleButton.isChecked()) {
            cell.toggleX();
        } else {
            if (cell.markBlackSquare()) {
                numBlackSquares--;
                cell.setBackgroundColor(Color.GRAY); // 클릭된 셀의 배경을 회색으로 변경
                if (numBlackSquares == 0) {
                    gameOver(true);
                }
            } else {
                lives--;
                cell.toggleX(); // 틀렸을 때 "X" 표시
                Toast.makeText(this, "Wrong Guess!", Toast.LENGTH_SHORT).show();
                updateLifeText();
                if (lives == 0) {
                    gameOver(false);
                }
            }
        }
    }

    private void updateLifeText() {
        lifeTextView.setText("Life: " + lives);
    }

    private void gameOver(boolean won) {
        String message = won ? "You Won!" : "Game Over!";
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        disableAllCells();
    }

    private void disableAllCells() {
        for (int i = 0; i < tableLayout.getChildCount(); i++) {
            TableRow row = (TableRow) tableLayout.getChildAt(i);
            for (int j = 0; j < row.getChildCount(); j++) {
                row.getChildAt(j).setEnabled(false);
            }
        }
    }
}
