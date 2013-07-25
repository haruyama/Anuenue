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

import java.io.IOException;

import org.unigram.anuenue.client.command.AbstractCommand;

/**
 * runner implementation of command.
 */
public final class DistCommandRunner extends Thread {

    /** Manager of DistCommandRunner. */
    private final DistCommandManager distCommandManager;

    /** Dist command. */
    private final AbstractCommand anuenueCommand;

    /**
     * Constructer.
     *
     * @param manager DistCommandManager
     * @param command AnuenueCommand
     */
    public DistCommandRunner(final DistCommandManager manager, final AbstractCommand command) {
        distCommandManager = manager;
        anuenueCommand = command;
        distCommandManager.increment();
    }

    /**
     * run given command.
     */
    public void run() {
        try {
            anuenueCommand.runCommand();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            distCommandManager.decrement();
        }
    }
}
