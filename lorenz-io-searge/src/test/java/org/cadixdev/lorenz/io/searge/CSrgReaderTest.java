/*
 * This file is part of Lorenz, licensed under the MIT License (MIT).
 *
 * Copyright (c) Jamie Mansfield <https://www.jamierocks.uk/>, Bleach <https://bleach.dev/> and contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 */

package org.cadixdev.lorenz.io.searge;

import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.io.searge.csrg.CSrgMappingFormat;
import org.cadixdev.lorenz.io.searge.csrg.CSrgReader;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CSrgReaderTest extends AbstractSrgReaderTest {

    public CSrgReaderTest() throws Exception {
        super(CSrgMappingFormat.INSTANCE, "/test.csrg");
    }

    @Test
    public void ignoresPackages() throws IOException {
        // This test ensures that package mappings won't erroneously be read as
        // class mappings. No exceptions should be thrown either.
        final MappingSet mappings = new CSrgReader(new StringReader("abc/ uk/jamierocks/Example")).read();

        assertEquals(0, mappings.getTopLevelClassMappings().size());
    }

    @Test
    public void tooLongInput() throws IOException {
        // This test should set off the first case where IllegalArgumentException
        // is thrown
        final CSrgReader parser = new CSrgReader(new StringReader("this is a faulty mapping because it is too long"));
        assertThrows(IllegalArgumentException.class, parser::read);
    }

    @Test
    public void invalidInput() throws IOException {
        // This test should set off the first case where IllegalArgumentException
        // is thrown
        final CSrgReader parser = new CSrgReader(new StringReader("cls"));
        assertThrows(IllegalArgumentException.class, parser::read);
    }

}
