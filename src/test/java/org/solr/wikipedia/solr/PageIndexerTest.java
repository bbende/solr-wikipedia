package org.solr.wikipedia.solr;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.solr.wikipedia.handler.CollectingPageHandler;
import org.solr.wikipedia.handler.DefaultPageHandler;
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
public class PageIndexerTest {

    private SolrServer solrServer;

    private PageIndexer pageIndexer;

    @Before
    public void setup() {
        this.solrServer = EmbeddedSolrServerFactory.create("wikipediaCollection");
        this.pageIndexer = new PageIndexer(solrServer);
    }

    @Test
    public void testIndexPages() throws IOException, XMLStreamException, SolrServerException {
        String testWikiXmlFile = "src/test/resources/test-wiki-data.xml";

        try (FileReader reader = new FileReader(testWikiXmlFile)) {
            Iterator<Page> iterator = new WikiMediaIterator<>(
                    reader, new DefaultPageHandler());

            pageIndexer.index(iterator);
            solrServer.commit();
        }

        SolrQuery solrQuery = new SolrQuery("*:*");
        QueryResponse response = solrServer.query(solrQuery);
        Assert.assertEquals(2, response.getResults().size());
    }

}
