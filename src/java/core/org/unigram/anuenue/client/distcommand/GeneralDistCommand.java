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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.unigram.anuenue.client.AnuenueCommandConstants;
import org.unigram.anuenue.exception.AnuenueException;
import org.unigram.anuenue.node.NodeDatabase;
/**
 * General distribution command.
 */
public final class GeneralDistCommand extends AbstractDistCommand {

    /**
     * Constructor.
     * @param target target
     * @param command command name
     * @param arg argument
     * @param database database of node
     * @param requiresArgument true if command requires argument.
     * @throws AnuenueException if command requires argument and argument is empty
     */
    public GeneralDistCommand(final String target, final String command, final String arg, final NodeDatabase database, final boolean requiresArgument)
    throws AnuenueException {
        super(target, command, arg, database, requiresArgument);
    }

    /**
     * commands which GeneralDistCommand can process.
     */
    private static Set<String> commandSet = new HashSet<String>(Arrays.asList(
        AnuenueCommandConstants.COMMAND_ADDFILE,
        AnuenueCommandConstants.COMMAND_COMMIT,
        AnuenueCommandConstants.COMMAND_DELETE,
        AnuenueCommandConstants.COMMAND_DELETEALL,
        AnuenueCommandConstants.COMMAND_OPTIMIZE,
        AnuenueCommandConstants.COMMAND_PING,
        AnuenueCommandConstants.COMMAND_SOFT_COMMIT
    ));
    /**
     * commands which GeneralDistCommand can process.
     */
    private static Set<String> commandThatRequiresArgumentSet = new HashSet<String>(Arrays.asList(
        AnuenueCommandConstants.COMMAND_ADDFILE,
        AnuenueCommandConstants.COMMAND_DELETE
    ));

    /**
     * return whether command can be processed by this class.
     * @param command command name.
     * @return return true if command can be processed by this class.
     */
    public static boolean isGeneralDistCommandThatRequiresArgument(final String command) {
        return commandThatRequiresArgumentSet.contains(command);
    }

    /**
     * return whether command can be processed by this class.
     * @param command command name.
     * @return return true if command can be processed by this class.
     */
    public static boolean isGeneralDistCommand(final String command) {
        return commandSet.contains(command);
    }

    /**
     * actual Implementation of execute().
     * @throws AnuenueException in case of any error.
     */
    @Override
    protected void executeImpl() throws AnuenueException {
        executeCommand();
    }
}
