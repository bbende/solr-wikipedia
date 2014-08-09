package org.solr.wikipedia.iterator;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.solr.wikipedia.handler.CollectingPageHandler;
import org.solr.wikipedia.handler.DefaultPageHandler;
import org.solr.wikipedia.model.Page;
import org.solr.wikipedia.util.WikiMediaTestData;

import javax.xml.stream.XMLStreamException;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;

/**
 * @author bryanbende
 */
public class WikiMediaIteratorTest {

    private List<Page> expectedPages;

    @Before
    public void setup() {
        expectedPages = WikiMediaTestData.getPages();
    }

    @Test
    public void testIterator() throws XMLStreamException, IOException {
        String testWikiXmlFile = "src/test/resources/test-wiki-data.xml";

        try (FileReader reader = new FileReader(testWikiXmlFile)) {
            CollectingPageHandler handler = new CollectingPageHandler();

            Iterator<Page> iterator = new WikiMediaIterator<>(
                    reader, handler);

            testIterator(iterator, handler);
        }
    }

//    @Test
//    public void testIteratorFullFile() throws XMLStreamException, IOException {
//        String testWikiXmlFile = "/Users/bryanbende/Data/Wikipedia/enwiki-latest-pages-articles.xml";
//
//        try (FileReader reader = new FileReader(testWikiXmlFile)) {
//            DefaultPageHandler handler = new DefaultPageHandler();
//
//            Iterator<Page> iterator = new WikiMediaIterator<>(
//                    reader, handler);
//
//            int count = 0;
//            while (iterator.hasNext()) {
//                Page page = iterator.next();
//                System.out.println(page.getTitle());
//                count++;
//            }
//
//            System.out.println("DONE, total = " + count);
//        }
//    }

    @Test
    public void testIteratorWithBZipInputStream() throws XMLStreamException, IOException {
        String testWikiXmlFile = "src/test/resources/test-wiki-data.xml.bz2";

        try (FileInputStream fileIn = new FileInputStream(testWikiXmlFile);
             BZip2CompressorInputStream bzipIn = new BZip2CompressorInputStream(fileIn);
             InputStreamReader reader = new InputStreamReader(bzipIn)) {

            CollectingPageHandler handler = new CollectingPageHandler();

            Iterator<Page> iterator = new WikiMediaIterator<>(
                    reader, handler);

            testIterator(iterator, handler);
        }
    }


    private void testIterator(Iterator<Page> iterator, CollectingPageHandler handler) {
        while(iterator.hasNext()) {
            Page page = iterator.next();
            Assert.assertNotNull(page);
        }

        List<Page> pages = handler.getPages();
        Assert.assertNotNull(pages);
        Assert.assertEquals(2, pages.size());
        Assert.assertEquals(expectedPages, pages);
    }
}
