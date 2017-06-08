package Test;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.*;


/**
 *
 * Created by rzx on 2017/6/6.
 */
public class Highlight {
    // 高亮處理文本（以下内容纯属虚构）
    private static String text = "China has lots of people,most of them are very poor.China is very big.China become strong now,but the poor people is also poor than other controry";

     /**
     * 使用已建立的索引检索指定的关键词，并高亮关键词后返回结果
     * @param indexFilePath 索引文件路径
     * @param keyword 检索关键词
     */
    public static void searchByIndex(String indexFilePath,String keyword) throws ParseException, InvalidTokenOffsetsException {
        try {
            String indexDataPath="testindex";
            String keyWord = "main";
            Directory dir= FSDirectory.open(new File(indexDataPath).toPath());
            IndexReader reader= DirectoryReader.open(dir);
            IndexSearcher searcher=new IndexSearcher(reader);
            QueryParser queryParser = new QueryParser("contents",new StandardAnalyzer());
            Query query = queryParser.parse("main");

            TopDocs topdocs=searcher.search(query,10);
            ScoreDoc[] scoredocs=topdocs.scoreDocs;
            System.out.println("最大的评分:"+topdocs.getMaxScore());
            for(int i=0;i<scoredocs.length;i++){
                int doc=scoredocs[i].doc;
                Document document=searcher.doc(doc);
                System.out.println("====================文件【"+(i+1)+"】=================");
                System.out.println("检索关键词："+keyWord);
                System.out.println("文件路径:"+document.get("path"));
                System.out.println("文件ID:"+scoredocs[i].doc);
				//开始高亮
                SimpleHTMLFormatter formatter=new SimpleHTMLFormatter("<b><font color='red'>","</font></b>");
                Highlighter highlighter=new Highlighter(formatter, new QueryScorer(query));
                highlighter.setTextFragmenter(new SimpleFragmenter(400));
                String conten =  highlighter.getBestFragment(new StandardAnalyzer(),"contents","hello main man test");
                //String conten =  highlighter.getBestFragment(new StandardAnalyzer(),"contents",document.get("contents"));

                System.out.println("文件内容:"+conten);
                System.out.println("匹配相关度："+scoredocs[i].score);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws ParseException, IOException, InvalidTokenOffsetsException {
         Highlight.searchByIndex(null,null);
    }

}
