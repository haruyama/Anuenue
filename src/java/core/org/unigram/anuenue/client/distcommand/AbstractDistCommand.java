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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unigram.anuenue.client.AnuenueCommandConstants;
import org.unigram.anuenue.client.command.AnuenueCommandFactory;
import org.unigram.anuenue.exception.AnuenueException;
import org.unigram.anuenue.node.Node;
import org.unigram.anuenue.node.NodeDatabase;

/**
 * base class of dist command.
 */
public abstract class AbstractDistCommand {

    /** maximum line in one file. */
    private int maxLinePerFile;

    /** property: max line. */
    private static final String PROPERTY_MAX_LINE_PER_FILE = "anuenue.post.max.line";

    /** default: maximum number of line to be sent one time. */
    private static final String DEFAULT_MAX_LINE_PER_FILE = "100";

    /** factor of thread number which multiplies size of the master nodes. */
    private final double factorOfThreadNumber;

    /** property: factor of thread number. */
    private static final String PROPERTY_FACTOR_OF_THREAD_NUMBER = "anuenue.post.factor.of.thread.number";

    /** default: factor of thread number which multiplies size of the master nodes. */
    private static final String DEFAULT_FACTOR_OF_THREAD_NUMBER = "3.0";

    /** logger. */
    private static final Log LOG = LogFactory.getLog(AbstractDistCommand.class);

    /** database of node. */
    private final NodeDatabase nodeDatabase;

    /** command to be executed. */
    private final String commandName;

    /** target node type (master, slave, backup, merger, all). */
    private final String targetName;

    /** argument of the command. */
    private final String argument;

    /**
     * get command name.
     * @return command name
     */
    public final String getCommandName() {
        return commandName;
    }

    /**
     * get argument.
     * @return argument
     */
    public final String getArgument() {
        return argument;
    }

    /**
     * get target name.
     * @return target name
     */
    public final String getTargetName() {
        return targetName;
    }

    /**
     * Constructor.
     *
     * @param target target
     * @param command command
     * @param arg argument
     * @param database database of node
     * @param requiresArgument this command requires argument.
     * @throws AnuenueException if command requires argument and argument is empty.
     */
    public AbstractDistCommand(final String target, final String command,
            final String arg, final NodeDatabase database, final boolean requiresArgument) throws AnuenueException {
        targetName = target;
        commandName = command;
        argument = arg;

        if (requiresArgument && isArgumentEmpty()) {
            throw new AnuenueException(command + " DistCommand requires an argument. Please specify --arg option and value.");
        }

        nodeDatabase = database;

        // set the configuration on maximum line per file
        maxLinePerFile = Integer.parseInt(System.getProperty(PROPERTY_MAX_LINE_PER_FILE, DEFAULT_MAX_LINE_PER_FILE));
        factorOfThreadNumber = Double.parseDouble(System.getProperty(PROPERTY_FACTOR_OF_THREAD_NUMBER, DEFAULT_FACTOR_OF_THREAD_NUMBER));
    }

    /**
     * set maxLinePerFile.
     * @param line new maxLinePerFile
     */
    public final void setMaxLinePerFile(final int line) {
        maxLinePerFile = line;
    }

    /**
     * execute dist command.
     *
     * @throws AnuenueException
     *             in case of any error
     */
    public final void execute() throws AnuenueException {
        CommandUncaughtExceptionHandler exceptionHandler = new CommandUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(exceptionHandler);

        executeImpl();

        if (!exceptionHandler.isSucceeded()) {
            throw new AnuenueException(exceptionHandler.getErrorMassage());
        }
    }

    /**
     * actual implementation of execute().
     *
     * @throws AnuenueException
     *             in case of any error.
     */
    protected abstract void executeImpl() throws AnuenueException;

    /**
     * exectute command.
     *
     * @throws AnuenueException
     *             in case of any error.
     */
    protected final void executeCommand() throws AnuenueException {
        executeCommand(getTargetNodeList(), commandName, argument);
    }

    /**
     * execute command for nodeList.
     *
     * @param nodeList
     *            information of target nodes.
     * @param command
     *            command
     * @param arg
     *            argument
     * @throws AnuenueException
     *             in case of any error.
     */
    protected static void executeCommand(final List<Node> nodeList,
            final String command, final String arg) throws AnuenueException {

        DistCommandManager manager = new DistCommandManager();
        try {
            for (Node node : nodeList) {
                DistCommandRunner runner = new DistCommandRunner(manager,
                        AnuenueCommandFactory.createCommand(node, command, arg));
                runner.start();
            }
        } catch (RuntimeException e) {
            LOG.error("detected runtimeexception");
            throw new AnuenueException(e);
        } finally {
            manager.waitSync();
        }

    }

    /**
     * get target nodeList.
     *
     * @return target node list.
     * @throws AnuenueException
     *             in case that no node is found.
     */
    private List<Node> getTargetNodeList() throws AnuenueException {
        if (targetName == null || targetName.length() == 0) {
            LOG.warn("no targets are specified.");
            return null;
        }
        if (targetName.equals(AnuenueCommandConstants.TARGET_MASTER)) {
            return nodeDatabase.getMasterList();
        } else if (targetName.equals(AnuenueCommandConstants.TARGET_SLAVE)) {
            return nodeDatabase.getSlaveList();
        } else if (targetName.equals(AnuenueCommandConstants.TARGET_ALL)) {
            return nodeDatabase.getNodeList();
        } else {
            return getTargetNodeList(targetName);
        }
    }

    /**
     * get target node list.
     *
     * @param targetList CSV format of target list
     * @return target node list.
     * @throws AnuenueException
     *             in case that no node is found.
     */
    private List<Node> getTargetNodeList(final String targetList) throws AnuenueException {
        String[] names = targetList.split(",");
        List<Node> targetNodeList = new ArrayList<Node>();
        for (int i = 0; i < names.length; ++i) {
            String name = names[i];
            Node node = nodeDatabase.getNodeByName(name);
            if (node == null) {
                throw new AnuenueException("No instance name like " + name);
            }
            targetNodeList.add(node);
        }
        return targetNodeList;
    }

    /**
     * add file.
     * @param fileName file name.
     * @throws AnuenueException in case of IO error.
     */
    protected final void addFile(final String fileName) throws AnuenueException {

        // get target instances
        List<Node> nodeList = getTargetNodeList();

        int dataNodeSize = nodeList.size();
        long totalNumberOfThread = Math.round(dataNodeSize * factorOfThreadNumber);

        try (TSVFileReader reader = new TSVFileReader(fileName)) {
            reader.init();
            ALL_DATA_WERE_PROCESSED: while (true) {
                DistCommandManager manager = new DistCommandManager();
                for (int k = 0; k < totalNumberOfThread; ++k) {
                    String chunk = reader.readChunk(maxLinePerFile);
                    if (chunk == null) {
                        manager.waitSync();
                        break ALL_DATA_WERE_PROCESSED;
                    }
                    new DistCommandRunner(manager,
                            AnuenueCommandFactory.createCommand(
                                    nodeList.get(k % dataNodeSize),
                                    AnuenueCommandConstants.COMMAND_ADDDATA,
                                    chunk)).start();
                }
                manager.waitSync();
            }
        } catch (IOException e) {
            throw new AnuenueException("addFile() failed", e);
        }
    }

    /**
     * delete file.
     * @param inputFile File
     * @throws AnuenueException in case of IO error
     */
    protected final void deleteFile(final File inputFile)
            throws AnuenueException {

        try (FileInputStream fileInputStream = new FileInputStream(inputFile);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            String[] strAry;

            /*
             * WARN: currently "id" of input document is specified as the first column.
             */
            String header = bufferedReader.readLine();
            if (header == null) {
                LOG.error("input file: " + inputFile.toString() + "has no content.");
                throw new AnuenueException("input file: " + inputFile.toString() + "has no content.");
            }
            String[] headerAry = header.split("\t");
            String idTag = headerAry[0];
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                strAry = line.split("\t");
                if (strAry.length != 0 && strAry[0] != null) {
                    executeCommand(getTargetNodeList(),
                            AnuenueCommandConstants.COMMAND_DELETE, idTag + ":"
                                    + strAry[0]);
                }
            }
        } catch (IOException e) {
            LOG.fatal("IO Error", e);
            throw new AnuenueException("IO Error", e);
        }
    }

    /**
     * Check Argument is empty.
     * @return true if argument is empty
     */
    protected final boolean isArgumentEmpty() {
        if (argument == null || "".equals(argument)) {
            return true;
        }
        return false;
    }

    /**
     * A Reader of TSV File.
     */
    private static class TSVFileReader implements AutoCloseable {

        /** BufferedReader. */
        private BufferedReader reader = null;

        /** FileInputStream. */
        private FileInputStream fis = null;

        /** InputStreamReader. */
        private InputStreamReader isr = null;

        /** filename. */
        private final String inputFileName;

        /** The header of file. */
        private String header;

        /** has more chunk. */
        private boolean hasNext;

        /** Constructor.
         *
         * @param filename filename
         */
        public TSVFileReader(final String filename) {
            inputFileName = filename;
            hasNext = true;
        }

        /**
         * Initialize.
         *
         * @throws IOException in case of IO error.
         * @throws AnuenueException in case that a file is empty.
         */
        public void init() throws IOException, AnuenueException {
            fis = new FileInputStream(inputFileName);
            isr = new InputStreamReader(fis, "UTF-8");
            reader = new BufferedReader(isr);

            header = reader.readLine();
            if (header == null) {
                LOG.error("input file: " + inputFileName + "has no content.");
                throw new AnuenueException("input file: " + inputFileName + "has no content.");
            }
            header += "\n";
        }

        /**
         * read a chunk.
         *
         * @param maxLineNumber max line number.
         * @return a chunk started with header
         * @throws IOException in case of IO error.
         */
        public String readChunk(final int maxLineNumber) throws IOException {
            if (!hasNext) {
                return null;
            }

            StringBuffer builder = new StringBuffer(header);

            for (int i = 0; i < maxLineNumber; ++i) {
                String line = reader.readLine();
                if (line == null) {
                    hasNext = false;
                    break;
                }
                builder.append(line).append('\n');
            }
            return builder.toString();
        }

        /**
         * close readers.
         */
        public void close() {
            IOUtils.closeQuietly(reader);
            IOUtils.closeQuietly(isr);
            IOUtils.closeQuietly(fis);
        }

    }
}
