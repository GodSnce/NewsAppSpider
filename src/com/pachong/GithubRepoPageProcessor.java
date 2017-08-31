package com.pachong;

import com.db.DBHelper;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

public class GithubRepoPageProcessor implements PageProcessor {

	private Site site = Site.me().setRetryTimes(3).setSleepTime(2000);
	
	@Override
	public Site getSite() {
		return site;
	}

	@Override
	public void process(Page page) {
		//指定抓取html页面的符合此正则的所有链接
		page.addTargetRequests(page.getHtml().links().regex("(http://www.jiedai.cn/news/[^>]*.html)").all());
        page.putField("author", page.getUrl().regex("http://www.jiedai.cn/news/[^>]*.html").toString());
        //指定抓取好class属性为标签的子标签下的文本内容
        page.putField("name", page.getHtml().xpath("//div[@class='article-title']/h1/text()").toString());
       
        if (page.getResultItems().get("name")==null){
            //skip this page
            page.setSkip(true);
        }
        page.putField("readme", page.getHtml().xpath("//div[@class='article-content']/tidyText()"));
        
        page.putField("resource", page.getHtml().xpath("//p[@class='origin clearfix']/a/text()"));

        page.putField("img", page.getHtml().links().regex("(http://img.jiedai.cn/article/[^>]*.jpg)").all());
        
        String[] s2 = null;

        if (s2 != null) {
        	String sql = "INSERT INTO jiedaiwang SET url_jdw='"+ page.getResultItems().get("author") +"',title_jdw='"+page.getResultItems().get("name")+"',article_jdw='"+s2[0]+"',source_jdw='"+page.getResultItems().get("resource")+"',img_jdw='"+page.getResultItems().get("img")+"'";
            DBHelper.insert(sql);
		}
	}
	
	public static void main(String[] args) {
        Spider.create(new GithubRepoPageProcessor()).addUrl("http://www.jiedai.cn/news/").thread(5).run();
    }

}
