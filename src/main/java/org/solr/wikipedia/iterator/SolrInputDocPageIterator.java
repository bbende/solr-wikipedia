package org.solr.wikipedia.iterator;

import com.google.common.collect.Multimap;
import org.apache.solr.common.SolrInputDocument;
import org.solr.wikipedia.indexer.PageMultimap;
import org.solr.wikipedia.model.Page;

import java.util.Collection;
import java.util.Iterator;

/**
 * Implementation of SolrInputDocIterator that processes Pages.
 *
 * @author bryanbende
 */
public class SolrInputDocPageIterator extends SolrInputDocIterator<Page> {

    /**
     * @param iterator
     */
    public SolrInputDocPageIterator(Iterator<Page> iterator) {
        super(iterator);
    }

    @Override
    protected SolrInputDocument create(Page page) {
        PageMultimap pageMultimap = new PageMultimap(page);
        Multimap<String,Object> multimap = pageMultimap.getMultimap();

        SolrInputDocument doc = new SolrInputDocument();
        for (String key : multimap.keySet()) {
            Collection<Object> values = multimap.get(key);
            for (Object value : values) {
                doc.addField(key, value);
            }
        }
        return doc;
    }

}
