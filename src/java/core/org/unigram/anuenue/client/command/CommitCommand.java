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

import org.unigram.anuenue.client.AnuenueCommandConstants;
import org.unigram.anuenue.client.solr.SolrClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A Class for "commit" command.
 */
public final class CommitCommand extends AbstractCommand {
    /** logger. */
    private static final Log LOG = LogFactory.getLog(CommitCommand.class);

    /** property: anuenue.http.connect.timeout.optimize. */
    private static final String PROPERTY_HTTP_READ_TIMEOUT_COMMIT = "anuenue.http.read.timeout.commit";

    /** HTTP read timeout(msec). */
    private static final String DEFAULT_HTTP_READ_TIMEOUT = "600000";

    /**
     * Constructer.
     *
     * @param client SolrClient
     * @param arg argument
     */
    public CommitCommand(final SolrClient client, final String arg) {
        super(client, arg);
        client.setReadTimeout(Integer.parseInt(System.getProperty(PROPERTY_HTTP_READ_TIMEOUT_COMMIT, DEFAULT_HTTP_READ_TIMEOUT)));
    }

    /**
     * run command.
     *
     * @return result of command
     * @throws IOException in case of IO error
     */
    @Override
    public String runCommand() throws IOException {
        LOG.info("commiting the changes");
        return getSolrClient().updateXML(AnuenueCommandConstants.COMMIT_COMMAND);
    }

}
