package com.amee.platform.search;

import com.amee.base.resource.ValidationResult;
import com.amee.base.validation.ValidationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * LuceneIndexWrapper wraps a Lucene file system index and provides a simplified abstraction of
 * the Lucene API.
 * <p/>
 * Instances of this class should only be used by one thread. Care should be taken to respect the
 * sequence of method calls as described in the method documentation.
 */
public class LuceneService implements Serializable {

    private final Log log = LogFactory.getLog(getClass());

    private Analyzer analyzer;
    private Directory directory;
    private IndexWriter indexWriter;

    /**
     * Conduct a search in the Lucene index based on the supplied field name and query string.
     *
     * @param field name of field to search within
     * @param q     query to search with
     * @return a List of Lucene Documents
     */
    public List<Document> doSearch(String field, String q) {
        log.debug("doSearch()");
        QueryParser parser = new QueryParser(Version.LUCENE_30, field, getAnalyzer());
        try {
            return doSearch(parser.parse(q));
        } catch (ParseException e) {
            ValidationResult validationResult = new ValidationResult();
            validationResult.addValue("field", field);
            validationResult.addValue("query", q);
            validationResult.addError(field, "parse", q);
            throw new ValidationException(validationResult);
        }
    }

    /**
     * Conduct a search in the Lucene index based on the supplied Query.
     *
     * @param query to search with
     * @return a List of Lucene Documents
     */
    public List<Document> doSearch(Query query) {
        log.debug("doSearch()");
        List<Document> documents = new ArrayList<Document>();
        try {
            IndexSearcher searcher = new IndexSearcher(getDirectory());
            TopScoreDocCollector collector = TopScoreDocCollector.create(50, true);
            searcher.search(query, collector);
            ScoreDoc[] hits = collector.topDocs().scoreDocs;
            for (ScoreDoc hit : hits) {
                documents.add(searcher.doc(hit.doc));
            }
            searcher.close();
        } catch (IOException e) {
            throw new RuntimeException("Caught IOException: " + e.getMessage(), e);
        }
        return documents;
    }

    public void addDocument(Document document) {
        synchronized (getIndexWriter()) {
            try {
                getIndexWriter().addDocument(document);
                closeIndexWriter();
            } catch (IOException e) {
                throw new RuntimeException("Caught IOException: " + e.getMessage(), e);
            }
        }
    }

    public void addDocuments(Collection<Document> documents) {
        synchronized (getIndexWriter()) {
            try {
                for (Document document : documents) {
                    getIndexWriter().addDocument(document);
                }
                closeIndexWriter();
            } catch (IOException e) {
                throw new RuntimeException("Caught IOException: " + e.getMessage(), e);
            }
        }
    }

    public void updateDocument(Term term, Document document, Analyzer analyzer) {
        synchronized (getIndexWriter()) {
            try {
                getIndexWriter().updateDocument(term, document, analyzer);
                closeIndexWriter();
            } catch (IOException e) {
                throw new RuntimeException("Caught IOException: " + e.getMessage(), e);
            }
        }
    }

    public void deleteDocuments(Term... terms) {
        synchronized (getIndexWriter()) {
            try {
                getIndexWriter().deleteDocuments(terms);
                closeIndexWriter();
            } catch (IOException e) {
                throw new RuntimeException("Caught IOException: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Clear the Lucene index.
     */
    public synchronized void clearIndex() {
        // First ensure index is not locked (perhaps from a crash).
        unlockIndex();
        // Create a new index.
        getIndexWriter(true);
        closeIndexWriter();
    }

    /**
     * Unlocks the Lucene index. Useful following JVM crashes.
     */
    public synchronized void unlockIndex() {
        try {
            if (IndexReader.indexExists(getDirectory())) {
                IndexReader.open(getDirectory());
                if (IndexWriter.isLocked(getDirectory())) {
                    IndexWriter.unlock(getDirectory());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Caught IOException: " + e.getMessage(), e);
        }
    }

    /**
     * Ensure the Lucene directory is closed
     *
     * @throws Throwable the <code>Exception</code> raised by this method
     */
    protected void finalize() throws Throwable {
        super.finalize();
        closeIndexWriter();
        closeDirectory();
    }

    /**
     * Get the Analyzer. Will call createAnalyser if it does not yet exist.
     *
     * @return the Analyzer
     */
    public synchronized Analyzer getAnalyzer() {
        if (analyzer == null) {
            createAnalyser();
        }
        return analyzer;
    }

    /**
     * Create a new Analyzer.
     */
    private void createAnalyser() {
        setAnalyzer(new StandardAnalyzer(Version.LUCENE_30));
    }

    /**
     * Set the Analyzer.
     *
     * @param analyzer to set
     */
    private void setAnalyzer(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    /**
     * Get the Analyzer. Will call createAnalyser if it does not yet exist.
     *
     * @return the Analyzer
     */
    private synchronized Directory getDirectory() {
        if (directory == null) {
            createDirectory();
        }
        return directory;
    }

    /**
     * Open the Directory.
     */
    private void createDirectory() {
        try {
            String path = System.getProperty("amee.lucenePath", "/var/www/apps/amee/lucene");
            setDirectory(FSDirectory.open(new File(path)));
        } catch (IOException e) {
            throw new RuntimeException("Caught IOException: " + e.getMessage(), e);
        }
    }

    /**
     * Closes the Lucene directory.
     */
    private synchronized void closeDirectory() {
        try {
            if (directory != null) {
                directory.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Caught IOException: " + e.getMessage(), e);
        }
    }

    /**
     * Set the Directory.
     *
     * @param directory to set
     */
    private void setDirectory(Directory directory) {
        this.directory = directory;
    }


    /**
     * Gets the current IndexWriter or creates a new one.
     *
     * @return the IndexWriter
     */
    private IndexWriter getIndexWriter() {
        return getIndexWriter(false);
    }

    /**
     * Gets IndexWriter. Will call createIndexWriter if an IndexWriter is not yet created. Can
     * be called multiple times within a thread. The create parameter is only effective when the
     * IndexWriter has not previously been created.
     * <p/>
     * Later, closeIndexWriter must be called at least once.
     *
     * @param create a new index if true
     * @return the IndexWriter
     */
    private synchronized IndexWriter getIndexWriter(boolean create) {
        if (indexWriter == null) {
            createIndexWriter(create);
        }
        return indexWriter;
    }

    /**
     * Construct and set a new IndexWriter. Should only be called once within a thread.
     * <p/>
     * Later, closeIndexWriter must be called at least once.
     *
     * @param create a new index if true
     */
    private void createIndexWriter(boolean create) {
        try {
            setIndexWriter(new IndexWriter(
                    getDirectory(),
                    getAnalyzer(),
                    create,
                    new IndexWriter.MaxFieldLength(25000)));
        } catch (IOException e) {
            throw new RuntimeException("Caught IOException: " + e.getMessage(), e);
        }
    }

    /**
     * Closes the IndexWriter. Will optimise the index prior to closing.
     * <p/>
     * This must be called at least once following previous calls to getIndexWriter.
     */
    private synchronized void closeIndexWriter() {
        if (indexWriter != null) {
            try {
                indexWriter.optimize();
                indexWriter.close();
                setIndexWriter(null);
            } catch (IOException e) {
                throw new RuntimeException("Caught IOException: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Set the IndexWriter.
     *
     * @param indexWriter to set
     */
    private void setIndexWriter(IndexWriter indexWriter) {
        this.indexWriter = indexWriter;
    }
}
