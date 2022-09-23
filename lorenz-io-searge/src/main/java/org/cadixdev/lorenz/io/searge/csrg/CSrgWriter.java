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
import org.cadixdev.lorenz.io.MappingsWriter;
import org.cadixdev.lorenz.io.TextMappingsWriter;
import org.cadixdev.lorenz.model.ClassMapping;
import org.cadixdev.lorenz.model.FieldMapping;
import org.cadixdev.lorenz.model.Mapping;
import org.cadixdev.lorenz.model.MethodMapping;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of {@link MappingsWriter} for the CSRG format.
 *
 * @author Jamie Mansfield
 * @since 0.4.0
 */
public class CSrgWriter extends TextMappingsWriter {

    private final List<String> classes = new ArrayList<>();
    private final List<String> fields = new ArrayList<>();
    private final List<String> methods = new ArrayList<>();

    /**
     * Creates a new CSRG mappings writer, from the given {@link Writer}.
     *
     * @param writer The writer
     */
    public CSrgWriter(final Writer writer) {
        super(writer);
    }

    @Override
    public void write(final MappingSet mappings) {
        // Write class mappings
        mappings.getTopLevelClassMappings().stream()
                .filter(ClassMapping::hasMappings)
                .sorted(getConfig().getClassMappingComparator())
                .forEach(this::writeClassMapping);

        // Write everything to the print writer
        classes.forEach(writer::println);
        fields.forEach(writer::println);
        methods.forEach(writer::println);

        // Clear out the lists, to ensure that mappings aren't written twice (or more)
        classes.clear();
        fields.clear();
        methods.clear();
    }

    /**
     * Writes the given {@link ClassMapping}, alongside its member mappings.
     *
     * @param mapping The class mapping
     */
    protected void writeClassMapping(final ClassMapping<?, ?> mapping) {
        // Check if the mapping should be written, and if so write it
        if (mapping.hasDeobfuscatedName()) {
            classes.add(String.format("%s %s", mapping.getFullObfuscatedName(), mapping.getFullDeobfuscatedName()));
        }

        // Write inner class mappings
        mapping.getInnerClassMappings().stream()
                .filter(ClassMapping::hasMappings)
                .sorted(getConfig().getClassMappingComparator())
                .forEach(this::writeClassMapping);

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
    }

    /**
     * Writes the given {@link FieldMapping}.
     *
     * @param mapping The field mapping
     */
    protected void writeFieldMapping(final FieldMapping mapping) {
        // The hasDeobfuscatedName test should have already have been performed, so we're good
        fields.add(String.format("%s %s %s",
                mapping.getParent().getFullObfuscatedName(),
                mapping.getObfuscatedName(),
                mapping.getDeobfuscatedName()
        ));
    }

    /**
     * Writes the given {@link MethodMapping}.
     *
     * @param mapping The method mapping
     */
    protected void writeMethodMapping(final MethodMapping mapping) {
        // The hasDeobfuscatedName test should have already have been performed, so we're good
        methods.add(String.format("%s %s %s %s",
                mapping.getParent().getFullObfuscatedName(),
                mapping.getObfuscatedName(), mapping.getObfuscatedDescriptor(),
                mapping.getDeobfuscatedName()
        ));
    }

}
