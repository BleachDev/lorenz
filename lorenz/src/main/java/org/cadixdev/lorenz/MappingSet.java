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

package org.cadixdev.lorenz;

import org.cadixdev.bombe.type.ArrayType;
import org.cadixdev.bombe.type.FieldType;
import org.cadixdev.bombe.type.MethodDescriptor;
import org.cadixdev.bombe.type.ObjectType;
import org.cadixdev.bombe.type.Type;
import org.cadixdev.lorenz.impl.MappingSetModelFactoryImpl;
import org.cadixdev.lorenz.merge.MappingSetMerger;
import org.cadixdev.lorenz.model.ClassMapping;
import org.cadixdev.lorenz.model.InnerClassMapping;
import org.cadixdev.lorenz.model.TopLevelClassMapping;
import org.cadixdev.lorenz.util.Reversible;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * The container for {@link TopLevelClassMapping}s, allowing for their creating and
 * locating any {@link ClassMapping}.
 *
 * @author Jamie Mansfield
 * @since 0.1.0
 */
public class MappingSet implements Reversible<MappingSet, MappingSet>, Iterable<TopLevelClassMapping> {

    private final MappingSetModelFactory modelFactory;
    private final Map<String, TopLevelClassMapping> topLevelClasses = new ConcurrentHashMap<>();

    /**
     * Creates a mapping set using the default {@link MappingSetModelFactory}.
     */
    public MappingSet() {
        this(MappingSetModelFactoryImpl.INSTANCE);
    }

    /**
     * Creates a mapping set using the provided {@link MappingSetModelFactory}.
     *
     * @param modelFactory The model factory to use
     */
    public MappingSet(final MappingSetModelFactory modelFactory) {
        this.modelFactory = modelFactory;
    }

    /**
     * Gets the underlying model factory, that is used to construct
     * the implementation classes for all the models.
     *
     * @return The model factory
     */
    public MappingSetModelFactory getModelFactory() {
        return modelFactory;
    }

    /**
     * Gets an immutable collection of all of the top-level class
     * mappings of the mapping set.
     *
     * @return The top-level class mappings
     */
    public Collection<TopLevelClassMapping> getTopLevelClassMappings() {
        return Collections.unmodifiableCollection(topLevelClasses.values());
    }

    /**
     * Creates a top-level class mapping with the given obfuscated and de-obfuscated
     * names.
     *
     * @param obfuscatedName The obfuscated name of the top-level class
     * @param deobfuscatedName The de-obfuscated name of the top-level class
     * @return The top-level class mapping, to allow for chaining
     */
    public TopLevelClassMapping createTopLevelClassMapping(final String obfuscatedName, final String deobfuscatedName) {
        return topLevelClasses.compute(obfuscatedName.replace('.', '/'), (name, existingMapping) -> {
            if (existingMapping != null) return existingMapping.setDeobfuscatedName(deobfuscatedName);
            return getModelFactory().createTopLevelClassMapping(this, name, deobfuscatedName);
        });
    }

    /**
     * Gets the top-level class mapping of the given obfuscated name of the
     * class mapping, should it exist.
     *
     * @param obfuscatedName The obfuscated name of the top-level class mapping
     * @return The top-level class mapping, wrapped in an {@link Optional}
     */
    public Optional<TopLevelClassMapping> getTopLevelClassMapping(final String obfuscatedName) {
        return Optional.ofNullable(topLevelClasses.get(obfuscatedName.replace('.', '/')));
    }

    /**
     * Gets, or creates should it not exist, a top-level class mapping of the
     * given obfuscated name.
     *
     * @param obfuscatedName The obfuscated name of the top-level class mapping
     * @return The top-level class mapping
     */
    public TopLevelClassMapping getOrCreateTopLevelClassMapping(final String obfuscatedName) {
        return getTopLevelClassMapping(obfuscatedName)
                .orElseGet(() -> createTopLevelClassMapping(obfuscatedName, obfuscatedName));
    }

    /**
     * Establishes whether the mapping set contains a top-level class
     * mapping of the given obfuscated name.
     *
     * @param obfuscatedName The obfuscated name of the top-level class
     *                       mapping
     * @return {@code true} should a top-level class mapping of the
     *         given obfuscated name exist in the mapping set;
     *         {@code false} otherwise
     */
    public boolean hasTopLevelClassMapping(final String obfuscatedName) {
        return topLevelClasses.containsKey(obfuscatedName.replace('.', '/'));
    }

    /**
     * Gets the class mapping of the given obfuscated name.
     *
     * @param obfuscatedName The obfuscated name
     * @return The class mapping, wrapped in an {@link Optional}
     */
    public Optional<? extends ClassMapping<?, ?>> getClassMapping(final String obfuscatedName) {
        final int lastIndex = obfuscatedName.lastIndexOf('$');
        if (lastIndex == -1) return getTopLevelClassMapping(obfuscatedName);

        // Split the obfuscated name, to fetch the parent class name, and inner class name
        final String parentClassName = obfuscatedName.substring(0, lastIndex);
        final String innerClassName = obfuscatedName.substring(lastIndex + 1);

        // Get the parent class
        return getClassMapping(parentClassName)
                // Get and return the inner class
                .flatMap(parentClassMapping -> parentClassMapping.getInnerClassMapping(innerClassName));
    }

    /**
     * Remove the class mapping for the given obfuscated name.
     *
     * @param obfuscatedName The class name to remove.
     */
    public void removeClassMapping(final String obfuscatedName) {
        getClassMapping(obfuscatedName).ifPresent(this::removeClassMapping);
    }

    /**
     * Remove the given {@link ClassMapping}.
     *
     * @param mapping The mapping to remove.
     */
    public void removeClassMapping(final ClassMapping<?, ?> mapping) {
        if (mapping instanceof InnerClassMapping) {
            ((InnerClassMapping) mapping).getParent().removeInnerClassMapping(mapping);
        } else {
            topLevelClasses.values().remove(mapping);
        }
    }

    /**
     * Attempts to locate a class mapping for the given obfuscated name.
     *
     * <p>This is equivalent to calling {@link #getClassMapping(String)},
     * except that it will insert a new inner class mapping in case a
     * class mapping for the outer class exists.</p>
     *
     * <p>This method exists to simplify remapping, where it is important
     * to keep inner classes a part of the outer class.</p>
     *
     * @param obfuscatedName The obfuscated name
     * @return The class mapping, wrapped in an {@link Optional}
     */
    public Optional<? extends ClassMapping<?, ?>> computeClassMapping(final String obfuscatedName) {
        final int lastIndex = obfuscatedName.lastIndexOf('$');
        if (lastIndex == -1) return getTopLevelClassMapping(obfuscatedName);

        // Split the obfuscated name, to fetch the parent class name, and inner class name
        final String parentClassName = obfuscatedName.substring(0, lastIndex);
        final String innerClassName = obfuscatedName.substring(lastIndex + 1);

        // Get the parent class
        return getClassMapping(parentClassName)
                // Get and return the inner class
                .map(parentClassMapping -> parentClassMapping.getOrCreateInnerClassMapping(innerClassName));
    }

    /**
     * Gets, or creates should it not exist, a class mapping, of the given
     * obfuscated name.
     *
     * @param obfuscatedName The obfuscated name of the class mapping
     * @return The class mapping
     */
    public ClassMapping<?, ?> getOrCreateClassMapping(final String obfuscatedName) {
        final int lastIndex = obfuscatedName.lastIndexOf('$');
        if (lastIndex == -1) return getOrCreateTopLevelClassMapping(obfuscatedName);

        // Split the obfuscated name, to fetch the parent class name, and inner class name
        final String parentClassName = obfuscatedName.substring(0, lastIndex);
        final String innerClassName = obfuscatedName.substring(lastIndex + 1);

        // Get the parent class
        final ClassMapping parentClass = getOrCreateClassMapping(parentClassName);

        // Get the inner class
        return parentClass.getOrCreateInnerClassMapping(innerClassName);
    }

    /**
     * Gets the de-obfuscated view of the given type.
     *
     * @param type The type to de-obfuscate
     * @return The de-obfuscated type
     * @since 0.5.0
     */
    public Type deobfuscate(final Type type) {
        if (type instanceof FieldType) {
            return deobfuscate((FieldType) type);
        }
        return type;
    }

    /**
     * Gets the de-obfuscated view of the given field type.
     *
     * @param type The type to de-obfuscate
     * @return The de-obfuscated type
     * @since 0.5.0
     */
    public FieldType deobfuscate(final FieldType type) {
        if (type instanceof ArrayType) {
            final ArrayType arr = (ArrayType) type;
            final FieldType component = deobfuscate(arr.getComponent());
            return component == arr.getComponent() ?
                    arr :
                    new ArrayType(arr.getDimCount(), component);
        }
        else if (type instanceof ObjectType) {
            final ObjectType obj = (ObjectType) type;

            final String[] name = obj.getClassName().split("\\$");

            ClassMapping<?, ?> currentClass = getClassMapping(name[0]).orElse(null);
            if (currentClass == null) {
                return type;
            }

            for (int i = 1; i < name.length; i++) {
                final ClassMapping<?, ?> thisClass = currentClass.getInnerClassMapping(name[i]).orElse(null);
                if (thisClass == null) {
                    final String[] result = new String[name.length - i + 1];
                    result[0] = currentClass.getFullDeobfuscatedName();
                    System.arraycopy(name, i, result, 1, name.length - i);

                    return new ObjectType(String.join("$", result));
                }
                currentClass = thisClass;
            }

            return new ObjectType(currentClass.getFullDeobfuscatedName());
        }
        return type;
    }

    /**
     * Gets the de-obfuscated descriptor of the method.
     *
     * @param descriptor The descriptor to de-obfuscate
     * @return The de-obfuscated descriptor
     * @since 0.5.0
     */
    public MethodDescriptor deobfuscate(final MethodDescriptor descriptor) {
        return new MethodDescriptor(
                descriptor.getParamTypes().stream()
                        .map(this::deobfuscate)
                        .collect(Collectors.toList()),
                deobfuscate(descriptor.getReturnType())
        );
    }

    /**
     * Produces a new mapping set that is a reverse copy of the original.
     *
     * @return The reversed set
     * @since 0.5.0
     */
    public MappingSet reverse() {
        return reverse(createMappingSet());
    }

    @Override
    public MappingSet reverse(final MappingSet parent) {
        getTopLevelClassMappings().forEach(klass -> klass.reverse(parent));
        return parent;
    }

    /**
     * Produces a new mapping set, that is a merged copy with the provided
     * mappings.
     *
     * @param with The set to merge with
     * @return The merged set
     * @since 0.5.0
     */
    public MappingSet merge(final MappingSet with) {
        return merge(with, createMappingSet());
    }

    /**
     * Produces a new mapping set, that is a merged copy with the provided
     * mappings.
     *
     * @param with The set to merge with
     * @param parent The set to create entries
     * @return The merged set
     * @since 0.5.0
     */
    public MappingSet merge(final MappingSet with, final MappingSet parent) {
        return MappingSetMerger.create(this, with).merge(parent);
    }

    /**
     * Produces a new mapping set, which is a clone copy of the original.
     *
     * @return The cloned set
     * @since 0.5.0
     */
    public MappingSet copy() {
        final MappingSet mappings = createMappingSet();
        getTopLevelClassMappings().forEach(klass -> klass.copy(mappings));
        return mappings;
    }

    @Override
    public Iterator<TopLevelClassMapping> iterator() {
        return topLevelClasses.values().iterator();
    }

    protected MappingSet createMappingSet() {
        return new MappingSet(modelFactory);
    }

}
