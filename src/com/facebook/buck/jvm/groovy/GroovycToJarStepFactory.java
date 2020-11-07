/*
 * Copyright (c) Facebook, Inc. and its affiliates.
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

package com.facebook.buck.jvm.groovy;

import com.facebook.buck.core.build.buildable.context.BuildableContext;
import com.facebook.buck.core.build.context.BuildContext;
import com.facebook.buck.core.filesystems.RelPath;
import com.facebook.buck.core.model.BuildTarget;
import com.facebook.buck.core.rulekey.AddToRuleKey;
import com.facebook.buck.core.rulekey.AddsToRuleKey;
import com.facebook.buck.core.sourcepath.resolver.SourcePathResolverAdapter;
import com.facebook.buck.core.toolchain.tool.Tool;
import com.facebook.buck.io.filesystem.ProjectFilesystem;
import com.facebook.buck.jvm.java.CompileToJarStepFactory;
import com.facebook.buck.jvm.java.CompilerParameters;
import com.facebook.buck.jvm.java.JavacOptions;
import com.facebook.buck.jvm.java.ResolvedJavacOptions;
import com.facebook.buck.step.Step;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import java.nio.file.Path;
import java.util.Optional;

/** Factory that creates Groovy related compile build steps. */
class GroovycToJarStepFactory extends CompileToJarStepFactory implements AddsToRuleKey {

  @AddToRuleKey private final Tool groovyc;
  @AddToRuleKey private final Optional<ImmutableList<String>> extraArguments;
  @AddToRuleKey private final JavacOptions javacOptions;
  @AddToRuleKey private final boolean withDownwardApi;

  public GroovycToJarStepFactory(
      Tool groovyc,
      Optional<ImmutableList<String>> extraArguments,
      JavacOptions javacOptions,
      boolean withDownwardApi) {
    this.groovyc = groovyc;
    this.extraArguments = extraArguments;
    this.javacOptions = javacOptions;
    this.withDownwardApi = withDownwardApi;
  }

  @Override
  protected boolean areAllStepsConvertedToIsolatedSteps() {
    return true;
  }

  @Override
  public void createCompileStep(
      BuildContext buildContext,
      ProjectFilesystem projectFilesystem,
      ImmutableMap<String, RelPath> cellToPathMappings,
      BuildTarget invokingRule,
      CompilerParameters parameters,
      /* output params */
      Builder<Step> steps,
      BuildableContext buildableContext) {

    ImmutableSortedSet<Path> declaredClasspathEntries = parameters.getClasspathEntries();
    ImmutableSortedSet<Path> sourceFilePaths = parameters.getSourceFilePaths();
    RelPath outputDirectory = parameters.getOutputPaths().getClassesDir();
    Path pathToSrcsList = parameters.getOutputPaths().getPathToSourcesList();

    SourcePathResolverAdapter sourcePathResolver = buildContext.getSourcePathResolver();
    ImmutableList<String> commandPrefix = groovyc.getCommandPrefix(sourcePathResolver);

    steps.add(
        new GroovycStep(
            commandPrefix,
            extraArguments,
            ResolvedJavacOptions.of(
                javacOptions, sourcePathResolver, projectFilesystem.getRootPath()),
            outputDirectory.getPath(),
            sourceFilePaths,
            pathToSrcsList,
            declaredClasspathEntries,
            withDownwardApi));
  }

  @Override
  public boolean hasAnnotationProcessing() {
    return !javacOptions.getJavaAnnotationProcessorParams().isEmpty();
  }
}
