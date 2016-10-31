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

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.TimeZone;


public
class OS {

    @Property
    /**
     * By default, the timer resolution in some operating systems are not particularly high-resolution (ie: A Thread.sleep(1) will not
     * really sleep for 1ms, but will really sleep for 16ms). This forces the JVM to use high resolution timers.
     */
    public static boolean FORCE_HIGH_RES_TIMER = true;


    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    public static final String LINE_SEPARATOR_UNIX = "\n";
    public static final String LINE_SEPARATOR_WINDOWS = "\r\n";

    public static final Charset US_ASCII = Charset.forName("US-ASCII");
    public static final Charset UTF_8 = Charset.forName("UTF-8");
    public static final Charset UTF_16LE = Charset.forName("UTF-16LE");

    private static final OsType osType;
    private static final String originalTimeZone = TimeZone.getDefault().getID();

    /**
     * The currently running java version as a NUMBER. For example, "Java version 1.7u45", and converts it into 7
     */
    public static final int javaVersion = _getJavaVersion();

    static {
        /**
         * By default, the timer resolution in some operating systems are not particularly high-resolution (ie: 'Thread.sleep(1)' will not
         * really sleep for 1ms, but will really sleep for 16ms). This forces the JVM to use high resolution timers
         */
        if (FORCE_HIGH_RES_TIMER) {
            // fix issues with java using low-resolution time on some machines (usually windows)
            Thread timerAccuracyThread = new Thread(new Runnable() {
                public void run() {
                    //noinspection InfiniteLoopStatement
                    while (true) {
                        try {
                            Thread.sleep(Long.MAX_VALUE);
                        } catch (Exception ignored) {
                        }
                    }
                }
            }, "ForceHighResTimer");
            timerAccuracyThread.setDaemon(true);
            timerAccuracyThread.start();
        }


        String osName = System.getProperty("os.name");
        String osArch = System.getProperty("os.arch");

        if (osName != null && osArch != null) {
            osName = osName.toLowerCase(Locale.US);
            osArch = osArch.toLowerCase(Locale.US);

            if (osName.startsWith("linux")) {
                // android check
                boolean isAndroid = false;
                String property = System.getProperty("javafx.platform");
                if (property != null) {
                    isAndroid = "android".equals(property.toLowerCase(Locale.US));
                } else {
                    property = System.getProperty("java.vm.name");
                    if (property != null) {
                        isAndroid = "dalvik".equals(property.toLowerCase(Locale.US));
                    }
                }

                if (isAndroid) {
                    osType  = OsType.Android;
                } else {
                    // normal linux 32/64/arm32/arm64
                    if ("amd64".equals(osArch)) {
                        osType  = OsType.Linux64;
                    } else {
                        if (osArch.startsWith("arm")) {
                            if (osArch.contains("v8")) {
                                osType  = OsType.LinuxArm64;
                            } else {
                                osType  = OsType.LinuxArm32;
                            }
                        } else {
                            osType  = OsType.Linux32;
                        }
                    }
                }
            } else if (osName.startsWith("windows")) {
                if ("amd64".equals(osArch)) {
                    osType = OsType.Windows64;
                } else {
                    osType = OsType.Windows32;
                }
            } else if (osName.startsWith("mac os x") || osName.startsWith("darwin")) {
                if ("x86_64".equals(osArch)) {
                    osType = OsType.MacOsX64;
                } else {
                    osType = OsType.MacOsX32;
                }
            } else {
                osType  = null;
            }
        } else {
            osType  = null;
        }
    }

    public static
    OsType get() {
        return osType;
    }

    public static
    boolean is64bit() {
        return osType.is64bit();
    }

    public static
    boolean is32bit() {
        return osType.is32bit();
    }

    public static
    boolean isArm() {
        return osType.isArm();
    }

    public static
    boolean isLinux() {
        return osType.isLinux();
    }

    public static
    boolean isWindows() {
        return osType.isWindows();
    }

    public static
    boolean isMacOsX() {
        return osType.isMacOsX();
    }

    public static
    boolean isAndroid() {
        return osType.isAndroid();
    }


    /**
     * Gets the currently running java version as a NUMBER. For example, "Java version 1.7u45", and converts it into 7
     */
    private static
    int _getJavaVersion() {
        String fullJavaVersion = System.getProperty("java.version");

        char versionChar;
        if (fullJavaVersion.startsWith("1.")) {
            versionChar = fullJavaVersion.charAt(2);
        } else {
            versionChar = fullJavaVersion.charAt(0);
        }

        switch (versionChar) {
            case '4': return 4;
            case '5': return 5;
            case '6': return 6;
            case '7': return 7;
            case '8': return 8;
            case '9': return 9;
            default: return -1;
        }
    }

    /**
     * Set our system to UTC time zone. Retrieve the <b>original</b> time zone via {@link #getOriginalTimeZone()}
     */
    public static
    void setUTC() {
        // have to set our default timezone to UTC. EVERYTHING will be UTC, and if we want local, we must explicitly ask for it.
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    /**
     * Returns the *ORIGINAL* system time zone, before (*IF*) it was changed to UTC
     */
    public static
    String getOriginalTimeZone() {
        return originalTimeZone;
    }

    /**
     * @return the optimum number of threads for a given task. Makes certain not to take ALL the threads, always returns at least one
     * thread.
     */
    public static
    int getOptimumNumberOfThreads() {
        return Math.max(Runtime.getRuntime()
                               .availableProcessors() - 2, 1);
    }

    @Override
    public final
    Object clone() throws java.lang.CloneNotSupportedException {
        throw new java.lang.CloneNotSupportedException();
    }
    public final
    void writeObject(ObjectOutputStream out) throws java.io.IOException {
        throw new java.io.NotSerializableException();
    }

    public final
    void readObject(ObjectInputStream in) throws java.io.IOException {
        throw new java.io.NotSerializableException();
    }
}
