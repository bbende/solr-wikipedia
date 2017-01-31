package org.solr.wikipedia.handler;

import org.junit.Assert;
import org.junit.Test;
import org.solr.wikipedia.model.Page;
import org.solr.wikipedia.model.Revision;
import org.solr.wikipedia.model.Contributor;

/**
 * @author bryanbende
 */
public class DefaultPageHandlerTest {

    @Test
    public void testPageHandler() {
    	final int id = 1;
    	final String username = "username1";
        final String title = "title";
        final String text1 = "text1";
        final String timestamp1 = "2001-01-15T13:15:00Z";

        PageHandler<Page> handler = new DefaultPageHandler();
        handler.startPage();
        handler.title(title);
        handler.startRevision();
        handler.text(text1);
        handler.timestamp(timestamp1);
        handler.startContributor();
        handler.id(Integer.toString(id));
        handler.username(username);
        handler.endContributor();
        handler.endRevision();

        Page page = handler.endPage();
        Assert.assertNotNull(page);
        Assert.assertEquals(title, page.getTitle());
        Assert.assertEquals(1, page.getRevisions().size());

        Revision rev1 = page.getRevisions().get(0);
        Assert.assertEquals(text1, rev1.getText());
        Assert.assertNotNull(rev1.getTimestamp());
        
        Contributor cont1 = rev1.getContributors().get(0);
        Assert.assertEquals(id, cont1.getId());
        Assert.assertEquals(username, cont1.getUsername());
    }

}
