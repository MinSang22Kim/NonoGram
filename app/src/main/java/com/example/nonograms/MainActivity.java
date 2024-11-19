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
    private TableRow columnClueRow;
    private TextView lifeTextView;
    private ToggleButton toggleButton;
    private Button resetButton;
    private int lives = 3;
    private boolean[][] blackSquares;
    private int numBlackSquares; // 남은 검은 사각형 수
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
        columnClueRow = findViewById(R.id.columnClueRow);
        lifeTextView = findViewById(R.id.lifeTextView);
        toggleButton = findViewById(R.id.toggleButton);
        resetButton = findViewById(R.id.resetButton);

        resetButton.setOnClickListener(v -> resetGame());
        resetGame(); // 초기 게임 세팅
    }

    private void resetGame() {
        lives = 3; // 생명 초기화
        updateLifeText();
        blackSquares = new boolean[size][size];
        generateRandomBlackSquares();
        generateClues();
        displayClues();
        initBoard();
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

    /**
     * 힌트를 생성하는 메서드
     *
     * @param line boolean 배열 (행 또는 열)
     * @return 해당 라인의 힌트 배열
     */
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

        return clues.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * 특정 열을 추출하는 메서드
     *
     * @param colIndex 열 인덱스
     * @return 해당 열의 boolean 배열
     */
    private boolean[] getColumn(int colIndex) {
        boolean[] column = new boolean[size];
        for (int i = 0; i < size; i++) {
            column[i] = blackSquares[i][colIndex];
        }
        return column;
    }

    private void displayClues() {
        // 행 힌트 표시 (왼쪽 힌트)
        rowClueContainer.removeAllViews(); // 기존의 힌트 뷰를 모두 제거
        for (int[] rowClue : rowClues) { // 각 행의 힌트 데이터를 순회
            TextView textView = new TextView(this); // 힌트를 표시할 TextView 생성
            StringBuilder clueText = new StringBuilder("\n "); // 첫 번째 줄 공백 추가 (힌트를 한 칸 내리기 위해)
            for (int clue : rowClue) { // 현재 행 힌트의 각 숫자 순회
                clueText.append(clue).append(" "); // 힌트 숫자를 추가하고, 숫자 사이에 공백 삽입
            }
            textView.setText(clueText.toString().trim()); // 완성된 힌트 문자열을 TextView에 설정
            textView.setTextSize(14); // 힌트 글자 크기 설정
            textView.setGravity(Gravity.END); // 힌트를 오른쪽 정렬
            textView.setPadding(4, 18, 4, 18); // 힌트 텍스트의 여백 설정 (왼쪽, 위, 오른쪽, 아래)
            rowClueContainer.addView(textView); // 힌트 TextView를 행 힌트 컨테이너에 추가
        }

        // 열 힌트 표시 (위쪽 힌트)
        columnClueRow.removeAllViews(); // 기존의 열 힌트 뷰를 모두 제거
        for (int[] colClue : colClues) { // 각 열의 힌트 데이터를 순회
            TextView textView = new TextView(this); // 힌트를 표시할 TextView 생성
            StringBuilder clueText = new StringBuilder(); // 열 힌트를 저장할 문자열 빌더 생성
            for (int clue : colClue) { // 현재 열 힌트의 각 숫자 순회
                clueText.append(clue).append("\n"); // 힌트 숫자를 추가하고, 숫자 뒤에 줄바꿈 삽입
            }
            textView.setText(clueText.toString().trim()); // 완성된 힌트 문자열을 TextView에 설정
            textView.setTextSize(14); // 힌트 글자 크기 설정
            textView.setGravity(Gravity.CENTER); // 힌트를 중앙 정렬
            textView.setPadding(24, 4, 24, 4); // 힌트 텍스트의 여백 설정 (왼쪽, 위, 오른쪽, 아래)
            columnClueRow.addView(textView); // 힌트 TextView를 열 힌트 행에 추가
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
                Toast.makeText(this, "Wrong Guess!", Toast.LENGTH_SHORT).show(); // 알림 메시지
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
