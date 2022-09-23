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

package org.cadixdev.lorenz.io.proguard;

import me.jamiemansfield.string.StringReader;
import org.cadixdev.bombe.type.ArrayType;
import org.cadixdev.bombe.type.BaseType;
import org.cadixdev.bombe.type.FieldType;
import org.cadixdev.bombe.type.ObjectType;
import org.cadixdev.bombe.type.Type;
import org.cadixdev.bombe.type.VoidType;

/**
 * A {@link StringReader reader} for {@link Type types} within the ProGuard
 * mapping format.
 *
 * @author Jamie Mansfield
 * @since 0.5.1
 */
public class PGTypeReader extends StringReader {

    public PGTypeReader(final String source) {
        super(source);
    }

    /**
     * Reads a {@link Type type} from the source.
     *
     * @return A type
     */
    public Type readType() {
        if (match("void")) return VoidType.INSTANCE;
        return readFieldType();
    }

    /**
     * Reads a {@link FieldType field type} from the source.
     *
     * @return A field type
     */
    public FieldType readFieldType() {
        while (available() && peek() != '[') {
            advance();
        }
        final FieldType type = getType(substring(0, index()));
        if (!available()) return type;
        int dims = 0;
        while (available()) {
            if (advance() == '[') {
                dims++;
            }
        }
        return new ArrayType(dims, type);
    }

    private boolean match(final String raw) {
        for (int i = 0; i < raw.toCharArray().length; i++) {
            if (raw.toCharArray()[i] != peek(i)) {
                return false;
            }
        }
        return true;
    }

    private static FieldType getType(final String raw) {
        if ("byte".equals(raw)) {
            return BaseType.BYTE;
        } else if ("char".equals(raw)) {
            return BaseType.CHAR;
        } else if ("double".equals(raw)) {
            return BaseType.DOUBLE;
        } else if ("float".equals(raw)) {
            return BaseType.FLOAT;
        } else if ("int".equals(raw)) {
            return BaseType.INT;
        } else if ("long".equals(raw)) {
            return BaseType.LONG;
        } else if ("short".equals(raw)) {
            return BaseType.SHORT;
        } else if ("boolean".equals(raw)) {
            return BaseType.BOOLEAN;
        } else {
            // ObjectType will replace the full stops for forward slashes
            return new ObjectType(raw);
        }
    }

}
