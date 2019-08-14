package org.solr.wikipedia.indexer;

import com.google.common.collect.Multimap;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.lang3.Validate;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrInputField;
import org.omg.SendingContext.RunTime;
import org.solr.wikipedia.handler.DefaultPageHandler;
import org.solr.wikipedia.iterator.SolrInputDocPageIterator;
import org.solr.wikipedia.iterator.WikiMediaIterator;
import org.solr.wikipedia.model.Page;

import javax.xml.stream.XMLStreamException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Indexes Pages in Solr.
 *
 * @author bryanbende
 */
public class DefaultIndexer {

    static final int DEFAULT_BATCH_SIZE = 20;

    private final int batchSize;
    private final SolrServer solrServer;

    /**
     *
     * @param solrServer
     */
    public DefaultIndexer(SolrServer solrServer) {
        this(solrServer, DEFAULT_BATCH_SIZE);
    }

    /**
     *
     * @param solrServer
     * @param batchSize
     */
    public DefaultIndexer(SolrServer solrServer, int batchSize) {
        this.solrServer = solrServer;
        this.batchSize = batchSize;
        Validate.notNull(this.solrServer);
        if (this.batchSize <= 0) {
            throw new IllegalStateException("Batch size must be > 0");
        }
    }

    /**
     * Iterates over docs adding each SolrInputDocument to the given SolrServer in batches.
     *
     * @throws SolrServerException Iterates over docs adding each SolrInputDocument to the given SolrServer in batches.
     */
    public void index(Iterator<SolrInputDocument> docs) throws IOException, SolrServerException {
        if (docs == null) {
            return;
        }

        long count = 0;
        Collection<SolrInputDocument> solrDocs = new ArrayList<>();
        while (docs.hasNext()) {
            SolrInputDocument doc = docs.next();

            solrDocs.add(doc);

            if (solrDocs.size() >= this.batchSize) {
                count += solrDocs.size();
                System.out.println("reached batch size, total count = " + count);
                solrServer.add(solrDocs);
                solrDocs.clear();
            }
        }

        if (solrDocs.size() > 0) {
            solrServer.add(solrDocs);
            solrDocs.clear();
        }
    }


    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: DefaultIndexer <SOLR_URL> <WIKIPEDIA_DUMP_FILE> " +
                "(<BATCH_SIZE>)");
            System.exit(0);
        }

        String solrUrl = args[0];
        String wikimediaDumpFile = args[1];

        Validate.notEmpty(solrUrl);
        Validate.notEmpty(wikimediaDumpFile);

        // attempt to parse a provided batch size
        Integer batchSize = null;
        if (args.length == 3) {
            try {
                batchSize = Integer.valueOf(args[2]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // TODO extract this part from main to enable unit-testing
        try (FileInputStream fileIn = new FileInputStream(wikimediaDumpFile)) {
            InputStream input = null;
            String extension = wikimediaDumpFile.substring(wikimediaDumpFile.length() - 4);
            switch (extension) {
                case ".xml":
                    input = fileIn;
                    break;
                case ".bz2":
                    input = new BZip2CompressorInputStream(fileIn);
                    break;
                case ".gz":
                    input = new GZIPInputStream(fileIn);
                    break;
                default:
                    throw new RuntimeException("Currently can only process BZ2, GZ or raw XML dump, not [" + extension + ".]");
            }
            try (InputStreamReader reader = new InputStreamReader(input)) {
                Iterator<Page> pageIter = new WikiMediaIterator<>(
                    reader, new DefaultPageHandler());

                Iterator<SolrInputDocument> docIter =
                    new SolrInputDocPageIterator(pageIter);

                SolrServer solrServer = new HttpSolrServer(solrUrl);

                DefaultIndexer defaultIndexer = (batchSize != null ?
                    new DefaultIndexer(solrServer, batchSize) :
                    new DefaultIndexer(solrServer));

                long startTime = System.currentTimeMillis();

                defaultIndexer.index(docIter);

                System.out.println("Indexing finished at " + new Date());
                System.out.println("Took " + (System.currentTimeMillis() - startTime) + " ms");
            } catch (XMLStreamException e) {
                e.printStackTrace();
            } catch (SolrServerException e) {
                e.printStackTrace();
            } finally {
                input.close();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
