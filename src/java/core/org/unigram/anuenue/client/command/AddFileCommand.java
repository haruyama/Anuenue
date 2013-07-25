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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unigram.anuenue.client.solr.SolrClient;


/**
 * A Class for "addFile" command.
 */
public final class AddFileCommand extends AbstractCommand {

    /** logger. */
    private static final Log LOG = LogFactory.getLog(AddFileCommand.class);

    /**
     * Constructor.
     *
     * @param client SolrClient
     * @param arg argument
     */
    public AddFileCommand(final SolrClient client, final String arg) {
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
            LOG.info("adding tsv file: " + getArgument());
            result = getSolrClient().updateTSVFile(new File(getArgument()));
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
     * add file one line at a time.
     *
     * @param fileName file name
     * @throws ConnectException in case of connection failure.
     */
    private void addOneByOne(final String fileName) throws ConnectException {
        try (FileInputStream fileInputStream = new FileInputStream(fileName);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
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
        } catch (FileNotFoundException e) {
            LOG.error(e.toString());
        } catch (IOException e) {
            LOG.error(e.toString());
        }

    }
}
