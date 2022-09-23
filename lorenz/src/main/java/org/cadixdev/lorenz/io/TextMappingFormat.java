/*
 * This file is part of Lorenz, licensed under the MIT License (MIT).
 *
 * Copyright (c) Jamie Mansfield <https://www.jamierocks.uk/>, Bleach <https://bleach.dev/> and contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 */

package org.cadixdev.lorenz.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A representation of a de-obfuscation mapping format serialised as text.
 *
 * @author Minecrell
 * @since 0.4.0
 */
public interface TextMappingFormat extends MappingFormat {

    /**
     * Creates a {@link MappingsReader} from the given {@link Reader}
     * for the mapping format.
     *
     * @param reader The reader
     * @return The mapping reader
     * @throws IOException Should an I/O issue occur
     * @throws UnsupportedOperationException If the format does not support reading
     */
    MappingsReader createReader(final Reader reader) throws IOException;

    @Override
    default MappingsReader createReader(final InputStream stream) throws IOException {
        return createReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
    }

    @Override
    default MappingsReader createReader(final Path path) throws IOException {
        return createReader(Files.newBufferedReader(path));
    }

    /**
     * Creates a {@link MappingsWriter} from the given {@link Writer}
     * for the mapping format.
     *
     * @param writer The writer
     * @return The mapping writer
     * @throws IOException Should an I/O issue occur
     * @throws UnsupportedOperationException If the format does not support writing
     */
    MappingsWriter createWriter(final Writer writer) throws IOException;

    @Override
    default MappingsWriter createWriter(final OutputStream stream) throws IOException {
        return createWriter(new OutputStreamWriter(stream, StandardCharsets.UTF_8));
    }

    @Override
    default MappingsWriter createWriter(final Path path) throws IOException {
        return createWriter(Files.newBufferedWriter(path));
    }

    /**
     * Removes present comments, from the given {@link String} line.
     *
     * @param line The line
     * @return The comment-omitted line
     */
    default String removeComments(String line) {
        return line;
    }
}
