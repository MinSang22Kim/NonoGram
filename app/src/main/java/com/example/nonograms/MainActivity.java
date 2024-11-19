package com.example.nonograms;

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

    // UI 요소 및 게임 상태 변수 선언
    private TableLayout tableLayout;          // 게임 보드
    private LinearLayout rowClueContainer;    // 행 힌트 컨테이너
    private LinearLayout columnClueContainer; // 열 힌트 컨테이너
    private TextView lifeTextView;            // 남은 생명 텍스트뷰
    private ToggleButton toggleButton;        // 토글 버튼
    private Button resetButton;               // 리셋 버튼
    private int lives = 3;                    // 플레이어 생명
    private boolean[][] blackSquares;         // 정답으로 지정된 검은 사각형 위치
    private int numBlackSquares;              // 정답으로 남아있는 검은 사각형 수
    private final int size = 5;               // 보드 크기
    private ArrayList<int[]> rowClues;        // 행 힌트 숫자 배열
    private ArrayList<int[]> colClues;        // 열 힌트 숫자 배열

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

        // 리셋 버튼 클릭 이벤트 설정
        resetButton.setOnClickListener(v -> resetGame());

        // 초기 게임 세팅
        resetGame();
    }

    private void resetGame() {
        lives = 3;                              // 플레이어 생명 초기화
        updateLifeText();                       // 생명 표시 업데이트
        blackSquares = new boolean[size][size]; // 새로운 정답 배열 생성
        generateRandomBlackSquares();           // 랜덤으로 검은 사각형 생성
        generateClues();                        // 힌트 데이터 생성
        displayClues();                         // UI에 힌트 표시
        initBoard();                            // 보드 초기화
        toggleButton.setEnabled(true);          // 토글 버튼 활성화
    }

    private void generateRandomBlackSquares() {
        Random random = new Random();
        numBlackSquares = 0;                               // 남은 검은 사각형 수 초기화
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                blackSquares[i][j] = random.nextBoolean(); // 랜덤으로 검은 사각형 결정
                if (blackSquares[i][j]) numBlackSquares++; // 검은 사각형 개수 증가
            }
        }
    }

    private void generateClues() {
        rowClues = new ArrayList<>(); // 행 힌트 초기화
        colClues = new ArrayList<>(); // 열 힌트 초기화

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
                count++;            // 연속된 검은 사각형 수 증가
            } else if (count > 0) {
                clues.add(count);   // 연속된 검은 사각형 수 추가
                count = 0;
            }
        }

        // 마지막 클루 추가
        if (count > 0) clues.add(count);

        // 검은 사각형이 없는 경우 0으로 표시
        if (clues.isEmpty()) {
            clues.add(0);
        }

        return clues.stream().mapToInt(Integer::intValue).toArray();
    }

    private boolean[] getColumn(int colIndex) {
        boolean[] column = new boolean[size];
        for (int i = 0; i < size; i++) {
            column[i] = blackSquares[i][colIndex]; // 열 데이터 추출
        }
        return column;
    }

    private void displayClues() {
        // 행 힌트 숫자 표시
        rowClueContainer.removeAllViews();
        for (int[] rowClue : rowClues) {
            TextView textView = new TextView(this);
            StringBuilder clueText = new StringBuilder();
            clueText.append("\n");
            for (int clue : rowClue) {
                clueText.append(clue).append(" "); // 행 힌트 데이터 추가
            }

            textView.setText(clueText.toString().trim());
            textView.setTextSize(16);
            textView.setGravity(Gravity.END);
            textView.setPadding(4, 38, 4, 38);

            rowClueContainer.addView(textView);
        }

        // 열 힌트 표시
        columnClueContainer.removeAllViews();
        for (int[] colClue : colClues) {
            TextView textView = new TextView(this);
            StringBuilder clueText = new StringBuilder();
            for (int clue : colClue) {
                clueText.append(clue).append("\n");
            }

            textView.setText(clueText.toString().trim());
            textView.setTextSize(16);
            textView.setGravity(Gravity.BOTTOM);
            textView.setPadding(65, 4, 65, 4);

            columnClueContainer.addView(textView);
        }
    }

    private void initBoard() {
        tableLayout.removeAllViews(); // 기존 보드 제거

        int cellSize = 145; // 셀 크기 고정

        for (int i = 0; i < size; i++) {
            TableRow row = new TableRow(this);
            for (int j = 0; j < size; j++) {
                Cell cell = new Cell(this, blackSquares[i][j]); // 새로운 셀 생성
                TableRow.LayoutParams params = new TableRow.LayoutParams(cellSize, cellSize);
                cell.setLayoutParams(params);
                int finalI = i, finalJ = j;
                cell.setOnClickListener(v -> handleCellClick(cell, finalI, finalJ)); // 셀 클릭 이벤트
                row.addView(cell);
            }
            tableLayout.addView(row); // 보드에 행 추가
        }
    }

    private void handleCellClick(Cell cell, int row, int col) {
        if (toggleButton.isChecked()) {
            cell.toggleX();              // "X" 표시 토글
        } else {
            if (cell.markBlackSquare()) {
                numBlackSquares--;       // 남은 검은 사각형 감소
                cell.setBackgroundResource(R.drawable.cell_pressed_shape); // 클릭 시 셀 배경색 변경
                if (numBlackSquares == 0) {
                    gameOver(true); // 승리 조건
                }
            } else {
                lives--;            // 생명 감소
                cell.toggleX();     // "X" 표시
                Toast.makeText(this, "Wrong Guess!", Toast.LENGTH_SHORT).show();
                updateLifeText();   // 생명 표시 업데이트
                if (lives == 0) {
                    gameOver(false); // 패배 조건
                }
            }
        }
    }

    // 생명 개수 업데이트
    private void updateLifeText() {
        lifeTextView.setText("Life: " + lives);
    }

    // 게임 종료 시 셀과 토글 버튼 비활성화(Reset 버튼 제외)
    private void gameOver(boolean won) {
        String message = won ? "You Won!" : "Game Over!";
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        disableAllCells();              // 모든 셀 비활성화
        toggleButton.setEnabled(false); // 토글 버튼 비활성화
    }

    // 모든 셀을 순회하며 하나씩 비활성화
    private void disableAllCells() {
        for (int i = 0; i < tableLayout.getChildCount(); i++) {
            TableRow row = (TableRow) tableLayout.getChildAt(i);
            for (int j = 0; j < row.getChildCount(); j++) {
                row.getChildAt(j).setEnabled(false); // 하나의 셀 비활성화
            }
        }
    }
}
