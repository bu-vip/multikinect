java_library(
    name = "coordinatetransform",
    srcs = [
        "CoordinateTransform.java",
    ],
    visibility = ["//visibility:public"],
    deps = [
        ":protoutils",
        "//:com_google_guava_guava",
        "//:org_ejml_all",
        "//src/main/java/edu/bu/vip/multikinect/controller/camera:framereader",
        "//src/main/proto:frame",
    ],
)

java_library(
    name = "protoutils",
    srcs = [
        "FrameUtils.java",
        "SkeletonUtils.java",
    ],
    visibility = ["//visibility:public"],
    deps = [
        "//:com_google_guava_guava",
        "//:org_ejml_all",
        "//src/main/proto:frame",
    ],
)
