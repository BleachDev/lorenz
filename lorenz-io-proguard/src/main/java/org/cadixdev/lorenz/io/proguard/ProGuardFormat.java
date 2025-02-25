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

package org.cadixdev.lorenz.io.proguard;

import org.cadixdev.lorenz.io.MappingsReader;
import org.cadixdev.lorenz.io.MappingsWriter;
import org.cadixdev.lorenz.io.TextMappingFormat;

import java.io.Reader;
import java.io.Writer;
import java.util.Optional;

/**
 * The ProGuard mapping format.
 *
 * @author Jamie Mansfield
 * @since 0.5.1
 */
public class ProGuardFormat implements TextMappingFormat {

    @Override
    public String getIdentifier() {
        return "proguard";
    }

    @Override
    public String getName() {
        return "ProGuard";
    }

    @Override
    public MappingsReader createReader(final Reader reader) {
        return new ProGuardReader(reader);
    }

    @Override
    public MappingsWriter createWriter(final Writer writer) {
        throw new UnsupportedOperationException("cant write proguard");
    }

    @Override
    public Optional<String> getStandardFileExtension() {
        return Optional.empty();
    }

    @Override
    public boolean supportsWriting() {
        return false;
    }

}
