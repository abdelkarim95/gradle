/*
 * Copyright 2018 the original author or authors.
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
package org.gradle.integtests.resolve.alignment

class ForcingPlatformAlignmentTest extends AbstractAlignmentSpec {

    def "can force a virtual platform version by forcing one of its leaves"() {
        repository {
            ['2.7.9', '2.9.4', '2.9.4.1'].each {
                path "databind:$it -> core:$it"
                path "databind:$it -> annotations:$it"
                path "kotlin:$it -> core:$it"
                path "kotlin:$it -> annotations:$it"
            }
        }

        given:
        buildFile << """
            dependencies {
                conf("org:core:2.9.4")
                conf("org:databind:2.7.9") {
                  force = true
                }
                conf("org:kotlin:2.9.4.1")        
            }
        """

        and:
        "a rule which infers module set from group and version"()

        when:
        expectAlignment {
            module('core') tries('2.9.4', '2.9.4.1') alignsTo('2.7.9') byVirtualPlatform()
            module('databind') tries('2.9.4.1') alignsTo('2.7.9') byVirtualPlatform()
            module('kotlin') tries('2.9.4.1') alignsTo('2.7.9') byVirtualPlatform()
            module('annotations') tries('2.9.4.1') alignsTo('2.7.9') byVirtualPlatform()
        }
        run ':checkDeps'

        then:
        resolve.expectGraph {
            root(":", ":test:") {
                edge("org:core:2.9.4", "org:core:2.7.9") {
                    forced()
                }
                module("org:databind:2.7.9") {
                    forced()
                    module('org:annotations:2.7.9')
                    module('org:core:2.7.9')
                }
                edge("org:kotlin:2.9.4.1", "org:kotlin:2.7.9") {
                    forced()
                    module('org:core:2.7.9')
                    module('org:annotations:2.7.9')
                }
            }
        }
    }
}
