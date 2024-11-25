FROM busybox:latest
ADD build/libs/snapshotter.jar snapshotter.jar
CMD ["cp","/snapshotter.jar","/deploy"]
