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

package org.cadixdev.lorenz.model;

import org.cadixdev.lorenz.merge.MappingSetMerger;

/**
 * Represents a de-obfuscation mapping for a method parameter,
 * identified by index.
 *
 * @author Jamie Mansfield
 * @since 0.4.0
 */
public interface MethodParameterMapping extends MemberMapping<MethodParameterMapping, MethodMapping> {

    /**
     * Gets the index of the method parameter being mapped.
     *
     * @return The index
     */
    int getIndex();

    @Override
    default String getObfuscatedName() {
        return String.valueOf(getIndex());
    }

    @Override
    default MethodParameterMapping reverse(final MethodMapping parent) {
        return parent.createParameterMapping(getIndex(), getDeobfuscatedName());
    }

    @Override
    default MethodParameterMapping merge(final MethodParameterMapping with, final MethodMapping parent) {
        return MappingSetMerger.create(getMappings(), with.getMappings()).mergeMethodParameter(this, with, parent);
    }

    @Override
    default MethodParameterMapping copy(final MethodMapping parent) {
        return parent.createParameterMapping(getIndex(), getDeobfuscatedName());
    }

}
