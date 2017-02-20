java_test(
    name = "FragmenterTest",
    srcs = ["FragmenterTest.java"],
    resources = [
        "//src/test/resources:kinectdata",
    ],
    deps = [
        "//:com_google_guava_guava",
        "//:com_google_inject_guice",
        "//:junit_junit",
        "//:org_hamcrest_hamcrest_junit",
        "//:org_mockito_mockito_core",
        "//src/main/java/edu/bu/vip/multikinect/controller/calibration:calibrationdatadb",
        "//src/main/java/edu/bu/vip/multikinect/controller/calibration:fragmenter",
        "//src/main/java/edu/bu/vip/multikinect/controller/camera:fileframepublisher",
        "//src/main/java/edu/bu/vip/multikinect/controller/camera:framebus",
        "//src/main/java/edu/bu/vip/multikinect/controller/camera:framereader",
        "//src/main/proto:calibration",
        "//src/main/proto:camera_manager",
        "//src/main/proto:frame",
    ],
)

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
        "//src/test/resources:calibration",
        "//src/test/resources:kinectdata",
    ],
    deps = [
        "//:com_google_guava_guava",
        "//:com_google_inject_guice",
        "//:junit_junit",
        "//:org_hamcrest_hamcrest_junit",
        "//src/main/java/edu/bu/vip/multikinect/controller/calibration:calibrationalgorithm",
        "//src/main/java/edu/bu/vip/multikinect/controller/calibration:calibrationdatadb",
        "//src/main/java/edu/bu/vip/multikinect/controller/camera:framereader",
        "//src/main/proto:calibration",
        "//src/main/proto:camera_manager",
        "//src/main/proto:frame",
    ],
)
