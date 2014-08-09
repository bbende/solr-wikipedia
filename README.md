solr-wikipedia
==============

A collection of utilities for parsing WikiMedia XML dumps with the intent of indexing
the content in Solr.

There are three main concepts:
1. Handlers - Receive events related to the WikiMedia XML and produce objects 
based on those events. The DefaultHandler produces Page objects, but clients
could implement a custom handler to produce another type of object.

2. Parser - A SAX parser for the WikiMedia XML. Clients pass in a Reader for
the XML and a handler to take action on events.

3. Iterator - An Iterator that uses StAX processing to produces objects based
on the given handler.

An example of parsing a bzip dump file:
<pre><code>
    String testWikiXmlFile = "src/test/resources/test-wiki-data.xml.bz2";
    
    WikiMediaXMLParser<Page> wikiMediaXMLParser = new SAXWikiMediaParser<>();
    PageHandler<Page> handler = new DefaultPageHandler();

    try (FileInputStream fileIn = new FileInputStream(testWikiXmlFile);
         BZip2CompressorInputStream bzipIn = new BZip2CompressorInputStream(fileIn);
         InputStreamReader reader = new InputStreamReader(bzipIn)) {

        wikiMediaXMLParser.parse(reader, handler);
        ...
    }
</code></pre>

An example of iterating over a bzip dump file:
<pre><code>
    String testWikiXmlFile = "src/test/resources/test-wiki-data.xml.bz2";
    
    try (FileInputStream fileIn = new FileInputStream(testWikiXmlFile);
         BZip2CompressorInputStream bzipIn = new BZip2CompressorInputStream(fileIn);
         InputStreamReader reader = new InputStreamReader(bzipIn)) {

        PageHandler<Page> handler = new DefaultPageHandler();

        Iterator<Page> iterator = new WikiMediaIterator<>(
                reader, handler);

        while(iterator.hasNext()) {
            Page page = iterator.next();
        }
    }
</code></pre>
