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
import java.net.ConnectException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unigram.anuenue.client.solr.SolrClient;


/**
 * A Class for "addFile" command.
 */
public final class UpdateViaStreamFileCommand extends AbstractCommand {

    /** logger. */
    private static final Log LOG = LogFactory.getLog(UpdateViaStreamFileCommand.class);

    /** property: anuenue.http.connect.timeout.optimize. */
    private static final String PROPERTY_HTTP_READ_TIMEOUT_UPDATEVIASTREAMFILE = "anuenue.http.read.timeout.updateviastreamfile";

    /** HTTP read timeout(msec). */
    private static final String DEFAULT_HTTP_READ_TIMEOUT = "21600000";
    /**
    /**
     * Constructor.
     *
     * @param client SolrClient
     * @param arg argument
     */
    public UpdateViaStreamFileCommand(final SolrClient client, final String arg) {
        super(client, arg);
        client.setReadTimeout(Integer.parseInt(System.getProperty(PROPERTY_HTTP_READ_TIMEOUT_UPDATEVIASTREAMFILE, DEFAULT_HTTP_READ_TIMEOUT)));
    }

    /**
     * run command.
     * @return result of command
     * @throws IOException in case of IO error
     */
    @Override
    public String runCommand() throws IOException {
        String result = "";
        try {
            LOG.info("adding tsv file: " + getArgument());
            result = getSolrClient().updateTSVViaStreamFile(getArgument());
        } catch (ConnectException e) { // when server is down
            LOG.error(e.toString());
            throw e;
        }
        return result;
    }
}
