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

package org.cadixdev.lorenz.impl.model;

import org.cadixdev.bombe.type.signature.FieldSignature;
import org.cadixdev.lorenz.model.ClassMapping;
import org.cadixdev.lorenz.model.FieldMapping;

import java.util.Objects;

/**
 * A basic implementation of {@link FieldMapping}.
 *
 * @author Jamie Mansfield
 * @since 0.2.0
 */
public class FieldMappingImpl
        extends AbstractMemberMappingImpl<FieldMapping, ClassMapping>
        implements FieldMapping {

    private final FieldSignature signature;

    /**
     * Creates a new field mapping, from the given parameters.
     *
     * @param parentClass The class mapping, this mapping belongs to
     * @param signature The obfuscated signature
     * @param deobfuscatedName The de-obfuscated name
     */
    public FieldMappingImpl(final ClassMapping parentClass, final FieldSignature signature, final String deobfuscatedName) {
        super(parentClass, signature.getName(), deobfuscatedName);
        this.signature = signature;
    }

    @Override
    public FieldSignature getSignature() {
        return signature;
    }

    @Override
    public boolean equals(final Object obj) {
        if (!super.equals(obj)) return false;
        if (!(obj instanceof FieldMapping)) return false;

        final FieldMapping that = (FieldMapping) obj;
        return Objects.equals(signature, that.getSignature());
    }

}
