/*
 * Copyright (c) 2007, 2019, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;

import com.sun.org.apache.bcel.internal.Const;

/**
 * This class is derived from <em>Attribute</em> and denotes that this class
 * is an Inner class of another.
 * to the source file of this class.
 * It is instantiated from the <em>Attribute.readAttribute()</em> method.
 *
 * @version $Id: InnerClasses.java 1749603 2016-06-21 20:50:19Z ggregory $
 * @see     Attribute
 */
public final class InnerClasses extends Attribute {

    private InnerClass[] inner_classes;


    /**
     * Initialize from another object. Note that both objects use the same
     * references (shallow copy). Use clone() for a physical copy.
     */
    public InnerClasses(final InnerClasses c) {
        this(c.getNameIndex(), c.getLength(), c.getInnerClasses(), c.getConstantPool());
    }


    /**
     * @param name_index Index in constant pool to CONSTANT_Utf8
     * @param length Content length in bytes
     * @param inner_classes array of inner classes attributes
     * @param constant_pool Array of constants
     */
    public InnerClasses(final int name_index, final int length, final InnerClass[] inner_classes,
            final ConstantPool constant_pool) {
        super(Const.ATTR_INNER_CLASSES, name_index, length, constant_pool);
        this.inner_classes = inner_classes != null ? inner_classes : new InnerClass[0];
    }


    /**
     * Construct object from input stream.
     *
     * @param name_index Index in constant pool to CONSTANT_Utf8
     * @param length Content length in bytes
     * @param input Input stream
     * @param constant_pool Array of constants
     * @throws IOException
     */
    InnerClasses(final int name_index, final int length, final DataInput input, final ConstantPool constant_pool)
            throws IOException {
        this(name_index, length, (InnerClass[]) null, constant_pool);
        final int number_of_classes = input.readUnsignedShort();
        inner_classes = new InnerClass[number_of_classes];
        for (int i = 0; i < number_of_classes; i++) {
            inner_classes[i] = new InnerClass(input);
        }
    }


    /**
     * Called by objects that are traversing the nodes of the tree implicitely
     * defined by the contents of a Java class. I.e., the hierarchy of methods,
     * fields, attributes, etc. spawns a tree of objects.
     *
     * @param v Visitor object
     */
    @Override
    public void accept( final Visitor v ) {
        v.visitInnerClasses(this);
    }


    /**
     * Dump source file attribute to file stream in binary format.
     *
     * @param file Output file stream
     * @throws IOException
     */
    @Override
    public final void dump( final DataOutputStream file ) throws IOException {
        super.dump(file);
        file.writeShort(inner_classes.length);
        for (final InnerClass inner_class : inner_classes) {
            inner_class.dump(file);
        }
    }


    /**
     * @return array of inner class "records"
     */
    public final InnerClass[] getInnerClasses() {
        return inner_classes;
    }


    /**
     * @param inner_classes the array of inner classes
     */
    public final void setInnerClasses( final InnerClass[] inner_classes ) {
        this.inner_classes = inner_classes != null ? inner_classes : new InnerClass[0];
    }


    /**
     * @return String representation.
     */
    @Override
    public final String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append("InnerClasses(");
        buf.append(inner_classes.length);
        buf.append("):\n");
        for (final InnerClass inner_class : inner_classes) {
            buf.append(inner_class.toString(super.getConstantPool())).append("\n");
        }
        return buf.toString();
    }


    /**
     * @return deep copy of this attribute
     */
    @Override
    public Attribute copy( final ConstantPool _constant_pool ) {
        // TODO this could be recoded to use a lower level constructor after creating a copy of the inner classes
        final InnerClasses c = (InnerClasses) clone();
        c.inner_classes = new InnerClass[inner_classes.length];
        for (int i = 0; i < inner_classes.length; i++) {
            c.inner_classes[i] = inner_classes[i].copy();
        }
        c.setConstantPool(_constant_pool);
        return c;
    }
}
