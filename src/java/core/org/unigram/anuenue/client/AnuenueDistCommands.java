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
package org.unigram.anuenue.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unigram.anuenue.client.distcommand.AbstractDistCommand;
import org.unigram.anuenue.client.distcommand.AnuenueDistCommandFactory;
import org.unigram.anuenue.exception.AnuenueException;

/**
 * Maker and runner of dist commands.
 */
public final class AnuenueDistCommands {

    /** logger. */
    private static final Log LOG = LogFactory.getLog(AnuenueDistCommands.class);

    /**
     * Constructor.
     *
     */
    private AnuenueDistCommands() {
    }

    /**
     * Main method.
     *
     * @param args
     *            command line arguments
     */
    public static void main(final String[] args) {
        AbstractDistCommand distCommand = null;
        try {
            distCommand = AnuenueDistCommandFactory.createDistCommand(args);
        } catch (AnuenueException e) {
            LOG.error("failed to create AnuenueDistCommand");
            LOG.error(e.toString());
            System.exit(1);
        }

        try {
            distCommand.execute();
        } catch (AnuenueException e) {
            LOG.error("failed to run command");
            LOG.error(e.toString());
            System.exit(1);
        }
        System.exit(0);

    }
}
