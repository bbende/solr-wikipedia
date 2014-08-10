package org.solr.wikipedia.solr;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.lang3.Validate;
import org.solr.wikipedia.model.Page;
import org.solr.wikipedia.model.Revision;

import java.util.UUID;

/**
 * Produces the Multimap of key/value pairs for a given Page to index in Solr.
 *
 * @author bryanbende
 */
public class PageMultimap {

    private Page page;

    public PageMultimap(Page page) {
        this.page = page;
        Validate.notNull(page);
    }

    public Multimap<String,Object> getMultimap() {
        Multimap<String,Object> multimap = HashMultimap.create();
        multimap.put(IndexField.id.name(), UUID.nameUUIDFromBytes(
                page.getTitle().getBytes()));
        multimap.put(IndexField.TITLE.name(), page.getTitle());

        for(Revision rev : page.getRevisions()) {
            multimap.put(IndexField.REVISION_TIMESTAMP.name(), rev.getTimestamp());
            multimap.put(IndexField.REVISION_TEXT.name(), rev.getText());
        }

        return multimap;
    }

}
