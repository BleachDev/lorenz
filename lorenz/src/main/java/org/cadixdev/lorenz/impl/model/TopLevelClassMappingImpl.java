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

package org.cadixdev.lorenz.impl.model;

import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.model.TopLevelClassMapping;

/**
 * A basic implementation of {@link TopLevelClassMapping}.
 *
 * @author Jamie Mansfield
 * @since 0.2.0
 */
public class TopLevelClassMappingImpl
        extends AbstractClassMappingImpl<TopLevelClassMapping, MappingSet>
        implements TopLevelClassMapping {

    /**
     * Creates a new top-level class mapping, from the given parameters.
     *
     * @param mappings The mappings set, this mapping belongs to
     * @param obfuscatedName The obfuscated name
     * @param deobfuscatedName The de-obfuscated name
     */
    public TopLevelClassMappingImpl(final MappingSet mappings, final String obfuscatedName, final String deobfuscatedName) {
        super(mappings, obfuscatedName.replace('.', '/'), deobfuscatedName.replace('.', '/'));
    }

    @Override
    public TopLevelClassMapping setDeobfuscatedName(String deobfuscatedName) {
        return super.setDeobfuscatedName(deobfuscatedName.replace('.', '/'));
    }

    @Override
    public boolean equals(final Object obj) {
        if (!super.equals(obj)) return false;
        return obj instanceof TopLevelClassMapping;
    }

}
