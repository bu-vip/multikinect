#!/usr/bin/python
"""Use this script to generate the WORKSPACE file with all the necessary
dependencies. This script must be run after dependencies are added to the
"deps.txt" file. Use https://github.com/Dig-Doug/bazel-deps to generate code."""


def bazel_deps_jar():
  command = "java -jar target/bazel-deps-2.0-SNAPSHOT.jar "
  with open("deps.txt") as deps_file:
    for dep in deps_file:
      if len(dep) > 1 and "#" not in dep:
        command += " " + dep.rstrip()
  return command

print(bazel_deps_jar())
