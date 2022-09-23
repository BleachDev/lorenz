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

import org.cadixdev.bombe.type.FieldType;
import org.cadixdev.bombe.type.MethodDescriptor;
import org.cadixdev.bombe.type.signature.FieldSignature;
import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.io.MappingsReader;
import org.cadixdev.lorenz.io.TextMappingsReader;
import org.cadixdev.lorenz.model.ClassMapping;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;

/**
 * An implementation of {@link MappingsReader} for the Tiny V1 format.
 *
 * @author Bleach
 * @since 1.0.0
 */
public class TinyV1Reader extends TextMappingsReader {

    private static final String CLASS_MAPPING_KEY = "CLASS";
    private static final String FIELD_MAPPING_KEY = "FIELD";
    private static final String METHOD_MAPPING_KEY = "METHOD";

    /**
     * The column to read the input mappings from.
     *
     * The standard Fabric columns are {@code official}, {@code intermediary} and {@code named}
     */
    protected String from;
    protected int fromIndex;
    /**
     * The column to read the output mappings from.
     *
     * The standard Fabric columns are {@code official}, {@code intermediary} and {@code named}
     */
    protected String to;
    protected int toIndex;

    public TinyV1Reader(final Reader reader) {
        super(reader);
    }

    public TinyV1Reader withFormats(String from, String to) {
        this.from = from;
        this.to = to;
        return this;
    }

    @Override
    public MappingSet read(final MappingSet mappings) throws IOException {
        if (from == null || to == null) {
            throw new IllegalStateException("Format names not set. call withFormats() before reading!");
        }

        String header = reader.readLine();
        if (!header.startsWith("v1")) {
            throw new IllegalArgumentException("Faulty Tiny V1 mapping header!");
        }

        List<String> split = Arrays.asList(header.split("\t"));
        fromIndex = split.indexOf(from) - 1;
        if (fromIndex != 0) {
            // TODO: Support for any column as the "from" mappings
            throw new IllegalArgumentException("Input mappings must be the first column!");
        }
        toIndex = split.indexOf(to) - 1;

        reader.lines().forEach(line -> readLine(mappings, line));
        return mappings;
    }

    @Override
    public void readLine(final MappingSet mappings, final String line) {
        if (line.isEmpty()) return;

        // Split up the line, for further processing
        final String[] split = TAB.split(line);

        // Get the class were modifying
        final ClassMapping<?, ?> cls = mappings.getOrCreateClassMapping(split[1]);

        // Establish the type of mapping
        final String key = split[0];
        if (key.equals(CLASS_MAPPING_KEY)) {
            final String deobfName = split[1 + toIndex];
            cls.setDeobfuscatedName(deobfName);
        } else if (key.equals(FIELD_MAPPING_KEY)) {
            final String obfName = split[3 + fromIndex];
            final String deobfName = split[3 + toIndex];
            final FieldSignature type = new FieldSignature(obfName, split[2].isEmpty() ? null : FieldType.of(split[2]));
            cls.getOrCreateFieldMapping(type)
                    .setDeobfuscatedName(deobfName);
        } else if (key.equals(METHOD_MAPPING_KEY)) {
            final MethodDescriptor type = MethodDescriptor.of(split[2]);
            final String obfName = split[3 + fromIndex];
            final String deobfName = split[3 + toIndex];
            cls.getOrCreateMethodMapping(obfName, type)
                    .setDeobfuscatedName(deobfName);
        }
    }
}
