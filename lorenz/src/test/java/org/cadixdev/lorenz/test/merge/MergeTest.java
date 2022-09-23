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

package org.cadixdev.lorenz.test.merge;

import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.merge.FieldMergeStrategy;
import org.cadixdev.lorenz.merge.MappingSetMerger;
import org.cadixdev.lorenz.merge.MappingSetMergerHandler;
import org.cadixdev.lorenz.merge.MergeConfig;
import org.cadixdev.lorenz.merge.MergeContext;
import org.cadixdev.lorenz.merge.MergeResult;
import org.cadixdev.lorenz.merge.MethodMergeStrategy;
import org.cadixdev.lorenz.model.ClassMapping;
import org.cadixdev.lorenz.model.InnerClassMapping;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class MergeTest {

    @Test
    public void standardMergeTest() {
        MappingSet left = new MappingSet();
        left.getOrCreateClassMapping("foo/bar/A").setDeobfuscatedName("foo/bar/B")
                .getOrCreateFieldMapping("a").setDeobfuscatedName("b");
        MappingSet right = new MappingSet();
        right.getOrCreateClassMapping("foo/bar/B").setDeobfuscatedName("foo/bar/C")
                .getOrCreateFieldMapping("b").setDeobfuscatedName("c");
        MappingSet output = new MappingSet();
        output.getOrCreateClassMapping("foo/bar/A").setDeobfuscatedName("foo/bar/C")
                .getOrCreateFieldMapping("a").setDeobfuscatedName("c");
        testCase(left, right, output);
    }

    @Test
    public void missingEntriesTest() {
        MappingSet left = new MappingSet();
        left.getOrCreateClassMapping("foo/bar/A").setDeobfuscatedName("foo/bar/B")
                .getOrCreateFieldMapping("a").setDeobfuscatedName("b");
        MappingSet right = new MappingSet();
        right.getOrCreateClassMapping("foo/bar/B").setDeobfuscatedName("foo/bar/C")
                .getOrCreateFieldMapping("a1").setDeobfuscatedName("c1");
        MappingSet output = new MappingSet();
        output.getOrCreateClassMapping("foo/bar/A").setDeobfuscatedName("foo/bar/C")
                .getOrCreateFieldMapping("a").setDeobfuscatedName("b").getParent()
                .getOrCreateFieldMapping("a1").setDeobfuscatedName("c1");
        testCase(left, right, output);
    }

    @Test
    public void typesThenMembersTest() {
        MappingSet left = new MappingSet();
        left.getOrCreateClassMapping("A").setDeobfuscatedName("foo/bar/B");
        left.getOrCreateClassMapping("C").setDeobfuscatedName("bar/baz/D");
        left.getOrCreateClassMapping("E").setDeobfuscatedName("foo/bar/F");
        MappingSet right = new MappingSet();
        right.getOrCreateClassMapping("foo/bar/B").setDeobfuscatedName("foo/bar/B")
                .getOrCreateMethodMapping("a", "(ILbar/baz/D;Lfoo/bar/F;J)V").setDeobfuscatedName("new_a");
        MappingSet output = new MappingSet();
        output.getOrCreateClassMapping("A").setDeobfuscatedName("foo/bar/B")
                .getOrCreateMethodMapping("a", "(ILC;LE;J)V").setDeobfuscatedName("new_a");
        output.getOrCreateClassMapping("C").setDeobfuscatedName("bar/baz/D");
        output.getOrCreateClassMapping("E").setDeobfuscatedName("foo/bar/F");
        testCase(left, right, output);
    }

    @Test
    public void duplicateMembersTest() {
        MappingSet left = new MappingSet();
        left.getOrCreateClassMapping("A").setDeobfuscatedName("foo/bar/B")
                .getOrCreateFieldMapping("a").setDeobfuscatedName("b");
        MappingSet right = new MappingSet();
        right.getOrCreateClassMapping("foo/bar/B").setDeobfuscatedName("foo/bar/C")
                .getOrCreateFieldMapping("a").setDeobfuscatedName("c");
        MappingSet output = new MappingSet();
        output.getOrCreateClassMapping("A").setDeobfuscatedName("foo/bar/C")
                .getOrCreateFieldMapping("a").setDeobfuscatedName("c");
        testCase(left, right, output);
    }

    @Test
    public void overrideHandlerTest() {
        MappingSet left = new MappingSet();
        left.getOrCreateClassMapping("A").setDeobfuscatedName("foo/bar/B");
        left.getOrCreateClassMapping("A$a").setDeobfuscatedName("foo/bar/B$b")
                .getOrCreateFieldMapping("a").setDeobfuscatedName("b");
        MappingSet right = new MappingSet();
        right.getOrCreateClassMapping("foo/bar/B$b").setDeobfuscatedName("foo/bar/B$b")
                .getOrCreateFieldMapping("b").setDeobfuscatedName("c");
        MappingSet output = new MappingSet();
        output.getOrCreateClassMapping("A").setDeobfuscatedName("foo/bar/B");
        output.getOrCreateClassMapping("A$a").setDeobfuscatedName("foo/bar/B$a")
                .getOrCreateFieldMapping("a").setDeobfuscatedName("c");
        testCase(left, right, output, MergeConfig.builder()
                .withMergeHandler(new MappingSetMergerHandler() {
                    @Override
                    public MergeResult<InnerClassMapping> mergeInnerClassMappings(
                            final InnerClassMapping left,
                            final InnerClassMapping right,
                            final ClassMapping<?, ?> target,
                            final MergeContext context
                    ) {
                        return new MergeResult<>(target.createInnerClassMapping(left.getObfuscatedName(), left.getObfuscatedName()), right);
                    }
                })
                .build());
    }

    @Test
    public void combinedCaseStrict() {
        MappingSet left = new MappingSet();
        left.getOrCreateClassMapping("A").setDeobfuscatedName("B");
        left.getOrCreateClassMapping("A$1").setDeobfuscatedName("B$1")
                .getOrCreateMethodMapping("a", "()LA;").setDeobfuscatedName("a1");
        left.getOrCreateClassMapping("A$1$a").setDeobfuscatedName("B$1$b")
                .getOrCreateMethodMapping("b", "(LA$1;)V").setDeobfuscatedName("b1");
        MappingSet right = new MappingSet();
        right.getOrCreateClassMapping("B").setDeobfuscatedName("B")
                .getOrCreateFieldMapping("b").setDeobfuscatedName("c").getParent()
                .getOrCreateMethodMapping("b", "(III)V").setDeobfuscatedName("c");
        right.getOrCreateClassMapping("B$1").setDeobfuscatedName("B$2")
                .getOrCreateMethodMapping("a1", "()LB;").setDeobfuscatedName("a2");
        right.getOrCreateClassMapping("B$1$b").setDeobfuscatedName("B$2$c")
                .getOrCreateMethodMapping("b1", "(LA$1;)V").setDeobfuscatedName("b2");
        MappingSet output = new MappingSet();
        output.getOrCreateClassMapping("A").setDeobfuscatedName("B")
                .getOrCreateFieldMapping("b").setDeobfuscatedName("c").getParent()
                .getOrCreateMethodMapping("b", "(III)V").setDeobfuscatedName("c");
        output.getOrCreateClassMapping("A$1").setDeobfuscatedName("B$2")
                .getOrCreateMethodMapping("a", "()LA;").setDeobfuscatedName("a2");
        output.getOrCreateClassMapping("A$1$a").setDeobfuscatedName("B$2$c")
                .getOrCreateMethodMapping("b", "(LA$1;)V").setDeobfuscatedName("b1").getParent() // Strict merge strategy can't handle this case
                .getOrCreateMethodMapping("b1", "(LA$1;)V").setDeobfuscatedName("b2");
        testCase(left, right, output, MergeConfig.builder()
            .withFieldMergeStrategy(FieldMergeStrategy.STRICT)
            .withMethodMergeStrategy(MethodMergeStrategy.STRICT)
            .build());
    }

    @Test
    public void combinedCaseLoose() {
        MappingSet left = new MappingSet();
        left.getOrCreateClassMapping("A").setDeobfuscatedName("B");
        left.getOrCreateClassMapping("A$1").setDeobfuscatedName("B$1")
                .getOrCreateMethodMapping("a", "()LA;").setDeobfuscatedName("a1");
        left.getOrCreateClassMapping("A$1$a").setDeobfuscatedName("B$1$b")
                .getOrCreateMethodMapping("b", "(LA$1;)V").setDeobfuscatedName("b1");
        MappingSet right = new MappingSet();
        right.getOrCreateClassMapping("B").setDeobfuscatedName("B")
                .getOrCreateFieldMapping("b").setDeobfuscatedName("c").getParent()
                .getOrCreateMethodMapping("b", "(III)V").setDeobfuscatedName("c");
        right.getOrCreateClassMapping("B$1").setDeobfuscatedName("B$2")
                .getOrCreateMethodMapping("a1", "()LB;").setDeobfuscatedName("a2");
        right.getOrCreateClassMapping("B$1$b").setDeobfuscatedName("B$2$c")
                .getOrCreateMethodMapping("b1", "(LA$1;)V").setDeobfuscatedName("b2");
        MappingSet output = new MappingSet();
        output.getOrCreateClassMapping("A").setDeobfuscatedName("B")
                .getOrCreateFieldMapping("b").setDeobfuscatedName("c").getParent()
                .getOrCreateMethodMapping("b", "(III)V").setDeobfuscatedName("c");
        output.getOrCreateClassMapping("A$1").setDeobfuscatedName("B$2")
                .getOrCreateMethodMapping("a", "()LA;").setDeobfuscatedName("a2");
        output.getOrCreateClassMapping("A$1$a").setDeobfuscatedName("B$2$c")
                .getOrCreateMethodMapping("b", "(LA$1;)V").setDeobfuscatedName("b2");
        testCase(left, right, output, MergeConfig.builder()
            .withFieldMergeStrategy(FieldMergeStrategy.LOOSE)
            .withMethodMergeStrategy(MethodMergeStrategy.LOOSE)
            .build());
    }

    @RepeatedTest(value = 5, name = "parallelMergeTest with " + RepeatedTest.CURRENT_REPETITION_PLACEHOLDER + " threads")
    public void parallelMergeTest(final RepetitionInfo info) {
        System.out.println(info.getCurrentRepetition());
        MappingSet left = new MappingSet();
        left.getOrCreateClassMapping("A").setDeobfuscatedName("foo/bar/B");
        left.getOrCreateClassMapping("C").setDeobfuscatedName("bar/baz/D");
        left.getOrCreateClassMapping("E").setDeobfuscatedName("foo/bar/F");
        MappingSet right = new MappingSet();
        right.getOrCreateClassMapping("foo/bar/B").setDeobfuscatedName("foo/bar/B")
                .getOrCreateMethodMapping("a", "(ILbar/baz/D;Lfoo/bar/F;J)V").setDeobfuscatedName("new_a");
        MappingSet output = new MappingSet();
        output.getOrCreateClassMapping("A").setDeobfuscatedName("foo/bar/B")
                .getOrCreateMethodMapping("a", "(ILC;LE;J)V").setDeobfuscatedName("new_a");
        output.getOrCreateClassMapping("C").setDeobfuscatedName("bar/baz/D");
        output.getOrCreateClassMapping("E").setDeobfuscatedName("foo/bar/F");
        testCase(left, right, output, MergeConfig.builder()
            .withParallelism(info.getCurrentRepetition())
            .build());
    }

    private static void testCase(final MappingSet left, final MappingSet right, final MappingSet expected) {
        testCase(left, right, expected, MergeConfig.builder().build());
    }

    private static void testCase(final MappingSet left, final MappingSet right, final MappingSet expected, final MergeConfig config) {
        assertEquals(expected, MappingSetMerger.create(left, right, config).merge());
    }
}
