package businesslayer.commands;

/**
 * Command interface for implementing the Command pattern in the PTFMS system.
 * Provides a common interface for encapsulating operations as objects, allowing
 * for parameterization of objects with different requests, queueing of
 * requests,
 * and support for undoable operations.
 */
public interface Command {
  /**
   * Execute the command
   * 
   * @return true if successful
   */
  boolean execute();

  /**
   * Undo the command
   * 
   * @return true if successful
   */
  boolean undo();
}
