package com.example.nonograms;

import android.content.Context;

import androidx.appcompat.widget.AppCompatButton;

public class Cell extends AppCompatButton {

    private final boolean blackSquare; // 검은 사각형 여부
    private boolean markedAsX;

    public Cell(Context context, boolean isBlackSquare) {
        super(context);
        this.blackSquare = isBlackSquare;
        this.markedAsX = false;
        setBackgroundResource(R.drawable.cell_selector); // 초기 셀 스타일 설정
    }

    public boolean markBlackSquare() {
        if (!markedAsX && blackSquare) {
            setEnabled(false); // 클릭 비활성화
            return true;
        }
        return false;
    }

    public void toggleX() {
        if (!isEnabled()) return;
        markedAsX = !markedAsX;
        setText(markedAsX ? "X" : "");
    }
}
