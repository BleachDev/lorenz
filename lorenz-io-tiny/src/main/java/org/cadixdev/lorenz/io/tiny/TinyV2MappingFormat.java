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

package org.cadixdev.lorenz.io.tiny;

import org.cadixdev.lorenz.io.MappingsReader;
import org.cadixdev.lorenz.io.MappingsWriter;
import org.cadixdev.lorenz.io.TextMappingFormat;

import java.io.Reader;
import java.io.Writer;
import java.util.Optional;

/**
 * The Tiny V2 mapping format.
 *
 * @author Bleach
 * @since 1.0.0
 */
public class TinyV2MappingFormat implements TextMappingFormat {

    public static final TinyV2MappingFormat INSTANCE = new TinyV2MappingFormat();

    /**
     * The Tiny V2 file extension (note that it's the same in Tiny V1 mappings).
     */
    public static final String MAPPING_EXTENSION = "tiny";

    @Override
    public String getIdentifier() {
        return "tinyv2";
    }

    @Override
    public String getName() {
        return "Tiny V2";
    }

    @Override
    public MappingsReader createReader(final Reader reader) {
        return new TinyV2Reader(reader);
    }

    @Override
    public MappingsWriter createWriter(final Writer writer) {
        return new TinyV2Writer(writer);
    }

    @Override
    public Optional<String> getStandardFileExtension() {
        return Optional.of(MAPPING_EXTENSION);
    }
}
