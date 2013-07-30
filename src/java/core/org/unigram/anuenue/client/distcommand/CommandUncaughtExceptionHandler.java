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

import java.lang.Thread.UncaughtExceptionHandler;

/**
 * UncaughtExceptionHandler for AnuenueDistCommand.
 */
public final class CommandUncaughtExceptionHandler implements
        UncaughtExceptionHandler {

    /** if some exception throw, turn succeeded to false. */
    private boolean succeeded = true;

    /** last throwable. */
    private Throwable throwable;

    /**
     * @param thread thread
     * @param thr throwable
     */
    @Override
    public void uncaughtException(final Thread thread, final Throwable thr) {
        this.setThrowable(thr);
    }

    /**
     * get an error message.
     *
     * @return error message.
     */
    public String getErrorMassage() {
        return throwable.toString();
    }

    /**
     * return if uncaughtException is not called.
     *
     * @return if uncaughtException is not called, return true;
     */
    public boolean isSucceeded() {
        return succeeded;
    }

    /**
     * set throwable and change succeeded.
     *
     * @param thr Throwable
     */
    private void setThrowable(final Throwable thr) {
        throwable = thr;
        succeeded = false;
    }
}
