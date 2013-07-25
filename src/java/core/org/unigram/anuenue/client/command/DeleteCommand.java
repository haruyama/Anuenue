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

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unigram.anuenue.client.solr.SolrClient;

/**
 * A Class for "delete" command.
 */
public final class DeleteCommand extends AbstractCommand {
    /** logger. */
    private static final Log LOG = LogFactory.getLog(DeleteCommand.class);

    /**
     * Constructer.
     *
     * @param client SolrClient
     * @param arg argument
     */
    public DeleteCommand(final SolrClient client, final String arg) {
        super(client, arg);
    }

    /**
     * run command.
     *
     * @return result of command
     * @throws IOException in case of IO error
     */
    @Override
    public String runCommand() throws IOException {
        LOG.info("deleting index");
        return getSolrClient().updateXML("<delete><query>"
                + StringEscapeUtils.escapeXml(getArgument()) + "</query></delete>");

    }

}
