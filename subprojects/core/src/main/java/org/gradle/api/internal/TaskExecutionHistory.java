/*
 * Copyright 2010 the original author or authors.
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
package org.gradle.api.internal;

import org.gradle.api.Describable;
import org.gradle.api.Nullable;
import org.gradle.api.file.FileCollection;

public interface TaskExecutionHistory {
    /**
     * Returns the set of output files which the task produced.
     */
    FileCollection getOutputFiles();

    /**
     * Returns if overlapping outputs were detected
     */
    @Nullable
    OverlapOutputDetection getOverlappingOutputDetection();

    class OverlapOutputDetection implements Describable {
        private final String propertyName;
        private final String overlappedFilePath;

        public OverlapOutputDetection(String propertyName, String overlappedFilePath) {
            this.propertyName = propertyName;
            this.overlappedFilePath = overlappedFilePath;
        }

        public String getDisplayName() {
            return String.format("property '%s' with path '%s'", propertyName, overlappedFilePath);
        }
    }
}
