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

package org.cadixdev.lorenz.io.searge.tsrg;

import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.io.MappingsReader;
import org.cadixdev.lorenz.io.TextMappingsReader;
import org.cadixdev.lorenz.model.ClassMapping;

import java.io.Reader;

/**
 * An implementation of {@link MappingsReader} for the TSRG format.
 *
 * @author Jamie Mansfield
 * @since 0.4.0
 */
public class TSrgReader extends TextMappingsReader {

    private static final int CLASS_MAPPING_ELEMENT_COUNT = 2;
    private static final int FIELD_MAPPING_ELEMENT_COUNT = 2;
    private static final int METHOD_MAPPING_ELEMENT_COUNT = 3;

    private ClassMapping currentClass;

    /**
     * Creates a new TSRG mappings reader, for the given {@link Reader}.
     *
     * @param reader The reader
     */
    public TSrgReader(final Reader reader) {
        super(reader);
    }

    @Override
    protected void readLine(final MappingSet mappings, final String rawLine) {
        final String line = TSrgMappingFormat.INSTANCE.removeComments(rawLine);
        if (line.isEmpty()) return;

        if (line.length() < 3) {
            throw new IllegalArgumentException("Faulty TSRG mapping encountered: `" + line + "`!");
        }

        // Split up the line, for further processing
        final String[] split = SPACE.split(line);
        final int len = split.length;

        // Process class/package mappings
        if (!split[0].startsWith("\t") && len == CLASS_MAPPING_ELEMENT_COUNT) {
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
                currentClass = mappings.getOrCreateClassMapping(obfuscatedName);
                currentClass.setDeobfuscatedName(deobfuscatedName);
            }
        }
        else if (split[0].startsWith("\t") && currentClass != null) {
            final String obfuscatedName = split[0].replace("\t", "");

            // Process field mapping
            if (len == FIELD_MAPPING_ELEMENT_COUNT) {
                final String deobfuscatedName = split[1];

                // Get mapping, and set de-obfuscated name
                currentClass
                        .getOrCreateFieldMapping(obfuscatedName)
                        .setDeobfuscatedName(deobfuscatedName);
            }
            // Process method mapping
            else if (len == METHOD_MAPPING_ELEMENT_COUNT) {
                final String obfuscatedSignature = split[1];
                final String deobfuscatedName = split[2];

                // Get mapping, and set de-obfuscated name
                currentClass
                        .getOrCreateMethodMapping(obfuscatedName, obfuscatedSignature)
                        .setDeobfuscatedName(deobfuscatedName);
            }
        } else {
            throw new IllegalArgumentException("Failed to process line: `" + line + "`!");
        }
    }

}
