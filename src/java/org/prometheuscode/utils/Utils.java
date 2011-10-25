/*
 * Copyright (C) 2011 Marta Spodymek
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.prometheuscode.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Untilty method class.
 * 
 * @author marta
 * 
 */
public class Utils {

	/**
	 * Get class files File's from given package.
	 * 
	 * @param packageName
	 * @return Array of Files
	 * 
	 * @throws IllegalArgumentException
	 *             if package name does not exist or does not have files
	 *             classes.
	 */
	public static File[] getClassFilesFromPackage(String packageName) {

		FilenameFilter filter = new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				if (name.endsWith(".class")) {
					return true;
				}
				return false;
			}
		};

		String searchName = "/" + packageName.replace(".", "/");
		// System.out.print("searchName: " + searchName + "\n");
		URL dirName = Utils.class.getResource(searchName);
		if (dirName == null) {
			throw new IllegalArgumentException("Given package name does not exist");
		}

		File dir = null;
		try {
			dir = new File(dirName.toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		if (!dir.isDirectory()) {
			throw new IllegalArgumentException("List element: " + packageName + " is not correct package name");
		}

		File[] packageFiles = dir.listFiles(filter);
		if (packageFiles.length == 0) {
			throw new IllegalArgumentException("Given package name has no classes");
		}

		return packageFiles;
	}

}
