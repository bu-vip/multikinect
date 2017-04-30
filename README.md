# MultiKinect
MultiKinect is a system for collecting data using multiple Microsoft Kinect v2 cameras.

> For instructions on using MultiKinect with single pixel sensors, refer to 
> [multikinect + singlepixellocalization](https://github.com/bu-vip/singlepixellocalization/tree/master/src/main/java/edu/bu/vip/singlepixel/multikinect).

## Building
### Camera
> NOTE: The camera can only be built and run on Windows, as the Kinect v2 SDK only supports Windows. Each Kinect runs from a separate computer, so to run multiple Kinects, make sure to run the camera program run on each computer.

The camera program runs on a Windows machine and serves as the interface between the Kinect and the controller.
Requirements:
* [Visual Studio](https://www.visualstudio.com/)
* [Kinect v2 SDK](https://www.microsoft.com/en-us/download/details.aspx?id=44561)

Instructions:
1. Open `src/main/csharp/protos/protolib.sln`
2. Go to `Build > Rebuild Solution` to restore NuGet packages
3. Run `src/main/csharp/protos/build.bat`. This will generate all of the necessary protocol buffer files.
4. Close `src/main/csharp/protos/protolib.sln`
5. Open `src/main/csharp/camera/Camera.sln`
6. Build and start the project

### Controller
> NOTE: Building the Controller on Windows is not currently supported.

The controller program connects to each of the cameras and handles transforming the data into a common coordinate system.

#### Requirements
* Java + JDK 8
* [bazel](https://bazel.build/)
* [node.js](https://nodejs.org/en/)

#### Instructions

To build the main controller, run:
```bash
bazel build //src/main/java/edu/bu/vip/multikinect/controller:main
```

## Documentation
For documentation, including usage instructions, see the [docs](docs/).


## Plugins
The multikinect system supports adding plugins for collecting data from other types of systems.
To write a plugin, all you have to do is implement the 
[plugin interface](src/main/java/edu/bu/vip/multikinect/controller/plugin/Plugin.java) 
and add it to the controller.
You can find a full example of plugin usage 
[here](https://github.com/bu-vip/singlepixellocalization/tree/master/src/main/java/edu/bu/vip/singlepixel/multikinect).

## Developing
If you want to make changes to the controller, I highly recommend using 
[IntelliJ](https://www.jetbrains.com/idea/) 
with the [bazel plugin](https://github.com/bazelbuild/intellij).

To add bazel dependencies, you can generate the bazel code using these [scripts](scripts/deps) 
in combination with this [project](https://github.com/Dig-Doug/bazel-deps).

## About
TODO(doug)
