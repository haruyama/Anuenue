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
package org.unigram.anuenue.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.unigram.anuenue.exception.AnuenueException;

/**
 * Utility class for org.apache.commons.cli.CommandLine.
 */
public final class SimpleCommandLineParser {

    /**
     * parser.
     */
    private final CommandLineParser parser;
    /**
     * command line.
     */
    private CommandLine commandLine;
    /**
     * options.
     */
    private final Options options;
    /**
     * Map of (option => defaultValue).
     */
    private final Map<String, String> defaultValueMap;

    /**
     * Add an option setting.
     * @param option option Name
     * @param description description
     * @param defaultValue default value
     */
    public void addOption(final String option, final String description, final String defaultValue) {
        options.addOption(option, option, true, description);
        defaultValueMap.put(option, defaultValue);
    }

    /**
     * Constructor.
     */
    public SimpleCommandLineParser() {
        parser = new PosixParser();
        options = new Options();
        defaultValueMap = new HashMap<String, String>();
    }

    /**
     * Parse arguments.
     * @param args arguments
     * @throws ParseException in case of parse error
     */
    public void parse(final String[] args) throws ParseException {
        commandLine = parser.parse(options, args);
    }

    /**
     * Get a value of option. if option is not set, return defaultValue.
     * @param option option name
     * @return value of option
     * @throws AnuenueException if option is not registered.
     */
    public String getValue(final String option) throws AnuenueException {
        if (commandLine.hasOption(option)) {
            return commandLine.getOptionValue(option);
        }
        if (defaultValueMap.containsKey(option)) {
            return defaultValueMap.get(option);
        }
        throw new AnuenueException("option is not registered: " + option);
    }
}
