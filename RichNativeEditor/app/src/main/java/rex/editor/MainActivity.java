package rex.editor;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import rex.richetlibrary.IClickableSpan;
import rex.richetlibrary.RichRexEditText;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String url = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1526816460218&di=a8f3226ab247e9a3b123d31739d38abf&imgtype=0&src=http%3A%2F%2Fi2.hdslb.com%2Fbfs%2Farchive%2Fc329b6cefe81198c16d7bdee88d84d6bd611fc30.jpg";
        RichRexEditText richRexEditText = findViewById(R.id.rich_et);
        richRexEditText.insertImage(url
                , new IClickableSpan("需要传输的数据") {
                    @Override
                    public void onCustomClick(View widget, String data) {
                        Toast.makeText(MainActivity.this, data, Toast.LENGTH_SHORT).show();
                    }
                });

        richRexEditText.setBold();//开启粗体 其他变色和粗体原理类似
        String htmlData = richRexEditText.getHtmlData();

    }
}
