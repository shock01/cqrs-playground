- https://dzone.com/articles/docker-containers-with-gradle-application-plugin
- https://github.com/exceptionnotfound/SampleCQRS/tree/master/SampleCQRS.Application
- http://cqrs.wikidot.com/doc:projection
- http://cqrs.nu/tutorial/cs/03-read-models
- https://github.com/google/protobuf-gradle-plugin
- https://github.com/phensley/protobuf-vs-jackson

These challenges could be solved by using persistent, transactional storage:

When consuming domain events, update an in-memory model. Don’t touch the disk.
Every now and then (e.g. every 1000 events or every minute) take a snapshot of that model and write it to some persistent storage.
Let queries read from that persistent snapshot, possibly caching it in memory.
After restarting the application or an error, continue consuming the events from the latest snapshot.