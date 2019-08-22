#该原生实现思路因为远远不如新方案 故此项目已废弃
请移步
https://github.com/RexSuper/RichEditor

# RichNativeEditor
Rich text editor implemented entirely with native code

完全使用android原生代码实现的富文本编辑框架

编辑----->生成数据记录------>传输数据 再现效果
1.可直接生成网页
2.很方便实现元素的自定义
3.自定义标签的解析 点击事件 
*使用了glide加载在线图片


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
