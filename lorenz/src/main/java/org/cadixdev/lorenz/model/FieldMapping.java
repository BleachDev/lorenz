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

package org.cadixdev.lorenz.model;

import org.cadixdev.bombe.type.FieldType;
import org.cadixdev.bombe.type.signature.FieldSignature;

import java.util.Optional;
import org.cadixdev.lorenz.merge.MappingSetMerger;

/**
 * Represents a de-obfuscation mapping for fields.
 *
 * @author Jamie Mansfield
 * @since 0.1.0
 */
public interface FieldMapping extends MemberMapping<FieldMapping, ClassMapping> {

    /**
     * Gets the signature of this field mapping.
     *
     * @return The signature
     * @since 0.4.0
     */
    FieldSignature getSignature();

    /**
     * Gets the de-obfuscated signature of this field mapping.
     *
     * @return The signature
     * @since 0.5.0
     */
    default FieldSignature getDeobfuscatedSignature() {
        return getType().map(fieldType -> new FieldSignature(getDeobfuscatedName(), getMappings().deobfuscate(fieldType)))
                .orElseGet(() -> new FieldSignature(getDeobfuscatedName()));
    }

    @Override
    default String getObfuscatedName() {
        return getSignature().getName();
    }

    /**
     * Gets the {@link FieldType} of the field, if at all available.
     *
     * @return The type, wrapped in an {@link Optional}
     * @since 0.4.0
     */
    default Optional<FieldType> getType() {
        return getSignature().getType();
    }

    @Override
    default String getFullObfuscatedName() {
        return String.format("%s/%s", getParent().getFullObfuscatedName(), getObfuscatedName());
    }

    @Override
    default String getFullDeobfuscatedName() {
        return String.format("%s/%s", getParent().getFullDeobfuscatedName(), getDeobfuscatedName());
    }

    @Override
    default FieldMapping reverse(final ClassMapping parent) {
        return parent.createFieldMapping(getDeobfuscatedSignature(), getObfuscatedName());
    }

    @Override
    default FieldMapping merge(final FieldMapping with, final ClassMapping parent) {
        return MappingSetMerger.create(getMappings(), with.getMappings()).mergeField(this, with, parent);
    }

    @Override
    default FieldMapping copy(final ClassMapping parent) {
        return parent.createFieldMapping(getSignature(), getDeobfuscatedName());
    }

}
