package(
    default_visibility = ["//src/test/java/edu/bu/vip/multikinect/controller/realtime:__subpackages__"],
)

java_library(
    name = "manager",
    srcs = [
        "RealTimeManager.java",
        "RealtimeModule.java",
    ],
    visibility = ["//visibility:public"],
    deps = [
        ":cameragraph",
        ":framecombiner",
        ":syncedframebus",
        "//:com_google_guava_guava",
        "//:com_google_inject_guice",
        "//:org_ejml_all",
        "//:org_slf4j_slf4j_api",
        "//src/main/java/edu/bu/vip/multikinect/controller/camera:framebus",
        "//src/main/java/edu/bu/vip/multikinect/controller/camera:module",
        "//src/main/java/edu/bu/vip/multikinect/controller/data:sessiondatastore",
        "//src/main/java/edu/bu/vip/multikinect/sync:coordinatetransform",
        "//src/main/java/edu/bu/vip/multikinect/sync:protoutils",
        "//src/main/java/edu/bu/vip/multikinect/util:timestamputils",
        "//src/main/proto:calibration",
        "//src/main/proto:camera_manager",
        "//src/main/proto:frame",
        "//src/main/proto:realtime",
    ],
)

java_library(
    name = "syncedframebus",
    srcs = [
        "SyncedFrameBus.java",
    ],
    visibility = ["//visibility:public"],
    deps = [
        "//:com_google_guava_guava",
        "//:com_google_inject_guice",
        "//src/main/proto:realtime",
    ],
)

java_library(
    name = "cameragraph",
    srcs = [
        "CameraGraph.java",
    ],
    deps = [
        "//:com_google_guava_guava",
        "//:com_google_inject_guice",
        "//:org_ejml_all",
        "//:org_jgrapht_jgrapht_core",
        "//src/main/proto:calibration",
    ],
)

java_library(
    name = "framecombiner",
    srcs = [
        "FrameCombiner.java",
    ],
    deps = [
        "//:com_google_guava_guava",
        "//src/main/java/edu/bu/vip/multikinect/sync:protoutils",
        "//src/main/proto:frame",
        "//src/main/proto:realtime",
    ],
)
