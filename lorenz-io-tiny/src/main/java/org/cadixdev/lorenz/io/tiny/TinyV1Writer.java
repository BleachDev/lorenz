/*
 * This file is part of Lorenz, licensed under the MIT License (MIT).
 *
 * Copyright (c) Jamie Mansfield <https://www.jamierocks.uk/>
 * Copyright (c) contributors
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
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.cadixdev.lorenz.io.tiny;

import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.io.MappingsWriter;
import org.cadixdev.lorenz.io.TextMappingsWriter;
import org.cadixdev.lorenz.model.ClassMapping;
import org.cadixdev.lorenz.model.FieldMapping;
import org.cadixdev.lorenz.model.Mapping;
import org.cadixdev.lorenz.model.MethodMapping;

import java.io.IOException;
import java.io.Writer;

/**
 * An implementation of {@link MappingsWriter} for the Tiny V1 format.
 *
 * @author Bleach
 * @since 1.0.0
 */
public class TinyV1Writer extends TextMappingsWriter {

    private String from;
    private String to;

    public TinyV1Writer(final Writer writer) {
        super(writer);
    }

    public TinyV1Writer withFormats(String from, String to) {
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
        writer.println("v1\t" + from + "\t" + to);

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
            writer.println("CLASS\t"
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
        // The hasDeobfuscatedName test should have already have been performed, so we're good
        writer.println("FIELD\t"
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
        // The hasDeobfuscatedName test should have already have been performed, so we're good
        writer.println("METHOD\t"
                + mapping.getParent().getFullObfuscatedName() + "\t"
                + mapping.getObfuscatedDescriptor() + "\t"
                + mapping.getObfuscatedName() + "\t"
                + mapping.getDeobfuscatedName());
    }
}
