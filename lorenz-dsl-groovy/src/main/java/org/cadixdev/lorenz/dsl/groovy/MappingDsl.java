/*
 * This file is part of Lorenz, licensed under the MIT License (MIT).
 *
 * Copyright (c) Jamie Mansfield <https://www.jamierocks.uk/>
 * Copyright (c) contributors
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
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.cadixdev.lorenz.dsl.groovy;

import org.cadixdev.lorenz.model.Mapping;
import org.cadixdev.lorenz.util.MappingChangedListener;

/**
 * A DSL to simplify the manipulation of {@link Mapping}s in Groovy.
 *
 * @param <T> The type of the mapping
 * @author Jamie Mansfield
 * @since 0.6.0
 */
public class MappingDsl<T extends Mapping<T, P>, P> {

    /**
     * The mapping manipulated by this DSL.
     */
    protected final T mapping;

    public MappingDsl(final T mapping) {
        this.mapping = mapping;
    }

    /**
     * Sets the de-obfuscated name of the mapping.
     *
     * @param name The de-obfuscated name
     * @see Mapping#setDeobfuscatedName(String)
     */
    public void setDeobf(final String name) {
        mapping.setDeobfuscatedName(name);
    }

    /**
     * Adds the given listener to the mapping.
     *
     * @param listener The mapping listener
     * @see Mapping#addListener(MappingChangedListener)
     */
    public void listener(final MappingChangedListener<T, P> listener) {
        mapping.addListener(listener);
    }

}
