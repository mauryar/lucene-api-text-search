package com.neu.controller.highlight;
/**
 * @author rajan
 *
 */
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;


 
public class TikaSearchWord 
{
	
    public static ArrayList<IndividualDoc> getDoc(String args, ArrayList<IndividualDoc> resultsFetched) throws Exception 
    {
    	//add by me
    	List<String> stopWords = Arrays.asList("various", "sometime", "whatever");
    	CharArraySet stopSet = new CharArraySet(stopWords, false);
    	String INDEX_DIR = "C:\\Users\\rajan\\Desktop\\AlgoProjectLucene\\CounterWebApp\\indexedFiles";
    	
    	//add end by me
    	
        //Get directory reference
        Directory dir = FSDirectory.open(Paths.get(INDEX_DIR));
         
        //Index reader - an interface for accessing a point-in-time view of a lucene index
        IndexReader reader = DirectoryReader.open(dir);
         
        //Create lucene searcher. It search over a single IndexReader.
        IndexSearcher searcher = new IndexSearcher(reader);
         
        //analyzer with the default stop words
        Analyzer analyzer = new StandardAnalyzer(stopSet);
         
        //Query parser to be used for creating TermQuery
        QueryParser qp = new QueryParser("text", analyzer);
         
        //Create the query
        //Query query = qp.parse("cottage private discovery concluded");
        
        Query userQuery = null;
        
        if(args.toLowerCase().contains("not")) {
        	String[] split = args.toLowerCase().split("not");
        	Query userq1 = qp.parse(split[0]);
        	Query userq2 = qp.parse(split[1]);
        	userQuery = new BooleanQuery.Builder().add(userq1, BooleanClause.Occur.MUST)
    				.add(userq2, BooleanClause.Occur.MUST_NOT).build();
        }else {
        	if(args.toLowerCase().contains("and")) {
            	String[] split = args.toLowerCase().split("and");
            	Query userq1 = qp.parse(split[0]);
            	Query userq2 = qp.parse(split[1]);
            	userQuery = new BooleanQuery.Builder().add(userq1, BooleanClause.Occur.MUST)
        				.add(userq2, BooleanClause.Occur.MUST).build();
            }else {
            	if(args.toLowerCase().contains("or")) {
                	String[] split = args.toLowerCase().split("or");
                	Query userq1 = qp.parse(split[0]);
                	Query userq2 = qp.parse(split[1]);
                	userQuery = new BooleanQuery.Builder().add(userq1, BooleanClause.Occur.SHOULD)
            				.add(userq2, BooleanClause.Occur.SHOULD).build();
                }else {
                	userQuery = qp.parse(args);
                }
            }
        }
        
        
         
        //Search the lucene documents
        TopDocs hits = searcher.search(userQuery, 10);
         
        /* Highlighter Code Start */
         
        
        Formatter formatter = new SimpleHTMLFormatter();
         
        //It scores text fragments by the number of unique query terms found
        //Basically the matching score in layman terms
        QueryScorer scorer = new QueryScorer(userQuery);
         
        //used to markup highlighted terms found in the best sections of a text
        Highlighter highlighter = new Highlighter(formatter, scorer);
         
        //It breaks text up into same-size texts but does not split up spans
        Fragmenter fragmenter = new SimpleSpanFragmenter(scorer, 20);
         
        //breaks text up into same-size fragments with no concerns over spotting sentence boundaries.
        //Fragmenter fragmenter = new SimpleFragmenter(10);
         
        //set fragmenter to highlighter
        highlighter.setTextFragmenter(fragmenter);
        
        //Show hits for query
        System.out.println("hits for query: "+hits.totalHits);
         
        //Iterate over found results
        for (int i = 0; i < hits.scoreDocs.length; i++) 
        {
            int docid = hits.scoreDocs[i].doc;
            Document doc = searcher.doc(docid);
            String title = doc.get("path");
             
            //Printing - to which document result belongs
            System.out.println("Path " + " : " + title);
            //Get stored text from found document
            String text = doc.get("text");
 
            //Create token stream
            TokenStream stream = TokenSources.getAnyTokenStream(reader, docid, "text", analyzer);
            System.out.println("(" + hits.scoreDocs[i].score + ")");
            IndividualDoc giveResult = new IndividualDoc();
            giveResult.setScore(hits.scoreDocs[i].score);
            giveResult.setDocPath(doc.get("path"));
            //Get highlighted text fragments
            String[] frags = highlighter.getBestFragments(stream, text, 30);
            giveResult.setHighlightedText(frags);
            for (String frag : frags) 
            {
                System.out.println("=======================");
                System.out.println(frag);
                
            }
            resultsFetched.add(giveResult);
        }
        dir.close();
        return resultsFetched;
    }
}