/*
 * Copyright (c) 2015-2018 by k3b.
 *
 * This file is part of AndroFotoFinder.
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 */
 
package de.k3b.android.util;

import android.os.Environment;

import java.io.File;

import de.k3b.io.FileUtils;
import de.k3b.io.OSDirectory;

/**
 * Created by k3b on 22.06.2016.
 */
public class OsUtils {
    public static File[] getExternalStorageDirFiles() {
        // other alternatives ar described here http://stackoverflow.com/questions/5694933/find-an-external-sd-card-location

        // i.e. /mnt/sdcard0
        File extDir = Environment.getExternalStorageDirectory();

        // i.e. /mnt
        File mountRoot = (extDir == null) ? null :extDir.getParentFile();

        return  (mountRoot != null) ? mountRoot.listFiles() : null;
    }

    private static boolean isAllowed(File mountFile) {
        return ((mountFile != null) && mountFile.isDirectory() && !mountFile.isHidden());
    }

    /**
     * Append path segments to given base path, returning result.
     */
    public static File buildPath(File base, String... segments) {
        File cur = base;
        for (String segment : segments) {
            if (cur == null) {
                cur = new File(segment);
            } else {
                cur = new File(cur, segment);
            }
        }
        return cur;
    }

    /**
     * create android specific dir root.
     *
     * @param factory null or factory that creates OSDirectory or subclass of OSDirectory.
     */
    public static OSDirectory getRootOSDirectory(OSDirectory factory) {
        // #103: bugfix
        // this works for android-4.4 an earlier and on rooted devices
        OSDirectory root = createOsDirectory(FileUtils.tryGetCanonicalFile("/"), factory);
        if (root.getChildren().size() == 0) {
            // on android-5.0 an newer root access is not allowed.
            // i.e. /storage/emulated/0
            File externalRoot = new File("/storage");
            if (externalRoot.listFiles().length == 0) {
                externalRoot = Environment.getExternalStorageDirectory();
            }
            if (externalRoot != null) {
                root = createOsDirectory(externalRoot, factory);
            }
        }
        return root;
    }

    private static OSDirectory createOsDirectory(File file, OSDirectory factory) {
        if (factory != null) return factory.createOsDirectory(file, null, null);
        return new OSDirectory(file, null, null);
    }

    public static File getDefaultPhotoRoot() {
        return new File(Environment.getExternalStorageDirectory(),Environment.DIRECTORY_DCIM);
    }
}
