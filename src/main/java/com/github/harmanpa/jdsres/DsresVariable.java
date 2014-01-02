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

import edu.washington.biostr.sig.matfile.MatVar;
import edu.washington.biostr.sig.matfile.MatfileLoader;
import java.util.Arrays;

/**
 *
 * @author pete
 */
public class DsresVariable {

    private final MatfileLoader loader;
    private final String name;
    private final String description;
    private final int[] info;
    private final boolean transpose;

    DsresVariable(MatfileLoader loader, String name, String description, int[] info, boolean transpose) {
        this.loader = loader;
        this.name = name;
        this.description = description;
        this.info = info;
        this.transpose = transpose;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    private MatVar getTable() {
        switch (info[0]) {
            case 0:
            case 1:
                return loader.get("data_1");
            default:
                return loader.get("data_2");
        }
    }

    private int getColumnIndex() {
        return Math.abs(info[1]) - 1;
    }

    private boolean isInverse() {
        return info[1] < 0;
    }

    private double[] getColumn(MatVar v, int column, boolean invert) {
        int[] dims = v.getDim();
        double[] out = new double[dims[transpose ? 1 : 0]];
        for (int i = 0; i < out.length; i++) {
            out[i] = (invert ? -1.0 : 1.0) * v.getDouble(transpose ? column : i, transpose ? i : column);
        }
        return out;
    }

    public double[] getTime() {
        return getColumn(getTable(), 0, false);
    }

    public double[] getDouble() {
        return getColumn(getTable(), getColumnIndex(), isInverse());
    }

    public int[] getInteger() {
        double[] d = getDouble();
        int[] out = new int[d.length];
        for (int i = 0; i < d.length; i++) {
            out[i] = (int) d[i];
        }
        return out;
    }

    public boolean[] getBoolean() {
        double[] d = getDouble();
        boolean[] out = new boolean[d.length];
        for (int i = 0; i < d.length; i++) {
            out[i] = d[i] > 0.5;
        }
        return out;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.loader != null ? this.loader.hashCode() : 0);
        hash = 53 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 53 * hash + (this.description != null ? this.description.hashCode() : 0);
        hash = 53 * hash + Arrays.hashCode(this.info);
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
        final DsresVariable other = (DsresVariable) obj;
        if (this.loader != other.loader && (this.loader == null || !this.loader.equals(other.loader))) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if ((this.description == null) ? (other.description != null) : !this.description.equals(other.description)) {
            return false;
        }
        if (!Arrays.equals(this.info, other.info)) {
            return false;
        }
        return true;
    }
}
