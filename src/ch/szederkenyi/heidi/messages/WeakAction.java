package ch.szederkenyi.heidi.messages;


public class WeakAction extends BaseWeakAction<Runnable> {

    public WeakAction(Object recipient, Runnable action) {
        super(recipient, action);
    }

    @Override
    public void Execute() {
        if(isAlive()) {
            getAction().run();
        }
    }

    @Override
    public void Execute(Object arg) {
        throw new IllegalAccessError();
    }

}
