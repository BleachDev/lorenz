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

package org.cadixdev.lorenz.dsl.groovy.test

import org.cadixdev.lorenz.MappingSet
import org.cadixdev.lorenz.dsl.groovy.MappingSetDsl
import org.cadixdev.lorenz.model.*
import spock.lang.Specification

class MappingSetDslSpec extends Specification {

    def "creates mapping set"() {
        given:
        final MappingSet mappings = MappingSetDsl.create {
            klass('a') {
                deobf = 'Example'
                field('c') {
                    deobf = 'name'
                }
                method('d', '(Z)Ljava/lang/String;') {
                    deobf = 'getName'
                    param(0) {
                        deobf = 'propagate'
                    }
                }
            }
            klass('b') {
                deobf = 'Demo'
                klass('e') {
                    deobf = 'Inner'
                }
            }
        }

        expect:
        mappings.topLevelClassMappings.size() == 2

        // a.class
        final TopLevelClassMapping a = mappings.topLevelClassMappings[0]
        a.obfuscatedName == 'a'
        a.deobfuscatedName == 'Example'
        a.fieldMappings.size() == 1
        final FieldMapping c = a.fieldMappings[0]
        c.obfuscatedName == 'c'
        c.deobfuscatedName == 'name'
        !c.type.isPresent()
        a.methodMappings.size() == 1
        final MethodMapping d = a.methodMappings[0]
        d.obfuscatedName == 'd'
        d.deobfuscatedName == 'getName'
        d.parameterMappings.size() == 1
        final MethodParameterMapping d0 = d.parameterMappings[0]
        d0.index == 0
        d0.deobfuscatedName == 'propagate'

        // b.class
        final TopLevelClassMapping b = mappings.topLevelClassMappings[1]
        b.obfuscatedName == 'b'
        b.deobfuscatedName == 'Demo'
        // b$e.class
        b.innerClassMappings.size() == 1
        final InnerClassMapping b$e = b.innerClassMappings[0]
        b$e.obfuscatedName == 'e'
        b$e.deobfuscatedName == 'Inner'
    }

}
