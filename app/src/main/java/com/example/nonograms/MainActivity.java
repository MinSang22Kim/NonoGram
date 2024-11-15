// src/main/java/com/example/nonograms/MainActivity.java

package com.example.nonograms;

import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private TableLayout tableLayout;
    private TextView lifeTextView;
    private ToggleButton toggleButton;
    private int lives = 3; // 남은 생명 수
    private int numBlackSquares; // 총 검은 사각형의 개수

    private ArrayList<int[]> rowClues; // 행 힌트를 저장하는 리스트
    private ArrayList<int[]> colClues; // 열 힌트를 저장하는 리스트
    private boolean[][] blackSquares;  // 검은 사각형 위치를 저장하는 2차원 배열

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI 요소 초기화
        tableLayout = findViewById(R.id.tableLayout);
        lifeTextView = findViewById(R.id.lifeTextView);
        toggleButton = findViewById(R.id.toggleButton);

        // 검은색 사각형 및 힌트 생성
        generateRandomBlackSquaresAndClues();
        initBoard(); // 보드 초기화
        displayClues(); // 힌트 표시
        updateLifeText(); // 생명 수 업데이트
    }

    // 생명 텍스트를 업데이트
    private void updateLifeText() {
        lifeTextView.setText("Life: " + lives);
    }

    // 검은색 사각형과 힌트를 무작위로 생성
    private void generateRandomBlackSquaresAndClues() {
        int size = 8;
        blackSquares = new boolean[size][size];
        rowClues = new ArrayList<>();
        colClues = new ArrayList<>();
        numBlackSquares = 0;

        Random random = new Random();

        // 검은색 사각형 위치를 무작위로 결정
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                blackSquares[i][j] = random.nextBoolean();
                if (blackSquares[i][j]) numBlackSquares++; // 검은 사각형 개수 증가
            }
        }

        // 행 힌트 생성
        for (int i = 0; i < size; i++) {
            ArrayList<Integer> clues = new ArrayList<>();
            int count = 0;
            for (int j = 0; j < size; j++) {
                if (blackSquares[i][j]) {
                    count++;
                } else if (count > 0) {
                    clues.add(count);
                    count = 0;
                }
            }
            if (count > 0) clues.add(count); // 마지막 블록 힌트 추가
            rowClues.add(clues.stream().mapToInt(Integer::intValue).toArray());
        }

        // 열 힌트 생성
        for (int j = 0; j < size; j++) {
            ArrayList<Integer> clues = new ArrayList<>();
            int count = 0;
            for (int i = 0; i < size; i++) {
                if (blackSquares[i][j]) {
                    count++;
                } else if (count > 0) {
                    clues.add(count);
                    count = 0;
                }
            }
            if (count > 0) clues.add(count); // 마지막 블록 힌트 추가
            colClues.add(clues.stream().mapToInt(Integer::intValue).toArray());
        }
    }

    // 게임 보드 초기화
    private void initBoard() {
        tableLayout.removeAllViews();
        int cellSize = 70; // 셀 크기 설정

        for (int i = 0; i < 8; i++) {
            TableRow row = new TableRow(this);
            TableRow.LayoutParams rowParams = new TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(rowParams);

            for (int j = 0; j < 8; j++) {
                Cell cell = new Cell(this);
                cell.setLayoutParams(new TableRow.LayoutParams(cellSize, cellSize));
                cell.setBlackSquare(blackSquares[i][j]);  // 셀에 검은 사각형 설정
                cell.setOnClickListener(new CellClickListener(cell, i, j));
                row.addView(cell); // 셀을 행에 추가
            }
            tableLayout.addView(row); // 행을 보드에 추가
        }
    }

    // 힌트를 보드에 표시
    private void displayClues() {
        // 행 힌트를 왼쪽에 표시
        LinearLayout rowClueContainer = findViewById(R.id.rowClueContainer);
        rowClueContainer.removeAllViews();
        for (int[] rowClue : rowClues) {
            TextView textView = new TextView(this);
            StringBuilder clueText = new StringBuilder();
            for (int clue : rowClue) {
                clueText.append(clue).append(" ");
            }
            textView.setText(clueText.toString().trim());
            textView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END); // 오른쪽 정렬
            textView.setPadding(8, 8, 8, 8); // 여백 설정
            rowClueContainer.addView(textView);
        }

        // 열 힌트를 위쪽에 표시
        TableRow columnClueRow = findViewById(R.id.columnClueRow);
        columnClueRow.removeAllViews();
        for (int[] colClue : colClues) {
            TextView textView = new TextView(this);
            StringBuilder clueText = new StringBuilder();
            for (int clue : colClue) {
                clueText.append(clue).append("\n");
            }
            textView.setText(clueText.toString().trim());
            textView.setPadding(8, 8, 8, 8); // 여백 설정
            columnClueRow.addView(textView);
        }
    }

    // 셀 클릭 리스너 클래스 정의
    private class CellClickListener implements View.OnClickListener {
        private final Cell cell;
        private final int row;
        private final int col;

        CellClickListener(Cell cell, int row, int col) {
            this.cell = cell;
            this.row = row;
            this.col = col;
        }

        @Override
        public void onClick(View v) {
            if (toggleButton.isChecked()) {
                cell.toggleX();  // "X" 표시 토글
            } else {
                if (cell.markBlackSquare()) { // 검은 사각형을 맞췄을 경우
                    numBlackSquares--;
                    if (numBlackSquares == 0) {
                        gameOver(true);  // 모든 검은 사각형을 찾으면 승리
                    }
                } else {
                    lives--;
                    updateLifeText(); // 생명 수 업데이트
                    if (lives == 0) {
                        gameOver(false);  // 생명이 0이면 게임 오버
                    }
                }
            }
        }
    }

    // 게임 종료 처리
    private void gameOver(boolean won) {
        String message = won ? "You won!" : "Game Over!";
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        disableAllCells(); // 게임 종료 시 모든 셀 비활성화
    }

    // 모든 셀 비활성화
    private void disableAllCells() {
        for (int i = 0; i < tableLayout.getChildCount(); i++) {
            View row = tableLayout.getChildAt(i);
            if (row instanceof TableRow) {
                for (int j = 0; j < ((TableRow) row).getChildCount(); j++) {
                    ((TableRow) row).getChildAt(j).setEnabled(false);
                }
            }
        }
    }
}