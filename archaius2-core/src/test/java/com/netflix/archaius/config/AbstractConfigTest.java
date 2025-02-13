/**
 * Copyright 2015 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.archaius.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class AbstractConfigTest {

    private final AbstractConfig config = new AbstractConfig() {
        private final Map<String, Object> entries = new HashMap<>();

        {
            entries.put("foo", "bar");
            entries.put("byte", (byte) 42);
            entries.put("int", 42);
            entries.put("long", 42L);
            entries.put("float", 42.0f);
            entries.put("double", 42.0d);
        }

        @Override
        public boolean containsKey(String key) {
            return entries.containsKey(key);
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public Iterator<String> getKeys() {
            return Collections.unmodifiableSet(entries.keySet()).iterator();
        }

        @Override
        public Object getRawProperty(String key) {
            return entries.get(key);
        }

        @Override
        public void forEachProperty(BiConsumer<String, Object> consumer) {
            entries.forEach(consumer);
        }
    };

    @Test
    public void testGet() {
        assertEquals("bar", config.get(String.class, "foo"));
    }
    
    @Test
    public void getExistingProperty() {
        //noinspection OptionalGetWithoutIsPresent
        assertEquals("bar", config.getProperty("foo").get());
    }
    
    @Test
    public void getNonExistentProperty() {
        assertFalse(config.getProperty("non_existent").isPresent());
    }

    @Test
    public void testGetRawNumerics() {
        // First, get each entry as its expected type and the corresponding wrapper.
        assertEquals(42, config.get(int.class, "int"));
        assertEquals(42, config.get(Integer.class, "int"));
        assertEquals(42L, config.get(long.class, "long"));
        assertEquals(42L, config.get(Long.class, "long"));
        assertEquals((byte) 42, config.get(byte.class, "byte"));
        assertEquals((byte) 42, config.get(Byte.class, "byte"));
        assertEquals(42.0f, config.get(float.class, "float"));
        assertEquals(42.0f, config.get(Float.class, "float"));
        assertEquals(42.0d, config.get(double.class, "double"));
        assertEquals(42.0d, config.get(Double.class, "double"));

        // Then, get each entry as a string
        assertEquals("42", config.get(String.class, "int"));
        assertEquals("42", config.get(String.class, "long"));
        assertEquals("42", config.get(String.class, "byte"));
        assertEquals("42.0", config.get(String.class, "float"));
        assertEquals("42.0", config.get(String.class, "double"));

        // Then, narrowed types
        assertEquals((byte) 42, config.get(byte.class, "int"));
        assertEquals((byte) 42, config.get(byte.class, "long"));
        assertEquals((byte) 42, config.get(byte.class, "float"));
        assertEquals((byte) 42, config.get(byte.class, "double"));
        assertEquals(42.0f, config.get(double.class, "double"));

        // Then, widened
        assertEquals(42L, config.get(long.class, "int"));
        assertEquals(42L, config.get(long.class, "byte"));
        assertEquals(42L, config.get(long.class, "float"));
        assertEquals(42L, config.get(long.class, "double"));
        assertEquals(42.0d, config.get(double.class, "float"));

        // On floating point
        assertEquals(42.0f, config.get(float.class, "int"));
        assertEquals(42.0f, config.get(float.class, "byte"));
        assertEquals(42.0f, config.get(float.class, "long"));
        assertEquals(42.0f, config.get(float.class, "double"));

        // As doubles
        assertEquals(42.0d, config.get(double.class, "int"));
        assertEquals(42.0d, config.get(double.class, "byte"));
        assertEquals(42.0d, config.get(double.class, "long"));
        assertEquals(42.0d, config.get(double.class, "float"));

        // Narrowed types in wrapper classes
        assertEquals((byte) 42, config.get(Byte.class, "int"));
        assertEquals((byte) 42, config.get(Byte.class, "long"));
        assertEquals((byte) 42, config.get(Byte.class, "float"));

        // Widened types in wrappers
        assertEquals(42L, config.get(Long.class, "int"));
        assertEquals(42L, config.get(Long.class, "byte"));
    }
}
