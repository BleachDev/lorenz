/*
 * This file is part of Lorenz, licensed under the MIT License (MIT).
 *
 * Copyright (c) Jamie Mansfield <https://www.jamierocks.uk/>
 * Copyright (c) contributors
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
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.cadixdev.lorenz.io.searge.csrg;

import org.cadixdev.lorenz.io.MappingsReader;
import org.cadixdev.lorenz.io.MappingsWriter;
import org.cadixdev.lorenz.io.TextMappingFormat;
import org.cadixdev.lorenz.io.searge.SrgMappingFormat;

import java.io.Reader;
import java.io.Writer;
import java.util.Optional;

/**
 * The CSRG mapping format.
 *
 * @author Jamie Mansfield
 * @since 0.4.0
 */
public class CSrgMappingFormat extends SrgMappingFormat {

    public static final CSrgMappingFormat INSTANCE = new CSrgMappingFormat();

    /**
     * The standard file extension used with the CSRG format.
     */
    public static final String STANDARD_EXTENSION = "csrg";

    @Override
    public String getIdentifier() {
        return "csrg";
    }

    @Override
    public String getName() {
        return "CSRG";
    }

    @Override
    public MappingsReader createReader(final Reader reader) {
        return new CSrgReader(reader);
    }

    @Override
    public MappingsWriter createWriter(final Writer writer) {
        return new CSrgWriter(writer);
    }

    @Override
    public Optional<String> getStandardFileExtension() {
        return Optional.of(STANDARD_EXTENSION);
    }

}
