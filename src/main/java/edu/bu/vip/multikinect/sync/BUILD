java_library(
    name = "framereader",
    srcs = [
        "FrameDataSource.java",
        "FrameReader.java",
    ],
    visibility = ["//visibility:public"],
    deps = [
        "//:com_google_guava_guava",
        "//src/main/proto:frame",
    ],
)

java_library(
    name = "coordinatetransform",
    srcs = [
        "CoordinateTransform.java",
    ],
    visibility = ["//visibility:public"],
    deps = [
        ":framereader",
        ":protoutils",
        "//:org_ejml_all",
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

java_library(
    name = "calibration",
    srcs = [
        "Calibration.java",
    ],
    visibility = ["//visibility:public"],
    deps = [
        ":coordinatetransform",
        ":framereader",
        ":protoutils",
        "//:com_google_guava_guava",
        "//:org_ejml_all",
        "//src/main/proto:frame",
    ],
)
