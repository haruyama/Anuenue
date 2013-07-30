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
package org.unigram.anuenue.client.command;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unigram.anuenue.client.AnuenueCommandConstants;
import org.unigram.anuenue.client.solr.SolrClient;

/**
 * A Class for "optimize" command.
 */
public final class OptimizeCommand extends AbstractCommand {

    /** logger. */
    private static final Log LOG = LogFactory.getLog(OptimizeCommand.class);

    /** property: anuenue.http.connect.timeout.optimize. */
    private static final String PROPERTY_HTTP_READ_TIMEOUT_OPTIMIZE = "anuenue.http.read.timeout.optimize";

    /** HTTP read timeout(msec). */
    private static final String DEFAULT_HTTP_READ_TIMEOUT = "3600000";
    /**
     * Constructer.
     *
     * @param client SolrClient
     * @param arg argument
     */
    public OptimizeCommand(final SolrClient client, final String arg) {
        super(client, arg);
        client.setReadTimeout(Integer.parseInt(System.getProperty(PROPERTY_HTTP_READ_TIMEOUT_OPTIMIZE, DEFAULT_HTTP_READ_TIMEOUT)));
    }

    /**
     * run command.
     *
     * @return result of command
     * @throws IOException in case of IO error
     */
    @Override
    public String runCommand() throws IOException {
        LOG.info("optimizing index");
        return getSolrClient().updateXML(AnuenueCommandConstants.OPTIMIZE_COMMAND);
    }
}
