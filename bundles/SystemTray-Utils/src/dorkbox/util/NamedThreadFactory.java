/*
 * Copyright 2010 dorkbox, llc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dorkbox.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The default thread factory with names.
 */
public
class NamedThreadFactory implements ThreadFactory {
    private static final AtomicInteger poolId = new AtomicInteger();

    // permit this to be changed!
    /**
     * The stack size is arbitrary based on JVM implementation. Default is 0
     * 8k is the size of the android stack. Depending on the version of android, this can either change, or will always be 8k
     * <p/>
     * To be honest, 8k is pretty reasonable for an asynchronous/event based system (32bit) or 16k (64bit)
     * Setting the size MAY or MAY NOT have any effect!!!
     * <p/>
     * Stack size must be specified in bytes. Default is 8k
     */
    public static int stackSizeForThreads = 8192;

    private final AtomicInteger nextId = new AtomicInteger();

    private final ThreadGroup group;
    private final String namePrefix;
    private final int threadPriority;
    private final boolean daemon;

    /**
     * Creates a DAEMON thread
     */
    public
    NamedThreadFactory(String poolNamePrefix) {
        this(poolNamePrefix,
             Thread.currentThread()
                   .getThreadGroup(),
             Thread.MAX_PRIORITY,
             true);
    }

    public
    NamedThreadFactory(String poolNamePrefix, boolean isDaemon) {
        this(poolNamePrefix,
             Thread.currentThread()
                   .getThreadGroup(),
             Thread.MAX_PRIORITY,
             isDaemon);
    }


    /**
     * Creates a DAEMON thread
     */
    public
    NamedThreadFactory(String poolNamePrefix, ThreadGroup group) {
        this(poolNamePrefix, group, Thread.MAX_PRIORITY, true);
    }

    public
    NamedThreadFactory(String poolNamePrefix, ThreadGroup group, boolean isDaemon) {
        this(poolNamePrefix, group, Thread.MAX_PRIORITY, isDaemon);
    }

    /**
     * Creates a DAEMON thread
     */
    public
    NamedThreadFactory(String poolNamePrefix, int threadPriority) {
        this(poolNamePrefix, threadPriority, true);
    }

    public
    NamedThreadFactory(String poolNamePrefix, int threadPriority, boolean isDaemon) {
        this(poolNamePrefix, null, threadPriority, isDaemon);
    }

    /**
     * @param poolNamePrefix what you want the subsequent threads to be named.
     * @param group          the group this thread will belong to. If NULL, it will belong to the current thread group.
     * @param threadPriority Thread.MIN_PRIORITY, Thread.NORM_PRIORITY, Thread.MAX_PRIORITY
     */
    public
    NamedThreadFactory(String poolNamePrefix, ThreadGroup group, int threadPriority, boolean isDaemon) {
        this.daemon = isDaemon;
        this.namePrefix = poolNamePrefix + '-' + poolId.incrementAndGet();
        if (group == null) {
            this.group = Thread.currentThread()
                               .getThreadGroup();
        }
        else {
            this.group = group;
        }

        if (threadPriority != Thread.MAX_PRIORITY && threadPriority != Thread.NORM_PRIORITY && threadPriority != Thread.MIN_PRIORITY) {
            throw new IllegalArgumentException("Thread priority must be valid!");
        }
        this.threadPriority = threadPriority;
    }

    @Override
    public
    Thread newThread(Runnable r) {
        // stack size is arbitrary based on JVM implementation. Default is 0
        // 8k is the size of the android stack. Depending on the version of android, this can either change, or will always be 8k
        // To be honest, 8k is pretty reasonable for an asynchronous/event based system (32bit) or 16k (64bit)
        // Setting the size MAY or MAY NOT have any effect!!!
        Thread t = new Thread(this.group, r, this.namePrefix + '-' + this.nextId.incrementAndGet(), stackSizeForThreads);
        t.setDaemon(this.daemon);
        if (t.getPriority() != this.threadPriority) {
            t.setPriority(this.threadPriority);
        }
        return t;
    }
}
