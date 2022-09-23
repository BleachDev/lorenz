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

package org.cadixdev.lorenz.io.jam;

import org.cadixdev.lorenz.io.MappingsReader;
import org.cadixdev.lorenz.io.MappingsWriter;
import org.cadixdev.lorenz.io.TextMappingFormat;

import java.io.Reader;
import java.io.Writer;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * The JAM mapping format.
 *
 * @author Jamie Mansfield
 * @since 0.4.0
 */
public class JamMappingFormat implements TextMappingFormat {

    public static final JamMappingFormat INSTANCE = new JamMappingFormat();

    /**
     * A regex expression used to remove comments from lines.
     */
    private static final Pattern COMMENT_PATTERN = Pattern.compile("#.*");

    /**
     * The standard file extension used with the JAM format.
     */
    public static final String STANDARD_EXTENSION = "jam";

    @Override
    public String getIdentifier() {
        return "jam";
    }

    @Override
    public String getName() {
        return "JAM";
    }

    @Override
    public MappingsReader createReader(final Reader reader) {
        return new JamReader(reader);
    }

    @Override
    public MappingsWriter createWriter(final Writer writer) {
        return new JamWriter(writer);
    }

    @Override
    public Optional<String> getStandardFileExtension() {
        return Optional.of(STANDARD_EXTENSION);
    }

    @Override
    public String removeComments(final String line) {
        return COMMENT_PATTERN.matcher(line).replaceAll("");
    }
}
