java_binary(
    name = "kinectdisplay",
    srcs = glob(["**/*.java"]),
    main_class = "com.roeper.bu.kinectdisplay.KinectDisplay",
    # Private due to build problems with native libs
    visibility = ["//visibility:private"],
    runtime_deps = [
        "//:org_jogamp_gluegen_gluegen_rt_main",
        "//:org_jogamp_jogl_jogl_all_main",
    ],
    deps = [
        "//:com_beust_jcommander",
        "//:com_google_guava_guava",
        "//:org_ejml_all",
        "//:org_jogamp_gluegen_gluegen_rt",
        "//:org_jogamp_joal_joal_main",
        "//:org_jogamp_jocl_jocl_main",
        "//:org_jogamp_jogl_jogl_all",
        "//src/main/java/edu/bu/vip/kinect/sync:framereader",
        "//src/main/proto:frame",
    ],
)
