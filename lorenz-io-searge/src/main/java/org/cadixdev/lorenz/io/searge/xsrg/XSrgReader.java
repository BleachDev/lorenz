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

package org.cadixdev.lorenz.io.searge.xsrg;

import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.io.MappingsReader;
import org.cadixdev.lorenz.io.TextMappingsReader;

import java.io.Reader;

/**
 * An implementation of {@link MappingsReader} for the XSRG format.
 *
 * @author Jamie Mansfield
 * @since 0.5.3
 */
public class XSrgReader extends TextMappingsReader {

    private static final String PACKAGE_MAPPING_KEY = "PK:";
    private static final String CLASS_MAPPING_KEY = "CL:";
    private static final String FIELD_MAPPING_KEY = "FD:";
    private static final String METHOD_MAPPING_KEY = "MD:";

    private static final int PACKAGE_MAPPING_ELEMENT_COUNT = 3;
    private static final int CLASS_MAPPING_ELEMENT_COUNT = 3;
    private static final int FIELD_MAPPING_ELEMENT_COUNT = 5;
    private static final int METHOD_MAPPING_ELEMENT_COUNT = 5;

    /**
     * Creates a new XSRG mappings reader, for the given {@link Reader}.
     *
     * @param reader The reader
     */
    public XSrgReader(final Reader reader) {
        super(reader);
    }

    @Override
    protected void readLine(final MappingSet mappings, final String rawLine) {
        final String line = XSrgMappingFormat.INSTANCE.removeComments(rawLine).trim();
        if (line.isEmpty()) return;

        if (line.length() < 4) {
            throw new IllegalArgumentException("Faulty XSRG mapping encountered: `" + line + "`!");
        }

        // Split up the line, for further processing
        final String[] split = SPACE.split(line);
        final int len = split.length;

        // Establish the type of mapping
        final String key = split[0];
        if (key.equals(CLASS_MAPPING_KEY) && len == CLASS_MAPPING_ELEMENT_COUNT) {
            final String obfuscatedName = split[1];
            final String deobfuscatedName = split[2];

            // Get mapping, and set de-obfuscated name
            mappings.getOrCreateClassMapping(obfuscatedName)
                    .setDeobfuscatedName(deobfuscatedName);
        } else if (key.equals(FIELD_MAPPING_KEY) && len == FIELD_MAPPING_ELEMENT_COUNT) {
            final String fullObfuscatedName = split[1];
            final String obfuscatedType = split[2];
            final String fullDeobfuscatedName = split[3];
            final String deobfuscatedType = split[4];
            final int lastIndex = fullObfuscatedName.lastIndexOf('/');
            final String owningClass = fullObfuscatedName.substring(0, lastIndex);
            final String obfuscatedName = fullObfuscatedName.substring(lastIndex + 1);
            final String deobfuscatedName = fullDeobfuscatedName.substring(fullDeobfuscatedName.lastIndexOf('/') + 1);

            // Get mapping, and set de-obfuscated name
            mappings.getOrCreateClassMapping(owningClass)
                    .getOrCreateFieldMapping(obfuscatedName, obfuscatedType)
                    .setDeobfuscatedName(deobfuscatedName);
        } else if (key.equals(METHOD_MAPPING_KEY) && len == METHOD_MAPPING_ELEMENT_COUNT) {
            final String fullObfuscatedName = split[1];
            final String obfuscatedSignature = split[2];
            final String fullDeobfuscatedName = split[3];
            final String deobfuscatedSignature = split[4];
            final int lastIndex = fullObfuscatedName.lastIndexOf('/');
            final String owningClass = fullObfuscatedName.substring(0, lastIndex);
            final String obfuscatedName = fullObfuscatedName.substring(lastIndex + 1);
            final String deobfuscatedName = fullDeobfuscatedName.substring(fullDeobfuscatedName.lastIndexOf('/') + 1);

            // Get mapping, and set de-obfuscated name
            mappings.getOrCreateClassMapping(owningClass)
                    .getOrCreateMethodMapping(obfuscatedName, obfuscatedSignature)
                    .setDeobfuscatedName(deobfuscatedName);
        } else if (key.equals(PACKAGE_MAPPING_KEY) && len == PACKAGE_MAPPING_ELEMENT_COUNT) {
            // Lorenz doesn't currently support package mappings, though they are an SRG feature.
            // For now, Lorenz will just silently ignore those mappings.
        } else {
            throw new IllegalArgumentException("Failed to process line: `" + line + "`!");
        }
    }

}
