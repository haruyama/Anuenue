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
 * Class of "batchDelete" dist command.
 */
public final class BatchDeleteDistCommand extends AbstractDistCommand {

    /** logger. */
    private static final Log LOG = LogFactory.getLog(BatchDeleteDistCommand.class);

    /**
     * Constructor.
     * @param target target
     * @param arg argument
     * @param database database of node
     * @throws AnuenueException if argument is empty.
     */
    public BatchDeleteDistCommand(final String target, final String arg, final NodeDatabase database)
            throws AnuenueException {
        super(target, AnuenueCommandConstants.DISTCOMMAND_BATCHDELETE, arg, database, true);
    }

    /**
     * actual Implementation of execute().
     * @throws AnuenueException in case of any error
     */
    @Override
    protected void executeImpl() throws AnuenueException {
        deleteDir();
    }

    /**
     * delete files of directory.
     * @throws AnuenueException in case of any error
     */
    private void deleteDir() throws AnuenueException {
        String dirName = getArgument();
        File dir = new File(dirName);

        // if dir is a file (not a directory)
        if (dir.isFile()) {
            deleteFile(dir);
            return;
        }

        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            LOG.error("there is no files in directory: " + dirName);
            throw new AnuenueException("there is no files in directory: " + dirName);
        }

        for (int i = 0; i < files.length; i++) {
            deleteFile(files[i]);
        }
    }
}
