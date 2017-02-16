@echo off

:: src\main\csharp\protos
cd ..\..\..\..\

call :build_proto "frame.proto"
call :build_proto "camera.proto"
call :build_proto "camera_manager.proto"

cd src\main\csharp\protos
goto :eof

:: Function to compile a proto
:build_proto
set proto_dir=src/main/proto
set tools_dir=src/main/csharp/protos
set tools_dir_win=%tools_dir:/=\%
set out_dir=%tools_dir%/protolib
set file_name=%~1

set args="-I./ --csharp_out %out_dir%/ --grpc_out %out_dir%/ %proto_dir%/%file_name% --plugin=protoc-gen-grpc=%tools_dir%/packages/Grpc.Tools.1.0.0/tools/windows_x86/grpc_csharp_plugin.exe"

echo %args%

start /b "" "%tools_dir_win%\packages\Grpc.Tools.1.0.0\tools\windows_x86\protoc" "%args%"

exit /b