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
        "//src/main/java/edu/bu/vip/kinect/controllerv2/calibration:calibrationdatadb",
        "//src/main/java/edu/bu/vip/kinect/controllerv2/calibration:fragmenter",
        "//src/main/java/edu/bu/vip/kinect/controllerv2/calibration:inmemorycalibrationdatadb",
        "//src/main/java/edu/bu/vip/kinect/controllerv2/camera:fileframepublisher",
        "//src/main/java/edu/bu/vip/kinect/controllerv2/camera:framebus",
        "//src/main/java/edu/bu/vip/kinect/controllerv2/camera:framereader",
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
        "//src/main/java/edu/bu/vip/kinect/controllerv2/calibration:calibrationdatadb",
        "//src/main/java/edu/bu/vip/kinect/controllerv2/calibration:cameratransform",
        "//src/main/java/edu/bu/vip/kinect/controllerv2/calibration:inmemorycalibrationdatadb",
        "//src/main/java/edu/bu/vip/kinect/controllerv2/camera:framereader",
        "//src/main/proto:calibration",
        "//src/main/proto:camera_manager",
        "//src/main/proto:frame",
        "//src/test/java/edu/bu/vip/kinect/utils:matchers",
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
        "//src/main/java/edu/bu/vip/kinect/controllerv2/calibration:calibrationdatadb",
        "//src/main/java/edu/bu/vip/kinect/controllerv2/calibration:inmemorycalibrationdatadb",
        "//src/main/java/edu/bu/vip/kinect/controllerv2/calibration:manager",
        "//src/main/java/edu/bu/vip/kinect/controllerv2/camera:fileframepublisher",
        "//src/main/java/edu/bu/vip/kinect/controllerv2/camera:framebus",
        "//src/main/proto:calibration",
        "//src/main/proto:camera_manager",
        "//src/main/proto:frame",
        "//src/test/java/edu/bu/vip/kinect/utils:matchers",
    ],
)
