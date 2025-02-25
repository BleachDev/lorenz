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

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/**
 * Represents a reader that reads de-obfuscation mappings.
 *
 * @see TextMappingsReader
 * @see BinaryMappingsReader
 *
 * @author Jamie Mansfield
 * @since 0.4.0
 */
public abstract class MappingsReader implements Closeable {

    /**
     * Reads mappings from the previously given {@link InputStream}, to
     * a new {@link MappingSet}.
     *
     * @return The mapping set
     * @throws IOException Should an I/O issue occur
     */
    public MappingSet read() throws IOException {
        return read(new MappingSet());
    }

    /**
     * Reads mappings from the previously given {@link InputStream}, to
     * the given {@link MappingSet}.
     *
     * @param mappings The mapping set
     * @return The mapping set, to allow for chaining
     * @throws IOException Should an I/O issue occur
     */
    public abstract MappingSet read(final MappingSet mappings) throws IOException;

}
