solr-wikipedia
==============

A collection of utilities for parsing WikiMedia XML dumps with the intent of indexing
the content in Solr.

## Quick-Start
1. Download a Wikipedia dump file (http://en.wikipedia.org/wiki/Wikipedia:Database_download)

2. Download Solr 4.9 and extract (http://lucene.apache.org/solr/)

3. Configure environment variables

    Set SOLR_HOME to the location Solr was extracted to in Step 2 + "example", for example:
    export SOLR_HOME=/var/local/solr/example

    Set JAVA_HOME to the location of your JDK.

4. Clone and build code

    git clone https://github.com/bbende/solr-wikipedia.git
    
    cd solr-wikipedia
    
    mvn clean package -Pshade

5. Configure & start Solr

    ./deploy-wikipedia-collection.sh (copies src/main/resource/solr/wikiepediaCollection to $SOLR_HOME/solr/)
    
    src/main/resources/solr.sh start
    
    Check http://localhost:8983/solr in your browser

6. Ingest data (from solr-wikipedia dir)

    java -jar target/solr-wikipeida-1.0-SNAPSHOT.jar http://localhost:8984/solr/wikipediaCollection /var/local/test-wiki-data.xml.bz2

## Overview

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
