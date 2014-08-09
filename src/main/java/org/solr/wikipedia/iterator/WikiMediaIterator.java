package org.solr.wikipedia.iterator;

import org.solr.wikipedia.handler.PageHandler;
import org.solr.wikipedia.parser.PageElement;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.Reader;
import java.util.Iterator;

/**
 * An iterator that can process the given WikiMedia XML stream and
 * and return objects produced by the handler. Clients can provide
 * a custom PageHandler implementation, or use the DefaultPageHandler
 * which produces Page objects.
 *
 * This class is not thread-safe and should only be used for single
 * threaded processing.
 *
 * @author bryanbende
 */
public class WikiMediaIterator<T> implements Iterator<T> {

    static final String PAGE = "page";

    /**
     * The reader for the given XML.
     */
    private final XMLStreamReader reader;

    /**
     * A handler to delegate events to and produce objects.
     */
    private final PageHandler<T> handler;

    /**
     * The current object produced from the handler, or null if
     * in process of building.
     */
    private T currentPage;

    /**
     *
     * @param reader
     * @param handler
     * @throws XMLStreamException
     */
    public WikiMediaIterator(Reader reader, PageHandler<T> handler)
            throws XMLStreamException {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        this.reader = factory.createXMLStreamReader(reader);
        this.handler = handler;
    }

    @Override
    public boolean hasNext() {
        if (reader == null) {
            return false;
        }

        // if at start of a page, stay and return true
        if (isStartPage()) {
            return true;
        }

        // otherwise proceed til a start page or end of stream
        boolean hasNext = false;
        try {
            while(reader.hasNext()) {
                reader.next();
                if (isStartPage()) {
                    hasNext = true;
                    break;
                }
            }
        } catch (XMLStreamException e) {
            //TODO incorporate a logging framework
            e.printStackTrace();
        }

        return hasNext;
    }

    @Override
    public T next() {
        // reset the current page
        currentPage = null;

        try {
            String elementText = null;
            while(reader.hasNext()) {
                int event = reader.next();
                switch (event) {
                    case XMLStreamConstants.START_ELEMENT:
                        handleStartElement(reader.getLocalName());
                        break;
                    case XMLStreamConstants.CHARACTERS:
                        elementText = reader.getText().trim();
                        break;
                    case XMLStreamConstants.END_ELEMENT:
                        handleEndElement(reader.getLocalName(), elementText);
                        break;
                }

                // this means we hit an end page so return
                if (currentPage != null) {
                    return currentPage;
                }
            }
        } catch (XMLStreamException e) {
            //TODO incorporate a logging framework
            e.printStackTrace();
        }

        // really shouldn't ever make it here
        return null;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    private void handleStartElement(String localName) {
        try {
            PageElement currElement = PageElement.valueOf(localName);
            switch(currElement) {
                case page:
                    handler.startPage();
                    break;
                case revision:
                    handler.startRevision();
                    break;
                default:
                    break;
            }
        } catch(IllegalArgumentException e) {
            // ignore any elements we don't care about
            return;
        }
    }

    private void handleEndElement(String localName, String elementText) {
        try {
            PageElement currElement = PageElement.valueOf(localName);
            switch(currElement) {
                case page:
                    currentPage = handler.endPage();
                    break;
                case revision:
                    handler.endRevision();
                    break;
                case title:
                    handler.title(elementText);
                    break;
                case text:
                    handler.text(elementText);
                    break;
                case timestamp:
                    handler.timestamp(elementText);
                    break;
                default:
                    break;
            }
        } catch(IllegalArgumentException e) {
            // ignore any elements we don't care about
            return;
        }
    }

    private boolean isStartPage() {
        if (reader.isStartElement() && PAGE.equals(reader.getLocalName())) {
            handler.startPage();
            return true;
        } else {
            return false;
        }
    }

}
