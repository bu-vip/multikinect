java_test(
    name = "fragmenter",
    srcs = [
        "FragmenterTest.java",
    ],
    resources = [
        "//src/test/resources:kinectdata",
    ],
    test_class = "edu.bu.vip.kinect.controllerv2.calibration.FragmenterTest",
    deps = [
        "//:com_google_guava_guava",
        "//:com_google_inject_guice",
        "//:junit_junit",
        "//:org_mockito_mockito_core",
        "//src/main/java/edu/bu/vip/kinect/controllerv2/calibration:calibrationdatadb",
        "//src/main/java/edu/bu/vip/kinect/controllerv2/calibration:fragmenter",
        "//src/main/java/edu/bu/vip/kinect/controllerv2/calibration:inmemorycalibrationdatadb",
        "//src/main/java/edu/bu/vip/kinect/controllerv2/camera:framebus",
        "//src/main/proto:calibration",
        "//src/main/proto:frame",
    ],
)
