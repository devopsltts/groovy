/*
 * Copyright 2003-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.groovy.classgen.asm.indy.sc;

import org.codehaus.groovy.classgen.asm.BinaryExpressionWriter;
import org.codehaus.groovy.classgen.asm.MethodCaller;
import org.codehaus.groovy.classgen.asm.WriterController;
import org.codehaus.groovy.classgen.asm.sc.StaticTypesBinaryExpressionMultiTypeDispatcher;
import org.codehaus.groovy.vmplugin.v7.IndyInterface;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 * Multi type dispatcher for binary expression backend combining indy and static compilation
 * @author Jochen Theodorou
 * @since 2.5.0
 */
public class IndyStaticTypesMultiTypeDispatcher extends StaticTypesBinaryExpressionMultiTypeDispatcher {
    public IndyStaticTypesMultiTypeDispatcher(WriterController wc) {
        super(wc);

    }

    private static final String INDY_INTERFACE_NAME = IndyInterface.class.getName().replace('.', '/');
    private static final String BSM_METHOD_TYPE_DESCRIPTOR =
            MethodType.methodType(
                    CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class
            ).toMethodDescriptorString();
    private static final Handle BSM =
            new Handle(
                    H_INVOKESTATIC,
                    INDY_INTERFACE_NAME,
                    "staticArrayAccess",
                    BSM_METHOD_TYPE_DESCRIPTOR);
    private static class GenericArrayAccess extends MethodCaller {
        private final String name, signature;
        public GenericArrayAccess(String name, String signature) {
            this.name = name;
            this.signature = signature;
        }
        @Override public void call(MethodVisitor mv) {
            mv.visitInvokeDynamicInsn(name, signature, BSM);
        }
    }

    protected BinaryExpressionWriter[] initializeDelegateHelpers() {
        BinaryExpressionWriter[] bewArray = super.initializeDelegateHelpers();
        /* 1: int    */
        bewArray[1].setArraySetAndGet(  new GenericArrayAccess("set","([III)V"),
                                        new GenericArrayAccess("get","([II)I"));
        /* 2: long   */
        bewArray[2].setArraySetAndGet(  new GenericArrayAccess("set","([LLL)V"),
                                        new GenericArrayAccess("get","([LL)L"));
        /* 3: double */
        bewArray[3].setArraySetAndGet(  new GenericArrayAccess("set","([DDD)V"),
                                        new GenericArrayAccess("get","([DD)D"));
        /* 4: char   */
        bewArray[4].setArraySetAndGet(  new GenericArrayAccess("set","([CCC)V"),
                                        new GenericArrayAccess("get","([CC)C"));
        /* 5: byte   */
        bewArray[5].setArraySetAndGet(  new GenericArrayAccess("set","([BBB)V"),
                                        new GenericArrayAccess("get","([BB)B"));
        /* 6: short  */
        bewArray[6].setArraySetAndGet(  new GenericArrayAccess("set","([SSS)V"),
                                        new GenericArrayAccess("get","([SS)S"));
        /* 7: float  */
        bewArray[7].setArraySetAndGet(  new GenericArrayAccess("get","([FFF)V"),
                                        new GenericArrayAccess("set","([FF)F"));
        /* 8: bool   */
        bewArray[8].setArraySetAndGet(  new GenericArrayAccess("get","([ZZZ)V"),
                                        new GenericArrayAccess("set","([ZZ)Z"));
        return bewArray;
    }
}
