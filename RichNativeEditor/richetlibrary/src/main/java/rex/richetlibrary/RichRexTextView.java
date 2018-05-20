package rex.richetlibrary;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static rex.richetlibrary.RichRexEditText.match;

/**
 * Created by Rex on 2018/5/15.
 * 只用于显示
 */

public class RichRexTextView extends TextView {


    public RichRexTextView(Context context) {
        super(context);
        initView(context);
    }


    public RichRexTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public RichRexTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }


    private void initView(Context context) {
    }

    /**
     * 设置带有自定义元素的网页
     */
    public void setCustomHtml(String html) {

        if (!html.contains("<") && !html.contains(">")) {
            setText(html);//普通文本
            return;
        }
        html = html.replaceAll("<p", "<span");
        html = html.replaceAll("</p>", "</span>");
        //p标签带多余的换行 我们改为span

        final String content = html;
        List<String> list = match(html, HtmlNewTagHandler.TAG_NEW_IMG, "src");

        if (list.size() > 0) {
            getHttpDrawAble(list, new RichRexEditText.loadStatusImpl() {   //专门处理TAG_NEW_IMG
                @Override
                public void finish(Map<String, Drawable> httpDraws) {
                    setText(Html.fromHtml(content,
                            null, new HtmlNewTagHandler((Activity) getContext(), httpDraws)));
                }
            });
        } else {
            setText(Html.fromHtml(content));
        }

    }

    private int load_num = 0;

    public void getHttpDrawAble(final List<String> list, final RichRexEditText.loadStatusImpl impl) {
        final Map<String, Drawable> httpDraws = new HashMap<String, Drawable>();
        load_num = 0;

        for (int i = 0; i < list.size(); i++) {
            final String source = list.get(i);
            Glide.with(getContext()).asDrawable().load(source).into(new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(Drawable d, Transition<? super Drawable> transition) {
                    httpDraws.put(source, d);
                    load_num++;
                    if (load_num > 0 && load_num == list.size()) {    //图片加载完毕
                        if (impl != null) {
                            impl.finish(httpDraws);
                        }

                    }
                }

            });
        }
    }
}
