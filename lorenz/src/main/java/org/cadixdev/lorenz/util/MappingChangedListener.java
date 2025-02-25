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

package org.cadixdev.lorenz.util;

import org.cadixdev.lorenz.model.Mapping;

/**
 * A listener for observing changes in {@link Mapping mappings}.
 *
 * @param <M> The type of the mapping being changed
 * @param <P> The type of the mapping's parent
 *
 * @author Jamie Mansfield
 * @since 0.6.0
 */
@FunctionalInterface
public interface MappingChangedListener<M extends Mapping<M, P>, P> {

    /**
     * Called whenever the mapping's name is to be changed.
     *
     * @param mapping The mapping (still with the original de-obfuscated name)
     * @param newName The new de-obfuscated name
     */
    void handle(final M mapping, final String newName);

}
