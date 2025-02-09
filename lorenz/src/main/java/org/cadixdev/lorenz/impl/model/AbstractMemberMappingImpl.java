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

import org.cadixdev.lorenz.model.Mapping;
import org.cadixdev.lorenz.model.MemberMapping;

import java.util.Objects;

/**
 * An abstract basic implementation of {@link MemberMapping}.
 *
 * @param <M> The type of the mapping
 * @param <P> The type of the parent mapping
 *
 * @author Jamie Mansfield
 * @since 0.2.0
 */
public abstract class AbstractMemberMappingImpl<M extends MemberMapping<M, P>, P extends Mapping>
        extends AbstractMappingImpl<M, P>
        implements MemberMapping<M, P> {

    private final P parent;

    /**
     * Creates a new member mapping, from the given parameters.
     *
     * @param parent The mapping, this mapping belongs to
     * @param obfuscatedName The obfuscated name
     * @param deobfuscatedName The de-obfuscated name
     */
    protected AbstractMemberMappingImpl(final P parent, final String obfuscatedName,
            final String deobfuscatedName) {
        super(parent.getMappings(), obfuscatedName, deobfuscatedName);
        this.parent = parent;
    }

    @Override
    public P getParent() {
        return parent;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), parent.getFullObfuscatedName(), parent.getFullDeobfuscatedName());
    }

}
