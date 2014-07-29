package org.solr.wikipedia.parser;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.solr.wikipedia.model.Page;
import org.solr.wikipedia.handler.CollectingPageHandler;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * @author bryanbende
 */
public class SAXWikiMediaXMLParserTest {

    private List<Page> expectedPages;

    @Before
    public void setup() {
        CollectingPageHandler handler = new CollectingPageHandler();

        handler.startPage();
        handler.title("AfghanistanGeography");
        handler.startRevision();
        handler.text("#REDIRECT [[Geography of Afghanistan]] {{R from CamelCase}}");
        handler.timestamp("2011-01-10T03:56:19Z");
        handler.endRevision();
        handler.endPage();

        handler.startPage();
        handler.title("AfghanistanPeople");
        handler.startRevision();
        handler.text("#REDIRECT [[Demography of Afghanistan]] {{R from CamelCase}}");
        handler.timestamp("2007-06-01T13:59:37Z");
        handler.endRevision();
        handler.endPage();

        expectedPages = handler.getPages();
    }

    @Test
    public void testParse() {
        String testWikiXmlFile = "src/test/resources/test-wiki-data.xml";

        WikiMediaXMLParser<Page> wikiMediaXMLParser = new SAXWikiMediaXMLParser<>();
        CollectingPageHandler handler = new CollectingPageHandler();

        try (FileReader reader = new FileReader(testWikiXmlFile)) {
            wikiMediaXMLParser.parse(reader, handler);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Page> pages = handler.getPages();
        Assert.assertNotNull(pages);
        Assert.assertEquals(2, pages.size());
        Assert.assertEquals(expectedPages, pages);
    }

}
