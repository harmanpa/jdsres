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

import edu.washington.biostr.sig.matfile.MatVar;
import edu.washington.biostr.sig.matfile.MatfileLoader;
import java.util.Objects;

/**
 * Represents a matrix in a Dymola linearization.
 *
 * @author Peter Harman
 */
public class DslinMatrix {

    private final MatfileLoader loader;
    private final String name;
    private final int nx;
    private final boolean transpose;

    DslinMatrix(MatfileLoader loader, String name, int nx, boolean transpose) {
        this.loader = loader;
        this.name = name;
        this.nx = nx;
        this.transpose = transpose;
    }

    public String getName() {
        return name;
    }

    public double[][] get() {
        MatVar mat = loader.get("ABCD");
        int minRow = 0;
        int maxRow = mat.getDim()[0];
        int minCol = 0;
        int maxCol = mat.getDim()[1];
        switch (name) {
            case "A":
                maxRow = nx;
                maxCol = nx;
                break;
            case "B":
                maxRow = nx;
                minCol = nx;
                break;
            case "C":
                minRow = nx;
                maxCol = nx;
                break;
            case "D":
                minRow = nx;
                minCol = nx;
                break;
            default:
        }
        double[][] out = new double[maxRow - minRow][];
        for (int row = minRow; row < maxRow; row++) {
            for (int col = minCol; col < maxCol; col++) {
                out[row - minRow] = new double[maxCol - minCol];
                out[row - minRow][col - minCol] = mat.getDouble(transpose ? col : row, transpose ? row : col);
            }
        }
        return out;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.loader);
        hash = 37 * hash + Objects.hashCode(this.name);
        hash = 37 * hash + this.nx;
        hash = 37 * hash + (this.transpose ? 1 : 0);
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
        final DslinMatrix other = (DslinMatrix) obj;
        if (this.nx != other.nx) {
            return false;
        }
        if (this.transpose != other.transpose) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return Objects.equals(this.loader, other.loader);
    }

}
