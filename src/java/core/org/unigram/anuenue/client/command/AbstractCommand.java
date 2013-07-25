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

import org.unigram.anuenue.client.solr.SolrClient;

/**
 * Base class of command.
 */
public abstract class AbstractCommand {

    /** SolrClient. */
    private final SolrClient solrClient;

    /** argument of command. */
    private final String argument;

    /**
     * get SolrClient instance.
     *
     * @return SolrClient
     */
    protected final SolrClient getSolrClient() {
        return solrClient;
    }

    /**
     * get argument.
     *
     * @return argument
     */
    protected final String getArgument() {
        return argument;
    }

    /**
     * Constructer.
     * @param client SolrClient
     * @param arg argument of command
     */
    protected AbstractCommand(final SolrClient client, final String arg) {
        solrClient = client;
        argument = arg;
    }

    /**
     * run command.
     * @return result of command
     * @throws IOException in case of IO Error
     */
    public abstract String runCommand() throws IOException;
}
