package site.transcendence.controller;

public abstract class Controller {

    private final ModelAccess modelAccess;

    public Controller(ModelAccess modelAccess){
        this.modelAccess = modelAccess;
    }

    public ModelAccess getModelAccess() {
        return modelAccess;
    }

}
