package(
    default_visibility = ["//src/main/java/edu/bu/vip/multikinect/controller:__subpackages__"],
)

# TODO(doug) - Split webconsole into mutliple modules
java_library(
    name = "webconsole",
    srcs = glob(["**/*.java"]),
    visibility = ["//visibility:public"],
    exports = [
        "//:io_ratpack_ratpack_guice",
        "//:smartthings_ratpack_protobuf",
    ],
    deps = [
        "@org_pubref_rules_protobuf//java:grpc_compiletime_deps",
        "//:com_google_guava_guava",
        "//:com_google_inject_guice",
        "//:io_ratpack_ratpack_guice",
        "//:org_slf4j_slf4j_api",
        "//:smartthings_ratpack_protobuf",
        "//src/main/java/edu/bu/vip/multikinect/controller",
        "//src/main/java/edu/bu/vip/multikinect/controller/calibration:calibrationdatadb",
        "//src/main/java/edu/bu/vip/multikinect/controller/calibration:manager",
        "//src/main/java/edu/bu/vip/multikinect/controller/realtime:syncedframebus",
        "//src/main/proto:calibration",
        "//src/main/proto:realtime",
        "//src/main/proto:session",
        "//src/main/proto:web_api",
    ],
)
