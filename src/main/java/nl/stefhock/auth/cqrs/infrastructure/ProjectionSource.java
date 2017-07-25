package nl.stefhock.auth.cqrs.infrastructure;

import java.util.Date;
import java.util.stream.Stream;

/**
 * Created by hocks on 23-7-2017.
 */
public abstract class ProjectionSource<T> {

    private final SequenceInfo sequenceInfo;
    private final String name;

    protected ProjectionSource(String name) {
        this.name = name;
        sequenceInfo = new SequenceInfo();
    }

    public void initialize() {
    }

    public abstract void tryDelete(T entity);

    public abstract void addOrUpdate(T entity);

    public abstract Stream<T> stream();

    public SequenceInfo sequenceInfo() {
        return sequenceInfo;
    }

    public void synced(Long sequenceId) {
        sequenceInfo.update(sequenceId);
    }

    public String name() {
        return name;
    }

    @Override
    public String toString() {
        return "ProjectionSource{" +
                "sequenceInfo=" + sequenceInfo +
                ", name='" + name + '\'' +
                '}';
    }

    public static class SequenceInfo {
        private Long sequenceId;
        private Date sequenceDate;

        SequenceInfo(Long sequenceId, Date sequenceDate) {
            this.sequenceId = sequenceId;
            this.sequenceDate = sequenceDate;
        }

        SequenceInfo() {
            this(0L, new Date());
        }

        public Long sequenceId() {
            return sequenceId;
        }

        void update(Long sequenceId) {
            this.sequenceId = sequenceId;
            this.sequenceDate = new Date();
        }

        public Date date() {
            return sequenceDate;
        }
    }
}
