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

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unigram.anuenue.client.AnuenueCommandConstants;
import org.unigram.anuenue.exception.AnuenueException;
import org.unigram.anuenue.node.NodeDatabase;

/**
 * "addDir" Dist Command Class.
 */
public final class AddDirDistCommand extends AbstractDistCommand {

    /** logger. */
    private static final Log LOG = LogFactory.getLog(AddDirDistCommand.class);

    /**
     * Constructor.
     * @param target target
     * @param arg argument
     * @param database database of node
     * @throws AnuenueException if argument is empty.
     */
    public AddDirDistCommand(final String target, final String arg,
            final NodeDatabase database) throws AnuenueException {
        super(target, AnuenueCommandConstants.DISTCOMMAND_ADDDIR, arg, database, true);
    }

    /**
     * actual Implementation of execute().
     * @throws AnuenueException in case that adding a file is failed
     */
    @Override
    protected void executeImpl() throws AnuenueException {
        addDir();
    }

    /**
     * add by files of directory.
     * @throws AnuenueException in case that adding a file is failed
     */
    private void addDir() throws AnuenueException {
        String dirName = getArgument();
        File dir = new File(dirName);
        if (dir.isFile()) {
            LOG.info("input is specified as a file.");
            addFile(dir.toString());
            return;
        }

        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            LOG.error("there is no files in directory: " + dirName);
            throw new AnuenueException("there is no files in directory: " + dirName);
        }

        for (int i = 0; i < files.length; ++i) {
            String inputFile = files[i].toString();
            LOG.info("adding file: " + inputFile);
            addFile(inputFile);
        }
    }
}
