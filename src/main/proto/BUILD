load("@org_pubref_rules_protobuf//java:rules.bzl", "java_proto_library")

package(
    default_visibility = ["//visibility:public"],
)

java_proto_library(
    name = "calibration",
    proto_deps = [
        ":frame",
        "//thirdparty/protobuf/google/protobuf:well_known_types",
    ],
    protos = [
        "calibration.proto",
    ],
)

java_proto_library(
    name = "skeletonfeed",
    proto_deps = [
        ":frame",
    ],
    protos = ["skeleton_feed.proto"],
)

java_proto_library(
    name = "frame",
    protos = ["frame.proto"],
)

java_proto_library(
    name = "camera",
    proto_deps = [":frame"],
    protos = ["camera.proto"],
    with_grpc = True,
)

java_proto_library(
    name = "camera_manager",
    protos = ["camera_manager.proto"],
    with_grpc = True,
)
