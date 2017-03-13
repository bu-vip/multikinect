java_library(
    name = "sessiondatastore",
    srcs = [
        "FileSessionDataStore.java",
        "SessionDataStore.java",
    ],
    visibility = ["//visibility:public"],
    deps = [
        "//:com_google_guava_guava",
        "//:com_google_inject_guice",
        "//:com_google_protobuf_protobuf_java",
        "//:org_slf4j_slf4j_api",
        "//src/main/java/edu/bu/vip/multikinect/io:messagewriter",
        "//src/main/java/edu/bu/vip/multikinect/util:timestamputils",
        "//src/main/proto:frame",
        "//src/main/proto:realtime",
        "//src/main/proto:session",
    ],
)
