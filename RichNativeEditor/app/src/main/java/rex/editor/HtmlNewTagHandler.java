package rex.editor;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.style.ImageSpan;
import android.widget.Toast;


import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rex on 2018/5/17.
 * 对html网页标签的自定义处理
 * 源码依据 Html 846行
 */

public class HtmlNewTagHandler implements Html.TagHandler {
    public static final String TAG_NEW_IMG = "custom_img";

    private int startIndex = 0;
    private int stopIndex = 0;
    final HashMap<String, String> attributes = new HashMap<String, String>();

    private Activity mContext;
    private Map<String, Drawable> httpDraws;

    public HtmlNewTagHandler(Activity context, Map<String, Drawable> httpDraws) {
        mContext = context;
        this.httpDraws = httpDraws;
    }

    @Override
    public void handleTag(boolean opening, String tag, Editable mSpannableStringBuilder, XMLReader mReader) {
        processAttributes(mReader);
        //该类型只有start处理
        //<img src="http://www.1honeywan.com/dachshund/image/7.21/7.21_3_thumb.JPG">
        if (tag.equalsIgnoreCase(TAG_NEW_IMG)) {//<>
            if (opening) {
                startHttpImg(mSpannableStringBuilder);
            }
        } else {//</>

        }
    }

    public void startHttpImg(Editable text) {
        String src = attributes.get("src");
        String url = src;
        int screenWidth = ScreenUtil.getScreenWidth() / 2;//占屏幕一半 根据需求调整

        Drawable d = httpDraws.get(url);
        if (d == null) {
            Toast.makeText(mContext, "图片预加载失败", Toast.LENGTH_SHORT).show();
        }
        float bili = d.getIntrinsicWidth() * 1.0f / d.getIntrinsicHeight();
        int w = screenWidth;
        int h = (int) (screenWidth / bili);
        d.setBounds(0, 0, w, h);
        int len = text.length();
        text.append("\uFFFC");
        text.setSpan(new ImageSpan(d, src), len, text.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


    }

    private void processAttributes(final XMLReader xmlReader) {
        try {
            Field elementField = xmlReader.getClass().getDeclaredField("theNewElement");
            elementField.setAccessible(true);
            Object element = elementField.get(xmlReader);
            Field attsField = element.getClass().getDeclaredField("theAtts");
            attsField.setAccessible(true);
            Object atts = attsField.get(element);
            Field dataField = atts.getClass().getDeclaredField("data");
            dataField.setAccessible(true);
            String[] data = (String[]) dataField.get(atts);
            Field lengthField = atts.getClass().getDeclaredField("length");
            lengthField.setAccessible(true);
            int len = (Integer) lengthField.get(atts);

            for (int i = 0; i < len; i++) {
                attributes.put(data[i * 5 + 1], data[i * 5 + 4]);
            }
        } catch (Exception e) {

        }
    }

}