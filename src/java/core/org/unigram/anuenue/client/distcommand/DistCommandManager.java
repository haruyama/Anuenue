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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Manager of DistCommandRunner.
 */
public final class DistCommandManager extends Thread {

    /** logger. */
    private static final Log LOG = LogFactory.getLog(DistCommandManager.class);

    /** the number of threads. */
    private int numberOfThreads;

    /**
     * Constructor.
     */
    public DistCommandManager() {
        numberOfThreads = 0;
    }

    /**
     * increment numberOfthread.
     */
    public void increment() {
        synchronized (this) {
            ++numberOfThreads;    
        }
    }

    /**
     * decrement numberOfthread.
     */
    public void decrement() {
        synchronized (this) {
            --numberOfThreads;    
            try {
                notifyAll();
            } catch (Exception e) {
                LOG.error(e.toString());
            }
        }
    }

    /**
     * wait that all thread is executed.
     */
    public void waitSync() {
        synchronized (this) {
            while (numberOfThreads > 0) {
                try {
                    wait();
                } catch (Exception e) {
                    LOG.error(e.toString());
                }
            }
        }
    }
}
