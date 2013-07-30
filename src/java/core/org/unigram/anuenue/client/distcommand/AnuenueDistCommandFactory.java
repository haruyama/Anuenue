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
package org.unigram.anuenue.client.distcommand;

import org.apache.commons.cli.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unigram.anuenue.client.AnuenueCommandConstants;
import org.unigram.anuenue.exception.AnuenueException;
import org.unigram.anuenue.node.NodeDatabase;
import org.unigram.anuenue.node.NodeDatabaseBuilder;
import org.unigram.anuenue.util.SimpleCommandLineParser;

/**
 * factory class of AnuenueDistCommand.
 */
public final class AnuenueDistCommandFactory {

    /** logger. */
    private static final Log LOG = LogFactory
            .getLog(AnuenueDistCommandFactory.class);

    /**
     * private constructor.
     */
    private AnuenueDistCommandFactory() {
    }

    /**
     * Create distribution command (target MASTER).
     * @param command command name
     * @param arg argument
     * @param database database of node
     * @return dist command class
     * @throws AnuenueException in case of unknown command
     */
    public static AbstractDistCommand createDistCommand(final String command,
            final String arg, final NodeDatabase database) throws AnuenueException {
        // for Test.
        return createDistCommand(AnuenueCommandConstants.TARGET_MASTER, command,
                arg, database);
    }

    /**
     * Create distribution command.
     * @param target target name
     * @param command command name
     * @param arg argument
     * @param database database of node
     * @return dist command class
     * @throws AnuenueException in case of unknown command
     */
    public static AbstractDistCommand createDistCommand(final String target,
            final String command, final String arg,
            final NodeDatabase database) throws AnuenueException {

        if (AnuenueCommandConstants.DISTCOMMAND_ADDDIR.equals(command)) {
            return new AddDirDistCommand(target, arg, database);
        } else if (AnuenueCommandConstants.DISTCOMMAND_UPDATEDIR.equals(command)) {
            return new UpdateDirDistCommand(target, arg, database);
        } else if (AnuenueCommandConstants.DISTCOMMAND_BATCHDELETE.equals(command)) {
            return new BatchDeleteDistCommand(target, arg, database);
        } else if (GeneralDistCommand.isGeneralDistCommandThatRequiresArgument(command)) {
            return new GeneralDistCommand(target, command, arg, database, true);
        } else if (GeneralDistCommand.isGeneralDistCommand(command)) {
            return new GeneralDistCommand(target, command, arg, database, false);
        }
        throw new AnuenueException("unknown command: " + command);
    }

    /**
     * Create distribution command.
     * @param args command line argument
     * @return dist command class
     * @throws AnuenueException in case of any error
     */
    public static AbstractDistCommand createDistCommand(final String[] args)
            throws AnuenueException {
        SimpleCommandLineParser parser = new SimpleCommandLineParser();
        parser.addOption(AnuenueCommandConstants.OPTION_CONF, "conf", AnuenueCommandConstants.DEFAULT_CONFIGURATION_FILENAME);
        parser.addOption(AnuenueCommandConstants.OPTION_COMMAND, "command", null);
        parser.addOption(AnuenueCommandConstants.OPTION_ARG, "arg", null);
        parser.addOption(AnuenueCommandConstants.OPTION_TARGET, "target", AnuenueCommandConstants.TARGET_MASTER);

        try {
            parser.parse(args);
        } catch (ParseException e) {
            LOG.fatal("parse error");
            System.exit(1);
        }

        String target = parser.getValue(AnuenueCommandConstants.OPTION_TARGET);
        String command = parser.getValue(AnuenueCommandConstants.OPTION_COMMAND);
        String arg = parser.getValue(AnuenueCommandConstants.OPTION_ARG);
        String nodeConfFile = parser.getValue(AnuenueCommandConstants.OPTION_CONF);

        if (command == null) {
            throw new AnuenueException("command not found");
        }

        return createDistCommand(target, command, arg, new NodeDatabaseBuilder(
                nodeConfFile).build());

    }

}
