/*
 * Copyright (c) The Anuenue Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.unigram.anuenue.util;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * Factory Class of Anuenue Instance.
 */
public final class SimpleAnuenueInstanceFactory {

    /** logger. */
    private static final Log LOG = LogFactory.getLog(SimpleAnuenueInstanceFactory.class);

    /**
     * private Constructor.
     */
    private SimpleAnuenueInstanceFactory() {
    }

   /**
     * Create Anuenue instance for testing.
     * @param port port number
     * @param indexDir index directory
     * @param clusterConfigFile config file
     * @return Jetty Server
     * @throws Exception in case of any error.
     */
    public static Server createSolrInstance(final int port,
            final String indexDir, final String clusterConfigFile) throws Exception {

        FileUtils.deleteQuietly(new File(indexDir));
        Server server = new Server();
        SocketConnector connector = new SocketConnector();
        connector.setSoLingerTime(-1);
        connector.setPort(port);
        server.setConnectors(new Connector[] { connector });

        String currentDir = new File(".").getAbsoluteFile().getParent();
        LOG.info("current dir: " + currentDir);

        /* setting system properties */
        System.setProperty("solr.solr.home", currentDir + File.separator + "solr");
        System.setProperty("solr.data.dir", indexDir);
        System.setProperty("jetty.home", currentDir);
        System.setProperty("anuenue.hostName", "localhost");
        System.setProperty("anuenue.basePort", Integer.toString(port));

        //org.eclipse.jetty.util.log.Log.setLog(null);

        if (clusterConfigFile != null) {
            System.setProperty("anuenue.clusterconf", clusterConfigFile);
        }

        // add solr.war
        WebAppContext solrWac = new WebAppContext();
        // needed to load external lib
        solrWac.setClassLoader(Thread.currentThread().getContextClassLoader());
        solrWac.setServer(server);
        solrWac.setContextPath("/solr");
        solrWac.setWar("webapps" + File.separator + "solr.war");

        // add anuenue.war
        WebAppContext anuenueWac = new WebAppContext();
        anuenueWac.setServer(server);
        anuenueWac.setWar("webapps" + File.separator + "anuenue.war");
        anuenueWac.setContextPath("/anuenue");

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { solrWac, anuenueWac});
        server.setHandler(handlers);
        server.setStopAtShutdown(true);
        server.start();
        return server;
    }

    /**
     * Create Anuenue instance for testing.
     * @param port port number
     * @param indexDir index directory
     * @return Jetty Server
     * @throws Exception in case of any error.
     */
    public static Server createSolrInstance(final int port,
            final String indexDir) throws Exception {
        return createSolrInstance(port, indexDir, null);
    }

}
