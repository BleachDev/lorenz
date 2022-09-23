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

import org.cadixdev.bombe.type.FieldType;
import org.cadixdev.bombe.type.MethodDescriptor;
import org.cadixdev.bombe.type.Type;
import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.io.MappingsReader;
import org.cadixdev.lorenz.model.ClassMapping;
import org.cadixdev.lorenz.model.Mapping;

import java.io.Reader;

/**
 * A {@link MappingsReader mappings reader} for Fabric's fork of the Enigma
 * format.
 *
 * @author Jamie Mansfield
 * @since 0.6.0
 */
public class FabricEnigmaReader extends EnigmaReader {

    public FabricEnigmaReader(final Reader reader) {
        super(reader);
    }

    @Override
    protected ClassMapping<?, ?> readClassMapping(final MappingSet mappings, final String obfName) {
        // Fabric's fork of the Enigma format doesn't use full de-obfuscated
        // names when printing classes (practically this affects inner classes).
        final Mapping<?, ?> mapping = stack.peek();
        if (mapping == null) {
            return mappings.getOrCreateTopLevelClassMapping(obfName);
        }
        if (!(mapping instanceof ClassMapping)) {
            throw new UnsupportedOperationException("Not a class on the stack!");
        }

        return ((ClassMapping<?, ?>) mapping).getOrCreateInnerClassMapping(obfName);
    }

    @Override
    protected String convertClassName(final String descriptor) {
        // Fabric's fork of the Enigma format doesn't add a 'none/' prefix
        // to un-packaged classes.
        return descriptor;
    }

    @Override
    protected Type convertType(final Type type) {
        // Fabric's fork of the Enigma format doesn't add a 'none/' prefix
        // to un-packaged classes.
        return type;
    }

    @Override
    protected FieldType convertFieldType(final FieldType type) {
        // Fabric's fork of the Enigma format doesn't add a 'none/' prefix
        // to un-packaged classes.
        return type;
    }

    @Override
    protected MethodDescriptor convertDescriptor(final MethodDescriptor descriptor) {
        // Fabric's fork of the Enigma format doesn't add a 'none/' prefix
        // to un-packaged classes.
        return descriptor;
    }
}
