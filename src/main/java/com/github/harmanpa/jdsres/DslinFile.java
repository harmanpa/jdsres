/*
 * jdsres - Read Dymola / OpenModelica results in Java
 * Copyright (C) 2013 CyDesign Limited
 * Copyright (C) 2018 CAE Tech Limited
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
import java.util.List;
import java.util.Objects;

/**
 * Represents a .mat file used for linearization results in Dymola
 *
 * @author Peter Harman
 */
public class DslinFile {

    private final MatfileLoader loader;
    private final int nx;
    private final boolean transpose;
    private final String[] names;

    public DslinFile(File file) throws IOException {
        this(new FileInputStream(file));
    }

    public DslinFile(URL url) throws IOException {
        this(new MatfileLoader(url));
    }

    public DslinFile(InputStream is) throws IOException {
        this(new MatfileLoader(is));
    }

    protected DslinFile(MatfileLoader loader) throws IOException {
        this.loader = loader;
        loader.fillVariables();
        List<String> mats = Arrays.asList(loader.getNames());
        if (!mats.contains("Aclass")
                || !mats.contains("xuyName")
                || !mats.contains("nx")
                || !mats.contains("ABCD")) {
            throw new IOException("Required matrices not included, not a valid dslin file");
        }
        String[] aclass = toStringArray(loader.get("Aclass"), false);
        if (aclass.length != 4 || !"AlinearSystem".equals(aclass[0]) || !"1.0".equals(aclass[1])) {
            throw new IOException("Aclass version not supported or not a valid dslin file");
        }
        transpose = "binTrans".equals(aclass[3]);
        names = toStringArray(loader.get("xuyName"), transpose);
        nx = toIntMatrix(loader.get("nx"), transpose)[0][0];

    }

    public final DslinMatrix getA() {
        return new DslinMatrix(loader, "A", nx, transpose);
    }

    public final DslinMatrix getB() {
        return new DslinMatrix(loader, "B", nx, transpose);
    }

    public final DslinMatrix getC() {
        return new DslinMatrix(loader, "C", nx, transpose);
    }

    public final DslinMatrix getD() {
        return new DslinMatrix(loader, "D", nx, transpose);
    }

    public final DslinMatrix getABCD() {
        return new DslinMatrix(loader, "ABCD", nx, transpose);
    }

    public final int getNx() {
        return nx;
    }

    public final int getNu() {
        return loader.get("ABCD").getDim()[0] - nx;
    }

    public final int getNy() {
        return loader.get("ABCD").getDim()[1] - nx;
    }

    public final String[] getNames() {
        return names;
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
        hash = 41 * hash + Objects.hashCode(this.loader);
        hash = 41 * hash + this.nx;
        hash = 41 * hash + (this.transpose ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DslinFile other = (DslinFile) obj;
        if (this.nx != other.nx) {
            return false;
        }
        if (this.transpose != other.transpose) {
            return false;
        }
        return Objects.equals(this.loader, other.loader);
    }

}
