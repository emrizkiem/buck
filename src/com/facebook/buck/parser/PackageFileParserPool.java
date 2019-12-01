/*
 * Copyright 2019-present Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.facebook.buck.parser;

import com.facebook.buck.core.cell.Cell;
import com.facebook.buck.parser.api.PackageFileManifest;

/** Parser pool for {@link PackageFileManifest}s. */
class PackageFileParserPool extends FileParserPool<PackageFileManifest> {
  /** @param maxParsersPerCell maximum number of parsers to create for a single cell. */
  public PackageFileParserPool(
      int maxParsersPerCell, PackageFileParserFactory packageFileParserFactory) {
    super(maxParsersPerCell, packageFileParserFactory);
  }

  @Override
  void reportProfile() {
    // do nothing
  }

  @Override
  boolean shouldUsePoolForCell(Cell cell) {
    // PACKAGE files are always parsed with the Skylark parser.
    return false;
  }
}
