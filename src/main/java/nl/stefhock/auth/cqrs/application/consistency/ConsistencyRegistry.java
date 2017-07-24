package nl.stefhock.auth.cqrs.application.consistency;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by hocks on 17-7-2017.
 */
public class ConsistencyRegistry {

    private List<ConsistencyStrategy<?>> strategies = new ArrayList<>();

    public void register(ConsistencyStrategy<?> strategy) {
        strategies.add(strategy);
    }

    public List<ConsistencyStrategy<?>> strategies() {
        return strategies;
    }

    public Optional<ConsistencyStrategy<?>> locate(Object query) {
        return strategies.stream().filter(item -> item.instance == query).findFirst();
    }

    public void pauseAll() {
        strategies.stream().forEach(ConsistencyStrategy::pause);
    }

    public void resumeAll() {
        strategies.stream().forEach(ConsistencyStrategy::resume);
    }
}
