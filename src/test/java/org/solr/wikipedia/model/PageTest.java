package org.solr.wikipedia.model;

import org.junit.Assert;
import org.junit.Test;
import org.solr.wikipedia.model.Page;
import org.solr.wikipedia.model.Revision;
import org.solr.wikipedia.model.Contributor;

import java.util.Date;

import static org.solr.wikipedia.model.Page.PageBuilder;
import static org.solr.wikipedia.model.Revision.RevisionBuilder;
import static org.solr.wikipedia.model.Contributor.ContributorBuilder;

/**
 * @author bryanbende
 */
public class PageTest {

    @Test
    public void testBuilder() {
    	final int id = 1;
    	final String username = "test username";
        final String title = "test title";
        final String text = "test text";
        final Date date = new Date();
        
        ContributorBuilder contBuilder = new ContributorBuilder();
        contBuilder.id(id);
        contBuilder.username(username);

        RevisionBuilder revBuilder = new RevisionBuilder();
        revBuilder.text(text);
        revBuilder.timestamp(date);
        revBuilder.contributor(contBuilder.build());
        revBuilder.contributor(contBuilder.build());

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
        Assert.assertEquals(2, rev1.getContributors().size());
        
        Contributor cont1 = rev1.getContributors().get(0);
        Assert.assertEquals(id, cont1.getId());
        Assert.assertEquals(username, cont1.getUsername());

        Page page2 = builder.build();
        Assert.assertEquals(page, page2);
    }

}
