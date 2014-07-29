package org.solr.wikipedia.handler;

import org.solr.wikipedia.model.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * Keeps all Pages in memory, should most be used for testing.
 *
 * @author bryanbende
 */
public class CollectingPageHandler extends DefaultPageHandler {

    private List<Page> pages = new ArrayList<>();

    @Override
    public Page endPage() {
        Page page = super.endPage();
        pages.add(page);
        return page;
    }

    public List<Page> getPages() {
        return pages;
    }
}
