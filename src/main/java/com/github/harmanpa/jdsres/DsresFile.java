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
        if(!mats.contains("name")
                || !mats.contains("description")
                || !mats.contains("dataInfo")
                || !mats.contains("data_1")
                || !mats.contains("data_2")) {
            throw new IOException("Required matrices not included, not a valid dsres file");
        }
        String[] namestrings = toStringArray(loader.get("name"));
        String[] descriptionstrings = toStringArray(loader.get("description"));
        int[][] info = toIntMatrix(loader.get("dataInfo"));
        if (namestrings.length != descriptionstrings.length || namestrings.length != info.length) {
            throw new IOException("Names and descriptions don't match");
        }
        variables = new LinkedHashMap<String, DsresVariable>(namestrings.length);
        for (int i = 0; i < namestrings.length; i++) {
            variables.put(namestrings[i], new DsresVariable(loader, namestrings[i], descriptionstrings[i], info[i]));
        }
    }
    
    public final Map<String, DsresVariable> getVariables() {
        return variables;
    }
    
    public final DsresVariable getVariable(String name) {
        return variables.get(name);
    }
    
    static String[] toStringArray(MatVar mv) {
        if (mv.type() == MatVar.TEXT) {
            MatText mt = (MatText) mv;
            int[] dims = mv.getDim();
            String[] out = new String[dims[0]];
            for (int i = 0; i < dims[0]; i++) {
                char[] chars = new char[dims[1]];
                for (int j = 0; j < dims[1]; j++) {
                    chars[j] = mt.getChar(i, j);
                }
                out[i] = new String(chars).trim();
            }
            return out;
        }
        return new String[0];
    }
    
    static int[][] toIntMatrix(MatVar mv) {
        int[] dims = mv.getDim();
        int[][] out = new int[dims[0]][];
        for (int i = 0; i < dims[0]; i++) {
            int[] ints = new int[dims[1]];
            for (int j = 0; j < dims[1]; j++) {
                ints[j] = mv.getInt(i, j);
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
