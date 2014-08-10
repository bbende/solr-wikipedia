package org.solr.wikipedia.util;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.CoreContainer;

import java.io.File;

/**
 * Helper to create EmbeddedSolrServer instances for testing.
 *
 * @author bryanbende
 */
public class EmbeddedSolrServerFactory {

    public static final String DEFAULT_SOLR_HOME = "src/main/resources/solr";

    public static final String DEFAULT_SOLR_XML = "solr.xml";

    public static final String DEFAULT_DATA_DIR = "/tmp";

    public static SolrServer create(String coreName) {
        return create(DEFAULT_SOLR_HOME, coreName, DEFAULT_DATA_DIR);
    }

    public static SolrServer create(String solrHome, String coreName, String dataDir) {
        File coreDataDir = new File(dataDir + "/" + coreName);
        if (coreDataDir.exists()) {
            coreDataDir.delete();
        }

        // TODO is there a better way to do this without putting it in solrconfig.xml ?
        System.setProperty("solr.data.dir", coreDataDir.getAbsolutePath());

        File solrXmlFile = new File(solrHome, DEFAULT_SOLR_XML);
        CoreContainer core = CoreContainer.createAndLoad(
                DEFAULT_SOLR_HOME, solrXmlFile);
        return new EmbeddedSolrServer(core, coreName);
    }
}
