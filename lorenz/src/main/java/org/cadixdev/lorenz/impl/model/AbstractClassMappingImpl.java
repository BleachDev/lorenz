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

package org.cadixdev.lorenz.impl.model;

import org.cadixdev.bombe.analysis.InheritanceProvider;
import org.cadixdev.bombe.analysis.InheritanceType;
import org.cadixdev.bombe.type.MethodDescriptor;
import org.cadixdev.bombe.type.signature.FieldSignature;
import org.cadixdev.bombe.type.signature.MethodSignature;
import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.model.ClassMapping;
import org.cadixdev.lorenz.model.FieldMapping;
import org.cadixdev.lorenz.model.InnerClassMapping;
import org.cadixdev.lorenz.model.MethodMapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A basic implementation of {@link ClassMapping}.
 *
 * @param <M> The type of the class mapping
 * @param <P> The type of the parent
 *
 * @author Jamie Mansfield
 * @since 0.2.0
 */
public abstract class AbstractClassMappingImpl<M extends ClassMapping<M, P>, P>
        extends AbstractMappingImpl<M, P>
        implements ClassMapping<M, P> {

    private final Map<FieldSignature, FieldMapping> fields = new ConcurrentHashMap<>();
    private final Map<String, FieldMapping> fieldsByName = new ConcurrentHashMap<>();
    private final Map<MethodSignature, MethodMapping> methods = new ConcurrentHashMap<>();
    private final Map<String, InnerClassMapping> innerClasses = new ConcurrentHashMap<>();
    private boolean complete;

    /**
     * Creates a new class mapping, from the given parameters.
     *
     * @param mappings The mappings set, this mapping belongs to
     * @param obfuscatedName The obfuscated name
     * @param deobfuscatedName The de-obfuscated name
     */
    protected AbstractClassMappingImpl(final MappingSet mappings, final String obfuscatedName, final String deobfuscatedName) {
        super(mappings, obfuscatedName, deobfuscatedName);
    }

    @Override
    public Collection<FieldMapping> getFieldMappings() {
        return Collections.unmodifiableCollection(fields.values());
    }

    @Override
    public Map<String, FieldMapping> getFieldsByName() {
        return Collections.unmodifiableMap(fieldsByName);
    }

    @Override
    public Optional<FieldMapping> getFieldMapping(final FieldSignature signature) {
        return Optional.ofNullable(fields.get(signature));
    }

    @Override
    public Optional<FieldMapping> getFieldMapping(final String obfuscatedName) {
        return Optional.ofNullable(fieldsByName.get(obfuscatedName));
    }

    @Override
    public Optional<FieldMapping> computeFieldMapping(final FieldSignature signature) {
        // If the field type is not provided, lookup up only the field name
        if (!signature.getType().isPresent()) {
            return getFieldMapping(signature.getName());
        }

        // Otherwise, look up the signature as-is, but attempt falling back to a signature without type
        // Note: We cannot use fieldsByName here, because we'd eventually return FieldMappings with the wrong type
        return Optional.ofNullable(fields.computeIfAbsent(signature, (sig) -> {
            final FieldMapping mapping = fields.get(new FieldSignature(sig.getName()));
            return mapping != null ?
                    getMappings().getModelFactory().createFieldMapping(mapping.getParent(), sig, mapping.getDeobfuscatedName()) : null;
        }));
    }

    @Override
    public FieldMapping createFieldMapping(final FieldSignature signature, final String deobfuscatedName) {
        return fields.compute(signature, (sig, existingMapping) -> {
            if (existingMapping != null) return existingMapping.setDeobfuscatedName(deobfuscatedName);
            final FieldMapping mapping = getMappings().getModelFactory().createFieldMapping(this, sig, deobfuscatedName);
            fieldsByName.put(sig.getName(), mapping);
            return mapping;
        });
    }

    @Override
    public boolean hasFieldMapping(final FieldSignature signature) {
        return getFieldMapping(signature).isPresent();
    }

    @Override
    public boolean hasFieldMapping(final String obfuscatedName) {
        return fieldsByName.containsKey(obfuscatedName);
    }

    @Override
    public void removeFieldMapping(final FieldSignature signature) {
        final FieldMapping mapping = fields.remove(signature);
        if (mapping != null) {
            fieldsByName.values().remove(mapping);
        }
    }

    @Override
    public void removeFieldMapping(final FieldMapping mapping) {
        fields.values().remove(mapping);
        fieldsByName.values().remove(mapping);
    }

    @Override
    public void removeFieldMapping(final String obfuscatedName) {
        fields.keySet().removeIf(sig -> sig.getName().equals(obfuscatedName));
        fieldsByName.remove(obfuscatedName);
    }

    @Override
    public Collection<MethodMapping> getMethodMappings() {
        return Collections.unmodifiableCollection(methods.values());
    }

    @Override
    public Optional<MethodMapping> getMethodMapping(final MethodSignature signature) {
        return Optional.ofNullable(methods.get(signature));
    }

    @Override
    public MethodMapping createMethodMapping(final MethodSignature signature, final String deobfuscatedName) {
        return methods.compute(signature, (desc, existingMapping) -> {
            if (existingMapping != null) return existingMapping.setDeobfuscatedName(deobfuscatedName);
            return getMappings().getModelFactory().createMethodMapping(this, signature, deobfuscatedName);
        });
    }

    @Override
    public boolean hasMethodMapping(final MethodSignature signature) {
        return methods.containsKey(signature);
    }

    @Override
    public void removeMethodMapping(final MethodSignature signature) {
        methods.remove(signature);
    }

    @Override
    public void removeMethodMapping(final MethodMapping mapping) {
        methods.values().remove(mapping);
    }

    @Override
    public Collection<InnerClassMapping> getInnerClassMappings() {
        return Collections.unmodifiableCollection(innerClasses.values());
    }

    @Override
    public Optional<InnerClassMapping> getInnerClassMapping(final String obfuscatedName) {
        return Optional.ofNullable(innerClasses.get(obfuscatedName));
    }

    @Override
    public InnerClassMapping createInnerClassMapping(final String obfuscatedName, final String deobfuscatedName) {
        return innerClasses.compute(obfuscatedName, (name, existingMapping) -> {
            if (existingMapping != null) return existingMapping.setDeobfuscatedName(deobfuscatedName);
            return getMappings().getModelFactory().createInnerClassMapping(this, obfuscatedName, deobfuscatedName);
        });
    }

    @Override
    public boolean hasInnerClassMapping(final String obfuscatedName) {
        return innerClasses.containsKey(obfuscatedName);
    }

    @Override
    public void removeInnerClassMapping(String obfuscatedName) {
        innerClasses.remove(obfuscatedName);
    }

    @Override
    public void removeInnerClassMapping(final ClassMapping<?, ?> mapping) {
        innerClasses.values().remove(mapping);
    }

    @Override
    protected StringJoiner buildToString() {
        return super.buildToString()
                .add("fields=" + getFieldMappings())
                .add("methods=" + getMethodMappings())
                .add("innerClasses=" + getInnerClassMappings());
    }

    @Override
    public boolean equals(final Object obj) {
        if (!super.equals(obj)) return false;
        if (!(obj instanceof ClassMapping)) return false;

        final ClassMapping<?, ?> that = (ClassMapping<?, ?>) obj;
        return Objects.equals(new ArrayList<>(getFieldMappings()), new ArrayList<>(that.getFieldMappings())) &&
                Objects.equals(new ArrayList<>(getMethodMappings()), new ArrayList<>(that.getMethodMappings())) &&
                Objects.equals(new ArrayList<>(getInnerClassMappings()), new ArrayList<>(that.getInnerClassMappings()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), fields, methods, innerClasses);
    }

    @Override
    public boolean isComplete() {
        return complete;
    }

    @Override
    public void complete(final InheritanceProvider provider, final InheritanceProvider.ClassInfo info) {
        if (complete) {
            return;
        }

        final Map<String, Set<MethodSignature>> nameToMethods = new HashMap<>();
        for (final Map.Entry<MethodSignature, InheritanceType> method : info.getMethods().entrySet()) {
            final Set<MethodSignature> methods = nameToMethods.computeIfAbsent(method.getKey().getName(), name -> new HashSet<>());
            methods.add(method.getKey());
        }

        for (final InheritanceProvider.ClassInfo parent : info.provideParents(provider)) {
            final ClassMapping<?, ?> parentMappings = getMappings().getOrCreateClassMapping(parent.getName());
            parentMappings.complete(provider, parent);

            for (final FieldMapping mapping : parentMappings.getFieldMappings()) {
                // If the class has its own field that satisfies the parent's signature,
                // then we shouldn't inherit the mapping
                if (computeFieldMapping(mapping.getSignature()).isPresent()) {
                    continue;
                }

                if (parent.canInherit(info, mapping.getSignature())) {
                    fields.putIfAbsent(mapping.getSignature(), mapping);
                }
            }

            for (final MethodMapping mapping : parentMappings.getMethodMappings()) {
                if (parent.canInherit(info, mapping.getSignature())) {
                    methods.putIfAbsent(mapping.getSignature(), mapping);
                }

                // Check if there are any methods here that override the return type of a parent
                // method.
                if (nameToMethods.containsKey(mapping.getObfuscatedName())) {
                    for (final MethodSignature methodSignature : nameToMethods.get(mapping.getObfuscatedName())) {
                        final MethodDescriptor methodDescriptor = methodSignature.getDescriptor();

                        final MethodSignature mappingSignature = mapping.getSignature();
                        final MethodDescriptor mappingDescriptor = mappingSignature.getDescriptor();

                        // The method MUST have the same parameters
                        // TODO: handle generic params
                        if (!Objects.equals(methodDescriptor.getParamTypes(), mappingDescriptor.getParamTypes())) continue;

                        if (mappingDescriptor.getReturnType().isAssignableFrom(methodDescriptor.getReturnType(), provider)) {
                            methods.putIfAbsent(methodSignature, mapping);
                        }
                    }
                }
            }
        }

        complete = true;
    }

}
