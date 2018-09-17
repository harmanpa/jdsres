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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents the text formatted files used for dsin.txt and dsfinal.txt for 
 * Dymola simulations.
 * 
 * @author Peter Harman
 */
public class DstxtFile {

    private final Map<String, DstxtVariable> variables;

    public DstxtFile(File file) throws IOException {
        this(new FileInputStream(file));
    }

    public DstxtFile(URL url) throws IOException {
        this(url.openStream());
    }

    public DstxtFile(InputStream is) throws IOException {
        this(new InputStreamReader(is));
    }

    protected DstxtFile(Reader reader) throws IOException {
        variables = new LinkedHashMap<>();
        Pattern typePattern = Pattern.compile("^([a-z]+)\\s([a-zA-Z0-9]+)\\(([0-9]+),([0-9]+)\\)");
        BufferedReader br = new BufferedReader(reader);
        String line = br.readLine();
        DstxtVariable currentVariable = null;
        int lineNumber = 1;
        int row = 0;
        while (line != null) {
            // Skip comment lines
            if (!line.trim().isEmpty() && line.trim().charAt(0) != '#') {
                if (currentVariable == null) {
                    Matcher matcher = typePattern.matcher(line);
                    if (matcher.matches()) {
                        currentVariable = new DstxtVariable(matcher.group(1),
                                matcher.group(2),
                                Integer.parseInt(matcher.group(3)),
                                Integer.parseInt(matcher.group(4)));
                    }
                } else {
                    // Remove any comment
                    if (line.indexOf('#') > -1) {
                        line = line.substring(0, line.indexOf('#'));
                    }
                    switch (currentVariable.getType()) {
                        case "double":
                            String[] values = line.trim().split("\\s+");
                            if (currentVariable.getColumns() != values.length) {
                                throw new IOException("Wrong number of columns in line " + lineNumber + ": " + line);
                            }
                            double[] rowData = new double[values.length];
                            for (int i = 0; i < values.length; i++) {
                                rowData[i] = Double.parseDouble(values[i].trim());
                            }
                            currentVariable.addRow(rowData);
                            break;
                        case "int":
                            String[] values2 = line.trim().split("\\s+");
                            if (currentVariable.getColumns() != values2.length) {
                                throw new IOException("Wrong number of columns in line " + lineNumber + ": " + line);
                            }
                            int[] rowData2 = new int[values2.length];
                            for (int i = 0; i < values2.length; i++) {
                                rowData2[i] = Integer.parseInt(values2[i].trim());
                            }
                            currentVariable.addRow(rowData2);
                            break;
                        case "char":
                            currentVariable.addRow(line.substring(0, Math.min(line.length(), currentVariable.getColumns())));
                            break;
                        default:
                            throw new IOException("Unknown type " + currentVariable.getType());
                    }
                    row++;
                }
            }
            if (currentVariable != null && row == currentVariable.getRows()) {
                variables.put(currentVariable.getName(), currentVariable);
                currentVariable = null;
                row = 0;
            }
            lineNumber++;
            line = br.readLine();
        }
    }

    public final Map<String, DstxtVariable> getVariables() {
        return variables;
    }

    public final DstxtVariable getVariable(String name) {
        return variables.get(name);
    }
}
