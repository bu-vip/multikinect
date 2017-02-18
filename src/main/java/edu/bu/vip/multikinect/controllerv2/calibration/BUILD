package(
    default_visibility = ["//src/test/java/edu/bu/vip/multikinect/controllerv2/calibration:__subpackages__"],
)

java_library(
    name = "manager",
    srcs = [
        "CalibrationManager.java",
        "CalibrationModule.java",
    ],
    visibility = ["//visibility:public"],
    deps = [
        ":calibrationdatadb",
        ":calibrationio",
        ":calibrationstore",
        ":cameratransform",
        ":fragmenter",
        "//:com_google_guava_guava",
        "//:com_google_inject_guice",
        "//:org_ejml_all",
        "//:org_slf4j_slf4j_api",
        "//src/main/java/edu/bu/vip/multikinect/controllerv2/camera:framebus",
        "//src/main/java/edu/bu/vip/multikinect/sync:coordinatetransform",
        "//src/main/java/edu/bu/vip/multikinect/sync:protoutils",
        "//src/main/java/edu/bu/vip/multikinect/util:timestamputils",
        "//src/main/proto:calibration",
        "//src/main/proto:camera_manager",
        "//src/main/proto:frame",
    ],
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
        "//src/main/java/edu/bu/vip/multikinect/controllerv2/camera:framebus",
        "//src/main/proto:calibration",
        "//src/main/proto:camera_manager",
        "//src/main/proto:frame",
    ],
)

java_library(
    name = "calibrationdatadb",
    srcs = [
        "CalibrationDataDB.java",
        "InMemoryCalibrationDataDB.java",
    ],
    deps = [
        "//:com_google_guava_guava",
        "//:com_google_inject_guice",
        "//src/main/proto:frame",
    ],
)

java_library(
    name = "cameratransform",
    srcs = [
        "CameraTransform.java",
    ],
    deps = [
        ":calibrationdatadb",
        "//:com_google_guava_guava",
        "//:com_google_inject_guice",
        "//:org_ejml_all",
        "//:org_slf4j_slf4j_api",
        "//src/main/java/edu/bu/vip/multikinect/sync:coordinatetransform",
        "//src/main/java/edu/bu/vip/multikinect/sync:protoutils",
        "//src/main/proto:calibration",
        "//src/main/proto:camera_manager",
        "//src/main/proto:frame",
    ],
)

java_library(
    name = "calibrationstore",
    srcs = [
        "CalibrationStore.java",
        "InMemoryCalibrationStore.java",
    ],
    visibility = ["//visibility:public"],
    deps = [
        "//:com_google_guava_guava",
        "//:com_google_inject_guice",
        "//src/main/proto:calibration",
    ],
)
