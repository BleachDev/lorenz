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

/**
 * <p>
 * This enum represents a configuration value for determining how strictly method mappings will be merged via
 * {@link MappingSetMerger}. Use it with {@link MergeConfig} and {@link MappingSetMerger#create(MappingSet, MappingSet, MergeConfig)}.
 *
 * @author Kyle Wood
 * @since 0.5.4
 */
public enum MethodMergeStrategy {

    /**
     * Only match exactly. This either means:
     * <ul>
     *     <li>For continuation merges, the full deobfuscated output signature of the left mapping must match the
     *         full obfuscated input signature of the right mapping.</li>
     *     <li>For duplicate merges, the full obfuscated input signature of the left mapping must match the full
     *         obfuscated input signature of the right mapping.</li>
     * </ul>
     * For more information regarding continuation and duplicate merges, see the <b>Continuations and Duplicates</b>
     * section of {@link MappingSetMergerHandler}.
     */
    STRICT,

    /**
     * Match the same as {@link #STRICT}, but also match more broadly:
     * <ul>
     *     <li>For continuation merges, the deobfuscated output name and obfuscated input descriptor of the left mapping
     *         can match the obfuscated input signature of the right mapping.</li>
     *     <li>For duplicate merges, the obfuscated input name and deobfuscated output descriptor of the left mapping
     *         can match the obfuscated input signature of the right mapping.</li>
     * </ul>
     * For more information regarding continuation and duplicate merges, see the <b>Continuations and Duplicates</b>
     * section of {@link MappingSetMergerHandler}.
     */
    LOOSE

}
