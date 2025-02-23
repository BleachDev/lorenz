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

package org.cadixdev.lorenz.io.proguard;

import org.cadixdev.bombe.type.FieldType;
import org.cadixdev.bombe.type.MethodDescriptor;
import org.cadixdev.bombe.type.Type;
import org.cadixdev.bombe.type.signature.FieldSignature;
import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.io.MappingsReader;
import org.cadixdev.lorenz.io.TextMappingsReader;
import org.cadixdev.lorenz.model.ClassMapping;

import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An implementation of {@link MappingsReader} for the ProGuard format.
 *
 * @author Jamie Mansfield
 * @since 0.5.1
 */
public class ProGuardReader extends TextMappingsReader {

    private ClassMapping<?, ?> currentClass;

    public ProGuardReader(final Reader reader) {
        super(reader);
    }

    @Override
    protected void readLine(final MappingSet mappings, final String rawLine) {
        // Ignore comments
        if (rawLine.startsWith("#")) return;

        final String[] params = rawLine.trim().split(" ");

        if (params.length == 3 && params[1].equals("->")) {
            final String obf = params[0].replace('.', '/');
            // remove the trailing :
            final String deobf = params[2].substring(0, params[2].length() - 1).replace('.', '/');

            currentClass = mappings.getOrCreateClassMapping(obf)
                    .setDeobfuscatedName(deobf);
        }

        if (params.length == 4 && params[2].equals("->")) {
            final String returnTypeRaw = params[0];
            final String obf = params[1];
            final String deobf = params[3];

            // method
            if (obf.contains("(")) {
                // remove any line numbers
                final int index = returnTypeRaw.lastIndexOf(':');
                final String returnCleanRaw = index != -1 ?
                        returnTypeRaw.substring(index + 1) :
                        returnTypeRaw;
                final Type returnClean = new PGTypeReader(returnCleanRaw).readType();

                final String obfName = obf.substring(0, obf.indexOf('('));
                final String[] obfParams = obf.substring(obf.indexOf('(') + 1, obf.length() - 1).split(",");
                final List<FieldType> paramTypes = Arrays.stream(obfParams)
                        .filter(line -> !line.isEmpty())
                        .map(PGTypeReader::new)
                        .map(PGTypeReader::readFieldType)
                        .collect(Collectors.toList());

                currentClass.getOrCreateMethodMapping(obfName, new MethodDescriptor(paramTypes, returnClean))
                        .setDeobfuscatedName(deobf);
            }
            // field
            else {
                final FieldSignature fieldSignature = new FieldSignature(obf, new PGTypeReader(returnTypeRaw).readFieldType());
                currentClass.getOrCreateFieldMapping(fieldSignature)
                        .setDeobfuscatedName(deobf);
            }
        }
    }

}
