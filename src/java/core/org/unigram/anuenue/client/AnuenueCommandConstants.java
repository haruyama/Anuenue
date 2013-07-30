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

/**
 * Constants for executing commands.
 */
public final class AnuenueCommandConstants {

    /**
     * Private Constructor.
     */
    private AnuenueCommandConstants() {
    }

    /** delete command string. */
    public static final String DELETE_ALL_COMMAND = "<delete><query>*:*</query></delete>";

    /** commit command string. */
    public static final String COMMIT_COMMAND = "<commit/>";

    /** soft commit command string. */
    public static final String SOFT_COMMIT_COMMAND = "<commit softCommit=\"true\"/>";

    /** optimize command string. */
    public static final String OPTIMIZE_COMMAND = "<optimize/>";

    /** default node name. */
    public static final String DEFAULT_NODE = "localhost";

    /** default port name. */
    public static final int DEFAULT_PORT = 8983;

    /** default configuration filename. */
    public static final String DEFAULT_CONFIGURATION_FILENAME = "conf/anuenue-nodes.xml";

    /** "conf" command line option string. */
    public static final String OPTION_CONF = "conf";

    /** "node" command line option string. */
    public static final String OPTION_NODE = "node";

    /** "host" command line option string. */
    public static final String OPTION_HOST = "host";

    /** "port" command line option string. */
    public static final String OPTION_PORT = "port";

    /** "name" command line option string. */
    public static final String OPTION_NAME = "name";

    /** "arg" command line option string. */
    public static final String OPTION_ARG = "arg";

    /** "command" command line option string. */
    public static final String OPTION_COMMAND = "command";

    /** "target" command line option string. */
    public static final String OPTION_TARGET = "target";

    /** "master" target. */
    public static final String TARGET_MASTER = "master";

    /** "merger" target. */
    public static final String TARGET_MERGER = "merger";

    /** "slave" target. */
    public static final String TARGET_SLAVE = "slave";

    /** "all" target. */
    public static final String TARGET_ALL = "all";

    /** "addDir" distribution command. */
    public static final String DISTCOMMAND_ADDDIR = "addDir";

    /** "updateDir" distribution command. */
    public static final String DISTCOMMAND_UPDATEDIR = "updateDir";

    /** "batchDelete" distribution command. */
    public static final String DISTCOMMAND_BATCHDELETE = "batchDelete";

    /** "addFile" command. */
    public static final String COMMAND_ADDFILE = "addFile";

    /** "deleteAll" command. */
    public static final String COMMAND_DELETEALL = "deleteAll";

    /** "delete" command. */
    public static final String COMMAND_DELETE = "delete";

    /** "optimize" command. */
    public static final String COMMAND_OPTIMIZE = "optimize";

    /** "commit" command. */
    public static final String COMMAND_COMMIT = "commit";

    /** "commit" command. */
    public static final String COMMAND_SOFT_COMMIT = "softcommit";

    /** "ping" command. */
    public static final String COMMAND_PING = "ping";

    /** "updateViaStreamFile" command. */
    public static final String COMMAND_UPDATEVIASTREAMFILE = "updateViaStreamFile";

    /** "addData" command. */
    public static final String COMMAND_ADDDATA = "addData";
}
