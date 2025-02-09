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

package org.cadixdev.lorenz.dsl.groovy;

import groovy.lang.Closure;

import java.util.function.Function;

/**
 * Internal utility functions for the Lorenz Groovy DSL.
 *
 * @author Jamie Mansfield
 * @since 0.6.0
 */
class DslUtil {

    static final int RESOLVE_STRATEGY = Closure.DELEGATE_FIRST;

    static void setupAndCallDelegateClosure(final Object delegate, final Closure<?> script) {
        script.setResolveStrategy(DslUtil.RESOLVE_STRATEGY);
        script.setDelegate(delegate);
        script.call();
    }

    static <T> T delegate(final T obj, final Function<T, Object> delegate, final Closure<?> script) {
        setupAndCallDelegateClosure(delegate.apply(obj), script);
        return obj;
    }

    private DslUtil() {
    }

}
