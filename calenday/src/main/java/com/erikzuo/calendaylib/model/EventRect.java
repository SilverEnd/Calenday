package com.erikzuo.calendaylib.model;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.StyleSpan;

/**
 * Created by YifanZuo on 30/08/2016.
 */
public class EventRect {
    public Event event;
    public Rect eventBox;
    public Rect drawingBox;


    public EventRect(Event event, float left, float top, float right, float bottom) {
        this.event = event;

        this.eventBox = new Rect((int) left, (int) top, (int) right, (int) bottom);
        this.drawingBox = new Rect((int) left, (int) top, (int) right, (int) bottom);
        this.drawingBox.inset(1, 1);
    }

    public void drawTitle(Canvas canvas, TextPaint textPaint) {
        if (this.drawingBox.right - this.drawingBox.left < 0)
            return;

        if (this.drawingBox.bottom - this.drawingBox.top < 0)
            return;

        // Prepare the name of the event.
        SpannableStringBuilder builder = new SpannableStringBuilder();
        if (this.event.getTitle() != null) {
            builder.append(this.event.getTitle());
            builder.setSpan(new StyleSpan(Typeface.NORMAL), 0, builder.length(), 0);
            builder.append(' ');
        }


        int availableHeight = this.drawingBox.bottom - this.drawingBox.top;
        int availableWidth = this.drawingBox.right - this.drawingBox.left;

        // Get text dimensions.
        StaticLayout textLayout = new StaticLayout(builder, textPaint, availableWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f,
                false);

        int lineHeight = textLayout.getHeight() / textLayout.getLineCount();

        if (availableHeight >= lineHeight) {
            // Calculate available number of line counts.
            int availableLineCount = availableHeight / lineHeight;
            do {
                // Ellipsize text to fit into event rect.
                textLayout = new StaticLayout(TextUtils.ellipsize(builder, textPaint, availableLineCount * availableWidth,
                        TextUtils.TruncateAt.END), textPaint, availableWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

                // Reduce line count.
                availableLineCount--;

                // Repeat until text is short enough.
            } while (textLayout.getHeight() > availableHeight);

            // Draw text.
            canvas.save();
            canvas.translate(this.drawingBox.left, this.drawingBox.top);
            textLayout.draw(canvas);
            canvas.restore();
        }
    }
}