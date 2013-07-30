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

import org.apache.commons.cli.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unigram.anuenue.client.AnuenueCommandConstants;
import org.unigram.anuenue.client.solr.SolrClient;
import org.unigram.anuenue.exception.AnuenueException;
import org.unigram.anuenue.node.Node;
import org.unigram.anuenue.util.SimpleCommandLineParser;

/**
 * Factory class of AnuenueCommand.
 */
public final class AnuenueCommandFactory {

    /** logger. */
    private static final Log LOG = LogFactory.getLog(AnuenueCommandFactory.class);

    /**
     * private Constructer.
     */
    private AnuenueCommandFactory() {
    }

    /**
     * Create AnuenueCommand.
     *
     * @param node target Node
     * @param command command
     * @param arg argument of command
     *
     * @return AbstractAnuenueCommand class
     * @throws AnuenueException in case of command failure.
     */
    public static AbstractCommand createCommand(final Node node,
            final String command, final String arg) throws AnuenueException {

        return createCommand(node.getHostName(), node.getPortNumber(), command, arg);
    }

    /**
     * Create AnuenueCommand.
     *
     * @param host host name
     * @param port port number
     * @param command command
     * @param arg argument of command
     *
     * @return AbstractAnuenueCommand class
     * @throws AnuenueException in case of command failure.
     */
    public static AbstractCommand createCommand(final String host, final int port,
            final String command, final String arg) throws AnuenueException {

        return createCommand(new SolrClient(host, port), command, arg);
    }

    /**
     * Create AnuenueCommand.
     *
     * @param client SolrClient
     * @param command command
     * @param arg argument of command
     *
     * @return AbstractAnuenueCommand class
     * @throws AnuenueException in case of command failure.
     */
    public static AbstractCommand createCommand(final SolrClient client,
            final String command, final String arg) throws AnuenueException {

        switch(command) {
        case AnuenueCommandConstants.COMMAND_ADDFILE:
            return new AddFileCommand(client, arg);
        case AnuenueCommandConstants.COMMAND_ADDDATA:
            return new AddDataCommand(client, arg);
        case AnuenueCommandConstants.COMMAND_DELETEALL:
            return new DeleteAllCommand(client, arg);
        case AnuenueCommandConstants.COMMAND_DELETE:
            return new DeleteCommand(client, arg);
        case AnuenueCommandConstants.COMMAND_OPTIMIZE:
            return new OptimizeCommand(client, arg);
        case AnuenueCommandConstants.COMMAND_COMMIT:
            return new CommitCommand(client, arg);
        case AnuenueCommandConstants.COMMAND_SOFT_COMMIT:
            return new SoftCommitCommand(client, arg);
        case AnuenueCommandConstants.COMMAND_PING:
            return new PingCommand(client, arg);
        case AnuenueCommandConstants.COMMAND_UPDATEVIASTREAMFILE:
            return new UpdateViaStreamFileCommand(client, arg);
        default:
            throw new AnuenueException("Command not found.");
        }
    }

    /**
     * Create AnuenueCommand.
     * @param args command line arguments.
     * @return AbstractAnuenueCommand class.
     * @throws AnuenueException in case of any error.
     */
    public static AbstractCommand createCommand(final String[] args) throws AnuenueException {
        SimpleCommandLineParser parser = new SimpleCommandLineParser();
        parser.addOption(AnuenueCommandConstants.OPTION_HOST, "host", null);
        parser.addOption(AnuenueCommandConstants.OPTION_PORT, "port", "0");
        parser.addOption(AnuenueCommandConstants.OPTION_COMMAND, "command", null);
        parser.addOption(AnuenueCommandConstants.OPTION_ARG, "arg", null);

        try {
            parser.parse(args);
        } catch (ParseException e) {
            LOG.error("arguments parse error");
            throw new AnuenueException("arguments parse error.", e);
        }

        String host = parser.getValue(AnuenueCommandConstants.OPTION_HOST);
        int port = Integer.parseInt(parser.getValue(AnuenueCommandConstants.OPTION_PORT));
        String command = parser.getValue(AnuenueCommandConstants.OPTION_COMMAND);
        String arg = parser.getValue(AnuenueCommandConstants.OPTION_ARG);

        if (command == null) {
            LOG.error("no command specified.");
            throw new AnuenueException("no command specified");
        } else if (port == 0) {
            LOG.error("no port specified.");
            throw new AnuenueException("no port specified");
        }

        if (host == null) {
            LOG.info("no host specified. use localhost");
            host = "localhost";
        }
        System.out.println(host);
        return createCommand(host, port, command, arg);
    }
}
