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

import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.util.MappingChangedListener;
import org.cadixdev.lorenz.util.Reversible;

import java.util.List;

/**
 * Represents a de-obfuscation mapping for mappable constructs of the Java
 * class format - e.g. classes, fields, and methods.
 *
 * @param <M> The type of the mapping, used for chaining
 * @param <P> The type of the parent object
 *
 * @author Jamie Mansfield
 * @since 0.1.0
 */
public interface Mapping<M extends Mapping<M, P>, P> extends Reversible<M, P> {

    /**
     * Gets the obfuscated name of the member being represented.
     *
     * @return The obfuscated name
     */
    String getObfuscatedName();

    /**
     * Gets the de-obfuscated name of the member being represented.
     * This can be altered through use of {@link #setDeobfuscatedName(String)}.
     *
     * @return The de-obfuscated name
     */
    String getDeobfuscatedName();

    /**
     * Sets the de-obfuscated name of the member.
     *
     * @param deobfuscatedName The new de-obfuscated name
     * @return {@code this} for chaining
     */
    M setDeobfuscatedName(final String deobfuscatedName);

    /**
     * Registers a {@link MappingChangedListener mapping changed listener},
     * that is called whenever the de-obfuscated name of the mapping is
     * changed.
     * <p>
     * Listeners will be called <strong>before</strong> the name change is
     * applied, so calling the mapping will return the original name.
     *
     * @param listener The listener
     * @return {@code this} for chaining
     * @since 0.6.0
     */
    M addListener(final MappingChangedListener<M, P> listener);

    /**
     * De-registers a {@link MappingChangedListener mapping changed listener},
     * from the mapping.
     *
     * @param listener The listener to remove
     * @since 0.6.0
     */
    void removeListener(final MappingChangedListener<M, P> listener);

    /**
     * Gets the unqualified ("simple") obfuscated name of the member.
     *
     * @return The simple obfuscated name
     * @since 0.4.0
     */
    default String getSimpleObfuscatedName() {
        return getObfuscatedName();
    }

    /**
     * Gets the unqualified ("simple") de-obfuscated name of the member.
     *
     * @return The simple de-obfuscated name
     * @since 0.4.0
     */
    default String getSimpleDeobfuscatedName() {
        return getDeobfuscatedName();
    }

    /**
     * Gets the fully-qualified obfuscated name of the member.
     *
     * @return The fully-qualified obfuscated name
     */
    String getFullObfuscatedName();

    /**
     * Gets the fully-qualified de-obfuscated name of the member.
     *
     * @return The fully-qualified de-obfuscated name
     */
    String getFullDeobfuscatedName();

    /**
     * Establishes whether the mapping has had a de-obfuscated name set.
     *
     * @return {@code True} if the mapping is mapped, {@code false} otherwise
     */
    boolean hasDeobfuscatedName();

    /**
     * Gets the Javadoc of the member, this will never be {@code null}.
     *
     * @return The Javadoc of the member
     */
    List<String> getJavadoc();

    /**
     * Gets the {@link MappingSet} that the mappings belongs to.
     *
     * @return The owning mapping set
     */
    MappingSet getMappings();

    /**
     * Merges this mapping with another, to a given parent.
     *
     * @param with The mapping to merge with
     * @param parent The parent
     * @return The new mapping
     * @since 0.5.0
     */
    M merge(final M with, final P parent);

    /**
     * Clones this mapping, to a given parent.
     *
     * @param parent The parent
     * @return The cloned mapping
     * @since 0.5.0
     */
    M copy(final P parent);
}
