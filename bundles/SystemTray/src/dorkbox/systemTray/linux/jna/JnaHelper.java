/*
 * Copyright 2016 dorkbox, llc
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
package dorkbox.systemTray.linux.jna;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper method to get the library info from JNA when registering via direct map
 */
class JnaHelper {
    @SuppressWarnings("unchecked")
    static
    NativeLibrary register(final String libraryName, final Class<?> clazz) throws IllegalArgumentException {
        final Map<String, Object> options = new HashMap<String, Object>();
        options.put(Library.OPTION_CLASSLOADER, clazz.getClassLoader());

        final NativeLibrary library = NativeLibrary.getInstance(libraryName, options);
        if (library == null) {
            throw new IllegalArgumentException(libraryName + " doesn't exist or cannot be loaded.");
        }

        Native.register(clazz, library);
        return library;
    }
}
