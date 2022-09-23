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

import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.model.Mapping;
import org.cadixdev.lorenz.util.MappingChangedListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * An abstract basic implementation of {@link Mapping}.
 *
 * @param <M> The type of the mapping
 * @param <P> The type of the parent
 *
 * @author Jamie Mansfield
 * @since 0.2.0
 */
public abstract class AbstractMappingImpl<M extends Mapping<M, P>, P> implements Mapping<M, P> {

    private final MappingSet mappings;
    private final String obfuscatedName;
    private String deobfuscatedName;
    private final List<String> javadoc = new ArrayList<>();
    private final List<MappingChangedListener<M, P>> listeners = new ArrayList<>();

    /**
     * Creates a new de-obfuscation mapping, based on the given obfuscated name
     * and de-obfuscated name.
     *
     * @param mappings The mappings set, this mapping belongs to
     * @param obfuscatedName The obfuscated name
     * @param deobfuscatedName The de-obfuscated name
     */
    protected AbstractMappingImpl(final MappingSet mappings, final String obfuscatedName, final String deobfuscatedName) {
        this.mappings = mappings;
        this.obfuscatedName = obfuscatedName;
        this.deobfuscatedName = deobfuscatedName;
    }

    @Override
    public String getObfuscatedName() {
        return obfuscatedName;
    }

    @Override
    public String getDeobfuscatedName() {
        return deobfuscatedName;
    }

    @Override
    public M setDeobfuscatedName(final String deobfuscatedName) {
        for (final MappingChangedListener<M, P> listener : listeners) {
            listener.handle((M) this, deobfuscatedName);
        }
        this.deobfuscatedName = deobfuscatedName;
        return (M) this;
    }

    @Override
    public M addListener(final MappingChangedListener<M, P> listener) {
        listeners.add(listener);
        return (M) this;
    }

    @Override
    public void removeListener(final MappingChangedListener<M, P> listener) {
        listeners.remove(listener);
    }

    @Override
    public boolean hasDeobfuscatedName() {
        return !Objects.equals(obfuscatedName, deobfuscatedName);
    }

    @Override
    public List<String> getJavadoc() {
        return javadoc;
    }

    @Override
    public MappingSet getMappings() {
        return mappings;
    }

    protected StringJoiner buildToString() {
        return new StringJoiner(", ", getClass().getSimpleName() + "{", "}")
                .add("obfuscatedName=" + obfuscatedName)
                .add("deobfuscatedName=" + deobfuscatedName);
    }

    @Override
    public String toString() {
        return buildToString().toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Mapping)) return false;

        final Mapping<?, ?> that = (Mapping<?, ?>) obj;
        return Objects.equals(obfuscatedName, that.getObfuscatedName()) &&
                Objects.equals(deobfuscatedName, that.getDeobfuscatedName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(obfuscatedName, deobfuscatedName);
    }

}
