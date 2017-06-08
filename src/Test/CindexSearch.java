package Test;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ar.ArabicAnalyzer;
import org.apache.lucene.analysis.bg.BulgarianAnalyzer;
import org.apache.lucene.analysis.br.BrazilianAnalyzer;
import org.apache.lucene.analysis.ca.CatalanAnalyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.core.*;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.eu.BasqueAnalyzer;
import org.apache.lucene.analysis.hy.ArmenianAnalyzer;
import org.apache.lucene.analysis.standard.ClassicAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.UAX29URLEmailAnalyzer;
import org.apache.lucene.analysis.util.CharFilterFactory;
import org.apache.lucene.analysis.util.TokenFilterFactory;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.collation.CollationKeyAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import java.io.IOException;

/**
 * Created by rzx on 2017/6/1.
 */
public class CindexSearch {

    public static void createIndexANDSearchIndex() throws Exception{
        Analyzer analyzer = new StandardAnalyzer();
        //RAMDirectory内存字典存储索引
        Directory directory = new RAMDirectory();
        //Directory directory = FSDirectory.open("/tmp/testindex");磁盘存储索引

        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(directory,config);
        Document document = new Document();
        String text = "hello world main test";
        document.add(new Field("content",text, TextField.TYPE_STORED)); //将域field添加到document中
        writer.addDocument(document);
        writer.close();

        DirectoryReader directoryReader = DirectoryReader.open(directory);
        IndexSearcher isearch = new IndexSearcher(directoryReader);
        QueryParser qparser = new QueryParser("content",new StandardAnalyzer());

        //QueryParser qparser2 = new MultiFieldQueryParser(new String[]{"3132","1231"},new StandardAnalyzer());

        //Lucene中实现了很多分词器，有针对性的应用各个场景和各种语言。
        /*QueryParser qparser = new QueryParser("content",new SimpleAnalyzer());
        QueryParser qparser = new QueryParser("content",new ClassicAnalyzer());
        QueryParser qparser = new QueryParser("content",new KeywordAnalyzer());
        QueryParser qparser = new QueryParser("content",new StopAnalyzer());
        QueryParser qparser = new QueryParser("content",new UAX29URLEmailAnalyzer());
        QueryParser qparser = new QueryParser("content",new UnicodeWhitespaceAnalyzer());
        QueryParser qparser = new QueryParser("content",new WhitespaceAnalyzer());
        QueryParser qparser = new QueryParser("content",new ArabicAnalyzer());
        QueryParser qparser = new QueryParser("content",new ArmenianAnalyzer());
        QueryParser qparser = new QueryParser("content",new BasqueAnalyzer());
        QueryParser qparser = new QueryParser("content",new BrazilianAnalyzer());
        QueryParser qparser = new QueryParser("content",new BulgarianAnalyzer());
        QueryParser qparser = new QueryParser("content",new CatalanAnalyzer());
        QueryParser qparser = new QueryParser("content",new CJKAnalyzer());
        QueryParser qparser = new QueryParser("content",new CollationKeyAnalyzer());
        QueryParser qparser = new QueryParser("content",new CustomAnalyzer(Version defaultMatchVersion, CharFilterFactory[] charFilters, TokenizerFactory
        tokenizer, TokenFilterFactory[] tokenFilters, Integer posIncGap, Integer offsetGap));
        QueryParser qparser = new QueryParser("content",new SmartChineseAnalyzer());//中文最长分词*/

        Query query = qparser.parse("main");

//        Term term =new Term("content","main");
//        Query query2 = new TermQuery(term);

        ScoreDoc [] hits = isearch.search(query,10).scoreDocs;
        for (int i = 0; i <hits.length ; i++) {
            Document hitdoc =isearch.doc(hits[i].doc);
            System.out.print("命中的文件内容："+hitdoc.get("content"));
        }
        directoryReader.close();
        directory.close();
    }

    public static void main(String[] args) {
        try {
            createIndexANDSearchIndex();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
