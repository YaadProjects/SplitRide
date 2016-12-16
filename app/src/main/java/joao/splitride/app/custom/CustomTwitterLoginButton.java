package joao.splitride.app.custom;

import android.content.Context;
import android.util.AttributeSet;

import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import joao.splitride.R;

/**
 * Created by joaoferreira on 15/12/16.
 */

public class CustomTwitterLoginButton extends TwitterLoginButton {

    public CustomTwitterLoginButton(Context context) {
        super(context);
        init();
    }

    public CustomTwitterLoginButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomTwitterLoginButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        if (isInEditMode()) {
            return;
        }
        setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.twitter), null, null, null);
        //setBackgroundResource(R.drawable.);
        setTextSize(20);
        setPadding(30, 0, 10, 0);
        setTextColor(getResources().getColor(R.color.tw__blue_default));
        //setTypeface(App.getInstance().getTypeface());
    }
}
