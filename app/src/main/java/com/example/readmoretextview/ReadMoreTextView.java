package com.example.readmoretextview;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

/**
 * created by Ramanuj Kesharawani on 27/7/19
 */
public class ReadMoreTextView extends android.support.v7.widget.AppCompatTextView {

    private final String readMoreText = "...read more";
    private final String readLessText = "...show less";
    private int _maxLines = 4;
    private CharSequence originalText = null;
    private boolean isTextExpandable = false;

    public ReadMoreTextView(Context context) {
        super(context);
        init(context);
    }

    public ReadMoreTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ReadMoreTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {

        ViewTreeObserver vto = getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {

                ViewTreeObserver obs = getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);

                truncateText();
            }
        });
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);

        if (originalText == null) {
            originalText = text;
        }
    }

    @Override
    public int getMaxLines() {
        return _maxLines;
    }

    @Override
    public void setMaxLines(int maxLines) {
        _maxLines = maxLines;
    }

    public void truncateText() {

        int maxLines = _maxLines;
        String text = getText().toString();

        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });

        if (getLineCount() > maxLines) {
            isTextExpandable = true;

            int lineEndIndex = getLayout().getLineEnd(maxLines - 1);

            String truncatedText = getText().subSequence(0, lineEndIndex - readMoreText.length() + 1).toString();
            setText(truncatedText);

            SpannableString spannableString = makeLinkSpan(readMoreText, new OnClickListener() {
                @Override
                public void onClick(View v) {
                    expandText();
                }
            });
            append(spannableString);
            makeLinksFocusable(this);

        }
    }

    public void expandText() {
        setText(originalText);
        SpannableString spannableString = makeLinkSpan(readLessText, new OnClickListener() {
            @Override
            public void onClick(View v) {
                setMaxLines(_maxLines);
                truncateText();
                Log.d("less", "clicked");
            }
        });
        append(spannableString);
        super.setMaxLines(1000);
    }

    public void reset() {
        originalText = null;
    }

    public boolean isTextExpandable() {
        return isTextExpandable;
    }

    private SpannableString makeLinkSpan(CharSequence text, OnClickListener listener) {
        SpannableString link = new SpannableString(text);
        link.setSpan(new ClickableString(listener), 0, text.length(),
                SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
        return link;
    }

    private void makeLinksFocusable(TextView tv) {
        MovementMethod m = tv.getMovementMethod();
        if ((m == null) || !(m instanceof LinkMovementMethod)) {
            if (tv.getLinksClickable()) {
                tv.setMovementMethod(LinkMovementMethod.getInstance());
            }
        }
    }

    private static class ClickableString extends ClickableSpan {
        private OnClickListener mListener;

        public ClickableString(OnClickListener listener) {
            mListener = listener;
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(Color.BLUE);
            ds.setUnderlineText(false);
        }


    }

}
