package com.example.nonograms;

import static android.view.Gravity.CENTER;

import android.content.Context;
import android.graphics.Color;
import androidx.appcompat.widget.AppCompatButton;

import org.jetbrains.annotations.NotNull;

public class Cell extends AppCompatButton {
    private boolean blackSquare;  // 이 셀이 검은 사각형인지 여부를 저장
    private boolean checked;      // "X" 표기 여부를 저장

    // 생성자: Context를 받아 부모 클래스에 전달하고 초기 설정을 수행
    public Cell(@NotNull Context context) {
        super(context);
        init();  // 초기 설정을 위해 init() 메서드 호출
    }

    // 초기 설정 메서드: 셀의 배경, 텍스트 정렬, 크기 등을 설정
    private void init() {
        setBackgroundResource(R.drawable.cell_selector);  // 셀의 기본 배경 설정
        setGravity(CENTER); // 텍스트를 셀의 중앙에 위치하도록 설정
        setTextSize(18); // "X" 또는 "B" 텍스트의 크기 설정
        setTextColor(Color.BLACK); // 텍스트 색상 설정 (검정색)
        setPadding(0, 0, 0, 0); // 불필요한 여백 제거
    }

    // 셀을 검은 사각형으로 설정하는 메서드
    public void setBlackSquare(boolean blackSquare) {
        this.blackSquare = blackSquare;
    }

    // 검은 사각형을 발견하려고 시도하는 메서드
    public boolean markBlackSquare() {
        if (blackSquare) {  // 이 셀이 검은 사각형이라면
            setText("B");  // "B" 표시로 마킹
            setEnabled(false); // 클릭 불가 상태로 설정
            return true;  // 올바른 클릭이므로 true 반환
        } else {
            setText("X");  // 검은 사각형이 아니라면 "X"로 표시
            return false;  // 잘못된 클릭이므로 false 반환
        }
    }

    // "X" 표시를 토글하는 메서드
    public void toggleX() {
        checked = !checked;  // 현재 "X" 상태를 반전
        setText(checked ? "X" : "");  // "X"를 표시하거나 제거
    }

    // 이 셀이 검은 사각형인지 여부를 반환하는 메서드
    public boolean isBlackSquare() {
        return blackSquare;
    }
}