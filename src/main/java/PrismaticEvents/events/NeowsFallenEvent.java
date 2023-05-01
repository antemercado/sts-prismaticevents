package PrismaticEvents.events;

import com.megacrit.cardcrawl.events.AbstractImageEvent;

public class NeowsFallenEvent extends AbstractImageEvent{

    public static final String ID = "NeowsFallen";

    public NeowsFallenEvent(String title, String body, String imgUrl) {
        super(title, body, imgUrl);
        //TODO Auto-generated constructor stub
    }

    @Override
    protected void buttonEffect(int arg0) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'buttonEffect'");
    }
    
}
