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

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unigram.anuenue.client.command.AnuenueCommandFactory;
import org.unigram.anuenue.exception.AnuenueException;

/**
 * Maker and runner of commands.
 */
public final class AnuenueCommands {

    /** logger. */
    private static final Log LOG = LogFactory.getLog(AnuenueCommands.class);

    /**
     * private Constructor.
     */
    private AnuenueCommands() {
    }

    /**
     * main method.
     * @param args command line arguments.
     * @throws IOException in case of IO Error
     */
    public static void main(final String[] args) throws IOException {
        try {
            AnuenueCommandFactory.createCommand(args).runCommand();
        } catch (AnuenueException e) {
            LOG.error("error", e);
            System.exit(1);
        }
        System.exit(0);
    }
}
