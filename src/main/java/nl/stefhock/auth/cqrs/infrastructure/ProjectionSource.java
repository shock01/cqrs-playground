package nl.stefhock.auth.cqrs.infrastructure;

import java.util.Date;
import java.util.stream.Stream;

/**
 * Created by hocks on 23-7-2017.
 */
public abstract class ProjectionSource<T> {

    private final SequenceInfo sequenceInfo;

    protected ProjectionSource() {
        sequenceInfo = new SequenceInfo();
    }

    public abstract void tryDelete(T entity);

    public abstract void addOrUpdate(T entity);

    public abstract Stream<T> stream();

    public SequenceInfo sequenceInfo() {
        return sequenceInfo;
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

        public void update(Long sequenceId) {
            this.sequenceId = sequenceId;
            this.sequenceDate = new Date();
        }

        public Date date() {
            return sequenceDate;
        }
    }
}