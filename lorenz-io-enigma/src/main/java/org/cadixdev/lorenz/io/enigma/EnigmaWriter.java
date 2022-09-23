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

package org.cadixdev.lorenz.io.enigma;

import org.cadixdev.bombe.type.ArrayType;
import org.cadixdev.bombe.type.FieldType;
import org.cadixdev.bombe.type.MethodDescriptor;
import org.cadixdev.bombe.type.ObjectType;
import org.cadixdev.bombe.type.Type;
import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.io.MappingsWriter;
import org.cadixdev.lorenz.io.TextMappingsWriter;
import org.cadixdev.lorenz.model.ClassMapping;
import org.cadixdev.lorenz.model.FieldMapping;
import org.cadixdev.lorenz.model.InnerClassMapping;
import org.cadixdev.lorenz.model.Mapping;
import org.cadixdev.lorenz.model.MethodMapping;
import org.cadixdev.lorenz.model.MethodParameterMapping;

import java.io.IOException;
import java.io.Writer;

/**
 * An implementation of {@link MappingsWriter} for the Enigma format.
 *
 * @author Jamie Mansfield
 * @since 0.4.0
 */
public class EnigmaWriter extends TextMappingsWriter {

    public EnigmaWriter(final Writer writer) {
        super(writer);
    }

    @Override
    public void write(final MappingSet mappings) throws IOException {
        mappings.getTopLevelClassMappings().stream()
                .filter(ClassMapping::hasMappings)
                .sorted(getConfig().getClassMappingComparator())
                .forEach(klass -> writeClassMapping(klass, 0));
    }

    private void writeClassMapping(final ClassMapping<?, ?> klass, final int indent) {
        printClassMapping(klass, indent);

        // Write inner class mappings
        klass.getInnerClassMappings().stream()
                .filter(ClassMapping::hasMappings)
                .sorted(getConfig().getClassMappingComparator())
                .forEach(inner -> writeClassMapping(inner, indent + 1));

        // Write field mappings
        klass.getFieldMappings().stream()
                .filter(Mapping::hasDeobfuscatedName)
                .sorted(getConfig().getFieldMappingComparator())
                .forEach(field -> writeFieldMapping(field, indent + 1));

        // Write method mappings
        klass.getMethodMappings().stream()
                .filter(MethodMapping::hasMappings)
                .sorted(getConfig().getMethodMappingComparator())
                .forEach(method -> writeMethodMapping(method, indent + 1));
    }

    private void writeFieldMapping(final FieldMapping field, final int indent) {
        // The SHOULD_WRITE test should have already have been performed, so we're good
        field.getType().ifPresent(type -> {
            printMapping(field, indent, String.format("FIELD %s %s %s",
                    field.getObfuscatedName(),
                    field.getDeobfuscatedName(),
                    convertFieldType(type)
            ));
        });
        // TODO: throw an exception if the type is unknown / WriterResult container
    }

    private void writeMethodMapping(final MethodMapping method, final int indent) {
        // The SHOULD_WRITE test should have already have been performed, so we're good
        if (method.hasDeobfuscatedName()) {
            printMapping(method, indent, String.format("METHOD %s %s %s",
                    method.getObfuscatedName(),
                    method.getDeobfuscatedName(),
                    convertDescriptor(method.getDescriptor())
            ));
        } else {
            printMapping(method, indent, String.format("METHOD %s %s",
                    method.getObfuscatedName(),
                    convertDescriptor(method.getDescriptor())
            ));
        }

        for (final MethodParameterMapping param : method.getParameterMappings()) {
            printMapping(param, indent + 1, String.format("ARG %s %s",
                    param.getIndex(),
                    param.getDeobfuscatedName()
            ));
        }
    }
    protected void printClassMapping(final ClassMapping<?, ?> klass, final int indent) {
        final String obfName = convertClassName(klass.getFullObfuscatedName());
        if (klass.hasDeobfuscatedName()) {
            final String deobfName = klass instanceof InnerClassMapping ?
                    klass.getDeobfuscatedName() :
                    convertClassName(klass.getDeobfuscatedName());
            printMapping(klass, indent, "CLASS " + obfName + " " + deobfName);
        } else {
            printMapping(klass, indent, "CLASS " + obfName);
        }
    }

    protected void printMapping(final Mapping<?, ?> mapping, final int indent, final String line) {
        printIndent(indent);
        writer.println(line);

        for (String comment : mapping.getJavadoc()) {
            printIndent(indent + 1);
            writer.println(comment.isEmpty() ? "COMMENT" : String.format("COMMENT %s", comment));
        }
    }

    private void printIndent(int indent) {
        for (int i = 0; i < indent; i++) {
            writer.print('\t');
        }
    }

    protected String convertClassName(final String descriptor) {
        if (!descriptor.contains("/")) {
            return "none/" + descriptor;
        }
        return descriptor;
    }

    protected Type convertType(final Type type) {
        if (type instanceof FieldType) {
            return convertFieldType((FieldType) type);
        }
        return type;
    }

    protected FieldType convertFieldType(final FieldType type) {
        if (type instanceof ArrayType) {
            final ArrayType arr = (ArrayType) type;
            return new ArrayType(arr.getDimCount(), convertFieldType(arr.getComponent()));
        }
        if (type instanceof ObjectType) {
            final ObjectType obj = (ObjectType) type;
            return new ObjectType(convertClassName(obj.getClassName()));
        }
        return type;
    }

    protected String convertDescriptor(final MethodDescriptor descriptor) {
        final StringBuilder typeBuilder = new StringBuilder();
        typeBuilder.append("(");
        descriptor.getParamTypes().forEach(type -> typeBuilder.append(convertFieldType(type)));
        typeBuilder.append(")");
        typeBuilder.append(convertType(descriptor.getReturnType()));
        return typeBuilder.toString();
    }

}
