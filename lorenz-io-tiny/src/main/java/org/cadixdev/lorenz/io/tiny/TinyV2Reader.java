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

package org.cadixdev.lorenz.io.tiny;

import org.cadixdev.bombe.type.FieldType;
import org.cadixdev.bombe.type.MethodDescriptor;
import org.cadixdev.bombe.type.signature.FieldSignature;
import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.io.MappingsReader;
import org.cadixdev.lorenz.io.TextMappingsReader;
import org.cadixdev.lorenz.model.ClassMapping;
import org.cadixdev.lorenz.model.Mapping;
import org.cadixdev.lorenz.model.MethodMapping;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

/**
 * An implementation of {@link MappingsReader} for the Tiny V2 format.
 *
 * @author Bleach
 * @since 1.0.0
 */
public class TinyV2Reader extends TextMappingsReader {

    protected final Deque<Mapping<?, ?>> stack = new ArrayDeque<>();

    /**
     * The column to read the input mappings from.
     *
     * The standard Fabric columns are {@code official}, {@code intermediary} and {@code named}
     */
    protected String from;
    protected int fromIndex;
    /**
     * The column to read the output mappings from.
     *
     * The standard Fabric columns are {@code official}, {@code intermediary} and {@code named}
     */
    protected String to;
    protected int toIndex;

    public TinyV2Reader(final Reader reader) {
        super(reader);
    }

    public TinyV2Reader withFormats(String from, String to) {
        this.from = from;
        this.to = to;
        return this;
    }

    @Override
    public MappingSet read(final MappingSet mappings) throws IOException {
        if (from == null || to == null) {
            throw new IllegalStateException("Format names not set. call withFormats() before reading!");
        }

        String header = reader.readLine();
        if (!header.startsWith("tiny\t2")) {
            throw new IllegalArgumentException("Faulty Tiny V2 mapping header!");
        }

        List<String> split = Arrays.asList(header.split("\t"));
        fromIndex = split.indexOf(from) - 3;
        toIndex = split.indexOf(to) - 3;

        reader.lines().forEach(line -> readLine(mappings, line));
        return mappings;
    }

    @Override
    public void readLine(final MappingSet mappings, final String line) {
        if (line.isEmpty()) return;

        final int indentLevel = getIndentLevel(line);
        while (indentLevel < stack.size()) {
            stack.pop();
        }

        // Split up the line, for further processing
        final String[] split = TAB.split(line.trim());

        // Establish the type of mapping
        final String key = split[0];
        if (key.equals("c") && indentLevel == 0) {
            // Class
            final String obfName = split[1 + fromIndex];
            final String deobfName = split[1 + toIndex];
            stack.push(mappings.getOrCreateClassMapping(obfName)
                            .setDeobfuscatedName(deobfName));
        } else if (key.equals("f")) {
            // Field
            final String obfName = split[2 + fromIndex];
            final String deobfName = split[2 + toIndex];
            final FieldSignature type = new FieldSignature(obfName, split[1].isEmpty() ? null : FieldType.of(split[1]));
            stack.push(peekClass().getOrCreateFieldMapping(type)
                    .setDeobfuscatedName(deobfName));
        } else if (key.equals("m")) {
            // Method
            final MethodDescriptor type = MethodDescriptor.of(split[1]);
            final String obfName = split[2 + fromIndex];
            final String deobfName = split[2 + toIndex];
            stack.push(peekClass().getOrCreateMethodMapping(obfName, type)
                    .setDeobfuscatedName(deobfName));
        } else if (key.equals("p")) {
            // Parameter
            final int index = Integer.parseInt(split[1]);
            final String deobfName = split[2 + toIndex];
            stack.push(peekMethod().getOrCreateParameterMapping(index)
                    .setDeobfuscatedName(deobfName));
        } else if (key.equals("c")) {
            // Comment
            stack.peek().getJavadoc().addAll(getComment(split));
        }
    }

    protected ClassMapping<?, ?> peekClass() {
        if (!(stack.peek() instanceof ClassMapping)) throw new UnsupportedOperationException("Not a class on the stack!");
        return (ClassMapping<?, ?>) stack.peek();
    }

    protected MethodMapping peekMethod() {
        if (!(stack.peek() instanceof MethodMapping)) throw new UnsupportedOperationException("Not a method on the stack!");
        return (MethodMapping) stack.peek();
    }

    private static int getIndentLevel(final String line) {
        int indentLevel = 0;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) != '\t') break;
            indentLevel++;
        }
        return indentLevel;
    }

    private static List<String> getComment(String[] split) {
        String[] commentSplit = new String[split.length - 1];
        System.arraycopy(split, 1, commentSplit, 0, commentSplit.length);

        String comment = String.join("\t", commentSplit);
        return Arrays.asList(comment.split("\n"));
    }
}
