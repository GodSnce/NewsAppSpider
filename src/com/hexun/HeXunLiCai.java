package com.hexun;

import com.db.DBHelper;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
/**
 * 和讯理财数据获取
 * @author lishanglai
 *
 */
public class HeXunLiCai implements PageProcessor {

private Site site = Site.me().setRetryTimes(3).setSleepTime(2000);
	
	@Override
	public Site getSite() {
		return site;
	}

	@Override
	public void process(Page page) {
		//指定抓取html页面的符合此正则的所有链接
		page.addTargetRequests(page.getHtml().links().regex("(http://money.hexun.com/[^>]*.html)").all());
        page.putField("author", page.getUrl().regex("http://money.hexun.com/[^>]*.html").toString());
        //指定抓取好class属性为标签的子标签下的文本内容
        //标题
        page.putField("name", page.getHtml().xpath("//div[@class='articleName']/h1/text()").toString());
       
        if (page.getResultItems().get("name")==null){
            //skip this page
            page.setSkip(true);
        }
        //内容
        page.putField("readme", page.getHtml().xpath("//div[@class='art_contextBox']/tidyText()"));
        
        //来源
        page.putField("resource", page.getHtml().xpath("//div[@class='art_contextBox']/p/text()"));

        //图片链接http://i0.hexun.com/2017-08-30/190652015.jpg
        page.putField("img", page.getHtml().links().regex("(http://i0.hexun.com/[^>]*.jpg)").all());

        System.out.println("---------------------------------->" + page.getResultItems().get("name"));
        System.out.println("---------------------------------->" + page.getResultItems().get("resource"));
        System.out.println("---------------------------------->" + page. getResultItems().get("readme"));
        System.out.println("---------------------------------->" + page. getResultItems().get("img"));

        if (page.getResultItems().get("name") != null) {
        	String sql = "INSERT INTO hexunlicai SET url_hx='"+ page.getResultItems().get("author") +"',title_hx='"+page.getResultItems().get("name")+"',article_hx='"+page. getResultItems().get("readme")+"',source_hx='"+page.getResultItems().get("resource")+"',img_hx='"+page.getResultItems().get("img")+"'";
            DBHelper.insert(sql);
		}

	}
	
	public static void main(String[] args) {
        Spider.create(new HeXunLiCai()).addUrl("http://money.hexun.com/zxkd/index.html").thread(5).run();
    }
}
