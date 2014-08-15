package org.solr.wikipedia.indexer;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.solr.wikipedia.handler.DefaultPageHandler;
import org.solr.wikipedia.iterator.SolrInputDocPageIterator;
import org.solr.wikipedia.iterator.WikiMediaIterator;
import org.solr.wikipedia.model.Page;
import org.solr.wikipedia.util.EmbeddedSolrServerFactory;

import javax.xml.stream.XMLStreamException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

/**
 * @author bryanbende
 */
public class DefaultIndexerTest {

    private SolrServer solrServer;

    private DefaultIndexer defaultIndexer;

    @Before
    public void setup() {
        this.solrServer = EmbeddedSolrServerFactory.create("wikipediaCollection");
        this.defaultIndexer = new DefaultIndexer(solrServer);
    }

    @After
    public void after() {
        this.solrServer.shutdown();
    }

    @Test
    public void testIndexPages() throws IOException, XMLStreamException, SolrServerException {
        String testWikiXmlFile = "src/test/resources/test-wiki-data.xml";

        try (FileReader reader = new FileReader(testWikiXmlFile)) {
            Iterator<Page> pageIter = new WikiMediaIterator<>(
                    reader, new DefaultPageHandler());

            Iterator<SolrInputDocument> docIter =
                    new SolrInputDocPageIterator(pageIter);

            defaultIndexer.index(docIter);
            solrServer.commit();
        }

        SolrQuery solrQuery = new SolrQuery("*:*");
        QueryResponse response = solrServer.query(solrQuery);
        Assert.assertEquals(2, response.getResults().size());
    }

}
