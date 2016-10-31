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

public enum OsType {
    Windows32("windows_32", ".dll"),
    Windows64("windows_64", ".dll"),
    Linux32("linux_32", ".so"),
    Linux64("linux_64", ".so"),
    MacOsX32("macosx_32", ".jnilib", ".dylib"),
    MacOsX64("macosx_64", ".jnilib", ".dylib"),
    Android("android", ".so"),

    /** Hard float, meaning floats are handled in hardware. WE ONLY SUPPORT HARD FLOATS! */
    LinuxArm32("linux_arm7_hf", ".so"),
    LinuxArm64("linux_arm8_hf", ".so"),
    ;

    private final String name;
    private final String[] libraryNames;

    OsType(String name, String... libraryNames) {
        this.name = name;
        this.libraryNames = libraryNames;
    }

    public String getName() {
        return this.name;
    }
    public String[] getLibraryNames() {
        return this.libraryNames;
    }


    public
    boolean is64bit() {
        return this == OsType.Linux64 || this == OsType.LinuxArm64 ||
               this == OsType.Windows64 || this == OsType.MacOsX64;
    }

    public
    boolean is32bit() {
        return this == OsType.Linux32 || this == OsType.LinuxArm32 ||
               this == OsType.Windows32 || this == OsType.MacOsX32 ||
               this == OsType.Android; // default android is 32bit
    }

    public
    boolean isArm() {
        return this == OsType.LinuxArm64 || this == OsType.LinuxArm32;
    }

    public
    boolean isLinux() {
        return this == OsType.Linux64 || this == OsType.Linux32 || isArm();
    }

    public
    boolean isWindows() {
        return this == OsType.Windows64 || this == OsType.Windows32;
    }

    public
    boolean isMacOsX() {
        return this == OsType.MacOsX64 || this == OsType.MacOsX32;
    }

    public
    boolean isAndroid() {
        return this == OsType.Android;
    }
}
