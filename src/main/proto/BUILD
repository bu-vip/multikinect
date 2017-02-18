load("@org_pubref_rules_protobuf//java:rules.bzl", "java_proto_library")
load("@org_pubref_rules_protobuf//csharp:rules.bzl", "csharp_proto_library")

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
    proto_deps = [
        "//thirdparty/protobuf/google/protobuf:well_known_types",
    ],
    protos = ["frame.proto"],
)

java_proto_library(
    name = "camera",
    proto_deps = [":frame"],
    protos = ["camera.proto"],
    with_grpc = True,
)

#csharp_proto_library(
#    name = "camera_csharp",
#    proto_deps = [
#        ":frame",
#    ],
#    protos = [
#        "camera.proto",
#    ],
#    with_grpc = True,
#)

java_proto_library(
    name = "camera_manager",
    protos = ["camera_manager.proto"],
    with_grpc = True,
)

java_proto_library(
    name = "web_api",
    proto_deps = [
        ":calibration",
        ":session",
        "//thirdparty/protobuf/google/protobuf:well_known_types",
    ],
    protos = [
        "web_api.proto",
    ],
)

java_proto_library(
    name = "session",
    proto_deps = [
        "//thirdparty/protobuf/google/protobuf:well_known_types",
    ],
    protos = [
        "session.proto",
    ],
)
