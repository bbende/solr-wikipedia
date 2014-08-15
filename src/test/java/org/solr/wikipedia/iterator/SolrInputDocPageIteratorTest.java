package org.solr.wikipedia.iterator;

import org.apache.solr.common.SolrInputDocument;
import org.junit.Assert;
import org.junit.Test;
import org.solr.wikipedia.handler.DefaultPageHandler;
import org.solr.wikipedia.handler.PageHandler;
import org.solr.wikipedia.model.Page;

import javax.xml.stream.XMLStreamException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

/**
 * @author bryanbende
 */
public class SolrInputDocPageIteratorTest {

    @Test
    public void testIterator() throws IOException, XMLStreamException {
        String testWikiXmlFile = "src/test/resources/test-wiki-data.xml";

        try (FileReader reader = new FileReader(testWikiXmlFile)) {
            PageHandler handler = new DefaultPageHandler();

            Iterator<Page> pageIter = new WikiMediaIterator<Page>(
                    reader, handler);

            Iterator<SolrInputDocument> docIter =
                    new SolrInputDocPageIterator(pageIter);

            int count = 0;
            while (docIter.hasNext()) {
                SolrInputDocument doc = docIter.next();
                Assert.assertNotNull(doc);
                count++;
            }

            Assert.assertEquals(2, count);
        }
    }

}
