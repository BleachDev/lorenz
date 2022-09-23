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
import org.cadixdev.lorenz.io.searge.xsrg.XSrgMappingFormat;
import org.cadixdev.lorenz.io.searge.xsrg.XSrgReader;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class XSrgReaderTest extends AbstractSrgReaderTest {

    public XSrgReaderTest() throws Exception {
        super(XSrgMappingFormat.INSTANCE, "/test.xsrg");
    }

    @Test
    public void ignoresPackages() throws IOException {
        // This test ensures that package mappings won't set off any exceptions
        // as they are valid input - even though Lorenz won't parse them :p
        final MappingSet mappings = new XSrgReader(new StringReader("PK: abc uk/jamierocks/Example")).read();
    }

    @Test
    public void tooLongInput() throws IOException {
        // This test should set off the first case where IllegalArgumentException
        // is thrown
        final XSrgReader parser = new XSrgReader(new StringReader("this is a faulty mapping because it is too long"));
        assertThrows(IllegalArgumentException.class, parser::read);
    }

    @Test
    public void invalidInput() throws IOException {
        // This test should set off the first case where IllegalArgumentException
        // is thrown
        final XSrgReader parser = new XSrgReader(new StringReader("PK: TooShort"));
        assertThrows(IllegalArgumentException.class, parser::read);
    }

}
