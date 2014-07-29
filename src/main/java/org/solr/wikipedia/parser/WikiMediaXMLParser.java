package org.solr.wikipedia.parser;

import org.solr.wikipedia.handler.PageHandler;

import java.io.Reader;

/**
 * Parses a WikiMedia XML stream and calls events on the given handler.
 *
 * @author bryanbende
 */
public interface WikiMediaXMLParser<T> {

    /**
     *
     * @param reader
     * @param handler
     */
    public void parse(Reader reader, PageHandler<T> handler);

}
