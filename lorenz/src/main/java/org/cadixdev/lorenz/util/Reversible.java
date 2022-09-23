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

/**
 * Interface for describing an object that can be reversed.
 *
 * @param <T> The type of the reversible object
 * @param <P> The type of the parent object
 *
 * @author Jamie Mansfield
 * @since 0.5.0
 */
public interface Reversible<T, P> {

    /**
     * Produces a new object that is a reverse copy of the original.
     *
     * @param parent The parent object
     * @return The reversed object
     */
    T reverse(final P parent);

}
