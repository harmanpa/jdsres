/*
 * jdsres - Read Dymola / OpenModelica results in Java
 * Copyright (C) 2013 CyDesign Limited
 * Author Peter Harman
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com.github.harmanpa.jdsres;

import edu.washington.biostr.sig.matfile.MatText;
import edu.washington.biostr.sig.matfile.MatVar;
import edu.washington.biostr.sig.matfile.MatfileLoader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author pete
 */
public class DsresFile {

    private final Map<String, DsresVariable> variables;

    public DsresFile(File file) throws IOException {
        this(new FileInputStream(file));
    }

    public DsresFile(URL url) throws IOException {
        this(new MatfileLoader(url));
    }

    public DsresFile(InputStream is) throws IOException {
        this(new MatfileLoader(is));
    }

    protected DsresFile(MatfileLoader loader) throws IOException {
        loader.fillVariables();
        List<String> mats = Arrays.asList(loader.getNames());
        if (!mats.contains("Aclass")
                || !mats.contains("name")
                || !mats.contains("description")
                || !mats.contains("dataInfo")
                || !mats.contains("data_1")
                || !mats.contains("data_2")) {
            throw new IOException("Required matrices not included, not a valid dsres file");
        }
        String[] aclass = toStringArray(loader.get("Aclass"), false);
        if (aclass.length != 4 || !"Atrajectory".equals(aclass[0]) || !"1.1".equals(aclass[1])) {
            throw new IOException("Aclass version not supported or not a valid dsres file");
        }
        boolean transpose = "binTrans".equals(aclass[3]);
        String[] namestrings = toStringArray(loader.get("name"), transpose);
        String[] descriptionstrings = toStringArray(loader.get("description"), transpose);
        int[][] info = toIntMatrix(loader.get("dataInfo"), transpose);
        if (namestrings.length != descriptionstrings.length || namestrings.length != info.length) {
            throw new IOException("Names and descriptions don't match");
        }
        variables = new LinkedHashMap<String, DsresVariable>(namestrings.length);
        for (int i = 0; i < namestrings.length; i++) {
            variables.put(namestrings[i], new DsresVariable(loader, namestrings[i], descriptionstrings[i], info[i], transpose));
        }
    }

    public final Map<String, DsresVariable> getVariables() {
        return variables;
    }

    public final DsresVariable getVariable(String name) {
        return variables.get(name);
    }

    static String[] toStringArray(MatVar mv, boolean transpose) {
        if (mv.type() == MatVar.TEXT) {
            MatText mt = (MatText) mv;
            int[] dims = mv.getDim();
            String[] out = new String[dims[transpose ? 1 : 0]];
            for (int i = 0; i < dims[transpose ? 1 : 0]; i++) {
                char[] chars = new char[dims[transpose ? 0 : 1]];
                for (int j = 0; j < dims[transpose ? 0 : 1]; j++) {
                    chars[j] = mt.getChar(transpose ? j : i, transpose ? i : j);
                }
                out[i] = new String(chars).trim();
            }
            return out;
        }
        return new String[0];
    }

    static int[][] toIntMatrix(MatVar mv, boolean transpose) {
        int[] dims = mv.getDim();
        int[][] out = new int[dims[transpose ? 1 : 0]][];
        for (int i = 0; i < dims[transpose ? 1 : 0]; i++) {
            int[] ints = new int[dims[transpose ? 0 : 1]];
            for (int j = 0; j < dims[transpose ? 0 : 1]; j++) {
                ints[j] = mv.getInt(transpose ? j : i, transpose ? i : j);
            }
            out[i] = ints;
        }
        return out;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.variables != null ? this.variables.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DsresFile other = (DsresFile) obj;
        if (this.variables != other.variables && (this.variables == null || !this.variables.equals(other.variables))) {
            return false;
        }
        return true;
    }
}
