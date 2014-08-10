package org.solr.wikipedia.solr;

import com.google.common.collect.Multimap;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.lang3.Validate;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.solr.wikipedia.handler.CollectingPageHandler;
import org.solr.wikipedia.handler.DefaultPageHandler;
import org.solr.wikipedia.iterator.WikiMediaIterator;
import org.solr.wikipedia.model.Page;

import javax.xml.stream.XMLStreamException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Indexes Pages in Solr.
 *
 * @author bryanbende
 */
public class PageIndexer {

    static final int DEFAULT_BATCH_SIZE = 10;

    private final SolrServer solrServer;
    private final int batchSize;

    /**
     *
     * @param solrServer
     */
    public PageIndexer(SolrServer solrServer) {
        this(solrServer, DEFAULT_BATCH_SIZE);
    }

    /**
     *
     * @param solrServer
     * @param batchSize
     */
    public PageIndexer(SolrServer solrServer, int batchSize) {
        this.solrServer = solrServer;
        this.batchSize = batchSize;
        Validate.notNull(this.solrServer);
        if (this.batchSize <= 0) {
            throw new IllegalStateException("Batch size must be > 0");
        }
    }

    /**
     * Iterates over pages adding each Page to the given SolrServer.
     *
     * @param pages
     * @throws IOException
     * @throws SolrServerException
     */
    public void index(Iterator<Page> pages) throws IOException, SolrServerException {
        if (pages == null) {
            return;
        }

        Collection<SolrInputDocument> solrDocs = new ArrayList<>();

        while(pages.hasNext()) {
            Page page = pages.next();
            PageMultimap pageMultimap = new PageMultimap(page);

            SolrInputDocument doc = getSolrInputDocument(pageMultimap.getMultimap());
            solrDocs.add(doc);

            if (solrDocs.size() >= this.batchSize) {
                solrServer.add(solrDocs);
                solrDocs.clear();
            }
        }

        if (solrDocs.size() > 0) {
            solrServer.add(solrDocs);
            solrDocs.clear();
        }
    }

    private SolrInputDocument getSolrInputDocument(Multimap<String,Object> multimap) {
        SolrInputDocument doc = new SolrInputDocument();
        for (String key : multimap.keySet()) {
            Collection<Object> values = multimap.get(key);
            for (Object value : values) {
                doc.addField(key, value);
            }
        }
        return doc;
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: PageIndexer <SOLR_URL> <WIKIPEDIA_DUMP_FILE>");
            System.exit(0);
        }

        String solrUrl = args[0];
        String wikimediaDumpFile = args[1];

        Validate.notEmpty(solrUrl);
        Validate.notEmpty(wikimediaDumpFile);

        try (FileInputStream fileIn = new FileInputStream(wikimediaDumpFile);
             BZip2CompressorInputStream bzipIn = new BZip2CompressorInputStream(fileIn);
             InputStreamReader reader = new InputStreamReader(bzipIn)) {

            Iterator<Page> iterator = new WikiMediaIterator<>(
                    reader, new DefaultPageHandler());

            SolrServer solrServer = new HttpSolrServer(solrUrl);
            PageIndexer pageIndexer = new PageIndexer(solrServer);
            pageIndexer.index(iterator);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } catch (SolrServerException e) {
            e.printStackTrace();
        }
    }

}
