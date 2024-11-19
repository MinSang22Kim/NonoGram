package com.example.nonograms;

import static android.view.Gravity.CENTER;

import android.content.Context;
import android.graphics.Color;

import androidx.appcompat.widget.AppCompatButton;

public class Cell extends AppCompatButton {

    private final boolean blackSquare; // 정답으로 지정된 검은 사각형 여부
    private boolean markedAsX;         // "X"로 표시 여부

    public Cell(Context context, boolean isBlackSquare) {
        super(context);
        this.blackSquare = isBlackSquare;                // 셀의 정답 여부 설정
        this.markedAsX = false;                          // 초기에는 "X" 표시 없음
        setBackgroundResource(R.drawable.cell_selector); // 초기 셀 스타일 설정
        setTextColor(Color.BLACK);                       // 텍스트 색상을 검정으로 고정
        setGravity(CENTER);                              // 텍스트 중앙 정렬
        setTextSize(18);                                 // 텍스트 크기 설정
    }

    public boolean markBlackSquare() {
        if (!markedAsX && blackSquare) {             // "X"로 표시되지 않았고 정답인 경우
            setEnabled(false);                       // 클릭 비활성화
            return true;                             // 정답임을 반환
        }
        return false;                                // 정답이 아님을 반환
    }

    public void toggleX() {
        if (!isEnabled()) return;                   // 셀이 비활성화된 경우 무시
        markedAsX = !markedAsX;                     // "X" 표시 토글
        setText(markedAsX ? "X" : "");              // "X" 표시하거나 제거
    }
}
