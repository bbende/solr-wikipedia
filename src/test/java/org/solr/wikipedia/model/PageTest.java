package org.solr.wikipedia.model;

import org.junit.Assert;
import org.junit.Test;
import org.solr.wikipedia.model.Page;
import org.solr.wikipedia.model.Revision;

import java.util.Date;

import static org.solr.wikipedia.model.Page.PageBuilder;
import static org.solr.wikipedia.model.Revision.RevisionBuilder;

/**
 * @author bryanbende
 */
public class PageTest {

    @Test
    public void testBuilder() {
        final String title = "test title";
        final String text = "test text";
        final Date date = new Date();

        RevisionBuilder revBuilder = new RevisionBuilder();
        revBuilder.text(text);
        revBuilder.timestamp(date);

        PageBuilder builder = new PageBuilder();
        builder.title(title);
        builder.revision(revBuilder.build());
        builder.revision(revBuilder.build());

        Page page = builder.build();
        Assert.assertNotNull(page);
        Assert.assertEquals(title, page.getTitle());
        Assert.assertEquals(2, page.getRevisions().size());

        Revision rev1 = page.getRevisions().get(0);
        Assert.assertEquals(text, rev1.getText());
        Assert.assertEquals(date, rev1.getTimestamp());

        Page page2 = builder.build();
        Assert.assertEquals(page, page2);
    }

}
