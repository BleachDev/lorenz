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
 * Represents a de-obfuscation mapping for an inner class.
 *
 * @author Jamie Mansfield
 * @since 0.1.0
 */
public interface InnerClassMapping extends ClassMapping<InnerClassMapping, ClassMapping>, MemberMapping<InnerClassMapping, ClassMapping> {

    /**
     * Sets the de-obfuscated name of this inner class mapping.
     *
     * <em>Implementations will need to support the input being
     * a fully-qualified name!</em>
     *
     * @param deobfuscatedName The new de-obfuscated name
     * @return {@code this}, for chaining
     */
    @Override
    InnerClassMapping setDeobfuscatedName(final String deobfuscatedName);

    /**
     * {@inheritDoc}
     *
     * <p><strong>Note:</strong> The simple name is empty for anonymous classes.
     * For local classes, the leading digits are stripped.</p>
     */
    @Override
    String getSimpleObfuscatedName();

    /**
     * {@inheritDoc}
     *
     * <p><strong>Note:</strong> The simple name is empty for anonymous classes.
     * For local classes, the leading digits are stripped.</p>
     */
    @Override
    String getSimpleDeobfuscatedName();

    @Override
    default String getFullObfuscatedName() {
        return String.format("%s$%s", getParent().getFullObfuscatedName(), getObfuscatedName());
    }

    @Override
    default String getFullDeobfuscatedName() {
        return String.format("%s$%s", getParent().getFullDeobfuscatedName(), getDeobfuscatedName());
    }

    @Override
    default String getObfuscatedPackage() {
        return getParent().getObfuscatedPackage();
    }

    @Override
    default String getDeobfuscatedPackage() {
        return getParent().getDeobfuscatedPackage();
    }

    @Override
    default InnerClassMapping reverse(final ClassMapping parent) {
        final InnerClassMapping mapping = parent.createInnerClassMapping(getDeobfuscatedName(), getObfuscatedName());
        getFieldMappings().forEach(field -> field.reverse(mapping));
        getMethodMappings().forEach(method -> method.reverse(mapping));
        getInnerClassMappings().forEach(klass -> klass.reverse(mapping));
        return mapping;
    }

    @Override
    default InnerClassMapping merge(final InnerClassMapping with, final ClassMapping parent) {
        return MappingSetMerger.create(getMappings(), with.getMappings()).mergeInnerClass(this, with, parent);
    }

    @Override
    default InnerClassMapping copy(final ClassMapping parent) {
        final InnerClassMapping mapping = parent.createInnerClassMapping(getObfuscatedName(), getDeobfuscatedName());
        getFieldMappings().forEach(field -> field.copy(mapping));
        getMethodMappings().forEach(method -> method.copy(mapping));
        getInnerClassMappings().forEach(klass -> klass.copy(mapping));
        return mapping;
    }
}
