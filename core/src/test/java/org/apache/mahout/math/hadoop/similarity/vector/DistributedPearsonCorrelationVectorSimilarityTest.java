/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.mahout.math.hadoop.similarity.vector;

/**
 * tests {@link DistributedPearsonCorrelationVectorSimilarity}
 */
public class DistributedPearsonCorrelationVectorSimilarityTest
    extends DistributedVectorSimilarityTestCase {

  public void testPearsonCorrelation() throws Exception {
    assertSimilar(new DistributedPearsonCorrelationVectorSimilarity(),
        asVector(3, -2),
        asVector(3, -2), 2, 1.0);

    assertSimilar(new DistributedPearsonCorrelationVectorSimilarity(),
        asVector(3, 3),
        asVector(3, 3), 2, Double.NaN);

    assertSimilar(new DistributedPearsonCorrelationVectorSimilarity(),
        asVector(0, 3),
        asVector(3, 0), 2, Double.NaN);
  }

}
