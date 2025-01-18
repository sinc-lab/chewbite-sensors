package com.android.chewbiteSensors.ui.audio;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.LinkedList;
import java.util.Queue;

public class AudioWaveformView extends View {

    private static final int BAR_WIDTH = 10;
    private static final int BAR_GAP = 5;

    private int maxVisibleBars;
    private Paint barPaint, timePaint, guideLinePaint;
    private Queue<Float> amplitudes;
    private Queue<Long> timestamps;
    private long startTime;

    public AudioWaveformView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // Configurar el paint de las barras
        barPaint = new Paint();
        barPaint.setColor(Color.parseColor("#FF3700B3"));
        barPaint.setStyle(Paint.Style.FILL);

        // Configurar el paint del tiempo
        timePaint = new Paint();
        timePaint.setColor(Color.BLACK);
        timePaint.setTextSize(30);

        // Configurar el paint de las líneas de guía
        guideLinePaint = new Paint();
        guideLinePaint.setColor(Color.GRAY);
        guideLinePaint.setStyle(Paint.Style.STROKE);
        guideLinePaint.setStrokeWidth(1);
        guideLinePaint.setAlpha(100);

        amplitudes = new LinkedList<>();
        timestamps = new LinkedList<>();
        startTime = System.currentTimeMillis();
    }

    public void addAmplitude(float amplitude) {
        long currentTime = System.currentTimeMillis() - startTime;

        // Eliminar elementos fuera del rango visible
        while (amplitudes.size() >= maxVisibleBars) {
            amplitudes.poll();
            timestamps.poll();
        }

        amplitudes.add(amplitude);
        timestamps.add(currentTime);
        invalidate(); // Solicitar redibujar la vista
    }

    public void clearAmplitudes() {
        amplitudes.clear();
        invalidate(); // Redibujar la vista para reflejar el cambio
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawGuideLines(canvas); // Dibujar líneas de guía
        drawWaveform(canvas);   // Dibujar barras y tiempos
    }

    private void drawGuideLines(Canvas canvas) {
        int totalHorizontalLines = 10; // Número de líneas verticales
        float spacingY = getHeight() / (float) totalHorizontalLines; // Espaciado vertical

        // Dibujar líneas verticales
        for (int i = 1; i < totalHorizontalLines; i++) {
            float y = i * spacingY; // Posición vertical de la línea
            canvas.drawLine(0, y, getWidth(), y, guideLinePaint); // Dibujar línea vertical
        }
    }

    /**
     * Dibuja la forma de onda de audio en el lienzo.
     * @param canvas
     */
    private void drawWaveform(Canvas canvas) {
        float width = getWidth(); // Ancho del lienzo
        float height = getHeight(); // Altura del lienzo
        float centerY = height / 2; // Centro vertical del lienzo

        int i = 0; // Índice para recorrer las amplitudes
        long previousSecond = -1; // Último segundo procesado

        // Recorrer las amplitudes y timestamps
        for (Float amplitude : amplitudes) {
            float barHeight = amplitude / 32768f * height; // Altura de la barra
            float x = i * (BAR_WIDTH + BAR_GAP); // Posición horizontal de la barra

            if (x > width) break;

            // Dibujar barra con esquinas redondeadas
            float left = x; // Posición horizontal izquierda de la barra
            float top = centerY - barHeight; // Posición vertical superior de la barra
            float right = x + BAR_WIDTH; // Posición horizontal derecha de la barra
            float bottom = centerY + barHeight; // Posición vertical inferior de la barra
            float cornerRadius = 5; // Radio de las esquinas de la barra
            canvas.drawRoundRect(left, top, right, bottom, cornerRadius, cornerRadius, barPaint); // Dibujar barra

            // Dibujar tiempo si es necesario
            long timestamp = timestamps.toArray(new Long[0])[i]; // Obtener el timestamp correspondiente
            long seconds = timestamp / 1000; // Convertir a segundos

            // Dibujar tiempo si es necesario
            if (seconds != previousSecond && x > 0) {
                previousSecond = seconds; // Actualizar el último segundo procesado

                String text = String.valueOf(seconds); // Convertir segundos a cadena
                float textWidth = timePaint.measureText(text); // Obtener el ancho del texto
                float xText = x - textWidth / 2 + BAR_WIDTH / 2; // Posición horizontal del texto
                float yText = height - 10; // Posición vertical del texto
                canvas.drawText(text, xText, yText, timePaint); // Dibujar el texto

                // Dibujar línea de guía vertical
                canvas.drawLine(x + BAR_WIDTH / 2, 0, x + BAR_WIDTH / 2, height, guideLinePaint); // Dibujar línea vertical
            }

            i++; // Incrementar el índice
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        maxVisibleBars = w / (BAR_WIDTH + BAR_GAP);
   }
}
