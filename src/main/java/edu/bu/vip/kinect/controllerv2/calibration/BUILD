package(
    default_visibility = ["//src/test/java/edu/bu/vip/kinect/controllerv2/calibration:__subpackages__"],
)

java_library(
    name = "calibrationio",
    srcs = [
        "CalibrationDataLocation.java",
        "CalibrationLoader.java",
    ],
    visibility = ["//visibility:public"],
    deps = [
        "//:com_google_guava_guava",
        "//:com_google_inject_guice",
        "//:org_slf4j_slf4j_api",
        "//src/main/proto:calibration",
    ],
)

java_library(
    name = "fragmenter",
    srcs = [
        "Fragmenter.java",
    ],
    deps = [
        ":calibrationdatadb",
        "//:com_google_guava_guava",
        "//:com_google_inject_guice",
        "//:org_slf4j_slf4j_api",
        "//src/main/java/edu/bu/vip/kinect/controllerv2/camera:framebus",
        "//src/main/proto:calibration",
        "//src/main/proto:camera_manager",
        "//src/main/proto:frame",
    ],
)

java_library(
    name = "calibrationdatadb",
    srcs = [
        "CalibrationDataDB.java",
    ],
    deps = [
        "//:com_google_guava_guava",
        "//src/main/proto:frame",
    ],
)

java_library(
    name = "inmemorycalibrationdatadb",
    srcs = [
        "InMemoryCalibrationDataDB.java",
    ],
    deps = [
        ":calibrationdatadb",
        "//:com_google_guava_guava",
        "//:com_google_inject_guice",
        "//src/main/proto:frame",
    ],
)
