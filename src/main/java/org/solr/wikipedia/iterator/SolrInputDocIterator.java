package org.solr.wikipedia.iterator;

import org.apache.commons.lang3.Validate;
import org.apache.solr.common.SolrInputDocument;

import java.util.Iterator;

/**
 * Converts each element to a SolrInputDocument while iterating over
 * the base iterator.
 *
 * @author bryanbende
 */
public abstract class SolrInputDocIterator<T> implements Iterator<SolrInputDocument> {

    private Iterator<T> iterator;

    /**
     *
     * @param iterator
     */
    public SolrInputDocIterator(Iterator<T> iterator) {
        this.iterator = iterator;
        Validate.notNull(this.iterator);
    }


    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public SolrInputDocument next() {
        T t = iterator.next();
        return create(t);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    /**
     * Sub-classes determine how to convert object to SolrInputDocument.
     *
     * @param t
     * @return
     */
    protected abstract SolrInputDocument create(T t);

}
