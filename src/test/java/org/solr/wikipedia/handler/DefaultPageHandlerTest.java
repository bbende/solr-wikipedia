package org.solr.wikipedia.handler;

import org.junit.Assert;
import org.junit.Test;
import org.solr.wikipedia.model.Page;
import org.solr.wikipedia.model.Revision;

/**
 * @author bryanbende
 */
public class DefaultPageHandlerTest {

    @Test
    public void testPageHandler() {
        final String title = "title";
        final String text1 = "text1";
        final String timestamp1 = "2001-01-15T13:15:00Z";

        PageHandler<Page> handler = new DefaultPageHandler();
        handler.startPage();
        handler.title(title);
        handler.startRevision();
        handler.text(text1);
        handler.timestamp(timestamp1);
        handler.endRevision();

        Page page = handler.endPage();
        Assert.assertNotNull(page);
        Assert.assertEquals(title, page.getTitle());
        Assert.assertEquals(1, page.getRevisions().size());

        Revision rev1 = page.getRevisions().get(0);
        Assert.assertEquals(text1, rev1.getText());
        Assert.assertNotNull(rev1.getTimestamp());
    }

}
