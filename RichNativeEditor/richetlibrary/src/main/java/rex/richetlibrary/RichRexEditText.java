package rex.richetlibrary;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Rex on 2018/5/15.
 */

public class RichRexEditText extends EditText {

    public boolean is2ndChanged = false;//该属性非常重要，不使用会造成死循环

    public RichRexEditText(Context context) {
        super(context);
        init(context);
    }


    public RichRexEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RichRexEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }



    /**
     * 显示与编辑的转化
     *
     * @param isCanEdit
     */
    public void setEdit(boolean isCanEdit) {
        setCursorVisible(isCanEdit);
//        setFocusable(isCanEdit);//会导致点击事件无效
//        setFocusableInTouchMode(isCanEdit);
    }



    public String getHtmlData() {
        return Html.toHtml(getText());
    }

    private void init(Context context) {

        addTextChangedListener(new TextWatcher() {
            private CharSequence beforeTextChangedStr = "";
            private CharSequence onTextChangedStr = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                beforeTextChangedStr = s;
            }


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (is2ndChanged) {//处理文本处理中导致的onTextChanged
                    is2ndChanged = false;
                    return;
                }
                //如果before大于0则代表删除的内容
                if (count > 0) {//count部分即为新增的str 此时已经添加到了et里面
                    CharSequence charSequence = s.subSequence(start, start + count);
                    if (isBold) {//如果加粗模式开启

                        SpannableString spannableString = new SpannableString(charSequence);
                        /**
                         * SPAN_EXCLUSIVE_EXCLUSIVE(前后都不包括)
                         * Spanned.SPAN_INCLUSIVE_INCLUSIVE：在开始或结尾处处插入新内容时，会与原来的SpannableString混合在一起，组成一个新的SpannableString
                         */
                        spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
                                0, charSequence.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); //加粗模式下输入的文字 均加粗

                        //后续功能：加个颜色等功能
                        //spannableString.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

//                        if (clickableSpan != null) {
//                            spannableString.setSpan(clickableSpan, 0, charSequence.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                        }


                        int pStart = getSelectionStart();//获取光标位置

                        needChange();//阻止再次onTextChanged

                        getText().delete(pStart - count, pStart);//删除输入未加粗数据

                        needChange();

                        int pStart2 = getSelectionStart();//获取光标位置

                        getText().insert(pStart2, spannableString);//插入加粗后的数据


                    }
                }

                if (before > 0) {//往前删除
                    //系统默认处理不会影响span
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private ClickableSpan clickableSpan = new ClickableSpan() {
        @Override
        public void onClick(View widget) {
            //Do something.
            Toast.makeText(getContext(), "点击生效", Toast.LENGTH_LONG).show();
        }


    };

    public void needChange() {
        is2ndChanged = true;//阻止再次onTextChanged
    }

    /**
     * 插入一段加粗文字
     *
     * @param change
     */
    public void insertBoldText(String change) {
        if (isBold) {
            is2ndChanged = true;
//            String fromat_text = "<special>{\"bold\":\"" + change + "\"}<special>";
            String fromat_text = change;
            SpannableString spannableString = new SpannableString(fromat_text);

            spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            int pStart = getSelectionStart();//获取光标位置
            getText().insert(pStart, spannableString);//
        } else {
            int pStart = getSelectionStart();//获取光标位置
            getText().insert(pStart, change);//
        }


    }

    /**
     * 插入一段文字
     *
     * @param where
     * @param text
     */
    public void insertStr(int where, CharSequence text) {
        is2ndChanged = true;
        try {
            getText().insert(where, text);
        } catch (Exception e) {
            getText().append(text);
        }

    }

    /**
     * 插入一个在线图片
     *
     * @param imgPath
     */
    public void insertImage(final String imgPath) {
        insertImage(imgPath, null);
    }


    /**
     * @param imgPath       图片地址
     * @param clickableSpan 点击事件
     */
    public void insertImage(final String imgPath, final IClickableSpan clickableSpan) {
        is2ndChanged = true;
        getText().append("\n");

        Glide.with(getContext()).asDrawable().load(imgPath).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(Drawable drawable, Transition<? super Drawable> transition) {
                //图片过大的话 此处进行压缩
                float bili = drawable.getIntrinsicHeight() * 1.0f / drawable.getIntrinsicWidth();
                int height = (int) (getWidth() * bili);
                /**
                 * 1.getText.toString可以得到图片对应部分会显示
                 * 2.删除图片会删除这一串
                 * 3.做表情的话可以用这个比较方便
                 *
                 */
                String fromat_imgPath = "Custom text messages for ‘getText.toString’";   // edittext内部记录

                SpannableString spannableString = new SpannableString(fromat_imgPath);
                drawable.setBounds(0, 0, getWidth() / 2, height / 2);

                /**
                 * 1.drawable实际显示图片
                 * 2.imgPath是内部记录  span.getSource() 或者转html的时候src可以得到
                 */
                ImageSpan span = new ImageSpan(drawable, imgPath);
                is2ndChanged = true;
                spannableString.setSpan(span, 0, fromat_imgPath.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                // 注释4.1效果前后都不包括  例如word编辑，粗体后面继续打字 还是粗体
                // 注释4.2 但中间是包括的也就是一串粗体中间插入新的文字

                if (clickableSpan != null) {
                    spannableString.setSpan(clickableSpan, 0, fromat_imgPath.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                int start = getSelectionStart();//
                getText().insert(start, spannableString);
                setMovementMethod(LinkMovementMethod.getInstance());

                //按照需求加一行换行
                setSelection(getText().length());
                getText().append("\n");
                setSelection(getText().length());
                //让点击事件生效


                setFocusable(true);
                setFocusableInTouchMode(true);
                requestFocus();//获取焦点 光标出现
            }


        });

    }

    private boolean isBold = false;

    /**
     * 调整是否加粗
     */
    public void setBold() {
        isBold = !isBold;
    }

    public void setBold(boolean is) {
        isBold = is;
    }

    public boolean isBold() {
        return isBold;
    }

    /**
     * 获取指定的span
     * getSpanStart--getSpanEnd 获取具体范围
     *
     * @param type
     * @param <T>
     * @return
     */
    public <T> T[] getEditSpans(Class<T> type) {
        final Editable editable = getText();
        T[] words = editable.getSpans(0, getText().length(), type);
        Arrays.sort((T[]) words, new Comparator<T>() {
            @Override
            public int compare(T arg0, T arg1) {
                int index0 = editable.getSpanStart(arg0);
                int index1 = editable.getSpanStart(arg1);
                return index0 == index1 ? 0 : (index0 > index1 ? 1 : -1);
            }
        });
        return words;
    }

    /**
     * 获取指定HTML标签的指定属性的值    * @param source 要匹配的源文本    * @param element 标签名称    * @param attr 标签的属性名称    * @return 属性值列表
     */
    public static List<String> match(String source, String element, String attr) {
        List<String> result = new ArrayList<String>();
        String reg = "<" + element + "[^<>]*?\\s" + attr + "=['\"]?(.*?)['\"]?(\\s.*?)?>";
        Matcher m = Pattern.compile(reg).matcher(source);
        while (m.find()) {
            String r = m.group(1);
            result.add(r);
        }
        return result;
    }

    private int load_num = 0;

    public void getHttpDrawAble(final List<String> list, final loadStatusImpl impl) {
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


    public interface loadStatusImpl {
        void finish(Map<String, Drawable> httpDraws);
    }
}
