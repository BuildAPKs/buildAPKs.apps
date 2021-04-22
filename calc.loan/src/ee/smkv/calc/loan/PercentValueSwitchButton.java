package ee.smkv.calc.loan;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

public class PercentValueSwitchButton extends Button implements View.OnClickListener {
    private OnClickListener listener;
    private boolean percent = true;

    public PercentValueSwitchButton(Context context) {
        super(context);
        init();
    }

    public PercentValueSwitchButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PercentValueSwitchButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        changeIcon();
        super.setOnClickListener(this);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        listener = l;
    }

    private void changeIcon() {
        Drawable icon = percent ?
                getResources().getDrawable(R.drawable.percent) :
                getResources().getDrawable(R.drawable.value);
        setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
    }

    public void onClick(View view) {
        percent = !percent;
        changeIcon();
        if(listener!=null){
            listener.onClick(this);
        }
    }

    public boolean isPercent() {
        return percent;
    }

    public void setPercent(boolean percent) {
        this.percent = percent;
        changeIcon();
    }
}
