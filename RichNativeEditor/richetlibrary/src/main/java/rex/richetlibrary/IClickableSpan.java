package rex.richetlibrary;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

/**
 * Created by Rex on 2018/5/17.
 * 自定义带数据的span点击事件
 */

public abstract class IClickableSpan extends ClickableSpan {


    private String data;

    public IClickableSpan(String data) {
        this.data = data;
    }

    @Override
    public void onClick(View widget) {
        onCustomClick(widget, data);
    }

    public abstract void onCustomClick(View widget, String data);

    @Override
    public void updateDrawState(@NonNull TextPaint ds) {
        super.updateDrawState(ds);
        ds.setColor(Color.RED);
        ds.setUnderlineText(false);
        ds.clearShadowLayer();
    }

}
