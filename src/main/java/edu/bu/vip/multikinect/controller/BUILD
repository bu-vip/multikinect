package(
    default_visibility = ["//src/main/java/edu/bu/vip/multikinect/controller:__subpackages__"],
)

java_binary(
    name = "main",
    srcs = [
        "Main.java",
    ],
    main_class = "edu.bu.vip.multikinect.controller.Main",
    resources = [
        "//src/main/resources:log4jprops",
    ],
    runtime_deps = [
        "@org_pubref_rules_protobuf//java:netty_runtime_deps",
    ],
    deps = [
        ":controller",
        "//:com_google_guava_guava",
        "//:com_google_inject_guice",
        "//:org_slf4j_slf4j_api",
        "//:org_slf4j_slf4j_log4j12",
        "//src/main/java/edu/bu/vip/multikinect/controller/calibration:calibrationdatadb",
        "//src/main/java/edu/bu/vip/multikinect/controller/calibration:manager",
        "//src/main/java/edu/bu/vip/multikinect/controller/camera:module",
        "//src/main/java/edu/bu/vip/multikinect/controller/data:sessiondatastore",
        "//src/main/java/edu/bu/vip/multikinect/controller/realtime:manager",
        "//src/main/java/edu/bu/vip/multikinect/controller/webconsole",
    ],
)

java_library(
    name = "controller",
    srcs = [
        "Controller.java",
    ],
    deps = [
        "@org_pubref_rules_protobuf//java:grpc_compiletime_deps",
        "//:com_google_guava_guava",
        "//:com_google_inject_guice",
        "//:com_google_protobuf_protobuf_java",
        "//:com_google_protobuf_protobuf_java_util",
        "//:org_ejml_all",
        "//:org_slf4j_slf4j_api",
        "//src/main/java/edu/bu/vip/multikinect/controller/calibration:bruteforcecalibrationalgorithm",
        "//src/main/java/edu/bu/vip/multikinect/controller/calibration:calibrationdatadb",
        "//src/main/java/edu/bu/vip/multikinect/controller/calibration:manager",
        "//src/main/java/edu/bu/vip/multikinect/controller/camera:module",
        "//src/main/java/edu/bu/vip/multikinect/controller/data:sessiondatastore",
        "//src/main/java/edu/bu/vip/multikinect/controller/plugin",
        "//src/main/java/edu/bu/vip/multikinect/controller/realtime:manager",
        "//src/main/java/edu/bu/vip/multikinect/controller/realtime:syncedframebus",
        "//src/main/java/edu/bu/vip/multikinect/sync:coordinatetransform",
        "//src/main/java/edu/bu/vip/multikinect/sync:protoutils",
        "//src/main/java/edu/bu/vip/multikinect/util:timestamputils",
        "//src/main/proto:calibration",
        "//src/main/proto:camera",
        "//src/main/proto:camera_manager",
        "//src/main/proto:frame",
        "//src/main/proto:realtime",
        "//src/main/proto:session",
        "//src/main/proto:skeletonfeed",
    ],
)
