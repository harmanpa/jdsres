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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a matrix/array variable in a dsin.txt/dsfinal.txt file from
 * Dymola.
 *
 * @author Peter Harman
 */
public class DstxtVariable {

    private final String type;
    private final String name;
    private final int rows;
    private final int columns;
    private final List<Object> data;

    DstxtVariable(String type, String name, int rows, int columns) {
        this.type = type;
        this.name = name;
        this.rows = rows;
        this.columns = columns;
        this.data = new ArrayList<>(rows);
    }

    void addRow(Object rowData) {
        data.add(rowData);
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    /**
     * Get the variable data as a double matrix
     *
     * @return
     * @throws IOException
     */
    public double[][] getDoubleMatrix() throws IOException {
        if ("double".equals(type)) {
            double[][] out = new double[rows][];
            for (int row = 0; row < rows; row++) {
                out[row] = new double[columns];
                System.arraycopy((double[]) data.get(row), 0, out[row], 0, columns);
            }
            return out;
        }
        throw new IOException("Cannot convert type to double");
    }

    /**
     * Get the variable data as an int matrix
     *
     * @return
     * @throws IOException
     */
    public int[][] getIntMatrix() throws IOException {
        if ("int".equals(type)) {
            int[][] out = new int[rows][];
            for (int row = 0; row < rows; row++) {
                out[row] = new int[columns];
                System.arraycopy((int[]) data.get(row), 0, out[row], 0, columns);
            }
            return out;
        }
        throw new IOException("Cannot convert type to int");
    }

    /**
     * Get the variable data as a List of Strings
     *
     * @return
     * @throws IOException
     */
    public List<String> getStringList() throws IOException {
        if ("char".equals(type)) {
            List<String> out = new ArrayList<>(rows);
            for (int row = 0; row < rows; row++) {
                out.add((String) data.get(row));
            }
            return out;
        }
        throw new IOException("Cannot convert type to String");
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.type);
        hash = 97 * hash + Objects.hashCode(this.name);
        hash = 97 * hash + this.rows;
        hash = 97 * hash + this.columns;
        hash = 97 * hash + Objects.hashCode(this.data);
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
        final DstxtVariable other = (DstxtVariable) obj;
        if (this.rows != other.rows) {
            return false;
        }
        if (this.columns != other.columns) {
            return false;
        }
        if (!Objects.equals(this.type, other.type)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return Objects.equals(this.data, other.data);
    }

    @Override
    public String toString() {
        return type + " " + name + "(" + rows + "," + columns + ')';
    }

}
