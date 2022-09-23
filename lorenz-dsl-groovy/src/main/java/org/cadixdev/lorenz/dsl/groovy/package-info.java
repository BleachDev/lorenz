/*
 * This file is part of Lorenz, licensed under the MIT License (MIT).
 *
 * Copyright (c) Jamie Mansfield <https://www.jamierocks.uk/>, Bleach <https://bleach.dev/> and contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 */

/**
 * A Groovy DSL, that simplifies the creation of Lorenz mappings.
 *
 * <pre>
 *     def mappings = MappingSetDsl.create {
 *         klass('a') {
 *             deobf = 'Demo'
 *             extension EXTRA, 'a.class'
 *             field('g') { deobf = 'name' }
 *             method('h', '()Ljava/lang/String;') { deobf = 'getName' }
 *         }
 *     }
 * </pre>
 *
 * @author Jamie Mansfield
 * @since 0.6.0
 */
package org.cadixdev.lorenz.dsl.groovy;
