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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.ConnectException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unigram.anuenue.client.solr.SolrClient;

/**
 * A Class for "addData" command.
 */
public final class AddDataCommand extends AbstractCommand {

    /** logger. */
    private static final Log LOG = LogFactory.getLog(AddDataCommand.class);

    /**
     * Constructor.
     *
     * @param client SolrClient
     * @param arg argument
     */
    public AddDataCommand(final SolrClient client, final String arg) {
        super(client, arg);
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
            result = getSolrClient().updateTSVData(getArgument());
        } catch (ConnectException e) { // when server is down
            LOG.error(e.toString());
            throw e;
        } catch (IOException  e) { // in case input contains invalid lines
            LOG.warn(e.toString());
            LOG.warn("adding " + getArgument() + " one by one...");
            addOneByOne(getArgument());
        }
        return result;
    }

    /**
     * add data one line at a time.
     *
     * @param chunk chunkData
     * @throws ConnectException in case of connection failure.
     */
    private void addOneByOne(final String chunk) throws ConnectException {
        try (StringReader stringReader = new StringReader(chunk);
                BufferedReader bufferedReader = new BufferedReader(stringReader)) {
            String header = bufferedReader.readLine(); // fist line must be a header

            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                try {
                    getSolrClient().updateTSVData(header + "\n" + line);
                } catch (ConnectException e) {
                    LOG.error(e.toString());
                    throw e;
                } catch (IOException e) {
                    LOG.warn(e.toString());
                    LOG.warn("skipping invalid line: " + line);
                }
            }
        } catch (IOException e) {
            LOG.error(e.toString());
        }

    }
}
