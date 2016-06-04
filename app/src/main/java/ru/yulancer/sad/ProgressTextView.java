package ru.yulancer.sad;


import android.content.Context;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.Locale;

public class ProgressTextView extends TextView {
    // Максимальное значение шкалы
    private int mMaxValue = 100;
    private int mValue = 0;

    // Конструкторы
    public ProgressTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ProgressTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProgressTextView(Context context) {
        super(context);
    }

    // Установка максимального значения
    public void setMaxValue(int maxValue) {
        mMaxValue = maxValue;
    }

    public int getMaxValue() {
        return mMaxValue;
    }

    // Установка значения
    public synchronized void setValue(int value) {
        // Установка новой надписи
        String newText = String.format(Locale.getDefault(), "%d из %d", value, mMaxValue);
        this.setText(newText);

        // Drawable, отвечающий за фон
        LayerDrawable background = (LayerDrawable) this.getBackground();

        // Достаём Clip, отвечающий за шкалу, по индексу 1
        ClipDrawable barValue = (ClipDrawable) background.getDrawable(1);

        // Устанавливаем уровень шкалы
        int newClipLevel = (int) (mMaxValue == 0 ? 0 : (value * 10000 / mMaxValue));
        barValue.setLevel(newClipLevel);

        // Уведомляем об изменении Drawable
        drawableStateChanged();
    }

    public int getValue() {
        return mValue;
    }
}