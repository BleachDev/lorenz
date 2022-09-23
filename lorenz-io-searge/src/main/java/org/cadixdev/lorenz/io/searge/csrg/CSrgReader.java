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

package org.cadixdev.lorenz.io.searge.csrg;

import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.io.MappingsReader;
import org.cadixdev.lorenz.io.TextMappingsReader;

import java.io.Reader;

/**
 * An implementation of {@link MappingsReader} for the CSRG format.
 *
 * @author Jamie Mansfield
 * @since 0.4.0
 */
public class CSrgReader extends TextMappingsReader {

    private static final int CLASS_MAPPING_ELEMENT_COUNT = 2;
    private static final int FIELD_MAPPING_ELEMENT_COUNT = 3;
    private static final int METHOD_MAPPING_ELEMENT_COUNT = 4;

    /**
     * Creates a new CSRG mappings reader, for the given {@link Reader}.
     *
     * @param reader The reader
     */
    public CSrgReader(final Reader reader) {
        super(reader);
    }

    @Override
    protected void readLine(final MappingSet mappings, final String rawLine) {
        final String line = CSrgMappingFormat.INSTANCE.removeComments(rawLine).trim();
        if (line.isEmpty()) return;

        if (line.length() < 4) {
            throw new IllegalArgumentException("Faulty CSRG mapping encountered: `" + line + "`!");
        }

        // Split up the line, for further processing
        final String[] split = SPACE.split(line);
        final int len = split.length;

        // Process class/package mappings
        if (len == CLASS_MAPPING_ELEMENT_COUNT) {
            final String obfuscatedName = split[0];
            final String deobfuscatedName = split[1];

            // Package mappings
            if (obfuscatedName.endsWith("/")) {
                // Lorenz doesn't currently support package mappings, though they are an SRG feature.
                // For now, Lorenz will just silently ignore those mappings.
            }
            // Class mappings
            else {
                // Get mapping, and set de-obfuscated name
                mappings.getOrCreateClassMapping(obfuscatedName)
                        .setDeobfuscatedName(deobfuscatedName);
            }
        }
        // Process field mapping
        else if (len == FIELD_MAPPING_ELEMENT_COUNT) {
            final String parentClass = split[0];
            final String obfuscatedName = split[1];
            final String deobfuscatedName = split[2];

            // Get mapping, and set de-obfuscated name
            mappings.getOrCreateClassMapping(parentClass)
                    .getOrCreateFieldMapping(obfuscatedName)
                    .setDeobfuscatedName(deobfuscatedName);
        }
        // Process method mapping
        else if (len == METHOD_MAPPING_ELEMENT_COUNT) {
            final String parentClass = split[0];
            final String obfuscatedName = split[1];
            final String obfuscatedSignature = split[2];
            final String deobfuscatedName = split[3];

            // Get mapping, and set de-obfuscated name
            mappings.getOrCreateClassMapping(parentClass)
                    .getOrCreateMethodMapping(obfuscatedName, obfuscatedSignature)
                    .setDeobfuscatedName(deobfuscatedName);
        } else {
            throw new IllegalArgumentException("Failed to process line: `" + line + "`!");
        }
    }

}
