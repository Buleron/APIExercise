package tasks;

import actor.MyActorTask;
import com.google.inject.AbstractModule;
import controllers.StreamController;

public class TasksModule extends AbstractModule {

    @Override
    protected void configure() {
        //running on start
        bind(StreamController.class).asEagerSingleton();
    }
}