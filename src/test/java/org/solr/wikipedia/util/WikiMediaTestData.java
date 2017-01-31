package org.solr.wikipedia.util;

import org.solr.wikipedia.handler.CollectingPageHandler;
import org.solr.wikipedia.model.Page;

import java.util.List;

/**
 * @author bryanbende
 */
public class WikiMediaTestData {

    public static List<Page> getPages() {
        CollectingPageHandler handler = new CollectingPageHandler();

        handler.startPage();
        handler.title("AfghanistanGeography");
        handler.startRevision();
        handler.text("#REDIRECT [[Geography of Afghanistan]] {{R from CamelCase}}");
        handler.timestamp("2011-01-10T03:56:19Z");
        handler.startContributor();
        handler.username("Graham87");
        handler.id("194203");
        handler.endContributor();
        handler.endRevision();
        handler.endPage();

        handler.startPage();
        handler.title("AfghanistanPeople");
        handler.startRevision();
        handler.text("#REDIRECT [[Demography of Afghanistan]] {{R from CamelCase}}");
        handler.timestamp("2007-06-01T13:59:37Z");
        handler.startContributor();
        handler.username("RussBot");
        handler.id("279219");
        handler.endContributor();
        handler.endRevision();
        handler.endPage();

        return handler.getPages();
    }

}
