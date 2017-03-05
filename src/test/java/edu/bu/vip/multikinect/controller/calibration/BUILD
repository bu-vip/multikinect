java_test(
    name = "CameraTransformTest",
    srcs = ["CameraTransformTest.java"],
    resources = [
        "//src/test/resources:kinectdata",
    ],
    deps = [
        "//:com_google_guava_guava",
        "//:junit_junit",
        "//:org_ejml_all",
        "//:org_hamcrest_hamcrest_junit",
        "//src/main/java/edu/bu/vip/multikinect/controller/calibration:calibrationdatadb",
        "//src/main/java/edu/bu/vip/multikinect/controller/calibration:cameratransform",
        "//src/main/java/edu/bu/vip/multikinect/controller/camera:framereader",
        "//src/main/proto:calibration",
        "//src/main/proto:camera_manager",
        "//src/main/proto:frame",
        "//src/test/java/edu/bu/vip/multikinect/utils:matchers",
    ],
)

java_test(
    name = "CameraManagerIntegrationTest",
    size = "medium",
    srcs = [
        "CameraManagerIntegrationTest.java",
    ],
    resources = [
        "//src/test/resources:kinectdata",
    ],
    deps = [
        "//:com_google_guava_guava",
        "//:com_google_inject_guice",
        "//:junit_junit",
        "//:org_ejml_all",
        "//:org_hamcrest_hamcrest_junit",
        "//src/main/java/edu/bu/vip/multikinect/controller/calibration:calibrationdatadb",
        "//src/main/java/edu/bu/vip/multikinect/controller/calibration:manager",
        "//src/main/java/edu/bu/vip/multikinect/controller/camera:fileframepublisher",
        "//src/main/java/edu/bu/vip/multikinect/controller/camera:framebus",
        "//src/main/proto:calibration",
        "//src/main/proto:camera_manager",
        "//src/main/proto:frame",
        "//src/test/java/edu/bu/vip/multikinect/utils:matchers",
    ],
)

java_test(
    name = "BruteForceCalibrationAlgorithmTest",
    srcs = [
        "BruteForceCalibrationAlgorithmTest.java",
    ],
    deps = [
        ":CalbrationAlgorithmTest",
        "//src/main/java/edu/bu/vip/multikinect/controller/calibration:bruteforcecalibrationalgorithm",
        "//src/main/java/edu/bu/vip/multikinect/controller/calibration:calibrationalgorithm",
        "//src/main/proto:calibration",
        "//src/main/proto:camera_manager",
        "//src/main/proto:frame",
    ],
)

java_library(
    name = "CalbrationAlgorithmTest",
    srcs = [
        "CalibrationAlgorithmTest.java",
    ],
    resources = [
        "//src/main/resources:log4jprops",
        "//src/test/resources:calibration",
        "//src/test/resources:kinectdata",
    ],
    deps = [
        "//:com_google_guava_guava",
        "//:com_google_inject_guice",
        "//:junit_junit",
        "//:org_hamcrest_hamcrest_junit",
        "//:org_slf4j_slf4j_log4j12",
        "//src/main/java/edu/bu/vip/multikinect/controller/calibration:calibrationalgorithm",
        "//src/main/java/edu/bu/vip/multikinect/controller/calibration:calibrationdatadb",
        "//src/main/java/edu/bu/vip/multikinect/controller/camera:framereader",
        "//src/main/proto:calibration",
        "//src/main/proto:camera_manager",
        "//src/main/proto:frame",
    ],
)

java_library(
    name = "CalibrationDataStoreTest",
    srcs = [
        "CalibrationDataStoreTest.java",
    ],
    deps = [
        "//:com_google_guava_guava",
        "//:junit_junit",
        "//:org_hamcrest_hamcrest_junit",
        "//:org_slf4j_slf4j_log4j12",
        "//src/main/java/edu/bu/vip/multikinect/controller/calibration:calibrationdatadb",
        "//src/main/proto:calibration",
        "//src/main/proto:frame",
    ],
)

java_test(
    name = "InMemoryCalibrationDataStoreTest",
    srcs = [
        "InMemoryCalibrationDataStoreTest.java",
    ],
    deps = [
        ":CalibrationDataStoreTest",
        "//:com_google_guava_guava",
        "//:junit_junit",
        "//:org_hamcrest_hamcrest_junit",
        "//:org_slf4j_slf4j_log4j12",
        "//src/main/java/edu/bu/vip/multikinect/controller/calibration:calibrationdatadb",
        "//src/main/proto:calibration",
        "//src/main/proto:frame",
    ],
)

java_test(
    name = "FileCalibrationDataStoreTest",
    srcs = [
        "FileCalibrationDataStoreTest.java",
    ],
    deps = [
        ":CalibrationDataStoreTest",
        "//:com_google_guava_guava",
        "//:junit_junit",
        "//:org_hamcrest_hamcrest_junit",
        "//:org_slf4j_slf4j_log4j12",
        "//src/main/java/edu/bu/vip/multikinect/controller/calibration:calibrationdatadb",
        "//src/main/proto:calibration",
        "//src/main/proto:frame",
    ],
)
