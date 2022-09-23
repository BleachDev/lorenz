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

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * An implementation of {@link MappingsWriter} designed to aid
 * with the implementation of binary de-obfuscation mapping
 * formats.
 *
 * @author Jamie Mansfield
 * @since 0.4.0
 */
public abstract class BinaryMappingsWriter extends MappingsWriter {

    protected final DataOutputStream stream;

    /**
     * Creates a new mappings writer, from the given {@link OutputStream}.
     *
     * @param stream The output stream, to write to
     */
    protected BinaryMappingsWriter(final OutputStream stream) {
        this.stream = new DataOutputStream(stream);
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }

}
