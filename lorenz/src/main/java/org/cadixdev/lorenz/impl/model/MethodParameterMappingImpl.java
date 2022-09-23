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

import org.cadixdev.lorenz.model.MethodMapping;
import org.cadixdev.lorenz.model.MethodParameterMapping;

/**
 * A basic implementation of {@link MethodParameterMapping}.
 *
 * @author Jamie Mansfield
 * @since 0.4.0
 */
public class MethodParameterMappingImpl
        extends AbstractMemberMappingImpl<MethodParameterMapping, MethodMapping>
        implements MethodParameterMapping {

    private final int index;

    /**
     * Creates a new method parameter mapping, from the given parameters.
     *
     * @param parent The mapping, this mapping belongs to
     * @param index The index of the parameter
     * @param deobfuscatedName The de-obfuscated name
     */
    public MethodParameterMappingImpl(final MethodMapping parent, final int index, String deobfuscatedName) {
        super(parent, String.valueOf(index), deobfuscatedName);
        this.index = index;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public String getFullObfuscatedName() {
        return getObfuscatedName();
    }

    @Override
    public String getFullDeobfuscatedName() {
        return getDeobfuscatedName();
    }

    @Override
    public boolean equals(final Object obj) {
        if (!super.equals(obj)) return false;
        return obj instanceof MethodParameterMapping;
    }

}
