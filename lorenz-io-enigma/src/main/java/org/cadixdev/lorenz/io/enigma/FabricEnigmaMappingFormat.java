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

import java.io.Reader;
import java.io.Writer;

/**
 * The Fabric Enigma mapping format.
 *
 * @author Jamie Mansfield
 * @since 0.6.0
 */
public class FabricEnigmaMappingFormat extends EnigmaMappingFormat {

    public static FabricEnigmaMappingFormat INSTANCE = new FabricEnigmaMappingFormat();

    @Override
    public String getIdentifier() {
        return "fabric-engima";
    }

    @Override
    public String getName() {
        return "Enigma (Fabric)";
    }

    @Override
    public MappingsReader createReader(final Reader reader) {
        return new FabricEnigmaReader(reader);
    }

    @Override
    public MappingsWriter createWriter(final Writer writer) {
        return new FabricEnigmaWriter(writer);
    }

}
