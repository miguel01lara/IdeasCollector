package pro.dreamcode.ideascollector.adapters;

/**
 * Created by migue on 09/05/2017.
 */

public interface MarkListener {
    void markCompleted(int position);
    void markCompletedInRealm(int position);
    boolean isItemCompleted(int position);
}
