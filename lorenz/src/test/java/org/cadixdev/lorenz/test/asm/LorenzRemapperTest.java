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

package org.cadixdev.lorenz.test.asm;

import org.cadixdev.bombe.analysis.CompositeInheritanceProvider;
import org.cadixdev.bombe.analysis.InheritanceProvider;
import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.asm.LorenzRemapper;
import org.cadixdev.lorenz.model.InnerClassMapping;
import org.cadixdev.lorenz.model.TopLevelClassMapping;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.tree.ClassNode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public final class LorenzRemapperTest {

    private static final MappingSet MAPPINGS = new MappingSet();
    private static final InheritanceProvider INHERITANCE = new CompositeInheritanceProvider();
    private static final LorenzRemapper REMAPPER = new LorenzRemapper(MAPPINGS, INHERITANCE);

    static {
        final TopLevelClassMapping ght = MAPPINGS.getOrCreateTopLevelClassMapping("ght");
        final InnerClassMapping ght$hy = ght.getOrCreateInnerClassMapping("hy");

        // Mappings for #topLevelClass()
        ght.setDeobfuscatedName("Demo");

        // - #topLevelClassField()
        ght.getOrCreateFieldMapping("iu")
                .setDeobfuscatedName("log");

        // - #topLevelClassMethod()
        ght.getOrCreateMethodMapping("trp", "()V")
                .setDeobfuscatedName("run");

        // Mappings for #innerClass()
        ght$hy.setDeobfuscatedName("Inner");

        // - #innerClassField()
        ght$hy.getOrCreateFieldMapping("rt")
                .setDeobfuscatedName("name");

        // - #innerClassMethod()
        ght$hy.getOrCreateMethodMapping("kjl", "()Ljava/lang/String;")
                .setDeobfuscatedName("getName");
    }

    @Test
    public void topLevelClass() {
        final ClassNode node = new ClassNode();
        final ClassRemapper remapper = new ClassRemapper(node, REMAPPER);
        remapper.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC, "ght", null, "java/lang/Object", null);
        assertEquals("Demo", node.name);
    }

    @Test
    public void topLevelClassField() {
        final ClassNode node = new ClassNode();
        final ClassRemapper remapper = new ClassRemapper(node, REMAPPER);
        remapper.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC, "ght", null, "java/lang/Object", null);
        remapper.visitField(Opcodes.ACC_PUBLIC, "iu", "Ljava/util/logging/Logger;", null, null);
        assertFalse(node.fields.isEmpty());
        assertEquals("log", node.fields.get(0).name);
    }

    @Test
    public void topLevelClassMethod() {
        final ClassNode node = new ClassNode();
        final ClassRemapper remapper = new ClassRemapper(node, REMAPPER);
        remapper.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC, "ght", null, "java/lang/Object", null);
        remapper.visitMethod(Opcodes.ACC_PUBLIC, "trp", "()V", null, null);
        assertFalse(node.methods.isEmpty());
        assertEquals("run", node.methods.get(0).name);
    }

    @Test
    public void innerClass() {
        // rename classes properly
        {
            final ClassNode node = new ClassNode();
            final ClassRemapper remapper = new ClassRemapper(node, REMAPPER);
            remapper.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC, "ght$hy", null, "java/lang/Object", null);
            assertEquals("Demo$Inner", node.name);
        }
        // handle renaming attribute properly
        {
            final ClassNode node = new ClassNode();
            final ClassRemapper remapper = new ClassRemapper(node, REMAPPER);
            remapper.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC, "ght", null, "java/lang/Object", null);
            remapper.visitInnerClass("ght$hy", "ght", "hy", Opcodes.ACC_PUBLIC);
            assertFalse(node.innerClasses.isEmpty());
            assertEquals("Demo", node.innerClasses.get(0).outerName);
            assertEquals("Inner", node.innerClasses.get(0).innerName);
        }
    }

    @Test
    public void innerClassField() {
        final ClassNode node = new ClassNode();
        final ClassRemapper remapper = new ClassRemapper(node, REMAPPER);
        remapper.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC, "ght$hy", null, "java/lang/Object", null);
        remapper.visitField(Opcodes.ACC_PUBLIC, "rt", "Ljava/lang/String;", null, null);
        assertFalse(node.fields.isEmpty());
        assertEquals("name", node.fields.get(0).name);
    }

    @Test
    public void innerClassMethod() {
        final ClassNode node = new ClassNode();
        final ClassRemapper remapper = new ClassRemapper(node, REMAPPER);
        remapper.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC, "ght$hy", null, "java/lang/Object", null);
        remapper.visitMethod(Opcodes.ACC_PUBLIC, "kjl", "()Ljava/lang/String;", null, null);
        assertFalse(node.methods.isEmpty());
        assertEquals("getName", node.methods.get(0).name);
    }

}
