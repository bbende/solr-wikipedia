package org.solr.wikipedia.parser;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.solr.wikipedia.model.Page;
import org.solr.wikipedia.handler.CollectingPageHandler;
import org.solr.wikipedia.util.WikiMediaTestData;

import java.io.*;
import java.util.List;

/**
 * @author bryanbende
 */
public class SAXWikiMediaParserTest {

    private List<Page> expectedPages;

    @Before
    public void setup() {
        expectedPages = WikiMediaTestData.getPages();
    }

    @Test
    public void testParse() throws IOException {
        String testWikiXmlFile = "src/test/resources/test-wiki-data.xml";

        WikiMediaXMLParser<Page> wikiMediaXMLParser = new SAXWikiMediaParser<>();
        CollectingPageHandler handler = new CollectingPageHandler();

        try (FileReader reader = new FileReader(testWikiXmlFile)) {
            wikiMediaXMLParser.parse(reader, handler);

            List<Page> pages = handler.getPages();
            Assert.assertNotNull(pages);
            Assert.assertEquals(2, pages.size());
            Assert.assertEquals(expectedPages, pages);
        }
    }

    @Test
    public void testParseWithBZipInputStream() throws IOException {
        String testWikiXmlFile = "src/test/resources/test-wiki-data.xml.bz2";

        WikiMediaXMLParser<Page> wikiMediaXMLParser = new SAXWikiMediaParser<>();
        CollectingPageHandler handler = new CollectingPageHandler();

        try (FileInputStream fileIn = new FileInputStream(testWikiXmlFile);
             BZip2CompressorInputStream bzipIn = new BZip2CompressorInputStream(fileIn);
             InputStreamReader reader = new InputStreamReader(bzipIn)) {

            wikiMediaXMLParser.parse(reader, handler);

            List<Page> pages = handler.getPages();
            Assert.assertNotNull(pages);
            Assert.assertEquals(2, pages.size());
            Assert.assertEquals(expectedPages, pages);
        }
    }

}
