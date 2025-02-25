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

import org.cadixdev.bombe.type.ArrayType;
import org.cadixdev.bombe.type.FieldType;
import org.cadixdev.bombe.type.MethodDescriptor;
import org.cadixdev.bombe.type.ObjectType;
import org.cadixdev.bombe.type.Type;
import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.io.MappingsReader;
import org.cadixdev.lorenz.io.TextMappingsReader;
import org.cadixdev.lorenz.model.ClassMapping;
import org.cadixdev.lorenz.model.Mapping;
import org.cadixdev.lorenz.model.MethodMapping;

import java.io.Reader;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.stream.Collectors;

/**
 * An implementation of {@link MappingsReader} for the Enigma format.
 *
 * @author Jamie Mansfield
 * @since 0.4.0
 */
public class EnigmaReader extends TextMappingsReader {

    private static final String CLASS_MAPPING_KEY = "CLASS";
    private static final String FIELD_MAPPING_KEY = "FIELD";
    private static final String METHOD_MAPPING_KEY = "METHOD";
    private static final String PARAM_MAPPING_KEY = "ARG";
    private static final String COMMENT_MAPPING_KEY = "COMMENT";

    private static final int CLASS_MAPPING_ELEMENT_WITH_DEOBF_COUNT = 3;
    private static final int CLASS_MAPPING_ELEMENT_WITHOUT_DEOBF_COUNT = 2;
    private static final int FIELD_MAPPING_ELEMENT_COUNT = 4;
    private static final int METHOD_MAPPING_ELEMENT_WITH_DEOBF_COUNT = 4;
    private static final int METHOD_MAPPING_ELEMENT_WITHOUT_DEOBF_COUNT = 3;
    private static final int PARAM_MAPPING_ELEMENT_COUNT = 3;

    protected final Deque<Mapping<?, ?>> stack = new ArrayDeque<>();

    public EnigmaReader(final Reader reader) {
        super(reader);
    }

    private static int getIndentLevel(final String line) {
        int indentLevel = 0;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) != '\t') break;
            indentLevel++;
        }
        return indentLevel;
    }

    @Override
    public void readLine(final MappingSet mappings, final String rawLine) {
        final int indentLevel = getIndentLevel(rawLine);

        // If there is a change in the indentation level, we will need to pop the stack
        // as needed
        while (indentLevel < stack.size()) {
            stack.pop();
        }

        final String line = EnigmaMappingFormat.INSTANCE.removeComments(rawLine).trim();
        if (line.isEmpty()) return;

        // Split up the line, for further processing
        final String[] split = SPACE.split(line);
        final int len = split.length;

        // Establish the type of mapping
        final String key = split[0];
        if (key.equals(CLASS_MAPPING_KEY) && len == CLASS_MAPPING_ELEMENT_WITHOUT_DEOBF_COUNT) {
            final String obfName = convertClassName(split[1]);
            stack.push(readClassMapping(mappings, obfName));
        } else if (key.equals(CLASS_MAPPING_KEY) && len == CLASS_MAPPING_ELEMENT_WITH_DEOBF_COUNT) {
            final String obfName = convertClassName(split[1]);
            final String deobfName = convertClassName(split[2]);
            stack.push(readClassMapping(mappings, obfName)
                    .setDeobfuscatedName(deobfName));
        } else if (key.equals(FIELD_MAPPING_KEY) && len == FIELD_MAPPING_ELEMENT_COUNT) {
            final String obfName = split[1];
            final String deobfName = split[2];
            final String type = convertFieldType(FieldType.of(split[3])).toString();
            peekClass().getOrCreateFieldMapping(obfName, type)
                    .setDeobfuscatedName(deobfName);
        } else if (key.equals(METHOD_MAPPING_KEY) && len == METHOD_MAPPING_ELEMENT_WITHOUT_DEOBF_COUNT) {
            final String obfName = split[1];
            final String descriptor = convertDescriptor(MethodDescriptor.of(split[2])).toString();
            stack.push(peekClass().getOrCreateMethodMapping(obfName, descriptor));
        } else if (key.equals(METHOD_MAPPING_KEY) && len == METHOD_MAPPING_ELEMENT_WITH_DEOBF_COUNT) {
            final String obfName = split[1];
            final String deobfName = split[2];
            final String descriptor = convertDescriptor(MethodDescriptor.of(split[3])).toString();
            stack.push(peekClass().getOrCreateMethodMapping(obfName, descriptor)
                    .setDeobfuscatedName(deobfName));
        } else if (key.equals(PARAM_MAPPING_KEY) && len == PARAM_MAPPING_ELEMENT_COUNT) {
            final int index = Integer.parseInt(split[1]);
            final String deobfName = split[2];
            peekMethod().getOrCreateParameterMapping(index)
                    .setDeobfuscatedName(deobfName);
        } else if (key.equals(COMMENT_MAPPING_KEY)) {
            final String comment = split.length == 1 ? "" : rawLine.substring(split[0].length() + 1);
            stack.peek().getJavadoc().add(comment);
        }
    }

    protected ClassMapping<?, ?> peekClass() {
        if (!(stack.peek() instanceof ClassMapping)) throw new UnsupportedOperationException("Not a class on the stack!");
        return (ClassMapping<?, ?>) stack.peek();
    }

    protected MethodMapping peekMethod() {
        if (!(stack.peek() instanceof MethodMapping)) throw new UnsupportedOperationException("Not a method on the stack!");
        return (MethodMapping) stack.peek();
    }

    protected ClassMapping<?, ?> readClassMapping(final MappingSet mappings, final String obfName) {
        return mappings.getOrCreateClassMapping(obfName);
    }

    protected String convertClassName(final String descriptor) {
        if (descriptor.startsWith("none/")) {
            return descriptor.substring("none/".length());
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

    protected MethodDescriptor convertDescriptor(final MethodDescriptor descriptor) {
        return new MethodDescriptor(
                descriptor.getParamTypes().stream()
                        .map(this::convertFieldType)
                        .collect(Collectors.toList()),
                convertType(descriptor.getReturnType())
        );
    }

}
