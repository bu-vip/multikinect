#!/usr/bin/python
'''Use this script to generate the WORKSPACE file with all the necessary dependencies. This script must be run after
dependencies are added to the "deps.txt" file'''

import os

script_dir = os.path.dirname(os.path.realpath(__file__))
build_file = script_dir + "/BUILD"
workspace_file = script_dir + "/WORKSPACE"
workspace_start_file = script_dir + "/WORKSPACE_START"
workspace_dep_file = script_dir + "/WORKSPACE_DEP"
workspace_end_file = script_dir + "/WORKSPACE_END"


def bazel_gen_workspace():
  # Remove old files
  command = "/bin/rm " + workspace_file + " " + build_file

  # Dependencies
  command += " && bazel run //src/tools/generate_workspace --"
  with open("deps.txt") as deps_file:
      for dep in deps_file:
          if len(dep) > 1:
              command += " --artifact=" + dep.rstrip()
  # Specify where the generated files should go
  command += " --output_dir=" + script_dir

  # Workspace file generation
  command += " && mv " + workspace_file + " " + workspace_dep_file
  command += " && printf '# THIS FILE IS GENERATED DO NOT MODIFY\n\n' > " + workspace_file
  command += " && cat " + workspace_start_file + " >> " + workspace_file
  command += " && cat " + workspace_dep_file + " >> " + workspace_file
  command += " && cat " + workspace_end_file + " >> " + workspace_file

  # Delete dep file
  command += " && /bin/rm " + workspace_dep_file

  return command

def bazel_deps_jar():
  command = "java -jar target/bazel-deps-2.0-SNAPSHOT.jar "
  with open("deps.txt") as deps_file:
    for dep in deps_file:
      if len(dep) > 1:
        command += " " + dep.rstrip()
  return command


print("gen_workspace", bazel_gen_workspace())
print("\n")
print("bazel-deps", bazel_deps_jar())
