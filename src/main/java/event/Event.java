package event;

public abstract class Event {
    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
