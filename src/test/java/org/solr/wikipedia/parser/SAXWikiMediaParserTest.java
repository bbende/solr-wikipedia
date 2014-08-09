package org.solr.wikipedia.parser;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.solr.wikipedia.handler.CollectingPageHandler;
import org.solr.wikipedia.handler.DefaultPageHandler;
import org.solr.wikipedia.model.Page;
import org.solr.wikipedia.util.WikiMediaTestData;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

//    @Test
//    public void testParseFullFile() throws IOException {
//        String testWikiXmlFile = "/Users/bryanbende/Data/Wikipedia/enwiki-latest-pages-articles.xml";
//
//        CountingHandler handler = new CountingHandler();
//        WikiMediaXMLParser<Page> wikiMediaXMLParser = new SAXWikiMediaParser<>();
//
//        try (FileReader reader = new FileReader(testWikiXmlFile)) {
//            wikiMediaXMLParser.parse(reader, handler);
//        }
//
//        System.out.println("DONE, total = " + handler.getCount());
//    }

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

    private class CountingHandler extends DefaultPageHandler {
        private long count = 0;

        @Override
        public void title(String title) {
            super.title(title);
            System.out.println(title);
        }

        @Override
        public Page endPage() {
            count++;
            return super.endPage();
        }

        public long getCount() {
            return count;
        }
    }
}
