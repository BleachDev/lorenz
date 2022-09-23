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

import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.io.MappingsWriter;
import org.cadixdev.lorenz.io.TextMappingsWriter;
import org.cadixdev.lorenz.model.ClassMapping;
import org.cadixdev.lorenz.model.FieldMapping;
import org.cadixdev.lorenz.model.Mapping;
import org.cadixdev.lorenz.model.MethodMapping;
import org.cadixdev.lorenz.model.MethodParameterMapping;

import java.io.IOException;
import java.io.Writer;

/**
 * An implementation of {@link MappingsWriter} for the Tiny V2 format.
 *
 * @author Bleach
 * @since 1.0.0
 */
public class TinyV2Writer extends TextMappingsWriter {

    private String from;
    private String to;

    public TinyV2Writer(final Writer writer) {
        super(writer);
    }

    public TinyV2Writer withFormats(String from, String to) {
        this.from = from;
        this.to = to;
        return this;
    }

    @Override
    public void write(final MappingSet mappings) throws IOException {
        if (from == null || to == null) {
            throw new IllegalStateException("Format names not set. call withFormats() before writing!");
        }

        // Write header
        writer.println("tiny\t2\t0\t" + from + "\t" + to);

        mappings.getTopLevelClassMappings().stream()
                .filter(ClassMapping::hasMappings)
                .sorted(getConfig().getClassMappingComparator())
                .forEach(this::writeClassMapping);
    }

    /**
     * Writes the given {@link ClassMapping}, alongside its member mappings.
     *
     * @param mapping The class mapping
     */
    protected void writeClassMapping(final ClassMapping<?, ?> mapping) {
        // Check if the mapping should be written, and if so write it
        if (mapping.hasDeobfuscatedName()) {
            printMapping(mapping, 0, "c\t"
                    + mapping.getFullObfuscatedName() + "\t"
                    + mapping.getFullDeobfuscatedName());
        }

        // Write field mappings
        mapping.getFieldsByName().values().stream()
                .filter(Mapping::hasDeobfuscatedName)
                .sorted(getConfig().getFieldMappingComparator())
                .forEach(this::writeFieldMapping);

        // Write method mappings
        mapping.getMethodMappings().stream()
                .filter(Mapping::hasDeobfuscatedName)
                .sorted(getConfig().getMethodMappingComparator())
                .forEach(this::writeMethodMapping);

        // Write inner class mappings
        mapping.getInnerClassMappings().stream()
                .filter(ClassMapping::hasMappings)
                .sorted(getConfig().getClassMappingComparator())
                .forEach(this::writeClassMapping);
    }

    /**
     * Writes the given {@link FieldMapping}.
     *
     * @param mapping The field mapping
     */
    protected void writeFieldMapping(final FieldMapping mapping) {
        printMapping(mapping, 1, "f\t"
                + mapping.getParent().getFullObfuscatedName() + "\t"
                + mapping.getType().map(Object::toString).orElse("") + "\t"
                + mapping.getObfuscatedName() + "\t"
                + mapping.getDeobfuscatedName());
    }

    /**
     * Writes the given {@link MethodMapping}.
     *
     * @param mapping The method mapping
     */
    protected void writeMethodMapping(final MethodMapping mapping) {
        printMapping(mapping, 1, "m\t"
                + mapping.getParent().getFullObfuscatedName() + "\t"
                + mapping.getObfuscatedDescriptor() + "\t"
                + mapping.getObfuscatedName() + "\t"
                + mapping.getDeobfuscatedName());

        for (MethodParameterMapping parameter : mapping.getParameterMappings()) {
            printMapping(parameter, 2, "p\t"
                + parameter.getIndex() + "\t\t"
                + parameter.getDeobfuscatedName());
        }
    }

    protected void printMapping(final Mapping<?, ?> mapping, final int indent, final String line) {
        printIndent(indent);
        writer.println(line);

        if (!mapping.getJavadoc().isEmpty()) {
            printIndent(indent + 1);
            writer.println("c " + String.join("\\n", mapping.getJavadoc()));
        }
    }

    private void printIndent(int indent) {
        for (int i = 0; i < indent; i++) {
            writer.print('\t');
        }
    }
}
