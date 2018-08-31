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

package me.jamiemansfield.lorenz.asm.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import me.jamiemansfield.bombe.asm.jar.SourceSet;
import me.jamiemansfield.bombe.type.FieldType;
import me.jamiemansfield.bombe.type.ObjectType;
import me.jamiemansfield.lorenz.MappingSet;
import me.jamiemansfield.lorenz.asm.AsmFieldTypeProvider;
import me.jamiemansfield.lorenz.model.FieldMapping;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import java.util.Optional;

public final class AsmFieldTypeProviderTest {

    @Test
    public void fetchFieldType() {
        final MappingSet mappings = MappingSet.create();
        final FieldMapping field = mappings.getOrCreateTopLevelClassMapping("ght")
                .getOrCreateFieldMapping("op");

        final ClassNode node = new ClassNode();
        node.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC, "ght", null, "java/lang/Object", null);
        node.visitField(Opcodes.ACC_PUBLIC, "op", "Ljava/util/logging/Logger;", null, null);

        final SourceSet sources = new SourceSet();
        sources.add(node);
        mappings.addFieldTypeProvider(new AsmFieldTypeProvider(sources));

        final Optional<FieldType> type = field.getType();
        assertTrue(type.isPresent());
        assertTrue(type.get() instanceof ObjectType);
        assertEquals("java/util/logging/Logger", ((ObjectType) type.get()).getClassName());
    }

}
