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

package org.cadixdev.lorenz.io.enigma;

import org.cadixdev.lorenz.io.MappingsReader;
import org.cadixdev.lorenz.io.MappingsWriter;
import org.cadixdev.lorenz.io.TextMappingFormat;

import java.io.Reader;
import java.io.Writer;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * The standard Enigma mapping format.
 *
 * @author Jamie Mansfield
 * @since 0.4.0
 */
public class EnigmaMappingFormat implements TextMappingFormat {

    public static final EnigmaMappingFormat INSTANCE = new EnigmaMappingFormat();

    /**
     * A regex expression used to remove comments from lines.
     */
    private static final Pattern COMMENT_PATTERN = Pattern.compile("#.*");

    /**
     * The {@code "mapping"} file extension, as used by both
     * cuchaz's mapping project and Fabric's Yarn mappings.
     */
    public static final String MAPPING_EXTENSION = "mapping";

    @Override
    public String getIdentifier() {
        return "enigma";
    }

    @Override
    public String getName() {
        return "Enigma";
    }

    @Override
    public MappingsReader createReader(final Reader reader) {
        return new EnigmaReader(reader);
    }

    @Override
    public MappingsWriter createWriter(final Writer writer) {
        return new EnigmaWriter(writer);
    }

    @Override
    public Optional<String> getStandardFileExtension() {
        return Optional.of(MAPPING_EXTENSION);
    }

    @Override
    public String removeComments(String line) {
        return COMMENT_PATTERN.matcher(line).replaceAll("");
    }
}
