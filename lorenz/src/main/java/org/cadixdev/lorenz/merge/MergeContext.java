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

package org.cadixdev.lorenz.merge;

import org.cadixdev.lorenz.MappingSet;

import java.util.Objects;

/**
 * <p>
 * This class represents the current context for a remapping operation for a {@link MappingSetMergerHandler}. This
 * allows implementers of {@link MappingSetMergerHandler}s access to each of the 4 combinations of mappings for a merge:
 * </p>
 * <ul>
 *     <li>The left mapping set</li>
 *     <li>The right mapping set</li>
 *     <li>The reverse of the left mapping set</li>
 *     <li>The reverse of the right mapping set</li>
 * </ul>
 * <p>
 * This class represents a performance advantage for retrieving the reverse of the mappings as well, as it only computes
 * the value of the reverse mapping set when requested, and only computes the value once per merge session.
 * </p>
 * <p>
 * This class is thread safe and all methods are reentrant.
 * </p>
 *
 * @author Kyle Wood
 * @since 0.5.4
 */
@SuppressWarnings("unused") // Unused methods for third-party handler implementations
public class MergeContext {

    private final MappingSet left;
    private final MappingSet right;

    private MappingSet leftReversed;
    private MappingSet rightReversed;

    /**
     * Create a new {@link MappingSet} merge context. Provide the left and right mapping sets for the merge, neither may
     * be {@code null}.
     *
     * @param left The {@code left} mapping set, must not be {@code null}.
     * @param right The {@code right} mapping set, must not be {@code null}.
     */
    public MergeContext(final MappingSet left, final MappingSet right) {
        this.left = Objects.requireNonNull(left, "Left MappingSet may not be null");
        this.right = Objects.requireNonNull(right, "Right MappingSet may not be null");
    }

    /**
     * @return The {@code left} {@link MappingSet} for the merge operation, not {@code null}.
     */
    public MappingSet getLeft() {
        return left;
    }

    /**
     * @return The {@code right} {@link MappingSet} for the merge operation, not {@code null}.
     */
    public MappingSet getRight() {
        return right;
    }

    /**
     * @return The {@link #getRight() right} {@link MappingSet} for the merge operation, but reversed. Never
     *         {@code null}. This method computes the value on first query and caches the result, making it more
     *         efficient than calling {@link MappingSet#reverse()} yourself.
     */
    public MappingSet getLeftReversed() {
        MappingSet ctx = leftReversed;
        if (ctx != null) {
            return ctx;
        }

        synchronized (left) {
            ctx = leftReversed;
            if (ctx != null) {
                return ctx;
            }
            leftReversed = left.reverse();
            ctx = leftReversed;
        }

        return ctx;
    }

    /**
     * @return The {@link #getRight() right} {@link MappingSet} for the merge operation, but reversed. Never
     *         {@code null}. This method computes the value on first query and caches the result, making it more
     *         efficient than calling {@link MappingSet#reverse()} yourself.
     */
    public MappingSet getRightReversed() {
        MappingSet ctx = rightReversed;
        if (ctx != null) {
            return ctx;
        }

        synchronized (right) {
            ctx = rightReversed;
            if (ctx != null) {
                return ctx;
            }
            rightReversed = right.reverse();
            ctx = rightReversed;
        }

        return ctx;
    }
}
