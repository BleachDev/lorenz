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

package org.cadixdev.lorenz.io;

import org.cadixdev.lorenz.MappingSet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.regex.Pattern;

/**
 * An implementation of {@link MappingsReader} designed to aid
 * with the implementation of mapping readers for text-based
 * mapping formats.
 *
 * @author Jamie Mansfield
 * @since 0.4.0
 */
public abstract class TextMappingsReader extends MappingsReader {

    protected static final Pattern SPACE = Pattern.compile(" ", Pattern.LITERAL);
    protected static final Pattern TAB = Pattern.compile("\t", Pattern.LITERAL);

    protected final BufferedReader reader;

    /**
     * Creates a new mappings reader, for the given {@link Reader}.
     *
     * @param reader The reader
     */
    protected TextMappingsReader(final Reader reader) {
        this.reader = reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader);
    }

    @Override
    public MappingSet read(final MappingSet mappings) throws IOException {
        reader.lines().forEach(line -> readLine(mappings, line));
        return mappings;
    }

    protected abstract void readLine(final MappingSet mappings, final String line);

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
