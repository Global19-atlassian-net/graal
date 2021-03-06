/*
 * Copyright (c) 2020, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package org.graalvm.compiler.truffle.runtime;

import java.util.HashMap;
import java.util.Map;

import org.graalvm.compiler.truffle.runtime.TruffleInlining.TruffleSourceLanguagePosition;

import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.SourceSection;

/**
 * A set of node source positions that keeps track of the node id per compilation.
 */
final class TruffleNodeSources {

    private final Map<Node, TruffleSourceLanguagePosition> sourcePositionCache = new HashMap<>();
    private int nodeIdCounter;

    TruffleSourceLanguagePosition getSourceLocation(Node truffleNode) {
        TruffleSourceLanguagePosition position = sourcePositionCache.get(truffleNode);
        if (position != null) {
            return position;
        }
        SourceSection section = null;
        if (truffleNode instanceof DirectCallNode) {
            section = ((DirectCallNode) truffleNode).getCurrentRootNode().getSourceSection();
        }
        if (section == null) {
            section = truffleNode.getSourceSection();
        }
        if (section == null) {
            Node cur = truffleNode.getParent();
            while (cur != null) {
                section = cur.getSourceSection();
                if (section != null) {
                    break;
                }
                cur = cur.getParent();
            }
        }
        position = new TruffleSourceLanguagePosition(section, truffleNode.getClass(), nodeIdCounter++);
        sourcePositionCache.put(truffleNode, position);
        return position;
    }
}
