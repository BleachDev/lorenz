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

package org.cadixdev.lorenz.io.tiny;

import org.cadixdev.lorenz.io.TextMappingFormat;

import java.io.Reader;
import java.io.Writer;
import java.util.Optional;

/**
 * The Tiny V1 mapping format.
 *
 * @author Bleach
 * @since 1.0.0
 */
public class TinyV1MappingFormat implements TextMappingFormat {

    public static final TinyV1MappingFormat INSTANCE = new TinyV1MappingFormat();

    /**
     * The Tiny V1 file extension (note that it's the same in Tiny V2 mappings).
     */
    public static final String MAPPING_EXTENSION = "tiny";

    @Override
    public String getIdentifier() {
        return "tinyv1";
    }

    @Override
    public String getName() {
        return "Tiny V1";
    }

    @Override
    public TinyV1Reader createReader(final Reader reader) {
        return new TinyV1Reader(reader);
    }

    @Override
    public TinyV1Writer createWriter(final Writer writer) {
        return new TinyV1Writer(writer);
    }

    @Override
    public Optional<String> getStandardFileExtension() {
        return Optional.of(MAPPING_EXTENSION);
    }
}
