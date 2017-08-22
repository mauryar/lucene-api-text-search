package com.neu.controller;
/**
 * @author rajan
 *
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

//import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
 
public class TikaWriteIndex 
{
    public static void main(String[] args)
    {
        //Input folder
        String docsPath = "inputFiles";
        
        String indexPath = "indexedFiles";
     
        
        //Output folder
        //Input Path Variable
        final Path docDir = Paths.get(docsPath);
 
        try
        {
            //org.apache.lucene.store.Directory instance
            Directory dir = FSDirectory.open( Paths.get(indexPath) );
             
            //analyzer with the default stop words
            Analyzer analyzer = new StandardAnalyzer();
             
            //IndexWriter Configuration
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
             
            //IndexWriter writes new index files to the directory
            IndexWriter writer = new IndexWriter(dir, iwc);
             
            //Its recursive method to iterate all files and directories
            indexDocs(writer, docDir);
 
            writer.close();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
     
    static void indexDocs(final IndexWriter writer, Path path) throws IOException 
    {
    	
    	
        //Directory?
        if (Files.isDirectory(path)) 
        {
            //Iterate directory
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() 
            {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException 
                {
                    try
                    {
                        //Index this file
                        indexDoc(writer, file, attrs.lastModifiedTime().toMillis());
                      
                        writer.commit();
                		writer.deleteUnusedFiles();
                    } 
                    catch (IOException ioe) 
                    {
                        ioe.printStackTrace();
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } 
        else
        {
            //Index this file
            indexDoc(writer, path, Files.getLastModifiedTime(path).toMillis());
        }
    }
 
    static void indexDoc(IndexWriter writer, Path file, long lastModified) throws IOException 
    {
		File fileinDoc = new File(file.toAbsolutePath().toString());
		 
    		Metadata metadata = new Metadata();
			ContentHandler handler = new BodyContentHandler();
			ParseContext context = new ParseContext();
			Parser parser = new AutoDetectParser();
			InputStream stream = new FileInputStream(fileinDoc);
			
			try {
				parser.parse(stream, handler, metadata, context);
			}
			catch (TikaException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			}
			finally {
				stream.close();
			}
			String text = handler.toString();
			String contextofFile=stream.toString();
			String fileName = fileinDoc.getName();
			
			Document doc = new Document();			
			for (String key : metadata.names()) {
				String name = key.toLowerCase();
				String value = metadata.get(key);
				doc.add(new StringField("path", file.toString(), Field.Store.YES));
				doc.add(new LongPoint("modified", lastModified));
			
				if (StringUtils.isBlank(value)) {
					continue;
				}
				
				if ("keywords".equalsIgnoreCase(key)) {
					for (String keyword : value.split(",?(\\s+)")) {
						doc.add(new StringField(name, keyword, Field.Store.YES));
					}
				}
				else if ("title".equalsIgnoreCase(key)) {
					doc.add(new StringField(name, value, Field.Store.YES));
				}
				else {
					doc.add(new StringField(name, fileName, Field.Store.YES));
				}
			}
			doc.add(new TextField("text", text, Store.YES));
			writer.addDocument(doc);

			System.out.println(writer.maxDoc() + " documents written");
    }
    
}