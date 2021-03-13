package controller;

public interface IInterpreter {
    boolean isDebugMode();

    void setDebugMode(boolean isDebugMode);

    String execute(String query);
}
