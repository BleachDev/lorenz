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

/**
 * Represents a mapping that is a member to a {@link ClassMapping}.
 *
 * @param <M> The type of the mapping
 * @param <P> The type of the parent mapping
 *
 * @see FieldMapping
 * @see MethodMapping
 * @see InnerClassMapping
 *
 * @author Jamie Mansfield
 * @since 0.1.0
 */
public interface MemberMapping<M extends MemberMapping<M, P>, P extends Mapping> extends Mapping<M, P> {

    /**
     * Gets the parent {@link Mapping} of this member mapping.
     *
     * @return The parent mapping
     * @since 0.4.0
     */
    P getParent();

}
