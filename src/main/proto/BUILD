load("@org_pubref_rules_protobuf//java:rules.bzl", "java_proto_library")
load("@org_pubref_rules_protobuf//python:rules.bzl", "py_proto_compile")

package(
    default_visibility = ["//visibility:public"],
)

java_proto_library(
    name = "calibration",
    imports = [
        "external/com_github_google_protobuf/src/",
    ],
    inputs = [
        "@com_github_google_protobuf//:well_known_protos",
    ],
    proto_deps = [
        ":frame",
    ],
    protos = [
        "calibration.proto",
    ],
)

java_proto_library(
    name = "camera",
    imports = [
        "external/com_github_google_protobuf/src/",
    ],
    inputs = [
        "@com_github_google_protobuf//:well_known_protos",
    ],
    proto_deps = [
        ":frame",
    ],
    protos = ["camera.proto"],
    with_grpc = True,
)

java_proto_library(
    name = "camera_manager",
    protos = ["camera_manager.proto"],
    with_grpc = True,
)

java_proto_library(
    name = "frame",
    imports = [
        "external/com_github_google_protobuf/src/",
    ],
    inputs = [
        "@com_github_google_protobuf//:well_known_protos",
    ],
    protos = ["frame.proto"],
)

py_proto_compile(
    name = "frame_python",
    imports = [
        "external/com_github_google_protobuf/src/",
    ],
    inputs = [
        "@com_github_google_protobuf//:well_known_protos",
    ],
    protos = [
        "frame.proto",
    ],
)

java_proto_library(
    name = "realtime",
    imports = [
        "external/com_github_google_protobuf/src/",
    ],
    inputs = [
        "@com_github_google_protobuf//:well_known_protos",
    ],
    proto_deps = [
        ":frame",
    ],
    protos = [
        "realtime.proto",
    ],
)

py_proto_compile(
    name = "realtime_python",
    imports = [
        "external/com_github_google_protobuf/src/",
    ],
    inputs = [
        "@com_github_google_protobuf//:well_known_protos",
    ],
    protos = [
        "realtime.proto",
    ],
    deps = [
        ":frame_python",
    ],
)

java_proto_library(
    name = "session",
    imports = [
        "external/com_github_google_protobuf/src/",
    ],
    inputs = [
        "@com_github_google_protobuf//:well_known_protos",
    ],
    protos = [
        "session.proto",
    ],
)

py_proto_compile(
    name = "session_python",
    imports = [
        "external/com_github_google_protobuf/src/",
    ],
    inputs = [
        "@com_github_google_protobuf//:well_known_protos",
    ],
    protos = [
        "session.proto",
    ],
)

java_proto_library(
    name = "skeletonfeed",
    imports = [
        "external/com_github_google_protobuf/src/",
    ],
    inputs = [
        "@com_github_google_protobuf//:well_known_protos",
    ],
    proto_deps = [
        ":frame",
    ],
    protos = ["skeleton_feed.proto"],
)

java_proto_library(
    name = "web_api",
    imports = [
        "external/com_github_google_protobuf/src/",
    ],
    inputs = [
        "@com_github_google_protobuf//:well_known_protos",
    ],
    proto_deps = [
        ":calibration",
        ":session",
    ],
    protos = [
        "web_api.proto",
    ],
)
