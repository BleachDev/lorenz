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

import org.cadixdev.bombe.type.MethodDescriptor;
import org.cadixdev.bombe.type.signature.MethodSignature;
import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.merge.MappingSetMerger;

import java.util.Collection;
import java.util.Optional;

/**
 * Represents a de-obfuscation mapping for methods.
 *
 * @author Jamie Mansfield
 * @since 0.1.0
 */
public interface MethodMapping extends MemberMapping<MethodMapping, ClassMapping> {

    /**
     * Gets the signature of this method mapping.
     *
     * @return The signature
     */
    MethodSignature getSignature();

    /**
     * Gets the de-obfuscated signature of this method mapping.
     *
     * @return The de-obfuscated signature
     * @since 0.5.0
     */
    default MethodSignature getDeobfuscatedSignature() {
       return new MethodSignature(getDeobfuscatedName(), getMappings().deobfuscate(getDescriptor()));
    }

    /**
     * Gets the {@link MethodDescriptor} of the method.
     *
     * @return The method descriptor
     * @see MethodSignature#getDescriptor()
     */
    default MethodDescriptor getDescriptor() {
        return getSignature().getDescriptor();
    }

    /**
     * Gets the obfuscated descriptor of the method.
     *
     * @return The obfuscated descriptor
     * @see MethodSignature#getDescriptor()
     * @see MethodDescriptor#toString()
     */
    default String getObfuscatedDescriptor() {
        return getDescriptor().toString();
    }

    /**
     * Gets the de-obfuscated descriptor of the method.
     *
     * @return The de-obfuscated descriptor
     * @see MethodSignature#getDescriptor()
     * @see MappingSet#deobfuscate(MethodDescriptor)
     */
    default String getDeobfuscatedDescriptor() {
        return getDeobfuscatedSignature().getDescriptor().toString();
    }

    /**
     * Gets an immutable view of all the {@link MethodParameterMapping}s
     * that belong to this method.
     *
     * @return The parameter mappings
     * @since 0.4.0
     */
    Collection<MethodParameterMapping> getParameterMappings();

    /**
     * Creates a {@link MethodParameterMapping} parented by this method, of
     * the given integer index.
     *
     * @param index The index of the parameter
     * @param deobfuscatedName The de-obfuscated name to give the parameter
     * @return The parameter mapping
     * @since 0.4.0
     */
    MethodParameterMapping createParameterMapping(final int index, final String deobfuscatedName);

    /**
     * Gets a {@link MethodParameterMapping}, if present, of the given
     * integer index.
     *
     * @param index The index of the parameter
     * @return The parameter mapping, wrapped in an {@link Optional}
     * @since 0.4.0
     */
    Optional<MethodParameterMapping> getParameterMapping(final int index);

    /**
     * Gets, or creates should it not exist, a parameter mapping of the
     * given integer index.
     *
     * @param index The index of the parameter
     * @return The parameter mapping
     * @since 0.4.0
     */
    default MethodParameterMapping getOrCreateParameterMapping(final int index) {
        return getParameterMapping(index)
                .orElseGet(() -> createParameterMapping(index, String.valueOf(index)));
    }

    /**
     * Establishes whether the method mapping contains a parameter mapping
     * of the integer index.
     *
     * @param index The index of the parameter
     * @return {@code true} should a parameter mapping of the given
     *         index exists in the method mapping; {@code false} otherwise
     * @since 0.4.0
     */
    boolean hasParameterMapping(final int index);

    /**
     * Establishes whether the method mapping has a de-obfuscation mapping, or
     * has some mappings within it.
     *
     * @return {@code true} if the method mappings has mappings;
     *         {@code false} otherwise
     * @since 0.4.0
     */
    default boolean hasMappings() {
        return hasDeobfuscatedName() ||
                getParameterMappings().stream().anyMatch(Mapping::hasDeobfuscatedName);
    }

    @Override
    default String getFullObfuscatedName() {
        return String.format("%s/%s", getParent().getFullObfuscatedName(), getObfuscatedName());
    }

    @Override
    default String getFullDeobfuscatedName() {
        return String.format("%s/%s", getParent().getFullDeobfuscatedName(), getDeobfuscatedName());
    }

    @Override
    default MethodMapping reverse(final ClassMapping parent) {
        final MethodMapping mapping = parent.createMethodMapping(getDeobfuscatedSignature(), getObfuscatedName());
        getParameterMappings().forEach(param -> param.reverse(mapping));
        return mapping;
    }

    @Override
    default MethodMapping merge(final MethodMapping with, final ClassMapping parent) {
        return MappingSetMerger.create(getMappings(), with.getMappings()).mergeMethod(this, with, parent);
    }

    @Override
    default MethodMapping copy(final ClassMapping parent) {
        final MethodMapping mapping = parent.createMethodMapping(getSignature(), getDeobfuscatedName());
        getParameterMappings().forEach(param -> param.copy(mapping));
        return mapping;
    }

}
