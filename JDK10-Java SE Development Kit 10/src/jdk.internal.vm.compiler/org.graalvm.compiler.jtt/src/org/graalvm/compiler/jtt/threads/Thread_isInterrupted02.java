/*
 * Copyright (c) 2007, 2012, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

/*
 */

package org.graalvm.compiler.jtt.threads;

import org.graalvm.compiler.jtt.JTTTest;
import org.graalvm.compiler.jtt.hotspot.NotOnDebug;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;

//Test all, mainly monitors
public class Thread_isInterrupted02 extends JTTTest {

    @Rule public TestRule timeout = NotOnDebug.create(Timeout.seconds(20));

    private static final Object start = new Object();
    private static final Object end = new Object();
    private static int waitTime;

    @SuppressWarnings("unused")
    public static boolean test(int i, int time) throws InterruptedException {
        waitTime = time;
        final Thread thread = new Thread();
        synchronized (thread) {
            // start the thread and wait for it
            thread.setDaemon(true); // in case the thread gets stuck
            thread.start();
            while (!thread.wait1Condition) {
                thread.wait(10000);
            }
        }
        synchronized (start) {
            thread.interrupt();
            thread.sentInterrupt = true;
        }
        synchronized (end) {
            while (!thread.wait2Condition) {
                end.wait(10000);
            }
        }
        return thread.interrupted;
    }

    private static class Thread extends java.lang.Thread {

        private boolean interrupted;
        private boolean sentInterrupt;
        private boolean wait1Condition;
        private boolean wait2Condition;

        @Override
        public void run() {
            try {
                synchronized (start) {
                    synchronized (this) {
                        // signal test thread that we are running
                        wait1Condition = true;
                        notify();
                    }
                    // wait for the condition, which should be interrupted
                    while (!sentInterrupt) {
                        if (waitTime == 0) {
                            start.wait();
                        } else {
                            start.wait(waitTime);
                        }
                        if (Thread.interrupted()) {
                            throw new InterruptedException();
                        }
                    }
                    Assert.fail("should not reach here - was not interrupted");
                }
            } catch (InterruptedException e) {
                // interrupted successfully.
                interrupted = true;
                synchronized (end) {
                    // notify the other thread we are done
                    wait2Condition = true;
                    end.notify();
                }
            }
        }
    }

    @Test
    public void run0() throws Throwable {
        initializeForTimeout();
        runTest("test", 0, 0);
    }

    @Test
    public void run1() throws Throwable {
        initializeForTimeout();
        runTest("test", 1, 500);
    }

}
