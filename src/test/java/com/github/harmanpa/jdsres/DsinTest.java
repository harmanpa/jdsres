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

import java.io.File;
import java.io.IOException;
import org.junit.Test;

/**
 *
 * @author Pete
 */
public class DsinTest {

    @Test
    public void test() throws IOException {
        File fdsin = new File(new File(System.getProperty("user.dir")), "target/test-classes/dsin.txt");
        File fdsfinal = new File(new File(System.getProperty("user.dir")), "target/test-classes/dsfinal.txt");
        DstxtFile dsin = new DstxtFile(fdsin);
        for (DstxtVariable var : dsin.getVariables().values()) {
            System.out.println(var);
        }
        DstxtFile dsfinal = new DstxtFile(fdsfinal);
        for (DstxtVariable var : dsfinal.getVariables().values()) {
            System.out.println(var);
        }
    }
}
