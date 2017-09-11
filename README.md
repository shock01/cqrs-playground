- https://dzone.com/articles/docker-containers-with-gradle-application-plugin
- https://github.com/exceptionnotfound/SampleCQRS/tree/master/SampleCQRS.Application
- http://cqrs.wikidot.com/doc:projection
- http://cqrs.nu/tutorial/cs/03-read-models
- https://github.com/google/protobuf-gradle-plugin
- https://github.com/phensley/protobuf-vs-jackson
- https://msdn.microsoft.com/en-us/library/jj591569.aspx

These challenges could be solved by using persistent, transactional storage:

When consuming domain events, update an in-memory model. Don’t touch the disk.
Every now and then (e.g. every 1000 events or every minute) take a snapshot of that model and write it to some persistent storage.
Let queries read from that persistent snapshot, possibly caching it in memory.
After restarting the application or an error, continue consuming the events from the latest snapshot.

domain event


AggregateId – This field is used to associate the particular event to a specific aggregate root.
Date Time Stamp – Ordering of events is crucial. Replaying events in the wrong order can result is unpredictable outcomes.
UserId – This field is commonly required in a line of business applications and can be used to build audit logs. It is a common field, but not always necessary and depends on the specific domain.
Version – The version number allows the developer to handle concurrency conflicts and partial connection scenarios. For more information, take a look at Handling Concurrency Issues in a CQRS Event Sourced system.
ProcessId – At its simplest, this field can be used to tie a series of events back to their originating command. However, it can also be used to ensure the idempotence* of the event.

- domain event should have a payload
- domain event should have type, sequence etc

// EventSource ??? never extend DomainEvent
// aggregateId is also always know.....
// do we really need to know the aggregateId per Event also
// Can we handle the whole DomainEvent in projections...: 
// should we handle DomainEvent<T> in Projection  
// when(DomainEvent<RegistrationCreated> event)
class DomainEvent<T> {
    Date date;
    Long sequence;
    String name;
    T payload;
}

KISS -> remove consistency strategy etc


http://mkuthan.github.io/blog/2013/11/04/ddd-architecture-summary/

class LinearProjection(Projection) implements MyQuery{
    constructor(Projection) 
}

class Projection<Query, Consistency> {
    
}

- Projection should be bound to eventBus and to a query instance
- Can we actually use Annotations for this...
- If we use annotations we can actually ... but then how do we inject the correct instance..:
  to decorate is easier and more readible
  base is that a query should be injected to be able to be used
  does a query need to know the readModel ... this will expose the methods
  
  


@Projection(query = MyQuery.class, consistency=Consistency.Linear)
public MyQuery implements MyQuery {
    
}

use the guava eventbus this one is much more mature

