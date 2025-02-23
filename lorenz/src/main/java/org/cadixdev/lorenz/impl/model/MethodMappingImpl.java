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

import org.cadixdev.bombe.type.signature.MethodSignature;
import org.cadixdev.lorenz.model.ClassMapping;
import org.cadixdev.lorenz.model.MethodMapping;
import org.cadixdev.lorenz.model.MethodParameterMapping;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A basic implementation of {@link MethodMapping}.
 *
 * @author Jamie Mansfield
 * @since 0.2.0
 */
public class MethodMappingImpl
        extends AbstractMemberMappingImpl<MethodMapping, ClassMapping>
        implements MethodMapping {

    private final MethodSignature signature;
    private final Map<Integer, MethodParameterMapping> parameters = new ConcurrentHashMap<>();

    /**
     * Creates a new method mapping, from the given parameters.
     *
     * @param parentClass The class mapping, this mapping belongs to
     * @param signature The signature
     * @param deobfuscatedName The de-obfuscated name
     */
    public MethodMappingImpl(final ClassMapping parentClass, final MethodSignature signature, final String deobfuscatedName) {
        super(parentClass, signature.getName(), deobfuscatedName);
        this.signature = signature;
    }

    @Override
    public MethodSignature getSignature() {
        return signature;
    }

    @Override
    public Collection<MethodParameterMapping> getParameterMappings() {
        return Collections.unmodifiableCollection(parameters.values());
    }

    @Override
    public MethodParameterMapping createParameterMapping(final int index, final String deobfuscatedName) {
        return parameters.compute(index, (i, mapping) -> {
            if (mapping != null) return mapping.setDeobfuscatedName(deobfuscatedName);
            return getMappings().getModelFactory().createMethodParameterMapping(this, i, deobfuscatedName);
        });
    }

    @Override
    public Optional<MethodParameterMapping> getParameterMapping(final int index) {
        return Optional.ofNullable(parameters.get(index));
    }

    @Override
    public boolean hasParameterMapping(final int index) {
        return parameters.containsKey(index);
    }

    @Override
    protected StringJoiner buildToString() {
        return super.buildToString()
                .add("obfuscatedSignature=" + getObfuscatedDescriptor())
                .add("deobfuscatedSignature=" + getDeobfuscatedDescriptor())
                .add("parameters=" + getParameterMappings());
    }

    @Override
    public boolean equals(final Object obj) {
        if (!super.equals(obj)) return false;
        if (!(obj instanceof MethodMapping)) return false;
        
        final MethodMapping that = (MethodMapping) obj;
        return Objects.equals(signature, that.getSignature());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), signature);
    }

}
