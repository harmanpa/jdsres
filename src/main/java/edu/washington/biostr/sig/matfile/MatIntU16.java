/*
 * Matfile Library (Pure Java MatLab file decoder)
 * Copyright (C) 2006-2007 University of Washington
 * Author Eider Moore
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
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
package edu.washington.biostr.sig.matfile;

/**
 * <p>
 * Title: Brain Visualizer</p>
 * <p>
 * Description: Displays and allows manipulation of 3d brain models</p>
 * <p>
 * Copyright: Copyright (c) 2002</p>
 * <p>
 * Company: Structural Informatics Group</p>
 * <p>
 * This is a MatVar that specifically is designed to store Unsigned 16 bit ints.
 * It can only hold integers and integers with imaginary components.
 * </p>
 *
 * @author Eider Moore
 * @version 1.0
 */
public class MatIntU16
        extends MatVar {

    private char[] data;
    private char[] imag;

    public MatIntU16(String name, int[] dim, int imagf) {
        this(name, dim, imagf != 0);
    }

    /**
     * Create a new MatInt32.
     *
     * @param name The name.
     * @param dim The dimensions.
     * @param imagf Whether or not to have imaginary numbers, if 0 we have no
     * imaginary numbers, otherwise we have imaginary numbers.
     */
    public MatIntU16(String name, int[] dim, boolean imagf) {
        super(name, dim);
        int size = 1;
        for (int i = 0; i < dim.length; i++) {
            size *= dim[i];
        }
        data = new char[size];
        if (imagf) {
            imag = new char[size];
        }
    }

    /**
     * returns the int at that location converted to a positive integer. We need
     * to increase the size to a long in order to pull this off. This is the
     * most natural call for this function.
     *
     * @param index the index as an array of ints
     * @return returns a int at the specified index.
     */
    public int getUShort(int[] index) {
        int i = getIndex(index);
        int num = data[i];
        if (num > 0) {
            return num;
        } else {
            return num;
        }
    }

    @Override
    public int getInt(int... i) {
        return getUShort(i);
    }

    @Override
    public double getDouble(int... i) {
        return getUShort(i);
    }

    /**
     * returns the int at that location converted to a positive integer. We need
     * to increase the size to a long in order to pull this off. This is the
     * most natural call for this function.
     *
     * @param index the index as an array of ints
     * @return returns a int at the specified index.
     */
    public int getImag(int[] index) {
        int i = getIndex(index);
        int num = imag[i];
        if (num > 0) {
            return num;
        } else {
            // the maximum value is 2^16 = 65536
            // I'm assuming 2's compliment
            num = (65536 + num);
            return num;
        }
    }

    /**
     * Whether or not this contains imaginary numbers.
     *
     * @return True if it contains imaginary numbers.
     */
    @Override
    public boolean containsImaginary() {
        return (imag != null);
    }

    /**
     * returns the imaginary component at that location converted to a positive
     * integer. We need to increase the size to a long in order to pull this
     * off. This is the most natural call for this function.
     *
     * @param loc the index as an array of ints
     * @return returns a int at the specified index.
     */
    @Override
    public Integer getImaginary(int... loc) {
        if (containsImaginary()) {
            return getImag(loc);
        } else {
            return 0;
        }
    }

    /**
     * Returns a Integer with the value stored in index.
     *
     * @param index the index of the value
     * @return a Integer with the value.
     */
    @Override
    public Integer getData(int... index) {
        return getNumber(index);
    }

    /**
     * Returns a Integer with the value stored in index.
     *
     * @param index the index of the value
     * @return a Double with the value.
     */
    @Override
    public Integer getNumber(int... index) {
        Integer answer = getUShort(index);
        return answer;
    }

    /**
     * Everything in a MatInt32 can be returned as a number and none can be
     * returned as a MatVar.
     *
     * @param index not used
     * @return true
     */
    @Override
    public boolean isNumber(int... index) {
        return true;
    }

    @Override
    public MatVar getMatVar(int... index) {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public int type() {
        return UINT16;
    }

    public void putAll(char[] values) {
        this.data = values;
    }

    public void putAllImage(char[] values) {
        this.imag = values;
    }

    public void putValue(int[] index, char value) {
        int i = getIndex(index);
        data[i] = value;
    }

    public void putImagValue(int[] index, char value) {
        int i = getIndex(index);
        imag[i] = value;
    }

    @Override
    protected void setDouble(int index, double value) {
        data[index] = (char) value;
    }

    @Override
    protected void setLong(int index, long value) {
        data[index] = (char) value;
    }

    @Override
    protected void setDoubleImag(int index, double value) {
        imag[index] = (char) value;
    }

    @Override
    protected void setLongImag(int index, long value) {
        imag[index] = (char) value;
    }
}
