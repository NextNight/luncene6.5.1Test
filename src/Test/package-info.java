/**
 *
 * Created by rzx on 2017/6/5.
 */
package Test;

/**
 * 1、Lucene核心jar,分词器，排序(评分排序，字段排序)
 *    JAR:Lucene核心代码JAR=================================
 *      *lucene-analyzers-common-6.5.1.jar
 *      *lucene-analyzers-smartcn-6.5.1.jar
 *      *lucene-core-6.5.1.jar
 *      *lucene-highlighter-6.5.1.jar
 *      *lucene-queries-6.5.1.jar
 *      *lucene-queryparser-6.5.1.jar
 *    Analyzer:自带分词器===================================
 *      * StandardAnalyzer()
 *      * SimpleAnalyzer();
        * ClassicAnalyzer();
        * KeywordAnalyzer();
        * StopAnalyzer();
        * UAX29URLEmailAnalyzer();
        * UnicodeWhitespaceAnalyzer();
        * WhitespaceAnalyzer();
        * ArabicAnalyzer();
        * ArmenianAnalyzer();
        * BasqueAnalyzer();
        * BrazilianAnalyzer();
        * BulgarianAnalyzer();
        * CatalanAnalyzer();
        * CJKAnalyzer(); //二分词
        * CollationKeyAnalyzer();
        * CustomAnalyzer(Version defaultMatchVersion, CharFilterFactory[] charFilters, TokenizerFactory
        tokenizer, TokenFilterFactory[] tokenFilters, Integer posIncGap, Integer offsetGap));//自定义分词器
        * SmartChineseAnalyzer());//最长中文分词
 *      *.........省略N种
 *
 * 2、Lucene的数学模型<空间向量模形>，评分机制<IF-DF权重计算,COS0余玄相似度>。
 *    2.1、空间向量模型：把每篇文档构成一个N维的向量，
 *    2.2、权重计算TF-DF：
 *      * TF:Term Frequency,词元在这个文档中出现的次数，tf值越大，词越重要。
 *      * DF:Document Frequency,有多少文档包含这个词元，df越大，那么词就越不重要。
 *      * W=TF*log(n/DF)：用一篇文档的每次词元的权重构成一个向量（每个词元的权重值作为向量的维度）来表示这篇文档，
 *                      这样所有的文档都可以表示成一个N维的空间向量。N表示所有文档分词后的词元集合的词元总数。
 *    2.3、余玄相似度cos0：计算搜索关键字与每篇文档的相似度，不为0则包含这个关键词，保存文档信息，打分并保存分数
 *      cos0 = V->*key->/|V->||key->| (->表示向量)
 * 1、Lucene索引的建立和查询：内存索引和磁盘索引，多条件查询。
 *    核心类：
 *      建立索引：
 *      * Analyzer analyzer = new StandardAnalyzer(); //实例化分词器
 *      * Directory directory = new RAMDirectory();   //初始化内存索引目录
        * Directory directory = FSDirectory.open("/tmp/testindex");//初始化磁盘存储索引
        * IndexWriterConfig config = new IndexWriterConfig(analyzer); //索引器配置
 *      * IndexWriter writer = new IndexWriter(directory,config); //索引器
        * Document document = new Document(); //初始化Document，用来存数据。
 *      * 把数据加入索引器进行索引建立：write.addDocument(document)
 *      查询索引：
 *      * DirectoryReader directoryReader = DirectoryReader.open(directory); //索引目录读取器
 *      * IndexSearcher isearch = new IndexSearcher(directoryReader);  //索引查询器
 *      *多种检索方式：
        * QueryParser单字段<域>绑定:
 *              QueryParser qparser = new QueryParser("filed",new StandardAnalyzer()); //查询解析器：参数Field域,分词器
 *              Query query = qparser.parse("main") //查询关键词
 *      * QueryParser多字段<域>绑定：
 *              QueryParser qparser2 = new MultiFieldQueryParser(new String[]{"field1","field2"},new StandardAnalyzer());//多字段查询解析器
 *              Query query = qparser2.parse("main") //查询关键词
 *      * Term绑定字段<域>查询：new Term(field,keyword);
 *              Term term =new Term("content","main");
                Query query = new TermQuery(term);
        *更多方法：参照http://blog.csdn.net/chenghui0317/article/details/10824789
 *      * ScoreDoc [] hits = isearch.search(query,1000).scoreDocs; //查询你命中的文档以及评分和所在分片
 *
 * 3、Lucene近实时搜索
 *      *
 *
 * 4、Lucene高亮显示,前台后台高亮。
 *      出现异常:Exception in thread "main" java.lang.NoClassDefFoundError: org/apache/lucene/index/memory/MemoryIndex
 *      需要导入:lucene-memory-6.5.1.jar,这个包用来处理存储位置的偏移量,可以让我们在文本中定位到关键词元.
 *      .....
 *      得到命中的doc数组之后：进行高亮显示<B>核心在于遍历命中的文档找到对相应的偏移量的关键字加上我们的高亮标签得到新的文本返回
 *      这样在页面进行渲染的时候就能够得到高亮显示。。<B/>...
 *      try {
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
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
