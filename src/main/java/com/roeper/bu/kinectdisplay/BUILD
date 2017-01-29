java_binary(
    name = "kinectdisplay",
    srcs = glob(["**/*.java"]),
    main_class = "com.roeper.bu.kinectdisplay.KinectDisplay",
    # Private due to build problems with native libs
    visibility = ["//visibility:private"],
    runtime_deps = [
        "@org_jogamp_gluegen_gluegen_rt_main//jar",
        "@org_jogamp_jogl_jogl_all_main//jar",
    ],
    deps = [
        "@com_beust_jcommander//jar",
        "@com_google_guava_guava//jar",
        "@org_ejml_all//jar",
        "@org_jogamp_gluegen_gluegen_rt//jar",
        "@org_jogamp_joal_joal_main//jar",
        "@org_jogamp_jocl_jocl_main//jar",
        "@org_jogamp_jogl_jogl_all//jar",
        "//src/main/java/edu/bu/vip/kinect/sync:framereader",
        "//src/main/proto:frame",
    ],
)
